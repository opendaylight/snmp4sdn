/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import org.opendaylight.controller.sal.core.Edge;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;
import org.opendaylight.snmp4sdn.IInventoryProvider;
import org.opendaylight.snmp4sdn.ITopologyServiceShim;
import org.opendaylight.snmp4sdn.internal.util.CommandProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.DeviceType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetEdgeListOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetEdgeListOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetNodeConnectorListOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetNodeConnectorListOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetNodeListOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.GetNodeListOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.RediscoverOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.RediscoverOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.SetDiscoveryIntervalInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.SetDiscoveryIntervalOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.SetDiscoveryIntervalOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.TopologyService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.edge.list.output.EdgeListEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.edge.list.output.EdgeListEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.node.connector.list.output.NodeConnectorListEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.node.connector.list.output.NodeConnectorListEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.node.list.output.NodeListEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.get.node.list.output.NodeListEntryBuilder;
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopologyImpl implements TopologyService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(TopologyImpl.class);

    private ITopologyServiceShim topo = null;
    private IInventoryProvider inv = null;
    private DiscoveryServiceAPI discov = null;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        //registerWithOSGIConsole();
    }

    /*private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }*/

    public void setTopologyServiceShim(ITopologyServiceShim topo) {
        if(topo == null) {
            logger.debug("ERROR: setTopologyServiceShim(): given null ITopologyServiceShim");
        }
        this.topo = topo;
    }

    public void unsetTopologyServiceShim(ITopologyServiceShim topo) {
        if (this.topo == topo) {
            this.topo = null;
        } else {
            logger.debug("ERROR: unsetTopologyServiceShim(): given ITopologyServiceShim is not the local one");
        }
    }

    public void setInventoryService(IInventoryProvider inv) {
        if(inv == null) {
            logger.debug("ERROR: setInventoryService(): given null IInventoryProvider");
        }
        this.inv = inv;
    }

    public void unsetInventoryService(IInventoryProvider inv) {
        if (this.inv == inv) {
            this.inv = null;
        } else {
            logger.debug("ERROR: unsetInventoryService(): given IInventoryProvider is not the local one");
        }
    }

    public void setDiscoveryService(DiscoveryServiceAPI discov) {
        if(discov == null) {
            logger.debug("ERROR: setDiscoveryService(): given null DiscoveryServiceAPI");
        }
        this.discov = discov;
    }

    public void unsetDiscoveryService(DiscoveryServiceAPI discov) {
        if (this.discov == discov) {
            this.discov = null;
        } else {
            logger.debug("ERROR: unsetDiscoveryService(): given DiscoveryServiceAPI is not the local one");
        }
    }

    private Future<RpcResult<GetEdgeListOutput>> createGetEdgeListFailRpcResult(){
        return RpcResultBuilder.<GetEdgeListOutput>failed().buildFuture();
    }

    public Future<RpcResult<GetNodeListOutput>> createGetNodeListFailRpcResult(){
        return RpcResultBuilder.<GetNodeListOutput>failed().buildFuture();
    }

    public Future<RpcResult<GetNodeConnectorListOutput>> createGetNodeConnectorListFailRpcResult(){
        return RpcResultBuilder.<GetNodeConnectorListOutput>failed().buildFuture();
    }

    private Future<RpcResult<RediscoverOutput>> createRediscoverRpcFailResult(){
        return RpcResultBuilder.<RediscoverOutput>failed().buildFuture();
    }

    private Future<RpcResult<SetDiscoveryIntervalOutput>> createSetDiscoveryIntervalRpcFailResult(){
        return RpcResultBuilder.<SetDiscoveryIntervalOutput>failed().buildFuture();
    }

    //md-sal
    @Override
    public Future<RpcResult<GetEdgeListOutput>> getEdgeList(){
        if(topo == null){
            logger.debug("ERROR: getEdgeList(): ITopologyServiceShim is null, can't proceed");
            return createGetEdgeListFailRpcResult();
        }

        List<Edge> edgeList = topo.getEdgeList();

        if(edgeList == null){
            logger.debug("ERROR: getEdgeList(): call ITopologyServiceShim.getEdgeList() returns null");
            return createGetEdgeListFailRpcResult();
        }

        List<EdgeListEntry> retList = new ArrayList<>();
        for(Edge edge : edgeList){
            //check parameters
            if(edge == null){
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() is null");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getHeadNodeConnector() == null){
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has null headNodeConnector");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getTailNodeConnector() == null){
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has null tailNodeConnector");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getHeadNodeConnector().getNode() == null){
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has null headNode");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getTailNodeConnector().getNode() == null){
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has null tailNode");
                return createGetEdgeListFailRpcResult();
            }

            //get the Type of head/tail Node/NodeConnector in the Edge, and check them
            String headNodeConnectorTypeStr = edge.getHeadNodeConnector().getType();
            if(headNodeConnectorTypeStr == null){
                logger.debug("ERROR: getEdgeList(): an edge's head NodeConnector in the edgeList from ITopologyServiceShim.getEdgeList() has null Type");
                return createGetEdgeListFailRpcResult();
            }
            String tailNodeConnectorTypeStr = edge.getTailNodeConnector().getType();
            if(tailNodeConnectorTypeStr == null){
                logger.debug("ERROR: getEdgeList(): an edge's tail NodeConnector in the edgeList from ITopologyServiceShim.getEdgeList() has null Type");
                return createGetEdgeListFailRpcResult();
            }
            String headNodeTypeStr = edge.getHeadNodeConnector().getNode().getType();
            if(headNodeTypeStr == null){
                logger.debug("ERROR: getEdgeList(): an edge's head Node in the edgeList from ITopologyServiceShim.getEdgeList() has null Type");
                return createGetEdgeListFailRpcResult();
            }
            String tailNodeTypeStr = edge.getTailNodeConnector().getNode().getType();
            if(tailNodeTypeStr == null){
                logger.debug("ERROR: getEdgeList(): an edge's tail Node in the edgeList from ITopologyServiceShim.getEdgeList() has null Type");
                return createGetEdgeListFailRpcResult();
            }

            //get the ID of head/tail Node/NodeConnector in the Edge, and check them
            if(edge.getHeadNodeConnector().getID() == null){
                logger.debug("ERROR: getEdgeList(): an edge's head NodeConnector in the edgeList from ITopologyServiceShim.getEdgeList() has null ID");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getTailNodeConnector().getID() == null){
                logger.debug("ERROR: getEdgeList(): an edge's tail NodeConnector in the edgeList from ITopologyServiceShim.getEdgeList() has null ID");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getHeadNodeConnector().getNode().getID() == null){
                logger.debug("ERROR: getEdgeList(): an edge's head Node in the edgeList from ITopologyServiceShim.getEdgeList() has null ID");
                return createGetEdgeListFailRpcResult();
            }
            if(edge.getTailNodeConnector().getNode().getID() == null){
                logger.debug("ERROR: getEdgeList(): an edge's tail Node in the edgeList from ITopologyServiceShim.getEdgeList() has null ID");
                return createGetEdgeListFailRpcResult();
            }

            EdgeListEntryBuilder entryBuilder = new EdgeListEntryBuilder();

            //TODO: in the following four "set xxxNC's type and ID", should not hard-code the mapping of "snmp-short/of-short/pr-string"
            //set head NodeConnecotr's type and ID
            if(headNodeConnectorTypeStr.equals("SNMP")){
                entryBuilder.setHeadNodeConnectorType(DeviceType.SNMP);
                entryBuilder.setHeadNodeConnectorId(((Short)edge.getHeadNodeConnector().getID()).toString());
            }
            else if(headNodeConnectorTypeStr.equals("OF")){
                entryBuilder.setHeadNodeConnectorType(DeviceType.OF);
                entryBuilder.setHeadNodeConnectorId(((Short)edge.getHeadNodeConnector().getID()).toString());
            }
            else if(headNodeConnectorTypeStr.equals("PR")){
                entryBuilder.setHeadNodeConnectorType(DeviceType.PR);
                entryBuilder.setHeadNodeConnectorId((String)edge.getHeadNodeConnector().getID());
            }
            else{
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has head NodeConnector of unknown type: {}", headNodeConnectorTypeStr);
                return createGetEdgeListFailRpcResult();
            }

            //set tail NodeConnecotr's type and ID
            if(tailNodeConnectorTypeStr.equals("SNMP")){
                entryBuilder.setTailNodeConnectorType(DeviceType.SNMP);
                entryBuilder.setTailNodeConnectorId(((Short)edge.getTailNodeConnector().getID()).toString());
            }
            else if(tailNodeConnectorTypeStr.equals("OF")){
                entryBuilder.setTailNodeConnectorType(DeviceType.OF);
                entryBuilder.setTailNodeConnectorId(((Short)edge.getTailNodeConnector().getID()).toString());
            }
            else if(tailNodeConnectorTypeStr.equals("PR")){
                entryBuilder.setTailNodeConnectorType(DeviceType.PR);
                entryBuilder.setTailNodeConnectorId((String)edge.getTailNodeConnector().getID());
            }
            else{
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has tail NodeConnector of unknown type: {}", tailNodeConnectorTypeStr);
                return createGetEdgeListFailRpcResult();
            }

            //set head Node's type and ID
            if(headNodeTypeStr.equals("SNMP")){
                entryBuilder.setHeadNodeType(DeviceType.SNMP);
                entryBuilder.setHeadNodeId(((Long)edge.getHeadNodeConnector().getNode().getID()).toString());
            }
            else if(headNodeTypeStr.equals("OF")){
                entryBuilder.setHeadNodeType(DeviceType.OF);
                entryBuilder.setHeadNodeId(((Long)edge.getHeadNodeConnector().getNode().getID()).toString());
            }
            else if(headNodeTypeStr.equals("PR")){
                entryBuilder.setHeadNodeType(DeviceType.PR);
                entryBuilder.setHeadNodeId((String)edge.getHeadNodeConnector().getNode().getID());
            }
            else{
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has head Node of unknown type: {}", headNodeTypeStr);
                return createGetEdgeListFailRpcResult();
            }

            //set tail Node's type and ID
            if(tailNodeTypeStr.equals("SNMP")){
                entryBuilder.setTailNodeType(DeviceType.SNMP);
                entryBuilder.setTailNodeId(((Long)edge.getTailNodeConnector().getNode().getID()).toString());
            }
            else if(tailNodeTypeStr.equals("OF")){
                entryBuilder.setTailNodeType(DeviceType.OF);
                entryBuilder.setTailNodeId(((Long)edge.getTailNodeConnector().getNode().getID()).toString());
            }
            else if(tailNodeTypeStr.equals("PR")){
                entryBuilder.setTailNodeType(DeviceType.PR);
                entryBuilder.setTailNodeId((String)edge.getTailNodeConnector().getNode().getID());
            }
            else{
                logger.debug("ERROR: getEdgeList(): an edge in the edgeList from ITopologyServiceShim.getEdgeList() has tail Node of unknown type: {}", tailNodeTypeStr);
                return createGetEdgeListFailRpcResult();
            }

            //make the edge! and save to return list.
            EdgeListEntry retEntry = entryBuilder.build();
            retList.add(retEntry);
        }

        //print edgeList on screen
        System.out.println();
        System.out.println();
        System.out.println("rpc get-edge-list is called, edge list:");
        System.out.println();
        printEdgeList(edgeList);
        System.out.println();

        GetEdgeListOutputBuilder ob = new GetEdgeListOutputBuilder().setEdgeListEntry(retList);
        return RpcResultBuilder.<GetEdgeListOutput>success(ob.build()).buildFuture();
    }

    //md-sal
    @Override
    public Future<RpcResult<GetNodeConnectorListOutput>> getNodeConnectorList(){
        if(inv == null){
            logger.debug("ERROR: getNodeConnectorList(): IInventoryProvider is null, can't proceed");
            return createGetNodeConnectorListFailRpcResult();
        }

        //get data from IInventoryProvider and check null
        ConcurrentMap<NodeConnector, Map<String, Property>> nodeConnectorProps = inv.getNodeConnectorProps(true);
        if(nodeConnectorProps == null){
            logger.debug("ERROR: getNodeConnectorList(): call IInventoryProvider.getNodeConnectorProps() returns null");
            return createGetNodeConnectorListFailRpcResult();
        }
        Set<NodeConnector> nodeConnectorSet = nodeConnectorProps.keySet();
        if(nodeConnectorSet == null){
            logger.debug("ERROR: getNodeConnectorList(): the NodeConnector set from IInventoryProvider.getNodeConnectorProps() is null");
            return createGetNodeConnectorListFailRpcResult();
        }

        List<NodeConnectorListEntry> retList = new ArrayList<>();
        for(NodeConnector nodeConnector : nodeConnectorSet){
            //check parameters
            if(nodeConnector == null){
                logger.debug("ERROR: getNodeConnectorList(): an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() is null");
                return createGetNodeConnectorListFailRpcResult();
            }
            Node node = nodeConnector.getNode();
            if(node == null){
                logger.debug("ERROR: getNodeConnectorList(): an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has null Node");
                return createGetNodeConnectorListFailRpcResult();
            }
            //check NodeConnector type
            String nodeConnectorTypeStr = nodeConnector.getType();
            if(nodeConnectorTypeStr == null){
                logger.debug("ERROR: getNodeConnectorList(): an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has null Type");
                return createGetNodeConnectorListFailRpcResult();
            }
            //check NodeConnector ID
            Object nodeConnectorID = nodeConnector.getID();
            if(nodeConnectorID == null){
                logger.debug("ERROR: getNodeConnectorList(): an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has null ID");
                return createGetNodeConnectorListFailRpcResult();
            }
            //check Node type
            String nodeTypeStr = node.getType();
            if(nodeTypeStr == null){
                logger.debug("ERROR: getNodeConnectorList(): the Node of an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has null Type");
                return createGetNodeConnectorListFailRpcResult();
            }
            //check Node ID
            Object nodeID = node.getID();
            if(nodeID == null){
                logger.debug("ERROR: getNodeConnectorList(): the Node of an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has null ID");
                return createGetNodeConnectorListFailRpcResult();
            }

            NodeConnectorListEntryBuilder entryBuilder = new NodeConnectorListEntryBuilder();

            //TODO: in the following setting type and ID for NodeConnector and Node, should not hard-code the mapping of "snmp-short/of-short/pr-string"
            //set NodeConnecotr's type and ID
            if(nodeConnectorTypeStr.equals("SNMP")){
                entryBuilder.setNodeConnectorType(DeviceType.SNMP);
                entryBuilder.setNodeConnectorId(((Short)nodeConnectorID).toString());
            }
            else if(nodeConnectorTypeStr.equals("OF")){
                entryBuilder.setNodeConnectorType(DeviceType.OF);
                entryBuilder.setNodeConnectorId(((Short)nodeConnectorID).toString());
            }
            else if(nodeConnectorTypeStr.equals("PR")){
                entryBuilder.setNodeConnectorType(DeviceType.PR);
                entryBuilder.setNodeConnectorId((String)nodeConnectorID);
            }
            else{
                logger.debug("ERROR: getNodeConnectorList(): an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has unknown Type");
                return createGetNodeConnectorListFailRpcResult();
            }
            //set Node's type and ID
            if(nodeTypeStr.equals("SNMP")){
                entryBuilder.setNodeType(DeviceType.SNMP);
                entryBuilder.setNodeId(((Long)nodeID).toString());
            }
            else if(nodeTypeStr.equals("OF")){
                entryBuilder.setNodeType(DeviceType.OF);
                entryBuilder.setNodeId(((Long)nodeID).toString());
            }
            else if(nodeTypeStr.equals("PR")){
                entryBuilder.setNodeType(DeviceType.PR);
                entryBuilder.setNodeId((String)nodeID);
            }
            else{
                logger.debug("ERROR: getNodeConnectorList(): the Node of an NodeConnector from ITopologyServiceShim.getNodeConnectorProps() has unknown Type");
                return createGetNodeConnectorListFailRpcResult();
            }

            //make the nodeConnector! and save to return list.
            NodeConnectorListEntry retEntry = entryBuilder.build();
            retList.add(retEntry);
        }

        //print node connector list on screen
        System.out.println();
        System.out.println();
        System.out.println("rpc get-node-connector-list is called, node connector list:");
        System.out.println();
        printNodeConnectorSet(nodeConnectorSet);
        System.out.println();

        GetNodeConnectorListOutputBuilder ob = new GetNodeConnectorListOutputBuilder().setNodeConnectorListEntry(retList);
        return RpcResultBuilder.<GetNodeConnectorListOutput>success(ob.build()).buildFuture();
    }

    //md-sal
    @Override
    public Future<RpcResult<GetNodeListOutput>> getNodeList(){
        if(inv == null){
            logger.debug("ERROR: getNodeList(): IInventoryProvider is null, can't proceed");
            return createGetNodeListFailRpcResult();
        }

        //get data from IInventoryProvider and check null
        ConcurrentMap<Node, Map<String, Property>> nodeProps = inv.getNodeProps();
        if(nodeProps == null){
            logger.debug("ERROR: getNodeList(): call IInventoryProvider.getNodeProps() returns null");
            return createGetNodeListFailRpcResult();
        }
        Set<Node> nodeSet = nodeProps.keySet();
        if(nodeSet == null){
            logger.debug("ERROR: getNodeList(): the Node set from IInventoryProvider.getNodeProps() is null");
            return createGetNodeListFailRpcResult();
        }

        List<NodeListEntry> retList = new ArrayList<>();
        for(Node node : nodeSet){
            //check parameters
            if(node == null){
                logger.debug("ERROR: getNodeList(): an Node from ITopologyServiceShim.getNodeProps() is null");
                return createGetNodeListFailRpcResult();
            }
            //check Node type
            String nodeTypeStr = node.getType();
            if(nodeTypeStr == null){
                logger.debug("ERROR: getNodeList(): an Node from ITopologyServiceShim.getNodeProps() has null Type");
                return createGetNodeListFailRpcResult();
            }
            //check Node ID
            Object nodeID = node.getID();
            if(nodeID == null){
                logger.debug("ERROR: getNodeList(): an Node from ITopologyServiceShim.getNodeProps() has null ID");
                return createGetNodeListFailRpcResult();
            }

            NodeListEntryBuilder entryBuilder = new NodeListEntryBuilder();

            //TODO: in the following setting type and ID for the Node, should not hard-code the mapping of "snmp-short/of-short/pr-string"
            //set Node's type and ID
            if(nodeTypeStr.equals("SNMP")){
                entryBuilder.setNodeType(DeviceType.SNMP);
                entryBuilder.setNodeId(((Long)nodeID).toString());
            }
            else if(nodeTypeStr.equals("OF")){
                entryBuilder.setNodeType(DeviceType.OF);
                entryBuilder.setNodeId(((Long)nodeID).toString());
            }
            else if(nodeTypeStr.equals("PR")){
                entryBuilder.setNodeType(DeviceType.PR);
                entryBuilder.setNodeId((String)nodeID);
            }
            else{
                logger.debug("ERROR: getNodeList(): a Node from ITopologyServiceShim.getNodeConnectorProps() has unknown Type");
                return createGetNodeListFailRpcResult();
            }

            //make the node! and save to return list.
            NodeListEntry retEntry = entryBuilder.build();
            retList.add(retEntry);
        }

        //print node list on screen
        System.out.println();
        System.out.println();
        System.out.println("rpc get-node-list is called, node list:");
        System.out.println();
        printNodeSet(nodeSet);
        System.out.println();

        GetNodeListOutputBuilder ob = new GetNodeListOutputBuilder().setNodeListEntry(retList);
        return RpcResultBuilder.<GetNodeListOutput>success(ob.build()).buildFuture();
    }

    @Override//md-sal
    public Future<RpcResult<RediscoverOutput>> rediscover(){

        logger.info("SNMP4SDN: Receive the Topology Discovery request!");

        boolean isSuccess = discov.doTopologyDiscovery();
        if(isSuccess){
            RediscoverOutputBuilder ob = new RediscoverOutputBuilder().setRediscoverResult(Result.SUCCESS);
            return RpcResultBuilder.<RediscoverOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: rediscover(): call DiscoveryService.doTopologyDiscovery() fail");
            return createRediscoverRpcFailResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<SetDiscoveryIntervalOutput>> setDiscoveryInterval(SetDiscoveryIntervalInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: setDiscoveryInterval(): given null input");
            return createSetDiscoveryIntervalRpcFailResult();
        }
        Integer interval = input.getIntervalSecond();
        if(interval == null){
            logger.debug("ERROR: setDiscoveryInterval(): given interval is null");
            return createSetDiscoveryIntervalRpcFailResult();
        }

        //parameters checking
        if(interval < 0){
            logger.debug("ERROR: setDiscoveryInterval(): given invalid interval {}", interval);
            return createSetDiscoveryIntervalRpcFailResult();
        }

        //set the interval, return success
        discov.setPeriodicTopologyDiscoveryIntervalTime(interval.intValue());

        logger.info("SNMP4SDN: Periodic Topology Discovery interval time has been set as {} second", interval);

        SetDiscoveryIntervalOutputBuilder ob = new SetDiscoveryIntervalOutputBuilder().setSetDiscoveryIntervalResult(Result.SUCCESS);
        return RpcResultBuilder.<SetDiscoveryIntervalOutput>success(ob.build()).buildFuture();
    }

    private void printEdgeList(List<Edge> edgeList){
        if(edgeList == null){
            System.out.println("ERROR: printEdgeList(): given null data");
            return;
        }
        for(Edge edge : edgeList) {
            System.out.println(edge);
        }
    }

    private void printNodeConnectorSet(Set<NodeConnector> nodeConnectorSet){
        if(nodeConnectorSet == null){
            System.out.println("ERROR: printNodeConnectorSet(): given null data");
            return;
        }
        for(NodeConnector nc : nodeConnectorSet) {
            System.out.println(nc);
        }
    }

    private void printNodeSet(Set<Node> nodeSet){
        if(nodeSet == null){
            System.out.println("ERROR: printNodeSet(): given null data");
            return;
        }
        for(Node node : nodeSet) {
            System.out.println(node);
        }
    }

    //TODO: OSGi test command

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---SNMP4SDN TopologyImpl---\n");
        return help.toString();
    }


}
