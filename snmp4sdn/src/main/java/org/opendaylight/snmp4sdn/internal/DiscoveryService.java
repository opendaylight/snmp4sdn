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
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;//s4s add
import java.util.LinkedList;//s4s simulation add
import java.util.Vector;
import org.apache.commons.lang3.tuple.ImmutablePair;//s4s simulation add
import org.apache.commons.lang3.tuple.Pair;//s4s simulation add

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.snmp4sdn.IDataPacketListen;
import org.opendaylight.snmp4sdn.IDataPacketMux;
import org.opendaylight.snmp4sdn.IDiscoveryListener;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;
import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.internal.Controller;
//import org.openflow.protocol.OFPhysicalPort;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.snmp4sdn.sal.core.Config;
import org.opendaylight.snmp4sdn.sal.core.ConstructionException;
import org.opendaylight.snmp4sdn.sal.core.Edge;
import org.opendaylight.snmp4sdn.sal.core.ContainerFlow;
import org.opendaylight.snmp4sdn.sal.core.IContainerListener;
import org.opendaylight.snmp4sdn.sal.core.Node;
import org.opendaylight.snmp4sdn.sal.core.NodeConnector;
import org.opendaylight.snmp4sdn.sal.core.Property;
import org.opendaylight.snmp4sdn.sal.core.State;
import org.opendaylight.snmp4sdn.sal.core.UpdateType;
import org.opendaylight.snmp4sdn.sal.packet.Ethernet;
import org.opendaylight.snmp4sdn.sal.packet.LLDP;
import org.opendaylight.snmp4sdn.sal.packet.LLDPTLV;
import org.opendaylight.snmp4sdn.sal.packet.LinkEncap;
import org.opendaylight.snmp4sdn.sal.packet.PacketResult;
import org.opendaylight.snmp4sdn.sal.packet.RawPacket;
import org.opendaylight.snmp4sdn.sal.utils.EtherTypes;
import org.opendaylight.snmp4sdn.sal.utils.HexEncode;
import org.opendaylight.snmp4sdn.sal.utils.NetUtils;
import org.opendaylight.snmp4sdn.sal.utils.NodeConnectorCreator;
import org.opendaylight.snmp4sdn.sal.utils.NodeCreator;

import org.opendaylight.snmp4sdn.internal.SNMPHandler;//snmp4sdn add
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;//snmp4sdn add//TODO: replace HexString by HexCode?
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;//snmp4sdn add

/**
 * The class describes neighbor discovery service in a network.
 */
public class DiscoveryService implements IInventoryShimExternalListener, IDataPacketListen, IContainerListener, DiscoveryServiceAPI,
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
    private ConcurrentMap<NodeConnector, Integer> linkupPendingMap = null;//snpm4sdn add
    // openflow edges keyed by head connector
    private ConcurrentMap<NodeConnector, Edge> edgeMap = null;
    //snmp4sdn: OF's need
    /*
    // Aging entries keyed by head edge connector
    private ConcurrentMap<NodeConnector, Integer> agingMap = null;
    // Production edges keyed by head edge connector*/
    private ConcurrentMap<NodeConnector, Edge> prodMap = null;

    private Timer discoveryTimer;
    private DiscoveryTimerTask discoveryTimerTask;
    private long discoveryTimerTick = 1L * 1000; // per tick in msec
    private int discoveryTimerTickCount = 0; // main tick counter

    private boolean isPeriodicTopoDiscov = true;
    private Timer doTopoDiscovTimer;
    private DoTopologyDiscoveryTimerTask doTopoDiscovTimerTask;
    private long doTopoDiscovTimeout = 60L * 1000;
    private long doTopoDiscovWaitLateTimeout = 10L * 1000;

    private int lldpExchangeTime = 30;//LLDP exchange time (in seconds)
    private int pendingTimeoutTicks = (int)(lldpExchangeTime * 1000 / discoveryTimerTick);//s4s add
    private int retryTimeoutTicks = (int)(5L * 1000 / discoveryTimerTick);//retry to read LLDP data

    private int discoveryThreadTimeout = 10;//10 sec

    boolean isNotifyCancelTopoDiscov = false;

    private int lockWaitTime = 20;
    private Date lockTimestamp = null;

    //snmp4sdn: OF's need
    /*
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
    private List<NodeConnector> discoverySnoopingDisableList;*///snmp4sdn: OF'code
    private BlockingQueue<NodeConnector> transmitQ;
    private Thread transmitThread;
    private DiscoveryTransmit transmitThreadBody;
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
    Map<Long, Map<Short, String>> portToRemotePortIDOnSwitchesTmp = new HashMap<Long, Map<Short, String>>();//clone portToRemotePortIDOnSwitches, and then remove the Eth-to-Eth edges data, the remaining data is OF-to-Eth edges

    boolean isEnableNewInventoryTriggerDiscovery = true;

    //JobMgr jobMgr = new JobMgr();
    boolean isDoingTopologyDiscovery = false;

    private ConcurrentMap<Edge, Set<Property>> discovEdgeMap = null;
    private ConcurrentMap<Edge, Set<Property>> discovProdEdgeMap = null;

    boolean sim_stayTopologyDiscovery = false;
    boolean sim_stayDiscoveryThread = false;

    private boolean isFakeSim = false;//s4s: if true, generate fake parameters
    private int fakeSwNum = 0;
    private short fakeSwPortNum = 0;
    private int fakeSwNum2 = 0;//the switches with OF-to-SNMP edge
    private int fakeSwPortNum2 = 0;//the number of port on a switch with OF-to-SNMP edge

    class DiscoveryThread implements Runnable {
        NodeConnector nodeConnector;
        Date startTime;
        Vector threadVec;
        DiscoveryThread(NodeConnector nodeConnector, Vector threadVec){
            this.nodeConnector = nodeConnector;
            this.threadVec = threadVec;
            threadVec.add(this);
        }

        @Override
        public void run() {
            logger.trace("DiscoveryThread of NodeConnector {} starts", nodeConnector);
            startTime = new Date();
            processLinkUpTrap(nodeConnector);

            //Verification test
            //(Just for verification: the "lock" for DiscoveryThread against Topology Disovery is whether threadVec is empty, so before threadVec.remove(), we pause here on purpose)
            if(threadVec.size() == 1){
                if(sim_stayDiscoveryThread){
                    System.out.println();
                    System.out.println("The DiscoveryThread (of NodeConnector " + nodeConnector+ ") is set to stay, pause for 15 seconds...");
                    System.out.println();
                    try{
                            Thread.sleep(15000);
                    }catch(Exception e1){
                        logger.debug("DiscoveryThread.run(): in the sim_stayDiscoveryThread loop, Thread.sleep() error: {}", e1);
                    }
                }
            }
            //end of Verification test

            threadVec.remove(this);
        }

        /*public boolean isTimeout(){
            return (((new Date()).getTime() - startTime.getTime()) > discoveryThreadTimeout * 1000)?true:false;
        }*/
    }

    class DiscoveryTransmit implements Runnable {
        private final BlockingQueue<NodeConnector> transmitQ;
        public Vector threadVec = new Vector();//to keep the DiscoveryThreads. If vector size = 0, means all link-up events are done

        DiscoveryTransmit(BlockingQueue<NodeConnector> transmitQ) {
            this.transmitQ = transmitQ;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    //TODO: the trace code printing transmitQ.size() is not correct, because multiple thread running, the log print from different threads concurrently!
                    logger.trace("transmitQ.size() = {} (before take)", transmitQ.size());
                    NodeConnector nodeConnector = transmitQ.take();
                    logger.trace("transmitQ.take(): {}", nodeConnector);
                    logger.trace("transmitQ.size() = {} (after take)", transmitQ.size());
                    /*//snmp4sdn. OF's need
                    RawPacket outPkt = createDiscoveryPacket(nodeConnector);
                    sendDiscoveryPacket(nodeConnector, outPkt);
                    nodeConnector = null;*/

                    /*s4s: here should process new switch join in and to figure out the topology.
                        but currently we don't deal with "a new switch" but we deal with "link up trap -- that is, a new port"
                    */

                    //processLinkUpTrap(nodeConnector);//Bug fix: processLinkUpTrap() consume time to read LLDP, if transmitQ has lots of ports to deal with, it may take too much time to go over transmitQ.

                    logger.trace("new DiscoveryThread() with NodeConnector {}", nodeConnector);
                    (new Thread(new DiscoveryThread(nodeConnector, threadVec))).start();
                    //TODO: put the DiscoveryThread in a List, and check DiscoveryThread.isTimeout() in checkTimeout()

                } catch (InterruptedException e1) {
                    logger.warn("DiscoveryTransmit interupted: {}", e1.getMessage());
                    if (shuttingDown) {
                        return;
                    }
                } catch (Exception e2) {
                    logger.error("ERROR: DiscoveryTransmit exception: {}", e2);
                }
            }
        }
    }

    class DiscoveryTimerTask extends TimerTask {
        @Override
        public void run() {

            //if current state is doing Topology Discovery, pause link event processing (i.e. can't enter the checkTimeout() below)
            while(isDoingTopologyDiscovery){
                try{
                    Thread.sleep(100);
                }catch(Exception e1){
                    logger.debug("DiscoveryTimerTask: run(): Thread.sleep() error: {}", e1);
                    freeLocks();
                    return;
                }
            }

            checkTimeout();
            /*checkAging();
            doConsistencyCheck();
            doDiscovery();*/
        }
    }

    class DoTopologyDiscoveryTimerTask extends TimerTask{

        @Override
        public void run() {

            //Check periodic Topology Discovery is disabled?
            if(!isPeriodicTopoDiscov){
                logger.trace("Enter periodic Topology Discovery task, but this function is disabled, so return.");
                return;
            }

            //Check there exists Topology Discovery executing now
            logger.debug("Enter periodic Topology Discovery task");
            if(isDoingTopologyDiscovery){
                logger.debug("DoTopologyDiscoveryTimerTask(): there exists another Topology Discovery task executing, so cancel this one!");
                return;
            }

            //Do topology discovery
            doTopologyDiscovery();
        }

        /*public void setPeriod(long period) {
            setDeclaredField(TimerTask.class, this, "period", period);
        }

        static boolean setDeclaredField(Class<?> clazz, Object obj,
            String name, Object value) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(obj, value);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }*/
    }

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
        //State state = (State) getProp(nodeConnector, State.StatePropName);
        return ((config != null) && (config.getValue() == Config.ADMIN_UP) /*&& (state != null) && (state.getValue() == State.EDGE_UP)*/);
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

    /*//snmp4sdn. OF's need. Here addDiscovery for all switches, but our plugin do this by doTopoDiscovery()
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
        }
        pendingMap.put(dstNodeConnector, 0);
        transmitQ.add(node);*/
    }

    private void addDiscovery(NodeConnector nodeConnector) {
        /*//snmp4sdn. OF's need
        if (isTracked(nodeConnector)) {
            return;
        }

        readyListHi.add(nodeConnector);*/

        linkupPendingMap.put(nodeConnector, -1);//Bug fix: -1 is the init tick, will be increase by 1 and program goes to processLinkUpTrap() to resolve edge, otherwise need to wait for a retry time then can enter processLinkUpTrap()
        //processLinkUpTrap(nodeConnector);//do this in the DiscoveryTransmit thread polling transmitQ
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

        removeSet = getRemoveSet(prodMap.keySet(), node);
        for (NodeConnector nodeConnector : removeSet) {
            removeProdEdge(nodeConnector);
        }
    }

    private void removeDiscovery(NodeConnector nodeConnector) {
        //snmp4sdn: OF's need
        /*
        readyListHi.remove(nodeConnector);
        readyListLo.remove(nodeConnector);
        waitingList.remove(nodeConnector);
        pendingMap.remove(nodeConnector);*/
        linkupPendingMap.remove(nodeConnector);
        removeEdge(nodeConnector, false);
        removeProdEdge(nodeConnector);//TODO: the function has been covered in removeEdge() above

        //Update LLDP (remove the LLDP data of remote chassis and port information of the nodeconnector)
            //TODO: need update? (not preferred, due to it's blocking) or delete the entries? (not preferred) or just leave it? (preffered, because our code refresh LLDP data before using)
        if(nodeConnector.getID() == null){
            logger.debug("ERROR: removeDiscovery(): given NodeConnector has null ID");
            return;
        }
        Node node = nodeConnector.getNode();
        if(node == null){
            logger.debug("ERROR: removeDiscovery(): given NodeConnector, whose ID {}, has null Node", (Short)(nodeConnector.getID()));
            return;
        }
        /*boolean bool = readLLDPonOneSwitch((Long)(node.getID()));
        if(!bool){
            logger.debug("ERROR: removeDiscovery(): readLLDPonOneSwitch() fail, given the Node {} of NodeConnector {}", node, nodeConnector);
            return;
        }*/
        if(isFakeSim){//for simulation
            Long switchID = (Long)(node.getID());
            Short portID = (Short)(nodeConnector.getID());
            removeFakeLLDPData(switchID, portID);
            logger.debug("WARNING: remove fake LLDP data of switch {} port {} (but note that in non-simulation, we don't read LLDP for link-down event!)", switchID, portID);
        }
    }

    /*private boolean removeLLDPData(NodeConnector nodeConnector) {//try to fix but useless: if port-down then port-up very quickly, the LLDP data is still old though it is possible will be changed.

        //check null
        Node node = nodeConnector.getNode();
        if(node == null){
            logger.debug("ERROR: removeLLDPData() given NodeConnector {}, fails because the Node is null", nodeConnector);
            return false;
        }
        Long nodeId = (Long)(node.getID());
        if(nodeId == null){
            logger.debug("ERROR: removeLLDPData() given NodeConnector {}, fails because the nodeID is null", nodeConnector);
            return false;
        }
        Short portId = (Short)(nodeConnector.getID());
        if(nodeId == null){
            logger.debug("ERROR: removeLLDPData() given NodeConnector {}, fails because the portID is null", nodeConnector);
            return false;
        }

        if(portToRemoteChassisOnSwitches.get(nodeId) != null){
            portToRemoteChassisOnSwitches.get(nodeId).remove(portId);
        }
        if(portToRemotePortIDOnSwitches.get(nodeId) != null){
            portToRemotePortIDOnSwitches.get(nodeId).remove(portId);
        }
        if(portToRemotePortIDOnSwitchesTmp.get(nodeId) != null){
            portToRemotePortIDOnSwitchesTmp.get(nodeId).remove(portId);
        }

        return true;
    }*/

    //This function is only used in simulation.
    //remove the LLDP data of remote chassis and port information of the nodeconnector
    private void removeFakeLLDPData(Long switchID, Short portID){
            Map<Short, String> portToRemoteChassis = portToRemoteChassisOnSwitches.get(switchID);
            if(portToRemoteChassis == null){
                logger.debug("WARNING: removeFakeLLDPData(): call portToRemoteChassisOnSwitches.get() fails, given switchID {}", switchID);
            }
            else{
                portToRemoteChassis.remove(portID);
                //portToRemoteChassisOnSwitches.put(switchID, portToRemoteChassis);
            }

            Map<Short, String> portToRemotePortID = portToRemotePortIDOnSwitches.get(switchID);
            if(portToRemotePortID == null){
                logger.debug("WARNING: removeFakeLLDPData(): call portToRemotePortIDOnSwitches.get() fails, given switchID {}", switchID);
            }
            else{
                portToRemotePortID.remove(portID);
                //portToRemotePortIDOnSwitches.put(switchID, portToRemotePortID);
            }

            Map<Short, String> portToRemotePortIDTmp = portToRemotePortIDOnSwitchesTmp.get(switchID);
            if(portToRemotePortIDTmp == null){
                logger.debug("WARNING: removeFakeLLDPData(): call portToRemotePortIDOnSwitchesTmp.get() fails, given switchID {}", switchID);
            }
            else{
                portToRemotePortIDTmp.remove(portID);
                //portToRemotePortIDOnSwitchesTmp.put(switchID, portToRemotePortID);
            }

            logger.trace("LLDP data update: remove the remote chassis and port information of switch {} port {}", switchID, portID);
    }

    private void checkTimeout() {
        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        Set<NodeConnector> retrySet = new HashSet<NodeConnector>();
        int ticks;

        Set<NodeConnector> pendingSet = linkupPendingMap.keySet();
        if (pendingSet != null) {
            for (NodeConnector nodeConnector : pendingSet) {
                //s4s. OF's code is replace by the similar code below
                /*ticks = pendingMap.get(nodeConnector);
                pendingMap.put(nodeConnector, ++ticks);
                if (ticks > getDiscoveryFinalTimeoutInterval()) {
                    // timeout the edge
                    removeSet.add(nodeConnector);
                    logger.trace("Discovery timeout {}", nodeConnector);
                } else if (ticks % discoveryTimeoutTicks == 0) {
                    retrySet.add(nodeConnector);
                }*/
                ticks = linkupPendingMap.get(nodeConnector);
                linkupPendingMap.put(nodeConnector, ++ticks);//TODO: "++ticks" results in that "ticks % retryTimeoutTicks ==0" later so that edge resolving would not be excecute immediatly
                //logger.trace("{}: {} ticks", nodeConnector, ticks);
                if(ticks > pendingTimeoutTicks){
                    //timeout the edge
                    removeSet.add(nodeConnector);
                    logger.debug("Link-up pending timeout: {}", nodeConnector);
                } else if (ticks % retryTimeoutTicks == 0) {
                    retrySet.add(nodeConnector);
                    logger.trace("Link-up retry resolving: {}", nodeConnector);//lg.dbug-trc
                }
            }
        }

        for (NodeConnector nodeConnector : removeSet) {
            //removeEdge(nodeConnector);//OF's code. snmpsdn also needs this?
            logger.trace("linkupPendingMap.remove(): {}", nodeConnector);
            linkupPendingMap.remove(nodeConnector);//snmp4sdn add
        }

        for (NodeConnector nodeConnector : retrySet) {
            if(transmitQ.contains(nodeConnector))//Bug fix: transmitQ is not processed finished yet (due to in processLinkUpTrap(), reading LLDP needs time), but retry timeout comes
                logger.trace("transmitQ.add() but ignore since existed: {}", nodeConnector);
            else{
                logger.trace("transmitQ.add(): {}", nodeConnector);
                transmitQ.add(nodeConnector);
            }
        }
    }

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
        logger.trace("addEdge(): Add edge {}", edge);//lg.dbug-trc
    }

    /**
     * Update Production Edge
     *
     * @param edge
     *            The Production Edge
     * @param props
     *            Properties associated with the edge
     */
    private void updateProdEdge(Edge edge, Set<Property> props) {
        NodeConnector edgePort = edge.getHeadNodeConnector();

        // * Do not update in case there is an existing link * //
        if (edgeMap.get(edgePort) != null) {
            logger.trace("Discarded edge {} since there is an existing link {}", edge, edgeMap.get(edgePort));
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
            /*NodeConnector dst = edge.getHeadNodeConnector();
            agingMap.put(dst, 0);*/
            //TODO: do we want to handle PR edge which is discovered again and again
        }
    }

    /**
     * Remove Production Edge for a given edge port
     *
     * @param edgePort
     *            The OF edge port
     */
    private void removeProdEdge(NodeConnector edgePort) {
        //agingMap.remove(edgePort);

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
    }

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

        //bug fix: we found a bug that we want to remove an PR1->SNMP2 edge, so the code below ("prodMap.remove(nodeConnector)") will do this. But, the code above ("edgeMap.remove(nodeConnector)") is also executed and remove an SNMP3->SNMP2 edge, which is wrong!
        //so the solution should be: since we have another function, removeProdEdge(), we should remove SNMP-SNMP egde by here removeEdge(), and remove PR-SNMP edge by removeProdEdge()
        //so we disable the code below
        /*
        Set<NodeConnector> edgeKeySetPr = prodMap.keySet();//For PR edges
        if ((edgeKeySetPr != null) && (edgeKeySetPr.contains(nodeConnector))) {
            edge = prodMap.get(nodeConnector);
            prodMap.remove(nodeConnector);
        }*/

        // notify Topology
        if (this.discoveryListener != null) {
            this.discoveryListener.notifyEdge(edge, UpdateType.REMOVED, null);
        }
        logger.trace("Remove {}", edge);
    }

    private void removeEdge(NodeConnector nodeConnector) {
        removeEdge(nodeConnector, isEnabled(nodeConnector));//for snmp4sdn, the second parameter has no effect to program
    }

    private void updateEdge(Edge edge, UpdateType type, Set<Property> props) {
        if (discoveryListener == null) {
            return;
        }

        /*//Bug fix: To independently maintain the edges in DiscoveryService. 
        Reason: Due to that, the code below, discoveryListener.notifyEdge() is to report upward the edge, 
        and the code below using edge2 to update local edgeMap and prodMap, if we use the same Edge object in both code, 
        the change in upper layer (including TopologyManager, in which resolve PR edge to such as OF-SNMP edge) also affect here.
        For correct updateProdEdgesAgainstKnownEdges(), we need to have independent edges maintainance in DiscoveryService from upper layer.
        */
        Edge edge2 = null;
        try{
            edge2 = new Edge(edge);
        }catch (ConstructionException e1) {
            freeLocks();//updateEdge() would be gone though during doTopoDiscovery(). During doTopoDiscovery(), freeLocks() needs to be called in any exception case. So we add freeLocks() here.
            logger.debug("ERROR: updateEdge(): create an Edge cloning Edge {}, occur exception: {}", e1);
            return;
        }

        this.discoveryListener.notifyEdge(edge, type, props);

        NodeConnector src = edge2.getTailNodeConnector(), dst = edge2.getHeadNodeConnector();
        if (!src.getType().equals(NodeConnector.NodeConnectorIDType.PRODUCTION)) {
            if (type == UpdateType.ADDED) {
                edgeMap.put(dst, edge2);
            } else {
                edgeMap.remove(dst);
            }
        }
        else {
            if (type == UpdateType.ADDED) {
                prodMap.put(dst, edge2);
            } else {
                prodMap.remove(dst);
            }
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
        /*if(!isEnableNewInventoryTriggerDiscovery){
            logger.info("SNMP4SDN: DiscoveryService is notified of a node event {Node {}, event {}}, but now DiscoveryService is set as disable of edge resolving for inventory event", node, type);
            return;
        }*/

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
        if(!isEnableNewInventoryTriggerDiscovery){
            logger.info("SNMP4SDN: DiscoveryService is notified of a port event {port {}, event {}}, but now DiscoveryService is set as disable of edge resolving for inventory event", nodeConnector, type);
            return;
        }

        Config config = null;
        //State state = null;
        boolean enabled = false;

        for (Property prop : props) {
            if (prop.getName().equals(Config.ConfigPropName)) {
                config = (Config) prop;
            } /*else if (prop.getName().equals(State.StatePropName)) {
                state = (State) prop;
            }*/
        }
        enabled = ((config != null) && (config.getValue() == Config.ADMIN_UP) /*&& (state != null) && (state.getValue() == State.EDGE_UP)*/);

        switch (type) {
        case ADDED:
            if (enabled) {
                addDiscovery(nodeConnector);
                logger.trace("ADDED enabled {}", nodeConnector);
            } else {
                logger.trace("ADDED disabled {}", nodeConnector);
            }
            break;
        case CHANGED:
            if (enabled) {
                addDiscovery(nodeConnector);
                logger.trace("CHANGED enabled {}", nodeConnector);
            } else {
                removeDiscovery(nodeConnector);
                logger.trace("CHANGED disabled {}", nodeConnector);
            }
            break;
        case REMOVED:
            removeDiscovery(nodeConnector);
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

        transmitQ = new LinkedBlockingQueue<NodeConnector>();

        /*//snmp4sdn. OF's need
        readyListHi = new CopyOnWriteArrayList<NodeConnector>();
        readyListLo = new CopyOnWriteArrayList<NodeConnector>();
        waitingList = new CopyOnWriteArrayList<NodeConnector>();
        pendingMap = new ConcurrentHashMap<NodeConnector, Integer>();
        agingMap = new ConcurrentHashMap<NodeConnector, Integer>();*/
        prodMap = new ConcurrentHashMap<NodeConnector, Edge>();
        edgeMap = new ConcurrentHashMap<NodeConnector, Edge>();
        linkupPendingMap = new ConcurrentHashMap<NodeConnector, Integer>();
        //discoverySnoopingDisableList = new CopyOnWriteArrayList<NodeConnector>();//snmp4sdn. OF's need

        discoveryTimer = new Timer("DiscoveryService");
        discoveryTimerTask = new DiscoveryTimerTask();

        //transmitThread = new Thread(new DiscoveryTransmit(transmitQ));//replace this line by the following two lines. This is for we can access DiscoveryTransmit's method in other places.
        transmitThreadBody = new DiscoveryTransmit(transmitQ);
        transmitThread = new Thread(transmitThreadBody);

        doTopoDiscovTimer = new Timer("PeriodicTopologyDiscovery");
        doTopoDiscovTimerTask = new DoTopologyDiscoveryTimerTask();

        discovEdgeMap = new ConcurrentHashMap<Edge, Set<Property>>();
        discovProdEdgeMap = new ConcurrentHashMap<Edge, Set<Property>>();

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
        agingMap = null;*/
        prodMap = null;
        edgeMap = null;
        linkupPendingMap = null;
        discoveryTimer = null;
        discoveryTimerTask = null;
        transmitThread = null;
        transmitThreadBody = null;
        discovEdgeMap = null;
        discovProdEdgeMap = null;
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
        /*
        NOTICE: doTopoDiscovWaitLateTimeout is actually for an issue that
        due to the race condition that an OF->SNMP edge, 
        which is identified by snmp4sdn plugin as PR->SNMP edge, 
        can't immediate be resolved in Topology Manager when OF plugin not yet report the OF switch,
        so snmp4sdn plugin needs to report the PR edge again after OF switch is identified in Controller.
        Therefore, we set the Periodic Topology Discovery to start in some time after snmp4sdn plugin starts, say 15 seconds,
        as the timer schedule below.
        */

        //The timer for Periodic Topology Discovery
        doTopoDiscovTimer.schedule(doTopoDiscovTimerTask, doTopoDiscovWaitLateTimeout, doTopoDiscovTimeout);

        //The timer for checking pending link-up event
        discoveryTimer.schedule(discoveryTimerTask, discoveryTimerTick, discoveryTimerTick);//snmp4sdn. OF's need

        //The thread for handling link-up events to be processing
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
        isDoingTopologyDiscovery = false;//in some code, including thread or timertask..., isDoingTopologyDiscovery is used to decide whether program keep going, so we set isDoingTopologyDiscovery as false to stop program going
        //bug fix: when uninstall snmp4sdn plugin, a null exception occurs due to that in destroy() the linkupPendingMap is set as null, but linkupPendingMap is still used in the checkTimeout() which is called in DiscoveryTimerTask. So we should correctly cancel DiscoveryTimerTask first. To cancel DiscoveryTimerTask, just cancel discoveryTimer.
        discoveryTimer.cancel();
        doTopoDiscovTimer.cancel();
        shuttingDown = true;//shuttingDown as true will make the following transmitThread stop (transmitThread wraps transmitThreadBody, transmitThreadBody is a DiscoveryTransmit. DiscoveryTransmit is about processing link-up event)
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
        if(nodeConnector == null){
            logger.debug("ERROR: processLinkUpTrap(): given null NodeConnector! so stop processLinkUpTrap()");
            return;
        }
        Short localPortNum = (Short)(nodeConnector.getID());
        if(localPortNum == null){
            logger.debug("ERROR: processLinkUpTrap(): null NodeConnector ID! so stop processLinkUpTrap()");
            return;
        }
        Node node = nodeConnector.getNode();
        if(node == null){
            logger.debug("ERROR: processLinkUpTrap(): given NodeConnector (port {}) with null Node! so stop processLinkUpTrap()", localPortNum);
            return;
        }
        Long localSwitchID = (Long)(node.getID());
        if(localSwitchID == null){
            logger.debug("ERROR: processLinkUpTrap(): given NodeConnector (port {}) with null Node ID! so stop processLinkUpTrap()", localPortNum);
            return;
        }

        logger.trace("Enter DiscoveryService.processLinkUpTrap(), given NodeConnector {}", nodeConnector);

        //Refresh local switch's LLDP data
        boolean val = readLLDPonOneSwitch(localSwitchID);//read the LLDP data on this port's switch//TODO:should condense to "readLLDP(switch, port)"
        if(val == false){
            logger.debug("WARNING: processLinkUpTrap(): call readLLDPonOneSwitch() for local switch ID {} fails! so stop processLinkUpTrap()", localSwitchID);//lg.dbug-trc
            return;
        }

        //Local switch LLDP data - remote switch chassis
        if(portToRemoteChassisOnSwitches.get(localSwitchID) == null){
            logger.trace("WARNING: processLinkUpTrap(): local switch {} has no LLDP data of portToRemoteChassis! so stop processLinkUpTrap()", localSwitchID);//lg.dbug-trc
            return;
        }
        String remoteChassis = portToRemoteChassisOnSwitches.get(localSwitchID).get(localPortNum);
        if(remoteChassis == null){
            logger.trace("WARNING: processLinkUpTrap(): for local switch {} port {}, LLDP remoteChassis is unknown now! so stop processLinkUpTrap()", localSwitchID, localPortNum);//lg.dbug-trc
            return;
        }
        else
            logger.trace("--> this port's remoteChassis is " + remoteChassis);

        //Local switch LLDP data - remote switch port ID
        if(portToRemotePortIDOnSwitches.get(localSwitchID) == null){
            logger.trace("WARNING: processLinkUpTrap(): local switch {} has no LLDP data of portToRemotePortID! so stop processLinkUpTrap()", localSwitchID);//lg.dbug-trc
            return;
        }
        String remotePortID = portToRemotePortIDOnSwitches.get(localSwitchID).get(localPortNum);
        if(remotePortID == null){
            logger.debug("ERROR: can't find LLDP remote port ID for switch {} port {}! so stop processLinkUpTrap() (==> abnormal, should not happen!since remoteChassis is known)", localSwitchID, localPortNum);
            return;
        }
        else
            logger.trace("--> this port's remotePortID is " + remotePortID);


        //now LLDP chassis/port on both switches are ready, now can read about this switch's LLDP data and mapping to the remote switch's

        //case 1: PR edge (no chassis-id mapping for the remote node, means the remote node is PR)
        Long remoteSwitchID = switches_Chassis2ID.get(remoteChassis);
        if(remoteSwitchID == null){
            //NOTICE: since no remote switch ID in switches_Chassis2ID, it implies that remote switch is an OF switch, or an not-yet-identified Eth switch. Anyway, so let's report an PR edge here.
            logger.trace("In chassis-id mapping can't find the node ID for remote switch of chassis {} (==> report as a PR edge)", remoteChassis);
            linkupPendingMap.remove(nodeConnector);

            //TODO: the following line is specifically for OF which needs xx:xx:xx:xx:xx:xx:xx:xx format, can't deal with other case well
            String remoteSwitchIDStr = HexEncode.longToHexString(HexEncode.stringToLong(remoteChassis));
            myAddEdgeProdSrc(remoteSwitchIDStr, remotePortID, localSwitchID, localPortNum);
            return;
        }
        else
            logger.trace("--> this port's remoteSwitchID is " + remoteSwitchID);


        //case2: Eth-to-Eth edge
        //Refresh remote switch's LLDP data
        boolean val2 = readLLDPonOneSwitch(remoteSwitchID);//read the LLDP data on this port's switch//TODO:should condense to "readLLDP(switch, port)"
        if(val2 == false){
            logger.debug("WARNING: processLinkUpTrap(): call readLLDPonOneSwitch() for remote switch ID {} fails! so stop processLinkUpTrap()", remoteSwitchID);
            return;
        }

        //For reverse checking: get all remote switch's remote port ID
        Map<Short, String>remoteSwitchPortIDs = localPortIDsOnSwitches.get(remoteSwitchID);
        if(remoteSwitchPortIDs == null){
            logger.debug("ERROR: processLinkUpTrap(): remote switch {} has no local port ID LLDP data! so stop processLinkUpTrap()", remoteSwitchID);
            return;
        }

        //Find out the remote port (in remote switch's ports, get the one whose ID meets local switch's remote port), then check LLDP data consistency
        logger.trace("Compare with remote switch " + HexString.toHexString(remoteSwitchID) + "(ip " + cmethUtil.getIpAddr(remoteSwitchID)  +")'s " + remoteSwitchPortIDs.size() + " ports");
        for(Map.Entry<Short, String> entry : remoteSwitchPortIDs.entrySet()){
            logger.trace("\tcompare with remote port of id: " + entry.getValue());
            if(entry.getValue().compareToIgnoreCase(remotePortID) == 0){
                Short remotePortNum = entry.getKey();
                if(remotePortNum == null){
                    logger.debug("ERROR: processLinkUpTrap(): remote switch {} has a null local port ID in LLDP data! so stop processLinkUpTrap()", remoteSwitchID);
                    return;
                }

                //check the remote switch's LLDP that the remote port directs to the local switch and local port
                boolean isConsistent = checkRemoteLLDPConsistentWithLocalLLDP(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);
                if(!isConsistent){
                    logger.trace("WARNING: LLDP consistency checking fails: due to some input data is null, or due to remote switch {} port {} and local switch {} port {} not consistent", localSwitchID, localPortNum, remoteSwitchID, remotePortNum);//lg.dbug-trc
                    return;
                }

                logger.trace("\t\t==> Add edge: local (ip " + cmethUtil.getIpAddr(localSwitchID) + ", port " + localPortNum + ") --> remote (ip " + cmethUtil.getIpAddr(remoteSwitchID) + ", port " + remotePortNum +")");
                linkupPendingMap.remove(nodeConnector);
                myAddEdge(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);
                myAddEdge(remoteSwitchID, remotePortNum, localSwitchID, localPortNum);//TODO: strictly we should only report one edge, but if only report one edge, the edge is rejected by TopologyManager since the remote port is not yet known by controller. So here we add two edges at once.
                break;
            }
        }
    }

    //check the remote switch's LLDP that the remote port directs to the local switch and local port
    private boolean checkRemoteLLDPConsistentWithLocalLLDP(Long localSwitchID, Short localPortNum, Long remoteSwitchID, Short remotePortNum){
                String localChassis = switches_ID2Chassis.get(localSwitchID);
                String localPortID = localPortIDsOnSwitches.get(localSwitchID).get(localPortNum);
                String checkLocalChassis = portToRemoteChassisOnSwitches.get(remoteSwitchID).get(remotePortNum);
                String checkLocalPortID = portToRemotePortIDOnSwitches.get(remoteSwitchID).get(remotePortNum);
                if(localChassis == null){
                    logger.debug("WARNING: can't look up required LLDP data: switches_ID2Chassis.get(), given localSwitchID {}", switches_ID2Chassis.get(localSwitchID));
                    return false;
                }
                if(localPortID == null){
                    logger.debug("WARNING: can't look up required LLDP data: localPortIDsOnSwitches.get().get(), given localSwitchID {} and localPortNum {}", localSwitchID, localPortNum);
                    return false;
                }
                if(checkLocalChassis == null ){
                    logger.debug("WARNING: can't look up required LLDP data: portToRemoteChassisOnSwitches.get().get(), given remoteSwitchID {} and remotePortNum {}", remoteSwitchID, remotePortNum);
                    return false;
                }
                if(checkLocalPortID == null){
                    logger.debug("WARNING: can't look up required LLDP data: portToRemotePortIDOnSwitches.get().get(), given remoteSwitchID {} and remotePortNum {}", remoteSwitchID, remotePortNum);
                    return false;
                }
                if(checkLocalChassis.compareToIgnoreCase(localChassis) != 0
                    || checkLocalPortID.compareToIgnoreCase(localPortID) != 0
                    ){
                    logger.trace("WARNING: the LLDP of remote switch {} port {} directs to switch {} port {}, which is not consistent with local switch {} port {}", remoteSwitchID, remotePortNum, checkLocalChassis, checkLocalPortID, localChassis, localPortID);//lg.dbug-trc
                    return false;
                }

                return true;
    }

    private void readSwLLDP(Node node){
        //TODO
    }

    @Override
    public boolean doTopologyDiscovery(){
        return doTopoDiscovery();
    }

    @Override
    public void setPeriodicTopologyDiscoveryIntervalTime(int interval){
        doTopoDiscovTimeout = (long)interval * 1000;

        logger.info("FAIL: setPeriodicTopologyDiscoveryIntervalTime() is inavailable now!");

        //doTopoDiscovTimerTask.setPeriod(doTopoDiscovTimeout);
        //doTopoDiscovTimer.schedule(doTopoDiscovTimerTask, doTopoDiscovWaitLateTimeout, doTopoDiscovTimeout);
    }

    private void freeLocks(){
        isNotifyCancelTopoDiscov = false;
        isDoingTopologyDiscovery = false;
    }

    @Override
    public void notifyCancelTopologyDiscovery(){//bug fix: without the protection of link-down and Topology Discovery by mutual exclusive, we let Topology Discovery to be canceled if link-down occurs.
        isNotifyCancelTopoDiscov = true;
    }

    public boolean doTopoDiscovery(){//snmp4sdn add

        if(isDoingTopologyDiscovery){
            if(new Date().getTime() - lockTimestamp.getTime() > lockWaitTime * 1000){
                logger.debug("doTopoDiscovery(): existing Topology Discovery task already exists for over {} seconds, so kill it. The incoming task is also rejected, in case of multiple tasks proceed)", lockWaitTime);
                freeLocks();                
            }
            else{
                logger.debug("doTopoDiscovery(): there exists another Topology Discovery task executing, so cancel this one!");
            }
            return false;
        }

        //TODO (memo): For the isXXXXX lock, remember to release them in every 'accidental return' in this function code.
        isNotifyCancelTopoDiscov = false;//bug fix: without the protection of link-down and Topology Discovery by mutual exclusive, we let Topology Discovery to be canceled if link-down occurs.
        isDoingTopologyDiscovery = true;
        lockTimestamp = new Date();

        while(transmitThreadBody.threadVec.size() != 0){
            logger.trace("doTopoDiscovery(): There are still {} threads for processing link-up event running, let's wait them done...", transmitThreadBody.threadVec.size());//lg.dbug-trc
            try{
                Thread.sleep(3000);
            }catch(Exception e1){
                logger.debug("ERROR: doTopoDiscovery(): Thread.sleep() error: {}", e1);
                freeLocks();
                return false;
            }
        }

        logger.debug("");
        logger.debug("doTopoDiscovery(): Topology Discovery starts now!");
        logger.debug("");

        //refresh port state (on/off). (i.e. more specifically, update port state which is maintained in SwitchHandler)
        //(in order to get current correct port state, we give "true" input to inventoryProvider.getNodeConnectorProps(), which will trigger to refresh port state from switch hardware)
        Map<NodeConnector, Map<String, Property>> props = inventoryProvider.getNodeConnectorProps(true);
        if (props == null) {
            logger.debug("ERROR: doTopoDiscovery(): InventoryService.getNodeConnectorProps() fail");
            freeLocks();
            return false;
        }

       //clear previously collected LLDP data
        switches_ID2Chassis.clear();
        switches_Chassis2ID.clear();
        localPortIDsOnSwitches.clear();
        portToRemoteChassisOnSwitches.clear();
        portToRemotePortIDOnSwitches.clear();

        //clear temp LLDP data
        portToRemotePortIDOnSwitchesTmp.clear();

        //get LLDP from switches
        boolean isSuccess = readLLDPonSwitches();
        if(!isSuccess){
            logger.debug("ERROR: doTopoDiscovery(): call readLLDPonSwitches() fail");
            freeLocks();
            return false;
        }

        //clear the discovered edges information, which is just used for resolvePortPairs and updateEdgesAgainstKnown.
        discovEdgeMap.clear();
        discovProdEdgeMap.clear();

        //resolve snmp-snmp edges
        resolvePortPairsAndAddEdges();

        //resolve prod-snmp edges
        resolveProdPortsAndAddEdges();

        //testing code
        //myAddEdgeProdSrc(new String("00:00:00:26:6c:f6:81:b0"), new String("1"), HexString.toLong("90:94:e4:23:0a:e0"), (short)8);//ovs48-switch35
        //myAddEdgeProdSrc(new String("00:00:fa:4b:c1:3e:b1:4a"), new String("1"), HexString.toLong("90:94:e4:23:0a:e0"), (short)8);//ovs18-switch35

        //report upward the edge to be add/remove
        updateEdgesAgainstKnownEdges();
        updateProdEdgesAgainstKnownEdges();

        //Verification test
        //(Just for verification: the "lock" for Topology Disovery against DiscoveryThread is "isDoingTopologyDiscovery", so isDoingTopologyDiscovery is freed, we pause here on purpose)
        if(sim_stayTopologyDiscovery){
            System.out.println();
            System.out.println("The system is set to stay in Topology Discovery, for 10 seconds...");
            System.out.println();
            try{
                Thread.sleep(10000);
            }catch(Exception e1){
                logger.debug("ERROR: doTopoDiscovery(): in the sim_stayTopologyDiscovery loop, Thread.sleep() error: {}", e1);
                freeLocks();
                return false;
            }
        }
        logger.debug("Leave Topology Discovery...");
        //end of Verification test

        freeLocks();

        logger.debug("");
        logger.debug("doTopoDiscovery(): Topology Discovery finish now!");
        logger.debug("");

        return true;
    }

    private boolean readLLDPonOneSwitch(Long switchID){
            if(isFakeSim){//s4s simulation add
                logger.info("readLLDPonOneSwitch(): now is simulation, so not truely reading LLDP data from swtich {} but directly returns", switchID);
                return true;
            }

            SNMPHandler snmp = new SNMPHandler(cmethUtil);
            String localChassis = snmp.getLLDPChassis(switchID);
            if(localChassis == null){
                logger.debug("ERROR: readLLDPonOneSwitch(): call SNMPHandler.getLLDPChassis() fails, given switchID {}", switchID);
                return false;
            }
            switches_ID2Chassis.put(switchID, localChassis);
            switches_Chassis2ID.put(localChassis, switchID);
            logger.trace("###################################################################");
            logger.trace("######### Reading switch (ip: " + HexString.toHexString(switchID) + ")'s, chassis = " + localChassis +" ####");

            Map<Short, String> localPortIDs = snmp.readLLDPLocalPortIDs(switchID);
            if(localPortIDs == null){
                logger.debug("ERROR: readLLDPonOneSwitch(): call SNMPHandler.readLLDPLocalPortIDs() fails, given switchID {}", switchID);
                return false;
            }
            localPortIDsOnSwitches.put(switchID, localPortIDs);
            logger.trace("--> local port id: " + localPortIDs.size() + " entries");
            /*for(Map.Entry<Short, String> entryp: localPortIDs.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " as port id " + entryp.getValue());
            }*/

            Map<Short, String> portToRemoteChassis = snmp.readLLDPAllRemoteChassisID(switchID);
            if(portToRemoteChassis == null){
                logger.debug("ERROR: readLLDPonOneSwitch(): call SNMPHandler.readLLDPAllRemoteChassisID() fails, given switchID {}", switchID);
                return false;
            }
            portToRemoteChassisOnSwitches.put(switchID, portToRemoteChassis);
            logger.trace("--> remote chassis: " + portToRemoteChassis.size() + "entries");
            for(Map.Entry<Short, String> entryp: portToRemoteChassis.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " ==> remote chassis: " + entryp.getValue());
            }

            Map<Short, String> portToRemotePortID = snmp.readLLDPRemotePortIDs(switchID);
            if(portToRemotePortID == null){
                logger.debug("ERROR: readLLDPonOneSwitch(): call SNMPHandler.readLLDPRemotePortIDs() fails, given switchID {}", switchID);
                return false;
            }
            portToRemotePortIDOnSwitches.put(switchID, portToRemotePortID);
            portToRemotePortIDOnSwitchesTmp.put(switchID, portToRemotePortID);
            logger.trace("--> remote port id " + portToRemotePortID.size() + "entries");
            for(Map.Entry<Short, String> entryp: portToRemotePortID.entrySet()){
                logger.trace("\tlocal port " + entryp.getKey() + " ==> remote port id\"" + entryp.getValue() + "\"");
            }

            logger.trace("### end of Reading switch (ip: " + HexString.toHexString(switchID) + ")'s, chassis = " + localChassis +" ##");
            logger.trace("###############################################################");

            return true;
    }

    //snmp4sdn add
    private boolean readLLDPonSwitches(){
        if(controller == null){
            logger.debug("ERROR: readLLDPonSwitches(): IController is null, can't proceed!");
            return false;
        }
        logger.trace("============================================");
        logger.trace("=============== Read LLDP on switches ==============");
        Map<Long, ISwitch> switches = controller.getSwitches();
        for(ISwitch sw : switches.values()){
            //this "for loop"'s body, entirely move to readLLDPonOneSwitch()
            Long switchID = sw.getId();
            boolean val = readLLDPonOneSwitch(switchID);
            if(val == false){
                logger.trace("read node " + switchID + "'s LLDP data error!");
                return false;
            }
        }
        logger.trace("======== end of Read LLDP on switches ========");
        logger.trace("====================================");
        return true;
    }

    //snmp4sdn add
    private void resolvePortPairsAndAddEdges(){//TODO: not yet check null for variables in this function...
        //System.out.println("===========================================");
        //System.out.println("===== Resolve the port pars, and add edges correspondingly =====");
        //System.out.println("number of switches to resolve: " + portToRemoteChassisOnSwitches.size());
        for(Map.Entry<Long, Map<Short, String>> entryS : portToRemoteChassisOnSwitches.entrySet()){
            Long localSwitchID = entryS.getKey();
            String localChassis = switches_ID2Chassis.get(localSwitchID);
            Map<Short, String> localPortIDs = entryS.getValue();
            String localIP = cmethUtil.getIpAddr(localSwitchID);
            logger.trace("  Processing local switch (id: ip = " + localIP + ", chassis: " + localChassis + ", number of ports which connect another remote switch: " + localPortIDs.size() + ")");
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
                    logger.trace("...compare with remote port of id: " + entryR.getValue());
                    Short remotePortNum = entryR.getKey();
                    String remotePortID = localPortIDsOnSwitches.get(remoteSwitchID).get(remotePortNum);
                    String remotePortPointToLocalPortID = entryR.getValue();
                    if(checkBidirectionLLDPConsistent(localSwitchID, localChassis, localPortNum, localPortID, remoteSwitchID, remoteChassis, remotePortNum, remotePortID, remotePortPointToLocalPortID)){

                        //Memo: no worry whether the port on/off state is updated before we call isPortUp(), the port on/off state is updated and saved before edge resolving starts (see in SwitchHandler.handleMessages(), processPortStatusMsg() is called before notifyMessageListener())
                        if(!isPortUp(localSwitchID, localPortNum)){
                            logger.trace("\t\tThough LLDP data on both switches are consistent, but the local port is DOWN, so ignore this edge (local switch {} port {}, remote switch {} port {})", localSwitchID, localPortNum, remoteSwitchID, remotePortNum);
                            break;
                        }
                        if(!isPortUp(remoteSwitchID, remotePortNum)){
                            logger.trace("\t\tThough LLDP data on both switches are consistent, but the remote port is DOWN, so ignore this edge (local switch {} port {}, remote switch {} port {})", localSwitchID, localPortNum, remoteSwitchID, remotePortNum);
                            break;
                        }

                        //Short remotePortNum = entryR.getKey();

                        //report the edge found in this for loop
                        logger.trace("\t\tEdge discovered (A->B): local (ip " + localIP + ", port " + localPortNum + ") --> remote (ip " + remoteIP + ", port " + remotePortNum +")");
                        addDiscoveredEdge(localSwitchID, localPortNum, remoteSwitchID, remotePortNum);

                        //report the edge above but with reverse direction
                        logger.trace("\t\tEdge discovered (B->A): local  (ip " + remoteIP + ", port " + remotePortNum +")--> remote (ip " + localIP + ", port " + localPortNum + ")");
                        addDiscoveredEdge(remoteSwitchID, remotePortNum, localSwitchID, localPortNum);

                        //remove the processed port in tmpMap
                        portToRemotePortIDOnSwitchesTmp.get(localSwitchID).remove(localPortNum);
                        portToRemotePortIDOnSwitchesTmp.get(remoteSwitchID).remove(remotePortNum);

                        break;
                    }
                    else{
                        logger.trace("\t\tLLDP data on Local switch {} port and Remote switch {} port {} are not consistent", localPortID, localPortNum, remotePortID, remotePortNum);
                    }
                }
            }
        }
        //System.out.println("===== end of Resolve the port pars, and add edges correspondingly =====");
        //System.out.println("=================================================");
    }

    private boolean checkBidirectionLLDPConsistent(Long localSwitchID, String localChassis, Short localPortNum, String localPortID, Long remoteSwitchID, String remoteChassis, Short remotePortNum, String remotePortID, String remotePortPointToLocalPortID){
        //check null
        if(localSwitchID == null || localChassis == null || localPortNum == null
            || localPortID == null || remoteSwitchID == null || remoteChassis == null
            || remotePortNum == null || remotePortID == null || remotePortPointToLocalPortID == null){
            logger.debug("ERROR: checkBidirectionLLDPConsistent(): there is null input parameter");
            return false;
        }
        if(portToRemotePortIDOnSwitches.get(localSwitchID) == null || portToRemotePortIDOnSwitches.get(localSwitchID).get(localPortNum) == null){
            logger.trace("ERROR: checkBidirectionLLDPConsistent(): portToRemotePortIDOnSwitches has null content given localSwitchID {} and localPortNum {}", localSwitchID, localPortNum);//lg.dbug-trc
            return false;
        }
        if(portToRemoteChassisOnSwitches.get(remoteSwitchID) == null || portToRemoteChassisOnSwitches.get(remoteSwitchID).get(remotePortNum) == null){
            logger.trace("ERROR: checkBidirectionLLDPConsistent(): portToRemoteChassisOnSwitches has null content given remoteSwitchID {} and remotePortNum {}", remoteSwitchID, remotePortNum);//lg.dbug-trc
            return false;
        }
        if(portToRemoteChassisOnSwitches.get(localSwitchID) == null || portToRemoteChassisOnSwitches.get(localSwitchID).get(localPortNum) == null){
            logger.trace("ERROR: checkBidirectionLLDPConsistent(): portToRemoteChassisOnSwitches has null content given localSwitchID {} and localPortNum {}", localSwitchID, localPortNum);//lg.dbug-trc
            return false;
        }

        //Bidirectional LLDP consistency checking
        if(remotePortPointToLocalPortID.compareToIgnoreCase(localPortID) == 0
            && portToRemotePortIDOnSwitches.get(localSwitchID).get(localPortNum).compareToIgnoreCase(remotePortID) == 0
            && portToRemoteChassisOnSwitches.get(remoteSwitchID).get(remotePortNum).compareToIgnoreCase(localChassis) == 0
            && portToRemoteChassisOnSwitches.get(localSwitchID).get(localPortNum).compareToIgnoreCase(remoteChassis) == 0)
            return true;
        else
            return false;
    }

    private boolean isPortUp(Long switchID, Short portNum){
        Node node = null;
        NodeConnector nodeConnector = null;
        try{
            node = new Node("SNMP", switchID);
            nodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", portNum, node);
        }catch (ConstructionException e1) {
            logger.error("ERROR: isPortUp(): create SNMP Node and NodeConnector, given switchID {} portNum {}, error: {}", switchID, portNum, e1);
            return false;
        }

        return isEnabled(nodeConnector);
    }

    //s4s add
    private void myAddEdge(Long srcSwitchID, Short srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node("SNMP", srcSwitchID);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", srcPortNum, srcNode);

            //create dest node and nodeconnector
            Node destNode = new Node("SNMP", destSwitchID);
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", destPortNum, destNode);

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

    private void addDiscoveredEdge(Long srcSwitchID, Short srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node("SNMP", srcSwitchID);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", srcPortNum, srcNode);

            //create dest node and nodeconnector
            Node destNode = new Node("SNMP", destSwitchID);
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", destPortNum, destNode);

            //create the edge connecting the source/dest nodeconnectors
            Edge edge = null;
            Set<Property> props = null;
            edge = new Edge(srcNodeConnector, destNodeConnector);
            props = getProps(destNodeConnector);

            discovEdgeMap.put(edge, props);
        }
        catch (ConstructionException e1) {
            freeLocks();//addDiscoveredEdge() would be gone though during doTopoDiscovery(). During doTopoDiscovery(), freeLocks() needs to be called in any exception case. So we add freeLocks() here.
            logger.error("addDiscoveredEdge(): Caught exception: {}", e1);
        }
    }

    /*
    * Hybrid Link issue fix
    *
    * Author: Christine
    * Date: 2015/4/17
    * Description: To resolve PR->SNMP edges and report them.
    * Code modification: for this issue fix, we add the following code, and also add code to doTopoDiscovery() and readLLDPonOneSwitch()
    *
    */
    private void resolveProdPortsAndAddEdges(){//TODO: not yet check null for variables in this function...
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

                //Memo: no worry whether the port on/off state is updated before we call isPortUp(), the port on/off state is updated and saved before edge resolving starts (see in SwitchHandler.handleMessages(), processPortStatusMsg() is called before notifyMessageListener())
                if(!isPortUp(localSwitchID, localPortNum)){
                    logger.trace("\t\tThe local port is DOWN, so ignore this edge (local switch {} port {}, remote switch {} port {})", localSwitchID, localPortNum, remoteSwitchID, remotePortID);
                    break;
                }

                logger.trace("\t\tPR edge discovered: remote (ip " + remoteSwitchID + ", portID " + remotePortID +") --> local (ip " + localSwitchID + ", port " + localPortNum + ")");
                addDiscoveredEdgeProdSrc(remoteSwitchID, remotePortID, localSwitchID, localPortNum);
            }
        }
        //System.out.println("===== end of Resolve the PR ports, and add edges correspondingly =====");
        //System.out.println("=================================================");
    }

    private void myAddEdgeProdSrc(String srcSwitchID, String srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node(NodeConnector.NodeConnectorIDType.PRODUCTION, srcSwitchID);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    NodeConnector.NodeConnectorIDType.PRODUCTION, srcPortNum, srcNode);

            //create dest node and nodeconnector
            Node destNode = new Node("SNMP", destSwitchID);
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", destPortNum, destNode);

            //create the edge connecting the source/dest nodeconnectors
            Edge edge = null;
            Set<Property> props = new HashSet<Property>();
            edge = new Edge(srcNodeConnector, destNodeConnector);
            props = getProps(destNodeConnector);

            //addEdge(edge, props);//<--in old code, prodMap was not used, we directly call addEdge()
            updateProdEdge(edge, props);
        }
        catch (ConstructionException e) {
            logger.error("Caught exception ", e);
        }
    }

    private void addDiscoveredEdgeProdSrc(String srcSwitchID, String srcPortNum, Long destSwitchID, Short destPortNum){
        try {
            //create source node and nodeconnector
            Node srcNode = new Node(NodeConnector.NodeConnectorIDType.PRODUCTION, srcSwitchID);
            NodeConnector srcNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    NodeConnector.NodeConnectorIDType.PRODUCTION, srcPortNum, srcNode);

            //create dest node and nodeconnector
            Node destNode = new Node("SNMP", destSwitchID);
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", destPortNum, destNode);

            //create the edge connecting the source/dest nodeconnectors
            Edge edge = null;
            Set<Property> props = new HashSet<Property>();
            edge = new Edge(srcNodeConnector, destNodeConnector);
            props = getProps(destNodeConnector);

            discovProdEdgeMap.put(edge, props);
        }
        catch (ConstructionException e1) {
            freeLocks();//addDiscoveredEdgeProdSrc() would be gone though during doTopoDiscovery(). During doTopoDiscovery(), freeLocks() needs to be called in any exception case. So we add freeLocks() here.
            logger.error("addDiscoveredEdgeProdSrc(): Caught exception: {}", e1);
        }
    }

    private void updateEdgesAgainstKnownEdges(){
        //Algorithm:
        //(1) Clone the Edges in discovEdgeMap and edgeMap, for later processing, as dSet and kSet (kSet means 'known' Set)
        //(2) Remove dupicate Edges in dSet and kSet.
        //(3) After (3), naturally, the remaining dSet is the edges to ADD, the remaining kSet is the edges to REMOVE

        //(1)
        //dSet: clone discovEdgeMap, then for step (2)(3) use
        Set<Edge> dSet = new HashSet<Edge>();
        for(Edge dEntry : discovEdgeMap.keySet())
            dSet.add(dEntry);

        //kSet: the known edgeMap, then for step (2)(3) use
        Set<Edge> kSet = new HashSet<Edge>();
        for(Edge kEntry : edgeMap.values())
            kSet.add(kEntry);


        //(2)
        //(NOTICE for coding: we can't use code for the following 'for loop', like "for(Edge dEge : dSet.keySet())", it will occur ConcurrentModificationException! So instead, we have to use discovEdgeMap. For the same reason, we use "for(Edge kEdge : edgeMap.values())" for the next 'for loop')
        /*for(Edge dEdge : discovEdgeMap.keySet()){
            if(kSet.contains(dEdge)){
                dSet.remove(dEdge);
                kSet.remove(dEdge);
                logger.trace("updateEdgesAgainstKnownEdges(): an edge {} in discovEdges is also in knownEdges, so ignore it in edge update", dEdge);
            }
        }*///TODO: may remove this code? It's fine that do duplication checking only one way?
        for(Edge kEdge : edgeMap.values()){
            if(dSet.contains(kEdge)){
                kSet.remove(kEdge);
                dSet.remove(kEdge);
                logger.trace("updateEdgesAgainstKnownEdges(): an edge {} in knownEdges is also in discovEdges, so ignore it in edge update", kEdge);
            }
        }

        //(3)
        for(Edge dEdge : dSet){
            logger.debug("");
            logger.debug("updateEdgesAgainstKnownEdges(): find an edge {} in discovEdges but not in knownEdges , so add it", dEdge);
            logger.debug("");
            if(isNotifyCancelTopoDiscov){//note: why put this flag 'isNotifyCancelTopoDiscov' is enough for protection? ==> so far, for the "edgeMap and prodMap" which is used for update comparison, we've finish the comparison, will not use them later. So, as long as "isNotifyCancelTopoDiscov is false", it means they are not yet polluted by link-down event, it implies that the link-down edge will "not" be reported upward by Topology Discovery.
                logger.info("updateEdgesAgainstKnownEdges(): addEdge() for Edge {} is canceled due to the flag 'isNotifyCancelTopoDiscov' is on (usuaully due to a Link-down Event just occurs)", dEdge);
                break;
            }
            addEdge(dEdge, discovEdgeMap.get(dEdge));
        }
        for(Edge kEdge : kSet){
            logger.debug("");
            logger.debug("updateEdgesAgainstKnownEdges(): find an edge {} in knownEdges but not in discovEdges, so remove it", kEdge);
            logger.debug("");
            if(isNotifyCancelTopoDiscov){//TODO: this protection can protect what kind of special case?
                logger.info("updateEdgesAgainstKnownEdges(): removeEdge() for Edge {} is canceled the flag 'isNotifyCancelTopoDiscov' is on", kEdge);
                break;
            }
            removeEdge(kEdge.getHeadNodeConnector());
        }
    }

    private void updateProdEdgesAgainstKnownEdges(){
        //Algorithm:
        //(1) Clone the Edges in discovProdEdgeMap and prodMap, for later processing, as dSet and kSet (kSet means 'known' Set)
        //(2) For duplicate edges in dSet and kSet, remove the dupicate Edges in kSet, but don't remove the duplicate edges in dSet.
        //(3) After (3), naturally, the remaining dSet is the edges to ADD, the remaining kSet is the edges to REMOVE
        //(just detail explanation if interested in: in (2) don't remove PR edges in dSet, because we'd like to allow PR edge to be reported again and again, for race condition when OF plugin and snmp4sdn plugin start up)

        //(1)
        //dSet: clone discovProdEdgeMap, then for step (2)(3) use
        Set<Edge> dSet = new HashSet<Edge>();
        for(Edge dEntry : discovProdEdgeMap.keySet())
            dSet.add(dEntry);

        //kSet: the known prodMap, then for step (2)(3) use
        Set<Edge> kSet = new HashSet<Edge>();
        for(Edge kEntry : prodMap.values())
            kSet.add(kEntry);


        //(2)
        //(NOTICE for coding: we can't use code for the following 'for loop', like "for(Edge dEge : dSet.keySet())", it will occur ConcurrentModificationException! So instead, we have to use discovProdEdgeMap. For the same reason, we use "for(Edge kEdge : prodMap.values())" for the next 'for loop')
        /*for(Edge dEdge : discovProdEdgeMap.keySet()){
            if(kSet.contains(dEdge)){
                //dSet.remove(dEdge);//we don't remove the duplicate edge in dSet, because we'd like to allow PR edge to be reported again and again.
                kSet.remove(kEdge);
                logger.trace("updateProdEdgesAgainstKnownEdges(): an edge {} in discovProdEdges is also in knownProdEdges, so ignore it in edge update", dEdge);
            }
        }*///TODO: may remove this code? It's fine that do duplication checking only one way?
        for(Edge kEdge : prodMap.values()){
            if(dSet.contains(kEdge)){
                kSet.remove(kEdge);
                //dSet.remove(dEdge);//we don't remove the duplicate edge in dSet, because we'd like to allow PR edge to be reported again and again.
                logger.trace("updateProdEdgesAgainstKnownEdges(): an edge {} in knownProdEdges is also in discovProdEdges, so ignore it in edge update", kEdge);
            }
        }

        //(3)
        for(Edge dEdge : dSet){
            logger.trace("");//lg.dbug-trc
            logger.trace("updateProdEdgesAgainstKnownEdges(): find an edge {} in discovProdEdges but not in knownProdEdges , so add it", dEdge);//lg.dbug-trc
            logger.trace("");//lg.dbug-trc
            if(isNotifyCancelTopoDiscov){//note: why put this flag 'isNotifyCancelTopoDiscov' is enough for protection? ==> so far, for the "edgeMap and prodMap" which is used for update comparison, we've finish the comparison, will not use them later. So, as long as "isNotifyCancelTopoDiscov is false", it means they are not yet polluted by link-down event, it implies that the link-down edge will "not" be reported upward by Topology Discovery.
                logger.trace("updateProdEdgesAgainstKnownEdges(): addEdge() for Edge {} is canceled due to the flag 'isNotifyCancelTopoDiscov' is on (usuaully due to a Link-down Event just occurs)", dEdge);//lg.dbug-trc
                break;
            }
            addEdge(dEdge, discovProdEdgeMap.get(dEdge));
        }
        for(Edge kEdge : kSet){
            logger.trace("");//lg.dbug-trc
            logger.trace("updateProdEdgesAgainstKnownEdges(): find an edge {} in knownProdEdges but not in discovProdEdges, so remove it", kEdge);//lg.dbug-trc
            logger.trace("");//lg.dbug-trc
            if(isNotifyCancelTopoDiscov){//TODO: this protection can protect what kind of special case?
                logger.trace("updateProdEdgesAgainstKnownEdges(): removeEdge() for Edge {} is canceled due to the flag 'isNotifyCancelTopoDiscov' is on", kEdge);//lg.dbug-trc
                break;
            }
            removeProdEdge(kEdge.getHeadNodeConnector());
        }
    }


    public void disableNewInventoryTriggerDiscovery(){
        this.isEnableNewInventoryTriggerDiscovery = false;
        logger.debug("SNMP4SDN: now new inventory would not trigger edge discovery!");
    }

    public void enableNewInventoryTriggerDiscovery(){
        this.isEnableNewInventoryTriggerDiscovery = true;
        logger.debug("SNMP4SDN: now new inventory would trigger edge discovery!");
    }

    private void print_switches_ID2Chassis(){
        System.out.println("------ switches_ID2Chassis -------");
        System.out.println("Switch ID\t\tChassis");
        for (Map.Entry<Long, String> entry : switches_ID2Chassis.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
        System.out.println();
    }

    private void print_switches_Chassis2ID(){
        System.out.println("------ switches_ID2Chassis -------");
        System.out.println("Chassis\t\tSwitch ID");
        for (Map.Entry<String, Long> entry : switches_Chassis2ID.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
        System.out.println();
    }

    private void print_localPortIDsOnSwitches(){
        System.out.println("------ localPortIDsOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : localPortIDsOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("local port " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }
    private void print_portToRemoteChassisOnSwitches(){
        System.out.println("------ portToRemoteChassisOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemoteChassisOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote chassis of port " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }
    private void print_portToRemotePortIDOnSwitches(){
        System.out.println("------ portToRemotePortIDOnSwitches -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemotePortIDOnSwitches.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote port ID of port" + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }

    private void print_portToRemotePortIDOnSwitchesTmp(){
        System.out.println("------ portToRemotePortIDOnSwitchesTmp -------");
        for (Map.Entry<Long, Map<Short, String>> entry1 : portToRemotePortIDOnSwitchesTmp.entrySet()) {
            System.out.println("Switch " + entry1.getKey());
            for (Map.Entry<Short, String> entry2 : entry1.getValue().entrySet()){
                System.out.println("remote port ID " + entry2.getKey() + ": " + entry2.getValue());
            }
        }
        System.out.println();
    }

    private void print_edgeMap(){
        System.out.println("------ edgeMap -------");
        for (Map.Entry<NodeConnector, Edge> entry : edgeMap.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
        System.out.println();
    }

    private void print_prodMap(){
        System.out.println("------ prodMap -------");
        for (Map.Entry<NodeConnector, Edge> entry : prodMap.entrySet()) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
        System.out.println();
    }

    public void _s4sResetTopologyDiscoverLock(CommandInterpreter ci){
        isDoingTopologyDiscovery = false;
        System.out.println();
        System.out.println("Topology Discover lock is free now");
        System.out.println();
    }

    public void _s4sPeriodicTopologyDiscover_on(CommandInterpreter ci){
        isPeriodicTopoDiscov = true;
        System.out.println();
        System.out.println("Periodic Topology Discover is on now");
        System.out.println();
    }
    public void _s4sPeriodicTopologyDiscover_off(CommandInterpreter ci){
        isPeriodicTopoDiscov = false;
        System.out.println();
        System.out.println("Periodic Topology Discover is off now");
        System.out.println();
    }

    public void _s4sDiscoverService_addEdge(CommandInterpreter ci){
        String sw1Str = ci.nextArgument();
        String port1Str = ci.nextArgument();
        String sw2Str = ci.nextArgument();
        String port2Str = ci.nextArgument();
        String gargage = ci.nextArgument();

        if(sw1Str == null || port1Str == null || sw2Str == null || port2Str == null && gargage != null){
            System.out.println("Please use: s4sDiscoverService_addEdge <switch1> <port1> <switch2> <port2>  (as SNMP->SNMP link: sw1->sw2)");
            return;
        }

        if(edgeMap == null){
            System.out.println("Local manintained Edge Map is null, can't proceed!");
            return;
        }

        long sw1ID = -1;
        try{
            if(sw1Str.indexOf(":") < 0)
                sw1ID = Long.parseLong(sw1Str);
            else
                sw1ID = HexString.toLong(sw1Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw1Str + " to long value error: " + e1);
            return;
        }

        long sw2ID = -1;
        try{
            if(sw2Str.indexOf(":") < 0)
                sw2ID = Long.parseLong(sw2Str);
            else
                sw2ID = HexString.toLong(sw2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw2Str + " to long value error: " + e1);
            return;
        }

        short port1 = -1;
        try{
            port1 = Short.parseShort(port1Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port1Str + " to int value error: " + e1);
            return;
        }

        short port2 = -1;
        try{
            port2 = Short.parseShort(port2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port2Str + " to int value error: " + e1);
            return;
        }

        myAddEdge(new Long(sw1ID), new Short(port1), new Long(sw2ID), new Short(port2));

        System.out.println();
        System.out.println("Edge " +
            "(SNMP|" +port1 + "@" + "SNMP|" + sw1ID + "->" + "SNMP|" +port2 + "@" + "SNMP|" + sw2ID + ")" +
            " is added (it has been put into the local edge map and also reported to SAL)");
        System.out.println();
    }

    public void _s4sDiscoverService_delEdge(CommandInterpreter ci){
        String sw1Str = ci.nextArgument();
        String port1Str = ci.nextArgument();
        String sw2Str = ci.nextArgument();
        String port2Str = ci.nextArgument();
        String gargage = ci.nextArgument();

        if(sw1Str == null || port1Str == null || sw2Str == null || port2Str == null && gargage != null){
            System.out.println("Please use: s4sDiscoverService_delEdge <switch1> <port1> <switch2> <port2>  (as SNMP->SNMP link: sw1->sw2)");
            return;
        }

        if(edgeMap == null){
            System.out.println("Local manintained Edge Map is null, can't proceed!");
            return;
        }

        long sw1ID = -1;
        try{
            if(sw1Str.indexOf(":") < 0)
                sw1ID = Long.parseLong(sw1Str);
            else
                sw1ID = HexString.toLong(sw1Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw1Str + " to long value error: " + e1);
            return;
        }

        long sw2ID = -1;
        try{
            if(sw2Str.indexOf(":") < 0)
                sw2ID = Long.parseLong(sw2Str);
            else
                sw2ID = HexString.toLong(sw2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw2Str + " to long value error: " + e1);
            return;
        }

        short port1 = -1;
        try{
            port1 = Short.parseShort(port1Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port1Str + " to int value error: " + e1);
            return;
        }

        short port2 = -1;
        try{
            port2 = Short.parseShort(port2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port2Str + " to int value error: " + e1);
            return;
        }

        try {
            Node destNode = new Node("SNMP", new Long(sw2ID));
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", new Short(port2), destNode);
            removeEdge(destNodeConnector);
        }
        catch (Exception e1) {
            ci.println("ERROR: " + e1);
            return;
        }

        System.out.println();
        System.out.println("Edge " +
            "(SNMP|" +port1 + "@" + "SNMP|" + sw1ID + "->" + "SNMP|" +port2 + "@" + "SNMP|" + sw2ID + ")" +
            " is removed (it has been removed from the local edge map and also reported to SAL)");
        System.out.println();
    }

    public void _s4sDiscoverService_addPrEdge(CommandInterpreter ci){
        String sw1Str = ci.nextArgument();
        String port1Str = ci.nextArgument();
        String sw2Str = ci.nextArgument();
        String port2Str = ci.nextArgument();
        String gargage = ci.nextArgument();

        if(sw1Str == null || port1Str == null || sw2Str == null || port2Str == null && gargage != null){
            System.out.println("Please use: s4sDiscoverService_addPrEdge <switch1> <port1> <switch2> <port2>  (as PR->SNMP ink: sw1->sw2)");
            return;
        }

        if(edgeMap == null){
            System.out.println("Local manintained Edge Map is null, can't proceed!");
            return;
        }

        long sw2ID = -1;
        try{
            if(sw2Str.indexOf(":") < 0)
                sw2ID = Long.parseLong(sw2Str);
            else
                sw2ID = HexString.toLong(sw2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw2Str + " to long value error: " + e1);
            return;
        }

        short port2 = -1;
        try{
            port2 = Short.parseShort(port2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port2Str + " to int value error: " + e1);
            return;
        }

        myAddEdgeProdSrc(sw1Str, port1Str, new Long(sw2ID), new Short(port2));

        System.out.println();
        System.out.println("Edge " +
            "(PR|" +port1Str + "@" + "PR|" + sw1Str + "->" + "SNMP|" +port2 + "@" + "SNMP|" + sw2ID + ")" +
            " is added (it has been put into the local edge map and also reported to SAL)");
        System.out.println();
    }

    public void _s4sDiscoverService_delPrEdge(CommandInterpreter ci){
        //MEMO: actually the src switch and src port are useless, because the removeEdge() needs only destNodeConnector

        String sw2Str = ci.nextArgument();
        String port2Str = ci.nextArgument();
        String gargage = ci.nextArgument();

        if(sw2Str == null || port2Str == null && gargage != null){
            System.out.println("Please use: s4sDiscoverService_delPrEdge <switch2> <port2>  (as PR->SNMP link: sw1->sw2, sw1 could be ignored)");
            return;
        }

        if(edgeMap == null){
            System.out.println("Local manintained Edge Map is null, can't proceed!");
            return;
        }

        long sw2ID = -1;
        try{
            if(sw2Str.indexOf(":") < 0)
                sw2ID = Long.parseLong(sw2Str);
            else
                sw2ID = HexString.toLong(sw2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + sw2Str + " to long value error: " + e1);
            return;
        }

        short port2 = -1;
        try{
            port2 = Short.parseShort(port2Str);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + port2Str + " to int value error: " + e1);
            return;
        }

        try {
            Node destNode = new Node("SNMP", new Long(sw2ID));
            NodeConnector destNodeConnector = NodeConnectorCreator.createNodeConnector(
                                                                    "SNMP", new Short(port2), destNode);
            removeEdge(destNodeConnector);
        }
        catch (Exception e1) {
            ci.println("ERROR: " + e1);
            return;
        }

        System.out.println();
        System.out.println("Edge " +
            "(Remote ->" + "SNMP|" +port2 + "@" + "SNMP|" + sw2ID + ")" +
            " is removed (it has been removed from the local edge map and also reported to SAL)");
        System.out.println();
    }

    //VerificationTest is for verifying Topology Discovery and Link-up event handling are mutual exclusive.
        //The sim_stayTopologyDiscovery is for even Topology Discovery is finished but the program still stay in the function so that we have time to manually trigger link-up link and see the link-up event is halt until Topology Discovery really ends.
        //The sim_stayDiscoveryThread is for even all the running DiscoveryThreads are finished but the program still stay in the last DiscoveryThread so that we have time to observe Topology Discovery until DiscoveryThreads really ends.
    public void _s4sVerificationTest_showLocks(CommandInterpreter ci){
        System.out.println();

        if(sim_stayTopologyDiscovery)
            System.out.println("stayTopologyDiscovery is on now ==> Link-up event can't proceed now. (i.e. DiscoveryTimerTask would be halt, DiscoveryTimerTask is to process link-up event queue)");
        else
            System.out.println("stayTopologyDiscovery is off now (i.e. Link-up event can proceed as usual)");
        if(sim_stayDiscoveryThread)
            System.out.println("stayDiscoveryThread is on now ==> Topology Discovery can't proceed now. (i.e. can't enter doTopoDiscovery(), doTopoDiscovery is the Topology Discovery function)");
        else
            System.out.println("stayDiscoveryThread is off now (i.e. Topology Discovery can proceed as usual)");

        System.out.println();
    }
    public void _s4sVerificationTest_stayTopologyDiscovery_on(CommandInterpreter ci){
        sim_stayTopologyDiscovery = true;
        System.out.println();
        System.out.println("stayTopologyDiscovery is on now");
        System.out.println();
    }
    public void _s4sVerificationTest_stayTopologyDiscovery_off(CommandInterpreter ci){
        //TODO: if sim_stayTopologyDiscovery is set as true, and the program enters doTopoDiscovery() the loop waiting for sim_stayTopologyDiscovery to be false, OSGi console seems to can't allow user to execute CLI command, so this 's4sVerificationTest_stayTopologyDiscovery_off' CLI command is inavailable!
        sim_stayTopologyDiscovery = false;
        System.out.println();
        System.out.println("stayTopologyDiscovery is off now");
        System.out.println();
    }
    public void _s4sVerificationTest_stayDiscoveryThread_on(CommandInterpreter ci){
        sim_stayDiscoveryThread = true;
        System.out.println();
        System.out.println("stayDiscoveryThread is on now");
        System.out.println();
    }
    public void _s4sVerificationTest_stayDiscoveryThread_off(CommandInterpreter ci){
        //TODO: if sim_stayTopologyDiscovery is set as true, and the program enters DiscoveryThread.run() the loop waiting for sim_stayDiscoveryThread to be false, OSGi console seems to can't allow user to execute CLI command, so this 's4sVerificationTest_stayDiscoveryThread_off' CLI command is inavailable!
        sim_stayDiscoveryThread = false;
        System.out.println();
        System.out.println("stayDiscoveryThread is off now");
        System.out.println();
    }

    public void _s4sPrintDiscoveryServiceData(CommandInterpreter ci){

        //LLDP data
        print_switches_ID2Chassis();
        print_switches_Chassis2ID();
        print_localPortIDsOnSwitches();
        print_portToRemoteChassisOnSwitches();
        print_portToRemotePortIDOnSwitches();
        print_portToRemotePortIDOnSwitchesTmp();
    }

    public void _s4sPrintDiscoveryServiceEdgeData(CommandInterpreter ci){

        //edges
        print_edgeMap();
        print_prodMap();
    }

    public void _s4sPrintUpdatedPortPropsFromInventoryService(CommandInterpreter ci){
        Map<NodeConnector, Map<String, Property>> props = inventoryProvider.getNodeConnectorProps(true);
        if (props == null) {
            logger.debug("ERROR: InventoryService.getNodeConnectorProps() fail");
            return;
        }

        System.out.println("NodeConnector" + "\t" + "\t" + "\t" + /*"Property" + "\t" + */"Value");
        System.out.println("----------------------------------------------------");
        for(Map.Entry<NodeConnector, Map<String, Property>> entry1 : props.entrySet()){
            NodeConnector nc = entry1.getKey();
            for(Map.Entry<String, Property> entry2 : entry1.getValue().entrySet()){
                System.out.println(nc + "\t" + /*entry2.getKey() + "\t" + */entry2.getValue());
            }
        }
    }

    public void _s4sPrintUpdatedPortStateFromInventoryService(CommandInterpreter ci){
        Map<NodeConnector, Map<String, Property>> props = inventoryProvider.getNodeConnectorProps(true);
        if (props == null) {
            logger.debug("ERROR: InventoryService.getNodeConnectorProps() fail");
            return;
        }

        System.out.println("NodeConnector" + "\t" + "\t" + "\t" + "State");
        System.out.println("----------------------------------------------------");
        for(Map.Entry<NodeConnector, Map<String, Property>> entry1 : props.entrySet()){
            NodeConnector nc = entry1.getKey();
            for(Map.Entry<String, Property> entry2 : entry1.getValue().entrySet()){
                Property prop = entry2.getValue();
                if(prop.getName().equals(Config.ConfigPropName))
                    System.out.println(nc + "\t" + (Config)prop);
            }
        }
    }

    public void _s4sAddLLDPData(CommandInterpreter ci){
        String swStr = ci.nextArgument();
        String portStr = ci.nextArgument();
        String isEth = ci.nextArgument();
        String remoteChassisStr = ci.nextArgument();
        String remotePortID = ci.nextArgument();
        String gargage = ci.nextArgument();

        if(swStr == null || portStr == null || isEth == null || remoteChassisStr == null || remotePortID == null && gargage != null){
            System.out.println("Please use: s4sAddLLDPData <switch> <port> <eth_or_nonEth> <remote_switch> <remote_port>");
            return;
        }

        if(portToRemoteChassisOnSwitches == null){
            System.out.println("LLDP data 'portToRemoteChassisOnSwitches' is null, can't proceed!");
            return;
        }
        if(portToRemotePortIDOnSwitches == null){
            System.out.println("LLDP data 'portToRemotePortIDOnSwitches' is null, can't proceed!");
            return;
        }

        Long swID = new Long(Long.parseLong(swStr));
        Short port  = new Short(Short.parseShort(portStr));
        String localChassis;
        String remoteChassis;
        if(swStr.indexOf(":") >= 0)//use input remoteChassisStr in hex format
            localChassis = HexEncode.longToHexString(HexEncode.stringToLong(swStr));
        else
            localChassis = HexEncode.longToHexString(Long.parseLong(swStr));
        if(remoteChassisStr.indexOf(":") >= 0)//use input remoteChassisStr in hex format
            remoteChassis = HexEncode.longToHexString(HexEncode.stringToLong(remoteChassisStr));
        else
            remoteChassis = HexEncode.longToHexString(Long.parseLong(remoteChassisStr));

        switches_ID2Chassis.put(swID, localChassis);
        switches_Chassis2ID.put(localChassis, swID);
        System.out.println("Added LLDP data of chassis-to-swID mapping (the local switch): chassis " + localChassis + " as switch ID " + swID);

        if(isEth.compareToIgnoreCase("eth") == 0){
            switches_ID2Chassis.put(HexEncode.stringToLong(remoteChassis), remoteChassis);
            switches_Chassis2ID.put(remoteChassis, HexEncode.stringToLong(remoteChassis));
            System.out.println("Added LLDP data of chassis-to-swID mapping (the remote switch): chassis " + remoteChassis + " as switch ID " + HexEncode.stringToLong(remoteChassis));
        }
        else if(isEth.compareToIgnoreCase("nonEth") == 0){
        }
        else{
            System.out.println("The value for <eth_or_nonEth> should be 'eth' or 'nonEth'");
            System.out.println("Please use: s4sAddLLDPData <switch> <port> <eth_or_nonEth> <remote_switch> <remote_port>");
            return;
        }

        Map<Short, String> localPortIDs = localPortIDsOnSwitches.get(swID);
        if(localPortIDs == null) localPortIDs = new HashMap<Short, String>();
        localPortIDs.put(port, port.toString());
        localPortIDsOnSwitches.put(swID, localPortIDs);
        System.out.println("Added local port LLDP data: Sw " + swID + " port " + port);

        Map<Short, String> portToRemoteChassis = portToRemoteChassisOnSwitches.get(swID);
        if(portToRemoteChassis == null) portToRemoteChassis = new HashMap<Short, String>();
        portToRemoteChassis.put(port, remoteChassis);
        portToRemoteChassisOnSwitches.put(swID, portToRemoteChassis);
        System.out.println("Added LLDP data: Sw " + swID + " port " + port + " -> remote chassis " + remoteChassis);

        Map<Short, String> portToRemotePortIDs = portToRemotePortIDOnSwitches.get(swID);
        if(portToRemotePortIDs == null) portToRemotePortIDs = new HashMap<Short, String>();
        portToRemotePortIDs.put(port, remotePortID);
        portToRemotePortIDOnSwitches.put(swID, portToRemotePortIDs);
        System.out.println("Added LLDP data: Sw " + swID + " port " + port + " -> remote port ID " + remotePortID);
    }

    public void _s4sSetTimeout(CommandInterpreter ci){
        String cancelTimeout = ci.nextArgument();
        String retryTimeout =  ci.nextArgument();
        String garbage =  ci.nextArgument();

        if(cancelTimeout == null || retryTimeout == null || garbage != null){
            System.out.println("Please use: s4sSetTimeout <cancel_timeout(sec)> <retry_timeout(sec)>");
            return;
        }

        pendingTimeoutTicks = (int)(Double.parseDouble(cancelTimeout) * 1000 / (double)discoveryTimerTick);
        retryTimeoutTicks = (int)(Double.parseDouble(retryTimeout) * 1000 / (double)discoveryTimerTick);

        System.out.println("Cancel timeout = " + pendingTimeoutTicks + " ticks");
        System.out.println("Retry timeout = " + retryTimeoutTicks + " ticks");
        System.out.println("(per tick: " + (discoveryTimerTick/1000) + "sec)");

    }

    public void _s4sClearLinkUpPending(CommandInterpreter ci){
        linkupPendingMap.clear();
        System.out.println();
        System.out.println("Clear link-up pending list, so the edge resolving retry will stop!");
        System.out.println();
    }

    /************
    * The following code are for simulation!!
    ************/

    /* Testing scenario as follows:
      * setFakeSwitchAndPortNumber(): 4 parameters for user to specify, let user specify how many Eth switches and port per switch, for two kinds of switch groups (Eth-to-Eth / OF-to-Eth)
      *    Note: the Eth switches in Eth-to-Eth group and OF-to-Eth group are independent! (No edge between them)
      * createFakeSNMPEdges(): In Eth-to-Eth group, all ports are connected matually. Sw1's all ports are assign to other switches in round robin,  and then Sw2's all ports, Sw3's all ports...
      * createFakeOFtoSNMPEdges(): In OF-to-Eth group, OF Sw1's all ports are assign to Eth switches in round robin, and then OF Sw2's all ports, Sw3's all ports...
    */

    //s4s simulation
    public void _s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService(CommandInterpreter ci){
        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        String swNum = ci.nextArgument();
        String portNum = ci.nextArgument();
        String swNum2 = ci.nextArgument();//the switches with OF-to-SNMP edge
        String portNum2 = ci.nextArgument();//the number of port on a switch with OF-to-SNMP edge
        String garbage = ci.nextArgument();

        if(swNum == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(portNum == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(swNum2 == null){
            logger.debug("ERROR: the number of switches with OF-to-SNMP edge is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(portNum2 == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(garbage != null){
            logger.debug("Please use: s4sStressTest_setFakeSwitchAndPortNumber_DiscoveryService <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }

        fakeSwNum = Integer.parseInt(swNum);
        fakeSwPortNum = Short.parseShort(portNum);
        fakeSwNum2 = Integer.parseInt(swNum2);
        fakeSwPortNum2 = Short.parseShort(portNum2);

        System.out.println("DiscoveryService: fakeSwNum is set as " + fakeSwNum);
        System.out.println("DiscoveryService: fakeSwPortNum is set as " + fakeSwPortNum);
        System.out.println("DiscoveryService: fakeSwNum2 (OF-to-SNMP) is set as " + fakeSwNum2);
        System.out.println("DiscoveryService: fakeSwPortNum2 (OF-to-SNMP) is set as " + fakeSwPortNum2);
    }

    //s4s simulation
    public void _s4sStressTest_createFakeSNMPEdges(CommandInterpreter ci){
        logger.debug("==========================");
        logger.debug("Creating fake SNMP edges...");
        logger.debug("----------------------------------");

        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        LinkedList<Short>[] swVec = createFakeSwAndPortsListForAssign();
        if(swVec == null){System.out.println("createFakeSwAndPortsListForAssign() fail!");return;}

        int sw_j = 1;
        for(int sw_i = 0; sw_i < fakeSwNum; sw_i++){
            //System.out.println("layer 1: sw_i="+sw_i+", sw_j="+sw_j);
            LinkedList<Short> swVec_i = swVec[sw_i];
            int port_i_inc = 1;
            while(swVec_i.size() >= 0){
                //System.out.println("layer 2 begin: sw_i="+sw_i+", sw_j="+sw_j+", swVec_i.size()="+swVec_i.size());
                if(sw_j == sw_i) {
                    sw_j += 1;
                    sw_j %= fakeSwNum;
                    //System.out.println("sw_j == sw_i ="+sw_i+", so sw_j++ as "+sw_j+", then continue");continue;
                }

                if(swVec_i.peek() == null) {
                    //System.out.println("swVec_i.peek()==null"+sw_i+", means sw" + sw_i + "'s port queue is empty, so break");
                    break;//the for loop port_j from 1~fakeSwPortNum is for every port of sw_i. So, if poll() gets null, means the port list of sw_i (i.e. swVec_i) is run out, so no need to keep going this for loop.
                }
                LinkedList<Short> swVec_j = swVec[sw_j];
                Short sw_j_port = swVec_j.poll();
                if(sw_j_port != null){
                    Short sw_i_port = swVec_i.poll();
                    myAddEdge(new Long(sw_i + 1), sw_i_port, new Long(sw_j + 1), sw_j_port);
                    myAddEdge(new Long(sw_j + 1), sw_j_port, new Long(sw_i + 1), sw_i_port);
                }

                sw_j += 1;
                sw_j %= fakeSwNum;
                //System.out.println("layer 2 end: sw_i="+sw_i+", sw_j="+sw_j+", swVec_i.size()="+swVec_i.size());
            }
        }
    }

    //s4s simulation
    public void _s4sStressTest_createFakeOFtoSNMPEdges(CommandInterpreter ci){
        logger.debug("==========================");
        logger.debug("Creating fake OF->SNMP edges...");
        logger.debug("----------------------------------");

        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        String ofSwStr = ci.nextArgument();
        String ofSwPortStr = ci.nextArgument();
        if(ofSwStr == null){
            System.out.println("Please use: s4sStressTest_createFakeOFtoSNMPEdges <OF-switch-1-mac> <OF-switch-1-port-number> ...");
            return;
        }
        if(ofSwPortStr == null){
            System.out.println("Please use: s4sStressTest_createFakeOFtoSNMPEdges <OF-switch-1-mac> <OF-switch-1-port-number>");
            return;
        }

        //Put all OF switches' port together in a List
        List<ImmutablePair<String, String>> ofSwPortList = new ArrayList<ImmutablePair<String, String>>();//<mac,portId>
        do{
            if(ofSwStr != null && ofSwPortStr == null){
                System.out.println("Please use: s4sStressTest_createFakeOFtoSNMPEdges <OF-switch-1-mac> <OF-switch-1-port-number>");
                return;
            }
            int ofSwPortNum = Integer.parseInt(ofSwPortStr);
            for(int i = 1; i <= ofSwPortNum; i++){
                ofSwPortStr = new Integer(i).toString();
                ofSwPortList.add(new ImmutablePair<String, String>(ofSwStr, ofSwPortStr));
                System.out.println("Add ofSwPortList - ofSw:"+ofSwStr+", ofSwPort:"+ofSwPortStr);
            }

            ofSwStr = ci.nextArgument();
            ofSwPortStr = ci.nextArgument();
        }while(ofSwPortStr != null);


        //LLDP data making...

        portToRemoteChassisOnSwitches.clear();
        //portToRemotePortIDOnSwitches.clear();
        portToRemotePortIDOnSwitchesTmp.clear();

        //To initialize (create) portToRemoteChassisOnSwitches, which is to store the mapping of port-remoteChassis
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            portToRemoteChassisOnSwitches.put(new Long(i), new HashMap<Short, String>());
        }

        //To initialize (create) portToRemotePortIDOnSwitchesTmp, which is to store the mapping of port-remotePortID
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            portToRemotePortIDOnSwitchesTmp.put(new Long(i), new HashMap<Short, String>());
        }

        //Put each OF port to the LLDP datastore (i.e. portToRemoteChassisOnSwitches and portToRemotePortIDOnSwitchesTmp)
        //(That is, place each port in ofSwPortList (i.e. the List of all OF switches' ports) to the portToRemoteChassisOnSwitches Map)
        int swCount = 0;
        int portCount = 1;
        for(ImmutablePair<String, String> ofSwPort : ofSwPortList){
            int nowSwitch = swCount % fakeSwNum2 + 1 + fakeSwNum;//derive which port to remote OF port
            int nowPort = (portCount / fakeSwNum2) + (portCount % fakeSwNum2 == 0 ? 0:1);//derive that on the chosen switch (i.e. nowSwitch), which port to remote OF port
            if(nowPort > fakeSwPortNum2){
                System.out.println("The number OF switches' ports is over SNMP switches' ports! (" + portCount + " > " + fakeSwNum2 +"sw*" + fakeSwPortNum2 + "port, so that nowPort "+nowPort+" > fakeSwPortNum2 "+fakeSwPortNum2+")");
                return;
            }System.out.println("nowSwitch="+nowSwitch+",nowPort="+nowPort+"; swCount="+swCount+",portCount="+portCount);

            //Put the OF switch
            Map<Short, String> portToRemoteChassis = portToRemoteChassisOnSwitches.get(new Long(nowSwitch));
            portToRemoteChassis.put(new Short((short)nowPort), ofSwPort.getLeft());
            swCount += 1;

            //Put the OF port
            Map<Short, String> remotePortIDs = portToRemotePortIDOnSwitchesTmp.get(new Long(nowSwitch));
            remotePortIDs.put(new Short((short)nowPort), ofSwPort.getRight());
            portCount += 1;

            logger.debug("In fake LLDP datastore: add switch {} port {}, with remote OF switch {} port {}", nowSwitch, nowPort, ofSwPort.getLeft(), ofSwPort.getRight());
        }

        resolveProdPortsAndAddEdges();
    }

    //s4s simulation
    public void _s4sStressTest_createFakeLLDPData(CommandInterpreter ci){
        logger.debug("==========================");
        logger.debug("Creating fake LLDP data...");
        logger.debug("----------------------------------");

        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        //Deal with user input OF switches list
        String ofSwStr = ci.nextArgument();
        String ofSwPortStr = ci.nextArgument();
        if(ofSwStr == null){
            System.out.println("Please use: s4sStressTest_createFakeLLDPData <OF-switch-1-mac> <OF-switch-1-port-number> ...");
            return;
        }
        if(ofSwPortStr == null){
            System.out.println("Please use: s4sStressTest_createFakeLLDPData <OF-switch-1-mac> <OF-switch-1-port-number>");
            return;
        }

        //Put all user input OF switches' port together in a List
        List<ImmutablePair<String, String>> ofSwPortList = new ArrayList<ImmutablePair<String, String>>();//<mac,portId>
        do{
            if(ofSwStr != null && ofSwPortStr == null){
                System.out.println("Please use: s4sStressTest_createFakeLLDPData <OF-switch-1-mac> <OF-switch-1-port-number>");
                return;
            }
            int ofSwPortNum = Integer.parseInt(ofSwPortStr);
            for(int i = 1; i <= ofSwPortNum; i++){
                ofSwPortStr = new Integer(i).toString();
                ofSwPortList.add(new ImmutablePair<String, String>(ofSwStr, ofSwPortStr));
                System.out.println("Add ofSwPortList - ofSw:"+ofSwStr+", ofSwPort:"+ofSwPortStr);
            }

            ofSwStr = ci.nextArgument();
            ofSwPortStr = ci.nextArgument();
        }while(ofSwPortStr != null);

        LinkedList<Short>[] swVec = createFakeSwAndPortsListForAssign();
        if(swVec == null){
            System.out.println("createFakeSwAndPortsListForAssign() fail!");
            return;
        }

        //Clear LLDP data
        portToRemoteChassisOnSwitches.clear();
        portToRemotePortIDOnSwitches.clear();

        //Create fake LLDP data for switch Chassis-ID mutual mapping
        createFakeLLDPDataForSwitchIDChassisMapping();

        //Create fake LLDP data for Eth-to-Eth edges
        createFakeLLDPDataForSNMPEdges(swVec);

        //Create fake LLDP data for OF-to-Eth edges
        createFakeLLDPDataForOFToSNMPEdges(swVec, ofSwPortList);

    }

    //s4s simulation
    public void _s4sEnableSim_DiscoveryService(CommandInterpreter ci){
        isFakeSim = true;
        System.out.println("Simulation is enabled now (DiscoveryService)");
    }
    
    //s4s simulation
    private LinkedList<Short>[] createFakeSwAndPortsListForAssign(){
        LinkedList<Short>[] swVec = new LinkedList[fakeSwNum + fakeSwNum2];
        for(int sw_i = 0; sw_i < fakeSwNum; sw_i++){
            swVec[sw_i] = new LinkedList<Short>();
            for(short port_j = 1; port_j <= fakeSwPortNum; port_j++)
              swVec[sw_i].add(new Short(port_j));
        }
        for(int sw_i = fakeSwNum; sw_i < fakeSwNum + fakeSwNum2; sw_i++){
            swVec[sw_i] = new LinkedList<Short>();
            for(short port_j = 1; port_j <= fakeSwPortNum2; port_j++)
              swVec[sw_i].add(new Short(port_j));
        }
        return swVec;
    }

    //s4s simulation
    private void createFakeLLDPDataForSwitchIDChassisMapping(){
        for(int i = 1; i <= fakeSwNum + fakeSwNum2; i++){
            Long id = new Long(i);
            String chassis = HexEncode.longToHexString(new Long(i));
            switches_ID2Chassis.put(id, chassis);
            switches_Chassis2ID.put(chassis, id);
            //System.out.println("Added LLDP data of chassis-to-swID mapping: chassis " + remoteChassis + " as switch ID " + HexEncode.stringToLong(remoteChassis));
        }
    }

    //s4s simulation
    private void createFakeLLDPDataForSNMPEdges(LinkedList<Short>[] swVec){
        //To initialize (create) portToRemoteChassisOnSwitches, which is to store the mapping of port-remoteChassis
        for(int i = 1; i <= fakeSwNum; i++){
            portToRemoteChassisOnSwitches.put(new Long(i), new HashMap<Short, String>());
        }

        //To initialize (create) portToRemotePortIDOnSwitches, which is to store the mapping of port-remotePortID
        for(int i = 1; i <= fakeSwNum; i++){
            portToRemotePortIDOnSwitches.put(new Long(i), new HashMap<Short, String>());
        }

        //create fake data for switches_Chassis2ID and localPortIDsOnSwitches, for Eth-to-Eth edges
        for(int sw_i = 1; sw_i <= fakeSwNum; sw_i++){
            switches_Chassis2ID.put(HexEncode.longToHexString((long)sw_i), new Long(sw_i));
            Map<Short, String> localPortIDs = new HashMap<Short, String>();
            for(short port_j = 1; port_j <= fakeSwPortNum; port_j++){
                localPortIDs.put(new Short(port_j), new Short(port_j).toString());
            }
            localPortIDsOnSwitches.put(new Long(sw_i), localPortIDs);
        }

        //create fake portToRemoteChassisOnSwitches data, for Eth-to-Eth edges
        int sw_j = 1;
        for(int sw_i = 0; sw_i < fakeSwNum; sw_i++){
            //System.out.println("layer 1: sw_i="+sw_i+", sw_j="+sw_j);
            LinkedList<Short> swVec_i = swVec[sw_i];
            int port_i_inc = 1;
            while(swVec_i.size() >= 0){
                //System.out.println("layer 2 begin: sw_i="+sw_i+", sw_j="+sw_j+", swVec_i.size()="+swVec_i.size());
                if(sw_j == sw_i) {
                    sw_j += 1;
                    sw_j %= fakeSwNum;
                    //System.out.println("sw_j == sw_i ="+sw_i+", so sw_j++ as "+sw_j+", then continue");continue;
                }

                if(swVec_i.peek() == null) {
                    //System.out.println("swVec_i.peek()==null"+sw_i+", means sw" + sw_i + "'s port queue is empty, so break");
                    break;//the for loop port_j from 1~fakeSwPortNum is for every port of sw_i. So, if poll() gets null, means the port list of sw_i (i.e. swVec_i) is run out, so no need to keep going this for loop.
                }
                LinkedList<Short> swVec_j = swVec[sw_j];
                Short sw_j_port = swVec_j.poll();
                if(sw_j_port != null){
                    Short sw_i_port = swVec_i.poll();
                    //_s4sStressTest_createFakeSNMPEdges():
                    //myAddEdge(new Long(sw_i + 1), sw_i_port, new Long(sw_j + 1), sw_j_port);
                    //myAddEdge(new Long(sw_j + 1), sw_j_port, new Long(sw_i + 1), sw_i_port);
                    Map<Short, String> rc1 = portToRemoteChassisOnSwitches.get(new Long(sw_i + 1));
                    rc1.put(sw_i_port, HexEncode.longToHexString(sw_j + 1));
                    portToRemoteChassisOnSwitches.put(new Long(sw_i + 1), rc1);
                    //System.out.println("rc1.size()="+rc1.size());

                    Map<Short, String> rc2 = portToRemoteChassisOnSwitches.get(new Long(sw_j + 1));
                    rc2.put(sw_j_port, HexEncode.longToHexString(sw_i + 1));
                    portToRemoteChassisOnSwitches.put(new Long(sw_j + 1), rc2);
                    //System.out.println("rc2.size()="+rc2.size());

                    Map<Short, String> rp1 = portToRemotePortIDOnSwitches.get(new Long(sw_i + 1));
                    rp1.put(sw_i_port, sw_j_port.toString());
                    portToRemotePortIDOnSwitches.put(new Long(sw_i + 1), rp1);
                    //System.out.println("rp1.size()="+rp1.size());

                    Map<Short, String> rp2 = portToRemotePortIDOnSwitches.get(new Long(sw_j + 1));
                    rp2.put(sw_j_port, sw_i_port.toString());
                    portToRemotePortIDOnSwitches.put(new Long(sw_j + 1), rp2);
                    //System.out.println("rp2.size()="+rp2.size());

                    //System.out.println("Created LLDP data between Switch " + (sw_i + 1) + " port " + sw_i_port + " and Switch " + (sw_j + 1) + " port " + sw_j_port);
                }

                sw_j += 1;
                sw_j %= fakeSwNum;
                //System.out.println("layer 2 end: sw_i="+sw_i+", sw_j="+sw_j+", swVec_i.size()="+swVec_i.size());
            }
        }
    }

    //s4s simulation
    private void createFakeLLDPDataForOFToSNMPEdges(LinkedList<Short>[] swVec, List<ImmutablePair<String, String>> ofSwPortList){
        //To initialize (create) portToRemoteChassisOnSwitches, which is to store the mapping of port-remoteChassis
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            portToRemoteChassisOnSwitches.put(new Long(i), new HashMap<Short, String>());
        }

        //To initialize (create) portToRemotePortIDOnSwitchesTmp, which is to store the mapping of port-remotePortID
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            portToRemotePortIDOnSwitches.put(new Long(i), new HashMap<Short, String>());
        }

        //create fake data for switches_Chassis2ID and localPortIDsOnSwitches, for OF-to-Eth edges
        for(int sw_i = fakeSwNum + 1; sw_i <= fakeSwNum + fakeSwNum2; sw_i++){
            switches_Chassis2ID.put(HexEncode.longToHexString((long)sw_i), new Long(sw_i));
            Map<Short, String> localPortIDs = new HashMap<Short, String>();
            for(short port_j = 1; port_j <= fakeSwPortNum2; port_j++){
                localPortIDs.put(new Short(port_j), new Short(port_j).toString());
            }
            localPortIDsOnSwitches.put(new Long(sw_i), localPortIDs);
        }

        //create fake portToRemoteChassisOnSwitches data, for OF-to-Eth edges
        int swCount = 0;
        int portCount = 1;
        for(ImmutablePair<String, String> ofSwPort : ofSwPortList){
            int nowSwitch = swCount % fakeSwNum2 + 1 + fakeSwNum;//derive which port to remote OF port
            int nowPort = (portCount / fakeSwNum2) + (portCount % fakeSwNum2 == 0 ? 0:1);//derive that on the chosen switch (i.e. nowSwitch), which port to remote OF port
            if(nowPort > fakeSwPortNum2){
                System.out.println("The number OF switches' ports is over SNMP switches' ports! (" + portCount + " > " + fakeSwNum2 +"sw*" + fakeSwPortNum2 + "port, so that nowPort "+nowPort+" > fakeSwPortNum2 "+fakeSwPortNum2+")");
                return;
            }System.out.println("nowSwitch="+nowSwitch+",nowPort="+nowPort+"; swCount="+swCount+",portCount="+portCount);

            //Put the OF switch
            Map<Short, String> portToRemoteChassis = portToRemoteChassisOnSwitches.get(new Long(nowSwitch));
            portToRemoteChassis.put(new Short((short)nowPort), ofSwPort.getLeft());
            swCount += 1;

            //Put the OF port
            Map<Short, String> remotePortIDs = portToRemotePortIDOnSwitches.get(new Long(nowSwitch));
            remotePortIDs.put(new Short((short)nowPort), ofSwPort.getRight());
            portCount += 1;

            logger.debug("In fake LLDP datastore: add switch {} port {}, with remote OF switch {} port {}", nowSwitch, nowPort, ofSwPort.getLeft(), ofSwPort.getRight());
        }
    }
}
