/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

//Unit tests with expect4j enabled are successful, but currently loading expect4j in OSGi has problem,
//so here use a dummy (ExpectDummy, at bottom) to replace expec4j.
//The lines marked with "//e4j" and "//e4j-dummy" are for switching to use expect4j or the dummy

//import org.expect4j.*;//e4j
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class ExpectHandler{
    private static Logger logger = LoggerFactory.getLogger(ExpectHandler.class);
    Socket socket;
    //Expect4j expect;//e4j
    ExpectDummy expect;//e4j-dummy
    long expectTimeout = 2 * 1000; //set Expect4j's default timeout as 2 seconds
    
    String sw_ipAddr, username_prompt, password_prompt, username, password, prompt;

    boolean isDummy = false;//for testing without real Ethernet switch: when isDummy is true, ExpectHandler always answer 'sucessful' for any request

    public ExpectHandler(String sw_ipAddr, String username_prompt, String password_prompt, 
                                            String username, String password) throws Exception{
        if(isDummy)return;

        this.sw_ipAddr = new String(sw_ipAddr);
        this.username_prompt = new String(username_prompt);
        this.password_prompt = new String(password_prompt);
        this.username = new String(username);
        this.password = new String(password);

        socket = new Socket(sw_ipAddr, 23);
        if(!socket.isClosed())logger.info("(telnet to switch " + sw_ipAddr + " successful)");
        else logger.info("(telnet to switch " + sw_ipAddr + " fail)");
        boolean isLoggedIn = loginCLI();
    }

    private boolean loginCLI() throws Exception{
        if(socket.isClosed()){
            socket = new Socket(sw_ipAddr, 23);
            if(socket.isClosed()){
                logger.trace("(telnet to switch " + sw_ipAddr + " fail)");
                return false;
            }
            else
                logger.trace("(telnet to switch " + sw_ipAddr + " successfully)");
        }
        //expect = new Expect4j(socket);//e4j
        expect = new ExpectDummy(socket);//e4j-dummy
        if(expect.getClass().getName().equals(
                    "org.opendaylight.snmp4sdn.internal.util.ExpectHandler$ExpectDummy"))
            isDummy = true;//e4j-dummy
        expect.setDefaultTimeout(expectTimeout);

        logger.trace("expecting--" + username_prompt);
        expect.expect(username_prompt);
        logger.trace("1:" + expect.getLastState().getBuffer());
        expect.send(username + "\r\n");
        logger.trace("expecting--" + password_prompt);
        expect.expect(password_prompt);//d-link:PassWord  Accton:"Password "
        logger.trace("2:" + expect.getLastState().getBuffer());
        expect.send(password + "\r\n");
        logger.trace("login().expecting--#");
        expect.expect("#");
        if(expect.getLastState().getBuffer().endsWith("#")){
            logger.trace("(login successfully to " + sw_ipAddr +")");
            return true;//login successfully
        }
        return false;//login fail
    }

    //for executing CLI command
    //  sendStr: the command string
    //  expStr: expect string (usually the CLI console prompt)
    //  findStr: check if findStr is in the content shown after sendStr is executed
    public boolean execute(String sendStr, String expStr, String findStr) throws Exception{
        if(isDummy)return true;

        if(sendAndExpect(sendStr, expStr) < 0)
            return false;//TODO: retry if fail?
        return isStringinResult(findStr);
    }

    //for executing CLI command that needs 2 steps
    public boolean execute_2step_end(String sendStr, String expStr, String sendStr2) throws Exception{
        if(isDummy)return true;

        if(sendAndExpect(sendStr, expStr) < 0)
            return false;
        else
            return sendAndEnd(sendStr2);
    }

    public int sendAndExpect(String sendStr) throws Exception{
        if(isDummy)return 1;
                
        return sendAndExpect(sendStr, prompt);
    }

    public int sendAndExpect(String sendStr, String expStr) throws Exception{
        if(isDummy)return 1;
        
        int index = -1;
        if(socket.isClosed()){
            logger.trace("not logged in, try login again");
            loginCLI();
        }
        expect.send(sendStr + "\r\n");
        index = expect.expect(expStr);
        return index;
    }

    public boolean sendAndEnd(String sendStr) throws Exception{
        if(isDummy)return true;
        
        if(socket.isClosed()){
            logger.trace("not logged in, try login again");
            loginCLI();
        }
        expect.send(sendStr + "\r\n");
        return true;
    }

    public boolean isStringinResult(String str) throws Exception{
        if(isDummy)return true;

        if(expect.getLastState().getBuffer().indexOf(str) >= 0)
            return true;
        else
            return false;
    }

    public String getBuffer() throws Exception{
        if(isDummy)return null;
        
        return expect.getLastState().getBuffer();
    }

    private class ExpectDummy{
        public ExpectDummy(Socket socket){}
        public boolean setDefaultTimeout(Long time){return true;}
        public int expect(String str){return 0;}
        public void send(String str){}
        public ExpectDummy getLastState(){return new ExpectDummy(null);}
        public String getBuffer(){return "";}
    }
}
