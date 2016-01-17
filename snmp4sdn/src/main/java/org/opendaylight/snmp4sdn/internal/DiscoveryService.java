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
import org.opendaylight.snmp4sdn.protocol.util.HexString;//snmp4sdn add//TODO: replace HexString by HexCode?
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
    //snmp4sdn: OF's need
    /*
    // Newly added ports go into this list and will be served first
    private List<NodeConnector> readyListHi = null;
    // Come here after served at least once
    private List<NodeConnector> readyListLo = null;
    // Staging area during quiet period
    private List<NodeConnector> waitingList = null;
    // Wait for next discovery packet. The map contains the time elapsed since
    // the last received LLDP frame on each node connector
    private ConcurrentMap<NodeConnector, Integer> pendingMap = null;*/
    // openflow edges keyed by head connector
    private ConcurrentMap<NodeConnector, Edge> edgeMap = null;
    //snmp4sdn: OF's need
    /*
    // Aging entries keyed by head edge connector
    private ConcurrentMap<NodeConnector, Integer> agingMap = null;
    // Production edges keyed by head edge connector
    private ConcurrentMap<NodeConnector, Edge> prodMap = null;

    private Timer discoveryTimer;
    private DiscoveryTimerTask discoveryTimerTask;
    private long discoveryTimerTick = 1L * 1000; // per tick in msec
    private int discoveryTimerTickCount = 0; // main tick counter
    // Max # of ports handled in one batch
    private int discoveryBatchMaxPorts = 500;
    // Periodically restart batching process
    private int discoveryBatchRestartTicks = getDiscoveryInterval();
    private int discoveryBatchPausePeriod = 5; // pause for few secs
    // Pause after this point
    private int discoveryBatchPauseTicks = discoveryBatchRestartTicks - discoveryBatchPausePeriod;
    // Number of retries after initial timeout
    private int discoveryRetry = getDiscoveryRetry();
    private int discoveryTimeoutTicks = getDiscoveryTimeout(); // timeout in sec
    private int discoveryAgeoutTicks = 120; // age out 2 min
    // multiple of discoveryBatchRestartTicks
    private int discoveryConsistencyCheckMultiple = 2;
    // CC tick counter
    private int discoveryConsistencyCheckTickCount = discoveryBatchPauseTicks;
    // # of times CC getscalled
    private int discoveryConsistencyCheckCallingTimes = 0;
    // # of cases CC corrected
    private int discoveryConsistencyCheckCorrected = 0;
    // Enable or disable CC
    private boolean discoveryConsistencyCheckEnabled = true;
    // Enable or disable aging
    private boolean discoveryAgingEnabled = true;
    // Global flag to enable or disable LLDP snooping
    private boolean discoverySnoopingEnabled = true;
    // The list of ports that will not do LLDP snooping
    private List<NodeConnector> discoverySnoopingDisableList;
    private BlockingQueue<NodeConnector> <Node> transmitQ;//snmp4sdn: OF'code
    */
    private BlockingQueue <Node> transmitQ;
    private Thread transmitThread;
    //private Boolean throttling = false; // if true, no more batching.//snmp4sdn. OF's need
    private volatile Boolean shuttingDown = false;

    //private LLDPTLV chassisIdTlv, portIdTlv, ttlTlv, customTlv;//snmp4sdn. OF's need


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
    Map<Long, Map<Short, String>> portToRemotePortIDOnSwitchesTmp = new HashMap<Long, Map<Short, String>>();

    class DiscoveryTransmit implements Runnable {
        //private final BlockingQueue<NodeConnector> transmitQ;//snmp4sdn. OF's
        private final BlockingQueue<Node> transmitQ;//snmp4sdn

        //DiscoveryTransmit(BlockingQueue<NodeConnector> transmitQ) {//snmp4sdn. OF's
        DiscoveryTransmit(BlockingQueue<Node> transmitQ) {
            this.transmitQ = transmitQ;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //NodeConnector nodeConnector = transmitQ.take();//snmp4sdn. OF's code
                    Node node = transmitQ.take();//snmp4sdn
                    /*//snmp4sdn. OF's need
                    RawPacket outPkt = createDiscoveryPacket(nodeConnector);
                    sendDiscoveryPacket(nodeConnector, outPkt);
                    nodeConnector = null;*/

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

    //snmp4sdn: OF's need
    /*
    class DiscoveryTimerTask extends TimerTask {
        @Override
        public void run() {
            checkTimeout();
            checkAging();
            doConsistencyCheck();
            doDiscovery();
        }
    }*/

    //snmp4sdn: OF's need
    /*
    private RawPacket createDiscoveryPacket(NodeConnector nodeConnector) {
        String nodeId = HexEncode.longToHexString((Long) nodeConnector.getNode().getID());

        // Create LLDP ChassisID TLV
        byte[] cidValue = LLDPTLV.createChassisIDTLVValue(nodeId);
        chassisIdTlv.setType(LLDPTLV.TLVType.ChassisID.getValue()).setLength((short) cidValue.length)
                .setValue(cidValue);

        // Create LLDP PortID TLV
        String portId = nodeConnector.getNodeConnectorIDString();
        byte[] pidValue = LLDPTLV.createPortIDTLVValue(portId);
        portIdTlv.setType(LLDPTLV.TLVType.PortID.getValue()).setLength((short) pidValue.length).setValue(pidValue);

        // Create LLDP Custom TLV
        byte[] customValue = LLDPTLV.createCustomTLVValue(nodeConnector.toString());
        customTlv.setType(LLDPTLV.TLVType.Custom.getValue()).setLength((short) customValue.length)
                .setValue(customValue);

        // Create LLDP Custom Option list
        List<LLDPTLV> customList = new ArrayList<LLDPTLV>();
        customList.add(customTlv);

        // Create discovery pkt
        LLDP discoveryPkt = new LLDP();
        discoveryPkt.setChassisId(chassisIdTlv).setPortId(portIdTlv).setTtl(ttlTlv).setOptionalTLVList(customList);

        RawPacket rawPkt = null;
        try {
            // Create ethernet pkt
            byte[] sourceMac = getSourceMACFromNodeID(nodeId);
            Ethernet ethPkt = new Ethernet();
            ethPkt.setSourceMACAddress(sourceMac).setDestinationMACAddress(LLDP.LLDPMulticastMac)
                    .setEtherType(EtherTypes.LLDP.shortValue()).setPayload(discoveryPkt);

            byte[] data = ethPkt.serialize();
            rawPkt = new RawPacket(data);
            rawPkt.setOutgoingNodeConnector(nodeConnector);
        } catch (ConstructionException cex) {
            logger.warn("RawPacket creation caught exception {}", cex.getMessage());
        } catch (Exception e) {
            logger.error("Failed to serialize the LLDP packet: " + e);
        }

        return rawPkt;
    }*/

    //snmp4sdn: OF's need
    /*
    private void sendDiscoveryPacket(NodeConnector nodeConnector, RawPacket outPkt) {
        if (nodeConnector == null) {
            logger.debug("Can not send discovery packet out since nodeConnector is null");
            return;
        }

        if (outPkt == null) {
            logger.debug("Can not send discovery packet out since outPkt is null");
            return;
        }

        long sid = (Long) nodeConnector.getNode().getID();
        ISwitch sw = controller.getSwitches().get(sid);

        if (sw == null) {
            logger.debug("Can not send discovery packet out since switch {} is null", sid);
            return;
        }

        if (!sw.isOperational()) {
            logger.debug("Can not send discovery packet out since switch {} is not operational", sw);
            return;
        }

        if (this.iDataPacketMux == null) {
            logger.debug("Can not send discovery packet out since DataPacket service is not available");
            return;
        }

        logger.trace("Sending topology discovery pkt thru {}", nodeConnector);
        this.iDataPacketMux.transmitDataPacket(outPkt);
    }*/

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

        /*
        if (ethPkt.getPayload() instanceof LLDP) {
            NodeConnector dst = inPkt.getIncomingNodeConnector();
            if (isEnabled(dst)) {
                if (!processDiscoveryPacket(dst, ethPkt)) {
                    // Snoop the discovery pkt if not generated from us
                    snoopDiscoveryPacket(dst, ethPkt);
                }
                return PacketResult.CONSUME;
            }
        }*///OF'code. snmp4sdn don't send/receive LLDP (the discovery task is done by snmp LLDP request)
        return PacketResult.IGNORED;
    }

    /*
     * Snoop incoming discovery frames generated by the production network
     * neighbor switch
     */
    /*
    private void snoopDiscoveryPacket(NodeConnector dstNodeConnector, Ethernet ethPkt) {
        if (!this.discoverySnoopingEnabled || discoverySnoopingDisableList.contains(dstNodeConnector)) {
            logger.trace("Discarded received discovery packet on {} since snooping is turned off", dstNodeConnector);
            return;
        }

        if ((dstNodeConnector == null) || (ethPkt == null)) {
            logger.trace("Quit snooping discovery packet: Null node connector or packet");
            return;
        }

        LLDP lldp = (LLDP) ethPkt.getPayload();

        try {
            String nodeId = LLDPTLV.getHexStringValue(lldp.getChassisId().getValue(), lldp.getChassisId().getLength());
            String portId = LLDPTLV.getStringValue(lldp.getPortId().getValue(), lldp.getPortId().getLength());
            byte[] systemNameBytes = null;
            // get system name if present in the LLDP pkt
            for (LLDPTLV lldptlv : lldp.getOptionalTLVList()) {
                if (lldptlv.getType() == LLDPTLV.TLVType.SystemName.getValue()) {
                    systemNameBytes = lldptlv.getValue();
                    break;
                }
            }
            String nodeName = (systemNameBytes == null) ? nodeId
                    : new String(systemNameBytes, Charset.defaultCharset());
            Node srcNode = new Node(Node.NodeIDType.PRODUCTION, nodeName);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                    NodeConnector.NodeConnectorIDType.PRODUCTION, portId, srcNode);

            Edge edge = null;
            Set<Property> props = null;
            edge = new Edge(srcNodeConnector, dstNodeConnector);
            props = getProps(dstNodeConnector);

            updateProdEdge(edge, props);
        } catch (Exception e) {
            logger.warn("Caught exception ", e);
        }
    }*///OF' need

    /*
     * Handle discovery frames generated by our controller
     *
     * @return true if it's a success
     */
    /*
    private boolean processDiscoveryPacket(NodeConnector dstNodeConnector, Ethernet ethPkt) {
        if ((dstNodeConnector == null) || (ethPkt == null)) {
            logger.trace("Ignoring processing of discovery packet: Null node connector or packet");
            return false;
        }

        logger.trace("Handle discovery packet {} from {}", ethPkt, dstNodeConnector);

        LLDP lldp = (LLDP) ethPkt.getPayload();

        List<LLDPTLV> optionalTLVList = lldp.getOptionalTLVList();
        if (optionalTLVList == null) {
            logger.info("The discovery packet with null custom option from {}", dstNodeConnector);
            return false;
        }

        Node srcNode = null;
        NodeConnector srcNodeConnector = null;
        for (LLDPTLV lldptlv : lldp.getOptionalTLVList()) {
            if (lldptlv.getType() == LLDPTLV.TLVType.Custom.getValue()) {
                String ncString = LLDPTLV.getCustomString(lldptlv.getValue(), lldptlv.getLength());
                srcNodeConnector = NodeConnector.fromString(ncString);
                if (srcNodeConnector != null) {
                    srcNode = srcNodeConnector.getNode();
                }
            }
        }

        if ((srcNode == null) || (srcNodeConnector == null)) {
            logger.trace("Received non-controller generated discovery packet from {}", dstNodeConnector);
            return false;
        }

        // push it out to Topology
        Edge edge = null;
        Set<Property> props = null;
        try {
            edge = new Edge(srcNodeConnector, dstNodeConnector);
            props = getProps(dstNodeConnector);
        } catch (ConstructionException e) {
            logger.error("Caught exception ", e);
        }
        addEdge(edge, props);
        pendingMap.put(dstNodeConnector, 0);

        logger.trace("Received discovery packet for Edge {}", edge);

        return true;
    }*///snmp4sdn: OF's need

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

    //snmp4sdn: OF's need
    /*
    private boolean isTracked(NodeConnector nodeConnector) {
        if (readyListHi.contains(nodeConnector)) {
            return true;
        }

        if (readyListLo.contains(nodeConnector)) {
            return true;
        }

        if (pendingMap.keySet().contains(nodeConnector)) {
            return true;
        }

        if (waitingList.contains(nodeConnector)) {
            return true;
        }

        return false;
    }*/

    //snmp4sdn: OF's need
    /*
    private Set<NodeConnector> getWorkingSet() {
        Set<NodeConnector> workingSet = new HashSet<NodeConnector>();
        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();

        for (NodeConnector nodeConnector : readyListHi) {
            if (isOverLimit(workingSet.size())) {
                break;
            }

            workingSet.add(nodeConnector);
            removeSet.add(nodeConnector);
        }
        readyListHi.removeAll(removeSet);

        removeSet.clear();
        for (NodeConnector nodeConnector : readyListLo) {
            if (isOverLimit(workingSet.size())) {
                break;
            }

            workingSet.add(nodeConnector);
            removeSet.add(nodeConnector);
        }
        readyListLo.removeAll(removeSet);

        return workingSet;
    }

    private Boolean isOverLimit(int size) {
        return ((size >= discoveryBatchMaxPorts) && !throttling);
    }*/

    /*//snmp4sdn. OF's need. Here addDiscovery for all switches, but our plugin do this by doEthSwDiscovery()
    private void addDiscovery() {
        Map<Long, ISwitch> switches = controller.getSwitches();
        Set<Long> sidSet = switches.keySet();
        if (sidSet == null) {
            return;
        }
        for (Long sid : sidSet) {
            Node node = NodeCreator.createOFNode(sid);
            Node node = NodeCreator.createESNode(sid);
            addDiscovery(node);
        }
    }*/

    private void addDiscovery(Node node) {
        Map<Long, ISwitch> switches = controller.getSwitches();
        ISwitch sw = switches.get(node.getID());
        /*//snmp4sdn. OF's need. OF do addDiscovery for every port, but our plugin is once a switch
        List<OFPhysicalPort> ports = sw.getEnabledPorts();
        if (ports == null) {
            return;
        }
        for (OFPhysicalPort port : ports) {
            NodeConnector nodeConnector = NodeConnectorCreator.createOFNodeConnector(port.getPortNumber(), node);
            if (!readyListHi.contains(nodeConnector)) {
                readyListHi.add(nodeConnector);
            }
        }*/
        transmitQ.add(node);
    }

    private void addDiscovery(NodeConnector nodeConnector) {
        /*//snmp4sdn. OF's need
        if (isTracked(nodeConnector)) {
            return;
        }

        readyListHi.add(nodeConnector);*/

        //processLinkUpTrap(nodeConnector);//TODO: processLinkUpTrap() mechanisum is currently diabled. If it is enabled, this line should not be marked. Due to updateNodeConnector() calls addDiscovery() and reach here to call processLinkUpTrap(), so currently we mark this line.
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

        /*//snmp4sdn. OF's need
        removeSet = getRemoveSet(readyListHi, node);
        readyListHi.removeAll(removeSet);

        removeSet = getRemoveSet(readyListLo, node);
        readyListLo.removeAll(removeSet);

        removeSet = getRemoveSet(waitingList, node);
        waitingList.removeAll(removeSet);

        removeSet = getRemoveSet(pendingMap.keySet(), node);
        for (NodeConnector nodeConnector : removeSet) {
            pendingMap.remove(nodeConnector);
        }*/

        removeSet = getRemoveSet(edgeMap.keySet(), node);
        for (NodeConnector nodeConnector : removeSet) {
            removeEdge(nodeConnector, false);
        }

        /*//snmp4sdn. OF's need
        removeSet = getRemoveSet(prodMap.keySet(), node);
        for (NodeConnector nodeConnector : removeSet) {
            removeProdEdge(nodeConnector);
        }*/
    }

    private void removeDiscovery(NodeConnector nodeConnector) {
        //snmp4sdn: OF's need
        /*
        readyListHi.remove(nodeConnector);
        readyListLo.remove(nodeConnector);
        waitingList.remove(nodeConnector);
        pendingMap.remove(nodeConnector);*/
        removeEdge(nodeConnector, false);
        /*//snmp4sdn: OF's need
        removeProdEdge(nodeConnector);*/
    }

    //snmp4sdn: OF's need
    /*
    private void checkTimeout() {
        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        Set<NodeConnector> retrySet = new HashSet<NodeConnector>();
        int ticks;

        Set<NodeConnector> pendingSet = pendingMap.keySet();
        if (pendingSet != null) {
            for (NodeConnector nodeConnector : pendingSet) {
                ticks = pendingMap.get(nodeConnector);
                pendingMap.put(nodeConnector, ++ticks);
                if (ticks > getDiscoveryFinalTimeoutInterval()) {
                    // timeout the edge
                    removeSet.add(nodeConnector);
                    logger.trace("Discovery timeout {}", nodeConnector);
                } else if (ticks % discoveryTimeoutTicks == 0) {
                    retrySet.add(nodeConnector);
                }
            }
        }

        for (NodeConnector nodeConnector : removeSet) {
            removeEdge(nodeConnector);
        }

        for (NodeConnector nodeConnector : retrySet) {
            transmitQ.add(nodeConnector);
        }
    }*/

    //snmp4sdn: OF's need
    /*
    private void checkAging() {
        if (!discoveryAgingEnabled) {
            return;
        }

        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        int ticks;

        Set<NodeConnector> agingSet = agingMap.keySet();
        if (agingSet != null) {
            for (NodeConnector nodeConnector : agingSet) {
                ticks = agingMap.get(nodeConnector);
                agingMap.put(nodeConnector, ++ticks);
                if (ticks > discoveryAgeoutTicks) {
                    // age out the edge
                    removeSet.add(nodeConnector);
                    logger.trace("Discovery age out {}", nodeConnector);
                }
            }
        }

        for (NodeConnector nodeConnector : removeSet) {
            removeProdEdge(nodeConnector);
        }
    }*/

    //snmp4sdn: OF's need
    /*
    private void doDiscovery() {
        if (++discoveryTimerTickCount <= discoveryBatchPauseTicks) {
            for (NodeConnector nodeConnector : getWorkingSet()) {
                transmitQ.add(nodeConnector);
            }
        } else if (discoveryTimerTickCount >= discoveryBatchRestartTicks) {
            discoveryTimerTickCount = 0;
            for (NodeConnector nodeConnector : waitingList) {
                if (!readyListLo.contains(nodeConnector)) {
                    readyListLo.add(nodeConnector);
                }
            }
            waitingList.removeAll(readyListLo);
        }
    }*/

    //snmp4sdn: OF's need
    /*
    private void doConsistencyCheck() {
        if (!discoveryConsistencyCheckEnabled) {
            return;
        }

        if (++discoveryConsistencyCheckTickCount % getDiscoveryConsistencyCheckInterval() != 0) {
            return;
        }

        discoveryConsistencyCheckCallingTimes++;

        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        Set<NodeConnector> ncSet = edgeMap.keySet();
        if (ncSet == null) {
            return;
        }
        for (NodeConnector nodeConnector : ncSet) {
            if (!isEnabled(nodeConnector)) {
                removeSet.add(nodeConnector);
                discoveryConsistencyCheckCorrected++;
                logger.debug("ConsistencyChecker: remove disabled {}", nodeConnector);
                continue;
            }

            if (!isTracked(nodeConnector)) {
                waitingList.add(nodeConnector);
                discoveryConsistencyCheckCorrected++;
                logger.debug("ConsistencyChecker: add back untracked {}", nodeConnector);
                continue;
            }
        }

        for (NodeConnector nodeConnector : removeSet) {
            removeEdge(nodeConnector, false);
        }

        // remove stale entries
        removeSet.clear();
        for (NodeConnector nodeConnector : waitingList) {
            if (!isEnabled(nodeConnector)) {
                removeSet.add(nodeConnector);
                discoveryConsistencyCheckCorrected++;
                logger.debug("ConsistencyChecker: remove disabled {}", nodeConnector);
            }
        }
        waitingList.removeAll(removeSet);

        // Get a snapshot of all the existing switches
        Map<Long, ISwitch> switches = this.controller.getSwitches();
        for (ISwitch sw : switches.values()) {
            for (OFPhysicalPort port : sw.getEnabledPorts()) {
                Node node = NodeCreator.createOFNode(sw.getId());
                NodeConnector nodeConnector = NodeConnectorCreator.createOFNodeConnector(port.getPortNumber(), node);
                if (!isTracked(nodeConnector)) {
                    waitingList.add(nodeConnector);
                    discoveryConsistencyCheckCorrected++;
                    logger.debug("ConsistencyChecker: add back untracked {}", nodeConnector);
                }
            }
        }
    }*/

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
        logger.debug("addEdge(): Add edge {}", edge);
    }

    /**
     * Update Production Edge
     *
     * @param edge
     *            The Production Edge
     * @param props
     *            Properties associated with the edge
     */
    //snmp4sdn: OF's need
    /*
    private void updateProdEdge(Edge edge, Set<Property> props) {
        NodeConnector edgePort = edge.getHeadNodeConnector();

        // * Do not update in case there is an existing OpenFlow link * //
        if (edgeMap.get(edgePort) != null) {
            logger.trace("Discarded edge {} since there is an existing OF link {}", edge, edgeMap.get(edgePort));
            return;
        }

        // * Look for any existing Production Edge * //
        Edge oldEdge = prodMap.get(edgePort);
        if (oldEdge == null) {
            // * Let's add a new one * //
            addEdge(edge, props);
        } else if (!edge.equals(oldEdge)) {
            // * Remove the old one first * //
            removeProdEdge(oldEdge.getHeadNodeConnector());
            // * Then add the new one * //
            addEdge(edge, props);
        } else {
            // * o/w, just reset the aging timer * //
            NodeConnector dst = edge.getHeadNodeConnector();
            agingMap.put(dst, 0);
        }
    }*/

    /**
     * Remove Production Edge for a given edge port
     *
     * @param edgePort
     *            The OF edge port
     */
    /*//snmp4sdn: OF's need
        private void removeProdEdge(NodeConnector edgePort) {
        agingMap.remove(edgePort);

        Edge edge = null;
        Set<NodeConnector> prodKeySet = prodMap.keySet();
        if ((prodKeySet != null) && (prodKeySet.contains(edgePort))) {
            edge = prodMap.get(edgePort);
            prodMap.remove(edgePort);
        }

        // notify Topology
        if (this.discoveryListener != null) {
            this.discoveryListener.notifyEdge(edge, UpdateType.REMOVED, null);
        }
        logger.trace("Remove edge {}", edge);
    }*/

    /*
     * Remove OpenFlow edge
     */
    private void removeEdge(NodeConnector nodeConnector, boolean stillEnabled) {
        /*//snmp4sdn: OF's need
        pendingMap.remove(nodeConnector);
        readyListLo.remove(nodeConnector);
        readyListHi.remove(nodeConnector);

        if (stillEnabled) {
            // keep discovering
            if (!waitingList.contains(nodeConnector)) {
                waitingList.add(nodeConnector);
            }
        } else {
            // stop it
            waitingList.remove(nodeConnector);
        }*/

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
            /*//snmp4sdn: OF's need// *
            // * Save Production edge into different DB keyed by the Edge port
            // * /
            if (type == UpdateType.ADDED) {
                prodMap.put(dst, edge);
            } else {
                prodMap.remove(dst);
            }*/
            //TODO

        }
    }

    //snmp4sdn: OF's need
    /*
    private void moveToReadyListHi(NodeConnector nodeConnector) {
        if (readyListLo.contains(nodeConnector)) {
            readyListLo.remove(nodeConnector);
        } else if (waitingList.contains(nodeConnector)) {
            waitingList.remove(nodeConnector);
        }
        readyListHi.add(nodeConnector);
    }*/

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this, null);
    }

    //snmp4sdn: OF's need
    /*
    private int getDiscoveryConsistencyCheckInterval() {
        return discoveryConsistencyCheckMultiple * discoveryBatchRestartTicks;
    }*/

    //snmp4sdn: OF's need
    /*
    private int getDiscoveryFinalTimeoutInterval() {
        return (discoveryRetry + 1) * discoveryTimeoutTicks;
    }*/

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

    //"seems to be removed in OF's code"
    /*
    public void _prlh(CommandInterpreter ci) {
        ci.println("ReadyListHi\n");
        for (NodeConnector nodeConnector : readyListHi) {
            if (nodeConnector == null) {
                continue;
            }
            ci.println(nodeConnector);
        }
    }

    public void _prll(CommandInterpreter ci) {
        ci.println("ReadyListLo\n");
        for (NodeConnector nodeConnector : readyListLo) {
            if (nodeConnector == null) {
                continue;
            }
            ci.println(nodeConnector);
        }
    }

    public void _pwl(CommandInterpreter ci) {
        ci.println("WaitingList\n");
        for (NodeConnector nodeConnector : waitingList) {
            if (nodeConnector == null) {
                continue;
            }
            ci.println(nodeConnector);
        }
    }

    public void _ppl(CommandInterpreter ci) {
        ci.println("pendingMap\n");
        ci.println("          NodeConnector            Last rx LLDP (s)");
        for (ConcurrentMap.Entry<NodeConnector, Integer> entry: pendingMap.entrySet()) {
            ci.println(entry.getKey() + "\t\t" + entry.getValue());
        }
    }

    public void _ptick(CommandInterpreter ci) {
        ci.println("Current timer is " + discoveryTimerTick + " msec per tick");
    }

    public void _pcc(CommandInterpreter ci) {
        if (discoveryConsistencyCheckEnabled) {
            ci.println("ConsistencyChecker is currently enabled");
        } else {
            ci.println("ConsistencyChecker is currently disabled");
        }
        ci.println("Interval " + getDiscoveryConsistencyCheckInterval());
        ci.println("Multiple " + discoveryConsistencyCheckMultiple);
        ci.println("Number of times called " + discoveryConsistencyCheckCallingTimes);
        ci.println("Corrected count " + discoveryConsistencyCheckCorrected);
    }

    public void _ptm(CommandInterpreter ci) {
        ci.println("Final timeout ticks " + getDiscoveryFinalTimeoutInterval());
        ci.println("Per timeout ticks " + discoveryTimeoutTicks);
        ci.println("Number of retries after initial timeout " + discoveryRetry);
    }

    public void _psize(CommandInterpreter ci) {
        ci.println("readyListLo size " + readyListLo.size() + "\n" + "readyListHi size " + readyListHi.size() + "\n"
                + "waitingList size " + waitingList.size() + "\n" + "pendingMap size " + pendingMap.size() + "\n"
                + "edgeMap size " + edgeMap.size() + "\n" + "prodMap size " + prodMap.size() + "\n" + "agingMap size "
                + agingMap.size());
    }

    public void _page(CommandInterpreter ci) {
        if (this.discoveryAgingEnabled) {
            ci.println("Aging is enabled");
        } else {
            ci.println("Aging is disabled");
        }
        ci.println("Current aging time limit " + this.discoveryAgeoutTicks);
        ci.println("\n");
        ci.println("                           Edge                                 Aging ");
        Collection<Edge> prodSet = prodMap.values();
        if (prodSet == null) {
            return;
        }
        for (Edge edge : prodSet) {
            Integer aging = agingMap.get(edge.getHeadNodeConnector());
            if (aging != null) {
                ci.println(edge + "      " + aging);
            }
        }
        ci.println("\n");
        ci.println("              NodeConnector                                                 Edge ");
        Set<NodeConnector> keySet = prodMap.keySet();
        if (keySet == null) {
            return;
        }
        for (NodeConnector nc : keySet) {
            ci.println(nc + "      " + prodMap.get(nc));
        }
        return;
    }

    public void _sage(CommandInterpreter ci) {
        String val = ci.nextArgument();
        if (val == null) {
            ci.println("Please enter aging time limit. Current value " + this.discoveryAgeoutTicks);
            return;
        }
        try {
            this.discoveryAgeoutTicks = Integer.parseInt(val);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _eage(CommandInterpreter ci) {
        this.discoveryAgingEnabled = true;
        ci.println("Aging is enabled");
        return;
    }

    public void _dage(CommandInterpreter ci) {
        this.discoveryAgingEnabled = false;
        ci.println("Aging is disabled");
        return;
    }

    public void _scc(CommandInterpreter ci) {
        String val = ci.nextArgument();
        if (val == null) {
            ci.println("Please enter CC multiple. Current multiple " + discoveryConsistencyCheckMultiple
                    + " (interval " + getDiscoveryConsistencyCheckInterval() + ") calling times "
                    + discoveryConsistencyCheckCallingTimes);
            return;
        }
        try {
            discoveryConsistencyCheckMultiple = Integer.parseInt(val);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _ecc(CommandInterpreter ci) {
        this.discoveryConsistencyCheckEnabled = true;
        ci.println("ConsistencyChecker is enabled");
        return;
    }

    public void _dcc(CommandInterpreter ci) {
        this.discoveryConsistencyCheckEnabled = false;
        ci.println("ConsistencyChecker is disabled");
        return;
    }

    public void _psnp(CommandInterpreter ci) {
        if (this.discoverySnoopingEnabled) {
            ci.println("Discovery snooping is globally enabled");
        } else {
            ci.println("Discovery snooping is globally disabled");
        }

        ci.println("\nDiscovery snooping is locally disabled on these ports");
        for (NodeConnector nodeConnector : discoverySnoopingDisableList) {
            ci.println(nodeConnector);
        }
        return;
    }

    public void _esnp(CommandInterpreter ci) {
        String val = ci.nextArgument();

        if (val == null) {
            ci.println("Usage: esnp <all|nodeConnector>");
        } else if (val.equalsIgnoreCase("all")) {
            this.discoverySnoopingEnabled = true;
            ci.println("Discovery snooping is globally enabled");
        } else {
            NodeConnector nodeConnector = NodeConnector.fromString(val);
            if (nodeConnector != null) {
                discoverySnoopingDisableList.remove(nodeConnector);
                ci.println("Discovery snooping is locally enabled on port " + nodeConnector);
            } else {
                ci.println("Entered invalid NodeConnector " + val);
            }
        }
        return;
    }

    public void _dsnp(CommandInterpreter ci) {
        String val = ci.nextArgument();

        if (val == null) {
            ci.println("Usage: dsnp <all|nodeConnector>");
        } else if (val.equalsIgnoreCase("all")) {
            this.discoverySnoopingEnabled = false;
            ci.println("Discovery snooping is globally disabled");
        } else {
            NodeConnector nodeConnector = NodeConnector.fromString(val);
            if (nodeConnector != null) {
                discoverySnoopingDisableList.add(nodeConnector);
                ci.println("Discovery snooping is locally disabled on port " + nodeConnector);
            } else {
                ci.println("Entered invalid NodeConnector " + val);
            }
        }
        return;
    }

    public void _spause(CommandInterpreter ci) {
        String val = ci.nextArgument();
        String out = "Please enter pause period less than " + discoveryBatchRestartTicks + ". Current pause period is "
                + discoveryBatchPausePeriod + " pause tick is " + discoveryBatchPauseTicks + ".";

        if (val != null) {
            try {
                int pause = Integer.parseInt(val);
                if (pause < discoveryBatchRestartTicks) {
                    discoveryBatchPausePeriod = pause;
                    discoveryBatchPauseTicks = discoveryBatchRestartTicks - discoveryBatchPausePeriod;
                    return;
                }
            } catch (Exception e) {
            }
        }

        ci.println(out);
    }

    public void _sdi(CommandInterpreter ci) {
        String val = ci.nextArgument();
        String out = "Please enter discovery interval greater than " + discoveryBatchPausePeriod
                + ". Current value is " + discoveryBatchRestartTicks + ".";

        if (val != null) {
            try {
                int restart = Integer.parseInt(val);
                if (restart > discoveryBatchPausePeriod) {
                    discoveryBatchRestartTicks = restart;
                    discoveryBatchPauseTicks = discoveryBatchRestartTicks - discoveryBatchPausePeriod;
                    return;
                }
            } catch (Exception e) {
            }
        }
        ci.println(out);
    }

    public void _sports(CommandInterpreter ci) {
        String val = ci.nextArgument();
        if (val == null) {
            ci.println("Please enter max ports per batch. Current value is " + discoveryBatchMaxPorts);
            return;
        }
        try {
            discoveryBatchMaxPorts = Integer.parseInt(val);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _sretry(CommandInterpreter ci) {
        String val = ci.nextArgument();
        if (val == null) {
            ci.println("Please enter number of retries. Current value is " + discoveryRetry);
            return;
        }
        try {
            discoveryRetry = Integer.parseInt(val);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _stm(CommandInterpreter ci) {
        String val = ci.nextArgument();
        String out = "Please enter timeout tick value less than " + discoveryBatchRestartTicks + ". Current value is "
                + discoveryTimeoutTicks;
        if (val != null) {
            try {
                int timeout = Integer.parseInt(val);
                if (timeout < discoveryBatchRestartTicks) {
                    discoveryTimeoutTicks = timeout;
                    return;
                }
            } catch (Exception e) {
            }
        }

        ci.println(out);
    }

    public void _addsw(CommandInterpreter ci) {
        String val = ci.nextArgument();
        Long sid;
        try {
            sid = Long.parseLong(val);
            Node node = NodeCreator.createOFNode(sid);
            addDiscovery(node);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _remsw(CommandInterpreter ci) {
        String val = ci.nextArgument();
        Long sid;
        try {
            sid = Long.parseLong(val);
            Node node = NodeCreator.createOFNode(sid);
            removeDiscovery(node);
        } catch (Exception e) {
            ci.println("Please enter a valid number");
        }
        return;
    }

    public void _pthrot(CommandInterpreter ci) {
        if (this.throttling) {
            ci.println("Throttling is enabled");
        } else {
            ci.println("Throttling is disabled");
        }
    }

    public void _ethrot(CommandInterpreter ci) {
        this.throttling = true;
        ci.println("Throttling is enabled");
        return;
    }

    public void _dthrot(CommandInterpreter ci) {
        this.throttling = false;
        ci.println("Throttling is disabled");
        return;
    }*///snmp4sdn. end of "seems to be removed in OF's code"

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

    //snmp4sdn: OF's need
    /*
    private void initDiscoveryPacket() {
        // Create LLDP ChassisID TLV
        chassisIdTlv = new LLDPTLV();
        chassisIdTlv.setType(LLDPTLV.TLVType.ChassisID.getValue());

        // Create LLDP PortID TLV
        portIdTlv = new LLDPTLV();
        portIdTlv.setType(LLDPTLV.TLVType.PortID.getValue());

        // Create LLDP TTL TLV
        byte[] ttl = new byte[] { (byte) 0, (byte) 120 };
        ttlTlv = new LLDPTLV();
        ttlTlv.setType(LLDPTLV.TLVType.TTL.getValue()).setLength((short) ttl.length).setValue(ttl);

        customTlv = new LLDPTLV();
    }*/

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    void init() {
        logger.trace("Init called");

        //transmitQ = new LinkedBlockingQueue<NodeConnector>();//snmp4sdn. OF's code
        transmitQ = new LinkedBlockingQueue<Node>();//snmp4sdn

        /*//snmp4sdn. OF's need
        readyListHi = new CopyOnWriteArrayList<NodeConnector>();
        readyListLo = new CopyOnWriteArrayList<NodeConnector>();
        waitingList = new CopyOnWriteArrayList<NodeConnector>();
        pendingMap = new ConcurrentHashMap<NodeConnector, Integer>();
        agingMap = new ConcurrentHashMap<NodeConnector, Integer>();
        prodMap = new ConcurrentHashMap<NodeConnector, Edge>();*/
        edgeMap = new ConcurrentHashMap<NodeConnector, Edge>();
        //discoverySnoopingDisableList = new CopyOnWriteArrayList<NodeConnector>();//snmp4sdn. OF's need

        /*//snmp4sdn: OF's need
        discoveryTimer = new Timer("DiscoveryService");
        discoveryTimerTask = new DiscoveryTimerTask();*/

        transmitThread = new Thread(new DiscoveryTransmit(transmitQ));

        //initDiscoveryPacket();//snmp4sdn: OF's need

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
        /*//snmp4sdn. OF's need
        readyListHi = null;
        readyListLo = null;
        waitingList = null;
        pendingMap = null;
        agingMap = null;
        prodMap = null;*/
        edgeMap = null;
        /*//snmp4sdn. OF's need
        discoveryTimer = null;
        discoveryTimerTask = null;*/
        transmitThread = null;
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
        //discoveryTimer.schedule(discoveryTimerTask, discoveryTimerTick, discoveryTimerTick);//snmp4sdn. OF's need
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
        //discoveryTimer.cancel();//snmp4sdn. OF's need
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
            //moveToReadyListHi(p);//snmp4sdn. OF's need.
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

        //clear previously collected LLDP data
        switches_ID2Chassis.clear();
        switches_Chassis2ID.clear();
        localPortIDsOnSwitches.clear();
        portToRemoteChassisOnSwitches.clear();
        portToRemotePortIDOnSwitches.clear();

        //clear temp LLDP data
        portToRemotePortIDOnSwitchesTmp.clear();

        //resolve snmp-snmp edges
        readLLDPonSwitches();
        resolvePortPairsAndAddEdges();

        //resolve non snmp-snmp edges
        resolveProdPortsAndAddEdges();
        
        //myAddEdgeProdSrc(new String("00:00:00:26:6c:f6:81:b0"), new String("1"), HexString.toLong("90:94:e4:23:0a:e0"), (short)8);//ovs48-switch35
        //myAddEdgeProdSrc(new String("00:00:fa:4b:c1:3e:b1:4a"), new String("1"), HexString.toLong("90:94:e4:23:0a:e0"), (short)8);//ovs18-switch35
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
            /* Author: Nanfei Chen. For Bug 4526: the result of calling rpc get-edge-list defined in topology.yang is false. */
            Map<Short, String> portToRemotePortIDCopy = new HashMap(portToRemotePortID);
            portToRemotePortIDOnSwitchesTmp.put(switchID, portToRemotePortIDCopy);
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
        //System.out.println("===========================================");
        //System.out.println("===== Resolve the port pars, and add edges correspondingly =====");
        //System.out.println("number of switches to resolve: " + portToRemoteChassisOnSwitches.size());
        for(Map.Entry<Long, Map<Short, String>> entryS : portToRemoteChassisOnSwitches.entrySet()){
            Long localSwitchID = entryS.getKey();
            String localChassis = switches_ID2Chassis.get(localSwitchID);
            Map<Short, String> localPortIDs = entryS.getValue();
            String localIP = cmethUtil.getIpAddr(localSwitchID);
            //System.out.println("  Processing local switch (id: ip = " + localIP + ", chassis: " + localChassis + ", number of ports which connect another remote switch: " + localPortIDs.size() + ")");
            for(Map.Entry<Short, String> entryP : localPortIDs.entrySet()){
                Short localPortNum = entryP.getKey();
                String remoteChassis = entryP.getValue();
                Long remoteSwitchID = switches_Chassis2ID.get(remoteChassis);
                Map<Short, String> remotePortIDs = portToRemotePortIDOnSwitches.get(remoteSwitchID);
                if(remotePortIDs == null)
                    continue;
                String localPortID = localPortIDsOnSwitches.get(localSwitchID).get(localPortNum);
                String remoteIP = cmethUtil.getIpAddr(remoteSwitchID);
                //System.out.println("\tchecking local port (num: " + localPortNum + ", id: " +localPortID + ", remote switch's chassis: " + remoteChassis + "), so look into this remote switch (ip: " + remoteIP + ")");
                for(Map.Entry<Short, String> entryR : remotePortIDs.entrySet()){
                    //System.out.println("...compare with remote port of id: " + entryR.getValue());
                	/* Author: Nanfei Chen. For Bug 4526: the result of calling rpc get-edge-list defined in topology.yang is false. */
                    if(entryR.getValue().compareToIgnoreCase(localPortID) == 0 &&
                    		portToRemoteChassisOnSwitches.get(remoteSwitchID).get(entryR.getKey()).compareToIgnoreCase(localChassis) ==0){
                        Short remotePortNum = entryR.getKey();

                        //report the edge found in this for loop
                        //System.out.println("\t\tAdd edge A->B: local (ip " + localIP + ", port " + localPortNum + ") --> remote (ip " + remoteIP + ", port " + remotePortNum +")");
                        myAddEdge(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);

                        //report the edge above but with reverse direction
                        //System.out.println("\t\tAdd edge B->A: local  (ip " + remoteIP + ", port " + remotePortNum +")--> remote (ip " + localIP + ", port " + localPortNum + ")");
                        myAddEdge(remoteSwitchID, remotePortNum, localSwitchID, localPortNum);

                        //remove the processed port in tmpMap
                        portToRemotePortIDOnSwitchesTmp.get(localSwitchID).remove(localPortNum);
                        portToRemotePortIDOnSwitchesTmp.get(remoteSwitchID).remove(remotePortNum);

                        break;
                    }
                }
            }
        }
        //System.out.println("===== end of Resolve the port pars, and add edges correspondingly =====");
        //System.out.println("=================================================");
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

    /*
    * Hybrid Link issue fix
    *
    * Author: Christine
    * Date: 2015/4/17
    * Description: To resolve PR->SNMP edges and report them.
    * Code modification: for this issue fix, we add the following code, and also add code to doEthSwDiscovery() and readLLDPonOneSwitch()
    *
    */
    private void resolveProdPortsAndAddEdges(){
        //System.out.println("===========================================");
        //System.out.println("===== Resolve the PR ports, and add edges correspondingly =====");
        //System.out.println("\tNumber of switches to resolve: " + portToRemoteChassisOnSwitches.size());
        for(Map.Entry<Long, Map<Short, String>> entryS : portToRemoteChassisOnSwitches.entrySet()){
            Long localSwitchID = entryS.getKey();
            Map<Short, String> remotePortIDs = portToRemotePortIDOnSwitchesTmp.get(localSwitchID);
            //System.out.println(localSwitchID + "'s size3="+ remotePortIDs.size());
            for(Map.Entry<Short, String> entryR : remotePortIDs.entrySet()){
                //logger.trace("...compare with remote port of id: " + entryR.getValue());
                Short localPortNum = entryR.getKey();
                String remotePortID = entryR.getValue();
                String remoteSwitchID = entryS.getValue().get(localPortNum);

                //TODO: the following line is specifically for OF which needs xx:xx:xx:xx:xx:xx:xx:xx format, can't deal with other case well
                remoteSwitchID = HexEncode.longToHexString(HexEncode.stringToLong(remoteSwitchID));

                //System.out.println("\t\tAdd PR edge: remote (ip " + remoteSwitchID + ", portID " + remotePortID +") --> local (ip " + localSwitchID + ", port " + localPortNum + ")");
                myAddEdgeProdSrc(remoteSwitchID, remotePortID, localSwitchID, localPortNum);
            }
        }
        //System.out.println("===== end of Resolve the PR ports, and add edges correspondingly =====");
        //System.out.println("=================================================");
    }

    private void myAddEdgeProdSrc(String srcSwitchID, String srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node("PR", srcSwitchID);//tried using "new Node("PR", Long value)", get exception in Node.java's NodeType-ValueType checking
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "PR", srcPortNum, srcNode);
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

    public void _s4sPrintDiscoveryServiceData(CommandInterpreter ci){
        print_switches_ID2Chassis(ci);
        print_switches_Chassis2ID(ci);
        print_localPortIDsOnSwitches(ci);
        print_portToRemoteChassisOnSwitches(ci);
        print_portToRemotePortIDOnSwitches(ci);
        print_portToRemotePortIDOnSwitchesTmp(ci);
    }

    private void print_switches_ID2Chassis(CommandInterpreter ci){
        System.out.println("------ switches_ID2Chassis -------");
        System.out.println("Switch ID\t\tChassis");
        for (Map.Entry<Long, String> entry : switches_ID2Chassis.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
        System.out.println();
    }

    private void print_switches_Chassis2ID(CommandInterpreter ci){
        System.out.println("------ switches_ID2Chassis -------");
        System.out.println("Chassis\t\tSwitch ID");
        for (Map.Entry<String, Long> entry : switches_Chassis2ID.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
        System.out.println();
    }

    private void print_localPortIDsOnSwitches(CommandInterpreter ci){
        System.out.println("------ localPortIDsOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : localPortIDsOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("local port " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }
    private void print_portToRemoteChassisOnSwitches(CommandInterpreter ci){
        System.out.println("------ portToRemoteChassisOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemoteChassisOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote chassis " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }
    private void print_portToRemotePortIDOnSwitches(CommandInterpreter ci){
        System.out.println("------ portToRemotePortIDOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemotePortIDOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote port ID " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }

    private void print_portToRemotePortIDOnSwitchesTmp(CommandInterpreter ci){
        System.out.println("------ portToRemotePortIDOnSwitchesTmp -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemotePortIDOnSwitchesTmp.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote port ID " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }

}
