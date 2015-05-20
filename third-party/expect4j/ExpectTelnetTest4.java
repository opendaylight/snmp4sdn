/*
 * ExpectUtilsTelnetTest.java
 * JUnit based test
 *
 * Created on March 16, 2007, 9:42 AM
 */

//package org.expect4j;

//import junit.framework.*;
import org.expect4j.*;
import org.expect4j.matches.*;

import java.net.Socket;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author justin
 */
public class ExpectTelnetTest4 /*extends TestCase*/ {
    //private static final Logger logger = LoggerFactory.getLogger(Expect4j.class);
    String hostname = "192.168.0.34";
    final String username = "admin";
    final String password = "password";
    String sw_ipAddr, username_prompt="UserName:", password_prompt="PassWord:", cli_prompt = "admin#";
    boolean isLoggedIn = false;

    public ExpectTelnetTest4() {
        //super(testName);
        try{
            testTelnetRounds();
        }catch(Exception e1){
            System.out.println("ERROR: ExpectTelnetTest4(): call testTelnetRounds() fail, error: " + e1);
        }
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /*public void testTelnetSwitch() throws Exception {
        
    }*/

    public void testTelnetRounds(){
        System.out.println("testTelnetRounds");
        System.out.println();
        for(int i = 0; i < 10000; i++){
            System.out.println("Round " + i);
            try{
                boolean ret = testTelnet();
                if(!ret)System.exit(0);
            }catch(Exception e1){
                System.out.println("ERROR: testTelnetRounds(): call testTelnet() fail, error: " + e1);
            }
            //goSleep();
            System.out.println();
        }
    }
    private void goSleep(){
         try
        {
            Thread.sleep(1000);
        }
        catch(Exception e1){
            //logger.info("ERROR: ExpectHandler: goSleep(): exception: " + e1);
            System.out.println("ERROR: ExpectHandler: goSleep(): exception: " + e1);
        }
    }
    /*public*/private boolean testTelnet() throws Exception {
        System.out.println("testTelnet");
        
        if( hostname.equals("hostname") ) return false; // fill in hostname, username,password.
        
        //Expect4j expect = ExpectUtils.telnet(hostname, 23);
        Socket socket = new Socket(hostname, 23);
        Expect4j expect = new Expect4j(socket);
        expect.setDefaultTimeout(Expect4j.TIMEOUT_FOREVER/*10*1000*/);

        if(loginCLI(expect)){
            if(executeCliCmd(expect)){
                expect.close();
                return true;
            }
        }
        expect.close();
        return false;
    }

    private boolean loginCLI(Expect4j expect){

        ExpectState expState;

        //wait for username_prompt
        try{ expect.expect(username_prompt);} catch(Exception e1) { System.out.println(e1.getMessage() ); return false;}System.out.println(expect.getLastState().getBuffer());
        expState = expect.getLastState();
        if(expState == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", username_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", username_prompt, sw_ipAddr);
            return false;
        }

        //send username
        try { expect.send(username + "\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }

        //wait for password_prompt
        try{ expect.expect(password_prompt);} catch(Exception e1) { System.out.println(e1.getMessage() ); return false;}System.out.println(expect.getLastState().getBuffer());
        expState = expect.getLastState();
        if(expState == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", password_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", password_prompt, sw_ipAddr);
            return false;
        }

        //send password
        try { expect.send(password + "\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }

        //wait for CLI prompt
        try { expect.expect(cli_prompt); } catch(Exception e1) { System.out.println(e1.getMessage() ); return false;}System.out.println(expect.getLastState().getBuffer());
        expState = expect.getLastState();
        if(expState == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return false;
        }
        
        //TODO: without send "\r\n" at end, will EOF!
        try { expect.send("\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }

        //login successfully
        System.out.println("Login Successfully!");
        isLoggedIn = true;
        return true;
    }

    private boolean executeCliCmd(Expect4j expect){
        if(!isLoggedIn) return false;

        int index = -1;

        ExpectState expState;
        
        //Send a CLI command
        
        //wait for CLI prompt
        try { expect.expect(cli_prompt); } catch(Exception e1) { System.out.println(e1.getMessage() ); return false;}System.out.println(expect.getLastState().getBuffer());
        expState = expect.getLastState();
        if(expState == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return false;
        }

        //send disable stp
        try { expect.send("disable stp" + "\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }

        //wait for CLI prompt
        try { index = expect.expect("#"/*cli_prompt*/); } catch(Exception e1) { System.out.println(e1.getMessage() ); return false;}System.out.println(expect.getLastState().getBuffer());
        expState = expect.getLastState();
        if(expState == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch, get null ExpectState", cli_prompt, sw_ipAddr);
            return false;
        }
        if(expState.getBuffer() == null){
            //logger.debug("ERROR: loginCLI(): expecting for '{}' from switch {} fail", cli_prompt, sw_ipAddr);
            return false;
        }

        //check 'Success' received
        if(expect.getLastState().getBuffer().indexOf("Success") < 0)
            return false;

        //TODO: without send "\r\n" at end, will EOF!
        try { expect.send("\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); return false;}

        //login successfully
        System.out.println("Disable STP Successfully!");
        
        return true;
        
    }

    private void assertNotNull(Boolean result, String failCase){
        if(result == null){
            System.out.println(failCase + " fails");
            System.exit(0);
        }
    }

    public static void main(String args[]){
        new ExpectTelnetTest4();
    }
}
