/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

import org.opendaylight.snmp4sdn.internal.ExpectHandler;//s4s

import org.opendaylight.controller.sal.utils.Status;//s4s
import org.opendaylight.controller.sal.utils.StatusCode;//s4s

import org.opendaylight.snmp4sdn.IConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class CLIHandler{
    private static final Logger logger = LoggerFactory
            .getLogger(CLIHandler.class);

    ExpectHandler expect;
    String sw_ipAddr, username, password, prompt = "#";

    public CLIHandler(String sw_ipAddr, String username, String password){
        this.sw_ipAddr = new String(sw_ipAddr);
        this.username = new String(username);
        this.password = new String(password);
        try{
            expect = new ExpectHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);//d-link:UserName,PassWord  Accton:Username, Passwrod
        }catch(Exception e){
            logger.error("CLIHandler() err:" + e);
        }
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
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            logger.error("CLIHandler.reboot() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSTP(){
        try{
            logger.trace("disableSTP...");
            if(expect.execute("disable stp", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableSTP() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(){
        try{
            logger.trace("disableBpduFlooding()...");
            if(expect.execute("config stp fbpdu disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableBpduFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(Short port){
        try{
            logger.trace("disableBpduFlooding(port {})...", port);
            if(expect.execute("config stp ports " + port + "-" + port + " fbpdu disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableBpduFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(){
        try{
            logger.trace("disableBroadcastFlooding()...");
            if(expect.execute("config traffic control all broadcast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableBroadcastFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(Short port){
        try{
            logger.trace("disableBroadcastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " broadcast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableBroadcastFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(){
        try{
            logger.trace("disableMulticastFlooding()...");
            if(expect.execute("config traffic control all multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableMulticastFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(Short port){
        try{
            logger.trace("disableMulticastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler disableMulticastFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(){
        try{
            logger.trace("disableUnknownFlooding()...");
            if(expect.execute("config traffic control all unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableUnknownFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(Short port){
        try{
            logger.trace("disableUnknownFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableUnknownFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceMacCheck(){
        //no such command in D-Link switch
        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceMacCheck(Short port){
        //no such command in D-Link switch
        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceLearning(){
        try{
            logger.trace("disableSourceLearning()...");
            if(expect.execute("config ports all learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableSourceLearning() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceLearning(Short port){
        try{
            logger.trace("disableSourceLearning(port {})...", port);
            if(expect.execute("config ports " + port + "-" + port + " learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableSourceLearning(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void addFlow(String command){
        
    }
}

