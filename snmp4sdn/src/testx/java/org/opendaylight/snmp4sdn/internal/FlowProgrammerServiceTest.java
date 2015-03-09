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
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;


import org.opendaylight.snmp4sdn.protocol.util.HexString;

public class FlowProgrammerServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(FlowProgrammerServiceTest.class);
    FlowProgrammerService fps = null;
    Node testNodeGood = null;
    Node testNodeBad = null;
    Flow testFlow = null;
    Flow testFlow1 = null;
    Flow testFlow2 = null;
    Flow testFlow3 = null;
 
    public static void main(String args[]) {
        new FlowProgrammerServiceTest();
    }

    @Before
    public void before() throws Exception {
        //logger.info("before() 1");
        controller = new Controller();
        controller.init_forTest();
        controller.start();
        createNetworkAndService(controller);
        ////logger.info("before() 2");
    }

    @After
    public void after() {
        //logger.info("after() 1");
        controller.stop();
        controller.destroy();
        //logger.info("after() 2");
    }

    public FlowProgrammerServiceTest(){
        logger.info("====== FlowProgrammerServiceTest begin======");
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

        List<Action> actions1 = new ArrayList<Action>();
        NodeConnector oport1 = createNodeConnector( (short) 29, testNodeGood);
        actions1.add(new Output(oport1));
        testFlow1 = new Flow(match, actions1);

        List<Action> actions2 = new ArrayList<Action>();
        NodeConnector oport2 = createNodeConnector( (short) 30, testNodeGood);
        actions2.add(new Output(oport2));
        testFlow2 = new Flow(match, actions2);

        Match match3 = match.clone();//for modifyFlow()'s error case test: "match3" is the same as "match", but destination field is different
        byte dstMac3[] = { (byte) 0x99, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        match3.setField(MatchType.DL_DST, dstMac3);
        testFlow3 = new Flow(match3, actions1);
        
        /*
        * Create a Flow Programmer Service
        */
        fps = new FlowProgrammerService();
        fps.setController(controller);
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: FlowProgrammerServiceTest: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
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

    //@Test
    public void test_addFlow_nc() throws Exception {
        logger.info("[Enter test_addFlow() -- normal case]");
        Status status = fps.addFlow(testNodeGood, testFlow);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_addFlow_ec() throws Exception {
        logger.info("[Enter test_addFlow() -- error case]");
        Status status = fps.addFlow(testNodeBad, testFlow);//give a non-existing node
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_modifyFlow_nc() throws Exception {
        logger.info("[Enter test_modifyFlow() -- normal case]");
        Status status;
        status = fps.addFlow(testNodeGood, testFlow1);
        Assert.assertEquals("\tfirstly add testFlow1 so as to modify it, but add flow fail!", StatusCode.SUCCESS.toString(), status.getCode().toString());
        status = fps.modifyFlow(testNodeGood, testFlow1, testFlow2);//the two flows have the same "match", so testFlow1 will be deleted and testFlow2 will be added, let's check for the 'delete' and 'add'
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        //status = fps.readFlow(testNodeGood, testFlow1);
    }

    //@Test
    public void test_modifyFlow_ec1() throws Exception {
        logger.info("[Enter test_modifyFlow() -- error case 1]");
        Status status;
        status = fps.modifyFlow(testNodeBad, testFlow1, testFlow2);//give a non-existing node
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_modifyFlow_ec2() throws Exception {
        logger.info("[Enter test_modifyFlow() -- error case 2]");
        Status status;
        status = fps.addFlow(testNodeGood, testFlow1);
        Assert.assertEquals("\tfirstly add testFlow1 so as to modify it, but add flow fail!", StatusCode.SUCCESS.toString(), status.getCode().toString());
        status = fps.modifyFlow(testNodeGood, testFlow1, testFlow3);//give two flows whose match field (destination mac) is not consistent
        Assert.assertEquals(StatusCode.NOTACCEPTABLE.toString(), status.getCode().toString());
    }

    //@Test
    public void test_removeFlow() throws Exception {
        logger.info("----- test_removeFlow() Begin -----");
        Status status = fps.removeFlow(testNodeGood, testFlow);
        logger.info("----- test_removeFlow() result: " + status.getCode().toString() + " -----");
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }
}
