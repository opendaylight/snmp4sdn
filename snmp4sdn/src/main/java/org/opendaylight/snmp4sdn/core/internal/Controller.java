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
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.SNMPListener;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.SNMPMessage;
import org.opendaylight.snmp4sdn.protocol.SNMPType;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus.SNMPPortReason;
import org.openflow.util.HexString;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

public class Controller implements IController, CommandProvider {
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
        registerWithOSGIConsole();//s4s. in unit test, doesn't need. but need it when system test
    }
    public void init_forTest() {//s4s. same content as init(), but the last line is canceled
        logger.info("Controller: Initializing!");
        this.switches = new ConcurrentHashMap<Long, ISwitch>();
        this.switchEvents = new LinkedBlockingQueue<SwitchEvent>(MAXQUEUESIZE);
        this.messageListeners = new ConcurrentHashMap<SNMPType, IMessageListener>();
        this.switchStateListener = null;
        this.switchInstanceNumber = new AtomicInteger(0);
        //registerWithOSGIConsole();//s4s. in unit test, doesn't need. but need it when system test
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
        cmethUtil = new CmethUtil();
        snmpListener = new SNMPListener(this, cmethUtil);
        snmpListener.start();

        constructNetwork();//s4s: get switches from CmethUtil (i.e. a file), then for each of the switches, read their LLDP, and resolve all these LLDP data, then form the topology of the switches and their ports
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

    public void handleNewConnection(/*Selector selector,//s4s:OF's need
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
            logger.info("Switch({}) try to connected to the Controller", HexString.toHexString(sid));

            takeSwitchEventAdd(switchHandler);//s4s: in OF, this function is called in SwitchHandler, now we put it here directly
        /*} catch (IOException e) {
            return;
        }*///s4s: OF's
    }

    private void disconnectSwitch(ISwitch sw) {
        if (((SwitchHandler) sw).isOperational()) {
            Long sid = sw.getId();
            if (this.switches.remove(sid, sw)) {
                logger.info("{} is Disconnected", sw);
                notifySwitchDeleted(sw);
            }
        }
        ((SwitchHandler) sw).stop();
        sw = null;
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

    public void _controllerShowSwitches(CommandInterpreter ci) {
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

    public void constructNetwork(){
        ConcurrentMap<Long, Vector> entries = cmethUtil.getEntries();
        for(ConcurrentMap.Entry<Long, Vector> entry : entries.entrySet()){
            Long sid = entry.getKey();
            handleAddingSwitchAndItsPorts(sid);
        }
    }

    public void _constructNetwork(CommandInterpreter ci){
        ConcurrentMap<Long, Vector> entries = cmethUtil.getEntries();
        for(ConcurrentMap.Entry<Long, Vector> entry : entries.entrySet()){
            Long sid = entry.getKey();
            handleAddingSwitchAndItsPorts(sid);
        }
    }

    private void handleAddingSwitchAndItsPorts(Long sid){
        handleAddingSwitch(sid);
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
            logger.info("--> switch (ip: {}, sid: {}) already join in controller",  cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
        }
        else{
            logger.info("--> a new switch (ip: {}, sid: {}) join in controller", cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
            handleNewConnection(sid);
        }
        return sw;
    }

    private void scanAndAddPort(Long sid){
            short port;
            SNMPPhysicalPort phyPort;
            Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(sid);
            for(Map.Entry<Short, String> entry : portIDTable.entrySet()){
                String portName = entry.getValue();
                port = entry.getKey().shortValue();
                phyPort = new SNMPPhysicalPort(port);
                phyPort.setName(portName);
                logger.info("Add to switch (ip: {}, mac: {}) a new port, port number = {}", cmethUtil.getIpAddr(sid), HexString.toHexString(sid), port);
                handleAddingNewPort(sid, port, portName);
            }
    }

    private void handleAddingNewPort(Long sid, short port, String portName){
        ISwitch sw = switches.get(sid);
        if(sw == null)logger.warn("ISwitch sw is null!"); 

        SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
        phyPort.setName(portName);

        SNMPPortStatus portStatus = new SNMPPortStatus();
        portStatus.setDesc(phyPort);
        portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());

        /*((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
        ((Controller)controller).takeSwitchEventMsg(sw, portStatus);*///done in the line below
        ((SwitchHandler)sw).handleMessages(portStatus);
    }

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
