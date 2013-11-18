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
    protected static final Logger logger = LoggerFactory.getLogger(DiscoveryServiceTest.class);

    String sw_ipAddr = "10.217.0.32";
    String username = "admin";
    String password = "password";

    public static void main(String args[]) {
        new DiscoveryServiceTest();
    }

    public ConfigServiceTest(){
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Long.class, "SNMP");
        /*
        try {
            testDiscoveryService();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        */
    }

    public static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    private void addNewSwitch(Node node, SwitchHandler sw, String chassisID){
        Long sid = HexString.toLong(chassisID);

        node = createSNMPNode(sid);

        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        controller.handleNewConnection(sid);
     }

    ////@Test
    public void test_disableSTP() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableSTP(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableBpduFlooding() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());   
    }

    //@Test
    public void test_disableBpduFlooding_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding(sw_ipAddr, (short)1, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableBroadcastFlooding() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableBroadcastFlooding_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding(sw_ipAddr, (short)2, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableMulticastFlooding() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString()); 
    }

    //@Test
    public void test_disableMulticastFlooding_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding(sw_ipAddr, (short)3, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableUnknownFlooding() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableUnknownFlooding_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding(sw_ipAddr, (short)4, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }
    
    //@Test
    public void test_disableSourceMacCheck() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableSourceMacCheck_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck(sw_ipAddr, (short)5, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableSourceLearning() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    //@Test
    public void test_disableSourceLearning_withPort() throws UnknownHostException {
        Status status;
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning(sw_ipAddr, (short)6, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());  
    }


    ////@Test //this test covers all the tests
    public void test_AllTests() throws UnknownHostException {
        Status status;

        /*status = new CLIHandler(sw_ipAddr, username, password).disableSTP(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());*/

        status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());

        status = new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding(sw_ipAddr, (short)1, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding(sw_ipAddr, (short)2, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding(sw_ipAddr, (short)3, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding(sw_ipAddr, (short)4, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck(sw_ipAddr, (short)5, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning(sw_ipAddr, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
        
        status = new CLIHandler(sw_ipAddr, username, password).disableSourceLearning(sw_ipAddr, (short)6, username, password);
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());        
    }
}
