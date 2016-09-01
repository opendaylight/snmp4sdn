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

import java.util.Dictionary;
import java.util.List;
import org.apache.felix.dm.Component;
import org.opendaylight.snmp4sdn.IRefreshInternalProvider;
import org.opendaylight.snmp4sdn.ITopologyServiceShimListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.core.Edge;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.topology.IPluginInTopologyService;
import org.opendaylight.controller.sal.topology.IPluginOutTopologyService;
import org.opendaylight.controller.sal.topology.TopoEdgeUpdate;

import org.opendaylight.snmp4sdn.ITopologyService;
import org.opendaylight.snmp4sdn.internal.util.TopologyServiceUtil;

import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.topology.discovery.rev130819.LinkDiscovered;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.topology.discovery.rev130819.LinkDiscoveredBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.topology.discovery.rev130819.LinkRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.topology.discovery.rev130819.LinkRemovedBuilder;

public class TopologyServices implements ITopologyServiceShimListener,
        IPluginInTopologyService, ITopologyService {
    protected static final Logger logger = LoggerFactory
            .getLogger(TopologyServices.class);
    private IPluginOutTopologyService salTopoService = null;
    private NotificationProviderService notifService = null;
    private IRefreshInternalProvider topoRefreshService = null;
    private String containerName;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    @SuppressWarnings("unchecked")
    void init(Component c) {
        logger.trace("INIT called!");
        Dictionary<Object, Object> props = c.getServiceProperties();
        containerName = (props != null) ? (String) props.get("containerName")
                : null;
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

    /**
     * Retrieve SAL service IPluginOutTopologyService
     *
     * @param s
     *            Called by Dependency Manager as soon as the SAL service is
     *            available
     */
    public void setPluginOutTopologyService(IPluginOutTopologyService s) {
        logger.trace("Setting IPluginOutTopologyService to: {}", s);
        this.salTopoService = s;
    }

    /**
     * called when SAL service IPluginOutTopologyService is no longer available
     *
     * @param s
     *            Called by Dependency Manager as soon as the SAL service is
     *            unavailable
     */
    public void unsetPluginOutTopologyService(IPluginOutTopologyService s) {
        if (this.salTopoService == s) {
            logger.trace("UNSetting IPluginOutTopologyService from: {}", s);
            this.salTopoService = null;
        }
    }

    /**
     * Retrieve OF protocol_plugin service IRefreshInternalProvider
     *
     * @param s
     *            Called by Dependency Manager as soon as the SAL service is
     *            available
     */
    public void setRefreshInternalProvider(IRefreshInternalProvider s) {
        logger.trace("Setting IRefreshInternalProvider to: {}", s);
        this.topoRefreshService = s;
    }

    /**
     * called when OF protocol_plugin service IRefreshInternalProvider is no
     * longer available
     *
     * @param s
     *            Called by Dependency Manager as soon as the SAL service is
     *            unavailable
     */
    public void unsetRefreshInternalProvider(IRefreshInternalProvider s) {
        if (this.topoRefreshService == s) {
            logger.trace("UNSetting IRefreshInternalProvider from: {}", s);
            this.topoRefreshService = null;
        }
    }

    @Override
    public void setMdNotifService(NotificationProviderService s){
        logger.trace("Setting IRefreshInternalProvider to: {}", s);
        this.notifService = s;
    }

    @Override
    public void unsetMdNotifService(NotificationProviderService s){
        if (this.notifService == s) {
            logger.trace("unsetMdNotifService(): UNSetting NotificationProviderService from: {}", notifService);
            this.notifService = null;
        }
        else{
            logger.debug("unsetMdNotifService(): the given NotificationProviderService to unset is {}, which is not the exising one {}, so remains the existing one", s, notifService);
        }
    }

    @Override
    public void edgeUpdate(List<TopoEdgeUpdate> topoedgeupdateList) {

        //to ad-sal
        if (this.salTopoService != null) {
            logger.debug("edgeUpdate(): report edge list to SAL");
            logger.debug("edgeUpdate(): edge list: " + "\n" + edgeListToString(topoedgeupdateList));//lg.dbug-trc
            logger.trace("report to ad-sal...");
            this.salTopoService.edgeUpdate(topoedgeupdateList);
        }
        else
            logger.debug("ERROR: edgeUpdate(): IPluginOutTopologyService is null!");

        //to md-sal
        logger.trace("report to md-sal...");
        this.edgeUpdateToMdSal(topoedgeupdateList);
    }

    public void edgeUpdateToMdSal(List<TopoEdgeUpdate> topoedgeupdateList) {
        if (this.notifService == null) {
            logger.debug("ERROR: edgeUpdateToMdSal(): NotificationProviderService is null!");
            return;
        }
        for(TopoEdgeUpdate edgeU : topoedgeupdateList){
            Edge edge = edgeU.getEdge();
            UpdateType type = edgeU.getUpdateType();
            if(type == UpdateType.ADDED){
                LinkDiscovered link = makeAddLink(edge);
                if(link == null)
                    logger.debug("ERROR: edgeUpdateToMdSal(): call makeLink() given edge {}, error!");
                else
                    notifService.publish(link);
            }
            else if(type == UpdateType.REMOVED){
                LinkRemoved link = makeRemoveLink(edge);
                if(link == null)
                    logger.debug("ERROR: edgeUpdateToMdSal(): call makeLink() given edge {}, error!");
                else
                    notifService.publish(link);
            }
            else
                logger.debug("ERROR: edgeUpdateToMdSal(): given edge {} of type {}, not defined how to process, so ignore", edge, type);
        }
    }

    private LinkDiscovered makeAddLink(Edge edge){

        String headNodeIdStr = null, tailNodeIdStr = null, headNcIdStr = null, tailNcIdStr = null;
        boolean bool = TopologyServiceUtil.getNodeAndNcIdString(edge, headNodeIdStr, tailNodeIdStr, headNcIdStr, tailNcIdStr);
        if(bool == false){
            logger.debug("ERROR: makeAddLink(): given Edge {}, call TopologyServiceUtil.getNodeAndNcIdString() fail", edge);
            return null;
        }

        NodeId localNodeId = new NodeId(headNodeIdStr);
        NodeConnectorId localNodeConnectorId = new NodeConnectorId(headNcIdStr);
        InstanceIdentifier<NodeConnector> localInstanceIdentifier = InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(localNodeId))
                        .child(NodeConnector.class, new NodeConnectorKey(localNodeConnectorId)).toInstance();
        NodeConnectorRef localNodeConnectorRef = new NodeConnectorRef(localInstanceIdentifier);

        NodeId remoteNodeId = new NodeId(tailNodeIdStr);
        NodeConnectorId remoteNodeConnectorId = new NodeConnectorId(tailNcIdStr);
        InstanceIdentifier<NodeConnector> remoteInstanceIdentifier = InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(remoteNodeId))
                        .child(NodeConnector.class, new NodeConnectorKey(remoteNodeConnectorId)).toInstance();
        NodeConnectorRef remoteNodeConnectorRef = new NodeConnectorRef(remoteInstanceIdentifier);

        LinkDiscoveredBuilder ldb = new LinkDiscoveredBuilder();
        ldb.setSource(remoteNodeConnectorRef);
        ldb.setDestination(localNodeConnectorRef);
        return((ldb.build()));
    }

    private LinkRemoved makeRemoveLink(Edge edge){

        String headNodeIdStr = null, tailNodeIdStr = null, headNcIdStr = null, tailNcIdStr = null;
        boolean bool = TopologyServiceUtil.getNodeAndNcIdString(edge, headNodeIdStr, tailNodeIdStr, headNcIdStr, tailNcIdStr);
        if(bool == false){
            logger.debug("ERROR: makeAddLink(): given Edge {}, call TopologyServiceUtil.getNodeAndNcIdString() fail", edge);
            return null;
        }

        NodeId localNodeId = new NodeId(headNodeIdStr);
        NodeConnectorId localNodeConnectorId = new NodeConnectorId(headNcIdStr);
        InstanceIdentifier<NodeConnector> localInstanceIdentifier = InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(localNodeId))
                        .child(NodeConnector.class, new NodeConnectorKey(localNodeConnectorId)).toInstance();
        NodeConnectorRef localNodeConnectorRef = new NodeConnectorRef(localInstanceIdentifier);

        NodeId remoteNodeId = new NodeId(tailNodeIdStr);
        NodeConnectorId remoteNodeConnectorId = new NodeConnectorId(tailNcIdStr);
        InstanceIdentifier<NodeConnector> remoteInstanceIdentifier = InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(remoteNodeId))
                        .child(NodeConnector.class, new NodeConnectorKey(remoteNodeConnectorId)).toInstance();
        NodeConnectorRef remoteNodeConnectorRef = new NodeConnectorRef(remoteInstanceIdentifier);

        LinkRemovedBuilder ldb = new LinkRemovedBuilder();
        ldb.setSource(remoteNodeConnectorRef);
        ldb.setDestination(localNodeConnectorRef);
        return((ldb.build()));
    }

    @Override
    public void sollicitRefresh() {
        logger.debug("Got a request to refresh topology");
        topoRefreshService.requestRefresh(containerName);
    }

    @Override
    public void edgeOverUtilized(Edge edge) {
        if (this.salTopoService != null) {
            this.salTopoService.edgeOverUtilized(edge);
        }
    }

    @Override
    public void edgeUtilBackToNormal(Edge edge) {
        if (this.salTopoService != null) {
            this.salTopoService.edgeUtilBackToNormal(edge);
        }
    }

    private String edgeListToString(List<TopoEdgeUpdate> topoedgeupdateList){
        String str = "";
        for(TopoEdgeUpdate edgeUpd : topoedgeupdateList){
            str += edgeUpd.getEdge().toString() + "\t" + edgeUpd.getUpdateType() + "\n";
        }
        return str;
    }
}
