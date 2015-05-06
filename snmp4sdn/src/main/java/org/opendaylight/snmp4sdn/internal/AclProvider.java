package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.opendaylight.snmp4sdn.core.IController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AclProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(AclProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;

    AclServiceImpl aclImpl;

    IController controller = null;

    public AclProvider() {
        aclImpl = new AclServiceImpl();
        if(aclImpl == null){
            logger.debug("ERROR: AclProvider: AclProviderImpl() creation fail");
        }
        logger.debug("AclProvider creation complete");
    }

    //Deprecated in Helium (seems yangtools ver. 0.6.2-SNAPSHOT)
    @Override
    public Collection<? extends RpcService> getImplementations() {
        return null;
    }

    @Override
    public void onSessionInitiated(ProviderContext session) {
        aclImpl.init();
        session.addRpcImplementation(AclService.class, aclImpl);
        logger.debug("AclProvider: onSessionInitiated(): done");
    }

    /*@Override
    protected void startImpl(BundleContext context) {
    }*/

    //Deprecated in Helium (seems yangtools ver. 0.6.2-SNAPSHOT)
    @Override
    public Collection<? extends ProviderFunctionality> getFunctionality() {
        return null;
    }

    //Deprecated in Helium (seems yangtools ver. 0.6.2-SNAPSHOT)
    @Override
    public void onSessionInitialized(ConsumerContext session) {
        // NOOP
    }

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

    public void setController(IController core) {
        this.controller = (IController)core;
        aclImpl.setController(controller);
    }

    public void unsetController(IController core) {
        if (this.controller == (IController)core) {
            this.controller = null;
        }
    }

}

