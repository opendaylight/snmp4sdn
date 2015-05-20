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
public class ExpectTelnetTest /*extends TestCase*/ {
    //private static final Logger logger = LoggerFactory.getLogger(Expect4j.class);
    public ExpectTelnetTest() {
        //super(testName);
        try{
            testTelnetRounds();
        }catch(Exception e1){
            System.out.println("ERROR: ExpectTelnetTest(): call testTelnetRounds() fail, error: " + e1);
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
        for(int i = 0; i < 10; i++){
            System.out.println("round " + i);
            try{
                testTelnet();
            }catch(Exception e1){
                System.out.println("ERROR: testTelnetRounds(): call testTelnet() fail, error: " + e1);
            }
            //goSleep();
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
        
        expect.expect( new Match[] {
            new GlobMatch("UserName:"/*"login: "*/, new Closure() {
                public void run(ExpectState state) {printGotStringAndBuffer("UserName", state);
                    try { expect.send(username + "\r\n"); } catch(Exception e) { /*Expect4j.log.warning*//*logger.info*/System.out.println(e.getMessage() ); }
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
                public void run(ExpectState state) {printGotStringAndBuffer("PassWord", state);
                    try { expect.send(password + "\r\n");} catch(Exception e) { /*Expect4j.log.warning*//*logger.info*/System.out.println(e.getMessage() ); }
                    state.addVar("sentPassword", Boolean.TRUE);
                    state.exp_continue();
                }
            }),
            /*new RegExpMatch("@" + hostname + "\\]", new Closure() {*/
            new GlobMatch("admin#", new Closure() {
                public void run(ExpectState state) {printGotStringAndBuffer("admin#", state);
                    /*Expect4j.log.warning*//*logger.info*/System.out.println("Holy crap, this actually worked");
                    state.addVar("sentExit", Boolean.TRUE);
                    try { expect.send("exit\r\n"); } catch(Exception e) { }
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
                    /*Expect4j.log.warning*//*logger.info*/System.out.println(":-( Timeout");
                }
            })
        });
        
        expect.close();
        
        ExpectState lastState = expect.getLastState();
        
        Boolean result = (Boolean) lastState.getVar("sentUsername");
        assertNotNull( result , "sentUsername");
        
        result = (Boolean) lastState.getVar("sentPassword");
        assertNotNull( result , "sentPassword");
        
        /*result = (Boolean) lastState.getVar("gotLogin");
        assertNotNull( result );*/

        result = (Boolean) lastState.getVar("sentExit");
        assertNotNull( result , "sentExit");
        
        //result = (Boolean) lastState.getVar("gotEOF");
        //assertNotNull( result );
    }

    private void assertNotNull(Boolean result, String failCase){
        if(result == null){
            System.out.println(failCase + " fails");
            System.exit(0);
        }
    }

    private void printGotStringAndBuffer(String expStr, ExpectState state) {
        System.out.println("got '" + expStr + "', full buffer: ");
        System.out.println("---start-----------");
        System.out.println(state.getBuffer());
        System.out.println("---end-----------");
    }

    public static void main(String args[]){
        new ExpectTelnetTest();
    }
}
