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
//import org.opendaylight.controller.protocol_plugin.cmethernet.vendorextension.v6extension.V6Match;

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.internal.DiscoveryService;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(DiscoveryServiceTest.class);

    public static void main(String args[]) {
        new DiscoveryServiceTest();
    }

    public DiscoveryServiceTest(){
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

    private void addNewSwitch(Node node, SwitchHandler sw, String chassisID, String switchIP){
        Long sid = HexString.toLong(chassisID);

        node = createSNMPNode(sid);

        sw = new SwitchHandler(controller, "");
        sw.setId(sid);
        sw.start();

        //controller.getCmethUtil().addEntry(sid, switchIP);
        controller.handleNewConnection(sid);
     }

    //@Test
    public void testDiscoveryService() throws UnknownHostException {
        controller = new Controller();
        controller.init_forTest();
        controller.start();

        Node node[] = new Node[5];
        SwitchHandler sw[] =new SwitchHandler[5];

        /*node[0] = NodeCreator.createESNode(HexString.toLong("70:72:CF:2A:87:41"));
        node[1] = NodeCreator.createESNode(HexString.toLong("90:94:E4:23:13:E0"));
        node[2] = NodeCreator.createESNode(HexString.toLong("90:94:E4:23:0B:00"));
        node[3] = NodeCreator.createESNode(HexString.toLong("90:94:E4:23:0B:20"));
        node[4] = NodeCreator.createESNode(HexString.toLong("90:94:E4:23:0A:E0"));*/


        /*//replace this part by addNewSwitch()
        node[0] = createSNMPNode(HexString.toLong("00:00:00:00:00:01"));
        node[1] = createSNMPNode(HexString.toLong("00:00:00:00:00:02"));
        node[2] = createSNMPNode(HexString.toLong("00:00:00:00:00:03"));
        node[3] = createSNMPNode(HexString.toLong("00:00:00:00:00:04"));
        node[4] = createSNMPNode(HexString.toLong("00:00:00:00:00:05"));
        for(int i = 0; i < 5; i++){
            sw[i] = new SwitchHandler(controller, "");
            sw[i].setId((Long)(node[i].getID()));
            sw[i].start();

            controller.addSwitch(sw[i]);
        }*/
        addNewSwitch(node[0], sw[0], "00:00:00:00:00:01", "10.217.0.31");
        addNewSwitch(node[1], sw[1], "00:00:00:00:00:02", "10.217.0.32");
        addNewSwitch(node[2], sw[2], "00:00:00:00:00:03", "10.217.0.33");
        addNewSwitch(node[3], sw[3], "00:00:00:00:00:04", "10.217.0.34");


        DiscoveryService ds = new DiscoveryService();
        ds.setController(controller);
        ds.doEthSwDiscovery();

    }
}
