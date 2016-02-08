/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code base of OpenFlow plugin contributed by Cisco Systems, Inc. Their efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.core.internal;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.IMessageListener;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.ISwitchStateListener;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Config;
import org.opendaylight.controller.sal.core.State;
//import org.opendaylight.snmp4sdn.internal.ConfigService;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.SNMPListener;
import org.opendaylight.snmp4sdn.internal.VLANService;
import org.opendaylight.snmp4sdn.ICore;//karaf

//import org.opendaylight.snmp4sdn.VLANTable;//no-sal
//import org.opendaylight.controller.sal.vlan.VLANTable;//ad-sal

import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.SNMPMessage;
import org.opendaylight.snmp4sdn.protocol.SNMPType;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus.SNMPPortReason;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

public class Controller implements IController, ICore, CommandProvider {
    private static final Logger logger = LoggerFactory
            .getLogger(Controller.class);
    //private ControllerIO controllerIO;//s4s: is replaced by snmpListener
    private SNMPListener snmpListener;//s4s:to replace controllerIO
    private Thread switchEventThread;
    private ConcurrentHashMap<Long, ISwitch> switches;
    private BlockingQueue<SwitchEvent> switchEvents;
    // only 1 message listener per SNMPType
    private ConcurrentMap<SNMPType, IMessageListener> messageListeners;
    // only 1 switch state listener
    private ISwitchStateListener switchStateListener;
    private AtomicInteger switchInstanceNumber;
    private final int MAXQUEUESIZE = 50000;
    public CmethUtil cmethUtil;//s4s add
    public boolean isOtherBundleReady = false;

    /*
     * this thread monitors the switchEvents queue for new incoming events from
     * switch
     */
    private class EventHandler implements Runnable {
        @Override
        public void run() {
            logger.trace("Controller.EventHandler start running");
            while (true) {
                try {
                    SwitchEvent ev = switchEvents.take();
                    SwitchEvent.SwitchEventType eType = ev.getEventType();
                    ISwitch sw = ev.getSwitch();
                    switch (eType) {
                    case SWITCH_ADD:
                        logger.trace("enter Controller.EventHandler.SWITCH_ADD...");
                        Long sid = sw.getId();
                        ISwitch existingSwitch = switches.get(sid);
                        if (existingSwitch != null) {
                            logger.warn("Replacing existing {} with New {}",
                                    HexString.toHexString(existingSwitch.getId()), HexString.toHexString(sw.getId()));
                            disconnectSwitch(existingSwitch);
                        }
                        if(sw == null)logger.error("in EventHandler.SWITCH_ADD: ISwitch sw is null!");
                        switches.put(sid, sw);
                        notifySwitchAdded(sw);
                        break;
                    case SWITCH_DELETE:
                        disconnectSwitch(sw);
                        break;
                    case SWITCH_ERROR:
                        disconnectSwitch(sw);
                        break;
                    case SWITCH_MESSAGE:
                        logger.trace("new port event -- Controller.EventHandler()");
                        SNMPMessage msg = ev.getMsg();
                        if (msg != null) {
                            IMessageListener listener = messageListeners
                                    .get(msg.getType());
                            if (listener != null) {
                                logger.trace("call IMessageListener whose class name is " + listener.getClass().getName());
                                listener.receive(sw, msg);
                            }
                        }
                        break;
                    default:
                        logger.warn("Unknown switch event {}", eType.ordinal());
                    }
                } catch (InterruptedException e) {
                    switchEvents.clear();
                    return;
                }
            }
        }

    }

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        logger.info("snmp4sdn's Controller: Initializing!");
        this.switches = new ConcurrentHashMap<Long, ISwitch>();
        this.switchEvents = new LinkedBlockingQueue<SwitchEvent>(MAXQUEUESIZE);
        this.messageListeners = new ConcurrentHashMap<SNMPType, IMessageListener>();
        this.switchStateListener = null;
        this.switchInstanceNumber = new AtomicInteger(0);
        registerWithOSGIConsole();//s4s. in junit test, doesn't need. but need it when system test
        cmethUtil = new CmethUtil();
        cmethUtil.init();

        //Node.NodeIDType.registerIDType("SNMP", Long.class);
        //NodeConnector.NodeConnectorIDType.registerIDType("SNMP", Short.class, "SNMP");
    }
    public void init_forTest() {//s4s. same content as init(), but the last line is canceled
        logger.info("snmp4sdn's Controller: Initializing! (for junit test)");
        this.switches = new ConcurrentHashMap<Long, ISwitch>();
        this.switchEvents = new LinkedBlockingQueue<SwitchEvent>(MAXQUEUESIZE);
        this.messageListeners = new ConcurrentHashMap<SNMPType, IMessageListener>();
        this.switchStateListener = null;
        this.switchInstanceNumber = new AtomicInteger(0);
        //registerWithOSGIConsole();//s4s. in junit test, doesn't need. but need it when system test
        cmethUtil = new CmethUtil();
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    public void start() {
        logger.info("snmp4sdn's Controller: Starting!");
        /*
         * start a thread to handle event coming from the switch
         */
        switchEventThread = new Thread(new EventHandler(), "SwitchEvent Thread");
        switchEventThread.start();

        // spawn a thread to start to listen on the open flow port
        /*controllerIO = new ControllerIO(this);
        try {
            controllerIO.start();
        } catch (IOException ex) {
            logger.error("Caught exception while starting:", ex);
        }*///s4s. ControllerIO.java shows it just in charge of holding the socket. We don't need socket
        //s4s

        snmpListener = new SNMPListener(this, cmethUtil);
        snmpListener.start();

        //junit test
        //topologyDiscover();//s4s: get switches from CmethUtil (i.e. a file), then for each of the switches, read their LLDP, and resolve all these LLDP data, then form the topology of the switches and their ports

        //TODO: if SAL is 0.7 (or earlier), then call waitOtherNecessaryBundle(), otherwise dont' call. (because in Peregrine, which is in Hydrogen, SAL is 0.7)
            //we can use Bundle.getVersion() to know version!
        /*isOtherBundleReady = waitOtherNecessaryBundle();
        logger.info("\n\n=== SNMP4SDN: check other necessary bundle ready? --> {} ===\n\n", isOtherBundleReady ? "Yes" : "No");

        //cancelled-->Move "Topology Discovery" to the InventoryServiceShim.java started() and initTopologyDiscoveryFromCore(). Call topoDiscover() here is useless because DiscoveryService is not yet created done.
        logger.info("\n\n=== SNMP4SDN: trigger Topology Discovery ===\n\n");
        topoDiscover();*/
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy ()"
     * calls
     *
     */
    public void stop() {
        for (Iterator<Entry<Long, ISwitch>> it = switches.entrySet().iterator(); it
                .hasNext();) {
            Entry<Long, ISwitch> entry = it.next();
            ((SwitchHandler) entry.getValue()).stop();
            it.remove();
        }
        switchEventThread.interrupt();
        snmpListener.stopListening();

        /*try {
            controllerIO.shutDown();
        } catch (IOException ex) {
            logger.error("Caught exception while stopping:", ex);
        }*///s4s: controllerIO is abandonded in s4s
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    public void destroy() {
    }

    @Override
    public void addMessageListener(SNMPType type, IMessageListener listener) {
        IMessageListener currentListener = this.messageListeners.get(type);
        if (currentListener != null) {
            logger.warn("{} is already listened by {}", type,
                    currentListener);
        }
        this.messageListeners.put(type, listener);
        logger.warn("{} is now listened by {}", type, listener);
    }

    @Override
    public void removeMessageListener(SNMPType type, IMessageListener listener) {
        IMessageListener currentListener = this.messageListeners.get(type);
        if ((currentListener != null) && (currentListener == listener)) {
            logger.trace("{} listener {} is Removed", type, listener);
            this.messageListeners.remove(type);
        }
    }

    @Override
    public void addSwitchStateListener(ISwitchStateListener listener) {
        if (this.switchStateListener != null) {
            logger.warn("Switch events are already listened by {}",
                    this.switchStateListener);
        }
        this.switchStateListener = listener;
        logger.trace("Switch events are now listened by {}", listener);
    }

    @Override
    public void removeSwitchStateListener(ISwitchStateListener listener) {
        if ((this.switchStateListener != null)
                && (this.switchStateListener == listener)) {
            logger.trace("SwitchStateListener {} is Removed", listener);
            this.switchStateListener = null;
        }
    }

    private boolean waitOtherNecessaryBundle(){
        
        /*//It seems we can't check whether SAL bundle exists, because we need to check SAL impl bundle, but this bundle has no any exported Class or Interface so that we can't use FrameworkUtil.getBundle() to get the bundle.
            //Fortunately, it's fine to skip checking SAL bundle.
        Bundle salTopoBundle = null;
        while(salTopoBundle == null){
            logger.debug("waitOtherNecessaryBundle(): snmp4sdn plugin required bundle \"org.opendaylight.controller.sal.implementation.internal.Topology\" is null");
            try{
                Class salClass = Class.forName("org.opendaylight.controller.sal.implementation.internal.Topology");
                //Class<org.opendaylight.controller.sal.implementation.internal.Topology> salClass = (org.opendaylight.controller.sal.implementation.internal.Topology)Class.forName("org.opendaylight.controller.sal.implementation.internal.Topology");
                salTopoBundle = FrameworkUtil.getBundle(salClass);
            }catch(Exception e1){
                logger.debug("waitOtherNecessaryBundle(): try to get Class of \"org.opendaylight.controller.sal.implementation.internal.Topology\" error: {}", e1);
            }
            try{
                Thread.sleep(1000);
            }catch(Exception e2){
                logger.debug("waitOtherNecessaryBundle(): waiting for salTopoBundle to be installed, Thread.sleep() error: {}", e2);
            }
        }
        while(salTopoBundle.getState() != org.osgi.framework.Bundle.ACTIVE){
            try{
                Thread.sleep(1000);
            }catch(Exception e1){
                logger.debug("waitOtherNecessaryBundle(): waiting for salTopoBundle to ready, Thread.sleep() error: {}", e1);
            }
        }
        logger.debug("waitOtherNecessaryBundle(): snmp4sdn plugin required bundle \"org.opendaylight.controller.sal.implementation.internal.Topology\" is ready");
        */

        /*//remove the checking for topoMgr because nsf.managers feature can't be installed successfully
        Bundle topoMgrBundle = null;
        while(topoMgrBundle == null){
            try{
                topoMgrBundle = FrameworkUtil.getBundle(Class.forName("org.opendaylight.controller.topologymanager.TopologyUserLinkConfig"));
                if(topoMgrBundle == null)
                    logger.debug("waitOtherNecessaryBundle(): snmp4sdn plugin required bundle TopologyManager not there (try get Class \"org.opendaylight.controller.topologymanager.TopologyUserLinkConfig\" but gets null)");
            }catch(Exception e1){
                logger.debug("waitOtherNecessaryBundle(): try to get Class of \"org.opendaylight.controller.topologymanager.TopologyUserLinkConfig\" error: {}", e1);
                return false;
            }
            try{
                Thread.sleep(1000);
            }catch(Exception e1){
                logger.debug("waitOtherNecessaryBundle(): waiting for topoMgrBundle to be installed, Thread.sleep() error: {}", e1);
                return false;
            }
        }*/
        /*TODO: need fix!!
            actually we need the following while() to ensure TopologyManager bundle is active.
            But with this while(), pop up many registration-like errors,
            and we see topologymanager bundle stays 'resolving' state and snmp4sdn bundle stays 'starting' state.
            So we cancel this code, since it's fine that snmp4sdn will do periodic topology discovery,
            so even topology manager is not ready when snmp4sdn reports edges,
            the edges miss to be received by topology manager will be known in next time topology discovery.
        */
        /*while(topoMgrBundle.getState() != org.osgi.framework.Bundle.ACTIVE){
            try{
                Thread.sleep(1000);
            }catch(Exception e2){
                logger.debug("waitOtherNecessaryBundle(): waiting for topoMgrBundle to ready, Thread.sleep() error: {}", e2);
                return false;
            }
        }*/
        logger.debug("waitOtherNecessaryBundle(): snmp4sdn plugin required bundle TopologyManager is ready (got by Class \"org.opendaylight.controller.topologymanager.TopologyUserLinkConfig\")");

        //TODO: switch manager is also required, but its bundle has no exposed Class nor Interface so that we can't get the bundle.

        return true;
    }

    public ISwitch handleNewConnection(/*Selector selector,//s4s:OF's need
            SelectionKey serverSelectionKey*/Long sid) {//s4s: in OF, this function is called in ControllerIO, now in s4s it is called in SNMPListener
        //ServerSocketChannel ssc = (ServerSocketChannel) serverSelectionKey.channel();//s4s:OF's need
        //SocketChannel sc = null;//s4s:OF's need
        //try {//s4s: OF's
            //sc = ssc.accept();//s4s:OF's need
            // create new switch
            int i = this.switchInstanceNumber.addAndGet(1);
            String instanceName = "SwitchHandler-" + i;
            SwitchHandler switchHandler = new SwitchHandler(this, /*sc,*///s4s:OF's need
                    instanceName);
            switchHandler.setId(sid);
            switchHandler.start();
            /*if (sc.isConnected()) {
                logger.info("Switch:{} is connected to the Controller",
                        sc.socket().getRemoteSocketAddress()
                        .toString().split("/")[1]);
            }*///s4s:OF's
            logger.info("Add switch({}) to the Controller", HexString.toHexString(sid));

            //takeSwitchEventAdd(switchHandler);//s4s: in OF, this function is called in SwitchHandler, now we put it here directly
            //copy the following from "EventHandler's case SWITCH_ADD" to replace the "takeSwitchEventAdd(switchHandler)" above
            switches.put(sid, switchHandler);
            notifySwitchAdded(switchHandler);

            return switchHandler;
        /*} catch (IOException e) {
            return;
        }*///s4s: OF's
    }

    private void disconnectSwitch(ISwitch sw) {
        //if (((SwitchHandler) sw).isOperational()) {//s4s: no need to check isOperational
            Long sid = sw.getId();
            if (this.switches.remove(sid, sw)) {
                logger.info("switch {} is Disconnected", HexString.toHexString(sid));
                notifySwitchDeleted(sw);
            }
        //}//s4s: no need to check isOperational
        ((SwitchHandler) sw).stop();
        sw = null;
    }

    //add by fixing "topology discovery 2 step to 1 step"
    public void notifyMessageListener(ISwitch sw, SNMPMessage msg){
        if (msg != null) {
            IMessageListener listener = messageListeners.get(msg.getType());
            if (listener != null) {
                //logger.trace("notifyMessageListener(): now will call ({})IMessageListener.receive(), with switch {} msg: {}", listener.getClass().getName(), sw.getId(), msg);
                listener.receive(sw, msg);
            }
        }
    }

    private void notifySwitchAdded(ISwitch sw) {
        if (switchStateListener != null) {
            switchStateListener.switchAdded(sw);
        }
    }

    private void notifySwitchDeleted(ISwitch sw) {
        if (switchStateListener != null) {
            switchStateListener.switchDeleted(sw);
        }
    }

    private synchronized void addSwitchEvent(SwitchEvent event) {
        try {
            this.switchEvents.put(event);
        } catch (InterruptedException e) {
            logger.warn("SwitchEvent caught Interrupt Exception");
        }
    }

    public void takeSwitchEventAdd(ISwitch sw) {
        if(sw == null)logger.warn("in takeSwitchEventAdd: ISwitch sw is null!");
        SwitchEvent ev = new SwitchEvent(
                SwitchEvent.SwitchEventType.SWITCH_ADD, sw, null);
        addSwitchEvent(ev);
    }

    public void takeSwitchEventDelete(ISwitch sw) {
        SwitchEvent ev = new SwitchEvent(
                SwitchEvent.SwitchEventType.SWITCH_DELETE, sw, null);
        addSwitchEvent(ev);
    }

    public void takeSwitchEventError(ISwitch sw) {
        SwitchEvent ev = new SwitchEvent(
                SwitchEvent.SwitchEventType.SWITCH_ERROR, sw, null);
        addSwitchEvent(ev);
    }

    public void takeSwitchEventMsg(ISwitch sw, SNMPMessage msg) {
        logger.trace("new port event-- Controller.takeSwitchEventMsg(event) and will call addSwitchEvent() in which calls switchEvents.put(event)");
        if (messageListeners.get(msg.getType()) != null) {
            SwitchEvent ev = new SwitchEvent(
                    SwitchEvent.SwitchEventType.SWITCH_MESSAGE, sw, msg);
            addSwitchEvent(ev);
        }
    }

    @Override
    public Map<Long, ISwitch> getSwitches() {
        return this.switches;
    }

    @Override
    public ISwitch getSwitch(Long switchId) {
        return this.switches.get(switchId);
    }

    @Override//karaf
    public void readDB(String filepath){
        cmethUtil.readDB(filepath);
        cmethUtil.printDB();
    }

    @Override//karaf
    public void printDB(){
        cmethUtil.printDB();
    }

    @Override//karaf
    public void topoDiscover(){
        int count = 0;
        while(switchStateListener == null){
            try{
                logger.info("\nTopology Discovery halts, waiting for related modules to be ready, then can proceed\n");
                Thread.sleep(1000);
                count += 1;
                if(count > 20){
                    logger.info("\nTimeout of Topology Discovery waiting for related modules to be ready, cancel Topology Discovery\n");
                    break;
                }
            }catch(Exception e1){
                logger.debug("ERROR: topoDiscover(): Thread.sleept() error: {}", e1);
            }
        }
        switchStateListener.disableNewInventoryTriggerDiscovery();
        topologyDiscoverSwitchesAndPorts();
        topologyDiscoverEdges();
        switchStateListener.enableNewInventoryTriggerDiscovery();
        //TODO Issue: in topologyDiscoverSwitchesAndPorts(), new port triggers edges discovery, but maybe due to the racing of 'new port' and 'detected edge's remote port not yet detected", which cause discovery completion delay (with retry mechanism in DiscoveryService, finally complete discovery). So we also directly do topologyDiscoverEdges() to finish.
        //TODO: if the issue sovled, remember to modify topoDiscover() accordingly
    }

    @Override//karaf
    //Just copy topoDiscover()'s code, but remove calling the topologyDiscoverEdges()
    public void topoDiscoverSwitch(){
        int count = 0;
        while(switchStateListener == null){
            try{
                logger.info("\nTopology Discovery halts, waiting for related modules to be ready, then can proceed\n");
                Thread.sleep(1000);
                count += 1;
                if(count > 20){
                    logger.info("\nTimeout of Topology Discovery waiting for related modules to be ready, cancel Topology Discovery\n");
                    break;
                }
            }catch(Exception e1){
                logger.debug("ERROR: topoDiscover(): Thread.sleept() error: {}", e1);
            }
        }
        switchStateListener.disableNewInventoryTriggerDiscovery();
        topologyDiscoverSwitchesAndPorts();
        switchStateListener.enableNewInventoryTriggerDiscovery();
        //TODO Issue: in topologyDiscoverSwitchesAndPorts(), new port triggers edges discovery, but maybe due to the racing of 'new port' and 'detected edge's remote port not yet detected", which cause discovery completion delay (with retry mechanism in DiscoveryService, finally complete discovery). So we also directly do topologyDiscoverEdges() to finish.
        //TODO: if the issue sovled, remember to modify topoDiscover() accordingly
    }


    @Override//karaf
    public void topoDiscoverEdge(){
        topologyDiscoverEdges();
    }

    /*@Override//karaf
    public void addVLANSetPorts(String sw_mac, String vlanID, String vlanName, String portList){
        s4sAddVLANandSetPorts(sw_mac, vlanID, vlanName, portList);
    }

    @Override//karaf
    public void deleteVLAN(String sw_mac, String vlanID){   
        s4sDeleteVLAN_execute(sw_mac, vlanID);
    }

    @Override//karaf
    public void printVLANTable(String sw_mac){
        s4sPrintVLANTable_execute(sw_mac);
    }*/

    public void _s4sControllerShowSwitches(CommandInterpreter ci) {
        Set<Long> sids = switches.keySet();
        StringBuffer s = new StringBuffer();
        int size = sids.size();
        if (size == 0) {
            ci.print("switches: empty");
            return;
        }
        Iterator<Long> iter = sids.iterator();
        s.append("Total: " + size + " switches\n");
        while (iter.hasNext()) {
            Long sid = iter.next();
            Date date = switches.get(sid).getConnectedDate();
            String switchInstanceName = ((SwitchHandler) switches.get(sid))
                    .getInstanceName();
            s.append(switchInstanceName + "/" + HexString.toHexString(sid)
                    + " connected since " + date.toString() + "\n");
        }
        ci.print(s.toString());
        return;
    }

    public void _controllerReset(CommandInterpreter ci) {
        ci.print("...Disconnecting the communication to all switches...\n");
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        } finally {
            ci.print("...start to accept connections from switches...\n");
            start();
        }
    }

    public void _controllerShowConnConfig(CommandInterpreter ci) {
        String str = System.getProperty("secureChannelEnabled");
        if ((str != null) && (str.trim().equalsIgnoreCase("true"))) {
            ci.print("The Controller and Switch should communicate through TLS connetion.\n");

            String keyStoreFile = System.getProperty("controllerKeyStore");
            String trustStoreFile = System.getProperty("controllerTrustStore");
            if ((keyStoreFile == null) || keyStoreFile.trim().isEmpty()) {
                ci.print("controllerKeyStore not specified in ./configuration/config.ini\n");
            } else {
                ci.print("controllerKeyStore=" + keyStoreFile + "\n");
            }
            if ((trustStoreFile == null) || trustStoreFile.trim().isEmpty()) {
                ci.print("controllerTrustStore not specified in ./configuration/config.ini\n");
            } else {
                ci.print("controllerTrustStore=" + trustStoreFile + "\n");
            }
        } else {
            ci.print("The Controller and Switch should communicate through TCP connetion.\n");
        }
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    public void topologyDiscoverSwitchesAndPorts(){
        logger.trace("Remove existing switches...");
        for(ConcurrentHashMap.Entry<Long, ISwitch> entry : switches.entrySet()){
            ISwitch sw = entry.getValue();
            logger.trace("\tSwitch {} is being removed", HexString.toHexString(sw.getId()));
            //takeSwitchEventDelete(sw);
            disconnectSwitch(sw);//copy from "EventHandler's case SWITCH_DELETE"to replace the "takeSwitchEventDelete(sw)" above
        }

        try{
                Thread.sleep(2000);
        }catch(Exception e1){
            logger.debug("ERROR: topologyDiscoverSwitchesAndPorts(): Thread.sleept() error: {}", e1);
        }

        ConcurrentMap<Long, Vector> entries = cmethUtil.getEntries();//TODO: query DB instead
        for(ConcurrentMap.Entry<Long, Vector> entry : entries.entrySet()){
            Long sid = entry.getKey();
            handleAddingSwitchAndItsPorts(sid);
        }

        //topologyDiscoverEdges();
    }

    private void topologyDiscoverEdges(){
        logger.debug("\n\nBegin Topology resolving by retrieving LLDP data from switches\n\n");
        switchStateListener.doTopoDiscover();
        logger.debug("\n\nFinish Topology resolving by retrieving LLDP data from switches\n\n");
    }

    public void _s4sTopoDiscover(CommandInterpreter ci){
         topoDiscover();
    }

    public void _s4sTopoDiscoverSwitchesAndPorts(CommandInterpreter ci){
        topologyDiscoverSwitchesAndPorts();
    }

    public void _s4sTopoDiscoverEdges(CommandInterpreter ci){
        topologyDiscoverEdges();
    }

    private void handleAddingSwitchAndItsPorts(Long sid){
        ISwitch sw = handleAddingSwitch(sid);
        if(sw == null){
            logger.debug("handleAddingSwitch(sid:{}) fails, so skip to proceed scanAndAddPort(sid:{})", sid, sid);
            return;
        }
        while(this.switches.get(sid) == null){
            logger.trace("snmp4sdn-controller.handleAddingSwitch({}) not yet done", sid);
            try{
                Thread.sleep(500);
            }catch(Exception e){;}
        }
        logger.trace("snmp4sdn-controller.handleAddingSwitch({}) is done", sid);
        scanAndAddPort(sid);
    }

    private ISwitch handleAddingSwitch(Long sid){
        ISwitch sw = switches.get(sid);
        if(sw != null){
            logger.debug("In Controller.handleAddingSwitch(), switch (ip: {}, sid: {}) is already added in controller",  cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
        }
        else{
            //logger.debug("In Controller.handleAddingSwitch(), try to add a new switch (ip: {}, sid: {}) to controller", cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
            if(!checkNodeExists(sid)){
                logger.debug("ERROR: The node {} does not exists in network (decided by we can't read its LLDP chassis)", sid);
                return null;
            }
            sw = handleNewConnection(sid);
        }
        return sw;
    }

    private boolean checkNodeExists(Long sid){
        String chassis = (new SNMPHandler(cmethUtil)).getLLDPChassis(sid);
        if(chassis == null)
            return false;
        return true;
    }

    private void scanAndAddPort(Long sid){
            short port;
            SNMPPhysicalPort phyPort;
            Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(sid);
            Map<Short, Integer> portStateTable = (new SNMPHandler(cmethUtil)).readPortState(sid);
            
            for(Map.Entry<Short, String> entry : portIDTable.entrySet()){
                String portName = entry.getValue();
                port = entry.getKey().shortValue();
                phyPort = new SNMPPhysicalPort(port);
                phyPort.setName(portName);

                Integer portState = portStateTable.get(port);
                if(portState == null){
                    logger.error("ERROR: scanAndAddPort(): portStateTable has no entry for port {}", port);
                    return;
                }
                if(portState == 1){//standard snmp MIB: ifAdminStatus: 1 as up, 2 as down
                    logger.info("Add to switch (ip: {}, mac: {}) a new port, port number = {}, state = UP", cmethUtil.getIpAddr(sid), HexString.toHexString(sid), port);
                    handleAddingNewPort(sid, port, portName, Config.ADMIN_UP);
                }
                else if(portState == 2){//standard snmp MIB: ifAdminStatus: 1 as up, 2 as down
                    logger.info("Add to switch (ip: {}, mac: {}) a new port, port number = {}, state = DOWN", cmethUtil.getIpAddr(sid), HexString.toHexString(sid), port);
                    handleAddingNewPort(sid, port, portName, Config.ADMIN_DOWN);
                }
                else{
                    logger.info("WARNING: Add to switch (ip: {}, mac: {}) a new port, port number = {}, state = UNKNOWN", cmethUtil.getIpAddr(sid), HexString.toHexString(sid), port);
                    handleAddingNewPort(sid, port, portName, Config.ADMIN_UNDEF);
                }
            }
    }

    private void handleAddingNewPort(Long sid, short port, String portName, int config){
        ISwitch sw = switches.get(sid);
        if(sw == null)logger.warn("ISwitch sw is null!"); 

        SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
        phyPort.setName(portName);
        phyPort.setConfig(config);//Bug fix: "port on/off" is not "add/remove port".

        SNMPPortStatus portStatus = new SNMPPortStatus();
        portStatus.setDesc(phyPort);
        portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());

        /*((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
        ((Controller)controller).takeSwitchEventMsg(sw, portStatus);*///done in the line below
        ((SwitchHandler)sw).handleMessages(portStatus);
    }

    /*public void _s4sTestCLI(CommandInterpreter ci){
        System.out.println("enter _s4sTestCLI 1");
        new ConfigService().TestCLI();
        System.out.println("enter _s4sTestCLI 2");
    }*/

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---Open Flow Controller---\n");
        help.append("\t controllerShowSwitches\n");
        help.append("\t controllerReset\n");
        help.append("\t controllerShowConnConfig\n");
        return help.toString();
    }

    public void addSwitch(ISwitch sw){//s4s add. just for convenient for test, actually we don't need this function
        Long sid = sw.getId();
        switches.put(sid, sw);
    }

    public CmethUtil getCmethUtil(){//s4s add. just for convenient for test, actually we don't need this function
        return cmethUtil;
    }
}
