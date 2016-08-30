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
    import org.opendaylight.snmp4sdn.ITopologyService;
    import org.opendaylight.snmp4sdn.ITopologyServiceShim;
    import org.opendaylight.snmp4sdn.ITopologyServiceShimListener;
    import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;
    import org.opendaylight.snmp4sdn.core.IController;
    import org.opendaylight.snmp4sdn.core.IMessageListener;
    import org.opendaylight.snmp4sdn.core.internal.Controller;
/*import org.opendaylight.snmp4sdn.sal.connection.IPluginInConnectionService;
import org.opendaylight.snmp4sdn.sal.connection.IPluginOutConnectionService;*///s4s cs
//import org.opendaylight.snmp4sdn.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.snmp4sdn.sal.core.IContainerAware;
import org.opendaylight.snmp4sdn.sal.core.IContainerListener;
import org.opendaylight.snmp4sdn.sal.core.Node;
import org.opendaylight.snmp4sdn.sal.core.NodeConnector;
import org.opendaylight.snmp4sdn.sal.flowprogrammer.IPluginInFlowProgrammerService;
import org.opendaylight.snmp4sdn.sal.flowprogrammer.IPluginOutFlowProgrammerService;
import org.opendaylight.snmp4sdn.sal.inventory.IPluginInInventoryService;
import org.opendaylight.snmp4sdn.sal.inventory.IPluginOutInventoryService;
import org.opendaylight.snmp4sdn.sal.packet.IPluginInDataPacketService;
import org.opendaylight.snmp4sdn.sal.packet.IPluginOutDataPacketService;
import org.opendaylight.snmp4sdn.sal.reader.IPluginInReadService;
import org.opendaylight.snmp4sdn.sal.reader.IPluginOutReadService;
import org.opendaylight.snmp4sdn.sal.topology.IPluginInTopologyService;
import org.opendaylight.snmp4sdn.sal.topology.IPluginOutTopologyService;
import org.opendaylight.snmp4sdn.sal.utils.GlobalConstants;
import org.opendaylight.snmp4sdn.sal.utils.INodeConnectorFactory;//s4s test
import org.opendaylight.snmp4sdn.sal.utils.INodeFactory;//s4s test
//import org.opendaylight.snmp4sdn.sal.vlan.IPluginInVLANService;//s4s//ad-sal
//import org.opendaylight.snmp4sdn.IConfigService;//no-sal
//import org.opendaylight.snmp4sdn.IVLANService;//no-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanService;//md-sal
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;//md-sal

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;//md-sal

/**
 * SNMP4SDN plugin Activator
 *
 *
 */

/*
 revision memo:
 ComponentActivatorAbstractBase is provided by sal, which uses felix.dm ver 3.x,
 but felix.dm used in OpenDaylight Boron is ver 4.x,
 so we stop using ComponentActivatorAbstractBase, but use DependencyActivatorBase instead.
 So, the functions such as configureInstance() etc are removed and re-written as the current code in init()
 */

public class Activator extends DependencyActivatorBase/*extends ComponentActivatorAbstractBase*//*, AbstractBindingAwareProvidermd-sal*/ {
    protected static final Logger logger = LoggerFactory
            .getLogger(Activator.class);

    private MiscConfigProvider config = new MiscConfigProvider();//md-sal
    private FdbProvider fdb = new FdbProvider();//md-sal

    private AclProvider acl = new AclProvider();//md-sal
    private VlanProvider vlan = new VlanProvider();//md-sal
    private SwitchDbProvider switchdb = new SwitchDbProvider();//md-sal
    private TopologyProvider topo = new TopologyProvider();//md-sal
 
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        /*
         * KEYWD: ITRI_PEREGRINE_SNMP4SDN_Activator_RegisterNode&NodeConnectorOfTypeSNMP
         *         OpenDaylight controller had defined several 'type', such as OF (i.e. OpenFlow) ONEPK etc.
         *         If you'd like to add other custom types, it provides the 'registerIDType()' approach, as below, to manually add new type of Node and NodeConnector
         *  Author: Yi-Ling Hsieh
         */
        Node.NodeIDType.registerIDType("SNMP", Long.class);
        NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
        //cancelled->TODO: Issue: is code architecture okay in this way?: the code above is moved to Controller.init(), because SNMP type is used during Topology Discovery but here Activator.init() is the latest code to be called in snmp4sdn start up (start up means all modules creation, initialization, start... and we also put Topology Discovery in InventoryServiceShim's start).


        /*
         * The following is re-written from old Activator's configureInstance()
         */

        //TopologyServices
        manager.add(createComponent()
                .setImplementation(TopologyServices.class)
                .setInterface(new String[] { IPluginInTopologyService.class.getName(),
                            ITopologyServiceShimListener.class.getName() }, null)
                .add(createServiceDependency().setService(IRefreshInternalProvider.class).setRequired(false)
                        .setCallbacks("setRefreshInternalProvider", "unsetRefreshInternalProvider")));
        //InventoryService
        manager.add(createComponent()
                .setImplementation(InventoryService.class)
                .setInterface(new String[] {
                            IPluginInInventoryService.class.getName(),
                            IInventoryShimInternalListener.class.getName(),
                            IInventoryProvider.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController")));
        //DataPacketServices
            // export the service to be used by SAL
            Dictionary<String, Object> props1 = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used by SAL
            props1.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
        manager.add(createComponent()
                .setImplementation(DataPacketServices.class)
                .setInterface(IPluginInDataPacketService.class.getName(), props1)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IDataPacketMux.class).setRequired(true)
                        .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux")));
        //ReadService
            Dictionary<String, Object> props2 = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used by SAL
            props2.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
        manager.add(createComponent()
                .setImplementation(ReadService.class)
                .setInterface(IPluginInReadService.class.getName(), props2));
        //FlowProgrammerNotifier
            // export the service to be used by SAL
            Dictionary<String, Object> props3 = new Hashtable<String, Object>();
            // Set the protocolPluginType property which will be used by SAL
            props3.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
        manager.add(createComponent()
                .setImplementation(FlowProgrammerNotifier.class)
                .setInterface(IFlowProgrammerNotifier.class.getName(), props3));
        //VLANService
        manager.add(createComponent()
                .setImplementation(VLANService.class)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController")));

        /*
         * The following is re-written from old Activator's configureGlobalInstance()
         */

        //The XxxService below are md-sal services

        //ConfigService
        manager.add(createComponent()
                .setImplementation(config)
                .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                        .setCallbacks("setBroker", "unsetBroker")));
        logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for ConfigService");

        //FdbService
        manager.add(createComponent()
                        .setImplementation(fdb)
                        .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                                .setCallbacks("setBroker", "unsetBroker"))
                        .add(createServiceDependency().setService(IController.class).setRequired(true)
                                .setCallbacks("setController", "unsetController")));
        logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for FdbService");
        //AclService
        manager.add(createComponent()
                        .setImplementation(acl)
                        .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                                .setCallbacks("setBroker", "unsetBroker"))
                        .add(createServiceDependency().setService(IController.class).setRequired(true)
                                .setCallbacks("setController", "unsetController")));
        logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for AclService");
        //VlanService
        manager.add(createComponent()
                        .setImplementation(vlan)
                        .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                                .setCallbacks("setBroker", "unsetBroker"))
                        .add(createServiceDependency().setService(IController.class).setRequired(true)
                                .setCallbacks("setController", "unsetController")));
        logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for VlanService");
        //SwitchDbService
        manager.add(createComponent()
                        .setImplementation(switchdb)
                        .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                                .setCallbacks("setBroker", "unsetBroker"))
                        .add(createServiceDependency().setService(IController.class).setRequired(true)
                                .setCallbacks("setController", "unsetController")));
                logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for SwitchDbService");
        //TopologyService
        manager.add(createComponent()
                        .setImplementation(topo)
                        .add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true)
                                .setCallbacks("setTopologyService", "unsetTopologyService"))
                        .add(createServiceDependency().setService(ITopologyService.class).setRequired(true)
                                .setCallbacks("setController", "unsetController"))
                        .add(createServiceDependency().setService(ITopologyServiceShim.class).setRequired(true)
                                .setCallbacks("setTopologyServiceShim", "unsetTopologyServiceShim"))
                        .add(createServiceDependency().setService(IInventoryProvider.class).setRequired(true)
                                .setCallbacks("setInventoryService", "unsetInventoryService"))
                        .add(createServiceDependency().setService(DiscoveryServiceAPI.class).setRequired(true)
                                .setCallbacks("setDiscoveryService", "unsetDiscoveryService")));
        logger.debug("snmp4sdn: Activator: configured BindingAwareBroker and IController, for TopologyService");

        //The following are common modules providing OSGi interfaces

        //Controller
            Dictionary<String, Object> props4 = new Hashtable<String, Object>();
            props4.put("name", "Controller");
            props4.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
        manager.add(createComponent()
                .setImplementation(Controller.class)
                .setInterface(new String[] { IController.class.getName(),
                            ICore.class.getName() }, props4));
        //FlowProgrammerService
            Dictionary<String, Object> props5 = new Hashtable<String, Object>();
            props5.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
        manager.add(createComponent()
                .setImplementation(FlowProgrammerService.class)
                .setInterface(new String[] { IPluginInFlowProgrammerService.class.getName(),
                            IContainerListener.class.getName(), IInventoryShimExternalListener.class.getName(),
                            IContainerAware.class.getName() }, props5)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IFlowProgrammerNotifier.class).setRequired(false)
                        .setCallbacks("setFlowProgrammerNotifier", "unsetFlowProgrammerNotifier")));
        //ReadServiceFilter
        manager.add(createComponent()
                .setImplementation(ReadServiceFilter.class)
                .setInterface(new String[] { IContainerListener.class.getName(),
                            IContainerAware.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IOFStatisticsManager.class).setRequired(true)
                        .setCallbacks("setService", "unsetService")));
        //OFStatisticsManager
            manager.add(createComponent()
                .setImplementation(OFStatisticsManager.class)
                .setInterface(new String[] { IOFStatisticsManager.class.getName(),
                    IInventoryShimExternalListener.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController")));
        //DiscoveryService
        manager.add(createComponent()
                .setImplementation(DiscoveryService.class)
                .setInterface(new String[] { IInventoryShimExternalListener.class.getName(), IDataPacketListen.class.getName(),
                            IContainerListener.class.getName(), DiscoveryServiceAPI.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IDataPacketMux.class).setRequired(true)
                        .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux"))
                .add(createServiceDependency().setService(IDiscoveryListener.class).setRequired(true)
                        .setCallbacks("setDiscoveryListener", "unsetDiscoveryListener")));
        //DataPacketMuxDemux
        manager.add(createComponent()
                .setImplementation(DataPacketMuxDemux.class)
                .setInterface(new String[] { IDataPacketMux.class.getName(), IContainerListener.class.getName(),
                    IInventoryShimExternalListener.class.getName(), IContainerAware.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IDataPacketListen.class).setRequired(false)
                        .setCallbacks("setIDataPacketListen", "unsetIDataPacketListen")));
        //InventoryServiceShim
        manager.add(createComponent()
                .setImplementation(InventoryServiceShim.class)
                .setInterface(new String[] { IContainerListener.class.getName(),
                            IContainerAware.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IInventoryShimInternalListener.class).setRequired(true)
                        .setCallbacks("setInventoryShimInternalListener", "unsetInventoryShimInternalListener"))
                .add(createServiceDependency().setService(IInventoryShimExternalListener.class).setRequired(false)
                        .setCallbacks("setInventoryShimExternalListener", "unsetInventoryShimExternalListener")));
        //TopologyServiceShim
        manager.add(createComponent()
                .setImplementation(TopologyServiceShim.class)
                .setInterface(new String[] { IDiscoveryListener.class.getName(), IContainerListener.class.getName(),
                    IRefreshInternalProvider.class.getName(), IInventoryShimExternalListener.class.getName(),
                    IContainerAware.class.getName(), ITopologyServiceShim.class.getName() }, null)
                .add(createServiceDependency().setService(ITopologyServiceShimListener.class).setRequired(true)
                        .setCallbacks("setTopologyServiceShimListener", "unsetTopologyServiceShimListener"))
                .add(createServiceDependency().setService(IOFStatisticsManager.class).setRequired(false)
                        .setCallbacks("setStatisticsManager", "unsetStatisticsManager")));
        //NodeFactory
            Dictionary<String, Object> props6 = new Hashtable<String, Object>();
            props6.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props6.put("protocolName", "SNMP");
        manager.add(createComponent()
                .setImplementation(NodeFactory.class)
                .setInterface(INodeFactory.class.getName(), props6));
        //NodeConnectorFactory
            Dictionary<String, Object> props7 = new Hashtable<String, Object>();
            props7.put(GlobalConstants.PROTOCOLPLUGINTYPE.toString(), "SNMP");
            props7.put("protocolName", "SNMP");
        manager.add(createComponent()
                .setImplementation(NodeConnectorFactory.class)
                .setInterface(INodeConnectorFactory.class.getName(), props7));
        //DiscoveryService
        manager.add(createComponent()
                .setImplementation(Controller.class)
                .setInterface(new String[] { IInventoryShimExternalListener.class.getName(), IDataPacketListen.class.getName(),
                            IContainerListener.class.getName(), DiscoveryServiceAPI.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IDataPacketMux.class).setRequired(true)
                        .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux"))
                .add(createServiceDependency().setService(IDiscoveryListener.class).setRequired(true)
                        .setCallbacks("setDiscoveryListener", "unsetDiscoveryListener")));
        //DiscoveryService
        manager.add(createComponent()
                .setImplementation(Controller.class)
                .setInterface(new String[] { IInventoryShimExternalListener.class.getName(), IDataPacketListen.class.getName(),
                            IContainerListener.class.getName(), DiscoveryServiceAPI.class.getName() }, null)
                .add(createServiceDependency().setService(IController.class).setRequired(true)
                        .setCallbacks("setController", "unsetController"))
                .add(createServiceDependency().setService(IDataPacketMux.class).setRequired(true)
                        .setCallbacks("setIDataPacketMux", "unsetIDataPacketMux"))
                .add(createServiceDependency().setService(IDiscoveryListener.class).setRequired(true)
                        .setCallbacks("setDiscoveryListener", "unsetDiscoveryListener")));
        
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
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
        try{
            super.start(arg0);
        }catch(Exception e1){
            logger.debug("ERROR: Activator: start(): call super.start() given BundleContext {}, occur exception: {}", arg0, e1);
        }
        config.setContext(arg0);
        fdb.setContext(arg0);
        acl.setContext(arg0);
        vlan.setContext(arg0);
        switchdb.setContext(arg0);
        topo.setContext(arg0);
    }

}
