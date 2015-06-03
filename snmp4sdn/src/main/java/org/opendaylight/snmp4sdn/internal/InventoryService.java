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

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.dm.Component;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.snmp4sdn.IInventoryShimInternalListener;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.Bandwidth;
import org.opendaylight.controller.sal.core.Name;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.inventory.IPluginInInventoryService;
import org.opendaylight.controller.sal.inventory.IPluginOutInventoryService;
import org.opendaylight.controller.sal.utils.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class describes inventory service protocol plugin. One instance per
 * container of the network. Each instance gets container specific inventory
 * events from InventoryServiceShim. It interacts with SAL to pass inventory
 * data to the upper application.
 *
 *
 */
public class InventoryService implements IInventoryShimInternalListener,
        IPluginInInventoryService, IInventoryProvider {
    protected static final Logger logger = LoggerFactory
            .getLogger(InventoryService.class);
    private Set<IPluginOutInventoryService> pluginOutInventoryServices = Collections
            .synchronizedSet(new HashSet<IPluginOutInventoryService>());
    private IController controller = null;
    private ConcurrentMap<Node, Map<String, Property>> nodeProps; // properties are maintained in global container only
    private ConcurrentMap<NodeConnector, Map<String, Property>> nodeConnectorProps; // properties are maintained in global container only
    private boolean isDefaultContainer = false;
    private String containerName = null;

    void setController(IController s) {
        this.controller = s;
    }

    void unsetController(IController s) {
        if (this.controller == s) {
            this.controller = null;
        }
    }

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    @SuppressWarnings("rawtypes")
    void init(Component c) {
        logger.trace("INIT called!");

        Dictionary props = c.getServiceProperties();
        if (props != null) {
            containerName = (String) props.get("containerName");
            if(containerName != null)
                isDefaultContainer = containerName.equals(GlobalConstants.DEFAULT
                    .toString());
        }

        nodeProps = new ConcurrentHashMap<Node, Map<String, Property>>();
        nodeConnectorProps = new ConcurrentHashMap<NodeConnector, Map<String, Property>>();
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
        logger.trace("DESTROY called!");
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
        logger.trace("START called!");
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy ()"
     * calls
     *
     */
    void stop() {
        logger.trace("STOP called!");
    }

    public void setPluginOutInventoryServices(IPluginOutInventoryService service) {
        logger.trace("Got a service set request {}", service);
        if (this.pluginOutInventoryServices != null) {
            this.pluginOutInventoryServices.add(service);
        }
    }

    public void unsetPluginOutInventoryServices(
            IPluginOutInventoryService service) {
        logger.trace("Got a service UNset request");
        if (this.pluginOutInventoryServices != null) {
            this.pluginOutInventoryServices.remove(service);
        }
    }

    /**
     * Retrieve nodes from openflow
     */
    @Override
    public ConcurrentMap<Node, Map<String, Property>> getNodeProps() {
        logger.trace("getNodePros for container {}", containerName);
        return nodeProps;
    }

    @Override
    public ConcurrentMap<NodeConnector, Map<String, Property>> getNodeConnectorProps(
            Boolean refresh) {
        if (nodeConnectorProps == null) {
            return null;
        }

        if (isDefaultContainer && refresh) {
            Map<Long, ISwitch> switches = controller.getSwitches();
            for (ISwitch sw : switches.values()) {
                Map<NodeConnector, Set<Property>> ncProps = InventoryServiceHelper
                        .SNMPSwitchToProps(sw);
                for (Map.Entry<NodeConnector, Set<Property>> entry : ncProps
                        .entrySet()) {
                    updateNodeConnector(entry.getKey(), UpdateType.ADDED,
                            entry.getValue());
                }
            }
        }

        return nodeConnectorProps;
    }

    // nothing to return
    @Override
    public Set<Node> getConfiguredNotConnectedNodes() {
        return Collections.emptySet();
    }

    @Override
    public void updateNodeConnector(NodeConnector nodeConnector,
            UpdateType type, Set<Property> props) {
        logger.trace("updateNodeConnector {} type {}", nodeConnector,
                type.getName());
        if (nodeConnectorProps == null) {
            logger.trace("nodeConnectorProps is null");
            return;
        }

        /*Name newprop = new Name("s1-eth1");//s4s test
        for (Property prop : props) {//s4s test
            if(prop.getName().equals("name")){
                props.remove(prop);
                props.add(newprop);
            }
        }
        props.add(new Bandwidth((long) Math.pow(10, 9)));//s4s test*/
                
        Map<String, Property> propMap = nodeConnectorProps.get(nodeConnector);
        switch (type) {
        case ADDED:
        case CHANGED:
            if (propMap == null) {
                propMap = new HashMap<String, Property>();
            }
            if (props != null) {
                for (Property prop : props) {
                    propMap.put(prop.getName(), prop);
                }
            }
            nodeConnectorProps.put(nodeConnector, propMap);
            break;
        case REMOVED:
            nodeConnectorProps.remove(nodeConnector);
            break;
        default:
            return;
        }

        // update sal and discovery
        synchronized (pluginOutInventoryServices) {
            for (IPluginOutInventoryService service : pluginOutInventoryServices) {
                //logger.trace("new port event-- InventoryService.updateNodeConnector() now inform SAL [node {}({}), port {}({}), type {}]", (Long)nodeConnector.getNode().getID(), nodeConnector.getNode().getType(), nodeConnector.getID(), nodeConnector.getType(), type.getName());
                //logger.trace("\tthe SAL service's name: {}", service.getClass().getName());
                service.updateNodeConnector(nodeConnector, type, props);
            }
        }
    }

    private void addNode(Node node, Set<Property> props) {
        if (nodeProps == null) {
            return;
        }

        Set<Node> nodeSet = nodeProps.keySet();
        if (((props == null) || props.isEmpty()) && (nodeSet != null)
                && nodeSet.contains(node)) {
            // node already added
            return;
        }

        logger.trace("addNode: {} added, props: {} for container {}",
                new Object[] { node, props, containerName });

        // update local cache
        Map<String, Property> propMap = nodeProps.get(node);
        if (propMap == null) {
            propMap = new HashMap<String, Property>();
        }

        if (props != null) {
            for (Property prop : props) {
                propMap.put(prop.getName(), prop);
            }
        }
        nodeProps.put(node, propMap);

        // update sal
        synchronized (pluginOutInventoryServices) {
            for (IPluginOutInventoryService service : pluginOutInventoryServices) {
                service.updateNode(node, UpdateType.ADDED, props);
            }
        }
    }

    private void removeNode(Node node) {
        logger.trace("{} removed", node);
        if (nodeProps == null)
            return;

        // update local cache
        nodeProps.remove(node);

        Set<NodeConnector> removeSet = new HashSet<NodeConnector>();
        for (NodeConnector nodeConnector : nodeConnectorProps.keySet()) {
            if (nodeConnector.getNode().equals(node)) {
                removeSet.add(nodeConnector);
            }
        }
        for (NodeConnector nodeConnector : removeSet) {
            nodeConnectorProps.remove(nodeConnector);
        }

        // update sal
        synchronized (pluginOutInventoryServices) {
            for (IPluginOutInventoryService service : pluginOutInventoryServices) {
                service.updateNode(node, UpdateType.REMOVED, null);
            }
        }
    }

    private void updateNode(Node node, Set<Property> properties) {
        logger.trace("{} updated, props: {}", node, properties);
        if (nodeProps == null || !nodeProps.containsKey(node) ||
                properties == null || properties.isEmpty()) {
            return;
        }

        // Update local cache with new properties
        Set<Property> newProperties = new HashSet<Property>(properties.size());
        Map<String, Property> propertyMap = nodeProps.get(node);
        for (Property property : properties) {
            String name = property.getName();
            Property currentProperty = propertyMap.get(name);
            if (!property.equals(currentProperty)) {
                propertyMap.put(name, property);
                newProperties.add(property);
            }
        }

        // Update SAL if we got new properties
        if (!newProperties.isEmpty()) {
            synchronized (pluginOutInventoryServices) {
                for (IPluginOutInventoryService service : pluginOutInventoryServices) {
                    service.updateNode(node, UpdateType.CHANGED, newProperties);
                }
            }
        }
    }

    @Override
    public void updateNode(Node node, UpdateType type, Set<Property> props) {
        switch (type) {
        case ADDED:
            addNode(node, props);
            break;
        case REMOVED:
            removeNode(node);
            break;
        case CHANGED:
            updateNode(node, props);
            break;
        default:
            break;
        }
    }

}
