/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

import org.opendaylight.snmp4sdn.VLANTable;
import org.opendaylight.snmp4sdn.VLANTable.VLANTableEntry;

//no-sal/*not to remove this interface, otherwise code vary a lot //TODO:clean code */
/*import org.opendaylight.snmp4sdn.IVLANService;
import org.opendaylight.snmp4sdn.VLANTable;
import org.opendaylight.snmp4sdn.VLANTable.VLANTableEntry;*/

//custom ad-sal
/*import org.opendaylight.controller.sal.vlan.IPluginInVLANService;
import org.opendaylight.controller.sal.vlan.VLANTable;
import org.opendaylight.controller.sal.vlan.VLANTable.VLANTableEntry;*/

//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntryBuilder;

//For md-sal RPC call
import org.opendaylight.controller.sal.common.util.Rpcs;
import java.util.Collections;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

//TODO: com.google.common import error in karaf
/*import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VLANService implements /*IPluginInVLANService,//custom ad-sal*/ VlanService/*md-sal*/, /*IVLANService,//no-sal*/ CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(VLANService.class);

    public boolean isDummy = false;

    private Controller controller = null;
    private CLIHandler cli = null;
    private CmethUtil cmethUtil = null;

    private int NUMBER_OF_PORT = 64;
    //TODO: the vendor-specific parameteres, like NUMBER_OF_PORT_DLINK, need a way to apply in code. Now tempararily we use general parameter name, as above, in code
    //private int NUMBER_OF_PORT_DLINK = 24;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;//s4s add
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    /*@Override//abandom this method, because addVLAN() must be given vlanName also
    public Status addVLAN(Node node, Integer vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(!isValidVlan(vlanID))
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when addVLAN to node (mac: " + HexString.toHexString((Long)node.getID()) + ")");

        return new SNMPHandler(cmethUtil).addVLAN(node, vlanID);
    }*/

    /*
    *The following many createXxxxFilRpcResult() are for easy of return fail
    */
    private Future<RpcResult<AddVlanAndSetPortsOutput>> createAddVlanAndSetPortsFailRpcResult(){
        AddVlanAndSetPortsOutputBuilder ob = new AddVlanAndSetPortsOutputBuilder().setAddVlanAndSetPortsResult(Result.FAIL);
        RpcResult<AddVlanAndSetPortsOutput> rpcResult =
                    Rpcs.<AddVlanAndSetPortsOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    private Future<RpcResult<SetVlanPortsOutput>> createSetVlanPortsFailRpcResult(){
        SetVlanPortsOutputBuilder ob = new SetVlanPortsOutputBuilder().setSetVlanPortsResult(Result.FAIL);
        RpcResult<SetVlanPortsOutput> rpcResult =
                    Rpcs.<SetVlanPortsOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    private Future<RpcResult<AddVlanOutput>> createAddVlanFailRpcResult(){
        AddVlanOutputBuilder ob = new AddVlanOutputBuilder().setAddVlanResult(Result.FAIL);
        RpcResult<AddVlanOutput> rpcResult =
                    Rpcs.<AddVlanOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    private Future<RpcResult<DeleteVlanOutput>> createDeleteVlanFailRpcResult(){
        DeleteVlanOutputBuilder ob = new DeleteVlanOutputBuilder().setDeleteVlanResult(Result.FAIL);
        RpcResult<DeleteVlanOutput> rpcResult =
                    Rpcs.<DeleteVlanOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<AddVlanAndSetPortsOutput>> addVlanAndSetPorts(AddVlanAndSetPortsInput input){
        //error checking
        if(input == null){logger.debug("ERROR: addVlanAndSetPorts(): given AddVlanAndSetPortsInput input is null"); return createAddVlanAndSetPortsFailRpcResult();}
        Long nodeId = input.getNodeId();
            if(nodeId == null){logger.debug("ERROR: addVlanAndSetPorts(): given nodeId is null"); return createAddVlanAndSetPortsFailRpcResult();}
            //other nodeId checking would be checked in the checkNodeIpValid() later
        Integer vlanId = input.getVlanId();
            if(vlanId == null){logger.debug("ERROR: addVlanAndSetPorts(): given vlanId is null"); return createAddVlanAndSetPortsFailRpcResult();}
            if(!isValidVlan(vlanId)){logger.debug("ERROR: addVlanAndSetPorts(): given invalid vlanId {}", vlanId); return createAddVlanAndSetPortsFailRpcResult();}
        String vlanName = input.getVlanName();
            if(vlanName == null){logger.debug("ERROR: addVlanAndSetPorts(): given vlanName is null"); return createAddVlanAndSetPortsFailRpcResult();}
        String portListT = input.getTaggedPortList();
            if(portListT == null){logger.debug("ERROR: addVlanAndSetPorts(): given tagged port list (a String) is null"); return createAddVlanAndSetPortsFailRpcResult();}
        String portListU = input.getUntaggedPortList();
            if(portListU == null){logger.debug("ERROR: addVlanAndSetPorts(): given untagged port list (a String) is null"); return createAddVlanAndSetPortsFailRpcResult();}

        Node node = createSNMPNode(nodeId.longValue());
            if(!isNodeIpValid(node)){logger.debug("ERROR: addVlanAndSetPorts(): call isNodeIpValid() with nodeId {} fail", nodeId); return createAddVlanAndSetPortsFailRpcResult();}

        int portsT[] = convertPortListString2IntArray(portListT);
        int portsU[] = convertPortListString2IntArray(portListU);
        List<NodeConnector> ncListT = ports2NcList(portsT, nodeId);
        List<NodeConnector> ncListU = ports2NcList(portsU, nodeId);

        Status status = this.addVLANandSetPorts(node, new Integer(vlanId), vlanName, ncListT, ncListU);
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            AddVlanAndSetPortsOutputBuilder ob = new AddVlanAndSetPortsOutputBuilder().setAddVlanAndSetPortsResult(Result.SUCCESS);
            RpcResult<AddVlanAndSetPortsOutput> rpcResult =
                    Rpcs.<AddVlanAndSetPortsOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: addVlanAndSetPorts(): call addVLANandSetPorts(), with nodeId {}, vlanId {}, vlanName{}, taggedPorts, {} untaggedPorts {}, fail", nodeId, vlanId, vlanName, Arrays.toString(portsT), Arrays.toString(portsU));
            return createAddVlanAndSetPortsFailRpcResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<AddVlanOutput>> addVlan(AddVlanInput input){
        //error checking
        if(input == null){logger.debug("ERROR: addVlan(): given AddVlanInput input is null"); return createAddVlanFailRpcResult();}
        Long nodeId = input.getNodeId();
            if(nodeId == null){logger.debug("ERROR: addVlan(): given nodeId is null"); return createAddVlanFailRpcResult();}
            //other nodeId checking would be checked in the checkNodeIpValid() later
        Integer vlanId = input.getVlanId();
            if(vlanId == null){logger.debug("ERROR: addVlan(): given vlanId is null"); return createAddVlanFailRpcResult();}
            if(!isValidVlan(vlanId)){logger.debug("ERROR: addVlan(): given invalid vlanId {}", vlanId); return createAddVlanFailRpcResult();}
        String vlanName = input.getVlanName();
            if(vlanName == null){logger.debug("ERROR: addVlan(): given vlanName is null"); return createAddVlanFailRpcResult();}


        Node node = createSNMPNode(nodeId.longValue());
            if(!isNodeIpValid(node)){logger.debug("ERROR: addVlan(): call isNodeIpValid() with nodeId {} fail", nodeId); return createAddVlanFailRpcResult();}

        Status status = this.addVLAN(node, new Integer(vlanId), vlanName);
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            AddVlanOutputBuilder ob = new AddVlanOutputBuilder().setAddVlanResult(Result.SUCCESS);
            RpcResult<AddVlanOutput> rpcResult =
                    Rpcs.<AddVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: addVlan(): call addVLAN(), with nodeId {}, vlanId {}, vlanName{}, fail", nodeId, vlanId, vlanName);
            return createAddVlanFailRpcResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<DeleteVlanOutput>> deleteVlan(DeleteVlanInput input){
        //error checking
        if(input == null){logger.debug("ERROR: deleteVlan(): given DeleteVlanInput input is null"); return createDeleteVlanFailRpcResult();}
        Long nodeId = input.getNodeId();
            if(nodeId == null){logger.debug("ERROR: deleteVlan(): given nodeId is null"); return createDeleteVlanFailRpcResult();}
            //other nodeId checking would be checked in the checkNodeIpValid() later
        Integer vlanId = input.getVlanId();
            if(vlanId == null){logger.debug("ERROR: deleteVlan(): given vlanId is null"); return createDeleteVlanFailRpcResult();}
            if(!isValidVlan(vlanId)){logger.debug("ERROR: deleteVlan(): given invalid vlanId {}", vlanId); return createDeleteVlanFailRpcResult();}

        Node node = createSNMPNode(nodeId.longValue());
            if(!isNodeIpValid(node)){logger.debug("ERROR: deleteVlan(): call isNodeIpValid() with nodeId {} fail", nodeId); return createDeleteVlanFailRpcResult();}

        Status status = this.deleteVLAN(node, new Integer(vlanId));
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            DeleteVlanOutputBuilder ob = new DeleteVlanOutputBuilder().setDeleteVlanResult(Result.SUCCESS);
            RpcResult<DeleteVlanOutput> rpcResult =
                    Rpcs.<DeleteVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: deleteVlan(): call deleteVLAN(), with nodeId {} and vlanId {}, fail", nodeId, vlanId);
            return createDeleteVlanFailRpcResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<GetVlanTableOutput>> getVlanTable(GetVlanTableInput input){
        //error checking
        if(input == null){logger.debug("ERROR: getVlanTable(): given GetVlanTableInput input is null"); return null;}
        Long nodeId = input.getNodeId();
            if(nodeId == null){logger.debug("ERROR: getVlanTable(): given nodeId is null"); return null;}
            //other nodeId checking would be checked in the checkNodeIpValid() later
        Node node = createSNMPNode(nodeId.longValue());
            if(!isNodeIpValid(node)){logger.debug("ERROR: getVlanTable(): call isNodeIpValid() with nodeId {} fail", nodeId); return null;}

        VLANTable table = this.getVLANTable(node);
            if(table == null){logger.debug("ERROR: getVlanTable(): call getVLANTable() with nodeId {} fail", nodeId); return null;}
        Vector entries = table.getEntries();
            if(entries == null){logger.debug("ERROR: getVlanTable(): call getVLANTable() with nodeId {}, the returned Vector entries is null", nodeId); return null;}

        List <VlanTableEntry> retTable = new ArrayList<VlanTableEntry>();
        for(int i = 0; i < entries.size(); i++){
            VLANTableEntry entry = (VLANTableEntry)(entries.get(i));
            List<NodeConnector> ports = entry.getPorts();
            List<Short> portList = new ArrayList<Short>();
            for(int j = 0; j < ports.size(); j++)
                portList. add((Short)(ports.get(j).getID()));
            VlanTableEntryBuilder entryBuilder = new VlanTableEntryBuilder()
                                                                            .setVlanId(entry.getVlanID())
                                                                            /*.setVlanName(java.lang.String value)*///TODO: haven't retrive vlan name in SNMPHandler.getVLANTable()
                                                                            .setPortList(portList);
            VlanTableEntry retEntry = entryBuilder.build();
            retTable.add(retEntry);
        }

        GetVlanTableOutputBuilder ob = new GetVlanTableOutputBuilder().setVlanTableEntry(retTable);

        RpcResult<GetVlanTableOutput> rpcResult =
            Rpcs.<GetVlanTableOutput> getRpcResult(true, ob.build(),
                Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    //@Override//md-sal
    /*public Future<RpcResult<java.lang.Void>> printVlanTable(final PrintVlanTableInput input){System.out.println("HELLO");
        Long nodeId = input.getNodeId();
        Node node = createSNMPNode(nodeId.longValue());
        VLANTable vTable = getVLANTable(node);//argument value validation is checked in the following
        System.out.println(vTable.toString());

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
    }*/

    @Override//md-sal
    public Future<RpcResult<SetVlanPortsOutput>> setVlanPorts(SetVlanPortsInput input){
        //error checking
        if(input == null){logger.debug("ERROR: setVlanPorts(): given SetVlanPortsInput input is null"); return createSetVlanPortsFailRpcResult();}
        Long nodeId = input.getNodeId();
            if(nodeId == null){logger.debug("ERROR: setVlanPorts(): given nodeId is null"); return createSetVlanPortsFailRpcResult();}
            //other nodeId checking would be checked in the checkNodeIpValid() later
        Integer vlanId = input.getVlanId();
            if(vlanId == null){logger.debug("ERROR: setVlanPorts(): given vlanId is null"); return createSetVlanPortsFailRpcResult();}
            if(!isValidVlan(vlanId)){logger.debug("ERROR: setVlanPorts(): given invalid vlanId {}", vlanId); return createSetVlanPortsFailRpcResult();}
        String portListT = input.getTaggedPortList();
            if(portListT == null){logger.debug("ERROR: setVlanPorts(): given tagged port list (a String) is null"); return createSetVlanPortsFailRpcResult();}
        String portListU = input.getUntaggedPortList();
            if(portListU == null){logger.debug("ERROR: setVlanPorts(): given untagged port list (a String) is null"); return createSetVlanPortsFailRpcResult();}

        Node node = createSNMPNode(nodeId.longValue());
            if(!isNodeIpValid(node)){logger.debug("ERROR: setVlanPorts(): call isNodeIpValid() with nodeId {} fail", nodeId); return createSetVlanPortsFailRpcResult();}

        int portsT[] = convertPortListString2IntArray(portListT);
        int portsU[] = convertPortListString2IntArray(portListU);
        List<NodeConnector> ncListT = ports2NcList(portsT, nodeId);
        List<NodeConnector> ncListU = ports2NcList(portsU, nodeId);

        Status status = this.setVLANPorts(node, new Integer(vlanId), ncListT, ncListU);
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            SetVlanPortsOutputBuilder ob = new SetVlanPortsOutputBuilder().setSetVlanPortsResult(Result.SUCCESS);
            RpcResult<SetVlanPortsOutput> rpcResult =
                    Rpcs.<SetVlanPortsOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: setVlanPorts(): call setVLANPorts(), with nodeId {}, vlanId {}, taggedPorts, {} untaggedPorts {}, fail", nodeId, vlanId, Arrays.toString(portsT), Arrays.toString(portsU));
            return createSetVlanPortsFailRpcResult();
        }
    }


    //@Override//ad-sal
    public Status addVLAN(Node node, Integer vlanID, String vlanName){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLAN(): VLAN ID as " + vlanID + " is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLAN(): VLAN ID as " + vlanID + " is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
        }

        if(vlanName == null){
            logger.debug("ERROR: addVLAN(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "addVLAN(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        status = new SNMPHandler(cmethUtil).addVLAN(nodeId, vlanId, vlanName);
        if(!status.isSuccess())
            logger.debug("ERROR: addVLAN(): Add VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName:" + vlanName +") to switch fail: " + status);

        return status;
    }

    //TODO: d-link switch allow to add vlan without vlanName

    //@Override//ad-sal
    public Status setVLANPorts (Node node, Integer vlanID, List<NodeConnector> nodeConns){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS){
            logger.debug("ERROR: setVLANPorts(): Fail to find node {} in DB", (Long)node.getID());
            return status;
        }

        if(!isValidVlan(vlanID))
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));

        //check port number is in valid range
        for(int i = 0; i < nodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(nodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.setVLANPorts(): Port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.setVLANPorts(): Port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
            }
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        int portList[] = convertNcListToPortList(nodeConns);
        status = new SNMPHandler(cmethUtil).setVLANPorts(nodeId, vlanId, portList);
        if(!status.isSuccess())
            logger.debug("ERROR: setVLANPorts(): Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", portList:...) to switch fail: " + status);

        return status;
    }

    //@Override//ad-sal
    public Status addVLANandSetPorts (Node node, Integer vlanID, String vlanName, List<NodeConnector> nodeConns){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS){
            logger.debug("ERROR: addVLANandSetPorts(): Fail to find node {} in DB", (Long)node.getID());
            return status;
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLANandSetPorts(): VLAN ID " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "addVLANandSetPorts(): VLAN ID " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
        }

        if(vlanName == null){
            logger.debug("ERROR: addVLANandSetPorts(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLANandSetPorts(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
        }

        //check port number is in valid range
        for(int i = 0; i < nodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(nodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.addVLANandSetPorts(): when add and set VLAN ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLANandSetPorts(): when add and set VLAN ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
            }
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        int portList[] = convertNcListToPortList(nodeConns);
        status = new SNMPHandler(cmethUtil).addVLANandSetPorts(nodeId, vlanName, vlanId, portList);
        if(!status.isSuccess())
            logger.debug("ERROR: addVLANandSetPorts(): Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", portList: " + Arrays.toString(portList) + ") to switch fail: " + status);

        return status;
    }

    //@Override//ad-sal
    public Status setVLANPorts (Node node, Integer vlanID, List<NodeConnector> taggedNodeConns, List<NodeConnector> untaggedNodeConns){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS){
            logger.debug("ERROR: setVLANPorts(): Fail to find node {} in DB", (Long)node.getID());
            return status;
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: setVLANPorts(): VLAN ID as " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
        }

        //check tagged port number is in valid range
        for(int i = 0; i < taggedNodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(taggedNodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.setVLANPorts(): tagged port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.setVLANPorts(): tagged p number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
            }
        }

        //check untagged port number is in valid range
        for(int i = 0; i < untaggedNodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(untaggedNodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.setVLANPorts(): untagged port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.setVLANPorts(): untagged p number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
            }
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        int portListT[] = convertNcListToPortList(taggedNodeConns);
        int portListU[] = convertNcListToPortList(untaggedNodeConns);
        status = new SNMPHandler(cmethUtil).setVLANPorts(nodeId, vlanId, portListT, portListU);
        if(!status.isSuccess())
            logger.debug("ERROR: setVLANPorts(): Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", portList:...) to switch fail: " + status);

        return status;
    }

    //@Override//ad-sal
    public Status addVLANandSetPorts (Node node, Integer vlanID, String vlanName, List<NodeConnector> taggedNodeConns, List<NodeConnector> untaggedNodeConns){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS){
            logger.debug("ERROR: addVLANandSetPorts(): Fail to find node {} in DB", (Long)node.getID());
            return status;
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLANandSetPorts(): VLAN ID " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "addVLANandSetPorts(): VLAN ID " + vlanID + " is invalid, when set VLAN ports to node " + getNodeIP((Long)node.getID()));
        }

        if(vlanName == null){
            logger.debug("ERROR: addVLANandSetPorts(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLANandSetPorts(): VLAN name is null, which is invalid, when addVLAN to node " + getNodeIP((Long)node.getID()));
        }

        //check tagged port number is in valid range
        for(int i = 0; i < taggedNodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(taggedNodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.addVLANandSetPorts(): when add and set VLAN tagged ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLANandSetPorts(): when add and set tagged VLAN ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
            }
        }

        //check untagged port number is in valid range
        for(int i = 0; i < untaggedNodeConns.size(); i++){
            NodeConnector nc = (NodeConnector)(untaggedNodeConns.get(i));
            int portNum = ((Short)(nc.getID())).intValue();
            if(portNum < 1 || portNum > NUMBER_OF_PORT){//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.addVLANandSetPorts(): when add and set untagged VLAN ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.addVLANandSetPorts(): when add and set untagged VLAN ports to node " + getNodeIP((Long)node.getID()) + ", VLAN " + vlanID + ", port number " + portNum + " is invalid");
            }
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        int portListT[] = convertNcListToPortList(taggedNodeConns);
        int portListU[] = convertNcListToPortList(untaggedNodeConns);
        status = new SNMPHandler(cmethUtil).addVLANandSetPorts(nodeId, vlanName, vlanId, portListT, portListU);
        if(!status.isSuccess())
            logger.debug("ERROR: addVLANandSetPorts(): Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", taggedPortList: " + Arrays.toString(portListT) + ", untaggedPortList: " + Arrays.toString(portListU) + ") to switch fail: " + status);

        return status;
    }

    //@Override//ad-sal
    public Status deleteVLAN(Node node, Integer vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: VLAN ID as " + vlanID + " is invalid, when delete VLAN from node " + getNodeIP((Long)node.getID()));
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when delete VLAN from node " + getNodeIP((Long)node.getID()));
        }

        if(isDummy) return new Status(StatusCode.SUCCESS);

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        status = new SNMPHandler(cmethUtil).deleteVLAN(nodeId, vlanId);
        if(!status.isSuccess())
            logger.debug("ERROR: deleteVLAN(): Delete VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ") on switch fail: " + status);

        return status;
    }

    //@Override//ad-sal
    public List<NodeConnector> getVLANPorts(Node node, Integer vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return null;

        if(!isValidVlan(vlanID)) return null;

        if(isDummy) return new CopyOnWriteArrayList<NodeConnector>();

        long nodeId = ((Long)node.getID()).longValue();
        int vlanId = vlanID.intValue();
        List<NodeConnector> ncList = new SNMPHandler(cmethUtil).getVLANPorts(nodeId, vlanId);
        if(ncList == null)
            logger.debug("ERROR: getVLANPorts(): fail to get VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ")'s ports");

        return ncList;
    }

    //@Override//ad-sal
    public VLANTable getVLANTable(Node node){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return null;

        if(isDummy) return new VLANTable();

        long nodeId = ((Long)node.getID()).longValue();
        VLANTable vlanTable = new SNMPHandler(cmethUtil).getVLANTable(nodeId);
        if(vlanTable == null)
            logger.debug("ERROR: getVLANTable(): fail to get VLAN table on node " + nodeId);

        return vlanTable;
    }

    private Status checkNodeIpValid(Node node){
        if(node == null){
            logger.debug("ERROR: checkNodeIpValid(): given null Node");
            return new Status(StatusCode.INTERNALERROR, "snmp4sdn: ConfigService: checkNodeIpValid(): given null Node");
        }
        CmethUtil cmethUtil = controller.getCmethUtil();//TODO: may remove this line, since cmethUtil has been assigned in setController()
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        if(sw_ipAddr == null){
            logger.debug("ERROR: checkNodeIpValid(): IP address of switch (nodeID: " + (Long)node.getID() + ") is not found in DB");
            return new Status(StatusCode.NOTFOUND, "IP address of switch (nodeID: " + (Long)node.getID() + ") is not found in DB");
        }
        else
            return new Status(StatusCode.SUCCESS);
    }

    private boolean isNodeIpValid(Node node){
        if(node == null){
            logger.debug("ERROR: isNodeIpValid(): given null Node");
            return false;
        }
        CmethUtil cmethUtil = controller.getCmethUtil();//TODO: may remove this line, since cmethUtil has been assigned in setController()
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        if(sw_ipAddr == null){
            logger.debug("ERROR: isNodeIpValid(): IP address of switch (nodeID: " + (Long)node.getID() + ") is not found in DB");
            return false;
        }
        else
            return true;
    }

    private String getNodeIP(long nodeId){
        return getNodeIP(new Long(nodeId));
    }

    private String getNodeIP(Long nodeId){
        return cmethUtil.getIpAddr(nodeId);
    }

    private boolean isValidVlan(Integer vlanId){
        if(vlanId < 1 || vlanId > 4095)//TODO: valid vlan range?
            return false;
        else
            return true;
    }

    //example:
    //  input nodeConn of port 1,2,5,7
    //  return an integer array: int[number_of_port] = {1,1,0,0,1,0,1,....}. number_of_port = 48 in DLink case.
    private int[] convertNcListToPortList(List<NodeConnector> nodeConns){
        if(nodeConns == null){
            logger.debug("ERROR: convertPortListToBytes(nodeConns), given nodeConns is null, can't proceed");
            return null;
        }
        NodeConnector nc = null;
        int portNum = -1;
        int portList[] = new int[NUMBER_OF_PORT];
        byte[] answer = new byte[NUMBER_OF_PORT/8];
        int index = 0;
        for(int i = 0; i < nodeConns.size(); i++){
            nc = (NodeConnector)(nodeConns.get(i));
            portNum = ((Short)(nc.getID())).intValue() - 1;
            portList[portNum] = 1;
        }
        String portListStr = "";
        for(int k = 0; k < NUMBER_OF_PORT; k++)
            portListStr += portList[k];
        logger.trace("convertNcListToPortList(): converted port list: {}", portListStr);
        return portList;
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("VLANService.getHelp():null");
    }

    public void _s4sVLAN(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: s4sVLAN [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | ");
            ci.println("\t\t  setPorts <sw_id(or mac_addr)> <vlan_id> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  setPortsOnlytagged <sw_id(or mac_addr)> <vlan_id> <tagged ports> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id> | getPorts <sw_id(or mac_addr)> <vlan_id> | getVLANTable <sw_id(or mac_addr)>]");
            ci.println("\t\t  (ports seperated by comma)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("add") == 0){
            ci.println();
            _s4sAddVLAN(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setPorts") == 0){
            ci.println();
            _s4sSetVLANPorts(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setPortsOnlytagged") == 0){
            ci.println();
            _s4sSetVLANPortsOnlyTagged(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("addVLANSetPorts") == 0){
            ci.println();
            _s4sAddVLANSetPorts(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delete") == 0){
            ci.println();
            _s4sDeleteVLAN(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getPorts") == 0){
            ci.println();
            _s4sGetVLANPorts(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getVLANTable") == 0){
            ci.println();
            _s4sGetVLANTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: s4sVLAN [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | ");
            ci.println("\t\t  setPorts <sw_id(or mac_addr)> <vlan_id> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  setPortsOnlytagged <sw_id(or mac_addr)> <vlan_id> <tagged ports> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id> | getPorts <sw_id(or mac_addr)> <vlan_id> | getVLANTable <sw_id(or mac_addr)>]");
            ci.println("\t\t  (ports seperated by comma)");
            ci.println();
            return;
        }
    }

    //CLI: s4sVLAN add <sw_mac> <vlan_id> <vlan_name>
    public void _s4sAddVLAN(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println("Please use: s4sVLAN add <sw_mac> <vlan_id> <vlan_name>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument" + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //parse arg4 (no need parsing)
        String vlanName = new String(arg4);

        Node node = createSNMPNode(nodeId);
        Status status = this.addVLAN(node, new Integer(vlanId), vlanName);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Add VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName:" + vlanName +") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Add VLAN (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName:" + vlanName +") to switch fail: " + status);
            ci.println();
        }
    }

    //CLI: s4sVLAN setPorts <sw_mac> <vlan_id> <tagged ports> <untagged ports> (seperated ports by comma)
    public void _s4sSetVLANPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || garbage != null){
            ci.println("Please use: s4sVLAN setPorts <sw_mac> <vlan_id> <tagged ports> <untagged ports> (seperated ports by comma)");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //parse arg4: String tagged ports to int value array
        String portListT = new String(arg4);
        int portsT[];
        if(portListT.compareToIgnoreCase("none") == 0)
            portsT = new int[0];
        else{
            portsT = convertPortListString2IntArray(arg4);
            if(portsT == null){
                ci.println("Error: the given port list \"" + arg4 + "\", convert to int array fail");
                return;
            }
        }

        //parse arg5: String untagged ports to int value array
        String portListU = new String(arg5);
        int portsU[];
        if(portListU.compareToIgnoreCase("none") == 0)
            portsU = new int[0];
        else{
            portsU = convertPortListString2IntArray(arg5);
            if(portsU == null){
                ci.println("Error: the given port list \"" + arg5 + "\", convert to int array fail");
                return;
            }
        }


        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncListT = ports2NcList(portsT, nodeId);
        List<NodeConnector> ncListU = ports2NcList(portsU, nodeId);
        Status status = this.setVLANPorts(node, new Integer(vlanId), ncListT, ncListU);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", taggedPortList:" + Arrays.toString(portsT) + ", untaggedPortList:" + Arrays.toString(portsU) + ") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Set VLAN Ports (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", taggedPortList:" + Arrays.toString(portsT) + ", untaggedPortList:" + Arrays.toString(portsU) + ") to switch fail: " + status);
            ci.println();
        }
    }

    //CLI: s4sVLAN setPortsOnlyTagged <sw_mac> <vlan_id> <tagged ports> (seperated ports by comma)
    public void _s4sSetVLANPortsOnlyTagged(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println("Please use: s4sVLAN setPortsOnlyTagged <sw_mac> <vlan_id> <tagged ports> (seperated ports by comma)");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //parse arg4: String tagged ports to int value array
        String portListT = new String(arg4);
        int portsT[];
        if(portListT.compareToIgnoreCase("none") == 0)
            portsT = new int[0];
        else{
            portsT = convertPortListString2IntArray(arg4);
            if(portsT == null){
                ci.println("Error: the given port list \"" + arg4 + "\", convert to int array fail");
                return;
            }
        }
        String portListChkT = "";//convert the ports int array to String, later can print for check correctness
        for(int i = 0; i < portsT.length; i++)
            portListChkT += portsT[i] + ",";

        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncListT = ports2NcList(portsT, nodeId);
        Status status = this.setVLANPorts(node, new Integer(vlanId), ncListT);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", taggedPortList:" + portListChkT + ") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Set VLAN Ports (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", taggedPortList:" + portListChkT + ") to switch fail: " + status);
            ci.println();
        }
    }

    //CLI: s4sVLAN addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <tagged ports> <untagged ports> (seperated ports by comma)
    public void _s4sAddVLANSetPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5= ci.nextArgument();
        String arg6= ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || garbage != null){
            ci.println("Please use: s4sVLAN addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <tagged ports> <untagged ports> (seperated ports by comma)");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //arg4: vlanName
        String vlanName = new String(arg4);

        //parse arg5: String tagged ports to int value array
        String portListT = new String(arg5);
        int portsT[];
        if(portListT.compareToIgnoreCase("none") == 0)
            portsT = new int[0];
        else{
            portsT = convertPortListString2IntArray(arg5);
            if(portsT == null){
                ci.println("Error: the given port list \"" + arg5 + "\", convert to int array fail");
                return;
            }
        }

        //parse arg6: String tagged ports to int value array
        String portListU = new String(arg6);
        int portsU[];
        if(portListU.compareToIgnoreCase("none") == 0)
            portsU = new int[0];
        else{
            portsU = convertPortListString2IntArray(arg6);
            if(portsU == null){
                ci.println("Error: the given port list \"" + arg6 + "\", convert to int array fail");
                return;
            }
        }


        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncListT = ports2NcList(portsT, nodeId);
        List<NodeConnector> ncListU = ports2NcList(portsU, nodeId);
        Status status = this.addVLANandSetPorts(node, new Integer(vlanId), vlanName, ncListT, ncListU);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Add VLAN and set ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName: " + vlanName + ", taggedPortList:" + Arrays.toString(portsT) + ", untaggedPortList:" + Arrays.toString(portsU) + ") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Add VLAN and set ports (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName: " + vlanName + ", taggedPortList:" + Arrays.toString(portsT) + ", untaggedPortList:" + Arrays.toString(portsU) + ") to switch fail: " + status);
            ci.println();
        }
    }

    //CLI: s4sVLAN delete <sw_mac> <vlan_id>
    public void _s4sDeleteVLAN(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println("Please use: s4sVLAN delete <sw_mac> <vlan_id>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        Node node = createSNMPNode(nodeId);
        Status status = this.deleteVLAN(node, new Integer(vlanId));
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Delete VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ") on switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Delete VLAN (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ") on switch fail: " + status);
            ci.println("  --> error code = " + status.getCode() + ": " + status.getDescription());
            ci.println();
        }
    }

    //CLI: s4sVLAN getVLANPorts <sw_mac> <vlan_id>
    public void _s4sGetVLANPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println("Please use: s4sVLAN getVLANPorts <sw_mac> <vlan_id>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncList = this.getVLANPorts(node, new Integer(vlanId));
        if(ncList == null){
            ci.println();
            ci.println("Fail: fail to get VLAN (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ")'s ports");
            ci.println();
        }
        else{
            ci.println();
            ci.print("VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ")'s ports: {");
            for(NodeConnector nc : ncList){
                ci.print((Short)(nc.getID()) + ",");
            }
            ci.println("}");
            ci.println();
        }
    }

    //CLI: s4sVLAN getVLANTable <sw_mac>
    public void _s4sGetVLANTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println("Please use: s4sVLAN getVLANTable <sw_mac>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0)
                nodeId = Long.parseLong(arg2);
            else
                nodeId = HexString.toLong(arg2);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        Node node = createSNMPNode(nodeId);
        VLANTable table = this.getVLANTable(node);
        if(table == null){
            ci.println("Fail: fail to get VLAN table on node " + nodeId);
        }
        else{
            ci.println("------- VLAN on node " + nodeId + " --------");
            //ci.println("There are " + table.getEntries().size() + " VLANs:");;
            ci.println(table.toString());
        }
    }

    private static Node createSNMPNode(long switchId) {
        try {
            return new Node("SNMP", new Long(switchId));
        } catch (ConstructionException e1) {
            logger.debug("ERROR: VLANService: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", switchId, e1);
            return null;
        }
    }

    private static NodeConnector createSNMPNodeConnector(long switchId, int portNum) {
        try {
            Node node = createSNMPNode(switchId);
            return new NodeConnector("SNMP", new Short((short)portNum), node);
        } catch (Exception e1) {
            logger.error("VLANManagerUtil create NodeConnector of type SNMP fail: ", e1);
            return null;
        }
    }

    private List<NodeConnector> ports2NcList(int ports[], long nodeID){
        List<NodeConnector> ncList = new CopyOnWriteArrayList<NodeConnector>();
        for(int i = 0; i < ports.length; i++){
            NodeConnector nc = createSNMPNodeConnector(nodeID, ports[i]);
            ncList.add(nc);
        }
        return ncList;
    }

    private int[] convertPortListString2IntArray(String portList){
        if(portList.trim().length() == 0)
            return new int[0];

        portList = portList.trim();
        String[] portsStr = portList.split(",");
        int ports[] = new int[portsStr.length];
        for(int i = 0; i < ports.length; i++){
            try{
                ports[i] = Integer.parseInt(portsStr[i].trim());
            }
            catch(NumberFormatException e1){
                logger.debug("ERROR: convertPortListString2IntArray() error: input string \"" + portList +"\" has non-number string: " + e1);
                return null;//means fail
            }
        }
        return ports;
    }
}
