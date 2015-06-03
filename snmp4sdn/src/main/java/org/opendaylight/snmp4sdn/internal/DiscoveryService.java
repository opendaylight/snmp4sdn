/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code base of OpenFlow plugin contributed by Cisco Systems, Inc. Their efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.internal;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;//snmp4sdn add

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.snmp4sdn.IDataPacketListen;
import org.opendaylight.snmp4sdn.IDataPacketMux;
import org.opendaylight.snmp4sdn.IDiscoveryListener;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.internal.Controller;
//import org.openflow.protocol.OFPhysicalPort;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.core.Config;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Edge;
import org.opendaylight.controller.sal.core.ContainerFlow;
import org.opendaylight.controller.sal.core.IContainerListener;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.State;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.LLDP;
import org.opendaylight.controller.sal.packet.LLDPTLV;
import org.opendaylight.controller.sal.packet.LinkEncap;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.utils.HexEncode;
import org.opendaylight.controller.sal.utils.NetUtils;
import org.opendaylight.controller.sal.utils.NodeConnectorCreator;
import org.opendaylight.controller.sal.utils.NodeCreator;

import org.opendaylight.snmp4sdn.internal.SNMPHandler;//snmp4sdn add
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;//snmp4sdn add
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;//snmp4sdn add

/**
 * The class describes neighbor discovery service for an OpenFlow network.
 */
public class DiscoveryService implements IInventoryShimExternalListener, IDataPacketListen, IContainerListener,
        CommandProvider {
    private static Logger logger = LoggerFactory.getLogger(DiscoveryService.class);
    private IController controller = null;
    private CmethUtil cmethUtil = null;
    private IDiscoveryListener discoveryListener = null;
    private IInventoryProvider inventoryProvider = null;
    private IDataPacketMux iDataPacketMux = null;
    private ConcurrentMap<NodeConnector, Edge> edgeMap = null;
    private BlockingQueue <Node> transmitQ;
    private Thread transmitThread;
    private volatile Boolean shuttingDown = false;

    //s4s add
    /*LLDP data structure of a switch:

        e.g.
        for example in below switch, port 1's portID is "pt1", pt1 connects to remote switch whose chassisID is "12:34:56:78:9A:BC" and portID is "prt15"

        table 1: lldpLocalPortIdOID
            1   pt1
            2   pt2
            3   ...
        table 2: lldpRemoteChassisIdOID
            1   12:34:56:78:9A:BC
            2   21:43:65:87:A9:CB
            3   ...
        table 3: lldpRemotePortIdOID
            1   prt15
            1   prt36
            1   ...
    */
    Map<Long, String> switches_ID2Chassis = new HashMap<Long, String>();
    Map<String, Long> switches_Chassis2ID = new HashMap<String, Long>();
    Map<Long, Map<Short, String>> localPortIDsOnSwitches = new HashMap<Long, Map<Short, String>>();
    Map<Long, Map<Short, String>> portToRemoteChassisOnSwitches = new HashMap<Long, Map<Short, String>>();
    Map<Long, Map<Short, String>> portToRemotePortIDOnSwitches = new HashMap<Long, Map<Short, String>>();

    class DiscoveryTransmit implements Runnable {
        private final BlockingQueue<Node> transmitQ;//snmp4sdn

        DiscoveryTransmit(BlockingQueue<Node> transmitQ) {
            this.transmitQ = transmitQ;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Node node = transmitQ.take();//snmp4sdn

                    /*s4s: here should process new switch join in and to figure out the topology.
                        but currently we don't deal with "a new switch" but we deal with "link up trap -- that is, a new port"
                    */

                } catch (InterruptedException e1) {
                    logger.warn("DiscoveryTransmit interupted", e1.getMessage());
                    if (shuttingDown) {
                        return;
                    }
                } catch (Exception e2) {
                    logger.error("", e2);
                }
            }
        }
    }


    @Override
    public PacketResult receiveDataPacket(RawPacket inPkt) {
        if (inPkt == null) {
            logger.debug("Ignoring null packet");
            return PacketResult.IGNORED;
        }

        byte[] data = inPkt.getPacketData();
        if (data.length <= 0) {
            logger.trace("Ignoring zero length packet");
            return PacketResult.IGNORED;
        }

        if (!inPkt.getEncap().equals(LinkEncap.ETHERNET)) {
            logger.trace("Ignoring non ethernet packet");
            return PacketResult.IGNORED;
        }

        if (((Short) inPkt.getIncomingNodeConnector().getID()).equals(NodeConnector.SPECIALNODECONNECTORID)) {
            logger.trace("Ignoring ethernet packet received on special port: "
                    + inPkt.getIncomingNodeConnector().toString());
            return PacketResult.IGNORED;
        }

        Ethernet ethPkt = new Ethernet();
        try {
            ethPkt.deserialize(data, 0, data.length * NetUtils.NumBitsInAByte);
        } catch (Exception e) {
            logger.warn("Failed to decode LLDP packet from {}: {}", inPkt.getIncomingNodeConnector(), e);
            return PacketResult.IGNORED;
        }

        return PacketResult.IGNORED;
    }

    public Map<String, Property> getPropMap(NodeConnector nodeConnector) {
        if (nodeConnector == null) {
            return null;
        }

        if (inventoryProvider == null) {
            return null;
        }

        Map<NodeConnector, Map<String, Property>> props = inventoryProvider.getNodeConnectorProps(false);
        if (props == null) {
            return null;
        }

        return props.get(nodeConnector);
    }

    public Property getProp(NodeConnector nodeConnector, String propName) {
        Map<String, Property> propMap = getPropMap(nodeConnector);
        if (propMap == null) {
            return null;
        }

        Property prop = propMap.get(propName);
        return prop;
    }

    public Set<Property> getProps(NodeConnector nodeConnector) {
        Map<String, Property> propMap = getPropMap(nodeConnector);
        if (propMap == null) {
            return null;
        }

        Set<Property> props = new HashSet<Property>(propMap.values());
        return props;
    }

    private boolean isEnabled(NodeConnector nodeConnector) {
        if (nodeConnector == null) {
            return false;
        }

        Config config = (Config) getProp(nodeConnector, Config.ConfigPropName);
        State state = (State) getProp(nodeConnector, State.StatePropName);
        return ((config != null) && (config.getValue() == Config.ADMIN_UP) && (state != null) && (state.getValue() == State.EDGE_UP));
    }


    private void addDiscovery(Node node) {
        Map<Long, ISwitch> switches = controller.getSwitches();
        ISwitch sw = switches.get(node.getID());
        transmitQ.add(node);
    }

    private void addDiscovery(NodeConnector nodeConnector) {
        processLinkUpTrap(nodeConnector);
    }

    private Set<NodeConnector> getRemoveSet(Collection<NodeConnector> c, Node node) {
        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        if (c == null) {
            return removeSet;
        }
        for (NodeConnector nodeConnector : c) {
            if (node.equals(nodeConnector.getNode())) {
                removeSet.add(nodeConnector);
            }
        }
        return removeSet;
    }

    private void removeDiscovery(Node node) {
        Set<NodeConnector> removeSet;

        removeSet = getRemoveSet(edgeMap.keySet(), node);
        for (NodeConnector nodeConnector : removeSet) {
            removeEdge(nodeConnector, false);
        }

    }

    private void removeDiscovery(NodeConnector nodeConnector) {
        removeEdge(nodeConnector, false);
    }


    private void addEdge(Edge edge, Set<Property> props) {
        if (edge == null) {
            return;
        }

        /*
        NodeConnector src = edge.getTailNodeConnector();
        if (!src.getType().equals(NodeConnector.NodeConnectorIDType.PRODUCTION)) {
            pendingMap.remove(src);
        } else {
            NodeConnector dst = edge.getHeadNodeConnector();
            agingMap.put(dst, 0);
        }*///snmp4sdn: not sure whether to use pendingMap and agingMap in eth plugin

        // notify
        updateEdge(edge, UpdateType.ADDED, props);
        logger.trace("Add edge {}", edge);
    }

    /*
     * Remove SNMP edge
     */
    private void removeEdge(NodeConnector nodeConnector, boolean stillEnabled) {
        Edge edge = null;
        Set<NodeConnector> edgeKeySet = edgeMap.keySet();
        if ((edgeKeySet != null) && (edgeKeySet.contains(nodeConnector))) {
            edge = edgeMap.get(nodeConnector);
            edgeMap.remove(nodeConnector);
        }

        // notify Topology
        if (this.discoveryListener != null) {
            this.discoveryListener.notifyEdge(edge, UpdateType.REMOVED, null);
        }
        logger.trace("Remove {}", nodeConnector);
    }

    private void removeEdge(NodeConnector nodeConnector) {
        removeEdge(nodeConnector, isEnabled(nodeConnector));
    }

    private void updateEdge(Edge edge, UpdateType type, Set<Property> props) {
        if (discoveryListener == null) {
            return;
        }

        this.discoveryListener.notifyEdge(edge, type, props);

        NodeConnector src = edge.getTailNodeConnector(), dst = edge.getHeadNodeConnector();
        if (!src.getType().equals(NodeConnector.NodeConnectorIDType.PRODUCTION)) {
            if (type == UpdateType.ADDED) {
                edgeMap.put(dst, edge);
            } else {
                edgeMap.remove(dst);
            }
        }

        else {
            //TODO

        }
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this, null);
    }

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---Topology Discovery---\n");
        help.append("\t prlh                            - Print readyListHi entries\n");
        help.append("\t prll                            - Print readyListLo entries\n");
        help.append("\t pwl                             - Print waitingList entries\n");
        help.append("\t ppl                             - Print pendingList entries\n");
        help.append("\t ptick                           - Print tick time in msec\n");
        help.append("\t pcc                             - Print CC info\n");
        help.append("\t psize                           - Print sizes of all the lists\n");
        help.append("\t ptm                             - Print timeout info\n");
        help.append("\t ecc                             - Enable CC\n");
        help.append("\t dcc                             - Disable CC\n");
        help.append("\t scc [multiple]                  - Set/show CC multiple and interval\n");
        help.append("\t sports [ports]                  - Set/show max ports per batch\n");
        help.append("\t spause [ticks]                  - Set/show pause period\n");
        help.append("\t sdi [ticks]                     - Set/show discovery interval in ticks\n");
        help.append("\t stm [ticks]                     - Set/show per timeout ticks\n");
        help.append("\t sretry [count]                  - Set/show num of retries\n");
        help.append("\t addsw <swid>                    - Add a switch\n");
        help.append("\t remsw <swid>                    - Remove a switch\n");
        help.append("\t page                            - Print aging info\n");
        help.append("\t sage                            - Set/Show aging time limit\n");
        help.append("\t eage                            - Enable aging\n");
        help.append("\t dage                            - Disable aging\n");
        help.append("\t pthrot                          - Print throttling\n");
        help.append("\t ethrot                          - Enable throttling\n");
        help.append("\t dthrot                          - Disable throttling\n");
        help.append("\t psnp                            - Print LLDP snooping\n");
        help.append("\t esnp <all|nodeConnector>        - Enable LLDP snooping\n");
        help.append("\t dsnp <all|nodeConnector>        - Disable LLDP snooping\n");
        return help.toString();
    }

    @Override
    public void updateNode(Node node, UpdateType type, Set<Property> props) {
        switch (type) {
        case ADDED:
            addNode(node, props);
            break;
        case REMOVED:
            removeNode(node);
            break;
        default:
            break;
        }
    }

    @Override
    public void updateNodeConnector(NodeConnector nodeConnector, UpdateType type, Set<Property> props) {
        Config config = null;
        State state = null;
        boolean enabled = false;

        for (Property prop : props) {
            if (prop.getName().equals(Config.ConfigPropName)) {
                config = (Config) prop;
            } else if (prop.getName().equals(State.StatePropName)) {
                state = (State) prop;
            }
        }
        enabled = ((config != null) && (config.getValue() == Config.ADMIN_UP) && (state != null) && (state.getValue() == State.EDGE_UP));

        switch (type) {
        case ADDED:
            if (enabled) {
                addDiscovery(nodeConnector);//TODO: should addDiscovery(nodeConnector) or addDiscovery(node)
                logger.trace("ADDED enabled {}", nodeConnector);
            } else {
                logger.trace("ADDED disabled {}", nodeConnector);
            }
            break;
        case CHANGED:
            if (enabled) {
                addDiscovery(nodeConnector);//TODO: should addDiscovery(nodeConnector) or addDiscovery(node)
                logger.trace("CHANGED enabled {}", nodeConnector);
            } else {
                //removeDiscovery(nodeConnector);//TODO: should removeDiscovery(nodeConnector) or removeDiscovery(node)
                logger.trace("CHANGED disabled {}", nodeConnector);
            }
            break;
        case REMOVED:
            removeDiscovery(nodeConnector);//TODO: should removeDiscovery(nodeConnector) or removeDiscovery(node)
            logger.trace("REMOVED enabled {}", nodeConnector);
            break;
        default:
            return;
        }
    }

    public void addNode(Node node, Set<Property> props) {
        if (node == null) {
            return;
        }

        addDiscovery(node);
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        removeDiscovery(node);
    }

    void setController(IController s) {
        this.controller = s;
        cmethUtil = ((Controller)controller).cmethUtil;//s4s add
    }

    void unsetController(IController s) {
        if (this.controller == s) {
            this.controller = null;
        }
    }

    public void setInventoryProvider(IInventoryProvider service) {
        this.inventoryProvider = service;
    }

    public void unsetInventoryProvider(IInventoryProvider service) {
        this.inventoryProvider = null;
    }

    public void setIDataPacketMux(IDataPacketMux service) {
        this.iDataPacketMux = service;
    }

    public void unsetIDataPacketMux(IDataPacketMux service) {
        if (this.iDataPacketMux == service) {
            this.iDataPacketMux = null;
        }
    }

    void setDiscoveryListener(IDiscoveryListener s) {
        this.discoveryListener = s;
    }

    void unsetDiscoveryListener(IDiscoveryListener s) {
        if (this.discoveryListener == s) {
            this.discoveryListener = null;
        }
    }

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    void init() {
        logger.trace("Init called");

        transmitQ = new LinkedBlockingQueue<Node>();//snmp4sdn

        edgeMap = new ConcurrentHashMap<NodeConnector, Edge>();

        transmitThread = new Thread(new DiscoveryTransmit(transmitQ));

        registerWithOSGIConsole();
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
        transmitQ = null;
        edgeMap = null;
        transmitThread = null;
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
        transmitThread.start();
    }

    /**
     * Function called after registering the service in OSGi service registry.
     */
    void started() {
        /* get a snapshot of all the existing switches */
        //addDiscovery();//TODO: reuse addDiscovery() or call doEthSwDiscovery()
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy ()"
     * calls
     *
     */
    void stop() {
        shuttingDown = true;
        transmitThread.interrupt();
    }

    @Override
    public void tagUpdated(String containerName, Node n, short oldTag, short newTag, UpdateType t) {
    }

    @Override
    public void containerFlowUpdated(String containerName, ContainerFlow previousFlow, ContainerFlow currentFlow,
            UpdateType t) {
    }

    @Override
    public void nodeConnectorUpdated(String containerName, NodeConnector p, UpdateType t) {
        switch (t) {
        case ADDED:
            ////////////TODO
            break;
        default:
            break;
        }
    }

    @Override
    public void containerModeUpdated(UpdateType t) {
        // do nothing
    }

    private byte[] getSourceMACFromNodeID(String nodeId) {
        byte[] cid = HexEncode.bytesFromHexString(nodeId);
        byte[] sourceMac = new byte[6];
        int pos = cid.length - sourceMac.length;

        if (pos >= 0) {
            System.arraycopy(cid, pos, sourceMac, 0, sourceMac.length);
        }

        return sourceMac;
    }

    /**
     * This method returns the interval which determines how often the discovery
     * packets will be sent. Default is 300 seconds.
     *
     * @return The discovery interval in second
     */
    private int getDiscoveryInterval() {
        String elapsedTime = System.getProperty("of.discoveryInterval");
        int rv = 300;

        try {
            if (elapsedTime != null) {
                rv = Integer.parseInt(elapsedTime);
            }
        } catch (Exception e) {
        }

        return rv;
    }

    /**
     * This method returns the timeout value in waiting for response of a
     * discovery query. Default is 60 seconds.
     *
     * @return The discovery timeout in second
     */
    private int getDiscoveryTimeout() {
        String elapsedTime = System.getProperty("of.discoveryTimeout");
        int rv = 60;

        try {
            if (elapsedTime != null) {
                rv = Integer.parseInt(elapsedTime);
            }
        } catch (Exception e) {
        }

        return rv;
    }

    /**
     * This method returns the number of retries after the initial discovery
     * packet is not received within the timeout period. Default is 2 times.
     *
     * @return The number of discovery retries
     */
    private int getDiscoveryRetry() {
        String retry = System.getProperty("of.discoveryRetry");
        int rv = 2;

        if (retry != null) {
            try {
                rv = Integer.parseInt(retry);
            } catch (Exception e) {
            }
        }

        return rv;
    }

    private void processLinkUpTrap(NodeConnector nodeConnector){//snmp4sdn add: after snmp link up trap is recevied, the trap is inputted to this function to create an edge
        Node node = nodeConnector.getNode();
        Long localSwitchID = (Long)(node.getID());
        Short localPortNum = (Short)(nodeConnector.getID());
        logger.trace("enter DiscoveryService.processLinkUpTrap(NodeConnector: node " + HexString.toHexString(localSwitchID) + "(ip " + cmethUtil.getIpAddr(localSwitchID)+ ")'s port " + localPortNum + ")");

        boolean val = readLLDPonOneSwitch(localSwitchID);//read the LLDP data on this port's switch
        if(val == false){
            logger.trace("--> read node " + localSwitchID + "'s LLDP data error! so stop processLinkUpTrap()");
            return;
        }

        //now LLDP data on all switches are ready, now can read about this switch's LLDP data and mapping to the remote switch's

        String remoteChassis = portToRemoteChassisOnSwitches.get(localSwitchID).get(localPortNum);
        if(remoteChassis == null){
            logger.trace("--> can't find switch(Node " + localSwitchID + ",port " + localPortNum + ")'s remoteChassis (==> LLDP exchange with the remote port not yet done)");
            return;
        }
        else
            logger.trace("--> this port's remoteChassis is " + remoteChassis);

        String remotePortID = portToRemotePortIDOnSwitches.get(localSwitchID).get(localPortNum);
        if(remotePortID == null){
            logger.trace("--> can't find switch(Node " + localSwitchID + ",port " + localPortNum + ")'s remotePortID (==> abnormal, should not happen!since remoteChassis is known)");
            return;
        }
        else
            logger.trace("--> this port's remotePortID is " + remotePortID);

        Long remoteSwitchID = switches_Chassis2ID.get(remoteChassis);
        if(remoteSwitchID == null){
            logger.trace("can't find remote switch(chassis " + remoteChassis + ")'s node ID (==> abnormal, should not happen!since remoteChassis is known)");
            return;
        }
        else
            logger.trace("--> this port's remoteSwitchID is " + remoteSwitchID);

        Map<Short, String>remotePortIDs = localPortIDsOnSwitches.get(remoteSwitchID);

        logger.trace("Compare with remote switch " + HexString.toHexString(remoteSwitchID) + "(ip " + cmethUtil.getIpAddr(remoteSwitchID)  +")'s " + remotePortIDs.size() + " ports");
        for(Map.Entry<Short, String> entry : remotePortIDs.entrySet()){
            logger.trace("\tcompare with remote port of id: " + entry.getValue());
            if(entry.getValue().compareToIgnoreCase(remotePortID) == 0){
                Short remotePortNum = entry.getKey();
                logger.trace("\t\t==> Add edge: local (ip " + cmethUtil.getIpAddr(localSwitchID) + ", port " + localPortNum + ") --> remote (ip " + cmethUtil.getIpAddr(remoteSwitchID) + ", port " + remotePortNum +")");
                myAddEdge(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);
                break;
            }
        }
    }

    private void readSwLLDP(Node node){
        //TODO
    }

    public void doEthSwDiscovery(){//snmp4sdn add
        //TODO: where to call doEthSwDiscovery()
        readLLDPonSwitches();
        resolvePortPairsAndAddEdges();
    }

    private boolean readLLDPonOneSwitch(Long switchID){
            SNMPHandler snmp = new SNMPHandler(cmethUtil);
            String localChassis = snmp.getLLDPChassis(switchID);
            if(localChassis == null) return false;//this switch is not in the switches' ip list
            switches_ID2Chassis.put(switchID, localChassis);
            switches_Chassis2ID.put(localChassis, switchID);
            logger.trace("#############################################################################");
            logger.trace("######### Reading switch (ip: " + HexString.toHexString(switchID) + ")'s, chassis = " + localChassis +" ##############");

            Map<Short, String> localPortIDs = snmp.readLLDPLocalPortIDs(switchID);
            localPortIDsOnSwitches.put(switchID, localPortIDs);
            logger.trace("--> local port id: " + localPortIDs.size() + "entries");
            for(Map.Entry<Short, String> entryp: localPortIDs.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " as port id " + entryp.getValue());
            }

            Map<Short, String> portToRemoteChassis = snmp.readLLDPAllRemoteChassisID(switchID);
            portToRemoteChassisOnSwitches.put(switchID, portToRemoteChassis);
            logger.trace("--> remote chassis: " + portToRemoteChassis.size() + "entries");
            for(Map.Entry<Short, String> entryp: portToRemoteChassis.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " ==> remote chassis: " + entryp.getValue());
            }

            Map<Short, String> portToRemotePortID = snmp.readLLDPRemotePortIDs(switchID);
            portToRemotePortIDOnSwitches.put(switchID, portToRemotePortID);
            logger.trace("--> remote port id " + portToRemotePortID.size() + "entries");
            for(Map.Entry<Short, String> entryp: portToRemotePortID.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " ==> remote port id\"" + entryp.getValue() + "\"");
            }

            logger.trace("###### end of Reading switch (ip: " + HexString.toHexString(switchID) + ")'s, chassis = " + localChassis +" #####");
            logger.trace("#########################################################################");

            return true;
    }

    //snmp4sdn add
    private void readLLDPonSwitches(){
        logger.trace("============================================");
        logger.trace("=============== Read LLDP on switches ==============");
        Map<Long, ISwitch> switches = controller.getSwitches();
        for(ISwitch sw : switches.values()){
            //this "for loop"'s body, entirely move to readLLDPonOneSwitch()
            Long switchID = sw.getId();
            boolean val = readLLDPonOneSwitch(switchID);
            if(val == false)
                logger.trace("read node " + switchID + "'s LLDP data error!");
        }
        logger.trace("======== end of Read LLDP on switches ========");
        logger.trace("====================================");
    }

    //snmp4sdn add
    private void resolvePortPairsAndAddEdges(){
        logger.trace("===========================================");
        logger.trace("===== Resolve the port pars, and add edges correspondingly =====");
        logger.trace("number of switches to resolve: " + portToRemoteChassisOnSwitches.size());
        for(Map.Entry<Long, Map<Short, String>> entryS : portToRemoteChassisOnSwitches.entrySet()){
            Long localSwitchID = entryS.getKey();
            String localChassis = switches_ID2Chassis.get(localSwitchID);
            Map<Short, String> localPortIDs = entryS.getValue();
            String localIP = cmethUtil.getIpAddr(localSwitchID);
            logger.trace("processing local switch (id: ip = " + localIP + ", chassis: " + localChassis + ", number of ports which connect another remote switch: " + localPortIDs.size() + ")");
            for(Map.Entry<Short, String> entryP : localPortIDs.entrySet()){
                Short localPortNum = entryP.getKey();
                String remoteChassis = entryP.getValue();
                Long remoteSwitchID = switches_Chassis2ID.get(remoteChassis);
                Map<Short, String> remotePortIDs = portToRemotePortIDOnSwitches.get(remoteSwitchID);
                if(remotePortIDs == null)
                    continue;
                String localPortID = localPortIDsOnSwitches.get(localSwitchID).get(localPortNum);
                String remoteIP = cmethUtil.getIpAddr(remoteSwitchID);
                logger.trace("\tchecking local port (num: " + localPortNum + ", id: " +localPortID + ", remote switch's chassis: " + remoteChassis + "), so look into this remote switch (ip: " + remoteIP + ")");
                for(Map.Entry<Short, String> entryR : remotePortIDs.entrySet()){
                    //logger.trace("...compare with remote port of id: " + entryR.getValue());
                    if(entryR.getValue().compareToIgnoreCase(localPortID) == 0){
                        Short remotePortNum = entryR.getKey();

                        logger.trace("\t\tAdd edge: local (ip " + localIP + ", port " + localPortNum + ") --> remote (ip " + remoteIP + ", port " + remotePortNum +")");
                        myAddEdge(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);

                        break;
                    }
                }
            }
        }
        logger.trace("===== end of Resolve the port pars, and add edges correspondingly =====");
        logger.trace("=================================================");
    }

    //s4s add
    private void myAddEdge(Long srcSwitchID, Short srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node("SNMP", srcSwitchID);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", srcPortNum, srcNode);
            if (srcNodeConnector != null) {
                srcNode = srcNodeConnector.getNode();
            }

            //create dest node and nodeconnector
            Node destNode = new Node("SNMP", destSwitchID);
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", destPortNum, destNode);
            if (srcNodeConnector != null) {
                srcNode = srcNodeConnector.getNode();
            }

            //create the edge connecting the source/dest nodeconnectors
            Edge edge = null;
            Set<Property> props = null;
            edge = new Edge(srcNodeConnector, destNodeConnector);
            props = getProps(destNodeConnector);

            addEdge(edge, props);
        }
        catch (ConstructionException e) {
            logger.error("Caught exception ", e);
        }
    }

}
