/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.reader.FlowOnNode;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.utils.IPProtocols;
import org.opendaylight.controller.sal.utils.NodeConnectorCreator;
import org.opendaylight.controller.sal.utils.NodeCreator;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.internal.DiscoveryService;

import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

public class ReadServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(ReadServiceTest.class);
    ReadServiceFilter rsf = null;
    ReadService rs = null;
    Node testNodeGood = null;
    Flow testFlow = null;
    Node testNodeBad = null;

    public static void main(String args[]) {
        new ReadServiceTest();
    }

    @Before
    public void before() throws Exception {
        //logger.info("before() 1");
        controller = new Controller();
        controller.init_forTest();
        controller.start();
        createNetworkAndService(controller);
        //logger.info("before() 2");
    }

    @After
    public void after() {
        //logger.info("after() 1");
        controller.stop();
        controller.destroy();
        //logger.info("after() 2");
    }

    public ReadServiceTest(){
        logger.info("====== ReadServiceTest begin======");
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
    }

    private void createNetworkAndService(Controller controller){
        /*
         *Create a testing network
        */
            //controller://code is moved to before()

            //DB
        CmethUtil cmeth = controller.getCmethUtil();
        ConcurrentMap<Long, Vector> table = cmeth.getEntries();
        Long mac = null;
        for (ConcurrentMap.Entry<Long, Vector> entry: table.entrySet()) {
            mac = entry.getKey();
            if(mac == null){
                Assert.assertTrue("empty CmethUtil table!", false);
            }
            break;
        }
            //node:
        testNodeGood = createSNMPNode(mac);
            //nodeBad: fake node
        testNodeBad = createSNMPNode(9999L);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(testNodeGood, sw, (Long)(testNodeGood.getID()));
            //port:
        NodeConnector iport = createNodeConnector( (short) 1, testNodeGood);
        NodeConnector oport = createNodeConnector( (short) 30, testNodeGood);//s4s
        /*
          *end of Create a testing network
        */

        /*
         * Create a SAL Flow testFlow
         */
            //layer 2:
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;

        Match match = new Match();
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        testFlow = new Flow(match, actions);

         /*
         * Create a Read Service
         */
        rsf = new ReadServiceFilter();
        rsf.setController(controller);
        rs = new ReadService();
        rs.setService(rsf);
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: ReadServiceTest: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
            return null;
        }
    }

    private static NodeConnector createNodeConnector(Object portId, Node node) {
        if (node.getType().equals("SNMP")) {
            try {
                return new NodeConnector("SNMP",
                        (Short) portId, node);
            } catch (ConstructionException e1) {
                logger.error("",e1);
                return null;
            }
        }
        return null;
    }

    private static NodeConnector createNodeConnector(
            String nodeConnectorType, Object portId, Node node) {
        try {
            return new NodeConnector(nodeConnectorType, portId, node);
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    private void addNewSwitch(Node node, SwitchHandler sw, Long sid){
        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        controller.handleNewConnection(sid);
     }

    private void addFlow(Node node, Flow flow){
        FlowProgrammerService fps = new FlowProgrammerService();
        fps.setController(controller);
        Status status = fps.addFlow(node, flow);
        if(status.getCode().toString().equals(StatusCode.SUCCESS.toString()))
            return;
        else
            Assert.assertTrue("to create normal case for readFlow test, we firstly add a flow to switch, but fail!", false);
    }

    @Test
    public void test_readFlow_nc() throws Exception {
        logger.info("[Enter test_readFlow() - normal case]");
        addFlow(testNodeGood, testFlow);//add testFlow to testNodeGood, fot the next line (readFlow()) to read
        FlowOnNode flown = rs.readFlow(testNodeGood, testFlow, false);
        Assert.assertNotNull("error: testFlow installed but can't read it by ReadService.readFlow()!", flown);
    }

    @Test
    public void test_readFlow_ec() throws Exception {
        logger.info("[Enter test_readFlow() - error case]");
        FlowOnNode flown = rs.readFlow(testNodeBad, testFlow, false);//give a non-existing node
        Assert.assertNull("error: bad node is supposed to be not installed but can be read by ReadService.readFlow()!", flown);
    }

    @Test
    public void test_readAllFlow_nc() throws Exception {
        logger.info("[Enter test_readAllFlow() - normal case]");
        addFlow(testNodeGood, testFlow);//add testFlow to testNodeGood, fot the next line (readFlow()) to read
        List<FlowOnNode> flowns = rs.readAllFlow(testNodeGood, false);
        Assert.assertNotNull("error: testFlow installed but can't read it by ReadService.readFlow()!", flowns);

        for(int i = 0; i < flowns.size(); i++){
            logger.info(((FlowOnNode)flowns.get(i)).toString());
        }
    }

    @Test
    public void test_readAllFlow_ec() throws Exception {
        logger.info("[Enter test_readAllFlow() - error case]");
        List<FlowOnNode> flowns = rs.readAllFlow(testNodeBad, false);//give a non-existing node
        Assert.assertNull("error: bad node is supposed to be not installed but can be read by ReadService.readFlow()!", flowns);
    }
}
