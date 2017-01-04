/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.TopologyService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.opendaylight.snmp4sdn.ITopologyService;
import org.opendaylight.snmp4sdn.ITopologyServiceShim;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(TopologyProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;
    ProviderContext session;

    ITopologyService topoService;
    TopologyImpl topoImpl;
    NotificationProviderService notifService;

    //no need to maintain them here, directly pass them to topoImpl
    /*private ITopologyService topo = null;
    private IInventoryProvider inv = null;
    private DiscoveryServiceAPI service = null;*/

    public TopologyProvider() {
        topoImpl = new TopologyImpl();
        if(topoImpl == null){
            logger.debug("ERROR: TopologyProvider: TopologyImpl() creation fail");
        }
        logger.debug("TopologyProvider creation complete");
    }

    //The following three functions (getImplementations(), getFunctionality(), onSessionInitialized()), seems to be deprecated in yangtools ver. 0.7.0-SNAPSHOT

    /*@Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public Collection<? extends RpcService> getImplementations() {
        return null;
    }

    @Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public Collection<? extends ProviderFunctionality> getFunctionality() {
        return null;
    }

    @Override//seems deprecated in yangtools 0.7.0-SNAPSHOT
    public void onSessionInitialized(ConsumerContext session) {
        // NOOP
    }*/

    @Override
    public void onSessionInitiated(ProviderContext session) {

        //toposhim
        topoImpl.init();
        session.addRpcImplementation(TopologyService.class, topoImpl);

        //toposervices
        notifService = session.getSALService(NotificationProviderService.class);
        /*topoService.setMdNotifService(notifService);
        *//*Bug fix: Due to topoService is null at this moment,
                        we call topoService.setMdNotifService() after the TopologyServices.class
                        is created in Activator.java.
            More detail: topoService is a ITopologyService,
            and TopologyServices.class is actually the implementation of ITopologyService.
            However, TopologyServices.class is to be created in Activator,
            but is not yet created at this moment. So, topoService is null at this moment.
            So we should call topoService.setMdNotifService() after TopologyServices.class is created.
            */
        this.session = session;

        logger.debug("TopologyProvider: onSessionInitiated(): done");
    }

    public void setMdNotifService(){
        if(topoService != null){
            topoService.setMdNotifService(notifService);            
            logger.debug("TopologyProvider: setMdNotifService(): done");
        }
        else{
            logger.debug("ERROR: setMdNotifService(): topoService is null!");
        }
    }

    /*
    @Override
    protected void startImpl(BundleContext context) {
    }
    */

    @Override
    public void close() {
        topoService.unsetMdNotifService(notifService);
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public BindingAwareBroker getBroker() {
        return broker;
    }

    public void setBroker(BindingAwareBroker broker) {
        this.broker = broker;
        registerProvider();
    }

    public void unsetBroker(BindingAwareBroker broker) {
        this.broker = null;
    }

    private boolean hasAllDependencies(){
        if(this.broker != null/* && this.switchConnectionProvider != null*/) {
            return true;
        }
        return false;
    }

    private void registerProvider() {
        if(hasAllDependencies()) {
            this.broker.registerProvider(this,context);
        }
    }

    public void setTopologyService(ITopologyService topo) {
        logger.trace("TopologyProvider: setTopologyService is called!");
        if(topo == null)
            logger.debug("ERROR: setTopologyServiceShim(): given null ITopologyService");
        topoService = topo;
    }

    public void unsetTopologyService(ITopologyService topo) {
        topoService = null;
    }

    public void setTopologyServiceShim(ITopologyServiceShim topo) {
        if(topo == null)
            logger.debug("ERROR: setTopologyServiceShim(): given null ITopologyService");
        topoImpl.setTopologyServiceShim(topo);
    }

    public void unsetTopologyServiceShim(ITopologyServiceShim topo) {
        topoImpl.unsetTopologyServiceShim(topo);
    }

    public void setInventoryService(IInventoryProvider inv) {
        if(inv == null)
            logger.debug("ERROR: setInventoryService(): given null IInventoryProvider");
        topoImpl.setInventoryService(inv);
    }

    public void unsetInventoryService(IInventoryProvider inv) {
        topoImpl.unsetInventoryService(inv);
    }

    public void setDiscoveryService(DiscoveryServiceAPI discov) {
        if(discov == null)
            logger.debug("ERROR: setDiscoveryService(): given null DiscoveryServiceAPI");
        topoImpl.setDiscoveryService(discov);
    }

    public void unsetDiscoveryService(DiscoveryServiceAPI discov) {
        topoImpl.unsetDiscoveryService(discov);
    }

}

