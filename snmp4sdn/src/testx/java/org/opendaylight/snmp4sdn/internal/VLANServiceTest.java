/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
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
import java.util.concurrent.CopyOnWriteArrayList;
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

//import org.opendaylight.controller.sal.vlan.VLANTable;//ad-sal
import org.opendaylight.snmp4sdn.VLANTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;

import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

public class VLANServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(VLANServiceTest.class);
    VLANService vs = null;
    Node testNodeGood = null;
    Node testNodeBad = null;//value is given in createNetworkAndService()
    int goodVLAN = 4090;
    int badVLAN = 9999;
    short goodPort = 1;
    short badPort = 0;

    boolean isDummy = true;

    public static void main(String args[]) {
        new VLANServiceTest();
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

    public VLANServiceTest(){
        logger.info("====== VLANServiceTest begin======");
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
            mac = entry.getKey();//TODO: rewrite code to make mac randomly choosen
            if(mac == null){
                Assert.assertTrue("empty CmethUtil table!", false);
            }
            break;
        }
            //node:
        testNodeGood = createSNMPNode(mac);
            //nodeBad: fake node
        testNodeBad = createSNMPNode(9999L);
        /*
          *end of Create a testing network
        */


         /*
         * Create a VLAN Service
         */
        vs = new VLANService();
        vs.setController(controller);
        vs.isDummy = this.isDummy;
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: VLANServiceTest: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
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

    @Test
    public void test_addVLAN_nc() throws Exception {
        logger.info("<<< Enter test_addVLAN() - normal case >>>");
        Status status = vs.addVLAN(testNodeGood, new Integer(goodVLAN), "goodVLAN");
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void test_addVLAN_ec1() throws Exception {
        logger.info("<<< Enter test_addVLAN() - error case 1 >>>");
        Status status = vs.addVLAN(testNodeBad, new Integer(goodVLAN), "goodVLAN");//give a non-existing node
        Assert.assertEquals(StatusCode.NOTFOUND.toString(), status.getCode().toString());
    }

    
    @Test
    public void test_addVLAN_ec2() throws Exception {
        logger.info("<<< Enter test_addVLAN() - error case 2 >>>");
        Status status = vs.addVLAN(testNodeGood, new Integer(badVLAN), "badVLAN");//give a invalid VLAN ID
        Assert.assertEquals(StatusCode.NOTACCEPTABLE.toString(), status.getCode().toString());
    }

    @Test
    public void test_setVLANPorts_nc() throws Exception {
        logger.info("<<< Enter test_setVLANPorts() - normal case >>>");
        List<NodeConnector> nodeConns = new CopyOnWriteArrayList<NodeConnector>();
        nodeConns.add(createNodeConnector(new Short(goodPort), testNodeGood));
        Status status = vs.setVLANPorts(testNodeGood, new Integer(goodVLAN), nodeConns);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void test_setVLANPorts_ec1() throws Exception {
        logger.info("<<< Enter test_setVLANPorts() - error case 1 >>>");
        List<NodeConnector> nodeConns = new CopyOnWriteArrayList<NodeConnector>();
        nodeConns.add(createNodeConnector(new Short(goodPort), testNodeBad));
        Status status = vs.setVLANPorts(testNodeBad, new Integer(goodVLAN), nodeConns);//give a non-existing node
        Assert.assertEquals(StatusCode.NOTFOUND.toString(), status.getCode().toString());
    }

    @Test
    public void test_setVLANPorts_ec2() throws Exception {
        logger.info("<<< Enter test_setVLANPorts() - error case 2 >>>");
        List<NodeConnector> nodeConns = new CopyOnWriteArrayList<NodeConnector>();
        nodeConns.add(createNodeConnector(new Short(goodPort), testNodeGood));
        Status status = vs.setVLANPorts(testNodeGood, new Integer(badVLAN), nodeConns);//give a invalid VLAN ID
        Assert.assertEquals(StatusCode.NOTACCEPTABLE.toString(), status.getCode().toString());
    }

    @Test
    public void test_setVLANPorts_ec3() throws Exception {
        logger.info("<<< Enter test_setVLANPorts() - error case 3 >>>");
        List<NodeConnector> nodeConns = new CopyOnWriteArrayList<NodeConnector>();
        nodeConns.add(createNodeConnector(new Short(badPort), testNodeGood));//give invalid port
        Status status = vs.setVLANPorts(testNodeGood, new Integer(goodVLAN), nodeConns);
        Assert.assertEquals(StatusCode.NOTACCEPTABLE.toString(), status.getCode().toString());
    }

    @Test
    public void test_deleteVLAN_nc() throws Exception {
        logger.info("<<< Enter test_deleteVLAN() - normal case >>>");
        Status status = vs.deleteVLAN(testNodeGood, new Integer(goodVLAN));
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void test_deleteVLAN_ec1() throws Exception {
        logger.info("<<< Enter test_deleteVLAN() - error case 1 >>>");
        Status status = vs.deleteVLAN(testNodeBad, new Integer(goodVLAN));//give a non-existing node
        Assert.assertEquals(StatusCode.NOTFOUND.toString(), status.getCode().toString());
    }

    @Test
    public void test_deleteVLAN_ec2() throws Exception {
        logger.info("<<< Enter test_deleteVLAN() - error case 2 >>>");
        Status status = vs.deleteVLAN(testNodeGood, new Integer(badVLAN));//give a invalid VLAN ID
        Assert.assertEquals(StatusCode.NOTACCEPTABLE.toString(), status.getCode().toString());
    }

    @Test
    public void test_getVLANPorts_nc() throws Exception {//note! here call setVLANPorts() first, then call getVLANPorts()
        logger.info("<<< Enter test_getVLANPorts() - normal case >>>");

        if(vs.isDummy = true){
            List<NodeConnector> ncs = vs.getVLANPorts(testNodeGood, new Integer(goodVLAN));
            Assert.assertNotNull(ncs);
            return;
        }

        //when dummy is enabled
        List<NodeConnector> nodeConns = new CopyOnWriteArrayList<NodeConnector>();
        NodeConnector nc = createNodeConnector(new Short(goodPort), testNodeGood);
        nodeConns.add(nc);
        Status status = vs.setVLANPorts(testNodeGood, new Integer(goodVLAN), nodeConns);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());

        List<NodeConnector> ncs = vs.getVLANPorts(testNodeGood, new Integer(goodVLAN));
        Assert.assertNotNull(ncs);

        Assert.assertEquals(ncs.size(), 1);
        Assert.assertEquals(((Short)(nc.getID())).intValue(), ((Short)(ncs.get(0).getID())).intValue());
    }

    @Test
    public void test_getVLANPorts_ec1() throws Exception {
        logger.info("<<< Enter test_getVLANPorts() - error case 1 >>>");
        List<NodeConnector> ncs = vs.getVLANPorts(testNodeBad, new Integer(goodVLAN));//give a non-existing node
        Assert.assertNull(ncs);
    }

    @Test
    public void test_getVLANPorts_ec2() throws Exception {
        logger.info("<<< Enter test_getVLANPorts() - error case 2 >>>");
        List<NodeConnector> ncs = vs.getVLANPorts(testNodeGood, new Integer(badVLAN));//give a invalid VLAN ID
        Assert.assertNull(ncs);
    }

    @Test
    public void test_getVLANTable_nc() throws Exception {
        logger.info("<<< Enter test_getVLANTable() - normal case >>>");
        VLANTable table = vs.getVLANTable(testNodeGood);
        Assert.assertNotNull(table);
    }

    @Test
    public void test_getVLANTable_ec1() throws Exception {
        logger.info("<<< Enter test_getVLANTable() - error case 1 >>>");
        VLANTable table = vs.getVLANTable(testNodeBad);//give a non-existing node
        Assert.assertNull(table);
    }

}
