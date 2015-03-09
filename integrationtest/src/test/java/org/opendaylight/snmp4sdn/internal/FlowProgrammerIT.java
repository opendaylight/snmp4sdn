/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.utils.Status;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

//s4s add all the following packages
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//import org.junit.Assert;//listed above
//import org.junit.Test;//listed above
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IPluginInFlowProgrammerService;
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



@RunWith(PaxExam.class)
public class FlowProgrammerIT {
    private Logger log = LoggerFactory.getLogger(FlowProgrammerIT.class);
    // get the OSGI bundle context
    @Inject
    private BundleContext bc;

    private IPluginInFlowProgrammerService flowprogrammer = null;//s4s

    // Configure the OSGi container
    @Configuration
    public Option[] config() {
        return options(
                //
                systemProperty("logback.configurationFile").value(
                        "file:" + PathUtils.getBaseDir() + "/src/test/resources/logback.xml"),
                // To start OSGi console for inspection remotely
                systemProperty("osgi.console").value("2401"),
                // Set the systemPackages (used by clustering)
                systemPackages("sun.reflect", "sun.reflect.misc", "sun.misc"),
                // List framework bundles
                mavenBundle("equinoxSDK381", "org.eclipse.equinox.console").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.eclipse.equinox.util").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.eclipse.osgi.services").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.eclipse.equinox.ds").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.apache.felix.gogo.command").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.apache.felix.gogo.runtime").versionAsInProject(),
                mavenBundle("equinoxSDK381", "org.apache.felix.gogo.shell").versionAsInProject(),
                // List logger bundles
                mavenBundle("org.slf4j", "slf4j-api").versionAsInProject(),
                mavenBundle("org.slf4j", "log4j-over-slf4j").versionAsInProject(),
                mavenBundle("ch.qos.logback", "logback-core").versionAsInProject(),
                mavenBundle("ch.qos.logback", "logback-classic").versionAsInProject(),

                // List all the bundles on which the test case depends
                mavenBundle("org.opendaylight.controller", "sal").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "sal.implementation").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "snmp4sdn").versionAsInProject(),//s4s add
                /* //s4s marked
                mavenBundle("org.opendaylight.controller", "sal.connection").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "sal.connection.implementation").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "connectionmanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "connectionmanager.implementation").versionAsInProject(),

                // needed by statisticsmanager
                mavenBundle("org.opendaylight.controller", "containermanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "containermanager.it.implementation").versionAsInProject(),

                mavenBundle("org.opendaylight.controller", "clustering.services").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "clustering.stub").versionAsInProject(),

                // needed by forwardingrulesmanager
                mavenBundle("org.opendaylight.controller", "switchmanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "forwardingrulesmanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "statisticsmanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "switchmanager.implementation").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "configuration").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "configuration.implementation").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "hosttracker").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "hosttracker.implementation").versionAsInProject(),

                // needed by hosttracker
                mavenBundle("org.opendaylight.controller", "topologymanager").versionAsInProject(),
                mavenBundle("org.opendaylight.controller", "arphandler").versionAsInProject(),
                */
                mavenBundle("org.jboss.spec.javax.transaction", "jboss-transaction-api_1.1_spec").versionAsInProject(),
                mavenBundle("org.apache.commons", "commons-lang3").versionAsInProject(),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager").versionAsInProject(),
                junitBundles());
    }

    private String stateToString(int state) {
        switch (state) {
        case Bundle.ACTIVE:
            return "ACTIVE";
        case Bundle.INSTALLED:
            return "INSTALLED";
        case Bundle.RESOLVED:
            return "RESOLVED";
        case Bundle.UNINSTALLED:
            return "UNINSTALLED";
        default:
            return "Not CONVERTED";
        }
    }

    @Before
    public void areWeReady() {
        assertNotNull(bc);
        boolean debugit = false;
        Bundle b[] = bc.getBundles();
        for (int i = 0; i < b.length; i++) {
            int state = b[i].getState();
            if (state != Bundle.ACTIVE && state != Bundle.RESOLVED) {
                log.debug("Bundle:" + b[i].getSymbolicName() + " state:" + stateToString(state));
                debugit = true;
            }
        }
        if (debugit) {
            log.debug("Do some debugging because some bundle is " + "unresolved");
        }

        // Assert if true, if false we are good to go!
        assertFalse(debugit);

        // Now lets create a hosttracker for testing purpose
        ServiceReference r = bc.getServiceReference(IPluginInFlowProgrammerService.class.getName());//s4s
        if (r != null) {
            this.flowprogrammer= (IPluginInFlowProgrammerService) bc.getService(r);//s4s
        }

        // If FlowProgrammer is null, cannot run tests.
        assertNotNull(this.flowprogrammer);//s4s
    }

    public Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            log.error("",e1);
            return null;
        }
    }

    public NodeConnector createNodeConnector(Object portId, Node node) {
        if (node.getType().equals("SNMP")) {
            try {
                return new NodeConnector("SNMP",
                        (Short) portId, node);
            } catch (ConstructionException e1) {
                log.error("",e1);
                return null;
            }
        }
        return null;
    }

    @Test
    public void testAddandReadFlow() throws UnknownHostException {
        // create a node
        Node node = createSNMPNode(new Long(1000l));//cmeth.  here 1000l: not 10001, is 1000L
        NodeConnector iport = createNodeConnector( (short) 1, node);
        NodeConnector oport = createNodeConnector( (short) 30, node);//cmeth
        byte srcMac[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
        byte dstMac[] = { (byte) 0x70, (byte) 0x72, (byte) 0xCF, (byte) 0x2B, (byte) 0x95, (byte) 0x24 };//cmeth. in 10 base format = 112.114.207.43.149.36
        short ethertype = EtherTypes.IPv4.shortValue();

        // create a SAL Flow
        Match match = new Match();
        match.setField(MatchType.DL_SRC, srcMac);
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_TYPE, ethertype);
        Assert.assertTrue(match.isIPv4());
        List<Action> actions = new ArrayList<Action>();

        //test AddFlow
        Flow flow = new Flow(match, actions);
        Status st = flowprogrammer.addFlow(node, flow);
        Assert.assertTrue(st.isSuccess());

        /* //s4s marked
        // test addStaticHost(), store into inactive host DB
        Status st = this.hosttracker.addStaticHost("192.168.0.8", "11:22:33:44:55:66", nc1_1, null);
        Assert.assertTrue(st.isSuccess());
        st = this.hosttracker.addStaticHost("192.168.0.13", "11:22:33:44:55:77", nc1_2, "");
        Assert.assertTrue(st.isSuccess());

        // check inactive DB
        Iterator<HostNodeConnector> hnci = this.hosttracker.getInactiveStaticHosts().iterator();
        while (hnci.hasNext()) {
            ip = hnci.next().getNetworkAddressAsString();
            Assert.assertTrue(ip.equals("192.168.0.8") || ip.equals("192.168.0.13"));
        }

        // check active host DB
        hnci = this.hosttracker.getActiveStaticHosts().iterator();
        Assert.assertFalse(hnci.hasNext());

        // test removeStaticHost()
        st = this.hosttracker.removeStaticHost("192.168.0.8");
        Assert.assertTrue(st.isSuccess());

        hnci = this.hosttracker.getInactiveStaticHosts().iterator();
        while (hnci.hasNext()) {
            ip = hnci.next().getNetworkAddressAsString();
            Assert.assertTrue(ip.equals("192.168.0.13"));
        }*/
    }

    /* //s4s marked@Test
    public void testNotifyNodeConnector() throws UnknownHostException {
        assertNotNull(this.flowprogrammer);

        // create one node and two node connectors
        Node node1 = NodeCreator.createOFNode(1L);
        NodeConnector nc1_1 = NodeConnectorCreator.createOFNodeConnector((short) 1, node1);
        NodeConnector nc1_2 = NodeConnectorCreator.createOFNodeConnector((short) 2, node1);

        // test addStaticHost(), put into inactive host DB if not verifiable
        Status st = this.hosttracker.addStaticHost("192.168.0.8", "11:22:33:44:55:66", nc1_1, null);
        Assert.assertTrue(st.isSuccess());
        st = this.hosttracker.addStaticHost("192.168.0.13", "11:22:33:44:55:77", nc1_2, "0");
        Assert.assertFalse(st.isSuccess());


        this.invtoryListener.notifyNodeConnector(nc1_1, UpdateType.ADDED, null);

        // check all host list
        Iterator<HostNodeConnector> hnci = this.hosttracker.getAllHosts().iterator();
        while (hnci.hasNext()) {
            ip = hnci.next().getNetworkAddressAsString();
            Assert.assertTrue(ip.equals("192.168.0.8"));
        }

        // check active host DB
        hnci = this.hosttracker.getActiveStaticHosts().iterator();
        while (hnci.hasNext()) {
            ip = hnci.next().getNetworkAddressAsString();
            Assert.assertTrue(ip.equals("192.168.0.8"));
        }

        // check inactive host DB
        hnci = this.hosttracker.getInactiveStaticHosts().iterator();
        while (hnci.hasNext()) {
            ip = hnci.next().getNetworkAddressAsString();
            Assert.assertTrue(ip.equals("192.168.0.13"));
        }
    }

    @Test
    public void testHostFind() throws UnknownHostException {

        assertNotNull(this.invtoryListener);

        // create one node and two node connectors
        Node node1 = NodeCreator.createOFNode(1L);
        NodeConnector nc1_1 = NodeConnectorCreator.createOFNodeConnector((short) 1, node1);
        NodeConnector nc1_2 = NodeConnectorCreator.createOFNodeConnector((short) 2, node1);

        // test addStaticHost(), put into inactive host DB if not verifiable
        Status st = this.hosttracker.addStaticHost("192.168.0.8", "11:22:33:44:55:66", nc1_1, null);
        st = this.hosttracker.addStaticHost("192.168.0.13", "11:22:33:44:55:77", nc1_2, "");

        HostNodeConnector hnc_1 = this.hosttracker.hostFind(InetAddress.getByName("192.168.0.8"));
        assertNull(hnc_1);

        this.invtoryListener.notifyNodeConnector(nc1_1, UpdateType.ADDED, null);

        hnc_1 = this.hosttracker.hostFind(InetAddress.getByName("192.168.0.8"));
        assertNotNull(hnc_1);

    }*/

}
