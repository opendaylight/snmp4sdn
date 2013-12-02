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

public class ReadServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(ReadServiceTest.class);
    long switchNum = 1;

    public static void main(String args[]) {
        new ReadServiceTest();
    }

    public ReadServiceTest(){
        System.out.println("====== ReadServiceTest begin======");
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
    public void test_readFlow_noMatterTheFlowExistOrNot() throws Exception {
        System.out.println("----- test_readFlow() Begin -----");

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
         * Create a Read Service
         */
        ReadServiceFilter rsf = new ReadServiceFilter();
        rsf.setController(controller);
        ReadService rs = new ReadService();
        rs.setService(rsf);

        System.out.println("------------call readFlow()-----------------");
        FlowOnNode flown = rs.readFlow(node, aFlow, false);
        if(flown == null){
            System.out.println("----- test_readFlow() result: " + StatusCode.NOTFOUND.toString() + " -----");
            Assert.assertTrue(true);
            return;
        }
        System.out.println(flown.toString());
        System.out.println("----- test_readFlow() result: " + StatusCode.SUCCESS.toString() + " -----");
        Assert.assertTrue(true);
    }

    //@Test
    public void test_readAllFlow() throws Exception {
        System.out.println("----- test_readAllFlow() Begin -----");

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
         * Create a Read Service
         */
        ReadServiceFilter rsf = new ReadServiceFilter();
        rsf.setController(controller);
        ReadService rs = new ReadService();
        rs.setService(rsf);

        List<FlowOnNode> flowns = rs.readAllFlow(node, false);
        if(flowns == null){
            System.out.println("----- test_readAllFlow() result: " + StatusCode.NOTFOUND.toString() + " -----");
            Assert.assertTrue(false);
            return;
        }
        for(int i = 0; i < flowns.size(); i++){
            System.out.println((FlowOnNode)flowns.get(i));
        }
        System.out.println("----- test_readAllFlow() result: " + StatusCode.SUCCESS.toString() + " -----");
        Assert.assertTrue(true);
    }
}
