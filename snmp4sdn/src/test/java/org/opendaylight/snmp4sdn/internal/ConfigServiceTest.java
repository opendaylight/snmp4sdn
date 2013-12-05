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
import org.opendaylight.snmp4sdn.internal.DiscoveryService;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServiceTest {
    Controller controller = null;
    ConfigService cfgs = null;
    Node testNode = null;
    NodeConnector testPort = null;
    protected static final Logger logger = LoggerFactory.getLogger(DiscoveryServiceTest.class);

    String sw_ipAddr = "10.217.0.32";
    String username = "admin";
    String password = "password";

    public static void main(String args[]) {
        new DiscoveryServiceTest();
    }

    public ConfigServiceTest(){
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
        createNetworkAndService();
        /*
        try {
            testDiscoveryService();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        */
    }

    private void createNetworkAndService(){
        /*
         *Create a testing network
        */
            //controller:
        controller = new Controller();
        controller.init_forTest();
        controller.start();
            //node:
        testNode = null;
        testNode = createSNMPNode(2L);
            //switch:
        SwitchHandler sw = new SwitchHandler(controller, "");
        addNewSwitch(testNode, sw, (Long)(testNode.getID()));
            //port:
        testPort = NodeConnectorCreator.createNodeConnector("SNMP", (short) 1, testNode);

         /*
         * Create a Flow Programmer Service
         */
        cfgs = new ConfigService();
        cfgs.setController(controller);
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    private void addNewSwitch(Node node, SwitchHandler sw, Long sid){
        node = createSNMPNode(sid);

        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        controller.handleNewConnection(sid);
     }

    ////@Test
    public void test_disableSTP() throws UnknownHostException {
        System.out.println("Enter test_disableSTP()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSTP();
        status = cfgs.disableSTP(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableBpduFlooding() throws UnknownHostException {
        System.out.println("Enter test_disableBpduFlooding()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
        status = cfgs.disableBpduFlooding(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableBpduFlooding()");
    }

    //@Test
    public void test_disableBpduFlooding_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableBpduFlooding_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((short)1);
        status = cfgs.disableBpduFlooding(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableBpduFlooding_withPort()");
    }

    //@Test
    public void test_disableBroadcastFlooding() throws UnknownHostException {
        System.out.println("Enter test_disableBroadcastFlooding()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
        status = cfgs.disableBroadcastFlooding(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableBroadcastFlooding()");
    }

   //@Test
    public void test_disableBroadcastFlooding_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableBroadcastFlooding_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((short)2);
        status = cfgs.disableBroadcastFlooding(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableBroadcastFlooding_withPort()");
    }

  //@Test
    public void test_disableMulticastFlooding() throws UnknownHostException {
        System.out.println("Enter test_disableMulticastFlooding()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
        status = cfgs.disableMulticastFlooding(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableMulticastFlooding()");
    }

   //@Test
    public void test_disableMulticastFlooding_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableMulticastFlooding_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((short)3);
        status = cfgs.disableMulticastFlooding(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableMulticastFlooding_withPort()");
    }

   //@Test
    public void test_disableUnknownFlooding() throws UnknownHostException {
        System.out.println("Enter test_disableUnknownFlooding()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
        status = cfgs.disableUnknownFlooding(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableUnknownFlooding()");
    }

   //@Test
    public void test_disableUnknownFlooding_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableUnknownFlooding_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((short)4);
        status = cfgs.disableUnknownFlooding(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableUnknownFlooding_withPort()");
    }
    
   //@Test
    public void test_disableSourceMacCheck() throws UnknownHostException {
        System.out.println("Enter test_disableSourceMacCheck()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
        status = cfgs.disableSourceMacCheck(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableSourceMacCheck()");
    }

   //@Test
    public void test_disableSourceMacCheck_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableSourceMacCheck_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((short)5);
        status = cfgs.disableSourceMacCheck(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableSourceMacCheck_withPort()");
    }

   //@Test
    public void test_disableSourceLearning() throws UnknownHostException {
        System.out.println("Enter test_disableSourceLearning()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
        status = cfgs.disableSourceLearning(testNode);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        System.out.println("End test_disableSourceLearning()");
    }

   //@Test
    public void test_disableSourceLearning_withPort() throws UnknownHostException {
        System.out.println("Enter test_disableSourceLearning_withPort()");
        Status status;
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((short)6);
        status = cfgs.disableSourceLearning(testNode, testPort);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());  
        System.out.println("End test_disableSourceLearning_withPort()");
    }


    //////@Test //this test covers all the tests
    public void test_AllTests() throws UnknownHostException {
        Status status;

        /*//status = new CLIHandler(sw_ipAddr, username, password).disableSTP();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());*/

        status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());

        //status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((short)1);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((short)2);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((short)3);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((short)4);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((short)5);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        //status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((short)6);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());        
    }
}
