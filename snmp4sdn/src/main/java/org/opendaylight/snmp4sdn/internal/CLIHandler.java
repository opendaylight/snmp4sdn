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
    ExpectHandler expect;
    String sw_ipAddr, username, password, prompt = "#";

    public CLIHandler(String sw_ipAddr, String username, String password){
        this.sw_ipAddr = new String(sw_ipAddr);
        this.username = new String(username);
        this.password = new String(password);
        try{
            expect = new ExpectHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);//d-link:UserName,PassWord  Accton:Username, Passwrod
        }catch(Exception e){
            System.out.println("CLIHandler() err:" + e);
        }
    }

    private String printACL(){
        String str = null;
        /*try{
            expect.send("show access_profile profile_id 1\r\n");
            expect.send("a\r\n");
            expect.expect("#");
            System.out.println("3:" + expect.getBuffer());
        }catch(Exception e){
            System.out.println("CLIHandler.printACL() err:" + e);
        }*///TODO:expectHandler that can call multiple send() is not ready
        return str;
    }
    
    public Status reboot(){
        try{
            System.out.println("expecting--reboot(y/n)?");
            if(expect.execute_2step_end("reboot", "system reboot?(y/n)", "y")){
                System.out.println("sent 'y' to the question above");
                return new Status(StatusCode.SUCCESS);
            }
        }catch(Exception e){
            System.out.println("CLIHandler.reboot() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSTP(){
        try{
            System.out.println("disableSTP...");
            if(expect.execute("disable stp", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableSTP() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(){
        try{
            System.out.println("disableBpduFlooding()...");
            if(expect.execute("config stp fbpdu disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBpduFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(Short port){
        try{
            System.out.println("disableBpduFlooding(port " + port +" )...");
            if(expect.execute("config stp ports " + port + "-" + port + " fbpdu disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBpduFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(){
        try{
            System.out.println("disableBroadcastFlooding()...");
            if(expect.execute("config traffic control all broadcast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBroadcastFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(Short port){
        try{
            System.out.println("disableBroadcastFlooding(port " + port + ")...");
            if(expect.execute("config traffic control " + port + "-" + port + " broadcast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBroadcastFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(){
        try{
            System.out.println("disableMulticastFlooding()...");
            if(expect.execute("config traffic control all multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableMulticastFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(Short port){
        try{
            System.out.println("disableMulticastFlooding(port " + port +" )...");
            if(expect.execute("config traffic control " + port + "-" + port + " multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler disableMulticastFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(){
        try{
            System.out.println("disableUnknownFlooding()...");
            if(expect.execute("config traffic control all unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableUnknownFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(Short port){
        try{
            System.out.println("disableUnknownFlooding(port " + port +" )...");
            if(expect.execute("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableUnknownFlooding(port) err:" + e);
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
            System.out.println("disableSourceLearning()...");
            if(expect.execute("config ports all learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableSourceLearning() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceLearning(Short port){
        try{
            System.out.println("disableSourceLearning(port " + port +" )...");
            if(expect.execute("config ports " + port + "-" + port + " learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableSourceLearning(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

/*
    public void readFlowRequest(Flow flow, Long sw_macAddr){
        System.out.println("enter CLIHandler.readFlowRequest()");

        System.out.println("retrieving the metrics in the Flow...");

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
}

