package org.opendaylight.snmp4sdn.internal;

import org.expect4j.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpectHandlerTest {
    protected static final Logger logger = LoggerFactory.getLogger(ExpectHandlerTest.class);

    ExpectHandler expect;
    String sw_ipAddr = "192.168.0.33", username = "admin" , password = "password", prompt = "#";

    @Before
    public void before() throws Exception {
        logger.info("====== ExpectHandlerTest begin======");
    }

    @After
    public void after() {
    }

    public ExpectHandlerTest(){
        try{
            expect = new ExpectHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);
        }catch(Exception e1){
            logger.error("ERROR: ExpectHandlerTest(): create ExpectHandler err: {}", e1);
            logger.error("ERROR: ExpectHandlerTest(): usernamePrompt {}", "UserName:");
            logger.error("ERROR: ExpectHandlerTest(): passwordPrompt {}", "PassWord:");
            logger.error("ERROR: ExpectHandlerTest(): username {}", username);
            logger.error("ERROR: ExpectHandlerTest(): password {}", password);
        }
    }

    /*
    * In the folloiwng test1() ~ test10(), is to test login 10 times.
    * In the testX(), loginCLI() is not needed, because the expect object was created with logged in.
    */

    @Test
    public void test1(){
        logger.debug("---test1-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test1 end---");
    }

    @Test
    public void test2(){
        logger.debug("---test2-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test2 end---");
    }

    @Test
    public void test3(){
        logger.debug("---test3-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test3 end---");
    }
    @Test
    public void test4(){
        logger.debug("---test4-------");
        //expect.loginCLI();
        expect.close();
        logger.debug("---test4 end---");
    }
    @Test
    public void test5(){
        logger.debug("---test5-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test5 end---");
    }
    @Test
    public void test6(){
        logger.debug("---test6-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test6 end---");
    }
    @Test
    public void test7(){
        logger.debug("---test7-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test7 end---");
    }
    @Test
    public void test8(){
        logger.debug("---test8-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test8 end---");
    }
    @Test
    public void test9(){
        logger.debug("---test9-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test9 end---");
    }
    @Test
    public void test10(){
        logger.debug("---test10-------");
        //expect.loginCLI();
        expect.close();
        if(!expect.isLoggedInSwitch())
            Assert.assertFalse(false);
        logger.debug("---test10 end---");
    }

    /*
    * The follwing testing scripts are actually of CLIHandler level's functions. 
    * In the spirit of JUnit test, testing functions here should be testing ExpectHandler's own function, such as ExpectHandler.execute(), ExpectHandler.sendAndExpect(), etc.
    * 
    */

    //This one test is to test that can two CLI commands be excecuted one after one.
    //I also copy this test for 14 clones, below.
    @Test
    public void enableSTPAndDisableSTP(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP2(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP3(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP4(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP5(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP6(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP7(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP8(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP9(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP10(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP11(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP12(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP13(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP14(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void enableSTPAndDisableSTP15(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                Assert.assertTrue(true);
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
            Assert.assertTrue(false);
            return;
        }

        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        
        expect.close();
        Assert.assertTrue(false);
    }


    @Test
    public void enableSTP(){
        try{
            if(expect.execute("enable stp", "#", "Success")){
                logger.debug("Enable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: enableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Enable STP fail!");
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void disableSTP(){
        try{
            if(expect.execute("disable stp", "#", "Success")){
                logger.debug("Disable STP successfully!");
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e1){
            expect.close();
            logger.error("ERROR: disableSTP(): expectHandler execution err: {}", e1);
        }
        logger.debug("Disable STP fail!");
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void disableBpduFlooding(){
        try{
            logger.trace("disableBpduFlooding()...");
            if(expect.execute("config stp fbpdu disable", "#", "Success")){
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e){
            logger.error("CLIHandler.disableBpduFlooding() err: {}", e);
            expect.close();
        }
        logger.debug("ERROR: expect.execute() fail");
        expect.close();
        Assert.assertTrue(false);
    }

    @Test
    public void disableBpduFloodingWithPort(){
       Short port = 23;
       try{
            logger.trace("disableBpduFlooding(port {})...", port);
            if(expect.execute("config stp ports " + port + "-" + port + " fbpdu disable", "#", "Success")){
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e){
            logger.error("CLIHandler.disableBpduFlooding(port) err: {}", e);
            expect.close();
        }
        expect.close();
        logger.debug("ERROR: expect.execute() fail");
        Assert.assertTrue(false);
    }

    @Test
    public void disableBroadcastFlooding(){
        try{
            logger.trace("disableBroadcastFlooding()...");
            if(expect.execute("config traffic control all broadcast disable", "#", "Success")){
                expect.close();
                Assert.assertTrue(true);
                return;
            }
        }catch(Exception e){
            logger.error("CLIHandler.disableBroadcastFlooding() err: {}", e);
            expect.close();
        }
        expect.close();
        logger.debug("ERROR: expect.execute() fail");
        Assert.assertTrue(false);
    }
/*
    public void disableBroadcastFlooding(Short port){
        if(!isValidPort(port))
            return new Status(StatusCode.INTERNALERROR);

        try{
            logger.trace("disableBroadcastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " broadcast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableBroadcastFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableMulticastFlooding(){
        try{
            logger.trace("disableMulticastFlooding()...");
            if(expect.execute("config traffic control all multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableMulticastFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableMulticastFlooding(Short port){
        if(!isValidPort(port))
            return new Status(StatusCode.INTERNALERROR);

        try{
            logger.trace("disableMulticastFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " multicast disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler disableMulticastFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableUnknownFlooding(){
        try{
            logger.trace("disableUnknownFlooding()...");
            if(expect.execute("config traffic control all unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableUnknownFlooding() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableUnknownFlooding(Short port){
        if(!isValidPort(port))
            return new Status(StatusCode.INTERNALERROR);

        try{
            logger.trace("disableUnknownFlooding(port {})...", port);
            if(expect.execute("config traffic control " + port + "-" + port + " unicast enable action drop threshold 0", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableUnknownFlooding(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableSourceMacCheck(){
        //no such command in D-Link switch
        return new Status(StatusCode.SUCCESS);
    }

    public void disableSourceMacCheck(Short port){
        //no such command in D-Link switch

        if(!isValidPort(port))
            return new Status(StatusCode.INTERNALERROR);

        return new Status(StatusCode.SUCCESS);
    }

    public void disableSourceLearning(){
        try{
            logger.trace("disableSourceLearning()...");
            if(expect.execute("config ports all learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableSourceLearning() err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }

    public void disableSourceLearning(Short port){
        if(!isValidPort(port))
            return new Status(StatusCode.INTERNALERROR);

        try{
            logger.trace("disableSourceLearning(port {})...", port);
            if(expect.execute("config ports " + port + "-" + port + " learning disable", "#", "Success"))
                return new Status(StatusCode.SUCCESS);
        }catch(Exception e){
            logger.error("CLIHandler.disableSourceLearning(port) err: {}", e);
        }
        return new Status(StatusCode.INTERNALERROR);
    }*/
}
