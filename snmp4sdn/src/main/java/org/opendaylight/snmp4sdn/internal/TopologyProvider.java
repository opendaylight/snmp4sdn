package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;

import org.opendaylight.snmp4sdn.ITopologyService;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150528.TopologyService;

import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(TopologyProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;

    ITopologyService topo;
    IInventoryProvider inv;

    TopologyServiceImpl topoImpl;

    public TopologyProvider() {
        topoImpl = new TopologyServiceImpl();
        if(topoImpl == null){
            logger.debug("ERROR: TopologyProvider: TopologyProviderImpl() creation fail");
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
        topoImpl.init();
        session.addRpcImplementation(TopologyService.class, topoImpl);
        logger.debug("TopologyProvider: onSessionInitiated(): done");
    }

    /*
    @Override
    protected void startImpl(BundleContext context) {
    }
    */

    @Override
    public void close() {
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

    public void setTopologyServiceShim(ITopologyService topo) {
        if(topo == null)
            logger.debug("ERROR: setTopologyServiceShim(): given null ITopologyService");
        this.topo = topo;
        topoImpl.setTopologyServiceShim(topo);
    }

    public void unsetTopologyServiceShim(ITopologyService topo) {
        if (this.topo == topo) {
            this.topo = null;
            topoImpl.unsetTopologyServiceShim(topo);
        }
        else
            logger.debug("ERROR: unsetTopologyServiceShim(): given ITopologyService is not the local one");
    }

    public void setInventoryService(IInventoryProvider inv) {
        if(inv == null)
            logger.debug("ERROR: TopologyProvider: setInventoryService(): given null IInventoryProvider");
        this.inv = inv;
        topoImpl.setInventoryService(inv);
    }

    public void unsetInventoryService(IInventoryProvider inv) {
        if (this.inv == inv) {
            this.inv = null;
            topoImpl.unsetInventoryService(inv);
        }
        else
            logger.debug("ERROR: TopologyProvider: unsetInventoryService(): given IInventoryProvider is not the local one");
    }

}

