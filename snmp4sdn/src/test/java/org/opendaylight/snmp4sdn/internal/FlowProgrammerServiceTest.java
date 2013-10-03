/*
 * Copyright (c) 2013 Industrial Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
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

//import org.opendaylight.snmp4sdn.internal.FlowConverter;//s4s
//s4s
//import org.opendaylight.snmp4sdn.vendorextension.v6extension.V6Match;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.internal.DiscoveryService;

import org.opendaylight.snmp4sdn.protocol.util.HexString;

public class FlowProgrammerServiceTest {
    protected static final Logger logger = LoggerFactory.getLogger(FlowProgrammerServiceTest.class);

    public static void main(String args[]) {
        new FlowProgrammerServiceTest();
    }

    public FlowProgrammerServiceTest(){
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Long.class, "SNMP");
        /*try {
        //no need call the following functions, because as long as marked as @Test, it would be execute in maven script
            testSALtoOFFlowConverter();
            testDiscoveryService();
        } catch (UnknownHostException e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    public static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    /**
     * Generic NodeConnector creator
     * The nodeConnector type is inferred from the node type
     *
     * @param portId
     * @param node
     * @return
     */
    public static NodeConnector createNodeConnector(Object portId, Node node) {
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

    /**
     * NodeConnector creator where NodeConnector type can be specified
     * Needed to create special internal node connectors (like software stack)
     *
     * @param nodeConnectorType
     * @param portId
     * @param node
     * @return
     */
    public static NodeConnector createNodeConnector(
            String nodeConnectorType, Object portId, Node node) {
        try {
            return new NodeConnector(nodeConnectorType, portId, node);
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    //@Test
    public void testSALtoOFFlowConverter() throws UnknownHostException {
        System.out.println("----------------FlowProgrammerServiceTest Begin...-------------");
        //Node node = NodeCreator.createOFNode(1000l);//s4s
        Node node = createSNMPNode(new Long(1000l));//s4s.  here 1000l: not 10001, is 1000L
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//s4s
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x01 };
        //byte dstMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        //        (byte) 0x00, (byte) 0x02 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//s4s. in 10 base format = 112.114.207.43.149.36
        InetAddress srcIP = InetAddress.getByName("172.28.30.50");
        InetAddress dstIP = InetAddress.getByName("171.71.9.52");
        InetAddress ipMask = InetAddress.getByName("255.255.255.0");
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = (short) 27;
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
        match.setField(MatchType.DL_TYPE, ethertype);
        /*match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_VLAN_PR, vlanPr);
        match.setField(MatchType.NW_SRC, srcIP, ipMask);
        match.setField(MatchType.NW_DST, dstIP, ipMask);
        match.setField(MatchType.NW_TOS, tos);
        match.setField(MatchType.NW_PROTO, proto);
        match.setField(MatchType.TP_SRC, src);
        match.setField(MatchType.TP_DST, dst);*/

        Assert.assertTrue(match.isIPv4());

        List<Action> actions = new ArrayList<Action>();
        // Setting all the actions supported by of
        //actions.add(new PopVlan());
        actions.add(new Output(oport));//s4s
       /* actions.add(new Flood());
        actions.add(new FloodAll());
        actions.add(new SwPath());
        actions.add(new HwPath());
        actions.add(new Loopback());
        byte mac[] = { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5 };
        actions.add(new SetDlSrc(mac));
        actions.add(new SetDlDst(mac));
        actions.add(new SetNwSrc(dstIP));
        actions.add(new SetNwDst(srcIP));
        actions.add(new SetNwTos(3));
        actions.add(new SetTpSrc(10));
        actions.add(new SetTpDst(20));
        actions.add(new SetVlanId(200));*/

        Flow aFlow = new Flow(match, actions);

        List<Action> actions2 = new ArrayList<Action>();
        NodeConnector oport2 = NodeConnectorCreator.createNodeConnector((short)29, node);
        actions2.add(new Output(oport2));
        Flow bFlow = new Flow(match, actions2);

        //s4s
        FlowProgrammerService fps = new FlowProgrammerService();
        Controller controller = new Controller();
        SwitchHandler sw = new SwitchHandler(controller, null, "");
        sw.setId((Long)(node.getID()));
        sw.start();

        controller.init();
        controller.start();
        controller.addSwitch(sw);

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
        fps.removeFlow(node, bFlow);
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

    /* cmeth mark 1
        // *
        // *
         // * Convert the SAL aFlow to OF Flow
        // * /
        FlowConverter salToOF = new FlowConverter(aFlow);
        OFMatch ofMatch = salToOF.getOFMatch();
        List<OFAction> ofActions = salToOF.getOFActions();

        // *
        // * Convert the OF Flow to SAL Flow bFlow
        // * /
        FlowConverter ofToSal = new FlowConverter(ofMatch, ofActions);
        Flow bFlow = ofToSal.getFlow(node);
        Match bMatch = bFlow.getMatch();
        List<Action> bActions = bFlow.getActions();

        // *
        // * Verify the converted SAL flow bFlow is equivalent to the original SAL Flow
        // * /
        Assert.assertTrue(((NodeConnector) match.getField(MatchType.IN_PORT)
                .getValue()).equals(((NodeConnector) bMatch.getField(
                MatchType.IN_PORT).getValue())));
        Assert.assertTrue(Arrays.equals((byte[]) match.getField(
                MatchType.DL_SRC).getValue(), (byte[]) bMatch.getField(
                MatchType.DL_SRC).getValue()));
        Assert.assertTrue(Arrays.equals((byte[]) match.getField(
                MatchType.DL_DST).getValue(), (byte[]) bMatch.getField(
                MatchType.DL_DST).getValue()));
        Assert
                .assertTrue(((Short) match.getField(MatchType.DL_TYPE)
                        .getValue()).equals((Short) bMatch.getField(
                        MatchType.DL_TYPE).getValue()));
        Assert
                .assertTrue(((Short) match.getField(MatchType.DL_VLAN)
                        .getValue()).equals((Short) bMatch.getField(
                        MatchType.DL_VLAN).getValue()));
        Assert.assertTrue(((Byte) match.getField(MatchType.DL_VLAN_PR)
                .getValue()).equals((Byte) bMatch
                .getField(MatchType.DL_VLAN_PR).getValue()));
        Assert.assertTrue(((InetAddress) match.getField(MatchType.NW_SRC)
                .getValue()).equals((InetAddress) bMatch.getField(
                MatchType.NW_SRC).getValue()));
        Assert.assertTrue(((InetAddress) match.getField(MatchType.NW_SRC)
                .getMask()).equals((InetAddress) bMatch.getField(
                MatchType.NW_SRC).getMask()));
        Assert.assertTrue(((InetAddress) match.getField(MatchType.NW_DST)
                .getValue()).equals((InetAddress) bMatch.getField(
                MatchType.NW_DST).getValue()));
        Assert.assertTrue(((InetAddress) match.getField(MatchType.NW_DST)
                .getMask()).equals((InetAddress) bMatch.getField(
                MatchType.NW_DST).getMask()));
        Assert
                .assertTrue(((Byte) match.getField(MatchType.NW_PROTO)
                        .getValue()).equals((Byte) bMatch.getField(
                        MatchType.NW_PROTO).getValue()));
        Assert.assertTrue(((Byte) match.getField(MatchType.NW_TOS).getValue())
                .equals((Byte) bMatch.getField(MatchType.NW_TOS).getValue()));
        Assert.assertTrue(((Short) match.getField(MatchType.TP_SRC).getValue())
                .equals((Short) bMatch.getField(MatchType.TP_SRC).getValue()));
        Assert.assertTrue(((Short) match.getField(MatchType.TP_DST).getValue())
                .equals((Short) bMatch.getField(MatchType.TP_DST).getValue()));

        // FlowConverter parses and sets the actions in the same order for sal match and of match
        for (short i = 0; i < actions.size(); i++) {
            Assert.assertTrue(actions.get(i).equals(bActions.get(i)));
        }
        *///end of mark 1
    }

//s4s mark 2
/*
    @Test
    public void testV6toSALFlowConversion() throws Exception {
        Node node = NodeCreator.createOFNode(12l);
        NodeConnector port = NodeConnectorCreator.createNodeConnector(
                (short) 34, node);
        NodeConnector oport = NodeConnectorCreator.createNodeConnector(
                (short) 30, node);
        byte srcMac[] = { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
                (byte) 0x9a, (byte) 0xbc };
        byte dstMac[] = { (byte) 0x1a, (byte) 0x2b, (byte) 0x3c, (byte) 0x4d,
                (byte) 0x5e, (byte) 0x6f };
        InetAddress srcIP = InetAddress
                .getByName("2001:420:281:1004:407a:57f4:4d15:c355");
        InetAddress dstIP = InetAddress
                .getByName("2001:420:281:1004:e123:e688:d655:a1b0");
        InetAddress ipMask = InetAddress
                .getByName("ffff:ffff:ffff:ffff:0:0:0:0");
        short ethertype = EtherTypes.IPv6.shortValue();
        short vlan = (short) 27;
        byte vlanPr = 3;
        Byte tos = 4;
        byte proto = IPProtocols.TCP.byteValue();
        short src = (short) 55000;
        short dst = 80;

        // *
        // * Create a SAL Flow aFlow
        // * /
        Match aMatch = new Match();

        aMatch.setField(MatchType.IN_PORT, port);
        aMatch.setField(MatchType.DL_SRC, srcMac);
        aMatch.setField(MatchType.DL_DST, dstMac);
        aMatch.setField(MatchType.DL_TYPE, ethertype);
        aMatch.setField(MatchType.DL_VLAN, vlan);
        aMatch.setField(MatchType.DL_VLAN_PR, vlanPr);
        aMatch.setField(MatchType.NW_SRC, srcIP, ipMask);
        aMatch.setField(MatchType.NW_DST, dstIP, ipMask);
        aMatch.setField(MatchType.NW_TOS, tos);
        aMatch.setField(MatchType.NW_PROTO, proto);
        aMatch.setField(MatchType.TP_SRC, src);
        aMatch.setField(MatchType.TP_DST, dst);

        Assert.assertTrue(aMatch.isIPv6());

        List<Action> actions = new ArrayList<Action>();
        // Setting all the actions supported by of for v6
        actions.add(new PopVlan());
        actions.add(new Output(oport));
        actions.add(new Flood());
        actions.add(new FloodAll());
        actions.add(new SwPath());
        actions.add(new HwPath());
        actions.add(new Loopback());
        byte mac[] = { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5 };
        actions.add(new SetDlSrc(mac));
        actions.add(new SetDlDst(mac));
        //actions.add(new SetNwSrc(dstIP)); Nicira extensions do not provide IPv6 match addresses change
        //actions.add(new SetNwDst(srcIP));
        actions.add(new SetNwTos(3));
        actions.add(new SetTpSrc(10));
        actions.add(new SetTpDst(65535));
        actions.add(new SetVlanId(200));

        Flow aFlow = new Flow(aMatch, actions);

        // *
        // * Convert the SAL aFlow to OF Flow
        // * /
        FlowConverter salToOF = new FlowConverter(aFlow);
        V6Match v6Match = (V6Match) salToOF.getOFMatch();
        List<OFAction> ofActions = salToOF.getOFActions();

        // *
        // * Convert the OF Flow to SAL Flow bFlow
        // * /
        FlowConverter ofToSal = new FlowConverter(v6Match, ofActions);
        Flow bFlow = ofToSal.getFlow(node);
        Match bMatch = bFlow.getMatch();
        List<Action> bActions = bFlow.getActions();

        // *
        // * Verify the converted SAL flow bFlow is equivalent to the original SAL Flow
        // * /
        Assert.assertTrue(((NodeConnector) aMatch.getField(MatchType.IN_PORT)
                .getValue()).equals(((NodeConnector) bMatch.getField(
                MatchType.IN_PORT).getValue())));
        Assert.assertTrue(Arrays.equals((byte[]) aMatch.getField(
                MatchType.DL_SRC).getValue(), (byte[]) bMatch.getField(
                MatchType.DL_SRC).getValue()));
        Assert.assertTrue(Arrays.equals((byte[]) aMatch.getField(
                MatchType.DL_DST).getValue(), (byte[]) bMatch.getField(
                MatchType.DL_DST).getValue()));
        Assert.assertTrue(((Short) aMatch.getField(MatchType.DL_TYPE)
                .getValue()).equals((Short) bMatch.getField(MatchType.DL_TYPE)
                .getValue()));
        Assert.assertTrue(((Short) aMatch.getField(MatchType.DL_VLAN)
                .getValue()).equals((Short) bMatch.getField(MatchType.DL_VLAN)
                .getValue()));
        Assert.assertTrue(((Byte) aMatch.getField(MatchType.DL_VLAN_PR)
                .getValue()).equals((Byte) bMatch
                .getField(MatchType.DL_VLAN_PR).getValue()));
        Assert.assertTrue(((InetAddress) aMatch.getField(MatchType.NW_SRC)
                .getValue()).equals((InetAddress) bMatch.getField(
                MatchType.NW_SRC).getValue()));
        Assert.assertTrue(((InetAddress) aMatch.getField(MatchType.NW_SRC)
                .getMask()).equals((InetAddress) bMatch.getField(
                MatchType.NW_SRC).getMask()));
        Assert.assertTrue(((InetAddress) aMatch.getField(MatchType.NW_DST)
                .getValue()).equals((InetAddress) bMatch.getField(
                MatchType.NW_DST).getValue()));
        Assert.assertTrue(((InetAddress) aMatch.getField(MatchType.NW_DST)
                .getMask()).equals((InetAddress) bMatch.getField(
                MatchType.NW_DST).getMask()));
        Assert.assertTrue(((Byte) aMatch.getField(MatchType.NW_PROTO)
                .getValue()).equals((Byte) bMatch.getField(MatchType.NW_PROTO)
                .getValue()));
        Assert.assertTrue(((Byte) aMatch.getField(MatchType.NW_TOS).getValue())
                .equals((Byte) bMatch.getField(MatchType.NW_TOS).getValue()));
        Assert
                .assertTrue(((Short) aMatch.getField(MatchType.TP_SRC)
                        .getValue()).equals((Short) bMatch.getField(
                        MatchType.TP_SRC).getValue()));
        Assert
                .assertTrue(((Short) aMatch.getField(MatchType.TP_DST)
                        .getValue()).equals((Short) bMatch.getField(
                        MatchType.TP_DST).getValue()));

        // FlowConverter parses and sets the actions in the same order for sal match and of match
        for (short i = 0; i < actions.size(); i++) {
            Assert.assertTrue(actions.get(i).equals(bActions.get(i)));
        }
    }

    @Test
    public void testV6MatchToSALMatchToV6MatchConversion()
            throws UnknownHostException {
        NodeConnector port = NodeConnectorCreator.createNodeConnector(
                (short) 24, NodeCreator.createOFNode(6l));
        byte srcMac[] = { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
                (byte) 0x9a, (byte) 0xbc };
        byte dstMac[] = { (byte) 0x1a, (byte) 0x2b, (byte) 0x3c, (byte) 0x4d,
                (byte) 0x5e, (byte) 0x6f };
        InetAddress srcIP = InetAddress
                .getByName("2001:420:281:1004:407a:57f4:4d15:c355");
        InetAddress dstIP = InetAddress
                .getByName("2001:420:281:1004:e123:e688:d655:a1b0");
        InetAddress ipMask = null;//InetAddress.getByName("ffff:ffff:ffff:ffff:0:0:0:0");
        short ethertype = EtherTypes.IPv6.shortValue();
        short vlan = (short) 27;
        byte vlanPr = 3;
        Byte tos = 4;
        byte proto = IPProtocols.TCP.byteValue();
        short src = (short) 55000;
        short dst = 80;

        // *
        // * Create a SAL Flow aFlow
        // * /
        Match aMatch = new Match();

        aMatch.setField(MatchType.IN_PORT, port);
        aMatch.setField(MatchType.DL_SRC, srcMac);
        aMatch.setField(MatchType.DL_DST, dstMac);
        aMatch.setField(MatchType.DL_TYPE, ethertype);
        aMatch.setField(MatchType.DL_VLAN, vlan);
        aMatch.setField(MatchType.DL_VLAN_PR, vlanPr);
        aMatch.setField(MatchType.NW_SRC, srcIP, ipMask);
        aMatch.setField(MatchType.NW_DST, dstIP, ipMask);
        aMatch.setField(MatchType.NW_TOS, tos);
        aMatch.setField(MatchType.NW_PROTO, proto);
        aMatch.setField(MatchType.TP_SRC, src);
        aMatch.setField(MatchType.TP_DST, dst);

        Assert.assertTrue(aMatch.isIPv6());

    }

    */// end of mark 2
}
