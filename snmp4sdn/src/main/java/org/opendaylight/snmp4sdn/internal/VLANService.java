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

//no-sal/*not to remove this interface, otherwise code vary a lot //TODO:clean code */
import org.opendaylight.snmp4sdn.IVLANService;
import org.opendaylight.snmp4sdn.VLANTable;
import org.opendaylight.snmp4sdn.VLANTable.VLANTableEntry;
//custom ad-sal
/*import org.opendaylight.controller.sal.vlan.IPluginInVLANService;
import org.opendaylight.controller.sal.vlan.VLANTable;
import org.opendaylight.controller.sal.vlan.VLANTable.VLANTableEntry;*/
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.set.vlan.ports.input.*;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VLANService implements /*IPluginInVLANService//custom ad-sal,*/ VlanService/*md-sal*/, /*IVLANService,//no-sal*/ CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(VLANService.class);

    public boolean isDummy = false;

    private Controller controller = null;
    private CLIHandler cli = null;
    private CmethUtil cmethUtil = null;

    private int NUMBER_OF_PORT_DLINK = 24;
    private int NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK = 48;

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

    @Override//md-sal
    public Future<RpcResult<AddVlanOutput>> addVlan(AddVlanInput input){
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        String vlanName = input.getVlanName();
        Node node = createSNMPNode(nodeId.longValue());
        Status status = addVLAN(node, vlanId, vlanName);//argument value validation is checked in the following

        if(status.isSuccess()){
            AddVlanOutputBuilder ob = new AddVlanOutputBuilder().setAddVlanResult(Result.SUCCESS);
            RpcResult<AddVlanOutput> rpcResult =
                    Rpcs.<AddVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: addVlan(): add VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName:" + vlanName + ") to switch fail: " + status);
            AddVlanOutputBuilder ob = new AddVlanOutputBuilder().setAddVlanResult(Result.FAIL);
            RpcResult<AddVlanOutput> rpcResult =
                    Rpcs.<AddVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }

        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
    }

    //@Override//md-sal
    public Future<RpcResult<DeleteVlanOutput>> deleteVlan_dummy(DeleteVlanInput input){
        System.out.println("deleteVlan_dummy");
            DeleteVlanOutputBuilder ob = new DeleteVlanOutputBuilder().setDeleteVlanResult(Result.SUCCESS);
            RpcResult<DeleteVlanOutput> rpcResult =
                    Rpcs.<DeleteVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<DeleteVlanOutput>> deleteVlan(DeleteVlanInput input){
        System.out.println("downlayer deleteVlan() is called");
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        Node node = createSNMPNode(nodeId.longValue());

        Status status = deleteVLAN(node, vlanId);//argument value validation is checked in the following
        if(status.isSuccess()){
            DeleteVlanOutputBuilder ob = new DeleteVlanOutputBuilder().setDeleteVlanResult(Result.SUCCESS);
            RpcResult<DeleteVlanOutput> rpcResult =
                    Rpcs.<DeleteVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: deleteVlan(): delete VLAN (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ") to switch fail: " + status);
            DeleteVlanOutputBuilder ob = new DeleteVlanOutputBuilder().setDeleteVlanResult(Result.FAIL);
            RpcResult<DeleteVlanOutput> rpcResult =
                    Rpcs.<DeleteVlanOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
    }

    @Override//md-sal
    public Future<RpcResult<GetVlanPortsOutput>> getVlanPorts(GetVlanPortsInput input){
        return null;
    }

    @Override//md-sal
    public Future<RpcResult<java.lang.Void>> printVlanTable(PrintVlanTableInput input){
        Long nodeId = input.getNodeId();
        Node node = createSNMPNode(nodeId.longValue());
        VLANTable vTable = getVLANTable(node);//argument value validation is checked in the following

        return null;
        //TODO: maven say no package "RpcResultBuilder"
        /*if(vTable == null)
            return Futures.immediateFuture( RpcResultBuilder.<Void> failed().build() );

        System.out.println(vTable.toString());
        return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );*/
    }

    @Override//md-sal
    public Future<RpcResult<SetVlanPortsOutput>> setVlanPorts(SetVlanPortsInput input){
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        List<SetPortListEntry> ports = input.getSetPortListEntry();
        Node node = createSNMPNode(nodeId.longValue());

        //create nodeConnectors
        List<NodeConnector> nodeConns = new ArrayList<NodeConnector>();
        //String portList = "";
        //ports = portList.split(",");
        try{
            //for(int i = 0; i < ports.length; i++)
            //    nodeConns.add(new NodeConnector("SNMP", Short.parseShort(ports[i]), node));
            for(SetPortListEntry port : ports)
                nodeConns.add(new NodeConnector("SNMP", port.getPort(), node));
        }catch(Exception e){
            logger.error("in s4sSetVLANPorts_execute(): create nodeconnector error -- " + e);
        }

        setVLANPorts(node, vlanId, nodeConns);//argument value validation is checked in the following

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
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
            if(portNum < 1 || portNum > NUMBER_OF_PORT_DLINK)//TODO: max port number as upper bound
                logger.debug("ERROR: VLANService.setVLANPorts(): Port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
                return new Status(StatusCode.NOTACCEPTABLE, "VLANService.setVLANPorts(): Port number as " + portNum + " is invalid, when setVLANPorts to node " + getNodeIP((Long)node.getID()) + "'s VLAN " + vlanID);
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
            if(portNum < 1 || portNum > NUMBER_OF_PORT_DLINK){//TODO: max port number as upper bound
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
        int portList[] = new int[NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK];
        byte[] answer = new byte[NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK/8];
        int index = 0;
        for(int i = 0; i < nodeConns.size(); i++){
            nc = (NodeConnector)(nodeConns.get(i));
            portNum = ((Short)(nc.getID())).intValue() - 1;
            portList[portNum] = 1;
        }
        String portListStr = "";
        for(int k = 0; k < NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK; k++)
            portListStr += portList[k];
        logger.trace("port list: {}", portListStr);
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
            ci.println("Please use: s4sVLAN [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | setPorts <sw_id(or mac_addr)> <vlan_id> <ports (seperated by comma)> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <ports (seperated by comma)> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id> | getPorts <sw_id(or mac_addr)> <vlan_id> | getVLANTable <sw_id(or mac_addr)>]");
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
            ci.println("Please use: s4sVLAN [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | setPorts <sw_id(or mac_addr)> <vlan_id> <ports (seperated by comma)> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <ports (seperated by comma)> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id> | getPorts <sw_id(or mac_addr)> <vlan_id> | getVLANTable <sw_id(or mac_addr)>]");
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

    //CLI: s4sVLAN setPorts <sw_mac> <vlan_id> <ports (seperated by comma)>
    public void _s4sSetVLANPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println("Please use: s4sVLAN setPorts <sw_mac> <vlan_id> <ports (seperated by comma)>");
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

        //parse arg4: String ports to int value array
        String portList = new String(arg4);
        int ports[] = convertPortListString2IntArray(arg4);
        if(ports == null){
            ci.println("Error: the given port list \"" + arg4 + "\", convert to int array fail");
            return;
        }
        /*for(int i = 0; i < ports.length; i++){//check invalid port number//Here should not do this check, because actually we want to verify whether the setVLANPorts() below can check port list validness
            if(ports[i] < 1 || ports[i] > NUMBER_OF_PORT_DLINK){
                ci.println("Error: invalid input ports[" + i + "] as " + ports[i]);
                return;
            }
        }*/
        String portListChk = "";//convert the ports int array to String, later can print for check correctness
        for(int i = 0; i < ports.length; i++)
            portListChk += ports[i] + ",";


        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncList = ports2NcList(ports, nodeId);
        Status status = this.setVLANPorts(node, new Integer(vlanId), ncList);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Set VLAN Ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", portList:" + portListChk +") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Set VLAN Ports (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", portList:" + portListChk +") to switch fail: " + status);
            ci.println();
        }
    }

    //CLI: s4sVLAN addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <ports (seperated by comma)>
    public void _s4sAddVLANSetPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5= ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || garbage != null){
            ci.println("Please use: s4sVLAN addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <ports (seperated by comma)>");
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

        //parse arg5: String ports to int value array
        String portList = new String(arg5);
        int ports[] = convertPortListString2IntArray(arg5);
        if(ports == null){
            ci.println("Error: the given port list \"" + arg5 + "\", convert to int array fail");
            return;
        }
        /*for(int i = 0; i < ports.length; i++){//check invalid port number//Here should not do this check, because actually we want to verify whether the setVLANPorts() below can check port list validness
            if(ports[i] < 1 || ports[i] > NUMBER_OF_PORT_DLINK){
                ci.println("Error: invalid input ports[" + i + "] as " + ports[i]);
                return;
            }
        }*/
        String portListChk = "";//convert the ports int array to String, later can print for check correctness
        for(int i = 0; i < ports.length; i++)
            portListChk += ports[i] + ",";


        Node node = createSNMPNode(nodeId);
        List<NodeConnector> ncList = ports2NcList(ports, nodeId);
        Status status = this.addVLANandSetPorts(node, new Integer(vlanId), vlanName, ncList);
        if(status.isSuccess()){
            ci.println();
            ci.println("Success: Add VLAN and set ports (node:" + nodeId + "(" + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName: " + vlanName + ", portList:" + portListChk +") to switch successfully");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail: Add VLAN and set ports (node:" + nodeId + "(ip: " + getNodeIP(nodeId) + ")" + ", vlanId:" + vlanId + ", vlanName: " + vlanName + ", portList:" + portListChk +") to switch fail: " + status);
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
        String[] portsStr = portList.split(",");
        int ports[] = new int[portsStr.length];
        for(int i = 0; i < ports.length; i++){
            try{
                ports[i] = Integer.parseInt(portsStr[i]);
            }
            catch(NumberFormatException e1){
                logger.debug("ERROR: convertPortListString2IntArray() error: input string \"" + portList +"\" has non-number string: " + e1);
                return null;//means fail
            }
        }
        return ports;
    }
}
