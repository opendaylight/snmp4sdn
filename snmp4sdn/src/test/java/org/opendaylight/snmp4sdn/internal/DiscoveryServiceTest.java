/*
 * Copyright (c) 2013 Industrial Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.protocol_plugin.cmethernet.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Output;
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

import org.opendaylight.controller.protocol_plugin.cmethernet.core.internal.Controller;
import org.opendaylight.controller.protocol_plugin.cmethernet.core.internal.SwitchHandler;
import org.opendaylight.controller.protocol_plugin.cmethernet.internal.DiscoveryService;

import org.opendaylight.controller.protocol_plugin.cmethernet.eth.util.HexString;

public class DiscoveryServiceTest {
    public static void main(String args[]) {
        new DiscoveryServiceTest();
    }

    public DiscoveryServiceTest(){
        try {
            testDiscoveryService();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDiscoveryService() throws UnknownHostException {
        Controller controller = new Controller();
        controller.init();
        controller.start();

        Node node[] = new Node[5];
        SwitchHandler sw[] =new SwitchHandler[5];

        node[0] = NodeCreator.createESNode(HexString.toLong("00:00:00:00:00:01"));
        node[1] = NodeCreator.createESNode(HexString.toLong("00:00:00:00:00:02"));
        node[2] = NodeCreator.createESNode(HexString.toLong("00:00:00:00:00:03"));
        node[3] = NodeCreator.createESNode(HexString.toLong("00:00:00:00:00:04"));
        node[4] = NodeCreator.createESNode(HexString.toLong("00:00:00:00:00:05"));
        for(int i = 0; i < 5; i++){
            sw[i] = new SwitchHandler(controller, null, "");
            sw[i].setId((Long)(node[i].getID()));
            sw[i].start();

            controller.addSwitch(sw[i]);
        }

        DiscoveryService ds = new DiscoveryService();
        ds.setController(controller);
        ds.doEthSwDiscovery();

    }
}
