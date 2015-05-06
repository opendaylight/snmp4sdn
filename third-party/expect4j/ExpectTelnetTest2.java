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
public class ExpectTelnetTest2 /*extends TestCase*/ {
    //private static final Logger logger = LoggerFactory.getLogger(Expect4j.class);
    public ExpectTelnetTest2() {
        //super(testName);
        try{
            testTelnetRounds();
        }catch(Exception e1){
            System.out.println("ERROR: ExpectTelnetTest2(): call testTelnetRounds() fail, error: " + e1);
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
                testTelnet();
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
    /*public*/private void testTelnet() throws Exception {
        System.out.println("testTelnet");
        
        String hostname = "192.168.0.33";
        final String username = "admin";
        final String password = "password";
        
        if( hostname.equals("hostname") ) return; // fill in hostname, username,password.
        
        final Expect4j expect = ExpectUtils.telnet(hostname, 23);
        expect.setDefaultTimeout(Expect4j.TIMEOUT_FOREVER/*10*1000*/);

        ExpectState lastState;
        Boolean result;

        expect.expect( new Match[] {
            new GlobMatch("UserName:"/*"login: "*/, new Closure() {
                public void run(ExpectState state) {System.out.println("'UserName:' got!");
                    try { expect.send(username + "\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }
                    state.addVar("sentUsername", Boolean.TRUE);
                    //state.exp_continue();
                }
            }),
            new TimeoutMatch(new Closure() {
                public void run(ExpectState state) {
                    System.out.println(":-( Timeout for 'UserName:'");
                    System.exit(0);
                }
            })
        });
        lastState = expect.getLastState();
        result = (Boolean) lastState.getVar("sentUsername");
        assertNotNull( result , "sentUsername");
            /*new GlobMatch("Last login: ", new Closure() {
                public void run(ExpectState state) {
                    // This match should prevent Last login: from being recognized by the above match
                    state.addVar("gotLogin", Boolean.TRUE);
                    state.exp_continue();
                }
            }),*/
        expect.expect( new Match[] {
            new GlobMatch("PassWord:", new Closure() {
                public void run(ExpectState state) {System.out.println("'PassWord:' got!");
                    try { expect.send(password + "\r\n");} catch(Exception e) { System.out.println(e.getMessage() ); }
                    state.addVar("sentPassword", Boolean.TRUE);
                    //state.exp_continue();
                }
            }),
            new TimeoutMatch(new Closure() {
                public void run(ExpectState state) {
                    System.out.println(":-( Timeout for 'PassWord:'");
                    System.exit(0);
                }
            })
        });
        lastState = expect.getLastState();
        result = (Boolean) lastState.getVar("sentPassword");
        assertNotNull( result , "sentPassword");
        expect.expect( new Match[] {
            //new RegExpMatch("@" + hostname + "\\]", new Closure() {
            new GlobMatch("admin#", new Closure() {
                public void run(ExpectState state) {System.out.println("'admin#' got!");
                    System.out.println("Holy crap, this actually worked");
                    try { expect.send("exit\r\n"); } catch(Exception e) { System.out.println(e.getMessage() ); }
                    state.addVar("sentExit", Boolean.TRUE);
                }
            }),
            new TimeoutMatch(new Closure() {
                public void run(ExpectState state) {
                    System.out.println(":-( Timeout for 'admin#'");
                    System.exit(0);
                }
            })
        });
        lastState = expect.getLastState();
        result = (Boolean) lastState.getVar("sentExit");
        assertNotNull( result , "sentExit");
            /*
            new EofMatch(new Closure() {
                public void run(ExpectState state) {
                    // suck up everything until EOF
                    state.addVar("gotEOF", Boolean.TRUE);
                    expect.log.warning("EOF");
                }
            }),
            */
        
        expect.close();
        
        
        
        /*result = (Boolean) lastState.getVar("gotLogin");
        assertNotNull( result );*/

        
        
        //result = (Boolean) lastState.getVar("gotEOF");
        //assertNotNull( result );
    }

    private void assertNotNull(Boolean result, String failCase){
        if(result == null){
            System.out.println(failCase + " fails");
            System.exit(0);
        }
    }

    public static void main(String args[]){
        new ExpectTelnetTest2();
    }
}
