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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.internal.DiscoveryService;

import org.opendaylight.snmp4sdn.protocol.util.HexString;

public class FlowProgrammerServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(FlowProgrammerServiceTest.class);

    public static void main(String args[]) {
        new FlowProgrammerServiceTest();
    }

    public FlowProgrammerServiceTest(){
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Long.class, "SNMP");
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

    private void addNewSwitch(Node node, SwitchHandler sw, Long sid, String switchIP){
        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        //controller.getCmethUtil().addEntry(sid, switchIP);
        controller.handleNewConnection(sid);
     }

    @Test
    public void testReadWriteFlowsBySNMP() throws UnknownHostException {
        System.out.println("----------------testReadWriteFlowsBySNMP Begin...-------------");

        controller = new Controller();
        controller.init_forTest();
        controller.start();

        //Node node = createSNMPNode(new Long(1l));//s4s.  here 1l: not 11, is 1L
        Node node = null;

        SwitchHandler sw = new SwitchHandler(controller, "");
        /*//replace this part by addNewSwitch()sw.setId((Long)(node.getID()));
        sw.start();
        controller.addSwitch(sw);*/
        
        node = createSNMPNode(HexString.toLong("00:00:00:00:00:01"));
        addNewSwitch(node, sw, (Long)(node.getID()), "10.216.0.31");
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 1;

        InetAddress srcIP = InetAddress.getByName("172.28.30.50");
        InetAddress dstIP = InetAddress.getByName("171.71.9.52");
        InetAddress ipMask = InetAddress.getByName("255.255.255.0");
        byte vlanPr = 3;
        Byte tos = 4;
        byte proto = IPProtocols.TCP.byteValue();
        short src = (short) 55000;
        short dst = 80;

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
    }
}
