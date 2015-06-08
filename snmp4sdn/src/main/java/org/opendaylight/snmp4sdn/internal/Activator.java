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
    import org.opendaylight.snmp4sdn.ITopologyService;
    import org.opendaylight.snmp4sdn.core.IController;
    import org.opendaylight.snmp4sdn.core.IMessageListener;
    import org.opendaylight.snmp4sdn.core.internal.Controller;
/*import org.opendaylight.controller.sal.connection.IPluginInConnectionService;
import org.opendaylight.controller.sal.connection.IPluginOutConnectionService;*///s4s cs
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.sal.core.IContainerAware;
import org.opendaylight.controller.sal.core.IContainerListener;
import org.opendaylight.snmp4sdn.core.ISwitchStateListener;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
/*//Lithium: remove ad-sal service dependency
import org.opendaylight.controller.sal.flowprogrammer.IPluginInFlowProgrammerService;
import org.opendaylight.controller.sal.flowprogrammer.IPluginOutFlowProgrammerService;
import org.opendaylight.controller.sal.inventory.IPluginInInventoryService;
import org.opendaylight.controller.sal.inventory.IPluginOutInventoryService;
import org.opendaylight.controller.sal.packet.IPluginInDataPacketService;
import org.opendaylight.controller.sal.packet.IPluginOutDataPacketService;
import org.opendaylight.controller.sal.reader.IPluginInReadService;
import org.opendaylight.controller.sal.reader.IPluginOutReadService;
import org.opendaylight.controller.sal.topology.IPluginInTopologyService;
import org.opendaylight.controller.sal.topology.IPluginOutTopologyService;*/
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
         */
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
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
        topo.close();//md-sal
    }

    @Override
    public void start(BundleContext arg0) {//md-sal
        super.start(arg0);
        config.setContext(arg0);
        fdb.setContext(arg0);
        acl.setContext(arg0);
        vlan.setContext(arg0);
        topo.setContext(arg0);
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

    //NOTICE: in Lithium, it seems configureInstance() would not be executed, so I disable the getImplementations() which goes together with configureInstance()

    @Override
    public Object[] getImplementations() {
        //to make sure the code getImplementations() would not happen, the name changes to getImplementations_original(), and leave getImplementations() empty
        Object[] res = {};
        return res;
    }
    public Object[] getImplementations_original() {
        Object[] res = { TopologyServices.class,/* DataPacketServices.class,*/
                InventoryService.class,/* ReadService.class,*/
                //FlowProgrammerNotifier.class,
                //VLANService.class//ad-sal, no-sal
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

    //NOTICE: in Lithium, it seems configureInstance() would not be executed, so in the configureInstance() the if{} sections for TopologyServices and InventoryService are moved to configureGlobalInstance()
    @Override
    public void configureInstance(Component c, Object imp, String containerName) {
        //to make sure the code configureInstance() would not happen, the name changes to configureInstance_original(), and leave configureInstance() empty
    }
    public void configureInstance_original(Component c, Object imp, String containerName) {
        if (imp.equals(TopologyServices.class)) {
            // export the service to be used by SAL
            c.setInterface(
                    new String[] { //IPluginInTopologyService.class.getName(),//Lithium: remove ad-sal service dependency
                            ITopologyServiceShimListener.class.getName() }, null);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutTopologyService.class)
                    .setCallbacks("setPluginOutTopologyService",
                            "unsetPluginOutTopologyService").setRequired(false));*///Lithium: remove ad-sal service dependency
            c.add(createServiceDependency()
                    .setService(IRefreshInternalProvider.class)
                    .setCallbacks("setRefreshInternalProvider",
                            "unsetRefreshInternalProvider").setRequired(false));
        }

        if (imp.equals(InventoryService.class)) {
            // export the service
            c.setInterface(
                    new String[] {
                            //IPluginInInventoryService.class.getName(),//Lithium: remove ad-sal service dependency
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
                    .setRequired(false));*///Lithium: remove ad-sal service dependency
        }

        /*if (imp.equals(DataPacketServices.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
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
            c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutDataPacketService.class)
                    .setCallbacks("setPluginOutDataPacketService",
                            "unsetPluginOutDataPacketService")
                    .setRequired(false));
            //c.add(createServiceDependency()
            //        .setService(IPluginOutConnectionService.class)
            //        .setCallbacks("setIPluginOutConnectionService",
            //                "unsetIPluginOutConnectionService")
            //        .setRequired(true));//s4s cs
        }*///useless now

        /*if (imp.equals(ReadService.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            c.setInterface(new String[] {
                    //IReadFilterInternalListener.class.getName(),//s4s read
                    IPluginInReadService.class.getName() }, props);

            //c.add(createServiceDependency()
            //        .setService(IReadServiceFilter.class)
            //        .setCallbacks("setService", "unsetService")
            //        .setRequired(true));//s4s read

            c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutReadService.class)
                    .setCallbacks("setPluginOutReadServices",
                            "unsetPluginOutReadServices")
                    .setRequired(false));

            //c.add(createServiceDependency()
            //        .setService(IPluginOutConnectionService.class)
            //        .setCallbacks("setIPluginOutConnectionService",
            //                "unsetIPluginOutConnectionService")
            //        .setRequired(true));//s4s cs
        }*///useless now

        /*if (imp.equals(FlowProgrammerNotifier.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            c.setInterface(IFlowProgrammerNotifier.class.getName(), props);

            c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutFlowProgrammerService.class)
                    .setCallbacks("setPluginOutFlowProgrammerService",
                            "unsetPluginOutFlowProgrammerService")
                    .setRequired(true));
            //c.add(createServiceDependency()
            //        .setService(IPluginOutConnectionService.class)
            //        .setCallbacks("setIPluginOutConnectionService",
            //                "unsetIPluginOutConnectionService")
            //        .setRequired(true));//s4s cs
        }*///useless now

        /*if (imp.equals(VLANService.class)) {
            //c.setInterface(IPluginInVLANService.class.getName(), null);//ad-sal
            //c.setInterface(IVLANService.class.getName(), null);//no-sal
            //c.setInterface(VlanService.class.getName(), null);//md-sal

            c.add(createServiceDependency()
                    .setService(IController.class)
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
        }*///ad-sal, no-sal
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
        Object[] res = { Controller.class,/* OFStatisticsManager.class,
                FlowProgrammerService.class, ReadServiceFilter.class, //useless now*/
                DiscoveryService.class, /*DataPacketMuxDemux.class, //useless now*/ InventoryService.class,
                TopologyServices.class,
                InventoryServiceShim.class, TopologyServiceShim.class,
                NodeFactory.class, NodeConnectorFactory.class,//s4s test: add this line
                //ConfigService.class,//ad-sal or no-sal
                config,//md-sal
                fdb,//md-sal
                acl,//md-sal
                vlan,//md-sal
                topo//md-sal
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
     */
    @Override
    public void configureGlobalInstance(Component c, Object imp) {
        logger.debug("snmp4sdn: Activator configureGlobalInstance( ) is called");

        //md-sal (the following items: config, fdb, acl, vlan, topo)
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
        if (imp == topo) {
            c.add(createServiceDependency().setService(BindingAwareBroker.class)
                    .setCallbacks("setBroker", "unsetBroker").setRequired(true));
            c.add(createServiceDependency()
                    .setService(ITopologyService.class)
                    .setCallbacks("setTopologyServiceShim", "unsetTopologyServiceShim").setRequired(true));
            c.add(createServiceDependency()
                    .setService(IInventoryProvider.class)
                    .setCallbacks("setInventoryService", "unsetInventoryService").setRequired(true));
            logger.debug("snmp4sdn: Activator: configured BindingAwareBroker, ITopologyService, and IInventoryProvider, for TopologyService");
        }

        if (imp.equals(Controller.class)) {
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("name", "Controller");
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            c.setInterface(new String[] { IController.class.getName(),
                                          /*IPluginInConnectionService.class.getName()*///s4s cs
                                          ICore.class.getName()//karaf
                                          },
                                          props);
            logger.debug("snmp4sdn: Activator: Controller.class configured");
        }

        if (imp.equals(TopologyServices.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("containerName", GlobalConstants.DEFAULT.toString());
            c.setInterface(
                    new String[] { //IPluginInTopologyService.class.getName(),//Lithium: remove ad-sal service dependency
                            ITopologyServiceShimListener.class.getName() }, props);
            // Hook the services coming in from SAL, as optional in
            // case SAL is not yet there, could happen
            /*c.add(createContainerServiceDependency(containerName)
                    .setService(IPluginOutTopologyService.class)
                    .setCallbacks("setPluginOutTopologyService",
                            "unsetPluginOutTopologyService").setRequired(false));*///Lithium: remove ad-sal service dependency
            c.add(createServiceDependency()
                    .setService(IRefreshInternalProvider.class)
                    .setCallbacks("setRefreshInternalProvider",
                            "unsetRefreshInternalProvider").setRequired(false));
            logger.debug("snmp4sdn: Activator: TopologyServices.class configured");
        }

        if (imp.equals(InventoryService.class)) {
            // export the service
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("containerName", GlobalConstants.DEFAULT.toString());
            c.setInterface(
                    new String[] {
                            //IPluginInInventoryService.class.getName(),//Lithium: remove ad-sal service dependency
                            IInventoryShimInternalListener.class.getName(),
                            IInventoryProvider.class.getName()
                            }, props);

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
                    .setRequired(false));*///Lithium: remove ad-sal service dependency
            logger.debug("snmp4sdn: Activator: InventoryService.class configured");
        }

        /*if (imp.equals(FlowProgrammerService.class)) {
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            c.setInterface(
                    new String[] { IPluginInFlowProgrammerService.class.getName(), //IMessageListener.class.getName(),
                            IContainerListener.class.getName(), IInventoryShimExternalListener.class.getName(),
                            IContainerAware.class.getName() }, props);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));

            c.add(createServiceDependency()
                    .setService(IFlowProgrammerNotifier.class)
                    .setCallbacks("setFlowProgrammerNotifier",
                            "unsetsetFlowProgrammerNotifier")
                    .setRequired(false));

            //c.add(createServiceDependency()
            //        .setService(IPluginOutConnectionService.class)
            //        .setCallbacks("setIPluginOutConnectionService",
            //                "unsetIPluginOutConnectionService")
            //        .setRequired(true));//s4s cs
        }*/

        /*if (imp.equals(ReadServiceFilter.class)) {

            c.setInterface(new String[] { //IReadServiceFilter.class.getName(), //s4s read
                IContainerListener.class.getName(),
                    //IOFStatisticsListener.class.getName(), //s4s statistics
                    IContainerAware.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IOFStatisticsManager.class)
                    .setCallbacks("setService", "unsetService")
                    .setRequired(true));
            //c.add(createServiceDependency()
            //        .setService(IReadFilterInternalListener.class)
            //        .setCallbacks("setReadFilterInternalListener",
            //                "unsetReadFilterInternalListener")
            //        .setRequired(false));//s4s read
        }*/

        /*if (imp.equals(OFStatisticsManager.class)) {

            c.setInterface(new String[] { IOFStatisticsManager.class.getName(),
                    IInventoryShimExternalListener.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            //c.add(createServiceDependency()
            //        .setService(IOFStatisticsListener.class)
            //        .setCallbacks("setStatisticsListener",
            //                "unsetStatisticsListener").setRequired(false));//s4s statistics
        }*/

        if (imp.equals(DiscoveryService.class)) {
            // export the service
            c.setInterface(
                    new String[] { IInventoryShimExternalListener.class.getName(), IDataPacketListen.class.getName(),
                            IContainerListener.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            /*c.add(createServiceDependency().setService(IDataPacketMux.class)
                    .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux")
                    .setRequired(true));*///useless now
            c.add(createServiceDependency()
                    .setService(IDiscoveryListener.class)
                    .setCallbacks("setDiscoveryListener",
                            "unsetDiscoveryListener").setRequired(true));
            /*c.add(createServiceDependency()
                    .setService(IPluginOutConnectionService.class)
                    .setCallbacks("setIPluginOutConnectionService",
                            "unsetIPluginOutConnectionService")
                    .setRequired(true));*///s4s cs
            logger.debug("snmp4sdn: Activator: DiscoveryService.class configured");
        }

        // DataPacket mux/demux services, which is teh actual engine
        // doing the packet switching
        /*if (imp.equals(DataPacketMuxDemux.class)) {
            c.setInterface(new String[] { IDataPacketMux.class.getName(), IContainerListener.class.getName(),
                    IInventoryShimExternalListener.class.getName(), IContainerAware.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IPluginOutDataPacketService.class)
                    .setCallbacks("setPluginOutDataPacketService",
                            "unsetPluginOutDataPacketService")
                    .setRequired(false));
            // See if there is any local packet dispatcher
            c.add(createServiceDependency()
                    .setService(IDataPacketListen.class)
                    .setCallbacks("setIDataPacketListen",
                            "unsetIDataPacketListen").setRequired(false));
            //c.add(createServiceDependency()
            //        .setService(IPluginOutConnectionService.class)
            //        .setCallbacks("setIPluginOutConnectionService",
            //                "unsetIPluginOutConnectionService")
            //        .setRequired(true));//s4s cs
        }*/

        if (imp.equals(InventoryServiceShim.class)) {
            c.setInterface(new String[] { IContainerListener.class.getName(),
                    /*IOFStatisticsListener.class.getName(), *///s4s statistics
                    IContainerAware.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
            c.add(createServiceDependency()
                    .setService(IInventoryShimInternalListener.class, "(!(scope=Global))")
                    .setCallbacks("setInventoryShimInternalListener",
                            "unsetInventoryShimInternalListener")
                    .setRequired(true));
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
            logger.debug("snmp4sdn: Activator: InventoryServiceShim.class configured");
        }

        if (imp.equals(TopologyServiceShim.class)) {
            c.setInterface(new String[] { IDiscoveryListener.class.getName(), IContainerListener.class.getName(),
                    IRefreshInternalProvider.class.getName(), IInventoryShimExternalListener.class.getName(), ITopologyService.class.getName(),
                    IContainerAware.class.getName() }, null);
            /*c.add(createServiceDependency()
                    .setService(ITopologyServiceShimListener.class)
                    .setCallbacks("setTopologyServiceShimListener",
                            "unsetTopologyServiceShimListener")
                    .setRequired(true));*///TODO: don't know why. if enable this, it seems as a result, multiple services creation failure
            /*c.add(createServiceDependency()
                    .setService(IOFStatisticsManager.class)
                    .setCallbacks("setStatisticsManager",
                            "unsetStatisticsManager").setRequired(false));*///useless now
            logger.debug("snmp4sdn: Activator: TopologyServiceShim.class configured");
        }

        if (imp.equals(NodeFactory.class)) {//s4s test (copied from Stub plugin's activator.java)
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props.put("protocolName", "SNMP");
            c.setInterface(INodeFactory.class.getName(), props);
            logger.debug("snmp4sdn: Activator: NodeFactory.class configured");
        }
        if (imp.equals(NodeConnectorFactory.class)) {//s4s test (copied from Stub plugin's activator.java)
            // export the service to be used by SAL
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used
            // by SAL
            props.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props.put("protocolName", "SNMP");
            c.setInterface(INodeConnectorFactory.class.getName(), props);
            logger.debug("snmp4sdn: Activator: NodeConnectorFactory.class configured");
        }

        /*if (imp.equals(ConfigService.class)) {//for ad-sal or no-sal
            //c.setInterface(new String[] { IConfigService.class.getName() }, null);

            c.add(createServiceDependency()
                    .setService(IController.class, "(name=Controller)")
                    .setCallbacks("setController", "unsetController")
                    .setRequired(true));
        }*/
    }

}
