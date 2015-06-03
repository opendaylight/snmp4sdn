/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;//s4s

//Unit tests with expect4j enabled are successful, but currently loading expect4j in OSGi has problem,
//so here use a dummy (ExpectDummy, at bottom) to replace expec4j.
//The lines marked with "//e4j" and "//e4j-dummy" are for switching to use expect4j or the dummy

/*
* Note for usage of Expect4j:
*
* 1. There must be expect.send("\r\n") at the end of a series of expect/send. (For example there is an expect.send("\r\n") at the end of the loginCLI(), though seems unnecessary.)
* 2. A series of expect/send must begin with 'expect', not 'send'. (For example there is an expect.expect(cli_prompt) in the begining of the sendAndExpect(), though seems unnecessary and even afraid there's no cli_prompt. But currently only this way is workable.)
* 3. Proofing 1. is a must: there is expect.send("\r\n") at the end of sendAndExpect(), though seems unnecessary. But if expect.send("\r\n") is removed, program can't work correctly when there are two ConfigService functions one after one.
* 4. Proofing 2. is a must: if without 'expect' at the beginning, program can't run
* 5. 1 and 2 are also proved in the ExpectTelnetTest.java
* My conclusion: expect runs in pairs of expect/send, so, since in the loginCLI() it is beginned with 'expect', so consequently then 'send' -> 'expect' -> 'send' ... Thus result in 1. and 2.
*
*
* Other observation:
* 1. The whole program speed: connection by ExpectUtils.telnet() is faster than Expect4j(socket)
*/

import org.expect4j.*;//e4j
import org.expect4j.matches.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;

public class ExpectHandler{
    private static Logger logger = LoggerFactory.getLogger(ExpectHandler.class);
    Socket socket;
    Expect4j expect;//e4j
    //ExpectDummy expect;//e4j-dummy
    long expectTimeout = 10 * 1000; //set Expect4j's default timeout as 2 seconds
    
    String sw_ipAddr, username_prompt, password_prompt, username, password, cli_prompt = "admin#";

    boolean isDummy = false;//junit test. for testing without real Ethernet switch: when isDummy is true, ExpectHandler always answer 'sucessful' for any request

    boolean isLoggedIn = false;
    private static int RETRY_NUMBER = 5;

    public ExpectHandler(String sw_ipAddr, String username_prompt, String password_prompt, 
                                            String username, String password) throws Exception{
        if(isDummy)return;

        this.sw_ipAddr = new String(sw_ipAddr);
        this.username_prompt = new String(username_prompt);
        this.password_prompt = new String(password_prompt);
        this.username = new String(username);
        this.password = new String(password);

        try{
            Socket socket = new Socket(sw_ipAddr, 23);
            expect = new Expect4j(socket);

            //expect = ExpectUtils.telnet(sw_ipAddr, 23);
            /*using ExpectUtils.telnet(), may occur the error in run time, as below:

                at org.apache.commons.net.telnet.TelnetClient._closeOutputStream(TelnetClient.java:86)
                at org.apache.commons.net.telnet.TelnetOutputStream.close(TelnetOutputStream.java:155)
                at org.apache.commons.net.telnet.TelnetClient.disconnect(TelnetClient.java:127)
                at org.expect4j.ExpectUtils$5.close(ExpectUtils.java:213)
                at org.expect4j.BlockingConsumer.run(BlockingConsumer.java:136)
                at java.lang.Thread.run(Thread.java:744)
            */
        }catch(Exception e1){
            logger.debug("ERROR: ExpectHandler(): call ExpectUtils.telnet({}, port 23) error", sw_ipAddr);
            return;
        }
        if(expect == null){
            logger.debug("ERROR: ExpectHandler(): call ExpectUtils.telnet({}, port 23) fail", sw_ipAddr);
            return;
        }
        expect.setDefaultTimeout(expectTimeout/*Expect4j.TIMEOUT_FOREVER*/);

        loginCLI();
    }

    public boolean isLoggedIn(){
        return this.isLoggedIn;
    }

    public boolean loginCLI(){
        ExpectState expState ;

        //wait for username_prompt
        try{ expect.expect(username_prompt);} catch(Exception e1) { logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, occurs exception: {}", username_prompt, e1); return false;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {}, get null ExpectState", username_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", username_prompt, sw_ipAddr);
            return false;
        }

        //send username
        try { expect.send(username + "\r\n"); } catch(Exception e1) { logger.debug("ERROR: loginCLI(): expect.send({}) to switch {}, occurs exception: {}", username, sw_ipAddr, e1); return false;}

        //wait for password_prompt
        try{ expect.expect(password_prompt);} catch(Exception e1) { logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {}, get null ExpectState", password_prompt, sw_ipAddr); return false;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {}, get null ExpectState", password_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", password_prompt, sw_ipAddr);
            return false;
        }

        //send password
        try { expect.send(password + "\r\n"); } catch(Exception e1) { logger.debug("ERROR: loginCLI(): expect.send({}) to switch {}, occurs exception: {}", password, sw_ipAddr, e1); return false;}

        //wait for CLI prompt
        try { expect.expect(cli_prompt); } catch(Exception e1) { logger.debug("ERROR: loginCLI(): expecting for '{} from switch {}, occurs exception: {}", cli_prompt, sw_ipAddr, e1); return false;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return false;
        }
        try { expect.send("\r\n"); } catch(Exception e1) { logger.debug("ERROR: loginCLI(): expect.send(\r\n) occurs exception: {}", e1); return false;}
        //TODO: without send "\r\n" at end, will EOF!Why?

        //login successfully
        logger.debug("Login to {} Successfully!", sw_ipAddr);
        isLoggedIn = true;
        return true;
    }

    public void close(){
        try{
            expect.close();
        }catch(Exception e1){
            logger.debug("ERROR: close(): call expect.close() fail: {}", e1);
        }
    }

    //for executing CLI command
    //  sendStr: the command string
    //  expStr: expect string (usually the CLI console prompt)
    //  findStr: check if findStr is in the content shown after sendStr is executed
    public boolean execute(String sendStr, String expStr, String findStr) throws Exception{
        if(isDummy)return true;

        if(sendAndExpect(sendStr, expStr) < 0){
            logger.debug("ERROR: execute(): sendAndExpect() fails\n\tsendStr '{}'\n\texpStr '{}'", sendStr, expStr);
            return false;//TODO: retry if fail?
        }

        boolean found = isStringinResult(findStr);
        if(!found)
            logger.debug("ERROR: execute(): fail to find '{}', for which \n\tsendStr '{}'\n\texpStr '{}'", findStr, sendStr, expStr);

        return found;
    }

    //for executing CLI command that needs 2 steps
    public boolean execute_2step_end(String sendStr, String expStr, String sendStr2) throws Exception{
        if(isDummy)return true;

        if(sendAndExpect(sendStr, expStr) < 0)
            return false;
        else
            return sendAndEnd(sendStr2);
    }

    /*** This method is for CLIHandler.getAclIndexList() to get ACL printout text. Now CLIHandler.getAclIndexList() is deprecated ***/
    //TODO: this method can handle multiple pages, but can't handle single page
    //for executing CLI command and return the screen text
    //  sendStr: the command string
    //  expStr1: expect string (usually the continue hint)
    //  sendStr2: the string to continue
    //  expStr2: expect string (usually the CLI console prompt)
    public String executeAndGetMultiplePageText(String sendStr, String expStr1, String sendStr2, String expStr2) throws Exception{
        if(isDummy)return "test, dummy text";

        int index = -1;

        ExpectState expState;

        //wait for cli_prompt
        try{ expect.expect(cli_prompt);} catch(Exception e1) { logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' occurs exception: {}", cli_prompt, e1); return null;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return null;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return null;
        }
        logger.trace("ExpectHandler: executeAndGetMultiplePageText(): firstly, expected '{}'", cli_prompt);

        //send sendStr
        logger.trace("ExpectHandler: executeAndGetMultiplePageText(): send '{}'", sendStr);
        try{expect.send(sendStr + "\r\n");}catch(Exception e1) { logger.debug("ERROR: executeAndGetMultiplePageText(): expect.send('{}' + \\r\\n) occurs exception: {}", sendStr, e1); return null;}
        logger.trace("ExpectHandler: executeAndGetMultiplePageText(): after sending '{}', expect '{}'", sendStr, expStr1);

        //wait for expStr1
        try{index = expect.expect(expStr1);}catch(Exception e1) { logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' occurs exception: {}", expStr1, e1); return null;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch, get null ExpectState", expStr1, sw_ipAddr);
            return null;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch {} fail", expStr1, sw_ipAddr);
            return null;
        }

        //append to the result text
        String page1Text = expState.getBuffer();
        String retText = new String(page1Text);

        //send the string to continue
        try { expect.send(sendStr2); } catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expect.send('{}' + \\r\\n) occurs exception: {}", sendStr2, e1); return null;}

        //wait for expStr2
        try{index = expect.expect(expStr2);}catch(Exception e1) { logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' occurs exception: {}", expStr2, e1); return null;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch, get null ExpectState", expStr2, sw_ipAddr);
            return null;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: executeAndGetMultiplePageText(): expecting for '{}' from switch {} fail", expStr2, sw_ipAddr);
            return null;
        }

        //sent successfully
        try { expect.send("\r\n"); } catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expect.send('{}' + \\r\\n) occurs exception: {}", sendStr2, e1); return null;}

        //append to the result text
        String page2Text = expState.getBuffer();
        retText += new String(page2Text);

        //logger.trace("executeAndGetMultiplePageText():\n\tsendStr '{}'\n\texpStr1 '{}'\n\tsendStr2 '{}'\n\texpStr2 '{}'\nReturn text '{}'", sendStr, expStr1, sendStr2, expStr2, retText);
        return retText;
    }

    public int sendAndExpect(String sendStr) throws Exception{
        if(isDummy)return 1;
                
        return sendAndExpect(sendStr, cli_prompt);
    }

    public int sendAndExpect(String sendStr, String expStr) throws Exception{
        if(isDummy)return 1;
        if(!isLoggedIn) return -1;

        int index = -1;

        ExpectState expState;

        //wait for cli_prompt
        try{ expect.expect(cli_prompt);} catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expecting for '{}' occurs exception: {}", cli_prompt, e1); return -1;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: sendAndExpect(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return -1;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: sendAndExpect(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return -1;
        }
        logger.trace("ExpectHandler: sendAndExpect(): firstly, expected '{}'", cli_prompt);

        //send sendStr
        logger.trace("ExpectHandler: sendAndExpect(): send '{}'", sendStr);
        try{expect.send(sendStr + "\r\n");}catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expect.send('{}' + \\r\\n) occurs exception: {}", sendStr, e1); return -1;}
        logger.trace("ExpectHandler: sendAndExpect(): after sending '{}', expect '{}'", sendStr, expStr);

        //wait for expStr
        try{index = expect.expect(expStr);}catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expecting for '{}' occurs exception: {}", expStr, e1); return -1;}
        expState = expect.getLastState();
        if(expState == null){
            logger.debug("ERROR: sendAndExpect(): expecting for '{}' from switch, get null ExpectState", expStr, sw_ipAddr);
            return -1;
        }
        if(expState.getBuffer() == null){
            logger.debug("ERROR: sendAndExpect(): expecting for '{}' from switch {} fail", expStr, sw_ipAddr);
            return -1;
        }

        //sent successfully
        try { expect.send("\r\n"); } catch(Exception e1) { logger.debug("ERROR: sendAndExpect(): expect.send('{}' + \\r\\n) occurs exception: {}", sendStr, e1); return -1;}
        //TODO: without send "\r\n" at end, will EOF!Why?
        logger.trace("ExpectHandler: sendAndExpect(): after sending '{}', expected '{}' at index{}", sendStr, expStr, index);
        return index;
    }

    public boolean sendAndEnd(String sendStr) throws Exception{
        if(isDummy)return true;
        if(!isLoggedIn) return false;
 
        expect.send(sendStr + "\r\n");
        return true;
    }

    private void goSleep(long milliseconds){
         try
        {
            Thread.sleep(milliseconds);
        }
        catch(Exception e1){
            logger.debug("ERROR: ExpectHandler: goSleep(): exception: " + e1);
        }
    }

    public boolean isLoggedInSwitch(){
        return isLoggedIn;
    }

    public boolean isStringinResult(String str) throws Exception{
        if(isDummy)return true;

        if(expect.getLastState().getBuffer().indexOf(str) >= 0)
            return true;
        else
            return false;
    }

    public String getBuffer(){
        if(isDummy)return null;
        String str;
        try{str = expect.getLastState().getBuffer();}catch(Exception e1) { logger.debug("ERROR: getBuffer(): try expect.getLastState().getBuffer() occurs exception: {}", e1); return null;}
        return str;
    }

    private void printExpectStringAndBuffer(String funcName, String expStr){
        System.out.println(funcName + ": after expecting \"" + expStr + "\", the buffer: ");
        System.out.println("---begin------");
        System.out.println(getBuffer());
        System.out.println("---end------");
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
