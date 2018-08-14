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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.internal.util.CommandInterpreter;
import org.opendaylight.snmp4sdn.internal.util.CommandProvider;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
//TODO: should list every used one, or just use '*' to include all
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutputBuilder;
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AclServiceImpl implements AclService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(AclServiceImpl.class);

    Controller controller = null;
    CLIHandler cli = null;
    private CmethUtil cmethUtil = null;

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;
        if(cmethUtil == null){
            logger.debug("ERROR: AclServiceImpl: setController(): cmethUtil is null");
        }
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    public void init() {//this method would not be called, when Activator.java adopt "new AclProvider()->new AclServiceImpl()"
        logger.debug("AclServiceImpl: init() is called");
        //registerWithOSGIConsole();
    }

    /*private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }*/

    private boolean checkNodeIpValid(long nodeId){
        if(cmethUtil == null){
            logger.debug("ERROR: AclServiceImpl: checkNodeIpValid(): cmethUtil is null");
            return false;
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        if(sw_ipAddr == null){
            logger.debug("ERROR: checkNodeIpValid(): IP address of switch (nodeId: " + nodeId + ") not in DB");
            return false;
        } else {
            return true;
        }
    }

    /*
    *The following many createXxxxFilRpcResult() are for easy of return fail
    */
    private ListenableFuture<RpcResult<SetAclRuleOutput>> createSetAclRuleFailRpcResult(){
        return RpcResultBuilder.<SetAclRuleOutput>failed().buildFuture();
    }
    private ListenableFuture<RpcResult<CreateAclProfileOutput>> createCreateAclProfileFailRpcResult(){
        return RpcResultBuilder.<CreateAclProfileOutput>failed().buildFuture();
    }
    private ListenableFuture<RpcResult<DelAclProfileOutput>> createDelAclProfileFailRpcResult(){
        return RpcResultBuilder.<DelAclProfileOutput>failed().buildFuture();
    }
    private ListenableFuture<RpcResult<DelAclRuleOutput>> createDelAclRuleFailRpcResult(){
        return RpcResultBuilder.<DelAclRuleOutput>failed().buildFuture();
    }
    private ListenableFuture<RpcResult<ClearAclTableOutput>> createClearAclTableFailRpcResult(){
        return RpcResultBuilder.<ClearAclTableOutput>failed().buildFuture();
    }

    //md-sal
    @Override//TODO: rename as setAclRule()
    public ListenableFuture<RpcResult<SetAclRuleOutput>> setAclRule(SetAclRuleInput input){

        //check null parameters
        if(input == null){
            logger.debug("ERROR: setAclRule(): given null input");
            return createSetAclRuleFailRpcResult();
        }
        //SetAclRuleInput
        Long nodeId = input.getNodeId();
        //AclIndex
        Integer profileId = input.getProfileId();
        String profileName = input.getProfileName();
        Integer ruleId = input.getRuleId();
        String ruleName = input.getRuleName();
        //AclRule
        List<Short> portList = input.getPortList();
        AclLayer layer = input.getAclLayer();
        AclAction action = input.getAclAction();
        //AclField
        Integer vlanId = input.getVlanId();
        String srcIp = input.getSrcIp();
        String dstIp = input.getDstIp();
        //TODO: The following code is reused by CLIHandler.setAclRule()'s parameter checking. If code change, may refect to CLIHandler's code)
        if(nodeId == null){
            logger.debug("ERROR: setAclRule(): given nodeId is null");
            return createSetAclRuleFailRpcResult();
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: setAclRule(): given profileId and profileName are null");
            return createSetAclRuleFailRpcResult();
        }
        if(ruleId == null && ruleName == null){
            logger.debug("ERROR: setAclRule(): given ruleId and ruleName are null");
            return createSetAclRuleFailRpcResult();
        }
        if(portList == null){
            logger.debug("ERROR: setAclRule(): given portList is null");
            return createSetAclRuleFailRpcResult();
        }
        if(layer == null){
            logger.debug("ERROR: setAclRule(): given layer is null");
            return createSetAclRuleFailRpcResult();
        }
        if(action == null){
            logger.debug("ERROR: setAclRule(): given action is null");
            return createSetAclRuleFailRpcResult();
        }
        if(layer == AclLayer.ETHERNET){
            if(vlanId == null){
                logger.debug("ERROR: setAclRule(): layer is ethernet, but given vlanId is null");
                return createSetAclRuleFailRpcResult();
            }
        }
        if(layer == AclLayer.IP){
            if(vlanId == null && srcIp == null && dstIp == null){
                logger.debug("ERROR: setAclRule(): layer is IP, but given all of the vlanId, srcIp, dstIp, are null");
                return createSetAclRuleFailRpcResult();
            }
        }

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: setAclRule(): given invalid nodeId {}", nodeId);
            return createSetAclRuleFailRpcResult();
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: setAclRule(): given invalid nodeId {}", nodeId);
            return createSetAclRuleFailRpcResult();
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: setAclRule(): given invalid profileId {}", profileId);
            return createSetAclRuleFailRpcResult();
        }
        if(ruleId != null){
            if(ruleId < 0){//TODO: profileId valid range?
                logger.debug("ERROR: setAclRule(): given invalid ruleId {} and ruleName are null", ruleId);
                return createSetAclRuleFailRpcResult();
            }
        }
        for(Short port : portList){
            if(port < 0 || port > 32){//TODO: port range
                logger.debug("ERROR: setAclRule(): given invalid port {} in portList", nodeId);
                return createSetAclRuleFailRpcResult();
            }
        }
        if(vlanId != null){
            if(!isValidVlan(vlanId)){
                logger.debug("ERROR: setAclRule(): given invalid vlanId {}", vlanId);
                return createSetAclRuleFailRpcResult();
            }
        }

        //execute CLIHandler.setAclRule()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: setAclRule(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return createSetAclRuleFailRpcResult();
        }
        Status status = cli.setAclRule(nodeId, profileId, profileName, ruleId, ruleName, portList, layer, vlanId, srcIp, dstIp, action);
        if(status == null){
            logger.debug("ERROR: setAclRule(): call CLIHandler.setAclRule() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createSetAclRuleFailRpcResult();
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            SetAclRuleOutputBuilder ob = new SetAclRuleOutputBuilder().setSetAclRuleResult(Result.SUCCESS);
            return RpcResultBuilder.<SetAclRuleOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: setAclRule(): call CLIHandler.setAclRule() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createSetAclRuleFailRpcResult();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<CreateAclProfileOutput>> createAclProfile(CreateAclProfileInput input){
        //check null parameters
        if(input == null){
            logger.debug("ERROR: createAclProfile(): given null input");
            return createCreateAclProfileFailRpcResult();
        }
        //SetAclRuleInput
        Long nodeId = input.getNodeId();
        //AclProfileIndex
        Integer profileId = input.getProfileId();
        String profileName = input.getProfileName();
        //AclProfile
        AclLayer layer = input.getAclLayer();
        Short vlanMask = input.getVlanMask();
        String srcIpMask = input.getSrcIpMask();
        String dstIpMask = input.getDstIpMask();
        //TODO: The following code is reused by CLIHandler.createAclProfile()'s parameter checking. If code change, may refect to CLIHandler's code)
        if(nodeId == null){
            logger.debug("ERROR: createAclProfile(): given nodeId is null");
            return createCreateAclProfileFailRpcResult();
        }
        if(profileId == null){
            logger.debug("ERROR: createAclProfile(): given profileId is null");
            return createCreateAclProfileFailRpcResult();
        }
        if(profileName == null){
            logger.debug("ERROR: createAclProfile(): given profileName is null");
            return createCreateAclProfileFailRpcResult();
        }
        if(layer == null){
            logger.debug("ERROR: createAclProfile(): given layer is null");
            return createCreateAclProfileFailRpcResult();
        }
        if(layer == AclLayer.ETHERNET){
            if(vlanMask == null){
                logger.debug("ERROR: createAclProfile(): layer is ethernet, but given vlanMask is null");
                return createCreateAclProfileFailRpcResult();
            }
        }
        if(layer == AclLayer.IP){
            if(vlanMask == null && srcIpMask == null && dstIpMask == null){
                logger.debug("ERROR: createAclProfile(): layer is IP, but given all of the vlanMask, srcIpMask, dstIpMask, are null");
                return createCreateAclProfileFailRpcResult();
            }
        }

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: createAclProfile(): given invalid nodeId {}", nodeId);
            return createCreateAclProfileFailRpcResult();
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: createAclProfile(): given invalid nodeId {}", nodeId);
            return createCreateAclProfileFailRpcResult();
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: createAclProfile(): given invalid profileId {}", profileId);
            return createCreateAclProfileFailRpcResult();
        }
        if(vlanMask != null){
            if(vlanMask < 0 || vlanMask > 0xffff){//TODO: vlanMask valid range?
                logger.debug("ERROR: createAclProfile(): given invalid vlanMask {}", vlanMask);
                return createCreateAclProfileFailRpcResult();
            }
        }

        //execute CLIHandler.createAclProfile()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: createAclProfile(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return createCreateAclProfileFailRpcResult();
        }
        Status status = cli.createAclProfile(nodeId, profileId, profileName, layer, vlanMask, srcIpMask, dstIpMask);
        if(status == null){
            logger.debug("ERROR: createAclProfile(): call CLIHandler.createAclProfile() with nodeId {} fail", nodeId);
            return createCreateAclProfileFailRpcResult();
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            CreateAclProfileOutputBuilder ob = new CreateAclProfileOutputBuilder().setCreateAclProfileResult(Result.SUCCESS);
            return RpcResultBuilder.<CreateAclProfileOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: createAclProfile(): call CLIHandler.createAclProfile() with nodeId {} fail", nodeId);
            return createCreateAclProfileFailRpcResult();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<DelAclProfileOutput>> delAclProfile(DelAclProfileInput input){

        //check null parameters
        if(input == null){
            logger.debug("ERROR: delAclProfile(): given null input");
            return createDelAclProfileFailRpcResult();
        }
        //DelAclProfileInput
        Long nodeId = input.getNodeId();
        //AclIndex
        Integer profileId = input.getProfileId();
        String profileName = input.getProfileName();
        if(nodeId == null){
            logger.debug("ERROR: delAclProfile(): given nodeId is null");
            return createDelAclProfileFailRpcResult();
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: delAclProfile(): given profileId and profileName are null");
            return createDelAclProfileFailRpcResult();
        }

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: delAclProfile(): given invalid nodeId {}", nodeId);
            return createDelAclProfileFailRpcResult();
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: delAclProfile(): given invalid nodeId {}", nodeId);
            return createDelAclProfileFailRpcResult();
        }
        if(profileId != null){
            if(profileId < 0){//TODO1: profileId valid range?
                logger.debug("ERROR: delAclProfile(): given invalid profileId {}", profileId);
                return createDelAclProfileFailRpcResult();
            }
        }

        //execute CLIHandler.delAclProfile()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: delAclProfile(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return createDelAclProfileFailRpcResult();
        }
        Status status = cli.delAclProfile(nodeId, profileId, profileName);
        if(status == null){
            logger.debug("ERROR: delAclProfile(): call CLIHandler.delAclProfile() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createDelAclProfileFailRpcResult();
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            DelAclProfileOutputBuilder ob = new DelAclProfileOutputBuilder().setDelAclProfileResult(Result.SUCCESS);
            return RpcResultBuilder.<DelAclProfileOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: delAclProfile(): call CLIHandler.delAclProfile() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createDelAclProfileFailRpcResult();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<DelAclRuleOutput>> delAclRule(DelAclRuleInput input){

        //check null parameters
        if(input == null){
            logger.debug("ERROR: delAclRule(): given null input");
            return createDelAclRuleFailRpcResult();
        }
        //DelAclRuleInput
        Long nodeId = input.getNodeId();
        //AclIndex
        Integer profileId = input.getProfileId();
        String profileName = input.getProfileName();
        Integer ruleId = input.getRuleId();
        String ruleName = input.getRuleName();
        if(nodeId == null){
            logger.debug("ERROR: delAclRule(): given nodeId is null");
            return createDelAclRuleFailRpcResult();
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: delAclRule(): given profileId and profileName are null");
            return createDelAclRuleFailRpcResult();
        }
        if(ruleId == null && ruleName == null){
            logger.debug("ERROR: delAclRule(): given ruleId and ruleName are null");
            return createDelAclRuleFailRpcResult();
        }

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: delAclRule(): given invalid nodeId {}", nodeId);
            return createDelAclRuleFailRpcResult();
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: delAclRule(): given invalid nodeId {}", nodeId);
            return createDelAclRuleFailRpcResult();
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: delAclRule(): given invalid profileId {}", profileId);
            return createDelAclRuleFailRpcResult();
        }
        if(ruleId != null){
            if(ruleId < 0){//TODO: profileId valid range?
                logger.debug("ERROR: delAclRule(): given invalid ruleId {} and ruleName are null", ruleId);
                return createDelAclRuleFailRpcResult();
            }
        }

        //execute CLIHandler.delAclRule()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: delAclRule(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return createDelAclRuleFailRpcResult();
        }
        Status status = cli.delAclRule(nodeId, profileId, profileName, ruleId, ruleName);
        if(status == null){
            logger.debug("ERROR: delAclRule(): call CLIHandler.delAclRule() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createDelAclRuleFailRpcResult();
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            DelAclRuleOutputBuilder ob = new DelAclRuleOutputBuilder().setDelAclRuleResult(Result.SUCCESS);
            return RpcResultBuilder.<DelAclRuleOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: delAclRule(): call CLIHandler.delAclRule() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createDelAclRuleFailRpcResult();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<ClearAclTableOutput>> clearAclTable(ClearAclTableInput input){
        //check null parameters
        if(input == null){
            logger.debug("ERROR: clearAclTable(): given null input");
            return createClearAclTableFailRpcResult();
        }
        //ClearAclTableInput
        Long nodeId = input.getNodeId();

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: clearAclTable(): given invalid nodeId {}", nodeId);
            return createClearAclTableFailRpcResult();
        }
        if(!checkNodeIpValid(nodeId)){
            logger.debug("ERROR: clearAclTable(): given invalid nodeId {}", nodeId);
            return createClearAclTableFailRpcResult();
        }

        //execute CLIHandler.clearAclTable()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: clearAclTable(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return createClearAclTableFailRpcResult();
        }
        Status status = cli.clearAclTable(nodeId);
        if(status == null){
            logger.debug("ERROR: clearAclTable(): call CLIHandler.clearAclTable() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createClearAclTableFailRpcResult();
        }
        //TODO: for each case of returned status error code, give Result.XXX accordingly
        if(status.isSuccess()){
            ClearAclTableOutputBuilder ob = new ClearAclTableOutputBuilder().setClearAclTableResult(Result.SUCCESS);
            return RpcResultBuilder.<ClearAclTableOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: clearAclTable(): call CLIHandler.clearAclTable() with nodeId {} fail", nodeId);//TODO: print other parameters
            return createClearAclTableFailRpcResult();
        }
    }

    //Deprecated
    //md-sal
    //@Override
    /*public ListenableFuture<RpcResult<GetAclIndexListOutput>> getAclIndexList(GetAclIndexListInput input){
        //TODO: so far only return profileId and ruleId, profileName and ruleName are null in the return object

        //check null parameters
        if(input == null){
            logger.debug("ERROR: getAclIndexList(): given null input");
            return null;
        }
        //GetAclIndexListInput
        Long nodeId = input.getNodeId();
        if(nodeId == null){
            logger.debug("ERROR: getAclIndexList(): given nodeId is null");
            return null;
        }

        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: getAclIndexList(): given invalid nodeId {}", nodeId);
            return null;
        }

        //execute CLIHandler.getAclIndexList()
        CLIHandler cli = new CLIHandler(cmethUtil, nodeId);
        if(!cli.isLoggedIn()){
            logger.debug("ERROR: getAclIndexList(): create CLIHandler with nodeId {}, loggin fail", nodeId);
            return null;
        }
        List<ACLIndex> indexList = cli.getAclIndexList(nodeId);
        if(indexList == null){
            logger.debug("ERROR: getAclIndexList(): call CLIHandler.getAclIndexList() with nodeId {} fail", nodeId);
            return null;
        }

        List<AclIndexListEntry> retList = new ArrayList<AclIndexListEntry>();
        for(ACLIndex index : indexList){
            AclIndexListEntryBuilder anb = new AclIndexListEntryBuilder().setProfileId(index.profileId).setProfileName(index.profileName).setRuleId(index.ruleId).setRuleName(index.ruleName);
            AclIndexListEntry entry = anb.build();
            retList.add(entry);
        }
        GetAclIndexListOutputBuilder ob = new GetAclIndexListOutputBuilder().setAclIndexListEntry(retList);
        RpcResult<GetAclIndexListOutput> rpcResult =
                    Rpcs.<GetAclIndexListOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }*/

    private boolean isValidVlan(Integer vlanId){
        if(vlanId < 1 || vlanId > 4095) {
            return false;
        } else {
            return true;
        }
    }

    /*
    * CLI as following
    */

    //CLI: s4sACL
    public void _s4sACL(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use:");
            ci.println("s4sACL [");
            ci.println("  createProfile <switch> <...>");
            ci.println("| setRule <switch> <...>");
            ci.println("| delProfile <switch> <...>");
            ci.println("| delRule <switch> <...>");
            ci.println("| clearTable <switch> <...>");
            //ci.println("| showIndexList <switch> <...>");
            ci.println("(<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("createProfile") == 0){
            ci.println();
            _s4sCreateACLProfile(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setRule") == 0){
            ci.println();
            _s4sAddACLRule(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delProfile") == 0){
            ci.println();
            _s4sDelACLProfile(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("delRule") == 0){
            ci.println();
            _s4sDelACLRule(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("clearTable") == 0){
            ci.println();
            _s4sClearACLTable(ci);
            ci.println();
        }
        /*else if(arg1.compareToIgnoreCase("showIndexList") == 0){
            ci.println();
            _s4sGetACLIndexList(ci);
            ci.println();
        }*/
        else{
            ci.println();
            ci.println("Please use:");
            ci.println("s4sACL [");
            ci.println("  createProfile <switch> <...>");
            ci.println("| setRule <switch> <...>");
            ci.println("| delProfile <switch> <...>");
            ci.println("| delRule <switch> <...>");
            ci.println("| clearTable <switch> <...>");
            //ci.println("| showIndexList <switch> <...>");
            ci.println("(<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: s4sACL setRule <switch>
    //                                  {[profileId <profile_id>] | [profileName <profile_name>]}
    //                                  {[ruleId <rule_id>] | [ruleName <rule_name>]}
    //                                   ports <portList(seperate_by_comma)>
    //                                   layer <layer('ethernet'or'ip')>
    //                                   {[vlanId <vlan_id>] [srcIp <src_ip>] [dsIp <dst_ip>]}
    //                                   action <action('permit'or'deny')>
    public void _s4sAddACLRule(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String arg5 = ci.nextArgument();//'ruleId' or 'ruleName'
        String arg6 = ci.nextArgument();//value of ruleId or ruleName
        String arg7 = ci.nextArgument();//'portList'
        String arg7v = ci.nextArgument();//value of portList
        String arg8 = ci.nextArgument();//'layer'
        String arg8v = ci.nextArgument();//value of layer
        /*String arg9 = ci.nextArgument();//'vlanId'
        String arg9v = ci.nextArgument();//value of vlanId
        //TODO: srcIp and dstIp are not must
        String arg10 = ci.nextArgument();//'srcIp'
        String arg10v = ci.nextArgument();//value of srcIp
        String arg11 = ci.nextArgument();//'dstIp'
        String arg11v = ci.nextArgument();//value of dstIp
        String arg12 = ci.nextArgument();//'action'
        String arg12v = ci.nextArgument();//value of action
        String garbage = ci.nextArgument();*/

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || arg7 == null || arg7v == null
            || arg8 == null || arg8v == null/*arg9 == null || arg10 == null || arg11 == null || arg12 == null
            || garbage != null*/){
            printCiSetAclRuleUsage(ci);
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
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
        for(Short port : ports) {
            portListChk += port + ",";
        }

        //arg8
        if(arg8.compareToIgnoreCase("layer") != 0){
            printCiSetAclRuleUsage(ci);
            return;
        }

        //parse arg8v: <layer> to AclLayer value
        AclLayer layer = null;
        if(arg8v.compareToIgnoreCase("ethernet") == 0) {
            layer = AclLayer.ETHERNET;
        } else if(arg8v.compareToIgnoreCase("ip") == 0) {
            layer = AclLayer.IP;
        } else{
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
            if(arg == null) {
                break;
            }

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
                if(actionValue.compareToIgnoreCase("permit") == 0) {
                    action = AclAction.PERMIT;
                }
                if(actionValue.compareToIgnoreCase("deny") == 0) {
                    action = AclAction.DENY;
                }
            }
            //Leave this checking to setAclRule(), here should allow invalid case so as to test setAclRule()'s reliability
            /*else{
                ci.println("Field '" + arg + "' is not supported");
                printCiSetAclRuleUsage(ci);
                return;
            }*/
        }
        //Leave this checking to setAclRule(), here should allow invalid case so as to test setAclRule()'s reliability
        /*if(action == null){
            ci.println("Action is not given!");
            printCiSetAclRuleUsage(ci);
            return;
        }*/

/*//This part of code is re-write as the while loop above, since not every arg is must
        //parse arg9: String vlan_id to int value vlanId
        int vlanId = -1;
        try{
            vlanId = Integer.parseInt(arg9);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg9 + " to int value error: " + e1);
            return;
        }

        //arg10: srcIp
        String srcIp = new String(arg10);

        //arg11: dstIp
        String dstIp = new String(arg11);

        //prase arg12: <action> to AclAction value
        AclAction action = null;
        if(arg12.compareToIgnoreCase("permit") == 0)
            action = AclAction.PERMIT;
        if(arg12.compareToIgnoreCase("deny") == 0)
            action = AclAction.DENY;
*/

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
            ListenableFuture<RpcResult<SetAclRuleOutput>> ret = this.setAclRule(ib.build());
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

    private void printCiSetAclRuleUsage(CommandInterpreter ci){
            ci.println();
            ci.println("Please use: s4sACL setRule <switch>");
            ci.println("\t\t\t {[profileId <profile_id>] | [profileName <profile_name>]}");
            ci.println("\t\t\t {[ruleId <rule_id>] | [ruleName <rule_name>]}");
            ci.println("\t\t\t  ports <portList(seperate_by_comma)>");
            ci.println("\t\t\t  layer <layer('ethernet'or'ip')>");
            ci.println("\t\t\t  {[vlanId <vlan_id>] [srcIp <src_ip>] [dsIp <dst_ip>]}");
            ci.println("\t\t\t  action <action('permit'or'deny')>");
            ci.println();
    }

    //CLI: s4sACL delRule <switch> {[profileId <profile_id>] | [profileName <profile_name>]} ruleId <rule_id>
    public void _s4sDelACLRule(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String arg5 = ci.nextArgument();//'ruleId'
        String arg6 = ci.nextArgument();//value of ruleId
        String garbage = ci.nextArgument();
        //TODO: the arg numbering above skip 5, because the code reuse _s4sAddACLRule()'s code. For convenience of future modification, I leave the same numbering here

        if(arg2 == null || arg3 == null || arg4 == null || arg5 == null || arg6 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sACL delRule <switch> {[profileId <profile_id>] | [profileName <profile_name>]} ruleId <rule_id>");
            ci.println();
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
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


        //prepare parameters to delete ACL rule
        DelAclRuleInputBuilder ib = new DelAclRuleInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName)
                                                        .setRuleId(ruleId);

        //execute delAclRule(), and check return null parameters?
        RpcResult<DelAclRuleOutput> rpcResult;
        try {
            ListenableFuture<RpcResult<DelAclRuleOutput>> ret = this.delAclRule(ib.build());
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

    //CLI: s4sACL createProfile <switch> <profile_id> <profile_name>
    //                                      {   [<layer('ethernet')> vlanMask <vlan_mask>]
    //                                        | [<layer('ip')> {[vlanMask <vlan_mask>] [srcIpMask <src_ip_mask>] [dstIpMask <dst_ip_mask>]}
    public void _s4sCreateACLProfile(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//profileId
        String arg4 = ci.nextArgument();//profileName
        String arg8 = ci.nextArgument();//layer
        //String arg9 = ci.nextArgument();//vlanMask
        //String arg10 = ci.nextArgument();//srcIpMask
        //String arg11 = ci.nextArgument();//dstIpMask
        //String garbage = ci.nextArgument();
        //TODO: the arg numbering above skip such as 5~7, because the code reuse _s4sAddACLRule()'s code. For convenience of future modification, I leave the same numbering here

        if(arg2 == null || arg3 == null || arg4 == null
            || arg8 == null /*|| arg9 == null || arg10 == null || arg11 == null
            || garbage != null*/){
            printCiAddAclProfileUsage(ci);
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
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
        //Leave this checking to createAclProfile(), here should allow invalid case so as to test createAclProfile()'s reliability
        /*else{
            ci.println();
            ci.println("ERROR: Layer of '" + arg8 + "' is not supported");
            printCiAddAclProfileUsage(ci);
            return;
        }*/

        Short vlanMask = null;

        //parser for Ethernet layer
        //and parse <vlan_mask>
        if(layer == AclLayer.ETHERNET){
            //parse "vlanMask <vlan_mask>"
            String vlanMaskStr = ci.nextArgument();
            String maskValue = ci.nextArgument();
            ci.println("vlanMaskStr = " + vlanMaskStr + ", maskValue = " + maskValue);
            if(vlanMaskStr.compareToIgnoreCase("vlanMask") != 0){
                printCiAddAclProfileUsage(ci);
                return;
            }
            if(maskValue == null){
                printCiAddAclProfileUsage(ci);
                return;
            }
            vlanMask = parseMaskStringToShort(maskValue);
            //Leave this checking to createAclProfile(), here should allow invalid case so as to test createAclProfile()'s reliability
            /*if(vlanMask < 0){
                ci.println("Error: argument '" + maskValue + "' is not in valid mask format for short integer(e.g. 0x0FFF)");
                return;
            }*/

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
                if(maskStr == null) {
                    break;
                }
                if(maskStr != null && valueStr == null){
                    printCiAddAclProfileUsage(ci);
                    return;
                }

                if(maskStr.compareToIgnoreCase("vlanMask") == 0){
                    //arg: vlanMask
                    vlanMask = parseMaskStringToShort(valueStr);
                    //Leave this checking to createAclProfile(), here should allow invalid case so as to test createAclProfile()'s reliability
                    /*if(vlanMask < 0){
                        ci.println("Error: argument '" + valueStr + "' is not in valid mask format for short integer(e.g. 0x0FFF)");
                        return;
                    }*/
                }
                else if(maskStr.compareToIgnoreCase("srcIpMask") == 0){
                    //arg: srcIpMask
                    srcIpMask = new String(valueStr);
                }
                else if(maskStr.compareToIgnoreCase("dstIpMask") == 0){
                    //arg: dskIpMask
                    dstIpMask = new String(valueStr);
                }
                //Leave this checking to createAclProfile(), here should allow invalid case so as to test createAclProfile()'s reliability
                /*else{
                    ci.println();
                    ci.println("Mask field '" + maskStr + "' is not supported");
                    printCiAddAclProfileUsage(ci);
                    return;
                }*/
            }

            //Leave this checking to createAclProfile(), here should allow invalid case so as to test createAclProfile()'s reliability
            /*if(vlanMask == null && srcIpMask == null && dstIpMask == null){
                printCiAddAclProfileUsage(ci);
                return;
            }*/
        }


        //prepare parameters to set ACL Profile
        CreateAclProfileInputBuilder ib = new CreateAclProfileInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName)
                                                        .setAclLayer(layer);
        if(layer == AclLayer.ETHERNET) {
            ib = ib.setVlanMask(vlanMask);
        }
        if(layer == AclLayer.IP) {
            ib = ib.setVlanMask(vlanMask).setSrcIpMask(srcIpMask).setDstIpMask(dstIpMask);
        }

        //execute addAclProfile(), and check return null parameters?
        RpcResult<CreateAclProfileOutput> rpcResult;
        try {
            ListenableFuture<RpcResult<CreateAclProfileOutput>> ret = this.createAclProfile(ib.build());
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

    private void printCiAddAclProfileUsage(CommandInterpreter ci){
            ci.println();
            ci.println("Please use: s4sACL createProfile <switch> <profile_id> <profile_name>");
            ci.println("\t\t\t {   [<layer('ethernet')> vlanMask <vlan_mask>]");
            ci.println("\t\t\t   | [<layer('ip')> {[vlanMask <vlan_mask>] [srcIpMask <src_ip_mask>] [dstIpMask <dst_ip_mask>]}");
            ci.println();
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
        if(str.length() < 2) {
            return false;
        }
        if(str.substring(0, 2).compareToIgnoreCase("0x") == 0) {
            return true;
        } else {
            return false;
        }
    }

    //CLI: s4sACL delProfile <switch> {[profileId <profile_id>] | [profileName <profile_name>]}
    public void _s4sDelACLProfile(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String arg3 = ci.nextArgument();//'profileId' or 'profileName'
        String arg4 = ci.nextArgument();//value of profileId or profileName
        String garbage = ci.nextArgument();
        //TODO: the arg numbering above skip 5, because the code reuse _s4sDelACLRule()'s code. For convenience of future modification, I leave the same numbering here

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sACL delProfile <switch> {[profileId <profile_id>] | [profileName <profile_name>]}");
            ci.println();
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
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


        //prepare parameters to delete ACL profile
        DelAclProfileInputBuilder ib = new DelAclProfileInputBuilder()
                                                        .setNodeId(nodeId)
                                                        .setProfileId(profileId).setProfileName(profileName);

        //execute delAclProfile(), and check return null parameters?
        RpcResult<DelAclProfileOutput> rpcResult;
        try {
            ListenableFuture<RpcResult<DelAclProfileOutput>> ret = this.delAclProfile(ib.build());
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

    //CLI: s4sACL clearTable <switch>
    public void _s4sClearACLTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sACL clearTable <switch>");
            ci.println();
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }


        //prepare parameters to clear ACL Table
        ClearAclTableInputBuilder ib = new ClearAclTableInputBuilder()
                                                        .setNodeId(nodeId);

        //execute clearAclTable(), and check return null parameters?
        RpcResult<ClearAclTableOutput> rpcResult;
        try {
            ListenableFuture<RpcResult<ClearAclTableOutput>> ret = this.clearAclTable(ib.build());
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

    //Deprecated
    //CLI: s4sACL getIndexList <switch>
    /*public void _s4sGetACLIndexList(CommandInterpreter ci){
        String arg2 = ci.nextArgument();//nodeid
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sACL getIndexList <switch>");
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


        //prepare parameters to get ACL Index List
        GetAclIndexListInputBuilder ib = new GetAclIndexListInputBuilder()
                                                        .setNodeId(nodeId);

        //execute delAclProfile(), and check return null parameters?
        RpcResult<GetAclIndexListOutput> rpcResult;
        try {
            ListenableFuture<RpcResult<GetAclIndexListOutput>> ret = this.getAclIndexList(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to get ACL Index List on node " + nodeId + " (null return)");
                ci.println();
                return;
            }

            rpcResult = ret.get();
            if(rpcResult == null){
                ci.println();
                ci.println("Fail to get ACL Index List on node " + nodeId + " (null rpcResult)");
                ci.println();
                return;
            }
            if(rpcResult.getResult() == null){
                ci.println();
                ci.println("Fail to get ACL Index List on node " + nodeId + " (null in rpcResult)");
                ci.println();
                return;
            }

            if(rpcResult.getResult().getAclIndexListEntry() == null){
                ci.println();
                ci.println("Fail to get ACL Index List on node " + nodeId + " (null Result object)");
                ci.println();
                return;
            }

        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call getAclIndexList() occurs exception (node " + nodeId + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call getAclIndexList() occurs exception (node " + nodeId + "): " + ee);
            ci.println();
            return;
        }

        List<AclIndexListEntry> table = rpcResult.getResult().getAclIndexListEntry();
        if(table == null){
                ci.println();
                ci.println("Fail to get ACL Index List on node " + nodeId);
                ci.println();
                return;
        }

        ci.println();
        ci.println("ACL Index List on node " + nodeId);
        ci.println("ProfileId\tRuleId");
        for(AclIndexListEntry entry : table){
            ci.println("\t" + entry.getProfileId() + "\t" + entry.getRuleId());
        }
        ci.println();

    }*/

    public List<Short> convertPortListString2ShortList(String portList){
        String[] portsStr = portList.split(",");
        List<Short> ports = new ArrayList<>();
        for (String element : portsStr) {
            try{
                ports.add(Short.parseShort(element));
                /*if(ports.get(i) < 0){
                    logger.debug("ERROR: convertPortListString2ShortArray() error: input string \"" + portList +"\" has invalid port number " + portsStr[i]);
                    return null;
                }*/
            }
            catch(NumberFormatException e1){
                logger.debug("ERROR: convertPortListString2ShortArray() error: input string \"" + portList +"\" has non-number string: " + e1);
                return null;//means fail
            }
        }
        return ports;
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("ConfigServiceImpl.getHelp():null");
    }

}
