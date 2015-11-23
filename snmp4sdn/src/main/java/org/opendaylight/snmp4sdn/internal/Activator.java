/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.dm.Component;
/*import org.opendaylight.controller.protocol_plugin.openflow.IDataPacketListen;
import org.opendaylight.controller.protocol_plugin.openflow.IDataPacketMux;
import org.opendaylight.controller.protocol_plugin.openflow.IDiscoveryListener;
import org.opendaylight.controller.protocol_plugin.openflow.IFlowProgrammerNotifier;
import org.opendaylight.controller.protocol_plugin.openflow.IInventoryProvider;
import org.opendaylight.controller.protocol_plugin.openflow.IInventoryShimExternalListener;
import org.opendaylight.controller.protocol_plugin.openflow.IInventoryShimInternalListener;
import org.opendaylight.controller.protocol_plugin.openflow.IOFStatisticsListener;
import org.opendaylight.controller.protocol_plugin.openflow.IOFStatisticsManager;
import org.opendaylight.controller.protocol_plugin.openflow.IReadFilterInternalListener;
import org.opendaylight.controller.protocol_plugin.openflow.IReadServiceFilter;
import org.opendaylight.controller.protocol_plugin.openflow.IRefreshInternalProvider;
import org.opendaylight.controller.protocol_plugin.openflow.ITopologyServiceShimListener;
import org.opendaylight.controller.protocol_plugin.openflow.core.IController;
import org.opendaylight.controller.protocol_plugin.openflow.core.IMessageListener;
import org.opendaylight.controller.protocol_plugin.openflow.core.internal.Controller;*/
    import org.opendaylight.snmp4sdn.ICore;//karaf
    import org.opendaylight.snmp4sdn.IDataPacketListen;
    import org.opendaylight.snmp4sdn.IDataPacketMux;
    import org.opendaylight.snmp4sdn.IDiscoveryListener;
    import org.opendaylight.snmp4sdn.IFlowProgrammerNotifier;
    import org.opendaylight.snmp4sdn.IInventoryProvider;
    import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;
    import org.opendaylight.snmp4sdn.IInventoryShimInternalListener;
    import org.opendaylight.snmp4sdn.IOFStatisticsManager;
    import org.opendaylight.snmp4sdn.IPluginReadServiceFilter;
    import org.opendaylight.snmp4sdn.IRefreshInternalProvider;
    import org.opendaylight.snmp4sdn.IStatisticsListener;
    import org.opendaylight.snmp4sdn.ITopologyServiceShimListener;
    import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;
    import org.opendaylight.snmp4sdn.core.IController;
    import org.opendaylight.snmp4sdn.core.IMessageListener;
    import org.opendaylight.snmp4sdn.core.internal.Controller;
/*import org.opendaylight.controller.sal.connection.IPluginInConnectionService;
import org.opendaylight.controller.sal.connection.IPluginOutConnectionService;*///s4s cs
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.sal.core.IContainerAware;
import org.opendaylight.controller.sal.core.IContainerListener;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.flowprogrammer.IPluginInFlowProgrammerService;
import org.opendaylight.controller.sal.flowprogrammer.IPluginOutFlowProgrammerService;
import org.opendaylight.controller.sal.inventory.IPluginInInventoryService;
import org.opendaylight.controller.sal.inventory.IPluginOutInventoryService;
import org.opendaylight.controller.sal.packet.IPluginInDataPacketService;
import org.opendaylight.controller.sal.packet.IPluginOutDataPacketService;
import org.opendaylight.controller.sal.reader.IPluginInReadService;
import org.opendaylight.controller.sal.reader.IPluginOutReadService;
import org.opendaylight.controller.sal.topology.IPluginInTopologyService;
import org.opendaylight.controller.sal.topology.IPluginOutTopologyService;
import org.opendaylight.controller.sal.utils.GlobalConstants;
import org.opendaylight.controller.sal.utils.INodeConnectorFactory;//s4s test
import org.opendaylight.controller.sal.utils.INodeFactory;//s4s test
//import org.opendaylight.controller.sal.vlan.IPluginInVLANService;//s4s//ad-sal
//import org.opendaylight.snmp4sdn.IConfigService;//no-sal
//import org.opendaylight.snmp4sdn.IVLANService;//no-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanService;//md-sal
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;//md-sal
import org.osgi.framework.BundleContext;//md-sal

/**
 * SNMP4SDN plugin Activator
 *
 *
 */
public class Activator extends ComponentActivatorAbstractBase/*, AbstractBindingAwareProvidermd-sal*/ {
    protected static final Logger logger = LoggerFactory
            .getLogger(Activator.class);

    private ConfigProvider config = new ConfigProvider();//md-sal
    private FdbProvider fdb = new FdbProvider();//md-sal
    private AclProvider acl = new AclProvider();//md-sal
    private VlanProvider vlan = new VlanProvider();//md-sal
    private SwitchDbProvider switchdb = new SwitchDbProvider();//md-sal
    private TopologyProvider topo = new TopologyProvider();//md-sal
 
    /**
     * Function called when the activator starts just after some initializations
     * are done by the ComponentActivatorAbstractBase.
     *
     */
    public void init() {
        /*
         * KEYWD: ITRI_PEREGRINE_SNMP4SDN_Activator_RegisterNode&NodeConnectorOfTypeSNMP
         *         OpenDaylight controller had defined several 'type', such as OF (i.e. OpenFlow) ONEPK etc.
         *         If you'd like to add other custom types, it provides the 'registerIDType()' approach, as below, to manually add new type of Node and NodeConnector
         *  Author: Yi-Ling Hsieh
         */System.out.println("i");
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
        //cancelled->TODO: Issue: is code architecture okay in this way?: the code above is moved to Controller.init(), because SNMP type is used during Topology Discovery but here Activator.init() is the latest code to be called in snmp4sdn start up (start up means all modules creation, initialization, start... and we also put Topology Discovery in InventoryServiceShim's start).
    }

    /**
     * Function called when the activator stops just before the cleanup done by
     * ComponentActivatorAbstractBase
     *
     */
    public void destroy() {
        /*
         * KEYWD: ITRI_PEREGRINE_SNMP4SDN_Activator_RegisterNode&NodeConnectorOfTypeSNMP
         *         OpenDaylight controller had defined several 'type', such as OF (i.e. OpenFlow) ONEPK etc.
         *         If you'd like to add other custom types, it provides the 'registerIDType()' approach, as shown in the init() above, to manually add new type of Node and NodeConnector
                    To un-register the 'type', you can use 'unRegisterIDType()' as below.
         *  Author: Yi-Ling Hsieh
         */
        Node.NodeIDType.unRegisterIDType("SNMP");
        NodeConnector.NodeConnectorIDType.unRegisterIDType("SNMP");

        config.close();//md-sal
        fdb.close();//md-sal
        acl.close();//md-sal
        vlan.close();//md-sal
        switchdb.close();//md-sal
        topo.close();//md-sal
    }

    @Override
    public void start(BundleContext arg0) {//md-sal
        super.start(arg0);
        config.setContext(arg0);
        fdb.setContext(arg0);
        acl.setContext(arg0);
        vlan.setContext(arg0);
        switchdb.setContext(arg0);
        topo.setContext(arg0);System.out.println("j");
    }

    /**
     * Function that is used to communicate to dependency manager the list of
     * known implementations for services inside a container
     *
     *
     * @return An array containing all the CLASS objects that will be
     *         instantiated in order to get an fully working implementation
     *         Object
     */
    @Override
    public Object[] getImplementations() {
        Object[] res = { TopologyServices.class, DataPacketServices.class,
                InventoryService.class, ReadService.class,
                FlowProgrammerNotifier.class,
                VLANService.class
                };
        return res;
    }

    /**
     * Function that is called when configuration of the dependencies is
     * required.
     *
     * @param c
     *            dependency manager Component object, used for configuring the
     *            dependencies exported and imported
     * @param imp
     *            Implementation class that is being configured, needed as long
     *            as the same routine can configure multiple implementations
     * @param containerName
     *            The containerName being configured, this allow also optional
     *            per-container different behavior if needed, usually should not
     *            be the case though.
     */
    @Override
    public void configureInstance(Component c, Object imp, String containerName) {System.out.println("containerName="+containerName);
        if (imp.equals(TopologyServices.class)) {
            // export the service to be used by SAL
            c.setInterface(
                    new String[] { IPluginInTopologyService.class.getName(),
                            ITopologyServiceShimListener.class.getName() }, null);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutTopologyService.class)
                    .setCallbacks("setPluginOutTopologyService",
                            "unsetPluginOutTopologyService").setRequired(false));*///remove ad-sal
            c.add(createServiceDependency()
                    .setService(IRefreshInternalProvider.class)
                    .setCallbacks("setRefreshInternalProvider",
                            "unsetRefreshInternalProvider").setRequired(false));
        }

        if (imp.equals(InventoryService.class)) {
            // export the service
            c.setInterface(
                    new String[] {
                            IPluginInInventoryService.class.getName(),
                            IInventoryShimInternalListener.class.getName(),
                            IInventoryProvider.class.getName() }, null);

            // Now lets add a service dependency to make sure the
            // provider of service exists
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutInventoryService.class)
                    .setCallbacks("setPluginOutInventoryServices",
                            "unsetPluginOutInventoryServices")
                    .setRequired(false));*///remove ad-sal
        }

        if (imp.equals(DataPacketServices.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(IPluginInDataPacketService.class.getName(), props);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            // This is required for the transmission to happen properly
            c.add(createServiceDependency().setService(IDataPacketMux.class)
                    .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux")
                    .setRequired(true));
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutDataPacketService.class)
                    .setCallbacks("setPluginOutDataPacketService",
                            "unsetPluginOutDataPacketService")
                    .setRequired(false));*///remove ad-sal
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(ReadService.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(new String[] {
                    /*IReadFilterInternalListener.class.getName(),*///s4s read
                    IPluginInReadService.class.getName() }, props);

            /*c.add(createServiceDependency()
                    .setService(IReadServiceFilter.class)
                    .setCallbacks("setService", "unsetService")
                    .setRequired(true));*///s4s read

            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutReadService.class)
                    .setCallbacks("setPluginOutReadServices",
                            "unsetPluginOutReadServices")
                    .setRequired(false));*///remove ad-sal

            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(FlowProgrammerNotifier.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(IFlowProgrammerNotifier.class.getName(), props);

            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutFlowProgrammerService.class)
                    .setCallbacks("setPluginOutFlowProgrammerService",
                            "unsetPluginOutFlowProgrammerService")
                    .setRequired(true));*///remove ad-sal
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }
        if (imp.equals(VLANService.class)) {
            //c.setInterface(IPluginInVLANService.class.getName(), null);//ad-sal
            //c.setInterface(IVLANService.class.getName(), null);//no-sal
            //c.setInterface(VlanService.class.getName(), null);//md-sal

            c.add(createServiceDependency()
                    .setService(IController.class)
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
        }
    }

    /**
     * Function that is used to communicate to dependency manager the list of
     * known implementations for services that are container independent.
     *
     *
     * @return An array containing all the CLASS objects that will be
     *         instantiated in order to get an fully working implementation
     *         Object
     */
    @Override
    public Object[] getGlobalImplementations() {
        Object[] res = { Controller.class, OFStatisticsManager.class,
                FlowProgrammerService.class, ReadServiceFilter.class,
                /*DiscoveryService.class,*/ DataPacketMuxDemux.class, InventoryService.class,
                /*InventoryServiceShim.class,*/ TopologyServiceShim.class,
                NodeFactory.class, NodeConnectorFactory.class,//s4s test: add this line
                //move the following 43lines from "getImplementations()" to here, except for InventoryService (already have one here) and VLANService (already have md vlan here).
                TopologyServices.class, DataPacketServices.class,
                ReadService.class,
                FlowProgrammerNotifier.class,
                //ConfigService.class,//ad-sal or no-sal
                DiscoveryService.class, InventoryServiceShim.class,//put DiscoveryService and InventoryServiceShim in the last, because the interfaces they need are exposed in previous classes
                config,//md-sal
                fdb,//md-sal
                acl,//md-sal
                vlan,//md-sal
                switchdb,//md-sal
                topo//md-sal
                };
        System.out.println("getGlobalImplementations() done");
        return res;
    }

    /**
     * Function that is called when configuration of the dependencies is
     * required.
     *
     * @param c
     *            dependency manager Component object, used for configuring the
     *            dependencies exported and imported
     * @param imp
     *            Implementation class that is being configured, needed as long
     *            as the same routine can configure multiple implementations
     */
    @Override
    public void configureGlobalInstance(Component c, Object imp) {
        logger.debug("snmp4sdn: Activator configureGlobalInstance( ) is called");

        //md-sal (the following items: config, fdb, acl, vlan, switchdb, topo)
        if (imp == config) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for ConfigService");
        }
        if (imp == fdb) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for FdbService");
        }
        if (imp == acl) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for AclService");
        }
        if (imp == vlan) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for VlanService");
        }
        if (imp == switchdb) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for SwitchDb");
        }
        if (imp == topo) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(DiscoveryServiceAPI.class/*, "(name=XXX)"*/)/*Memo: name=XXX was given, then setService() fails!*/
                    .setCallbacks("setDiscoveryService", "setDiscoveryService").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and DiscoveryServiceAPI, for TopologyService");
        }

        if (imp.equals(Controller.class)) {System.out.println("0");
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("name", "Controller");
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(new String[] { IController.class.getName(),
                                          /*IPluginInConnectionService.class.getName()*///s4s cs
                                          ICore.class.getName()//karaf
                                          },
                                          props);
        }

        if (imp.equals(FlowProgrammerService.class)) {System.out.println("1");
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(
                    new String[] { IPluginInFlowProgrammerService.class.getName(), /*IMessageListener.class.getName(),*/
                            IContainerListener.class.getName(), IInventoryShimExternalListener.class.getName(),
                            IContainerAware.class.getName() }, props);System.out.println("2");

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));System.out.println("3");

            c.add(createServiceDependency()
                    .setService(IFlowProgrammerNotifier.class)
                    .setCallbacks("setFlowProgrammerNotifier",
                            "unsetsetFlowProgrammerNotifier")
                    .setRequired(false));System.out.println("4");

            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(ReadServiceFilter.class)) {System.out.println("5");

            c.setInterface(new String[] { /*IReadServiceFilter.class.getName(), *///s4s read
                IContainerListener.class.getName(),
                    /*IOFStatisticsListener.class.getName(), *///s4s statistics
                    IContainerAware.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IOFStatisticsManager.class)
                    .setCallbacks("setService", "unsetService")
                    .setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IReadFilterInternalListener.class)
                    .setCallbacks("setReadFilterInternalListener",
                            "unsetReadFilterInternalListener")
                    .setRequired(false));*///s4s read
        }

        if (imp.equals(OFStatisticsManager.class)) {System.out.println("6");

            c.setInterface(new String[] { IOFStatisticsManager.class.getName(),
                    IInventoryShimExternalListener.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IOFStatisticsListener.class)
                    .setCallbacks("setStatisticsListener",
                            "unsetStatisticsListener").setRequired(false));*///s4s statistics
        }

        if (imp.equals(DiscoveryService.class)) {System.out.println("a");
            // export the service
            c.setInterface(
                    new String[] { IInventoryShimExternalListener.class.getName(), IDataPacketListen.class.getName(),
                            IContainerListener.class.getName(), DiscoveryServiceAPI.class.getName() }, null);System.out.println("b");

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));System.out.println("c");
            c.add(/*createContainerServiceDependency(
                    GlobalConstants.DEFAULT.toString())*///bug fix: the "setInventoryPorvider" will not be called, so we try replace this argument by the following createServiceDependency()
                    createServiceDependency()
                    .setService(IInventoryProvider.class)
                    .setCallbacks("setInventoryProvider",
                    "unsetInventoryProvider").setRequired(true));System.out.println("d");
            c.add(createServiceDependency().setService(IDataPacketMux.class)
                    .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux")
                    .setRequired(true));System.out.println("e");
            c.add(createServiceDependency()
                    .setService(IDiscoveryListener.class)
                    .setCallbacks("setDiscoveryListener",
                            "unsetDiscoveryListener").setRequired(true));System.out.println("f");
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        // DataPacket mux/demux services, which is teh actual engine
        // doing the packet switching
        if (imp.equals(DataPacketMuxDemux.class)) {System.out.println("7");
            c.setInterface(new String[] { IDataPacketMux.class.getName(), IContainerListener.class.getName(),
                    IInventoryShimExternalListener.class.getName(), IContainerAware.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IPluginOutDataPacketService.class)
                    .setCallbacks("setPluginOutDataPacketService",
                            "unsetPluginOutDataPacketService")
                    .setRequired(false));*///remove ad-sal
            // See if there is any local packet dispatcher
            c.add(createServiceDependency()
                    .setService(IDataPacketListen.class)
                    .setCallbacks("setIDataPacketListen",
                            "unsetIDataPacketListen").setRequired(false));
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(InventoryService.class)) {System.out.println("8");
            // export the service
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("scope", "Global");

            c.setInterface(
                    new String[] { IPluginInInventoryService.class.getName(),
                            IInventoryShimInternalListener.class.getName(),
                            IInventoryProvider.class.getName() }, /*props*/null);//bug fix (just workaround): TODO: if give the "props" then InventoryService.init() would not be called so that InventoryService creation fails, so we use "null" instead, but we don't know why.

            // Now lets add a service dependency to make sure the
            // provider of service exists
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IPluginOutInventoryService.class, "(scope=Global)")
                    .setCallbacks("setPluginOutInventoryServices",
                            "unsetPluginOutInventoryServices")
                    .setRequired(true));*///remove ad-sal
        }

        if (imp.equals(InventoryServiceShim.class)) {System.out.println("9");
            c.setInterface(new String[] { IContainerListener.class.getName(),
                    /* IOFStatisticsListener.class.getName(), *///s4s statistics
                    IContainerAware.class.getName() }, null);//bug fix: the interfaces can be exposed successfully, but InventoryServiceShim creation can't finally be done (i.e. InventoryServiceShim.init() will not be called), since these interfaces are useless without adsal, let's remove this code.

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IInventoryShimInternalListener.class/*, "(!(scope=Global))"*/)//bug fix (just workaround): we eleminate the "props" in InventoryServiceShim.setInventoryShimInternalListener(), so remove related code
                    .setCallbacks("setInventoryShimInternalListener",
                            "unsetInventoryShimInternalListener")
                    .setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IInventoryShimInternalListener.class, "(scope=Global)")
                    .setCallbacks("setInventoryShimGlobalInternalListener",
                            "unsetInventoryShimGlobalInternalListener")
                    .setRequired(true));*///bug fix: there's no InventoryServiceShim.setInventoryShimGlobalInternalListener()
            c.add(createServiceDependency()
                    .setService(IInventoryShimExternalListener.class)
                    .setCallbacks("setInventoryShimExternalListener",
                            "unsetInventoryShimExternalListener")
                    .setRequired(false));
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(TopologyServiceShim.class)) {System.out.println("10");
            c.setInterface(new String[] { IDiscoveryListener.class.getName(), IContainerListener.class.getName(),
                    IRefreshInternalProvider.class.getName(), IInventoryShimExternalListener.class.getName(),
                    IContainerAware.class.getName() }, null);
            c.add(createServiceDependency()
                    .setService(ITopologyServiceShimListener.class)
                    .setCallbacks("setTopologyServiceShimListener",
                            "unsetTopologyServiceShimListener")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IOFStatisticsManager.class)
                    .setCallbacks("setStatisticsManager",
                            "unsetStatisticsManager").setRequired(false));
        }

        if (imp.equals(NodeFactory.class)) {//s4s test (copied from Stub plugin's activator.java)
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();System.out.println("11");
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props.put("protocolName", "SNMP");
            c.setInterface(INodeFactory.class.getName(), props);
        }
        if (imp.equals(NodeConnectorFactory.class)) {//s4s test (copied from Stub plugin's activator.java)
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();System.out.println("12");
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props.put("protocolName", "SNMP");
            c.setInterface(INodeConnectorFactory.class.getName(), props);
        }
        /*if (imp.equals(ConfigService.class)) {//for ad-sal or no-sal
            //c.setInterface(new String[] { IConfigService.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
        }*/


        //move from "configureInstance()" to here, except for InventoryService (already have one here) and VLANService (already have md vlan here).
        if (imp.equals(TopologyServices.class)) {System.out.println("13");
            // export the service to be used by SAL
            c.setInterface(
                    new String[] { IPluginInTopologyService.class.getName(),
                            ITopologyServiceShimListener.class.getName() }, null);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutTopologyService.class)
                    .setCallbacks("setPluginOutTopologyService",
                            "unsetPluginOutTopologyService").setRequired(false));*///remove ad-sal
            c.add(createServiceDependency()
                    .setService(IRefreshInternalProvider.class)
                    .setCallbacks("setRefreshInternalProvider",
                            "unsetRefreshInternalProvider").setRequired(false));
        }

        if (imp.equals(DataPacketServices.class)) {System.out.println("14");
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(IPluginInDataPacketService.class.getName(), props);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            // This is required for the transmission to happen properly
            c.add(createServiceDependency().setService(IDataPacketMux.class)
                    .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux")
                    .setRequired(true));
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutDataPacketService.class)
                    .setCallbacks("setPluginOutDataPacketService",
                            "unsetPluginOutDataPacketService")
                    .setRequired(false));*///remove ad-sal
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(ReadService.class)) {System.out.println("15");
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(new String[] {
                    /*IReadFilterInternalListener.class.getName(),*///s4s read
                    IPluginInReadService.class.getName() }, props);

            /*c.add(createServiceDependency()
                    .setService(IReadServiceFilter.class)
                    .setCallbacks("setService", "unsetService")
                    .setRequired(true));*///s4s read

            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutReadService.class)
                    .setCallbacks("setPluginOutReadServices",
                            "unsetPluginOutReadServices")
                    .setRequired(false));*///remove ad-sal

            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }

        if (imp.equals(FlowProgrammerNotifier.class)) {System.out.println("16");
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), /*Node.NodeIDType.OPENFLOW*/"SNMP");
            c.setInterface(IFlowProgrammerNotifier.class.getName(), props);

            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutFlowProgrammerService.class)
                    .setCallbacks("setPluginOutFlowProgrammerService",
                            "unsetPluginOutFlowProgrammerService")
                    .setRequired(true));*///remove ad-sal
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
        }
    }

    //Following are for md-sal

    /*VLANService vlanService;

    public Activator() {
        vlanService = new VLANService();
    }

    @Override
    public Collection<? extends RpcService> getImplementations() {
        return null;
    }

    @Override
    public Collection<? extends ProviderFunctionality> getFunctionality() {
        return null;
    }

    @Override
    public void onSessionInitiated(ProviderContext session) {
        session.addRpcImplementation(VLANService.class, vlanService);
    }

    @Override
    protected void startImpl(BundleContext context) {
    }*/
}
