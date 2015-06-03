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

package org.opendaylight.snmp4sdn.internal;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;
import org.opendaylight.snmp4sdn.IInventoryShimInternalListener;
import org.opendaylight.snmp4sdn.IStatisticsListener;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.IMessageListener;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.ISwitchStateListener;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Drop;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.action.SupportedFlowActions;
import org.opendaylight.controller.sal.core.Actions;
import org.opendaylight.controller.sal.core.Buffers;
import org.opendaylight.controller.sal.core.Capabilities;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.ContainerFlow;
import org.opendaylight.controller.sal.core.Description;
import org.opendaylight.controller.sal.core.IContainerAware;
import org.opendaylight.controller.sal.core.IContainerListener;
import org.opendaylight.controller.sal.core.MacAddress;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.Tables;
import org.opendaylight.controller.sal.core.TimeStamp;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.utils.GlobalConstants;

import org.opendaylight.snmp4sdn.protocol.SNMPMessage;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus.SNMPPortReason;
import org.opendaylight.snmp4sdn.protocol.SNMPType;
import org.opendaylight.snmp4sdn.protocol.statistics.SNMPDescriptionStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class describes a shim layer that bridges inventory events from Openflow
 * core to various listeners. The notifications are filtered based on container
 * configurations.
 *
 *
 */
public class InventoryServiceShim implements IContainerListener,
        IMessageListener, ISwitchStateListener, IStatisticsListener, IContainerAware {
    protected static final Logger logger = LoggerFactory
            .getLogger(InventoryServiceShim.class);
    private IController controller = null;
    private final ConcurrentMap<String, IInventoryShimInternalListener> inventoryShimInternalListeners = new ConcurrentHashMap<String, IInventoryShimInternalListener>();
    private final List<IInventoryShimExternalListener> inventoryShimExternalListeners = new CopyOnWriteArrayList<IInventoryShimExternalListener>();
    private final ConcurrentMap<NodeConnector, List<String>> containerMap = new ConcurrentHashMap<NodeConnector, List<String>>();

    void setController(IController s) {
        this.controller = s;
    }

    void unsetController(IController s) {
        if (this.controller == s) {
            this.controller = null;
        }
    }

    void setInventoryShimInternalListener(Map<?, ?> props,
            IInventoryShimInternalListener s) {
        if (props == null) {
            logger.error("setInventoryShimInternalListener property is null");
            return;
        }
        String containerName = (String) props.get("containerName");
        if (containerName == null) {
            logger.error("setInventoryShimInternalListener containerName not supplied");
            return;
        }
        if ((this.inventoryShimInternalListeners != null)
                && !this.inventoryShimInternalListeners.containsValue(s)) {
            this.inventoryShimInternalListeners.put(containerName, s);
            logger.trace(
                    "Added inventoryShimInternalListener for container {}",
                    containerName);
        }
    }

    void unsetInventoryShimInternalListener(Map<?, ?> props,
            IInventoryShimInternalListener s) {
        if (props == null) {
            logger.error("unsetInventoryShimInternalListener property is null");
            return;
        }
        String containerName = (String) props.get("containerName");
        if (containerName == null) {
            logger.error("unsetInventoryShimInternalListener containerName not supplied");
            return;
        }
        if ((this.inventoryShimInternalListeners != null)
                && this.inventoryShimInternalListeners.get(containerName) != null
                && this.inventoryShimInternalListeners.get(containerName)
                        .equals(s)) {
            this.inventoryShimInternalListeners.remove(containerName);
            logger.trace(
                    "Removed inventoryShimInternalListener for container {}",
                    containerName);
        }
    }

    void setInventoryShimExternalListener(IInventoryShimExternalListener s) {
        logger.trace("Set inventoryShimExternalListener");
        if ((this.inventoryShimExternalListeners != null)
                && !this.inventoryShimExternalListeners.contains(s)) {
            this.inventoryShimExternalListeners.add(s);
        }
    }

    void unsetInventoryShimExternalListener(IInventoryShimExternalListener s) {
        if ((this.inventoryShimExternalListeners != null)
                && this.inventoryShimExternalListeners.contains(s)) {
            this.inventoryShimExternalListeners.remove(s);
        }
    }

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    void init() {
        this.controller.addMessageListener(SNMPType.PORT_STATUS, this);
        this.controller.addSwitchStateListener(this);
    }

    /**
     * Function called after registering the service in OSGi service registry.
     */
    void started() {
        /* Start with existing switches */
        startService();
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
        this.controller.removeMessageListener(SNMPType.PACKET_IN, this);
        this.controller.removeSwitchStateListener(this);

        this.inventoryShimInternalListeners.clear();
        this.containerMap.clear();
        this.controller = null;
    }

    @Override
    public void receive(ISwitch sw, SNMPMessage msg) {
        try {
            if (msg instanceof SNMPPortStatus) {
                handlePortStatusMessage(sw, (SNMPPortStatus) msg);
            }
        } catch (ConstructionException e) {
            logger.warn("",e);
        }
        return;
    }

    protected void handlePortStatusMessage(ISwitch sw, SNMPPortStatus m)
            throws ConstructionException {
        Node node = new Node("SNMP", sw.getId());
        NodeConnector nodeConnector = PortConverter.toNodeConnector(m.getDesc()
                .getPortNumber(), node);
        UpdateType type = null;

        if (m.getReason() == (byte) SNMPPortReason.SNMPPPR_ADD.ordinal()) {
            type = UpdateType.ADDED;
        } else if (m.getReason() == (byte) SNMPPortReason.SNMPPPR_DELETE.ordinal()) {
            type = UpdateType.REMOVED;
        } else if (m.getReason() == (byte) SNMPPortReason.SNMPPPR_MODIFY.ordinal()) {
            type = UpdateType.CHANGED;
        }

        logger.trace("handlePortStatusMessage {} type {}", nodeConnector, type);
        //logger.debug("new port event: [Node " + (Long)(nodeConnector.getNode().getID()) + "(" + nodeConnector.getNode().getType() + ")" + " port " + (Short)(nodeConnector.getID()) + "(" + nodeConnector.getType() + ")" + "] -- InventoryServiceShim.handlePortStatusMessage()");

        if (type != null) {
            // get node connector properties
            Set<Property> props = InventoryServiceHelper.SNMPPortToProps(m
                    .getDesc());
            notifyInventoryShimListener(nodeConnector, type, props);
        }
    }

    @Override
    public void switchAdded(ISwitch sw) {
        if (sw == null) {
            return;
        }

    boolean isTrapMechnismCancled = true;//s4s: if true, trap mechanism is cancled
    if(isTrapMechnismCancled){//s4s:directly do what the "notifyInventoryShimListener()-->notifyInventoryShimExternalListener()" at the else section below will do.
        for (IInventoryShimExternalListener s : this.inventoryShimExternalListeners) {
            //logger.trace("new port event: InventoryServiceShim.notifyInventoryShimExternalListener(), then now call to DiscoveryService.doEthSwDiscovery()");
            if(s.getClass().getName().equals(DiscoveryService.class.getName()))
                ((DiscoveryService)s).doEthSwDiscovery();
        }
    }
    else{
        // Add all the nodeConnectors of this switch
        Map<NodeConnector, Set<Property>> ncProps = InventoryServiceHelper
                .SNMPSwitchToProps(sw);
        for (Map.Entry<NodeConnector, Set<Property>> entry : ncProps.entrySet()) {
            notifyInventoryShimListener(entry.getKey(), UpdateType.ADDED,
                    entry.getValue());
        }
    }

        // Add this node
        addNode(sw);
    }

    @Override
    public void switchDeleted(ISwitch sw) {
        if (sw == null) {
            return;
        }

        removeNode(sw);
    }

    @Override
    public void containerModeUpdated(UpdateType t) {
        // do nothing
    }

    @Override
    public void tagUpdated(String containerName, Node n, short oldTag,
            short newTag, UpdateType t) {
        logger.trace("tagUpdated: {} type {} for container {}", new Object[] {
                n, t, containerName });
    }

    @Override
    public void containerFlowUpdated(String containerName,
            ContainerFlow previousFlow, ContainerFlow currentFlow, UpdateType t) {
    }

    @Override
    public void nodeConnectorUpdated(String containerName, NodeConnector p,
            UpdateType t) {
        logger.trace("nodeConnectorUpdated: {} type {} for container {}",
                new Object[] { p, t, containerName });
        if (this.containerMap == null) {
            logger.warn("containerMap is NULL");
            return;
        }
        List<String> containers = this.containerMap.get(p);
        if (containers == null) {
            containers = new CopyOnWriteArrayList<String>();
        }
        boolean updateMap = false;
        switch (t) {
        case ADDED:
            if (!containers.contains(containerName)) {
                containers.add(containerName);
                updateMap = true;
            }
            break;
        case REMOVED:
            if (containers.contains(containerName)) {
                containers.remove(containerName);
                updateMap = true;
            }
            break;
        case CHANGED:
            break;
        }
        if (updateMap) {
            if (containers.isEmpty()) {
                // Do cleanup to reduce memory footprint if no
                // elements to be tracked
                this.containerMap.remove(p);
            } else {
                this.containerMap.put(p, containers);
            }
        }

        // notify InventoryService
        notifyInventoryShimInternalListener(containerName, p, t, null);
        notifyInventoryShimInternalListener(containerName, p.getNode(), t, null);
    }

    private void notifyInventoryShimExternalListener(Node node,
            UpdateType type, Set<Property> props) {
        for (IInventoryShimExternalListener s : this.inventoryShimExternalListeners) {
            s.updateNode(node, type, props);
        }
    }

    private void notifyInventoryShimExternalListener(
            NodeConnector nodeConnector, UpdateType type, Set<Property> props) {
        for (IInventoryShimExternalListener s : this.inventoryShimExternalListeners) {
            //logger.trace("new port event: InventoryServiceShim.notifyInventoryShimExternalListener(), then now call to DiscoveryService.updateNodeConnector()");
            s.updateNodeConnector(nodeConnector, type, props);
        }
    }

    private void notifyInventoryShimInternalListener(String container,
            NodeConnector nodeConnector, UpdateType type, Set<Property> props) {
        IInventoryShimInternalListener inventoryShimInternalListener = inventoryShimInternalListeners
                .get(container);
        if (inventoryShimInternalListener != null) {
            inventoryShimInternalListener.updateNodeConnector(nodeConnector,
                    type, props);
            logger.trace(
                    "notifyInventoryShimInternalListener {} type {} for container {}",
                    new Object[] { nodeConnector, type, container });
        }
    }

    /*
     * Notify all internal and external listeners
     */
    private void notifyInventoryShimListener(NodeConnector nodeConnector,
            UpdateType type, Set<Property> props) {
        // Always notify default InventoryService. Store properties in default
        // one.
        notifyInventoryShimInternalListener(GlobalConstants.DEFAULT.toString(),
                nodeConnector, type, props);

        // Now notify other containers
        List<String> containers = containerMap.get(nodeConnector);
        if (containers != null) {
            for (String container : containers) {
                // no property stored in container components.
                notifyInventoryShimInternalListener(container, nodeConnector,
                        type, null);
            }
        }

        // Notify DiscoveryService
        notifyInventoryShimExternalListener(nodeConnector, type, props);
    }

    /*
     * Notify all internal and external listeners
     */
    private void notifyInventoryShimListener(Node node, UpdateType type,
            Set<Property> props) {
        switch (type) {
        case ADDED:
            // Notify only the default Inventory Service
            IInventoryShimInternalListener inventoryShimDefaultListener = inventoryShimInternalListeners
                    .get(GlobalConstants.DEFAULT.toString());
            if (inventoryShimDefaultListener != null) {
                inventoryShimDefaultListener.updateNode(node, type, props);
            }
            break;
        case REMOVED:
            // Notify all Inventory Service containers
            for (IInventoryShimInternalListener inventoryShimInternalListener : inventoryShimInternalListeners
                    .values()) {
                inventoryShimInternalListener.updateNode(node, type, null);
            }
            break;
        case CHANGED:
            // Notify only the default Inventory Service
            inventoryShimDefaultListener = inventoryShimInternalListeners
                    .get(GlobalConstants.DEFAULT.toString());
            if (inventoryShimDefaultListener != null) {
                inventoryShimDefaultListener.updateNode(node, type, props);
            }
            break;
        default:
            break;
        }

        // Notify external listener
        notifyInventoryShimExternalListener(node, type, props);
    }

    private void notifyInventoryShimInternalListener(String container,
            Node node, UpdateType type, Set<Property> props) {
        IInventoryShimInternalListener inventoryShimInternalListener = inventoryShimInternalListeners
                .get(container);
        if (inventoryShimInternalListener != null) {
            inventoryShimInternalListener.updateNode(node, type, props);
            logger.trace(
                    "notifyInventoryShimInternalListener {} type {} for container {}",
                    new Object[] { node, type, container });
        }
    }

    private void addNode(ISwitch sw) {
        Node node;
        try {
            node = new Node(/*NodeIDType.OPENFLOW*/"SNMP", sw.getId());
        } catch (ConstructionException e) {
            logger.error("{}", e.getMessage());
            return;
        }

        UpdateType type = UpdateType.ADDED;

        Set<Property> props = new HashSet<Property>();
        Long sid = (Long) node.getID();

        Date connectedSince = controller.getSwitches().get(sid)
                .getConnectedDate();
        Long connectedSinceTime = (connectedSince == null) ? 0 : connectedSince
                .getTime();
        props.add(new TimeStamp(connectedSinceTime, "connectedSince"));
        props.add(new MacAddress(deriveMacAddress(sid)));

        byte tables = sw.getTables();
        Tables t = new Tables(tables);
        if (t != null) {
            props.add(t);
        }
        int cap = sw.getCapabilities();
        Capabilities c = new Capabilities(cap);
        if (c != null) {
            props.add(c);
        }
        int act = sw.getActions();
        //Actions a = new Actions(act);
        SupportedFlowActions a = new SupportedFlowActions(getFlowActions(act));//replace the previous line with this line, to adpat to certain changes for information validation in ODL. Note that "getFlowActions(act)" should be "FlowConverter.getFlowActions(act)", FlowConverter is not ready currently
        if (a != null) {
            props.add(a);
        }
        int buffers = sw.getBuffers();
        Buffers b = new Buffers(buffers);
        if (b != null) {
            props.add(b);
        }

        // Notify all internal and external listeners
        notifyInventoryShimListener(node, type, props);
    }

    private void removeNode(ISwitch sw) {
        Node node;
        try {
            node = new Node(/*NodeIDType.OPENFLOW*/"SNMP", sw.getId());
        } catch (ConstructionException e) {
            logger.error("{}", e.getMessage());
            return;
        }

        UpdateType type = UpdateType.REMOVED;

        // Notify all internal and external listeners
        notifyInventoryShimListener(node, type, null);
    }

    private void startService() {
        // Get a snapshot of all the existing switches
        Map<Long, ISwitch> switches = this.controller.getSwitches();
        for (ISwitch sw : switches.values()) {
            switchAdded(sw);
        }
    }

    @Override
    public void descriptionRefreshed(Long switchId,
            SNMPDescriptionStatistics descriptionStats) {
        Node node;
        try {
            node = new Node(/*NodeIDType.OPENFLOW*/"SNMP", switchId);
        } catch (ConstructionException e) {
            logger.error("{}", e.getMessage());
            return;
        }

        Set<Property> properties = new HashSet<Property>(1);
        Description desc = new Description(
                descriptionStats.getDatapathDescription());
        properties.add(desc);

        // Notify all internal and external listeners
        notifyInventoryShimListener(node, UpdateType.CHANGED, properties);
    }

    private byte[] deriveMacAddress(long dpid) {
        byte[] mac = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

        for (short i = 0; i < 6; i++) {
            mac[5 - i] = (byte) dpid;
            dpid >>= 8;
        }

        return mac;
    }
    @Override
    public void containerCreate(String containerName) {
        // do nothing
    }

    @Override
    public void containerDestroy(String containerName) {
        /*Set<NodeConnector> removeNodeConnectorSet = new HashSet<NodeConnector>();
        Set<Node> removeNodeSet = new HashSet<Node>();
        for (Map.Entry<NodeConnector, Set<String>> entry : nodeConnectorContainerMap.entrySet()) {
            Set<String> ncContainers = entry.getValue();
            if (ncContainers.contains(containerName)) {
                NodeConnector nodeConnector = entry.getKey();
                removeNodeConnectorSet.add(nodeConnector);
            }
        }
        for (Map.Entry<Node, Set<String>> entry : nodeContainerMap.entrySet()) {
            Set<String> nodeContainers = entry.getValue();
            if (nodeContainers.contains(containerName)) {
                Node node = entry.getKey();
                removeNodeSet.add(node);
            }
        }
        for (NodeConnector nodeConnector : removeNodeConnectorSet) {
            Set<String> ncContainers = nodeConnectorContainerMap.get(nodeConnector);
            ncContainers.remove(containerName);
            if (ncContainers.isEmpty()) {
                nodeConnectorContainerMap.remove(nodeConnector);
            }
        }
        for (Node node : removeNodeSet) {
            Set<String> nodeContainers = nodeContainerMap.get(node);
            nodeContainers.remove(containerName);
            if (nodeContainers.isEmpty()) {
                nodeContainerMap.remove(node);
            }
        }*///s4s IContainerAware
    }

    //copy from FlowConverter.java, and disable some code as those marked
    public static List<Class<? extends Action>> getFlowActions(int ofActionBitmask) {
        List<Class<? extends Action>> list = new ArrayList<Class<? extends Action>>();

        /*for (int i = 0; i < Integer.SIZE; i++) {
            int index = 1 << i;
            if ((index & ofActionBitmask) > 0) {
                if (actionMap.containsKey(index)) {
                    list.add(actionMap.get(index));
                }
            }
        }*/

        // Add implicit SAL actions
        //list.add(Controller.class);
        //list.add(SwPath.class);
        //list.add(HwPath.class);
        list.add(Drop.class);
        list.add(Output.class);//s4s: newly add

        return list;
    }

}
