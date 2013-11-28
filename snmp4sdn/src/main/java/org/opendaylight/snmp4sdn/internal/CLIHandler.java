/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

import org.opendaylight.controller.sal.utils.Status;//s4s
import org.opendaylight.controller.sal.utils.StatusCode;//s4s

import org.expect4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class CLIHandler{
    Socket socket;
    Expect4j expect;
    long expectTimeout = 2 * 1000; //set Expect4j's default timeout as 2 seconds
    boolean isLoggedIn = false;

    public static void main(String args[]){
        new CLIHandler("10.217.0.32", "admin", "password");//("10.215.0.32");
    }

    public CLIHandler(String sw_ipAddr, String username, String password){
        try{
            socket = new Socket(sw_ipAddr, 23);
            System.out.println("(telnet to switch successfully, then login)");
            isLoggedIn = loginCLI(sw_ipAddr, username, password);
            //System.out.println("1:" + expect.printBuffer());
        }catch(Exception e){
            System.out.println("CLIHandler.CLIHandler() err:" + e);
        }
    }

    private boolean loginCLI(String sw_ipAddr, String username, String password){
        try{
            if(socket.isClosed()){
                socket = new Socket(sw_ipAddr, 23);
                System.out.println("(telnet to switch successfully, then login)");
            }
            expect = new Expect4j(socket);
            expect.setDefaultTimeout(expectTimeout);

            System.out.println("expecting--Username:");
            expect.expect("UserName:");//d-link:UserName  Acctioin:"Username "
            System.out.println("1:" + expect.printBuffer());
            expect.send(username + "\r\n");
            System.out.println("expecting--Password:");
            expect.expect("PassWord:");//d-link:PassWord  Acctioin:"Password "
            System.out.println("2:" + expect.printBuffer());
            expect.send(password + "\r\n");
            System.out.println("expecting--#");
            expect.expect("#");
            if(expect.printBuffer().endsWith("#"))
                return true;//login successfully
        }catch(Exception e){
            System.out.println("CLIHandler.loginCLI() err:" + e);
        }
        return false;//login fail
    }

    private void loginoutCLI(String sw_ipAddr, String username, String password){
        try{
            expect.send("logout\r\n");
        }catch(Exception e){
            System.out.println("CLIHandler.loginoutCLI() err:" + e);
        }
    }

    private String printACL(String sw_ipAddr, String username, String password){
        String str = null;
        try{
            expect.send("show access_profile profile_id 1\r\n");
            expect.send("a\r\n");
            //System.out.println("8:" + expect.printBuffer());
            expect.expect("#");
            System.out.println("3:" + expect.printBuffer());
        }catch(Exception e){
            System.out.println("CLIHandler.printACL() err:" + e);
        }
        return str;
    }
    
    public void reboot(String sw_ipAddr, String username, String password){
        try{
            System.out.println("expecting--reboot(y/n)?");
            expect.expect("system reboot?(y/n)");
            expect.send("y\r\n");
            System.out.println("sent 'y' to the question above");
            //return Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.reboot() err:" + e);
        }
        //return Status(StatusCode.INTERNALERROR);
    }

    public Status disableSTP(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableSTP...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("disable stp\r\n");
            expect.expect("#");
            System.out.println("4:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableSTP() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableBpduFlooding()...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("config stp fbpdu disable\r\n");
            expect.expect("#");
            System.out.println("5:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBpduFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBpduFlooding(String sw_ipAddr, short port, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableBpduFlooding(port " + port +" )...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            //expect.send("config stp ports " + port + "-" + port + " fbpdu disable\r\n");
            String sendStr = new String("config stp ports " + port + "-" + port + " fbpdu disable\r\n");
            expect.send(sendStr);
            expect.expect("#");
            System.out.println("6:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBpduFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableBroadcastFlooding()...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("config traffic control all broadcast disable\r\n");
            expect.expect("#");
            System.out.println("7:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBroadcastFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableBroadcastFlooding(String sw_ipAddr, short port, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableBroadcastFlooding(port " + port + ")...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            //expect.send("config traffic control " + port + "-" + port + " broadcast disable\r\n");
            String sendStr = new String("config traffic control " + port + "-" + port + " broadcast disable\r\n");
            expect.send(sendStr);
            expect.expect("#");
            System.out.println("8:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableBroadcastFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableMulticastFlooding()...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("config traffic control all multicast disable\r\n");
            expect.expect("#");
            System.out.println("9:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableMulticastFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableMulticastFlooding(String sw_ipAddr, short port, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableMulticastFlooding(port " + port +" )...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            //expect.send("config traffic control " + port + "-" + port + " multicast disable\r\n");
            String sendStr = new String("config traffic control " + port + "-" + port + " multicast disable\r\n");
            expect.send(sendStr);
            expect.expect("#");
            System.out.println("10:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler disableMulticastFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableUnknownFlooding()...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("config traffic control all unicast enable action drop threshold 0\r\n");
            expect.expect("#");
            System.out.println("11:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableUnknownFlooding() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableUnknownFlooding(String sw_ipAddr, short port, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableUnknownFlooding(port " + port +" )...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            //expect.send("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0\r\n");
            String sendStr = new String("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0\r\n");
            expect.send(sendStr);
            expect.expect("#");
            System.out.println("12:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableUnknownFlooding(port) err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceMacCheck(String sw_ipAddr, String username, String password){
        //no such command in D-Link switch
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceMacCheck(String sw_ipAddr, short port, String username, String password){
        //no such command in D-Link switch
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        return new Status(StatusCode.SUCCESS);
    }

    public Status disableSourceLearning(String sw_ipAddr, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableSourceLearning()...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            expect.send("config ports all learning disable\r\n");
            expect.expect("#");
            System.out.println("13:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            System.out.println("CLIHandler.disableSourceLearning() err:" + e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public Status disableSourceLearning(String sw_ipAddr, short port, String username, String password){
        if(!isLoggedIn) return new Status(StatusCode.INTERNALERROR);

        try{
            System.out.println("disableSourceLearning(port " + port +" )...");
            if(socket.isClosed()) loginCLI(sw_ipAddr, username, password);
            //expect.send("config ports " + port + "-" + port + " learning disable\r\n");
            String sendStr = new String("config ports " + port + "-" + port + " learning disable\r\n");
            expect.send(sendStr);
            expect.expect("#");
            System.out.println("14:" + expect.printBuffer());
            if(expect.printBuffer().indexOf("Success") >= 0)
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

