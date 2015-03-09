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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
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
import org.opendaylight.controller.sal.core.Description;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
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
import org.opendaylight.snmp4sdn.internal.InventoryService;

import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

public class InventoryServiceTest {
    Controller controller = null;
    protected static final Logger logger = LoggerFactory.getLogger(InventoryServiceTest.class);
    InventoryService ivts = null;
    Node testNode1 = null;
    Node testNode2 = null;
    NodeConnector testPort1 = null;
    NodeConnector testPort2 = null;

    public static void main(String args[]) {
        new InventoryServiceTest();
    }

    @Before
     public void before() throws Exception {
         logger.info("before() 1");
         controller = new Controller();
         controller.init_forTest();
         controller.start();
         createNetworkAndService(controller);
         logger.info("before() 2");
     }
    
     @After
     public void after() {
         logger.info("after() 1");
         controller.stop();
         controller.destroy();
         logger.info("after() 2");
     }

    public InventoryServiceTest(){
        logger.info("====== InventoryServiceTest begin======");
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
        Long mac1 = null;
        Long mac2 = null;
        boolean mac1Given = false;
        boolean mac2Given = false;
        for (ConcurrentMap.Entry<Long, Vector> entry: table.entrySet()) {
            if(mac1Given == false){
                mac1 = entry.getKey();
                if(mac1 == null){
                    Assert.assertTrue("CmethUtil table size < 2!", false);
                }
                mac1Given = true;
                continue;
            }
            else{
                mac2 = entry.getKey();
                if(mac2 == null){
                    Assert.assertTrue("CmethUtil table size < 2!", false);
                }
                mac2Given = true;
                break;
            }
        }
        if(!mac1Given || !mac2Given)
            Assert.assertTrue("CmethUtil table size < 2!", false);

            //node:
        testNode1 = createSNMPNode(mac1);
        testNode2 = createSNMPNode(mac2);
            //switch:
        SwitchHandler sw1 = new SwitchHandler(controller, "sw1");
        addNewSwitch(testNode1, sw1, (Long)(testNode1.getID()));
        SwitchHandler sw2 = new SwitchHandler(controller, "sw2");
        //addNewSwitch(testNode2, sw2, (Long)(testNode2.getID()));
            //port:
        testPort1 = createNodeConnector( (short) 1, testNode1);
        testPort2 = createNodeConnector( (short) 1, testNode1);

         /*
         * Create a Inventory Service
         */
        ivts = new InventoryService();
        ivts.init_forTest();
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: InventoryServiceTest: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
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

    private void giveSomeNodeProp(){
        Set<Property> props = new HashSet<Property>();
        giveNodeProp(testNode1, props, "prop1-1");
        giveNodeProp(testNode1, props, "prop1-2");
        //giveNodeProp(testNode2, props, "prop2-1");
        //giveNodeProp(testNode2, props, "prop2-2");
    }

    private void giveNodeProp(Node node, Set<Property> props, String propName){
        props.add(new Description(propName));
        logger.debug("Node " + node.getID() + ": props.add(" + propName + ")");
        ivts.updateNode(node, UpdateType.ADDED, props);
    }

    private void giveSomeNodeConnectorProp(){
        Set<Property> props = new HashSet<Property>();
        giveNodeConnectorProp(testPort1, props, "nc1-prop1");
        giveNodeConnectorProp(testPort2, props, "nc2-prop1");
        giveNodeConnectorProp(testPort2, props, "nc2-prop2");
    }

    private void giveNodeConnectorProp(NodeConnector nodeConnector, Set<Property> props, String propName){
        props.add(new Description(propName));
        logger.debug("NodeConnector (Node " + (Long)(nodeConnector.getNode().getID()) + "'s port " + (Short)(nodeConnector.getID()) + ": props.add(" + propName + ")");
        ivts.updateNodeConnector(nodeConnector, UpdateType.ADDED, props);
    }

    @Test
    public void test_getNodeProps() throws Exception {
        logger.info("[Enter test_getNodeProps()]");
        giveSomeNodeProp();
        ConcurrentMap<Node, Map<String, Property>> nodeProps = ivts.getNodeProps();
        Assert.assertNotNull("error: node props given to InventoryService, but InventoryService.getNodeProps() returns null!", nodeProps);
    }

    @Test
    public void test_getNodeConnectorProps() throws Exception {
        logger.info("[Enter test_getNodeConnectorProps()]");
        giveSomeNodeConnectorProp();
        ConcurrentMap<NodeConnector, Map<String, Property>> nodeConnectorProps = ivts.getNodeConnectorProps(true);
        Assert.assertNotNull("error: nodeConnector props given to InventoryService, but InventoryService.getNodeConnectorProps() returns null!", nodeConnectorProps);
    }

}
