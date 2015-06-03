/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.fdbmanager.impl;

import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareConsumer;

//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntryType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutputBuilder;

import org.opendaylight.yangtools.yang.common.RpcResult;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FdbManagerImpl extends AbstractBindingAwareConsumer implements
        BundleActivator, BindingAwareConsumer, CommandProvider {

    private static final Logger logger = LoggerFactory.getLogger(FdbManagerImpl.class);

    private FdbService fdb;
    private ConsumerContext session;

    @Override
    public void onSessionInitialized(ConsumerContext session) {
        this.session = session;
        registerWithOSGIConsole();
        logger.debug("FdbManagerImpl: onSessionInitialized() completed");
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

    //CLI: fdbMgr
    public void _fdbMgr(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: fdbMgr [getEntry <switch> <vlan_id> <dest_mac_addr> | deleteEntry <switch> <vlan_id> <dest_mac_addr> | ");
            ci.println("\t\t  setEntry <switch> <vlan_id> <dest_mac_addr> <port>| getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getEntry") == 0){
            ci.println();
            _fdbMgrGetFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("deleteEntry") == 0){
            ci.println();
            _fdbMgrDeleteFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setEntry") == 0){
            ci.println();
            _fdbMgrSetFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getTable") == 0){
            ci.println();
            _fdbMgrGetFDBTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: fdbMgr [getEntry <switch> <vlan_id> <dest_mac_addr> | deleteEntry <switch> <vlan_id> <dest_mac_addr> | ");
            ci.println("\t\t  setEntry <switch> <vlan_id> <dest_mac_addr> <port>| getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: fdbMgr getEntry <switch> <vlan_id> <dest_mac_addr>
    public void _fdbMgrGetFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null  || garbage != null){
            ci.println();
            ci.println("Please use: fdbMgr getEntry <switch> <vlan_id> <dest_mac_addr>");
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

        //parse arg3: String vlan_id to int value nodeId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //parse arg4: String dest_mac_addr to long value nodeId
        long destMac = -1;
        try{
            if(arg4.indexOf(":") < 0)
                destMac = Long.parseLong(arg4);
            else
                destMac = HexString.toLong(arg4);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg4 + " to long value error: " + e1);
            return;
        }

        //check FdbService exists?
        if (fdb == null) {
            fdb = this.session.getRpcService(FdbService.class);
            if (fdb == null) {
                logger.debug("ERROR: Can't get FdbService, can't proceed!");
                return;
            }
        }

        //prepare parameters to get fdb entry
        GetFdbEntryInputBuilder ib = new GetFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(destMac);

        //execute getFdbEntry(), and check return null parameters?
        FdbEntry entry;
        String destMacAddr = HexString.toHexString(destMac).toUpperCase();
        try {
            Future<RpcResult<GetFdbEntryOutput>> ret = fdb.getFdbEntry(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to get FDB entry on node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr + " (null return)");
                ci.println();
                return;
            }
            RpcResult<GetFdbEntryOutput> result = ret.get();
            if(result == null){
                ci.println();
                ci.println("Fail to get FDB entry on node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr + " (null result)");
                ci.println();
                return;
            }
            if(result.getResult() == null){
                ci.println();
                ci.println("Fail to get FDB entry on node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr + " (null in result)");
                ci.println();
                return;
            }

            entry = result.getResult();
            if(entry == null){
                ci.println();
                ci.println("Fail to get FDB entry on node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr);
                ci.println();
                return;
            }
        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call getFdbEntry() occurs exception (node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call getFdbEntry() occurs exception (node " + nodeId + " for VLAN ID " + vlanId + " dest MAC " + destMacAddr + "): " + ee);
            ci.println();
            return;
        }

        String typeStr = "null";
            if(entry.getType() == FdbEntryType.OTHER)
                typeStr = "Other";
            else if(entry.getType() == FdbEntryType.INVALID)
                typeStr = "Invalid";
            else if(entry.getType() == FdbEntryType.LEARNED)
                typeStr = "Dynamic";
            else if(entry.getType() == FdbEntryType.SELF)
                typeStr = "Self";
            else if(entry.getType() == FdbEntryType.MGMT)
                typeStr = "Static";
            else
                typeStr = "NA";

        String portStr = "null";
            if(entry.getPort() == 0)
                portStr = "CPU";
            else
                portStr = Short.toString(entry.getPort());

        ci.println();
        ci.println("Requested FDB entry on node " + entry.getNodeId() + ": <VLAN " + entry.getVlanId() + ", MAC " + HexString.toHexString(entry.getDestMacAddr()).toUpperCase() + ", Port " + portStr + ", Type " + typeStr + ">");
        ci.println();

    }

    //CLI: fdbMgr deleteEntry <switch> <vlan_id> <dest_mac_addr>
    public void _fdbMgrDeleteFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: fdbMgr deleteEntry <switch> <vlan_id> <dest_mac_addr>");
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

        //parse arg3: String vlan_id to int value nodeId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //parse arg4: String dest_mac_addr to long value nodeId
        long destMac = -1;
        try{
            if(arg4.indexOf(":") < 0)
                destMac = Long.parseLong(arg4);
            else
                destMac = HexString.toLong(arg4);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg4 + " to long value error: " + e1);
            return;
        }

        //check FdbService exists?
        if (fdb == null) {
            fdb = this.session.getRpcService(FdbService.class);
            if (fdb == null) {
                logger.debug("ERROR: Can't get FdbService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set fdb entry
        DelFdbEntryInputBuilder ib = new DelFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(new Long(destMac));

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<DelFdbEntryOutput> rpcResult;
        try {
            Future<RpcResult<DelFdbEntryOutput>> ret = fdb.delFdbEntry(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to delete FDB Table on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to delete FDB Table entry on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to delete FDB Table entry on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getDelFdbEntryResult() == null){
                ci.println();
                ci.println("Fail to delete FDB Table entry on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call delFdbEntry() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call delFdbEntry() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result delFdbResult = rpcResult.getResult().getDelFdbEntryResult();
        switch (delFdbResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully delete FDB entry on node " + nodeId + ": <VLAN " + vlanId + ", MAC " + HexString.toHexString(destMac).toUpperCase() + ">");
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to delete FDB Table entry on node " + nodeId + "for <VLAN " + vlanId + ", MAC " + HexString.toHexString(destMac).toUpperCase() + "> (ErrorCode: " + delFdbResult + ")");
                ci.println();
            }
        }
    }

    //CLI: fdbMgr setEntry <switch> <vlan_id> <dest_mac_addr> <port>
    public void _fdbMgrSetFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || garbage != null){
            ci.println();
            ci.println("Please use: fdbMgr setEntry <switch> <vlan_id> <dest_mac_addr> <port>");
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

        //parse arg3: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to int value error: " + e1);
            return;
        }

        //arg4: String dest_mac_addr to long value destMac
        long destMac = -1;
        try{
            if(arg4.indexOf(":") < 0)
                destMac = Long.parseLong(arg4);
            else
                destMac = HexString.toLong(arg4);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg4 + " to long value error: " + e1);
            return;
        }

        //parse arg5: String port to short value
        short port = -1;
        try{
            port = Short.parseShort(arg5);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg5 + " to int value error: " + e1);
            return;
        }


        //check FdbService exists?
        if (fdb == null) {
            fdb = this.session.getRpcService(FdbService.class);
            if (fdb == null) {
                logger.debug("ERROR: Can't get FdbService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set fdb entry
        SetFdbEntryInputBuilder ib = new SetFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(new Long(destMac)).setPort(new Short(port));

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<SetFdbEntryOutput> rpcResult;
        try {
            Future<RpcResult<SetFdbEntryOutput>> ret = fdb.setFdbEntry(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set FDB Table on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set FDB Table entry on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set FDB Table entry on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getSetFdbEntryResult() == null){
                ci.println();
                ci.println("Fail to set FDB Table entry on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call setFdbEntry() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call setFdbEntry() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result setFdbResult = rpcResult.getResult().getSetFdbEntryResult();
        switch (setFdbResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully set FDB entry on node " + nodeId + ": <VLAN " + vlanId + ", MAC " + HexString.toHexString(destMac).toUpperCase() + ", Port " + port + ">");
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to set FDB Table entry on node " + nodeId + "for <VLAN " + vlanId + ", MAC " + HexString.toHexString(destMac).toUpperCase() + ", Port " + port + ">" + " (ErrorCode: " + setFdbResult + ")");
                ci.println();
            }
        }

    }

    //CLI: fdbMgr getTable <switch>
    public void _fdbMgrGetFDBTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: fdbMgr getTable <switch>");
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

        //check FdbService exists?
        if (fdb == null) {
            fdb = this.session.getRpcService(FdbService.class);
            if (fdb == null) {
                logger.debug("ERROR: Can't get FdbService, can't proceed!");
                return;
            }
        }

        //prepare parameters to get fdb entry
        GetFdbTableInputBuilder ib = new GetFdbTableInputBuilder().setNodeId(new Long(nodeId));

        //execute getFdbEntry(), and check return null parameters?
        List<FdbTableEntry> fdbTable;
        try {
            Future<RpcResult<GetFdbTableOutput>> ret = fdb.getFdbTable(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to get FDB Table on node " + nodeId + " (null return)");
                ci.println();
                return;
            }
            RpcResult<GetFdbTableOutput> result = ret.get();
            if(result == null){
                ci.println();
                ci.println("Fail to get FDB Table on node " + nodeId + " (null result)");
                ci.println();
                return;
            }
            if(result.getResult() == null){
                ci.println();
                ci.println("Fail to get FDB Table on node " + nodeId + " (null in result)");
                ci.println();
                return;
            }

            fdbTable = result.getResult().getFdbTableEntry();
            if(fdbTable == null){
                ci.println();
                ci.println("Fail to get FDB Table on node " + nodeId);
                ci.println();
                return;
            }
        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call getFdbTable() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call getFdbTable() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        ci.println();
        ci.println("========" + "FDB Table on node " + nodeId + "========");
        ci.println();
        ci.println("VLAN\t\tMAC\t\tPort\tType");
        for(int i = 0; i < fdbTable.size(); i++){
            FdbTableEntry entry = fdbTable.get(i);

            String typeStr = "null";
            if(entry.getType() == FdbEntryType.OTHER)
                typeStr = "Other";
            else if(entry.getType() == FdbEntryType.INVALID)
                typeStr = "Invalid";
            else if(entry.getType() == FdbEntryType.LEARNED)
                typeStr = "Dynamic";
            else if(entry.getType() == FdbEntryType.SELF)
                typeStr = "Self";
            else if(entry.getType() == FdbEntryType.MGMT)
                typeStr = "Static";
            else
                typeStr = "NA";

            String portStr = "null";
            if(entry.getPort() == 0)
                portStr = "CPU";
            else
                portStr = Short.toString(entry.getPort());
            
            ci.println(entry.getVlanId() + "\t" + HexString.toHexString(entry.getDestMacAddr()).substring(6, 23).toUpperCase() + "\t" + portStr + "\t" + typeStr);
            //substring(6, 23) is to truncate redundant "00:00" from the string generated by HexString tool
        }
        ci.println();

    }


    @Override//CommandProvider's
    public String getHelp() {
        return new String("FdbManagerImpl.getHelp():null");
    }
}

