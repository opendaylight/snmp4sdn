/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
//import org.opendaylight.controller.protocol_plugin.cmethernet.vendorextension.v6extension.V6Match;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.internal.DiscoveryService;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServiceTest {
    Controller controller = null;
    ConfigServiceImpl cfgs = null;
    Node testNodeGood = null;
    NodeConnector testPort1Good = null;
    NodeConnector testPort1Bad = null;
    Node testNodeBad = null;
    NodeConnector testPort2Good = null;
    NodeConnector testPort2Bad = null;
    protected static final Logger logger = LoggerFactory.getLogger(ConfigServiceTest.class);

    String sw_ipAddr = "192.168.0.33";
    String username = "admin";
    String password = "password";

    public static void main(String args[]) {
        new ConfigServiceTest();
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

    public ConfigServiceTest(){
        logger.info("====== ConfigServiceTest begin======");
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
    }

    //private void createNetworkAndService(){//before moving to each test case...
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
        mac = 158969157061408L;
            //node1: readlly exist node
        testNodeGood = createSNMPNode(mac);
            //switch1:
        SwitchHandler sw1 = new SwitchHandler(controller, "");
        //addNewSwitch(testNodeGood, sw1, (Long)(testNodeGood.getID()));//no use in ConfigServiceTest
            //port1_good
        testPort1Good = NodeConnectorCreator.createNodeConnector("SNMP", (short) 9, testNodeGood);
            //port1_bad: invalide port
        testPort1Bad = NodeConnectorCreator.createNodeConnector("SNMP", (short) 999, testNodeGood);

            //node2: fake node
        testNodeBad = createSNMPNode(9999L);
            //switch2:
        SwitchHandler sw2 = new SwitchHandler(controller, "");
        //mark line below due to junit test
        //addNewSwitch(testNodeBad, sw2, (Long)(testNodeBad.getID()));
            //port2_good
        testPort2Good = NodeConnectorCreator.createNodeConnector("SNMP", (short) 9, testNodeBad);
            //port: invalid port
        testPort2Bad = NodeConnectorCreator.createNodeConnector("SNMP", (short) 999, testNodeBad);

         /*
         * Create a Flow Programmer Service
         */
        cfgs = new ConfigServiceImpl();
        cfgs.setController(controller);
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: ConfigServiceTest: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
            return null;
        }
    }

    private void addNewSwitch(Node node, SwitchHandler sw, Long sid){
        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        //controller.handleNewConnection(sid);//no need to report info upward...
     }

    @Test
    public void test_disableSTP_10000times() throws UnknownHostException {
        logger.info("[Enter test_disableSTP() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSTP();
        for(int i = 0; i < 10000; i++){
            logger.info("Round " + i);
            status = cfgs.disableSTP(testNodeGood);
            Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        }
    }

    @Test
    public void test_disableSTP_nc() throws UnknownHostException {
        logger.info("[Enter test_disableSTP() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSTP();
        status = cfgs.disableSTP(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableSTP_ec() throws UnknownHostException {
        logger.info("[Enter test_disableSTP() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSTP();
        status = cfgs.disableSTP(testNodeBad);
        Assert.assertEquals(StatusCode.NOTFOUND.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableBpduFlooding_nc() throws UnknownHostException {
        logger.info("[Enter test_disableBpduFlooding() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
        status = cfgs.disableBpduFlooding(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableBpduFlooding_ec() throws UnknownHostException {
        logger.info("[Enter test_disableBpduFlooding() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
        status = cfgs.disableBpduFlooding(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableBpduFlooding_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableBpduFlooding_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((short)1);
        status = cfgs.disableBpduFlooding(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableBpduFlooding_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableBpduFlooding_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((short)1);
        status = cfgs.disableBpduFlooding(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableBpduFlooding_withPort_ec2() throws UnknownHostException {
        logger.info("[Enter test_disableBpduFlooding_withPort() - error case 2]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((short)1);
        status = cfgs.disableBpduFlooding(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableBroadcastFlooding_nc() throws UnknownHostException {
        logger.info("[Enter test_disableBroadcastFlooding() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
        status = cfgs.disableBroadcastFlooding(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        logger.info("End test_disableBroadcastFlooding()");
    }

    @Test
    public void test_disableBroadcastFlooding_ec() throws UnknownHostException {
        logger.info("[Enter test_disableBroadcastFlooding() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
        status = cfgs.disableBroadcastFlooding(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
        logger.info("End test_disableBroadcastFlooding()");
    }

    @Test
    public void test_disableBroadcastFlooding_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableBroadcastFlooding_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((short)2);
        status = cfgs.disableBroadcastFlooding(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableBroadcastFlooding_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableBroadcastFlooding_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((short)2);
        status = cfgs.disableBroadcastFlooding(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableBroadcastFlooding_withPort_ec2() throws UnknownHostException {
        logger.info("[Enter test_disableBroadcastFlooding_withPort() - error case 2]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((short)2);
        status = cfgs.disableBroadcastFlooding(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableMulticastFlooding_nc() throws UnknownHostException {
        logger.info("[Enter test_disableMulticastFlooding() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
        status = cfgs.disableMulticastFlooding(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableMulticastFlooding_ec() throws UnknownHostException {
        logger.info("[Enter test_disableMulticastFlooding() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
        status = cfgs.disableMulticastFlooding(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
        logger.info("End test_disableMulticastFlooding()");
    }

    @Test
    public void test_disableMulticastFlooding_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableMulticastFlooding_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((short)3);
        status = cfgs.disableMulticastFlooding(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        logger.info("End test_disableMulticastFlooding_withPort()");
    }

    @Test //give bad input: invalid node
    public void test_disableMulticastFlooding_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableMulticastFlooding_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((short)3);
        status = cfgs.disableMulticastFlooding(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableMulticastFlooding_withPort_ec2() throws UnknownHostException {
        logger.info("[Enter test_disableMulticastFlooding_withPort() - error case 2]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((short)3);
        status = cfgs.disableMulticastFlooding(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableUnknownFlooding_nc() throws UnknownHostException {
        logger.info("[Enter test_disableUnknownFlooding() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
        status = cfgs.disableUnknownFlooding(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        logger.info("End test_disableUnknownFlooding()");
    }

    @Test
    public void test_disableUnknownFlooding_ec() throws UnknownHostException {
        logger.info("[Enter test_disableUnknownFlooding() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
        status = cfgs.disableUnknownFlooding(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
        logger.info("End test_disableUnknownFlooding()");
    }

    @Test
    public void test_disableUnknownFlooding_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableUnknownFlooding_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((short)4);
        status = cfgs.disableUnknownFlooding(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableUnknownFlooding_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableUnknownFlooding_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((short)4);
        status = cfgs.disableUnknownFlooding(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableUnknownFlooding_withPort_ec2() throws UnknownHostException {
        logger.info("[Enter test_disableUnknownFlooding_withPort() - error case 2]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((short)4);
        status = cfgs.disableUnknownFlooding(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableSourceMacCheck_nc() throws UnknownHostException {
        logger.info("[Enter test_disableSourceMacCheck() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
        status = cfgs.disableSourceMacCheck(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        logger.info("End test_disableSourceMacCheck()");
    }

    @Test
    public void test_disableSourceMacCheck_ec() throws UnknownHostException {
        logger.info("[Enter test_disableSourceMacCheck() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
        status = cfgs.disableSourceMacCheck(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableSourceMacCheck_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableSourceMacCheck_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((short)5);
        status = cfgs.disableSourceMacCheck(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableSourceMacCheck_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableSourceMacCheck_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((short)5);
        status = cfgs.disableSourceMacCheck(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableSourceMacCheck_withPort_ec2() throws UnknownHostException {
        logger.info("[Enter test_disableSourceMacCheck_withPort() - error case 2]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((short)5);
        status = cfgs.disableSourceMacCheck(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test
    public void test_disableSourceLearning_nc() throws UnknownHostException {
        logger.info("[Enter test_disableSourceLearning() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
        status = cfgs.disableSourceLearning(testNodeGood);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        logger.info("End test_disableSourceLearning()");
    }

    @Test
    public void test_disableSourceLearning_ec() throws UnknownHostException {
        logger.info("[Enter test_disableSourceLearning() - error case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
        status = cfgs.disableSourceLearning(testNodeBad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
        logger.info("End test_disableSourceLearning()");
    }

    @Test
    public void test_disableSourceLearning_withPort_nc() throws UnknownHostException {
        logger.info("[Enter test_disableSourceLearning_withPort() - normal case]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((short)6);
        status = cfgs.disableSourceLearning(testNodeGood, testPort1Good);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid node
    public void test_disableSourceLearning_withPort_ec1() throws UnknownHostException {
        logger.info("[Enter test_disableSourceLearning_withPort() - error case 1]");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((short)6);
        status = cfgs.disableSourceLearning(testNodeBad, testPort2Good);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

    @Test //give bad input: invalid port
    public void test_disableSourceLearning_withPort() throws UnknownHostException {
        logger.info("[Enter test_disableSourceLearning_withPort()] - error case 2");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((short)6);
        status = cfgs.disableSourceLearning(testNodeGood, testPort1Bad);
        Assert.assertEquals(StatusCode.INTERNALERROR.toString(), status.getCode().toString());
    }

}
