package org.opendaylight.snmp4sdn.internal;

import java.util.Collection;

//import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ConfigService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;

import org.opendaylight.snmp4sdn.core.IController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProvider implements BindingAwareProvider, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);

    private BindingAwareBroker broker;
    private BundleContext context;

    ConfigServiceImpl configImpl;

    IController controller = null;

    public ConfigProvider() {
        configImpl = new ConfigServiceImpl();
        if(configImpl == null){
            logger.debug("ConfigProvider: ConfigProviderImpl() creation fail");
        }
        logger.debug("ConfigProvider creation complete");
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
        configImpl.init();
        logger.debug("ConfigProvider: onSessionInitiated(): before session.addRpcImpl()");//OK
        session.addRpcImplementation(ConfigService.class, configImpl);
        logger.debug("ConfigProvider: onSessionInitiated(): after session.addRpcImpl()");//OK

        //TODO: strange!! When session.addRpcImplementation() is called, then the following lines would not happen

        //TODO: reference to onSessionInitiated() in OpenflowPluginProvider.java, the registrationManager.onSessionInitiated()

        
    }

    /*
    @Override
    protected void startImpl(BundleContext context) {
    }
    */


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
        configImpl.setController(controller);
    }

    public void unsetController(IController core) {
        if (this.controller == (IController)core) {
            this.controller = null;
        }
    }

}

