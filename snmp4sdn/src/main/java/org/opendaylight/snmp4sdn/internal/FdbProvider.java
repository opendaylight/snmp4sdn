package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.opendaylight.snmp4sdn.core.IController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FdbProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(FdbProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;

    FdbServiceImpl fdbImpl;

    IController controller = null;

    public FdbProvider() {
        fdbImpl = new FdbServiceImpl();
        if(fdbImpl == null){
            logger.debug("ERROR: FdbProvider: FdbProviderImpl() creation fail");
        }
        logger.debug("FdbProvider creation complete");
    }

    //Deprecated in Helium (seems yangtools ver. 0.6.2-SNAPSHOT)
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
        fdbImpl.init();
        session.addRpcImplementation(FdbService.class, fdbImpl);
        logger.debug("FdbProvider: onSessionInitiated(): done");
    }

    /*
    @Override
    protected void startImpl(BundleContext context) {
    }
    */


    //Deprecated in Helium (seems yangtools ver. 0.6.2-SNAPSHOT)
    @Override
    public void onSessionInitialized(ConsumerContext session) {
        // NOOP
    }

    @Override
    public void close() {
    }

    //TODO: seems useless (neither controller nor snmp4sdn call it)
    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    //TODO: seems useless (neither controller nor snmp4sdn call it)
    public BindingAwareBroker getBroker() {
        return broker;
    }

    public void setBroker(BindingAwareBroker broker) {
        this.broker = broker;
        registerProvider();
    }

    //TODO: seems useless (neither controller nor snmp4sdn call it)
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

    public void setController(IController core) {
        this.controller = (IController)core;
        fdbImpl.setController(controller);
    }

    public void unsetController(IController core) {
        if (this.controller == (IController)core) {
            this.controller = null;
        }
    }

}

