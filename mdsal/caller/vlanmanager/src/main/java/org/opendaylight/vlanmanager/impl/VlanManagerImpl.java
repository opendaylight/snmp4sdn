/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.vlanmanager.impl;

import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareConsumer;

//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry;

import org.opendaylight.yangtools.yang.common.RpcResult;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VlanManagerImpl extends AbstractBindingAwareConsumer implements
        BundleActivator, BindingAwareConsumer, CommandProvider {

    private static final Logger logger = LoggerFactory.getLogger(VlanManagerImpl.class);

    private VlanService vlan;
    private ConsumerContext session;

    @Override
    public void onSessionInitialized(ConsumerContext session) {
        this.session = session;
        registerWithOSGIConsole();
        logger.debug("VlanManagerImpl: onSessionInitialized() completed");
    }

    @Override
    protected void startImpl(BundleContext context) {
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    /*
    * CLI as following
    */

    //CLI: vlanMgr
    public void _vlanMgr(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: vlanMgr [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | ");
            ci.println("\t\t  setPorts <sw_id(or mac_addr)> <vlan_id> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id>] | ");
            ci.println("\t\t  getVLANTable <sw_id(or mac_addr)>");
            ci.println("\t\t  (ports seperated by comma)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("add") == 0){
            ci.println();
            _vlanMgrAddVLAN(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setPorts") == 0){
            ci.println();
            _vlanMgrSetPorts(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("addVLANSetPorts") == 0){
            ci.println();
            _vlanMgrAddVLANSetPorts(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delete") == 0){
            ci.println();
            _vlanMgrDeleteVLAN(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getVLANTable") == 0){
            ci.println();
            _vlanMgrGetVlanTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: vlanMgr [add <sw_id(or mac_addr)> <vlan_id> <vlan_name> | ");
            ci.println("\t\t  setPorts <sw_id(or mac_addr)> <vlan_id> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  addVLANSetPorts <sw_id(or mac_addr)> <vlan_id> <vlan_name> <tagged ports> <untagged ports> | ");
            ci.println("\t\t  delete <sw_mac> <vlan_id>] | ");
            ci.println("\t\t  getVLANTable <sw_id(or mac_addr)>");
            ci.println("\t\t  (ports seperated by comma)");
            ci.println();
            return;
        }
    }

    //CLI: vlanMgr addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <tagged ports> <untagged ports> (seperated ports by comma)
    public void _vlanMgrAddVLANSetPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5= ci.nextArgument();
        String arg6= ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || garbage != null){
            ci.println("Please use: vlanMgr addVLANSetPorts <sw_mac> <vlan_id> <vlan_name> <tagged ports> <untagged ports> (seperated ports by comma)");
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

        //parse arg5: tagged ports
        String portListT = new String(arg5);

        //parse arg6: untagged ports
        String portListU = new String(arg6);


        //check VlanService exists?
        if (vlan == null) {
            vlan = this.session.getRpcService(VlanService.class);
            if (vlan == null) {
                logger.debug("Can't get VlanService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set vlan entry
        AddVlanAndSetPortsInputBuilder ib = new AddVlanAndSetPortsInputBuilder();
        ib = ib.setNodeId(new Long(nodeId))
                .setVlanId(new Integer(vlanId))
                .setVlanName(vlanName)
                .setTaggedPortList(portListT)
                .setUntaggedPortList(portListU);

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<AddVlanAndSetPortsOutput> rpcResult;
        try {
            Future<RpcResult<AddVlanAndSetPortsOutput>> ret = vlan.addVlanAndSetPorts(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set VLAN Table on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set VLAN Table entry on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set VLAN Table entry on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getAddVlanAndSetPortsResult() == null){
                ci.println();
                ci.println("Fail to set VLAN Table entry on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call VlanService.addVlanAndSetPorts() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call VlanService.addVlanAndSetPorts() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result result = rpcResult.getResult().getAddVlanAndSetPortsResult();
        switch (result) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully set VLAN entry on node " + nodeId + ": <ID: " + vlanId + ", Name: " + vlanName + ", Tagged ports: " + portListT + ", Untagged ports: " + portListU + ">");
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to set VLAN Table entry on node " + nodeId + ": <ID: " + vlanId + ", Name: " + vlanName + ", Tagged ports: " + portListT + ", Untagged ports: " + portListU + ">" + " (ErrorCode: " + result + ")");
                ci.println();
            }
        }
    }

    //CLI: vlanMgr addVLAN <sw_mac> <vlan_id> <vlan_name>
    public void _vlanMgrAddVLAN(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println("Please use: vlanMgr addVLANSetPorts <sw_mac> <vlan_id> <vlan_name>");
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


        //check VlanService exists?
        if (vlan == null) {
            vlan = this.session.getRpcService(VlanService.class);
            if (vlan == null) {
                logger.debug("Can't get VlanService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set vlan entry
        AddVlanInputBuilder ib = new AddVlanInputBuilder();
        ib = ib.setNodeId(new Long(nodeId))
                .setVlanId(new Integer(vlanId))
                .setVlanName(vlanName);

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<AddVlanOutput> rpcResult;
        try {
            Future<RpcResult<AddVlanOutput>> ret = vlan.addVlan(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to add VLAN on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to add VLAN on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to add VLAN on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getAddVlanResult() == null){
                ci.println();
                ci.println("Fail to add VLAN on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call VlanService.addVlan() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call VlanService.addVlan() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result result = rpcResult.getResult().getAddVlanResult();
        switch (result) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully add VLAN " + vlanId + " on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to add VLAN " + vlanId + " on node " + nodeId + " (ErrorCode: " + result + ")");
                ci.println();
            }
        }
    }

    //CLI: vlanMgr setPorts <sw_mac> <vlan_id> <tagged ports> <untagged ports> (seperated ports by comma)
    public void _vlanMgrSetPorts(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        //String arg4 = ci.nextArgument();//reuse "_vlanMgrAddVLANSetPorts()"'s code, so here mark the arg4 vlanName
        String arg5= ci.nextArgument();
        String arg6= ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || /*arg4 == null ||*/ arg5 == null || arg6 == null || garbage != null){
            ci.println("Please use: vlanMgr setPorts <sw_mac> <vlan_id> <tagged ports> <untagged ports> (seperated ports by comma)");
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
        //String vlanName = new String(arg4);

        //parse arg5: tagged ports
        String portListT = new String(arg5);

        //parse arg6: untagged ports
        String portListU = new String(arg6);


        //check VlanService exists?
        if (vlan == null) {
            vlan = this.session.getRpcService(VlanService.class);
            if (vlan == null) {
                logger.debug("Can't get VlanService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set vlan entry
        SetVlanPortsInputBuilder ib = new SetVlanPortsInputBuilder();
        ib = ib.setNodeId(new Long(nodeId))
                .setVlanId(new Integer(vlanId))
                .setTaggedPortList(portListT)
                .setUntaggedPortList(portListU);

        //execute setVlanPorts(), and check return null parameters?
        RpcResult<SetVlanPortsOutput> rpcResult;
        try {
            Future<RpcResult<SetVlanPortsOutput>> ret = vlan.setVlanPorts(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set VLAN ports on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set VLAN ports on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set VLAN ports on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getSetVlanPortsResult() == null){
                ci.println();
                ci.println("Fail to set VLAN ports on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call VlanService.setVlanPorts() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call VlanService.setVlanPorts() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result result = rpcResult.getResult().getSetVlanPortsResult();
        switch (result) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully set VLAN ports on node " + nodeId + ": <ID: " + vlanId + ", Tagged ports: " + portListT + ", Untagged ports: " + portListU + ">");
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to set VLAN Table ports on node " + nodeId + ": <ID: " + vlanId + ", Tagged ports: " + portListT + ", Untagged ports: " + portListU + ">" + " (ErrorCode: " + result + ")");
                ci.println();
            }
        }
    }

    //CLI: vlanMgr delete <sw_mac> <vlan_id>
    public void _vlanMgrDeleteVLAN(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println("Please use: vlanMgr delete <sw_mac> <vlan_id>");
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


        //check VlanService exists?
        if (vlan == null) {
            vlan = this.session.getRpcService(VlanService.class);
            if (vlan == null) {
                logger.debug("Can't get VlanService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set vlan entry
        DeleteVlanInputBuilder ib = new DeleteVlanInputBuilder()
                                                        .setNodeId(new Long(nodeId))
                                                        .setVlanId(new Integer(vlanId));

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<DeleteVlanOutput> rpcResult;
        try {
            Future<RpcResult<DeleteVlanOutput>> ret = vlan.deleteVlan(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to delete VLAN on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to deleteVLAN on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to delete VLAN on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getDeleteVlanResult() == null){
                ci.println();
                ci.println("Fail to delete VLAN on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call VlanService.deleteVlan() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call VlanService.deleteVlan() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result result = rpcResult.getResult().getDeleteVlanResult();
        switch (result) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully delete VLAN " + vlanId + " on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to delete VLAN " + vlanId + " on node " + nodeId + " (ErrorCode: " + result + ")");
                ci.println();
            }
        }
    }

    //CLI: vlanMgr getVlanTable <switch>
    public void _vlanMgrGetVlanTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: vlanMgr getVlanTable <switch>");
            ci.println();
            return;
        }

        //parse arg2: String switch to long value nodeId
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


        //check VlanService exists?
        if (vlan == null) {
            vlan = this.session.getRpcService(VlanService.class);
            if (vlan == null) {
                logger.debug("Can't get VlanService, can't proceed!");
                return;
            }
        }

        //prepare parameters to clear VLAN Table
        GetVlanTableInputBuilder ib = new GetVlanTableInputBuilder().setNodeId(nodeId);

        //execute getVlanTable(), and check return null parameters?
        List<VlanTableEntry> vlanTable;
        try {
            Future<RpcResult<GetVlanTableOutput>> ret = vlan.getVlanTable(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to get VLAN Table on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            RpcResult<GetVlanTableOutput> rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to get VLAN Table on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to get VLAN Table on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            vlanTable = rpcResult.getResult().getVlanTableEntry();
            if(vlanTable == null){
                ci.println();
                ci.println("Fail to get VLAN Table on node " + nodeId);
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call VlanService.getVlanTable() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call VlanService.getVlanTable() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        ci.println();
        ci.println("========" + "VLAN Table on node " + nodeId + "========");
        ci.println();
        ci.println("ID\tPorts");
        for(VlanTableEntry entry : vlanTable){
            Integer vlanId = entry.getVlanId();
                if(vlanId == null){ci.println("ERROR: a null vlanId in the returned VlanTable"); return;}
            List<Short> portList = entry.getPortList();
                if(portList == null){ci.println("ERROR: a null portList in the returned VlanTable"); return;}

            String portListStr = "";
            for(Short port : portList)
                portListStr += port + ",";
            if(portListStr.endsWith(","))
                portListStr = portListStr.substring(0, portListStr.length() - 1);

            ci.println(vlanId + "\t" + portListStr);
        }
        ci.println();

    }

    public List<Short> convertPortListString2ShortList(String portList){
        String[] portsStr = portList.split(",");
        List<Short> ports = new ArrayList<Short>();
        for(int i = 0; i < portsStr.length; i++){
            try{
                ports.add(Short.parseShort(portsStr[i]));
                if(ports.get(i) < 0){
                    logger.debug("VlanManagerImpl.convertPortListString2ShortArray() error: input string \"" + portList +"\" has invalid port number " + portsStr[i]);
                    return null;
                }
            }
            catch(NumberFormatException e1){
                logger.debug("VlanManagerImpl.convertPortListString2ShortArray() error: input string \"" + portList +"\" has non-number string: " + e1);
                return null;//means fail
            }
        }
        return ports;
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("VlanManagerImpl.getHelp():null");
    }
}

