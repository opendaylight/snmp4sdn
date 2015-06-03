/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.aclmanager.impl;

import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.sal.binding.api.AbstractBindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareConsumer;

//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.*;

import org.opendaylight.yangtools.yang.common.RpcResult;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AclManagerImpl extends AbstractBindingAwareConsumer implements
        BundleActivator, BindingAwareConsumer, CommandProvider {

    private static final Logger logger = LoggerFactory.getLogger(AclManagerImpl.class);

    private AclService acl;
    private ConsumerContext session;

    @Override
    public void onSessionInitialized(ConsumerContext session) {
        this.session = session;
        registerWithOSGIConsole();
        logger.debug("AclManagerImpl: onSessionInitialized() completed");
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

    //CLI: aclMgr
    public void _aclMgr(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use:");
            ci.println("aclMgr [");
            ci.println("  createProfile <switch> <...>");
            ci.println("| setRule <switch> <...>");
            ci.println("| delProfile <switch> <...>");
            ci.println("| delRule <switch> <...>");
            ci.println("| clearTable <switch> <...>");
            ci.println("(<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("createProfile") == 0){
            ci.println();
            _aclMgrCreateACLProfile(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setRule") == 0){
            ci.println();
            _aclMgrAddACLRule(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delProfile") == 0){
            ci.println();
            _aclMgrDelACLProfile(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delRule") == 0){
            ci.println();
            _aclMgrDelACLRule(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("clearTable") == 0){
            ci.println();
            _aclMgrClearACLTable(ci);
            ci.println();
        }else{
            ci.println();
            ci.println("Please use:");
            ci.println("aclMgr [");
            ci.println("  createProfile <switch> <...>");
            ci.println("| setRule <switch> <...>");
            ci.println("| delProfile <switch> <...>");
            ci.println("| delRule <switch> <...>");
            ci.println("| clearTable <switch> <...>");
            ci.println("(<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: aclMgr setRule <switch> 
    //                                  {[profileId <profile_id>] | [profileName <profile_name>]}
    //                                  {[ruleId <rule_id>] | [ruleName <rule_name>]}
    //                                   ports <portList(seperate_by_comma)>
    //                                   layer <layer('ethernet'or'ip')>
    //                                   {[vlanId <vlan_id>] [srcIp <src_ip>] [dsIp <dst_ip>]}
    //                                   action <action('permit'or'deny')>
    public void _aclMgrAddACLRule(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String arg5 = ci.nextArgument();//'ruleId' or 'ruleName'
        String arg6 = ci.nextArgument();//value of ruleId or ruleName
        String arg7 = ci.nextArgument();//'portList'
        String arg7v = ci.nextArgument();//value of portList
        String arg8 = ci.nextArgument();//'layer'
        String arg8v = ci.nextArgument();//value of layer

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || arg7 == null || arg7v == null
            || arg8 == null || arg8v == null){
            printCiSetAclRuleUsage(ci);
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

        //parse arg3: 'profileId' or 'profileName'
        int profileId = -1;
        if(arg3.compareToIgnoreCase("profileId") == 0){
            //parse arg4: String profile_id to int value
            try{
                profileId = Integer.parseInt(arg4);
            }catch(NumberFormatException e1){
                ci.println("Error: convert argument <profile_id> " + arg4 + " to int value error: " + e1);
                return;
            }
        }
        String profileName = null;
        if(arg3.compareToIgnoreCase("profileName") == 0){
            //arg4: profileName
            profileName = new String(arg4);
        }

        //parse arg5: 'ruleId' or 'ruleName'
        int ruleId = -1;
        if(arg5.compareToIgnoreCase("ruleId") == 0){
            //parse arg6: String rule_id to int value
            try{
                ruleId = Integer.parseInt(arg6);
            }catch(NumberFormatException e1){
                ci.println("Error: convert argument <rule_id> " + arg6 + " to int value error: " + e1);
                return;
            }
        }
        String ruleName = null;
        if(arg5.compareToIgnoreCase("ruleName") == 0){
            //arg6: ruleName
            ruleName = new String(arg6);
        }

        //arg7
        if(arg7.compareToIgnoreCase("ports") != 0){
            printCiSetAclRuleUsage(ci);
            return;
        }

        //parse arg7v: <port_list> to List<Short>
        String portList = new String(arg7v);
        List<Short> ports = convertPortListString2ShortList(arg7v);
        if(ports == null){
            ci.println("Error: the given port list \"" + arg7v + "\", convert to int array fail");
            return;
        }
        String portListChk = "";//convert the ports int array to String, later can print for check correctness
        for(Short port : ports)
            portListChk += port + ",";

        //arg8
        if(arg8.compareToIgnoreCase("layer") != 0){
            printCiSetAclRuleUsage(ci);
            return;
        }

        //parse arg8v: <layer> to AclLayer value
        AclLayer layer = null;
        if(arg8v.compareToIgnoreCase("ethernet") == 0)
            layer = AclLayer.ETHERNET;
        else if(arg8v.compareToIgnoreCase("ip") == 0)
            layer = AclLayer.IP;
        else{
            ci.println();
            ci.println("ERROR: Layer of '" + arg8v + "' is not supported");
            printCiSetAclRuleUsage(ci);
            return;
        }

        //parse args of vlanId / srcIp / dstIp / action
        Integer vlanId = null;
        String srcIp = null;
        String dstIp = null;
        AclAction action = null;
        while(true){
            String arg = ci.nextArgument();
            if(arg == null)
                break;

            if(arg.compareToIgnoreCase("vlanId") == 0){
                String vlanIdValue = ci.nextArgument();
                try{
                    vlanId = Integer.parseInt(vlanIdValue);
                }catch(NumberFormatException e1){
                    ci.println("Error: convert argument " + vlanIdValue + " to int value error: " + e1);
                    return;
                }
            }
            else if(arg.compareToIgnoreCase("srcIp") == 0){
                srcIp = new String(ci.nextArgument());
            }
            else if(arg.compareToIgnoreCase("dstIp") == 0){
                dstIp = new String(ci.nextArgument());
            }
            else if(arg.compareToIgnoreCase("action") == 0){
                String actionValue = ci.nextArgument();
                if(actionValue.compareToIgnoreCase("permit") == 0)
                    action = AclAction.PERMIT;
                if(actionValue.compareToIgnoreCase("deny") == 0)
                    action = AclAction.DENY;
            }
        }


        //check AclService exists?
        if (acl == null) {
            acl = this.session.getRpcService(AclService.class);
            if (acl == null) {
                logger.debug("Can't get AclService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set ACL rule
        SetAclRuleInputBuilder ib = new SetAclRuleInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName)
                                                        .setRuleId(ruleId).setRuleName(ruleName)
                                                        .setPortList(ports)
                                                        .setAclLayer(layer)
                                                        .setVlanId(vlanId)
                                                        .setSrcIp(srcIp).setDstIp(dstIp)
                                                        .setAclAction(action);

        //execute setAclRule(), and check return null parameters?
        RpcResult<SetAclRuleOutput> rpcResult;
        try {
            Future<RpcResult<SetAclRuleOutput>> ret = acl.setAclRule(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set ACL Rule on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set ACL Rule on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set ACL Rule on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getSetAclRuleResult() == null){
                ci.println();
                ci.println("Fail to set ACL Rule on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call setAclRule() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call setAclRule() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result setAclRuleResult = rpcResult.getResult().getSetAclRuleResult();
        switch (setAclRuleResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully set ACL Rule on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to set ACL Rule on node " + nodeId);
                ci.println();
            }
        }

    }

    //CLI: aclMgr delRule <switch> {[profileId <profile_id>] | [profileName <profile_name>]} ruleId <rule_id>
    public void _aclMgrDelACLRule(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String arg5 = ci.nextArgument();//'ruleId'
        String arg6 = ci.nextArgument();//value of ruleId
        String garbage = ci.nextArgument();
        //TODO: the arg numbering above skip 5, because the code reuse _aclMgrAddACLRule()'s code. For convenience of future modification, I leave the same numbering here

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || garbage != null){
            ci.println();
            ci.println("Please use: aclMgr delRule <switch> {[profileId <profile_id>] | [profileName <profile_name>]} ruleId <rule_id>");
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

        //parse arg3: 'profileId' or 'profileName'
        int profileId = -1;
        if(arg3.compareToIgnoreCase("profileId") == 0){
            //parse arg4: String profile_id to int value
            try{
                profileId = Integer.parseInt(arg4);
            }catch(NumberFormatException e1){
                ci.println("Error: convert argument <profile_id> " + arg4 + " to int value error: " + e1);
                return;
            }
        }
        String profileName = null;
        if(arg3.compareToIgnoreCase("profileName") == 0){
            //arg4: profileName
            profileName = new String(arg4);
        }

        //parse arg5: 'ruleId' or 'ruleName'
        int ruleId = -1;
        if(arg5.compareToIgnoreCase("ruleId") == 0){
            //parse arg6: String rule_id to int value
            try{
                ruleId = Integer.parseInt(arg6);
            }catch(NumberFormatException e1){
                ci.println("Error: convert argument <rule_id> " + arg6 + " to int value error: " + e1);
                return;
            }
        }


        //check AclService exists?
        if (acl == null) {
            acl = this.session.getRpcService(AclService.class);
            if (acl == null) {
                logger.debug("Can't get AclService, can't proceed!");
                return;
            }
        }

        //prepare parameters to delete ACL rule
        DelAclRuleInputBuilder ib = new DelAclRuleInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName)
                                                        .setRuleId(ruleId);

        //execute delAclRule(), and check return null parameters?
        RpcResult<DelAclRuleOutput> rpcResult;
        try {
            Future<RpcResult<DelAclRuleOutput>> ret = acl.delAclRule(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to delete ACL Rule on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to delete ACL Rule on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to delete ACL Rule on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getDelAclRuleResult() == null){
                ci.println();
                ci.println("Fail to delete ACL Rule on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call delAclRule() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call delAclRule() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result delAclRuleResult = rpcResult.getResult().getDelAclRuleResult();
        switch (delAclRuleResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully delete ACL Rule on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to delete ACL Rule on node " + nodeId);
                ci.println();
            }
        }

    }

    //CLI: aclMgr createProfile <switch> <profile_id> <profile_name>
    //                                      {   [<layer('ethernet')> <vlan_mask>]
    //                                        | [<layer('ip')> {[vlanMask <vlan_mask>] [srcIpMask <src_ip_mask>] [dstIpMask <dst_ip_mask>]}
    public void _aclMgrCreateACLProfile(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//profileId
        String arg4 = ci.nextArgument();//profileName
        String arg8 = ci.nextArgument();//layer

        if(arg2 == null || arg3 == null || arg4 == null
            || arg8 == null){
            printCiAddAclProfileUsage(ci);
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

        //parse arg3: 'profileId' or 'profileName'
        int profileId = -1;
        try{
                profileId = Integer.parseInt(arg3);
        }catch(NumberFormatException e1){
                ci.println("Error: convert argument <profile_id> " + arg4 + " to int value error: " + e1);
                return;
        }

        //arg4: profileName
        String profileName = new String(arg4);

        //prase arg8: <layer> to AclLayer value
        AclLayer layer = null;
        if(arg8.compareToIgnoreCase("ethernet") == 0){
            layer = AclLayer.ETHERNET;
            //other arguments are dealt with later
        }
        else if(arg8.compareToIgnoreCase("ip") == 0){
            layer = AclLayer.IP;
            //other arguments are dealt with later
        }

        Short vlanMask = null;

        //parser for Ethernet layer
        //and parse <vlan_mask>
        if(layer == AclLayer.ETHERNET){
            //parse "vlanMask <vlan_mask>"
            String vlanMaskStr = ci.nextArgument();
            String maskValue = ci.nextArgument();
            if(vlanMaskStr.compareToIgnoreCase("vlanMask") != 0){
                printCiAddAclProfileUsage(ci);
                return;
            }
            if(maskValue == null){
                printCiAddAclProfileUsage(ci);
                return;
            }
            vlanMask = parseMaskStringToShort(maskValue);

            //check CLI command string should end now
            if(ci.nextArgument() != null){
                printCiAddAclProfileUsage(ci);
                return;
            }
        }

        //parser for IP layer
        //and parse vlanMask / srcIpMask / dstIpMask
        String srcIpMask = null;
        String dstIpMask = null;
        if(layer == AclLayer.IP){
            while(true){
                String maskStr = ci.nextArgument();
                String valueStr = ci.nextArgument();
                if(maskStr == null) break;
                if(maskStr != null && valueStr == null){
                    printCiAddAclProfileUsage(ci);
                    return;
                }

                if(maskStr.compareToIgnoreCase("vlanMask") == 0){
                    //arg: vlanMask
                    vlanMask = parseMaskStringToShort(valueStr);
                }
                else if(maskStr.compareToIgnoreCase("srcIpMask") == 0){
                    //arg: srcIpMask
                    srcIpMask = new String(valueStr);
                }
                else if(maskStr.compareToIgnoreCase("dstIpMask") == 0){
                    //arg: dskIpMask
                    dstIpMask = new String(valueStr);
                }
            }
        }


        //check AclService exists?
        if (acl == null) {
            acl = this.session.getRpcService(AclService.class);
            if (acl == null) {
                logger.debug("Can't get AclService, can't proceed!");
                return;
            }
        }

        //prepare parameters to set ACL Profile
        CreateAclProfileInputBuilder ib = new CreateAclProfileInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName)
                                                        .setAclLayer(layer).setVlanMask(vlanMask);
        if(layer == AclLayer.IP)
            ib = ib.setSrcIpMask(srcIpMask).setDstIpMask(dstIpMask);

        //execute addAclProfile(), and check return null parameters?
        RpcResult<CreateAclProfileOutput> rpcResult;
        try {
            Future<RpcResult<CreateAclProfileOutput>> ret = acl.createAclProfile(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to create ACL Profile on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to create ACL Profile on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to create ACL Profile on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getCreateAclProfileResult() == null){
                ci.println();
                ci.println("Fail to create ACL Profile on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call createAclProfile() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call createAclProfile() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result createAclProfileResult = rpcResult.getResult().getCreateAclProfileResult();
        switch (createAclProfileResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully create ACL Profile on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to create ACL Profile on node " + nodeId);
                ci.println();
            }
        }

    }

    //CLI: aclMgr delProfile <switch> {[profileId <profile_id>] | [profileName <profile_name>]}
    public void _aclMgrDelACLProfile(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String garbage = ci.nextArgument();
        //TODO: the arg numbering above skip 5, because the code reuse _aclMgrDelACLRule()'s code. For convenience of future modification, I leave the same numbering here

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: aclMgr delProfile <switch> {[profileId <profile_id>] | [profileName <profile_name>]}");
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

        //parse arg3: 'profileId' or 'profileName'
        int profileId = -1;
        if(arg3.compareToIgnoreCase("profileId") == 0){
            //parse arg4: String profile_id to int value
            try{
                profileId = Integer.parseInt(arg4);
            }catch(NumberFormatException e1){
                ci.println("Error: convert argument <profile_id> " + arg4 + " to int value error: " + e1);
                return;
            }
        }
        String profileName = null;
        if(arg3.compareToIgnoreCase("profileName") == 0){
            //arg4: profileName
            profileName = new String(arg4);
        }


        //check AclService exists?
        if (acl == null) {
            acl = this.session.getRpcService(AclService.class);
            if (acl == null) {
                logger.debug("Can't get AclService, can't proceed!");
                return;
            }
        }

        //prepare parameters to delete ACL profile
        DelAclProfileInputBuilder ib = new DelAclProfileInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName);

        //execute delAclProfile(), and check return null parameters?
        RpcResult<DelAclProfileOutput> rpcResult;
        try {
            Future<RpcResult<DelAclProfileOutput>> ret = acl.delAclProfile(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getDelAclProfileResult() == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call delAclProfile() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call delAclProfile() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result delAclProfileResult = rpcResult.getResult().getDelAclProfileResult();
        switch (delAclProfileResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully delete ACL Profile on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to delete ACL Profile on node " + nodeId);
                ci.println();
            }
        }

    }

    //CLI: aclMgr clearTable <switch>
    public void _aclMgrClearACLTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: aclMgr clearTable <switch>");
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


        //check AclService exists?
        if (acl == null) {
            acl = this.session.getRpcService(AclService.class);
            if (acl == null) {
                logger.debug("Can't get AclService, can't proceed!");
                return;
            }
        }

        //prepare parameters to clear ACL Table
        ClearAclTableInputBuilder ib = new ClearAclTableInputBuilder()
                                                        .setNodeId(nodeId);

        //execute clearAclTable(), and check return null parameters?
        RpcResult<ClearAclTableOutput> rpcResult;
        try {
            Future<RpcResult<ClearAclTableOutput>> ret = acl.clearAclTable(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getClearAclTableResult() == null){
                ci.println();
                ci.println("Fail to set ACL Profile on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call clearAclTable() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call clearAclTable() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        Result clearAclTableResult = rpcResult.getResult().getClearAclTableResult();
        switch (clearAclTableResult) {
            case SUCCESS:{
                ci.println();
                ci.println("Successfully clear ACL Table on node " + nodeId);
                ci.println();
                break;
            }
            default:{
                ci.println();
                ci.println("Fail to clear ACL Table on node " + nodeId);
                ci.println();
            }
        }

    }

    public List<Short> convertPortListString2ShortList(String portList){
        String[] portsStr = portList.split(",");
        List<Short> ports = new ArrayList<Short>();
        for(int i = 0; i < portsStr.length; i++){
            try{
                ports.add(Short.parseShort(portsStr[i]));
                if(ports.get(i) < 0){
                    logger.debug("AclServiceImpl.convertPortListString2ShortArray() error: input string \"" + portList +"\" has invalid port number " + portsStr[i]);
                    return null;
                }
            }
            catch(NumberFormatException e1){
                logger.debug("AclServiceImpl.convertPortListString2ShortArray() error: input string \"" + portList +"\" has non-number string: " + e1);
                return null;//means fail
            }
        }
        return ports;
    }

    private short parseMaskStringToShort(String arg){
            short mask;
            if(isStartWith0x(arg)){//string starts as "0x"
                if(arg.length() != 6){
                    //ci.println("Error: argument '" + arg + "' is not in valid mask format for short integer(e.g. 0x0FFF)");
                    return -1;
                }
                String str = arg.substring(2, 4) + ":" + arg.substring(4, 6);
                mask = (short)HexString.toLong(str);
            }
            else{//string is representing as an number
                try{
                    mask = Short.parseShort(arg);
                }catch(NumberFormatException e1){
                    //ci.println("Error: convert argument " + arg + " to short value error: " + e1);
                    return -1;
                }
            }

            return mask;
    }

    private boolean isStartWith0x(String str){
        if(str.length() < 2) return false;
        if(str.substring(0, 2).compareToIgnoreCase("0x") == 0)
            return true;
        else
            return false;
    }

    private void printCiAddAclProfileUsage(CommandInterpreter ci){
            ci.println();
            ci.println("Please use: aclMgr createProfile <switch> <profile_id> <profile_name>");
            ci.println("\t\t\t {   [<layer('ethernet')> <vlan_mask>]");
            ci.println("\t\t\t   | [<layer('ip')> {[vlanMask <vlan_mask>] [srcIpMask <src_ip_mask>] [dstIpMask <dst_ip_mask>]}");
            ci.println();
    }

    private void printCiSetAclRuleUsage(CommandInterpreter ci){
            ci.println();
            ci.println("Please use: aclMgr setRule <switch>");
            ci.println("\t\t\t {[profileId <profile_id>] | [profileName <profile_name>]}");
            ci.println("\t\t\t {[ruleId <rule_id>] | [ruleName <rule_name>]}");
            ci.println("\t\t\t  ports <portList(seperate_by_comma)>");
            ci.println("\t\t\t  layer <layer('ethernet'or'ip')>");
            ci.println("\t\t\t  {[vlanId <vlan_id>] [srcIp <src_ip>] [dsIp <dst_ip>]}");
            ci.println("\t\t\t  action <action('permit'or'deny')>");
            ci.println();
    }


    @Override//CommandProvider's
    public String getHelp() {
        return new String("AclManagerImpl.getHelp():null");
    }
}

