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

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 *
 * @author justin
 */
public class ExpectTelnetTest3 /*extends TestCase*/ {
    //private static final Logger logger = LoggerFactory.getLogger(Expect4j.class);
    public ExpectTelnetTest3() {
        //super(testName);
        try{
            testTelnetRounds();
        }catch(Exception e1){
            System.out.println("ERROR: ExpectTelnetTest3(): call testTelnetRounds() fail, error: " + e1);
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
        
        String hostname = "192.168.0.34";
        final String username = "admin";
        final String password = "password";
        
        if( hostname.equals("hostname") ) return false; // fill in hostname, username,password.
        
        Expect4j expect = ExpectUtils.telnet(hostname, 23);
        expect.setDefaultTimeout(Expect4j.TIMEOUT_FOREVER/*10*1000*/);

        ExpectState expState;
        
        String sw_ipAddr, username_prompt="UserName:", password_prompt="PassWord:", cli_prompt = "admin#";

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
        try { expect.send("\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }
        //TODO: without send "\r\n" at end, will EOF!

        //login successfully
        System.out.println("Login Successfully!");
        
        
        expect.close();
        return true;
        
    }

    private void assertNotNull(Boolean result, String failCase){
        if(result == null){
            System.out.println(failCase + " fails");
            System.exit(0);
        }
    }

    public static void main(String args[]){
        new ExpectTelnetTest3();
    }
}
