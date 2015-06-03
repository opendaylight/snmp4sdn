/*
 * ExpectUtilsTelnetTest.java
 * JUnit based test
 *
 * Created on March 16, 2007, 9:42 AM
 */

package org.expect4j;

import junit.framework.*;
import org.expect4j.matches.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author justin
 */
public class ExpectUtilsTelnetTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(Expect4j.class);
    public ExpectUtilsTelnetTest(String testName) {
        super(testName);
        testTelnetRounds();
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /*public void testTelnetSwitch() throws Exception {
        
    }*/

    public void testTelnetRounds() throws Exception {
        System.out.println("testTelnetRounds");
        for(int i = 0; i < 10; i++){
            System.out.println("round " + i);
            testTelnet();
            //goSleep();
        }
    }
    private void goSleep(){
         try
        {
            Thread.sleep(5000);
        }
        catch(Exception e1){
            logger.info("ERROR: ExpectHandler: goSleep(): exception: " + e1);
        }
    }
    /*public*/private void testTelnet() throws Exception {
        System.out.println("testTelnet");
        
        String hostname = "192.168.0.33";
        final String username = "admin";
        final String password = "password";
        
        if( hostname.equals("hostname") ) return; // fill in hostname, username,password.
        
        final Expect4j expect = ExpectUtils.telnet(hostname, 23);
        expect.setDefaultTimeout(/*Expect4j.TIMEOUT_FOREVER*/5000);
        
        expect.expect( new Match[] {
            new GlobMatch("UserName:"/*"login: "*/, new Closure() {
                public void run(ExpectState state) {
                    try { expect.send(username + "\r"); } catch(Exception e) { /*Expect4j.log.warning*/logger.info(e.getMessage() ); }
                    state.addVar("sentUsername", Boolean.TRUE);
                    state.exp_continue();
                }
            }),
            /*new GlobMatch("Last login: ", new Closure() {
                public void run(ExpectState state) {
                    // This match should prevent Last login: from being recognized by the above match
                    state.addVar("gotLogin", Boolean.TRUE);
                    state.exp_continue();
                }
            }),*/
            new GlobMatch("PassWord:", new Closure() {
                public void run(ExpectState state) {
                    try { expect.send(password + "\r");} catch(Exception e) { /*Expect4j.log.warning*/logger.info(e.getMessage() ); }
                    state.addVar("sentPassword", Boolean.TRUE);
                    state.exp_continue();
                }
            }),
            new RegExpMatch(/*"@" + hostname + "\\]"*/"admin#", new Closure() {
                public void run(ExpectState state) {
                    /*Expect4j.log.warning*/logger.info("Holy crap, this actually worked");
                    state.addVar("sentExit", Boolean.TRUE);
                    try { expect.send("exit\r"); } catch(Exception e) { }
                }
            }),
            /*
            new EofMatch(new Closure() {
                public void run(ExpectState state) {
                    // suck up everything until EOF
                    state.addVar("gotEOF", Boolean.TRUE);
                    expect.log.warning("EOF");
                }
            }),
            */
            new TimeoutMatch(new Closure() {
                public void run(ExpectState state) {
                    /*Expect4j.log.warning*/logger.info(":-( Timeout");
                }
            })
        });
        
        expect.close();
        
        ExpectState lastState = expect.getLastState();
        
        Boolean result = (Boolean) lastState.getVar("sentUsername");
        assertNotNull( result );
        
        result = (Boolean) lastState.getVar("sentPassword");
        assertNotNull( result );
        
        /*result = (Boolean) lastState.getVar("gotLogin");
        assertNotNull( result );*/

        result = (Boolean) lastState.getVar("sentExit");
        assertNotNull( result );
        
        //result = (Boolean) lastState.getVar("gotEOF");
        //assertNotNull( result );
    }

    public static void main(String args[]){
        new ExpectUtilsTelnetTest();
    }
}
