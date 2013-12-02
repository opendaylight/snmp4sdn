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

import org.junit.Assert;
import org.junit.Test;
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

public class FlowProgrammerServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(FlowProgrammerServiceTest.class);
    long switchNum = 1;

    public static void main(String args[]) {
        new FlowProgrammerServiceTest();
    }

    public FlowProgrammerServiceTest(){
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
        /*//need the following when directly execute this program in command line
        try {
            testReadWriteFlowsBySNMP();
        } catch (UnknownHostException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.error("",e1);
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
    public void test_addFlow() throws Exception {
        System.out.println("----- test_addFlow() Begin -----");

        /*
         *Create a testing network (this section is copied to all other @Test functions)***
        */
            //controller:
        controller = new Controller();
        controller.init_forTest();
        controller.start();
            //node:
        Node node = null;
        node = createSNMPNode(switchNum++);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(node, sw, (Long)(node.getID()));
            //port:
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
            //layer 2:
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;

        /*
          *end of Create a testing network
        */

        /*
         * Create a SAL Flow aFlow
         */
        Match match = new Match();
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow aFlow = new Flow(match, actions);

        /*
         * Create a Flow Programmer Service
         */
        FlowProgrammerService fps = new FlowProgrammerService();
        fps.setController(controller);

        Status status = fps.addFlow(node, aFlow);
        System.out.println("----- test_addFlow() result: " + status.getCode().toString() + " -----");
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        controller.stop();
    }

    //@Test
    public void test_modifyFlow() throws Exception {
        System.out.println("----- test_modifyFlow() Begin: addFlow(aFlow) and then modifyFlow(aFlow, bFlow) -----");

        /*
         *Create a testing network (this section is copied to all other @Test functions)***
        */
            //controller:
        controller = new Controller();
        controller.init_forTest();
        controller.start();
            //node:
        Node node = null;
        node = createSNMPNode(switchNum++);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(node, sw, (Long)(node.getID()));
            //port:
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
            //layer 2:
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;

        /*
          *end of Create a testing network
        */

        /*
         * Create a SAL Flow aFlow
         */
        Match match = new Match();
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow aFlow = new Flow(match, actions);

        List<Action> actions2 = new ArrayList<Action>();
        NodeConnector oport2 = createNodeConnector((short)29, node);
        actions2.add(new Output(oport2));
        Flow bFlow = new Flow(match, actions2);

        /*
         * Create a Flow Programmer Service
         */
        FlowProgrammerService fps = new FlowProgrammerService();
        fps.setController(controller);

        fps.addFlow(node, aFlow);
        Status status = fps.modifyFlow(node, aFlow, bFlow);
        //Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());

        if(status.getCode().toString().equals(StatusCode.SUCCESS.toString()))
            System.out.println("----- test_modifyFlow() result: " + status.getCode().toString() + " -----");
        else
            System.out.println("----- test_modifyFlow() result: FAIL(" + status.getCode().toString() + ") -----");

        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        controller.stop();
    }

    //@Test
    public void test_removeFlow() throws Exception {
        System.out.println("----- test_removeFlow() Begin -----");

        /*
         *Create a testing network (this section is copied to all other @Test functions)***
        */
            //controller:
        controller = new Controller();
        controller.init_forTest();
        controller.start();
            //node:
        Node node = null;
        node = createSNMPNode(switchNum++);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(node, sw, (Long)(node.getID()));
            //port:
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
            //layer 2:
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;

        /*
          *end of Create a testing network
        */

        /*
         * Create a SAL Flow aFlow
         */
        Match match = new Match();
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow aFlow = new Flow(match, actions);

        /*
         * Create a Flow Programmer Service
         */
        FlowProgrammerService fps = new FlowProgrammerService();
        fps.setController(controller);

        Status status = fps.removeFlow(node, aFlow);
        System.out.println("----- test_removeFlow() result: " + status.getCode().toString() + " -----");
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        controller.stop();
    }


    ////@xTest //this test covers all the unit tests above, but may occur error, not suggest to use this test
    public void testReadWriteFlowsBySNMP() throws Exception {
        System.out.println("----- testReadWriteFlowsBySNMP() Begin -----");

        /*
         *Create a testing network (this section is copied to all other @Test functions)***
        */
            //controller:
        controller = new Controller();
        controller.init_forTest();
        controller.start();
            //node:
        Node node = null;
        node = createSNMPNode(switchNum++);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(node, sw, (Long)(node.getID()));
            //port:
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
            //layer 2:
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;
            //layer 3:
        InetAddress srcIP = InetAddress.getByName("172.28.30.50");
        InetAddress dstIP = InetAddress.getByName("171.71.9.52");
        InetAddress ipMask = InetAddress.getByName("255.255.255.0");
            //other:
        byte vlanPr = 3;
        Byte tos = 4;
        byte proto = IPProtocols.TCP.byteValue();
        short src = (short) 55000;
        short dst = 80;
        /*
          *end of Create a testing network
        */

        /*
         * Create a SAL Flow aFlow
         */
        Match match = new Match();
        //match.setField(MatchType.IN_PORT, port);
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        /* match.setField(MatchType.DL_VLAN_PR, vlanPr);
        match.setField(MatchType.NW_SRC, srcIP, ipMask);
        match.setField(MatchType.NW_DST, dstIP, ipMask);
        match.setField(MatchType.NW_TOS, tos);
        match.setField(MatchType.NW_PROTO, proto);
        match.setField(MatchType.TP_SRC, src);
        match.setField(MatchType.TP_DST, dst);*/

        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));

        Flow aFlow = new Flow(match, actions);

        List<Action> actions2 = new ArrayList<Action>();
        NodeConnector oport2 = createNodeConnector((short)29, node);
        actions2.add(new Output(oport2));
        Flow bFlow = new Flow(match, actions2);

        /*
         * Create a Flow Programmer Service
         */
        FlowProgrammerService fps = new FlowProgrammerService();
        fps.setController(controller);

        System.out.println("----------------call addFlow()----------------");
        fps.addFlow(node, aFlow);
        System.out.println("----------------call addFlow() done----------------");

        System.out.println("----------------call modifyFlow()----------------");
        fps.modifyFlow(node, aFlow, bFlow);
        System.out.println("----------------call modifyFlow() done----------------");

        System.out.println("----------------call modifyFlow() II----------------");
        fps.modifyFlow(node, aFlow, bFlow);
        System.out.println("----------------call modifyFlow() II done----------------");

        System.out.println("----------------call removeFlow()----------------");
        //fps.removeFlow(node, bFlow);
        System.out.println("----------------call removeFlow() done----------------");


        ReadServiceFilter rsf = new ReadServiceFilter();
        rsf.setController(controller);
        ReadService rs = new ReadService();
        rs.setService(rsf);

        System.out.println("------------call readFlow()-----------------");
        FlowOnNode flown = rs.readFlow(node, aFlow, false);
        System.out.println("flown:" + flown.toString());

        System.out.println("------------call readAllFlow()-----------------");
        List<FlowOnNode> flowns = rs.readAllFlow(node, false);
        System.out.println("------------call readAllFlow()-----------------");
        for(int i = 0; i < flowns.size(); i++){
            System.out.println((FlowOnNode)flowns.get(i));
        }
        System.out.println("------------call readAllFlow() done-----------------");
        controller.stop();
    }
}
