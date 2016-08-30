/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.opendaylight.snmp4sdn.internal.ExpectHandler;

import org.opendaylight.snmp4sdn.sal.utils.Status;
import org.opendaylight.snmp4sdn.sal.utils.StatusCode;

import org.opendaylight.snmp4sdn.ACLIndex;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import org.opendaylight.snmp4sdn.IConfigService;
//import org.opendaylight.snmp4sdn.SNMP4SDNErrorCode;

import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;

import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class CLIHandler{
    private static final Logger logger = LoggerFactory
            .getLogger(CLIHandler.class);

    ExpectHandler expect;
    String sw_ipAddr, username, password, prompt = "#";

    public CLIHandler(CmethUtil cmethUtil, long nodeId){
        if(cmethUtil == null){
            logger.debug("ERROR: CLIHandler(): given cmethUtil is null");
            return;
        }
        if(nodeId < 0){
            logger.debug("ERROR: CLIHandler(): given invalid nodeId {}", nodeId);
            return;
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null){
            logger.debug("ERROR: CLIHandler(): IP address of node {} not in DB", nodeId);
            return;
        }
        if(username == null){
            logger.debug("ERROR: CLIHandler(): CLI username of node {} not in DB", nodeId);
            return;
        }
        if(password == null){
            logger.debug("ERROR: CLIHandler(): CLI password of node {} not in DB", nodeId);
            return;
        }

        //new CLIHandler(sw_ipAddr, username, password);
        //TODO: the code below has the same meaning of the one line above. However, if using the line above, the 'expect' object will be gone, as this CLIHandler() end.
        this.sw_ipAddr = new String(sw_ipAddr);
        this.username = new String(username);
        this.password = new String(password);
        try{
            expect = new ExpectHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);
        }catch(Exception e1){
            logger.error("ERROR: CLIHandler(): create ExpectHandler err: {}", e1);
            logger.error("ERROR: CLIHandler(): usernamePrompt {}", "UserName:");
            logger.error("ERROR: CLIHandler(): passwordPrompt {}", "PassWord:");
            logger.error("ERROR: CLIHandler(): username {}", username);
            logger.error("ERROR: CLIHandler(): password {}", password);
        }
    }

    public CLIHandler(String sw_ipAddr, String username, String password){
        //TODO: how to use the following single line to complete this function. Now invalid because the called CLIHandler() has case of Exception
        //this(sw_ipAddr, "UserName:", "PassWord:", username, password);//d-link:UserName,PassWord  Accton:Username, Passwrod
            //TODO: for the line above, multi-vendor support to get 'usernamePrompt' and 'passwordPrompt', then to call new CLIHandler()
        
        this.sw_ipAddr = new String(sw_ipAddr);
        this.username = new String(username);
        this.password = new String(password);
        try{
            expect = new ExpectHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);
        }catch(Exception e1){
            logger.error("ERROR: CLIHandler(): create ExpectHandler err: {}", e1);
            logger.error("ERROR: CLIHandler(): usernamePrompt {}", "UserName:");
            logger.error("ERROR: CLIHandler(): passwordPrompt {}", "PassWord:");
            logger.error("ERROR: CLIHandler(): username {}", username);
            logger.error("ERROR: CLIHandler(): password {}", password);
        }
    }

    public CLIHandler(String sw_ipAddr, String usernamePrompt, String passwordPrompt, String username, String password){
        this.sw_ipAddr = new String(sw_ipAddr);
        this.username = new String(username);
        this.password = new String(password);
        try{
            expect = new ExpectHandler(sw_ipAddr, usernamePrompt, passwordPrompt, username, password);
        }catch(Exception e1){
            logger.error("ERROR: CLIHandler(): create ExpectHandler err: {}", e1);
            logger.error("ERROR: CLIHandler(): usernamePrompt {}", sw_ipAddr);
            logger.error("ERROR: CLIHandler(): passwordPrompt {}", sw_ipAddr);
            logger.error("ERROR: CLIHandler(): username {}", username);
            logger.error("ERROR: CLIHandler(): password {}", password);
        }
    }

    public boolean isLoggedIn(){
        return expect.isLoggedIn();
    }

    private String printACL(){
        String str = null;
        /*try{
            expect.send("show access_profile profile_id 1\r\n");
            expect.send("a\r\n");
            expect.expect("#");
            logger.trace("3:{}", expect.getBuffer());
        }catch(Exception e){
            logger.error("CLIHandler.printACL() err: {}", e);
        }*///TODO:expectHandler that can call multiple send() is not ready
        return str;
    }
    
    public Status reboot(){
        try{
            logger.trace("expecting--reboot(y/n)?");
            if(expect.execute_2step_end("reboot", "system reboot?(y/n)", "y")){
                logger.trace("sent 'y' to the question above");
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            logger.error("CLIHandler.reboot() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status/*SNMP4SDNErrorCode*/ enableSTP(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                expect.close();
                //return SNMP4SDNErrorCode.SUCCESS;
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: enableSTP(): expectHandler execution err: " + e1);
            //return SNMP4SDNErrorCode.FAIL;
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: enableSTP(): expectHandler execution fail");
        //return SNMP4SDNErrorCode.FAIL;
    }

    public Status/*SNMP4SDNErrorCode*/ disableSTP(){
        try{
            if(expect.execute("disable stp", "#", "Success")){
                expect.close();
                //return SNMP4SDNErrorCode.SUCCESS;
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: disableSTP(): expectHandler execution err: " + e1);
            //return SNMP4SDNErrorCode.FAIL;
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: disableSTP(): expectHandler execution fail");
        //return SNMP4SDNErrorCode.FAIL;
    }

    public Status disableBpduFlooding(){
        try{
            logger.trace("disableBpduFlooding()...");
            if(expect.execute("config stp fbpdu disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableBpduFlooding() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(Short port){
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        try{
            logger.trace("disableBpduFlooding(port {})...", port);
            if(expect.execute("config stp ports " + port + "-" + port + " fbpdu disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableBpduFlooding(port) err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(){
        try{
            logger.trace("disableBroadcastFlooding()...");
            if(expect.execute("config traffic control all broadcast disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableBroadcastFlooding() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(Short port){
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        try{
            logger.trace("disableBroadcastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " broadcast disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableBroadcastFlooding(port) err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(){
        try{
            logger.trace("disableMulticastFlooding()...");
            if(expect.execute("config traffic control all multicast disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableMulticastFlooding() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(Short port){
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        try{
            logger.trace("disableMulticastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " multicast disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler disableMulticastFlooding(port) err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(){
        try{
            logger.trace("disableUnknownFlooding()...");
            if(expect.execute("config traffic control all unicast enable action drop threshold 0", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableUnknownFlooding() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(Short port){
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        try{
            logger.trace("disableUnknownFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableUnknownFlooding(port) err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceMacCheck(){
        //no such command in D-Link switch
        //logger.debug("CLIHandler: disableSourceMacCheck(): begin");
        /*try{
            expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableSourceMacCheck() err: {}", e);
        }*/
        expect.close();
        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceMacCheck(Short port){
        //no such command in D-Link switch

        //logger.debug("CLIHandler: disableSourceMacCheck(port {}): begin", port);
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/      
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        expect.close();
        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceLearning(){
        try{
            logger.trace("disableSourceLearning()...");
            if(expect.execute("config ports all learning disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableSourceLearning() err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceLearning(Short port){
        if(!isValidPort(port)){
            /*try{
                expect.execute("\r\n", "#", "#");//before call expect.close(), because expect is in state of looking forward an 'expect', so call an empty expect.execute() on purpose.
            }catch(Exception e){
                expect.close();
                logger.error("CLIHandler.disableSourceMacCheck(port {}) err: {}", port, e);
            }*/
            expect.close();
            return new Status(StatusCode.INTERNALERROR);
        }

        try{
            logger.trace("disableSourceLearning(port {})...", port);
            if(expect.execute("config ports " + port + "-" + port + " learning disable", "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            expect.close();
            logger.error("CLIHandler.disableSourceLearning(port) err: {}", e);
        }
        expect.close();
        return new Status(StatusCode.INTERNALERROR);
    }

    public void closeExpect(){//TODO: in CLIHandlerTest.java's after(), calls this closeExpect() to ensure ExpectHandler close the Expect4j. But actually, I had call expect.close() in every functions in this code, why needs an additional closeExpect()? I suspect there's somewhere didn't close ExpectHandler in certain functions...
        expect.close();
    }
/*
    public void readFlowRequest(Flow flow, Long sw_macAddr){
        logger.trace("enter CLIHandler.readFlowRequest()");

        logger.trace("retrieving the metrics in the Flow...");

    //retrieve match fields from the flow...
        Match match = flow.getMatch();

        //src/dest mac
        MatchField fieldDlSrc= match.getField(MatchType.DL_SRC);
        String srcMac = (fieldDlSrc == null)? null : HexString.toHexString((byte[])fieldDlSrc.getValue());
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        String destMac = (fieldDlDest == null)? null : HexString.toHexString((byte[])fieldDlDest.getValue());

        //vlan
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = (fieldVlan == null)? null : ((Short)(fieldVlan.getValue())).shortValue();

        //ethernet_type 
        MatchField fieldEthType= match.getField(MatchType.DL_TYPE);
        short ethType = (fieldEthType == null)? null : ((Short)fieldNwSrc.getValue()).shortValue();

        //network protocol
        MatchField fieldNwProtocol= match.getField(MatchType.NW_PROTO);
        int nwProtocol = (fieldNwProtocol == null)? null : ((byte)(fieldNwProtocol.getValue())).intValue();//actually not int ~ value range is just 0~255

        //src/dest IP address
        MatchField fieldNwSrc= match.getField(MatchType.NW_SRC);
        String srcIP = (fieldNwSrc == null)? null : ((InetAddress)(fieldNwSrc.getValue())).getHostAddress();
        MatchField fieldNwDest= match.getField(MatchType.NW_DST);
        String destIP = (fieldNwDest == null)? null : ((InetAddress)(fieldNwDest.getValue())).getHostAddress();

        //src/dest IP port
        MatchField fieldSrcPort= match.getField(MatchType.TP_SRC);
        short srcPort = (fieldSrcPort == null)? null : ((Short)(fieldSrcPort.getValue())).shortValue();
        MatchField fieldDestPort= match.getField(MatchType.TP_DST);
        short destPort = (fieldDestPort == null)? null : ((Short)(fieldDestPort.getValue())).shortValue();
    }
*/
    public void addFlow(String command){
        
    }

    public Status createAclProfile(Long nodeId, Integer profileId, String profileName, AclLayer layer, Short vlanMask, String srcIpMask, String dstIpMask){

        //"parameters checking" (reuse code form AclServiceImpl.createAclProfile())
        if(nodeId == null){
            logger.debug("ERROR: createAclProfile(): given nodeId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given nodeId is null");
        }
        if(profileId == null){
            logger.debug("ERROR: createAclProfile(): given profileId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given profileId is null");
        }
        if(profileName == null){
            logger.debug("ERROR: createAclProfile(): given profileName is null (profileId {})", profileId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given profileName is null (profileId " + profileId + ")");
        }
        if(layer == null){
            logger.debug("ERROR: createAclProfile(): given layer is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given layer is null");
        }
        /*if(vlanMask == null){
            logger.debug("ERROR: createAclProfile(): given vlanMask is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given vlanMask is null");
        }
        if(srcIpMask == null){
            logger.debug("ERROR: createAclProfile(): given srcIpMask is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given srcIpMask is null");
        }
        
        if(dstIpMask == null){
            logger.debug("ERROR: createAclProfile(): given dstIpMask is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given dstIpMask is null");
        }*/
        if(layer == AclLayer.ETHERNET){
            if(vlanMask == null){
                logger.debug("ERROR: createAclProfile(): layer is ethernet, but given vlanId is null");
                return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): layer is ethernet, but given vlanId is null");
            }
        }
        if(layer == AclLayer.IP){
            if(vlanMask == null && srcIpMask == null && dstIpMask == null){
                logger.debug("ERROR: createAclProfile(): layer is IP, but given all of the vlanMask, srcIpMask, dstIpMask, are null");
                return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): layer is IP, but given all of the vlanMask, srcIpMask, dstIpMask, are null");
            }
        }
        
        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: createAclProfile(): given invalid nodeId {}", nodeId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given invalid nodeId " + nodeId);
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: createAclProfile(): given invalid profileId {}", profileId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given invalid profileId " + profileId);
        }
        if(vlanMask != null){
            if(vlanMask < 0 || vlanMask > 0xffff){//TODO: vlanMask valid range?
                logger.debug("ERROR: createAclProfile(): given invalid vlanMask {}", vlanMask);
                return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given invalid vlanMask " + vlanMask);
            }
        }
        //end of "parameters checking" (reuse code form AclServiceImpl.createAclProfile())


        String cmdStr = "";

        cmdStr += "create access_profile";
        cmdStr += " profile_id " + profileId;
        cmdStr += " profile_name " + profileName;

        if(layer == AclLayer.ETHERNET)
            cmdStr += " ethernet ";
        if(layer == AclLayer.IP)
            cmdStr += " ip ";
        if(layer == AclLayer.ETHERNET){
            String vlanMaskStr = shortTo4CharHexString(vlanMask);
            if(vlanMaskStr == null){
                logger.debug("ERROR: createAclProfile(): given vlanMask {} can't be converted to valid hexstring", vlanMask);
                return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given vlanMask " + vlanMask + " can't be converted to valid hexstring");
            }
            cmdStr += " vlan " + vlanMaskStr;
        }
        if(layer == AclLayer.IP){
            if(vlanMask != null){
                String vlanMaskStr = shortTo4CharHexString(vlanMask);
                if(vlanMaskStr == null){
                    logger.debug("ERROR: createAclProfile(): given vlanMask {} can't be converted to valid hexstring", vlanMask);
                    return new Status(StatusCode.BADREQUEST, "CLIHandler: createAclProfile(): given vlanMask " + vlanMask + " can't be converted to valid hexstring");
                }
                cmdStr += " vlan " + vlanMaskStr;
            }
            if(srcIpMask != null){
                cmdStr += " source_ip_mask " + srcIpMask;
            }
            if(dstIpMask != null){
                cmdStr += " destination_ip_mask " + dstIpMask;
            }
        }

        try{
            if(expect.execute(cmdStr, "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: createAclProfile(): ExpectHandler.execute() fail (exception: {}), the command string: \n{}\n", e1, cmdStr);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: createAclProfile(): with command string ('" + cmdStr + "'), ExpectHandler execution err: " + e1);
        }
        expect.close();
        logger.debug("ERROR: createAclProfile(): ExpectHandler.execute() fail, the command string: \n{}\n", cmdStr);
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: createAclProfile(): with command string ('" + cmdStr + "'), ExpectHandler.execute() fail");
    }


    public Status setAclRule(Long nodeId, Integer profileId, String profileName, Integer ruleId, String ruleName, List<Short> portList, AclLayer layer, Integer vlanId, String srcIp, String dstIp, AclAction action){

        //parameters checking (copy from AclServiceImpl.setAclRule())
        //TODO: the following code is reused by delAclRule(). If code change, may reflect to delAclRule().
        if(nodeId == null){
            logger.debug("ERROR: setAclRule(): given nodeId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given nodeId is null");
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: setAclRule(): given profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given profileId and profileName are null");
        }
        if(ruleId == null && ruleName == null){
            logger.debug("ERROR: setAclRule(): given ruleId and ruleName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given ruleId and ruleName are null");
        }
        if(portList == null){
            logger.debug("ERROR: setAclRule(): given portList is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given portList is null");
        }
        if(layer == null){
            logger.debug("ERROR: setAclRule(): given layer is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given layer is null");
        }
        if(action == null){
            logger.debug("ERROR: setAclRule(): given action is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given action is null");
        }
        if(layer == AclLayer.ETHERNET){
            if(vlanId == null){
                logger.debug("ERROR: setAclRule(): layer is ethernet, but given vlanId is null");
                return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): layer is ethernet, but given vlanId is null");
            }
        }
        if(layer == AclLayer.IP){
            if(vlanId == null && srcIp == null && dstIp == null){
                logger.debug("ERROR: setAclRule(): layer is IP, but given all of the vlanId, srcIp, dstIp, are null");
                return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): layer is IP, but given all of the vlanId, srcIp, dstIp, are null");
            }
        }
        
        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: setAclRule(): given invalid nodeId {}", nodeId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given invalid nodeId " + nodeId);
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: setAclRule(): given invalid profileId {}", profileId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given invalid profileId " + profileId);
        }
        if(ruleId != null){
            if(ruleId < 0){//TODO: profileId valid range?
                logger.debug("ERROR: setAclRule(): given invalid ruleId {} and ruleName are null", ruleId);
                return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given invalid ruleId " + ruleId + " and ruleName are null");
            }
        }
        for(Short port : portList){
            if(port < 0 || port > 32){//TODO: port range
                logger.debug("ERROR: setAclRule(): given invalid port {} in portList", nodeId);
                return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given invalid port " + nodeId + " in portList");
            }
        }
        if(vlanId != null){
            if(!isValidVlan(vlanId)){
                logger.debug("ERROR: setAclRule(): given invalid vlanId {}", vlanId);
                return new Status(StatusCode.BADREQUEST, "CLIHandler.setAclRule(): given invalid vlanId " + vlanId);
            }
        }
        //end of parameters checking


        String cmdStr = "";

        cmdStr += "config access_profile";

        //profileId / profileName
        if(profileId != null)
            cmdStr += " profile_id " + profileId;
        else if(profileName != null)
            cmdStr += " profile_name " + profileName;
        else{
            logger.error("ERROR: setAclRule(): both profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: setAclRule(): both profileId and profileName are null");
        }

        //ruleId / ruleName
        if(ruleId != null)
            cmdStr += " add access_id " + ruleId;
        else if(ruleName != null)
            ;//d-link has no ruleName
        else{
            logger.error("ERROR: setAclRule(): both ruleId and ruleName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: setAclRule(): both ruleId and ruleName are null");
        }

        //layer
        if(layer == AclLayer.ETHERNET)
            cmdStr += " ethernet ";
        if(layer == AclLayer.IP)
            cmdStr += " ip ";

        //layer of Ethernet
        if(layer == AclLayer.ETHERNET)
            cmdStr += " vlan_id " + vlanId;

        //layer of IP
        if(layer == AclLayer.IP){
            if(vlanId != null){
                cmdStr += " vlan_id " + vlanId;
            }
            if(srcIp != null){
                cmdStr += " source_ip " + srcIp;
            }
            if(dstIp != null){
                cmdStr += " destination_ip " + dstIp;
            }
        }

        //port list
        String portListStr = "";
        for(Short port : portList){
            portListStr += "," + port;
        }
        if(portListStr.charAt(0) == ',')
            portListStr = portListStr.substring(1, portListStr.length());
        cmdStr += " port " + portListStr;

        //action (permit /deny)
        if(action == AclAction.PERMIT)
            cmdStr += " permit";
        if(action == AclAction.DENY)
            cmdStr += " deny";

        //Execute command string
        try{
            if(expect.execute(cmdStr, "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: setAclRule(): ExpectHandler.execute() fail (exception: {}), the command string: \n{}\n", e1, cmdStr);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: setAclRule(): with command string ('" + cmdStr + "'), ExpectHandler execution err: " + e1);
        }
        expect.close();
        logger.debug("ERROR: setAclRule(): ExpectHandler.execute() fail, the command string: \n{}\n", cmdStr);
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: setAclRule(): with command string ('" + cmdStr + "), ExpectHandler.execute() fail");
    }

    public Status delAclRule(Long nodeId, Integer profileId, String profileName, Integer ruleId, String ruleName){
        //parameters checking (copy from setAclRule())
        if(nodeId == null){
            logger.debug("ERROR: delAclRule(): given nodeId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given nodeId is null");
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: delAclRule(): given profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given profileId and profileName are null");
        }
        if(ruleId == null && ruleName == null){
            logger.debug("ERROR: delAclRule(): given ruleId and ruleName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given ruleId and ruleName are null");
        }
        
        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: delAclRule(): given invalid nodeId {}", nodeId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given invalid nodeId " + nodeId);
        }
        if(profileId < 0){//TODO: profileId valid range?
            logger.debug("ERROR: delAclRule(): given invalid profileId {}", profileId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given invalid profileId " + profileId);
        }
        if(ruleId != null){
            if(ruleId < 0){//TODO: profileId valid range?
                logger.debug("ERROR: delAclRule(): given invalid ruleId {} and ruleName are null", ruleId);
                return new Status(StatusCode.BADREQUEST, "CLIHandler.delAclRule(): given invalid ruleId " + ruleId + " and ruleName are null");
            }
        }
        //end of parameters checking


        String cmdStr = "";

        cmdStr += "config access_profile";
        //TODO: agree with this? if profileId and profileName both have value, currently we use profileId, ignore profileName
        if(profileId != null)
            cmdStr += " profile_id " + profileId;
        else if(profileName != null)
            cmdStr += " profile_name " + profileName;
        else{
            logger.error("ERROR: delAclRule(): both profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclRule(): both profileId and profileName are null");
        }
        if(ruleId != null)
            cmdStr += " delete access_id " + ruleId;
        else{
            logger.error("ERROR: delAclRule(): ruleId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclRule(): ruleId is null");
        }

        try{
            if(expect.execute(cmdStr, "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: delAclRule(): ExpectHandler.execute() fail (exception: {}), the command string: \n{}\n", e1, cmdStr);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: delAclRule(): with command string ('" + cmdStr + "'), ExpectHandler execution err: " + e1);
        }
        expect.close();
        logger.debug("ERROR: delAclRule(): ExpectHandler.execute() fail, the command string: \n{}\n", cmdStr);
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: delAclRule(): with command string ('" + cmdStr + "), ExpectHandler.execute() fail");
    }

    public Status delAclProfile(Long nodeId, Integer profileId, String profileName){
        //parameters checking (reuse code form createAclProfile())
        if(nodeId == null){
            logger.debug("ERROR: delAclProfile(): given nodeId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclProfile(): given nodeId is null");
        }
        if(profileId == null && profileName == null){
            logger.debug("ERROR: delAclProfile(): given both profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclProfile(): given both profileId and profileName are null");
        }
        
        //checking parameters valid?
        if(nodeId < 0){
            logger.debug("ERROR: delAclProfile(): given invalid nodeId {}", nodeId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclProfile(): given invalid nodeId " + nodeId);
        }
        if(profileId != null){
            if(profileId < 0){//TODO: profileId valid range?
                logger.debug("ERROR: delAclProfile(): given invalid profileId {}", profileId);
                return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclProfile(): given invalid profileId " + profileId);
            }
        }
        //end of parameters checking


        String cmdStr = "";

        cmdStr += "delete access_profile";
        //TODO: agree with this? if profileId and profileName both have value, currently we use profileId, ignore profileName
        if(profileId != null)
            cmdStr += " profile_id " + profileId;
        else if(profileName != null)
            cmdStr += " profile_name " + profileName;
        else{
            logger.error("ERROR: delAclProfile(): both profileId and profileName are null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: delAclProfile(): both profileId and profileName are null");
        }

        try{
            if(expect.execute(cmdStr, "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: delAclProfile(): ExpectHandler.execute() fail (exception: {}), the command string: \n{}\n", e1, cmdStr);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: delAclProfile(): with command string ('" + cmdStr + "'), ExpectHandler execution err: " + e1);
        }
        expect.close();
        logger.debug("ERROR: delAclProfile(): ExpectHandler.execute() fail, the command string: \n{}\n", cmdStr);
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: delAclProfile(): with command string ('" + cmdStr + "), ExpectHandler.execute() fail");
    }

    public Status clearAclTable(Long nodeId){
        //parameters checking (reuse code form createAclProfile())
        if(nodeId == null){
            logger.debug("ERROR: clearAclTable(): given nodeId is null");
            return new Status(StatusCode.BADREQUEST, "CLIHandler: clearAclTable(): given nodeId is null");
        }
        if(nodeId < 0){
            logger.debug("ERROR: clearAclTable(): given invalid nodeId {}", nodeId);
            return new Status(StatusCode.BADREQUEST, "CLIHandler: clearAclTable(): given invalid nodeId " + nodeId);
        }


        String cmdStr = "delete access_profile all";

        try{
            if(expect.execute(cmdStr, "#", "Success")){
                expect.close();
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: clearAclTable(): ExpectHandler.execute() fail (exception: {}), the command string: \n{}\n", e1, cmdStr);
            return new Status(StatusCode.INTERNALERROR, "CLIHandler: clearAclTable(): with command string ('" + cmdStr + "'), ExpectHandler execution err: " + e1);
        }
        expect.close();
        logger.debug("ERROR: clearAclTable(): ExpectHandler.execute() fail, the command string: \n{}\n", cmdStr);
        return new Status(StatusCode.INTERNALERROR, "CLIHandler: clearAclTable(): with command string ('" + cmdStr + "), ExpectHandler.execute() fail");
    }

    //Deprecated
    //this method is used by AclService.getAclIndexList(). But AclService.getAclIndexList() is deprecated now.
    public List<ACLIndex> getAclIndexList(Long nodeId){
        if(nodeId == null){
            logger.debug("ERROR: getAclIndexList(): given nodeId is null");
            return null;
        }
        if(nodeId < 0){
            logger.debug("ERROR: getAclIndexList(): given invalid nodeId {}", nodeId);
            return null;
        }

        String text = null;
        try{
            text = expect.executeAndGetMultiplePageText("show access_profile", " Next Entry ", "a", "#");
        }catch(Exception e1){
            expect.close();
            logger.debug("ERROR: getAclIndexList(): call expect.executeAndGetMultiplePageText() fail for node: {}\n\tCommand: 'show access_profile'\n\tExpect string 1: ' Next Entry '\n\tExpect string 2: '#'\nException: {}", nodeId, e1);
            return null;
        }
        if(text == null){
            expect.close();
            logger.debug("ERROR: getAclIndexList(): call expect.executeAndGetMultiplePageText() fail for node: {}\n\tCommand: 'show access_profile'\n\tExpect string 1: ' Next Entry '\n\tExpect string 2: '#'", nodeId);
            return null;
        }
        expect.close();
        logger.debug("getAclIndexList(): get text as below:\n" + text);

        List<ACLIndex> ret = parseAclTableTextToIndexList(text);

        return ret;
    }

    private boolean isValidPort(Short port){
        if(port.shortValue() <= 0 || port.shortValue() > 32)//TODO: valid port range is hardware-dependent
            return false;
        else
            return true;
    }

    private boolean isValidVlan(Integer vlanId){
        if(vlanId < 1 || vlanId > 4095)//TODO: valid vlan range?
            return false;
        else
            return true;
    }

    private String shortTo4CharHexString(Short value){
        return shortTo4CharHexString(value.shortValue());
    }
    private String shortTo4CharHexString(short value){
        String str = HexString.toHexString((long)value);//str would be "xx:xx:xx:xx:xx:xx:xx:xx" (8 segments)
        if(str.length() != 2*8 + 7)//the length of string from HexString.toHexString() should be length of 2*8 + 7
            return null;
        str = str.substring(str.length() - 5, str.length());//str would be "xx:yy" (only 2 segments)
        str = str.substring(0, 2) + str.substring(3, 5);//str would be "xxyy"

        str = "0x" + str;
        return str;
    }

    private List<ACLIndex> parseAclTableTextToIndexList(String text){
        if(text == ""){//empty acl
            return new ArrayList<ACLIndex>();
        }

        //for locating 'Profile'
        int len = new String("Profile ID:").length();
        int p1 = -1;
        int p2 = -1;
        int pNextProfile = -1;

        //for locating 'Rule'
        int lenR = new String("Rule ID :").length();
        int pr1 = -1;
        int pr2 = -1;
        
        List<ACLIndex> retList = new ArrayList<ACLIndex>();
        do{
            //processing Profile ID
            if(text.indexOf("Profile ID:", p1) < 0)
                break;
            p1 = text.indexOf("Profile ID:", p1) + len;
            p2 = text.indexOf("Profile name:", p1);
            System.out.println("p1=" + p1 + ", p2=" + p2);
            int profileId = Integer.parseInt(text.substring(p1, p2).trim());
            pNextProfile = text.indexOf("Profile ID:", p1);
            System.out.println("pNextProfile=" + pNextProfile);
            if(pNextProfile < 0){
                ACLIndex index = new ACLIndex();
                index.profileId = profileId;
                retList.add(index);
                logger.debug("AclIndexList: add: <profileId {}, ruleId null>", profileId);
                break;
            }
            if(profileId == 33)//d-link special: even already delete all acl profile, there's still a profile which is id 33
                break;

            //processing Rule ID
            pr1 = p1;
            while(pr1 < pNextProfile){
                if(text.indexOf("Rule ID :", pr1) < 0)
                    break;
                pr1 = text.indexOf("Rule ID :", pr1) + lenR;
                pr2 = text.indexOf("Ports", pr1);
                System.out.println("pr1=" + pr1 + ", pr2=" + pr2);
                int ruleId = Integer.parseInt(text.substring(pr1, pr2).trim());
                if(pr1 >0 && pr1 < pNextProfile){
                    ACLIndex index = new ACLIndex();
                    index.profileId = profileId;
                    index.ruleId = ruleId;
                    retList.add(index);
                    logger.debug("AclIndexList: add: <profileId {}, ruleId {}>", profileId, ruleId);
                }
                else
                    break;
            }
        }while(pNextProfile > 0);

        return retList;
    }

}

