/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.configmanager.impl;

import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareConsumer;

import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ConfigService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.StpPortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.get.arp.table.output.ArpTableEntry;

import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableOutput;

import org.opendaylight.yangtools.yang.common.RpcResult;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManagerImpl extends AbstractBindingAwareConsumer implements
        BundleActivator, BindingAwareConsumer, CommandProvider {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManagerImpl.class);

    private ConfigService config;
    private ConsumerContext session;

    @Override
    public void onSessionInitialized(ConsumerContext session) {
        this.session = session;
        registerWithOSGIConsole();
        logger.debug("ConfigManagerImpl: onSessionInitialized() completed");
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

    public boolean disableStp(Long nodeId){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: disableStp(): ConfigService is null, nodeId = {}", nodeId);
                return false;
            }
        }

        DisableStpInputBuilder ib = new DisableStpInputBuilder();
        ib.setNodeId(nodeId);
        try {
            RpcResult<DisableStpOutput> result = config.disableStp(ib.build()).get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: disableStp(): call ConfigService.disableStp() fail (null result), nodeId = {}", nodeId);
                return false;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: disableStp(): call ConfigService.disableStp() fail (null in result), nodeId = {}", nodeId);
                return false;
            }
            switch (result.getResult().getDisableStpResult()) {
            case SUCCESS:
                return true;
            //TODO: other cases
            default:
                return false;
            }
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: disableStp(): nodeId = {}, InterruptedException: {}", nodeId, ie);
            return false;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: disableStp(): nodeId = {}, ExecutionException: {}", nodeId, ee);
            return false;
        }

    }

    public boolean enableStp(Long nodeId){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: enableStp(): ConfigService is null, nodeId = {}", nodeId);
                return false;
            }
        }

        EnableStpInputBuilder ib = new EnableStpInputBuilder();
        ib.setNodeId(nodeId);
        try {
            RpcResult<EnableStpOutput> result = config.enableStp(ib.build()).get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: enableStp(): call ConfigService.enableStp() fail (null result), nodeId = {}", nodeId);
                return false;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: enableStp(): call ConfigService.ensableStp() fail (null in result), nodeId = {}", nodeId);
                return false;
            }
            switch (result.getResult().getEnableStpResult()) {
            case SUCCESS:
                return true;
            //TODO: other cases
            default:
                return false;
            }
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: enableStp(): nodeId = {}, InterruptedException: {}", nodeId, ie);
            return false;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: enableStp(): nodeId = {}, ExecutionException: {}", nodeId, ee);
            return false;
        }

    }

    public boolean setStpPortState(Long nodeId, Short port, Boolean isEnable){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: setStpPortState(): ConfigService is null, nodeId = {}", nodeId);
                return false;
            }
        }

        SetStpPortStateInputBuilder ib = new SetStpPortStateInputBuilder();
        ib.setNodeId(nodeId);
        ib.setPort(port);
        ib.setEnable(isEnable);
        try {
            RpcResult<SetStpPortStateOutput> result = config.setStpPortState(ib.build()).get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: setStpPortState(): call ConfigService.setStpPortState() fail (null result), nodeId {} port {} isEnable {}", nodeId, port, isEnable);
                return false;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: setStpPortState(): call ConfigService.setStpPortState() fail (null in result), nodeId = {}", nodeId);
                return false;
            }
            switch (result.getResult().getSetStpPortStateResult()) {
            case SUCCESS:
                return true;
            //TODO: other cases
            default:
                return false;
            }
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: setStpPortState(): nodeId = {}, InterruptedException: {}", nodeId, ie);
            return false;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: setStpPortState(): nodeId = {}, ExecutionException: {}", nodeId, ee);
            return false;
        }

    }

    public StpPortState getStpPortState(Long nodeId, Short port){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: getStpPortState(): ConfigService is null, nodeId = {}", nodeId);
                return null;
            }
        }

        //prepare parameters to ConfigService
        GetStpPortStateInputBuilder ib = new GetStpPortStateInputBuilder();
        ib.setNodeId(nodeId);
        ib.setPort(port);

        //execute getArpEntry(), and check return null parameters?
        try {
            Future<RpcResult<GetStpPortStateOutput>> ret = config.getStpPortState(ib.build());
            if(ret == null){
                logger.debug("ERROR: ConfigManager: getStpPortState(): call ConfigService.getStpPortState() fail (return null), nodeId {} port {}", nodeId, port);
                return null;
            }
            RpcResult<GetStpPortStateOutput> result = ret.get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: getStpPortState(): call ConfigService.getStpPortState() fail (null result), nodeId {} port {}", nodeId, port);
                return null;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: getStpPortState(): call ConfigService.getStpPortState() fail (null in result), nodeId = {}", nodeId);
                return null;
            }
            StpPortState state = result.getResult().getStpPortState();
            if(state == null){
                logger.debug("ERROR: ConfigManager: getStpPortState(): call ConfigService.getStpPortState() with nodeId {} port {}, return null", nodeId, port);
                return null;
            }
            return state;
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: getStpPortState(): nodeId = {}, InterruptedException: {}", nodeId, ie);
            return null;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: getStpPortState(): nodeId = {}, ExecutionException: {}", nodeId, ee);
            return null;
        }

    }

    public ArpEntry getArpEntry(Long nodeId, String ipAddress){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: getArpEntry(): ConfigService is null, nodeId = {}", nodeId);
                return null;
            }
        }

        //prepare parameters to ConfigService
        GetArpEntryInputBuilder ib = new GetArpEntryInputBuilder();
        ib.setNodeId(nodeId);
        ib.setIpAddress(ipAddress);

        //execute getArpEntry(), and check return null parameters?
        try {
            Future<RpcResult<GetArpEntryOutput>> ret = config.getArpEntry(ib.build());
            if(ret == null){
                logger.debug("ERROR: ConfigManager: getArpEntry(): call ConfigService.getArpEntry() fail (return null), nodeId {} ipAddress {}", nodeId, ipAddress);
                return null;
            }
            RpcResult<GetArpEntryOutput> result = ret.get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: getArpEntry(): call ConfigService.getArpEntry() fail (null result), nodeId {} ipAddress {}", nodeId, ipAddress);
                return null;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: getArpEntry(): call ConfigService.getArpEntry() fail (null in result), nodeId {} ipAddress {}", nodeId, ipAddress);
                return null;
            }
            ArpEntry entry = result.getResult();
            if(entry == null){
                logger.debug("ERROR: ConfigManager: getArpEntry(): call ConfigService.getArpEntry() with , nodeId {} ipAddress {}, return null", nodeId, ipAddress);
                return null;
            }
            return entry;
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: getArpEntry(): nodeId {} ipAddress {}, InterruptedException: {}", nodeId, ipAddress, ie);
            return null;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: getArpEntry(): nodeId {} ipAddress {}, ExecutionException: {}", nodeId, ipAddress, ee);
            return null;
        }

    }

    public List<ArpTableEntry> getArpTable(Long nodeId){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: getArpTable(): ConfigService is null, nodeId = {}", nodeId);
                return null;
            }
        }

        //prepare parameters to ConfigService
        GetArpTableInputBuilder ib = new GetArpTableInputBuilder();
        ib.setNodeId(nodeId);

        //execute getArpTable(), and check return null parameters?
        try {System.out.println("xxxxx");
            Future<RpcResult<GetArpTableOutput>> ret = config.getArpTable(ib.build());System.out.println("yyyyy");
            RpcResult<GetArpTableOutput> result = ret.get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: getArpTable(): call ConfigService.getArpTable() fail (null result), nodeId {}", nodeId);
                return null;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: getArpTable(): call ConfigService.getArpTable() fail (null in result), nodeId {}", nodeId);
                return null;
            }
            List<ArpTableEntry> entryList = result.getResult().getArpTableEntry();
            if(entryList == null){
                logger.debug("ERROR: ConfigManager: getArpTable(): call ConfigService.getArpTable() with nodeId {}, fail", nodeId);
                return null;
            }
            return entryList;
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: getArpTable(): call ConfigService.getArpTable() with nodeId {}, InterruptedException: {}", nodeId, ie);
            return null;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: getArpTable(): call ConfigService.getArpTable() with nodeId {}, ExecutionException: {}", nodeId, ee);
            return null;
        }

    }

    public boolean deleteArpEntry(Long nodeId, String ipAddress){

        //check ConfigService exists?
        if (config == null) {
            config = this.session.getRpcService(ConfigService.class);
            if (config == null) {
                logger.debug("ERROR: ConfigManager: deleteArpEntry(): ConfigService is null, nodeId = {}", nodeId);
                return false;
            }
        }

        DeleteArpEntryInputBuilder ib = new DeleteArpEntryInputBuilder();
        ib.setNodeId(nodeId);
        ib.setIpAddress(ipAddress);
        try {
            RpcResult<DeleteArpEntryOutput> result = config.deleteArpEntry(ib.build()).get();
            if(result == null){
                logger.debug("ERROR: ConfigManager: deleteArpEntry(): call ConfigService.deleteArpEntry() fail (null result), nodeId {} and ipAddress {}", nodeId, ipAddress);
                return false;
            }
            if(result.getResult() == null){
                logger.debug("ERROR: ConfigManager: deleteArpEntry(): call ConfigService.deleteArpEntry() fail (null in result), nodeId = {}", nodeId);
                return false;
            }
            switch (result.getResult().getDeleteArpEntryResult()) {
            case SUCCESS:
                return true;
            //TODO: other cases
            default:
                return false;
            }
        } catch (InterruptedException ie) {
            logger.debug("ERROR: ConfigManager: deleteArpEntry(): nodeId = {}, InterruptedException: {}", nodeId, ie);
            return false;
        } catch (ExecutionException ee) {
            logger.debug("ERROR: ConfigManager: deleteArpEntry(): nodeId = {}, ExecutionException: {}", nodeId, ee);
            return false;
        }

    }


    //CLI: cfgSTP
    public void _cfgSTP(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: cfgSTP [getPortState <switch> <port> | setPortState <switch> <port> <enable(Y/N)> | ");
            ci.println("\t\t  disableSTP <switch> | enableSTP <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getPortState") == 0){
            ci.println();
            _cfgGetSTPPortState(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setPortState") == 0){
            ci.println();
            _cfgSetStpPortState(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("disableSTP") == 0){
            ci.println();
            _cfgDisableSTP(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("enableSTP") == 0){
            ci.println();
            _cfgEnableSTP(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: cfgSTP [getPortState <switch> <port> | setPortState <switch> <port> <enable(Y/N)> | ");
            ci.println("\t\t  disableSTP <switch> | enableSTP <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    /*public void _cfgmgr(CommandInterpreter ci){
        long nodeId = 158969157063648L;
        boolean result = disableStp(nodeId);
        if(result){
            System.out.println("Succeessful to disable STP of node " + nodeId);
        }
        else{
            System.out.println("Fail to disable STP of node " + nodeId);
        }
    }*/

    //CLI: cfgSTP getPortState <switch> <port>
    public void _cfgGetSTPPortState(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgSTP getPortState <switch> <port>");
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

        //parse arg3: String port to int value vlanId
        short portNum = -1;
        Short.parseShort(arg3);
        try{
            portNum = Short.parseShort(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to short value error: " + e1);
            return;
        }

        StpPortState state = getStpPortState(nodeId, portNum);
        if(state == null){
            ci.println();
            ci.println("Fail to get STP port state of node " + nodeId + " port " + portNum);
            ci.println();
        }
        else{
            ci.println();
            ci.println("STP port state of node " + nodeId + " port " + portNum + ": " + state);
            ci.println();
        }
    }

    //CLI: cfgSTP setPortState <switch> <port> <enable(Y/N)>
    public void _cfgSetStpPortState(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgSTP setPortState <switch> <port> <enable(Y/N)>");
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

        //parse arg3: String port to int value vlanId
        short portNum = -1;
        Short.parseShort(arg3);
        try{
            portNum = Short.parseShort(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to short value error: " + e1);
            return;
        }

        //parse arg4: enable port to run STP or not
        boolean isEnable;
        if(arg4.compareToIgnoreCase("Y") == 0)
            isEnable = true;
        else if(arg4.compareToIgnoreCase("N") == 0)
            isEnable = false;
        else{
            ci.println();
            ci.println("Please use: cfgSTP setPortState <switch> <port> <enable(Y/N)>");
            return;
        }

        boolean result = setStpPortState(nodeId, portNum, isEnable);
        if(result){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            ci.println();
            ci.println("Successfully set node " + nodeId + " port " + portNum + " of STP state as " + isEnable);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to set STP state of node " + nodeId + " port " + portNum);
            ci.println();
        }
    }

    //CLI: cfgSTP disableSTP <switch>
    public void _cfgDisableSTP(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgSTP disableSTP <switch>");
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

        boolean result = disableStp(nodeId);
        if(result){
            ci.println();
            ci.println("Successfully to disable STP of node " + nodeId);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to disable STP of node " + nodeId);
            ci.println();
        }
    }

    //CLI: cfgSTP enableSTP <switch>
    public void _cfgEnableSTP(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgSTP disableSTP <switch>");
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

        boolean result = enableStp(nodeId);
        if(result){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            ci.println();
            ci.println("Successfully to enable STP of node " + nodeId);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to enable STP of node " + nodeId);
            ci.println();
        }
    }

    //CLI: cfgSTP
    public void _cfgARP(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: cfgARP [getEntry <switch> <ip_address> | deleteEntry <switch> <ip_address> | ");
            ci.println("\t\t  setEntry <switch> <ip_address> <mac_address> | getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getEntry") == 0){
            ci.println();
            _cfgGetARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("deleteEntry") == 0){
            ci.println();
            _cfgDeleteARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setEntry") == 0){
            ci.println();
            ci.println("cfgARP setEntry: currently unavailable");
            //_cfgSetARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getTable") == 0){
            ci.println();
            _cfgGetARPTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: cfgARP [getEntry <switch> <ip_address> | deleteEntry <switch> <ip_address> | ");
            ci.println("\t\t  setEntry <switch> <ip_address> <mac_address> | getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: cfgARP getEntry <switch> <ip_address>
    public void _cfgGetARPEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgARP getEntry <switch> <ip_address>");
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

        String ipAddress = new String(arg3);
        ArpEntry entry = getArpEntry(nodeId, ipAddress);
        if(entry == null){
            ci.println();
            ci.println("Fail to get ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
        else{
            ci.println();
            ci.println("ARP entry on node " + nodeId + " for IP " + ipAddress + ": <IP " + entry.getIpAddress() + ", MAC " + HexString.toHexString(entry.getMacAddress()).toUpperCase() + ">");
            ci.println();
        }
    }

    //CLI: cfgARP deleteEntry <switch> <ip_address>
    public void _cfgDeleteARPEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgARP deleteEntry <switch> <ip_address>");
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

        //arg3
        String ipAddress = new String(arg3);

        boolean result = deleteArpEntry(nodeId, ipAddress);
        if(!result){
            ci.println();
            ci.println("Fail to delete ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Successfully delete ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
    }

    //CLI: cfgARP getTable <switch>
    public void _cfgGetARPTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: cfgARP getTable <switch>");
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

        List<ArpTableEntry> arpTable = getArpTable(nodeId);
        if(arpTable == null){
            ci.println();
            ci.println("Fail to get ARP Table on node " + nodeId);
            ci.println();
            return;
        }
        ci.println();
        ci.println("======== ARP Table of Node " + nodeId + " =========");
        ci.println("\tIP\t\t\tMAC");
        for(int i = 0; i < arpTable.size(); i++){
            ArpTableEntry entry = arpTable.get(i);
            ci.println(entry.getIpAddress() + "\t\t" + HexString.toHexString(entry.getMacAddress()).toUpperCase());
        }
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("ConfigServiceImpl.getHelp():null");
    }
}

