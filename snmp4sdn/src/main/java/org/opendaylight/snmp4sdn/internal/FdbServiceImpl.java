/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.controller.sal.utils.ServiceHelper;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import org.opendaylight.snmp4sdn.FDBEntry;

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

//For md-sal RPC call
import org.opendaylight.controller.sal.common.util.Rpcs;
import java.util.Collections;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FdbServiceImpl implements FdbService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(FdbServiceImpl.class);

    public boolean isDummy = false;

    Controller controller = null;
    CLIHandler cli = null;
    private CmethUtil cmethUtil = null;

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;
        if(cmethUtil == null){
            logger.debug("ERROR: FdbServiceImpl: setController(): cmethUtil is null");
        }
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    public void init() {//this method would not be called, when Activator.java adopt "new FdbProvider()->new FdbServiceImpl()"
        logger.debug("FdbServiceImpl: init() is called");
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    private boolean checkNodeIpValid(long nodeId){
        if(cmethUtil == null){
            logger.debug("ERROR: FdbServiceImpl: checkNodeIpValid(): cmethUtil is null");
            return false;
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        if(sw_ipAddr == null){
            logger.debug("ERROR: checkNodeIpValid(): IP address of switch (nodeId: " + nodeId + ") not in DB");
            return false;
        }
        else
            return true;
    }

    //md-sal
    @Override
    public Future<RpcResult<DelFdbEntryOutput>> delFdbEntry(DelFdbEntryInput input){
        //check null input parameters
        if(input == null){
            logger.debug("ERROR: delFdbEntry(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        Long destMac = input.getDestMacAddr();
        if(nodeId == null){
            logger.debug("ERROR: delFdbEntry(): given nodeId is null");
            return null;
        }
        if(vlanId == null){
            logger.debug("ERROR: delFdbEntry(): given vlanId is null");
            return null;
        }
        if(destMac == null){
            logger.debug("ERROR: delFdbEntry(): given destMac is null");
            return null;
        }
        
        //parameters checking
        if(nodeId < 0){
            logger.debug("ERROR: delFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: delFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!isValidVlan(vlanId)){
            logger.debug("ERROR: delFdbEntry(): given invalid vlanId {}", vlanId);
            return null;
        }
        if(destMac < 0){
            logger.debug("ERROR: delFdbEntry(): given invalid destMac {}", destMac);
            return null;
        }

        //execute SNMPHandler.delFdbEntry()
        Status status = new SNMPHandler(cmethUtil).delFdbEntry(nodeId, vlanId, destMac);
        if(status == null){
            logger.debug("ERROR: delFdbEntry(): call SNMPHandler.delFdbEntry() with nodeId {} fail", nodeId);
            DelFdbEntryOutputBuilder ob = new DelFdbEntryOutputBuilder().setDelFdbEntryResult(Result.FAIL);
            RpcResult<DelFdbEntryOutput> rpcResult =
                    Rpcs.<DelFdbEntryOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            DelFdbEntryOutputBuilder ob = new DelFdbEntryOutputBuilder().setDelFdbEntryResult(Result.SUCCESS);
            RpcResult<DelFdbEntryOutput> rpcResult =
                    Rpcs.<DelFdbEntryOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: delFdbEntry(): call SNMPHandler.delFdbEntry() with nodeId {} fail", nodeId);
            DelFdbEntryOutputBuilder ob = new DelFdbEntryOutputBuilder().setDelFdbEntryResult(Result.FAIL);
            RpcResult<DelFdbEntryOutput> rpcResult =
                    Rpcs.<DelFdbEntryOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
    }
    
    //md-sal
    @Override
    public Future<RpcResult<GetFdbEntryOutput>> getFdbEntry(GetFdbEntryInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: getFdbEntry(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        Long destMac = input.getDestMacAddr();
        if(nodeId == null){
            logger.debug("ERROR: getFdbEntry(): given nodeId is null");
            return null;
        }
        if(vlanId == null){
            logger.debug("ERROR: getFdbEntry(): given vlanId is null");
            return null;
        }
        if(destMac == null){
            logger.debug("ERROR: getFdbEntry(): given destMac is null");
            return null;
        }
        
        //parameters checking
        if(nodeId < 0){
            logger.debug("ERROR: getFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: getFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!isValidVlan(vlanId)){
            logger.debug("ERROR: getFdbEntry(): given invalid vlanId {}", vlanId);
            return null;
        }
        if(destMac < 0){
            logger.debug("ERROR: getFdbEntry(): given invalid destMac {}", destMac);
            return null;
        }

        //execute SNMPHandler.readFdbTableEntry()
        FDBEntry entry = new SNMPHandler(cmethUtil).readFdbTableEntry(nodeId, vlanId, destMac);
        if(entry == null){
            logger.debug("ERROR: getFdbEntry(): call SNMPHandler.readFdbTableEntry() fails, nodeId {} vlanId {} destMac {}", nodeId, vlanId, destMac);
            return null;
        }
        if(entry.nodeId != nodeId || entry.vlanId != vlanId || entry.destMacAddr != destMac){
            logger.debug("ERROR: getFdbEntry(): call SNMPHandler.readFdbTableEntry() but returns inconsistent values (input: nodeId {} vlanId {} destMac {}, return: nodeId {} vlanId {} destMac {} port {})", nodeId, vlanId, destMac, entry.nodeId, entry.vlanId, entry.destMacAddr, entry.port);
            return null;
        }
        if(entry.port < 0){
            logger.debug("ERROR: getFdbEntry(): call SNMPHandler.readFdbTableEntry() with nodeId {} vlanId {} destMac {} fail", nodeId, vlanId, destMac);
            return null;
        }
        if(entry.type == null){
            logger.debug("ERROR: getFdbEntry(): call SNMPHandler.readFdbTableEntry(), nodeId {} vlanId {} destMac {}, but get null entry type", nodeId, vlanId, destMac);
            return null;
        }

        //convert entry type to FdbEntryType
        FdbEntryType type = null;
        if(entry.type == FDBEntry.EntryType.OTHER)
            type = FdbEntryType.OTHER;
        else if(entry.type == FDBEntry.EntryType.INVALID)
            type = FdbEntryType.INVALID;
        else if(entry.type == FDBEntry.EntryType.LEARNED)
            type = FdbEntryType.LEARNED;
        else if(entry.type == FDBEntry.EntryType.SELF)
            type = FdbEntryType.SELF;
        else if(entry.type == FDBEntry.EntryType.MGMT)
            type = FdbEntryType.MGMT;
        else{
            logger.debug("ERROR: getFdbEntry(): call SNMPHandler.readFdbTableEntry() with nodeId {} vlanId {} destMac {}, get port {} but invalid entry type {}", nodeId, vlanId, destMac, entry.port, entry.type);
        }

        //return the result
        GetFdbEntryOutputBuilder ob = new GetFdbEntryOutputBuilder().setNodeId(new Long(entry.nodeId)).setVlanId(new Integer(entry.vlanId)).setDestMacAddr(new Long(entry.destMacAddr)).setPort(new Short(entry.port)).setType(type);
        RpcResult<GetFdbEntryOutput> rpcResult =
                Rpcs.<GetFdbEntryOutput> getRpcResult(true, ob.build(),
                Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }
    
    //md-sal
    @Override
    public Future<RpcResult<GetFdbTableOutput>> getFdbTable(GetFdbTableInput input){
        
        //check null input parameters
        if(input == null){
            logger.debug("ERROR: getFdbTable(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        if(nodeId == null){
            logger.debug("ERROR: getFdbTable(): given nodeId is null");
            return null;
        }
        
        //parameters checking
        if(nodeId < 0){
            logger.debug("ERROR: getFdbTable(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: getFdbTable(): given invalid nodeId {}", nodeId);
            return null;
        }

        //execute SNMPHandler.readAllFdbTableEntry()
        List<FDBEntry> table = new SNMPHandler(cmethUtil).readAllFdbTableEntry(nodeId);
        if(table == null){
            logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry() fails, nodeId {}", nodeId);
            return null;
        }

        //prepare the fdb table to return
        List<FdbTableEntry> retTable = new ArrayList<FdbTableEntry>();
        for(FDBEntry entry:table){
            //check parameters
            if(entry == null){
                logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry() fails, nodeId {}, gets a null entry", nodeId);
                return null;
            }
            if(entry.nodeId != nodeId){
                logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry() gets inconsistent nodeId (given nodeId {}, gets nodeId {} vlanId {} destMac {} port{})", nodeId, entry.nodeId, entry.vlanId, entry.destMacAddr, entry.port);
                return null;
            }
            if(entry.port < 0){
                logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry() with nodeId {} vlanId {} destMac {} but invalid port {}", entry.nodeId, entry.vlanId, entry.destMacAddr, entry.port);
                return null;
            }
            if(entry.type == null){
                logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry(), get nodeId {} vlanId {} destMac {} but null entry type", entry.nodeId, entry.vlanId, entry.destMacAddr);
                return null;
            }

            //convert entry type to FdbEntryType
            FdbEntryType type = null;
            if(entry.type == FDBEntry.EntryType.OTHER)
                type = FdbEntryType.OTHER;
            else if(entry.type == FDBEntry.EntryType.INVALID)
                type = FdbEntryType.INVALID;
            else if(entry.type == FDBEntry.EntryType.LEARNED)
                type = FdbEntryType.LEARNED;
            else if(entry.type == FDBEntry.EntryType.SELF)
                type = FdbEntryType.SELF;
            else if(entry.type == FDBEntry.EntryType.MGMT)
                type = FdbEntryType.MGMT;
            else{
                logger.debug("ERROR: getFdbTable(): call SNMPHandler.readAllFdbTableEntry(), get nodeId {} vlanId {} destMac {} but invalid entry type", entry.nodeId, entry.vlanId, entry.destMacAddr, entry.type);
                return null;
            }
        
            FdbTableEntryBuilder entryBuilder = new FdbTableEntryBuilder().setNodeId(entry.nodeId).setVlanId(entry.vlanId).setDestMacAddr(entry.destMacAddr).setPort(entry.port).setType(type);
            FdbTableEntry retEntry = entryBuilder.build();
            retTable.add(retEntry);
        }
        
        //return the result
        GetFdbTableOutputBuilder ob = new GetFdbTableOutputBuilder().setFdbTableEntry(retTable);
        RpcResult<GetFdbTableOutput> rpcResult =
                Rpcs.<GetFdbTableOutput> getRpcResult(true, ob.build(),
                Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }
    
    //md-sal
    //input: SetFdbEntryInput, which including nodeId, vlanId, destMac, port
    //TODO: Currently the fdb entry's 'type' field is ignored, since it must be 'static'. Should we check it? (if need to check, it should be null or?)
    @Override
    public Future<RpcResult<SetFdbEntryOutput>> setFdbEntry(SetFdbEntryInput input){
        //check null input parameters
        if(input == null){
            logger.debug("ERROR: setFdbEntry(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        Integer vlanId = input.getVlanId();
        Long destMac = input.getDestMacAddr();
        Short port = input.getPort();
        if(nodeId == null){
            logger.debug("ERROR: setFdbEntry(): given nodeId is null");
            return null;
        }
        if(vlanId == null){
            logger.debug("ERROR: setFdbEntry(): given vlanId is null");
            return null;
        }
        if(destMac == null){
            logger.debug("ERROR: setFdbEntry(): given destMac is null");
            return null;
        }
        if(port == null){
            logger.debug("ERROR: setFdbEntry(): given port is null");
            return null;
        }
        
        //parameters checking
        if(nodeId < 0){
            logger.debug("ERROR: setFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: setFdbEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!isValidVlan(vlanId)){
            logger.debug("ERROR: setFdbEntry(): given invalid vlanId {}", vlanId);
            return null;
        }
        if(destMac < 0){
            logger.debug("ERROR: setFdbEntry(): given invalid destMac {}", destMac);
            return null;
        }
        if(port < 0){
            logger.debug("ERROR: setFdbEntry(): given invalid port {}", port);
            return null;
        }

        //execute SNMPHandler.setFdbEntry()
        Status status = new SNMPHandler(cmethUtil).setFdbEntry(nodeId, vlanId, destMac, port);
        if(status == null){
            logger.debug("ERROR: setFdbEntry(): call SNMPHandler.setFdbEntry() with nodeId {} fail", nodeId);
            SetFdbEntryOutputBuilder ob = new SetFdbEntryOutputBuilder().setSetFdbEntryResult(Result.FAIL);
            RpcResult<SetFdbEntryOutput> rpcResult =
                    Rpcs.<SetFdbEntryOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            SetFdbEntryOutputBuilder ob = new SetFdbEntryOutputBuilder().setSetFdbEntryResult(Result.SUCCESS);
            RpcResult<SetFdbEntryOutput> rpcResult =
                    Rpcs.<SetFdbEntryOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: setFdbEntry(): call SNMPHandler.setFdbEntry() with nodeId {} fail", nodeId);
            SetFdbEntryOutputBuilder ob = new SetFdbEntryOutputBuilder().setSetFdbEntryResult(Result.FAIL);
            RpcResult<SetFdbEntryOutput> rpcResult =
                    Rpcs.<SetFdbEntryOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
    }

    private boolean isValidVlan(Integer vlanId){
        if(vlanId < 1 || vlanId > 4095)//TODO: valid vlan range?
            return false;
        else
            return true;
    }


    /*
    * CLI as following
    */

    //CLI: s4sFDB
    public void _s4sFDB(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: s4sFDB [getEntry <switch> <vlan_id> <dest_mac_addr> | deleteEntry <switch> <vlan_id> <dest_mac_addr> | ");
            ci.println("\t\t  setEntry <switch> <vlan_id> <dest_mac_addr> <port>| getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getEntry") == 0){
            ci.println();
            _s4sGetFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("deleteEntry") == 0){
            ci.println();
            _s4sDeleteFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setEntry") == 0){
            ci.println();
            _s4sSetFDBEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getTable") == 0){
            ci.println();
            _s4sGetFDBTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: s4sFDB [getEntry <switch> <vlan_id> <dest_mac_addr> | deleteEntry <switch> <vlan_id> <dest_mac_addr> | ");
            ci.println("\t\t  setEntry <switch> <vlan_id> <dest_mac_addr> <port>| getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: s4sFDB getEntry <switch> <vlan_id> <dest_mac_addr>
    public void _s4sGetFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null  || garbage != null){
            ci.println();
            ci.println("Please use: s4sFDB getEntry <switch> <vlan_id> <dest_mac_addr>");
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

        //prepare parameters to get fdb entry
        GetFdbEntryInputBuilder ib = new GetFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(destMac);

        //execute getFdbEntry(), and check return null parameters?
        FdbEntry entry;
        String destMacAddr = HexString.toHexString(destMac).toUpperCase();
        try {
            Future<RpcResult<GetFdbEntryOutput>> ret = this.getFdbEntry(ib.build());
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

    //CLI: s4sFDB deleteEntry <switch> <vlan_id> <dest_mac_addr>
    public void _s4sDeleteFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sFDB deleteEntry <switch> <vlan_id> <dest_mac_addr>");
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

        //prepare parameters to set fdb entry
        DelFdbEntryInputBuilder ib = new DelFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(new Long(destMac));

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<DelFdbEntryOutput> rpcResult;
        try {
            Future<RpcResult<DelFdbEntryOutput>> ret = this.delFdbEntry(ib.build());
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

    //CLI: s4sFDB setEntry <switch> <vlan_id> <dest_mac_addr> <port>
    public void _s4sSetFDBEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String arg5 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sFDB setEntry <switch> <vlan_id> <dest_mac_addr> <port>");
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


        //prepare parameters to set fdb entry
        SetFdbEntryInputBuilder ib = new SetFdbEntryInputBuilder().setNodeId(new Long(nodeId)).setVlanId(new Integer(vlanId)).setDestMacAddr(new Long(destMac)).setPort(new Short(port));

        //execute getFdbEntry(), and check return null parameters?
        RpcResult<SetFdbEntryOutput> rpcResult;
        try {
            Future<RpcResult<SetFdbEntryOutput>> ret = this.setFdbEntry(ib.build());
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

    //CLI: s4sFDB getTable <switch>
    public void _s4sGetFDBTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sFDB getTable <switch>");
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

        //prepare parameters to get fdb entry
        GetFdbTableInputBuilder ib = new GetFdbTableInputBuilder().setNodeId(new Long(nodeId));

        //execute getFdbEntry(), and check return null parameters?
        List<FdbTableEntry> fdbTable;
        try {
            Future<RpcResult<GetFdbTableOutput>> ret = this.getFdbTable(ib.build());
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
        return new String("ConfigServiceImpl.getHelp():null");
    }

}
