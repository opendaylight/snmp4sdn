package org.opendaylight.snmp4sdn.internal;

import org.expect4j.*;

import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CLIHandlerTest {
    protected static final Logger logger = LoggerFactory.getLogger(CLIHandlerTest.class);

    CLIHandler clih;
    String sw_ipAddr = "192.168.0.33", username = "admin" , password = "password", prompt = "#";

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() {
        clih.closeExpect();
    }

    public CLIHandlerTest(){
        logger.info("====== CLIHandlerTest() begin======");
        try{
            clih = new CLIHandler(sw_ipAddr, "UserName:", "PassWord:", username, password);
        }catch(Exception e1){
            logger.error("ERROR: CLIHandlerTest(): create ExpectHandler err: {}", e1);
            logger.error("ERROR: CLIHandlerTest(): usernamePrompt {}", "UserName:");
            logger.error("ERROR: CLIHandlerTest(): passwordPrompt {}", "PassWord:");
            logger.error("ERROR: CLIHandlerTest(): username {}", username);
            logger.error("ERROR: CLIHandlerTest(): password {}", password);
        }
    }

    @Test
    public void enableSTP(){
        logger.info("[To test: enableSTP()]");
        Status status = clih.enableSTP();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableSTP(){
        logger.info("[To test: disableSTP()]");
        Status status = clih.disableSTP();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBpduFlooding(){
        logger.info("[To test: disableBpduFlooding()]");
        Status status = clih.disableBpduFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBpduFloodingWithPort(){
       
    }

    @Test
    public void disableBroadcastFlooding(){
        logger.info("[To test: disableBroadcastFlooding()]");
        Status status = clih.disableBroadcastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBroadcastFloodingWithPort(){
    }

    @Test
    public void disableMulticastFlooding(){
        logger.info("[To test: disableMulticastFlooding()]");
        Status status = clih.disableMulticastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableMulticastFloodingWithPort(){
    }

    @Test
    public void disableUnknownFlooding(){
        logger.info("[To test: disableUnknownFlooding()]");
        Status status = clih.disableUnknownFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableUnknownFloodingWithPort(){
    }

    @Test
    public void disableSourceMacCheck(){
        //no such command in D-Link switch
        Assert.assertTrue(true);
    }

    @Test
    public void disableSourceMacCheckWithPort(){
    }


    @Test
    public void disableSourceLearning(){
        logger.info("[To test: disableSourceLearning()]");
        Status status = clih.disableSourceLearning();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableSourceLearningWithPort(){
    }




    //The following are just a copy for the tests above (and rename with adding '2' at tail), this is to increase the number of testing to evaluate expect4j's stability

    @Test
    public void enableSTP2(){
        logger.info("[To test: enableSTP()]");
        Status status = clih.enableSTP();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableSTP2(){
        logger.info("[To test: disableSTP()]");
        Status status = clih.disableSTP();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBpduFlooding2(){
        logger.info("[To test: disableBpduFlooding()]");
        Status status = clih.disableBpduFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBpduFloodingWithPort2(){
       
    }

    @Test
    public void disableBroadcastFlooding2(){
        logger.info("[To test: disableBroadcastFlooding()]");
        Status status = clih.disableBroadcastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableBroadcastFloodingWithPort2(){
    }

    @Test
    public void disableMulticastFlooding2(){
        logger.info("[To test: disableMulticastFlooding()]");
        Status status = clih.disableMulticastFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableMulticastFloodingWithPort2(){
    }

    @Test
    public void disableUnknownFlooding2(){
        logger.info("[To test: disableUnknownFlooding()]");
        Status status = clih.disableUnknownFlooding();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableUnknownFloodingWithPort2(){
    }

    @Test
    public void disableSourceMacCheck2(){
        //no such command in D-Link switch
        Assert.assertTrue(true);
    }

    @Test
    public void disableSourceMacCheckWithPort2(){
    }


    @Test
    public void disableSourceLearning2(){
        logger.info("[To test: disableSourceLearning()]");
        Status status = clih.disableSourceLearning();
        Assert.assertEquals(StatusCode.SUCCESS.toString(), status.getCode().toString());
    }

    @Test
    public void disableSourceLearningWithPort2(){
    }

}
