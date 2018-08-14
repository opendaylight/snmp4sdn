/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.snmp4sdn.ARPTableEntry;
//import org.opendaylight.snmp4sdn.SNMP4SDNErrorCode;
import org.opendaylight.snmp4sdn.STPPortState;
import org.opendaylight.snmp4sdn.core.IController;
//import org.opendaylight.snmp4sdn.IConfigService;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.internal.util.CommandInterpreter;
import org.opendaylight.snmp4sdn.internal.util.CommandProvider;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DeleteArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DeleteArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DeleteArpEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DisableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DisableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.DisableStpOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.EnableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.EnableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.EnableStpOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetArpTableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortRootInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortRootInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortRootOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortRootOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortStateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.GetStpPortStateOutputBuilder;
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.MiscConfigService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetArpEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetStpPortStateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.SetStpPortStateOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.StpPortState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.get.arp.table.output.ArpTableEntry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.miscconfig.rev151207.get.arp.table.output.ArpTableEntryBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscConfigServiceImpl implements /*IConfigService,*/MiscConfigService/*md-sal*/, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(MiscConfigServiceImpl.class);

    public boolean isDummy = false;

    Controller controller = null;
    CLIHandler cli = null;
    private CmethUtil cmethUtil = null;

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: setController(): cmethUtil is null");
        }
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    public void init() {//this method would not be called, when Activator.java adopt "new MiscConfigProvider()->new MiscConfigServiceImpl()"
        logger.debug("MiscConfigServiceImpl: init() is called");
        //registerWithOSGIConsole();
    }

    /*private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }*/

    public void TestCLI(){
        System.out.println("enter MiscConfigServiceImpl.TestCLI() 1");
        new CLIHandler("140.112.172.11", "telnet://ptt3.cc", "/q)", "new", "a");//notice to modify code: in ExpectHandler.java's loginCLI(), change logger.trace to logger.info, so that the string would be printed
        System.out.println("enter MiscConfigServiceImpl.TestCLI() 2");
    }

    /*@Override
    public Status disableSTP(Node node){
        return null;
    }*/

    //TODO: use SNMP4SDNErrorCode instead of Status so that md-sal API call can facilitate SNMP4SDNErrorCode to be the yang generated 'Result'
    public Status/*SNMP4SDNErrorCode*/ disableSTP(Node node/*long nodeId*/){
        if(node == null){
            logger.debug("ERROR: disableSTP(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: disableSTP(): null node");
        }
        long nodeId = ((Long)node.getID()).longValue();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableSTP(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSTP() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null){
            logger.debug("ERROR: disableSTP(): IP address of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.NOTFOUND, "MiscConfigServiceImpl: disableSTP(): IP address of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        if(username == null){
            logger.debug("ERROR: disableSTP(): CLI username of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSTP(): CLI username of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        if(password == null){
            logger.debug("ERROR: disableSTP(): CLI password of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSTP(): CLI password of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableSTP();
    }

    public Status/*SNMP4SDNErrorCode*/ enableSTP(Node node/*long nodeId*/){
        if(node == null){
            logger.debug("ERROR: enableSTP(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: enableSTP(): null node");
        }
        long nodeId = ((Long)node.getID()).longValue();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: enableSTP(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: enableSTP() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null){
            logger.debug("ERROR: enableSTP(): IP address of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.NOTFOUND, "MiscConfigServiceImpl: enableSTP(): IP address of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        if(username == null){
            logger.debug("ERROR: enableSTP(): CLI username of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: enableSTP(): CLI username of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        if(password == null){
            logger.debug("ERROR: enableSTP(): CLI password of node {} not in DB", HexString.toHexString(nodeId));
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: enableSTP(): CLI password of node " + HexString.toHexString(nodeId) + " not in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).enableSTP();
    }

   //@Override
   public Status disableBpduFlooding(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableBpduFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableBpduFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
    }

   //@Override
   public Status disableBpduFlooding(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableBpduFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableBpduFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((Short)nodeConnector.getID());
    }

    //@Override
    public Status disableBroadcastFlooding(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableBroadcastFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableBroadcastFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
    }

    //@Override
    public Status disableBroadcastFlooding(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableBroadcastFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableBroadcastFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((Short)nodeConnector.getID());
    }

    //@Override
    public Status disableMulticastFlooding(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableMulticastFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableMulticastFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
    }

   //@Override
   public Status disableMulticastFlooding(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableMulticastFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableMulticastFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((Short)nodeConnector.getID());
    }

   //@Override
   public Status disableUnknownFlooding(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableUnknownFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableUnknownFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
    }

   //@Override
   public Status disableUnknownFlooding(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableUnknownFlooding(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableUnknownFlooding() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((Short)nodeConnector.getID());
    }

   //@Override
   public Status disableSourceMacCheck(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableSourceMacCheck(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSourceMacCheck() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
    }

   //@Override
   public Status disableSourceMacCheck(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableSourceMacCheck(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSourceMacCheck() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((Short)nodeConnector.getID());
    }

   //@Override
   public Status disableSourceLearning(Node node){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableSourceLearning(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSourceLearning() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
    }

   //@Override
   public Status disableSourceLearning(Node node, NodeConnector nodeConnector){
        Long nodeId = (Long)node.getID();
        //CmethUtil cmethUtil = controller.getCmethUtil();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: disableSourceLearning(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: disableSourceLearning() with nodeId " + nodeId + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeId);
        String username = cmethUtil.getCliUsername(nodeId);
        String password = cmethUtil.getCliPassword(nodeId);
        if(sw_ipAddr == null) {
            return new Status(StatusCode.INTERNALERROR, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(username == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI username of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        if(password == null) {
            return new Status(StatusCode.INTERNALERROR, "CLI password of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        }
        return new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((Short)nodeConnector.getID());
    }

    public Status/*SNMP4SDNErrorCode*/ setSTPPortState (Node node/*long nodeID*/, short portNum, boolean isEnable){
        if(node == null){
            logger.debug("ERROR: setSTPPortState(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setSTPPortState(): null node");
        }
        long nodeID = ((Long)node.getID()).longValue();
        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: setSTPPortState(): call checkNodeIpValid() for node {} port {} fail", nodeID, portNum);
            return status;
        }
        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: setSTPPortState(): invalid port {}", portNum);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setSTPPortState(): invalid port " + portNum);
        }

        if(isDummy) {
            return /*SNMP4SDNErrorCode.SUCCESS*/new Status(StatusCode.SUCCESS);
        }

        status = new SNMPHandler(cmethUtil).setSTPPortState(nodeID, portNum, isEnable);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: setSTPPortState(): call SNMPHandler.setSTPPortState() with nodeID {} port {} isEnable {} fail ", nodeID, portNum, isEnable);
        }
        return status;
    }

    public STPPortState getSTPPortState (Node node/*long nodeID*/, short portNum){
        if(node == null){
            logger.debug("ERROR: getSTPPortState(): null node");
            return null;
        }
        long nodeID = ((Long)node.getID()).longValue();
        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: getSTPPortState(): call checkNodeIpValid() for node {} port {} fail", nodeID, portNum);
            return null;
        }
        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: getSTPPortState(): invalid port {}", portNum);
            return null;
        }

        if(isDummy) {
            return STPPortState.FORWARDING;
        }

        STPPortState state = new SNMPHandler(cmethUtil).getSTPPortState(nodeID, portNum);
        if(state == null){
            logger.debug("ERROR: getSTPPortState(): call SNMPHandler.getSTPPortState() with nodeID {} port {} fail", nodeID, portNum);
            return null;
        }
        return state;
    }

    public ARPTableEntry getARPEntry(Node node/*long nodeID*/, String ipAddress){
        if(node == null){
            logger.debug("ERROR: getARPEntry(): null node");
            return null;
        }
        long nodeID = ((Long)node.getID()).longValue();
        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: getARPEntry(): call checkNodeIpValid() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return null;
        }

        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: getARPEntry(): address translation for node {} for arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getARPEntry(): address translation for node {} for arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            return null;
        }

        if(isDummy){
            ARPTableEntry ret =new ARPTableEntry();
            ret.ipAddress = "1.1.1.1";
            ret.macAddress = HexString.toLong("12:34:56:78:AB:CD");
            return ret;
        }

        ARPTableEntry ret = new SNMPHandler(cmethUtil).getARPEntry(nodeID, ipAddress);
        if(ret == null){
            logger.debug("ERROR: getARPEntry(): call SNMPHandler.getARPEntry() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return null;
        }
        return ret;
    }

    public Status/*SNMP4SDNErrorCode*/ deleteARPEntry(Node node/*long nodeID*/, String ipAddress){
        if(node == null){
            logger.debug("ERROR: deleteARPEntry(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: deleteARPEntry(): null node");
        }
        long nodeID = ((Long)node.getID()).longValue();
        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: deleteARPEntry(): call checkNodeIpValid() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return status;
        }

        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: deleteARPEntry(): address translation for node {} for arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: deleteARPEntry(): address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: deleteARPEntry(): address translation for node {} for arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: deleteARPEntry(): address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e2);
        }

        if(isDummy) {
            return /*SNMP4SDNErrorCode.SUCCESS*/new Status(StatusCode.SUCCESS);
        }

        //SNMP4SDNErrorCode ret = new SNMPHandler(cmethUtil).deleteARPTableEntry(nodeID, ipAddress);
        Status ret = new SNMPHandler(cmethUtil).deleteARPTableEntry(nodeID, ipAddress);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            logger.debug("ERROR: deleteARPEntry(): call SNMPHandler.deleteARPEntry() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return new Status(ret.getCode(), "MiscConfigServiceImpl: deleteARPEntry(): call deleteARPTableEntry() with node " + nodeID + " for arp_entry_ip " + ipAddress + " fails: " + ret.getDescription());
        }
        return ret;
    }

    public Status/*SNMP4SDNErrorCode*/ setARPEntry(Node node/*long nodeID*/, ARPTableEntry entry){
        if(node == null){
            logger.debug("ERROR: setARPEntry(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setARPEntry(): null node");
        }
        long nodeID = ((Long)node.getID()).longValue();
        if(entry == null){
            logger.debug("ERROR: setARPEntry(): ARPTableEntry is null, for node {}", nodeID);
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setARPEntry: ARPTableEntry is null, for node " + nodeID);
        }

        String ipAddress = new String(entry.ipAddress);
        long macID = entry.macAddress;

        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: setARPEntry(): call checkNodeIpValid() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setARPEntry: call checkNodeIpValid() for node " + nodeID + " for entry ipAddress " + ipAddress + " fail");
        }

        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setARPEntry(): address translation for node {} for arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setARPEntry: address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPEntry(): address translation for node {} for arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: setARPEntry: address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e2);
        }

        if(isDummy) {
            return /*SNMP4SDNErrorCode.SUCCESS*/new Status(StatusCode.SUCCESS);
        }

        //SNMP4SDNErrorCode ret = new SNMPHandler(cmethUtil).setARPEntry(nodeID, ipAddress, macID);
        Status ret = new SNMPHandler(cmethUtil).setARPEntry(nodeID, ipAddress, macID);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: setARPEntry(): call SNMPHandler.deleteARPEntry() for node {} for entry ipAddress {} fail", nodeID, ipAddress);
            return new Status(ret.getCode(), "MiscConfigServiceImpl: setARPEntry(): call SNMPHandler.setARPEntry() for node " + nodeID + " for entry ipAddress " + ipAddress + " fail: " + status.getDescription());
        }
        return ret;
    }

    public List<ARPTableEntry> getARPTable(Node node/*long nodeID*/){
        if(node == null){
            logger.debug("ERROR: getARPTable(): null node");
            return null;
        }
        long nodeID = ((Long)node.getID()).longValue();
        //SNMP4SDNErrorCode status = checkNodeIpValid(nodeID);
        Status status = checkNodeIpValid(node);
        //if(status != SNMP4SDNErrorCode.SUCCESS){
        if(!status.isSuccess()){
            logger.debug("ERROR: getARPEntry(): call checkNodeIpValid() for node {} fail", nodeID);
            return null;
        }

        if(isDummy){
            List<ARPTableEntry> list = new ArrayList<>();
            ARPTableEntry ret =new ARPTableEntry();
            ret.ipAddress = "1.1.1.1";
            ret.macAddress = HexString.toLong("12:34:56:78:AB:CD");
            list.add(ret);
            return list;
        }

        List<ARPTableEntry> ret = new SNMPHandler(cmethUtil).getARPTable(nodeID);
        if(ret == null){
            logger.debug("ERROR: getARPEntry(): call SNMPHandler.getARPEntry() for node {} fail", nodeID);
            return null;
        }
        return ret;
    }


    /*private Status checkNodeIpValid(Node node){
        if(node == null){
            logger.debug("ERROR: checkNodeIpValid(): given null Node");
            return new Status(StatusCode.INTERNALERROR, "snmp4sdn: MiscConfigServiceImpl: checkNodeIpValid(): given null Node");
        }
        if(node.getID() == null){
            logger.debug("ERROR: checkNodeIpValid(): given Node's ID is null");
            return new Status(StatusCode.INTERNALERROR, "snmp4sdn: MiscConfigServiceImpl: checkNodeIpValid(): given Node's ID is null");
        }
        return checkNodeIpValid(((Long)node.getID()).longValue());
    }*/

    private Status/*SNMP4SDNErrorCode*/ checkNodeIpValid(Node node/*long nodeID*/){
        if(node == null){
            logger.debug("ERROR: checkNodeIpValid(): null node");
            return new Status(StatusCode.BADREQUEST, "MiscConfigServiceImpl: checkNodeIpValid(): null node");
        }

        long nodeID = ((Long)node.getID()).longValue();
        if(cmethUtil == null){
            logger.debug("ERROR: MiscConfigServiceImpl: checkNodeIpValid(): cmethUtil is null");
            return new Status(StatusCode.INTERNALERROR, "MiscConfigServiceImpl: checkNodeIpValid() with nodeId " + nodeID + ": cmethUtil is null");
        }
        String sw_ipAddr = cmethUtil.getIpAddr(nodeID);
        if(sw_ipAddr == null){
            logger.debug("ERROR: checkNodeIpValid(): IP address of switch (nodeID: " + nodeID + ") not in DB");
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.NOTFOUND);
        }
        else{
            //return SNMP4SDNErrorCode.SUCCESS;
            return new Status(StatusCode.SUCCESS);
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<DisableStpOutput>> disableStp(DisableStpInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: disableStp(): given null input");
            return RpcResultBuilder.<DisableStpOutput>failed().buildFuture();
        }
        Long nodeId = input.getNodeId();
        if(nodeId == null){
            logger.debug("ERROR: disableStp(): given nodeId is null");
            return RpcResultBuilder.<DisableStpOutput>failed().buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: disableStp(): call createSNMPNode() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<DisableStpOutput>failed().buildFuture();
        }

        //execute disableSTP
        Status status = disableSTP(node);
        //TODO: for each case of returned status error code, give DisableStpOutputBuilder Result.XXX accordingly
        if(status.isSuccess()){
            DisableStpOutputBuilder ob = new DisableStpOutputBuilder().setDisableStpResult(Result.SUCCESS);
            return RpcResultBuilder.<DisableStpOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: disableStp(): call disableSTP() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<DisableStpOutput>failed().buildFuture();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<EnableStpOutput>> enableStp(EnableStpInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: enableStp(): given null input");
            return RpcResultBuilder.<EnableStpOutput>failed().buildFuture();
        }
        Long nodeId = input.getNodeId();
        if(nodeId == null){
            logger.debug("ERROR: enbleStp(): given nodeId is null");
            return RpcResultBuilder.<EnableStpOutput>failed().buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: enableStp(): call createSNMPNode() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<EnableStpOutput>failed().buildFuture();
        }

        //execute enableSTP
        Status status = enableSTP(node);
        //TODO: for each case of returned status error code, give EnableStpOutputBuilder Result.XXX accordingly
        if(status.isSuccess()){
            EnableStpOutputBuilder ob = new EnableStpOutputBuilder().setEnableStpResult(Result.SUCCESS);
            return RpcResultBuilder.<EnableStpOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: enableStp(): call enableSTP() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<EnableStpOutput>failed().buildFuture();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<GetStpPortStateOutput>> getStpPortState(GetStpPortStateInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: getStpPortState(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        Short port = input.getPort();
        if(nodeId == null || port == null){
            logger.debug("ERROR: getStpPortState(): given nodeId or port is null");
            return null;
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: getStpPortState(): call createSNMPNode() with nodeId {} fail", nodeId);
            return null;
        }

        //execute getSTPPortState
        STPPortState state = getSTPPortState(node, port);
        if(state == null){
            logger.debug("ERROR: getStpPortState(): call getSTPPortState() fail, nodeId {} port {}", nodeId, port);
            return null;
        }
        GetStpPortStateOutputBuilder ob = new GetStpPortStateOutputBuilder();
        switch(state){
        case DISABLED:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.DISABLED);
            break;
        }
        case BLOCKING:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.BLOCKING);
            break;
        }
        case LISTENING:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.LISTENING);
            break;
        }
        case LEARNING:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.LEARNING);
            break;
        }
        case FORWARDING:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.FORWARDING);
            break;
        }
        case BROKEN:{
            ob = new GetStpPortStateOutputBuilder().setStpPortState(StpPortState.BROKEN);
            break;
        }
        default:{
            logger.debug("ERROR: getStpPortState(): call getSTPPortState() with nodeId {} and port {}, return invalid port state {}", nodeId, port, state);
            return null;
        }
        }

        return RpcResultBuilder.<GetStpPortStateOutput>success(ob.build()).buildFuture();
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<SetStpPortStateOutput>> setStpPortState(SetStpPortStateInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: setStpPortState(): given null input");
            return RpcResultBuilder.<SetStpPortStateOutput>failed().buildFuture();
        }
        Long nodeId = input.getNodeId();
        Short port = input.getPort();
        Boolean isEnable = input.isEnable();
        if(nodeId == null || port == null || isEnable == null){
            logger.debug("ERROR: setStpPortState(): given nodeId or port or isEnable is null");
            return RpcResultBuilder.<SetStpPortStateOutput>failed().buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: setStpPortState(): call createSNMPNode() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<SetStpPortStateOutput>failed().buildFuture();
        }

        //execute setSTPPortState
        Status status = setSTPPortState(node, port.shortValue(), isEnable.booleanValue());
        //TODO: for each case of returned status error code, give EnableStpOutputBuilder Result.XXX accordingly
        if(status.isSuccess()){
            SetStpPortStateOutputBuilder ob = new SetStpPortStateOutputBuilder().setSetStpPortStateResult(Result.SUCCESS);
            return RpcResultBuilder.<SetStpPortStateOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: setStpPortState(): call setSTPPortState() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<SetStpPortStateOutput>failed().buildFuture();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<GetStpPortRootOutput>> getStpPortRoot(GetStpPortRootInput input){
        //check null input parameters
        if(input == null){
            logger.debug("ERROR: getStpPortRoot(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        Short port = input.getPort();
        if(nodeId == null || port == null){
            logger.debug("ERROR: getStpPortRoot(): given nodeId or port is null");
            return null;
        }

        if(nodeId < 0){
            logger.debug("ERROR: getStpPortRoot(): invalid nodeId {} (port {})", nodeId, port);
            return null;
        }
        if(port < 0){//TODO: valid port range
            logger.debug("ERROR: getStpPortRoot(): invalid port {} (nodeId {})", port, nodeId);
            return null;
        }

        long rootNodeId = new SNMPHandler(cmethUtil).getStpPortRoot(nodeId, port);
        if(rootNodeId < 0){
            logger.debug("ERROR: getStpPortRoot(): call SNMPHandler.getStpPortRoot() fail, nodeId {} port {}", nodeId, port);
            return null;
        }

        GetStpPortRootOutputBuilder ob = new GetStpPortRootOutputBuilder().setRootNodeId(rootNodeId);
        return RpcResultBuilder.<GetStpPortRootOutput>success(ob.build()).buildFuture();
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<GetArpEntryOutput>> getArpEntry(GetArpEntryInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: getArpEntry(): given null input");
            return null;
        }
        Long nodeId = input.getNodeId();
        String ipAddress = input.getIpAddress();
        if(nodeId == null || ipAddress == null){
            logger.debug("ERROR: getArpEntry(): given nodeId or ipAddress is null");
            return null;
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: getArpEntry(): call createSNMPNode() with nodeId {} fail", nodeId);
            return null;
        }

        //execute getARPEntry
        ARPTableEntry entry = getARPEntry(node, ipAddress);
        if(entry == null){
            logger.debug("ERROR: getArpEntry(): call getARPEntry() fail, nodeId {} ipAddress {}", nodeId, ipAddress);
            return null;
        }

        GetArpEntryOutputBuilder ob = new GetArpEntryOutputBuilder().setIpAddress(entry.ipAddress).setMacAddress(entry.macAddress);

        return RpcResultBuilder.<GetArpEntryOutput>success(ob.build()).buildFuture();
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<SetArpEntryOutput>> setArpEntry(SetArpEntryInput input){
        //check null input parameters
        if(input == null){
            logger.debug("ERROR: setArpEntry(): given null input");
            return RpcResultBuilder.<SetArpEntryOutput>failed().buildFuture();
        }
        Long nodeId = input.getNodeId();
        String ipAddress = input.getIpAddress();
        Long macAddress = input.getMacAddress();
        if(nodeId == null || ipAddress == null || macAddress == null){
            logger.debug("ERROR: setArpEntry(): given nodeId or ipAddress or macAddress is null");
            return RpcResultBuilder.<SetArpEntryOutput>failed().buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: setArpEntry(): call createSNMPNode() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<SetArpEntryOutput>failed().buildFuture();
        }

        //execute setARPEntry
        ARPTableEntry entry = new ARPTableEntry();
        entry.ipAddress = new String(ipAddress);
        entry.macAddress = macAddress.longValue();
        Status status = setARPEntry(node, entry);
        //TODO: for each case of returned status error code, give EnableStpOutputBuilder Result.XXX accordingly
        if(status.isSuccess()){
            SetArpEntryOutputBuilder ob = new SetArpEntryOutputBuilder().setSetArpEntryResult(Result.SUCCESS);
            return RpcResultBuilder.<SetArpEntryOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: setArpEntry(): call setARPEntry() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<SetArpEntryOutput>failed().buildFuture();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<DeleteArpEntryOutput>> deleteArpEntry(DeleteArpEntryInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: deleteArpEntry(): given null input");
            return RpcResultBuilder.<DeleteArpEntryOutput>failed().buildFuture();
        }
        Long nodeId = input.getNodeId();
        String ipAddress = input.getIpAddress();
        if(nodeId == null || ipAddress == null){
            logger.debug("ERROR: deleteArpEntry(): given nodeId or ipAddress is null");
            return RpcResultBuilder.<DeleteArpEntryOutput>failed().buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if(node == null){
            logger.debug("ERROR: deleteArpEntry(): call createSNMPNode() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<DeleteArpEntryOutput>failed().buildFuture();
        }

        //execute deleteARPEntry
        Status status = deleteARPEntry(node, ipAddress);
        //TODO: for each case of returned status error code, give EnableStpOutputBuilder Result.XXX accordingly
        if(status.isSuccess()){
            DeleteArpEntryOutputBuilder ob = new DeleteArpEntryOutputBuilder().setDeleteArpEntryResult(Result.SUCCESS);
            return RpcResultBuilder.<DeleteArpEntryOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: deleteArpEntry(): call deleteARPEntry() with nodeId {} fail", nodeId);
            return RpcResultBuilder.<DeleteArpEntryOutput>failed().buildFuture();
        }
    }

    //md-sal
    @Override
    public ListenableFuture<RpcResult<GetArpTableOutput>> getArpTable(GetArpTableInput input) {
        /*
        * Fix Bug 5438: Result of calling rpc get-arp-table defined in misc-config.yang is false.
        * */
        GetArpTableOutputBuilder ob = new GetArpTableOutputBuilder();
        List<ArpTableEntry> arpTable = new ArrayList<>();

        //check null input parameters
        if (input == null) {
            logger.debug("ERROR: getArpTable(): given null input");

            ob.setArpTableEntry(arpTable);
            return RpcResultBuilder.<GetArpTableOutput>success(ob.build()).buildFuture();
        }

        Long nodeId = input.getNodeId();
        if (nodeId == null) {
            logger.debug("ERROR: getArpTable(): given nodeId is null");

            ob.setArpTableEntry(arpTable);
            return RpcResultBuilder.<GetArpTableOutput>success(ob.build()).buildFuture();
        }

        //create the node and check null
        Node node = createSNMPNode(nodeId.longValue());
        if (node == null) {
            logger.debug("ERROR: getArpTable(): call createSNMPNode() with nodeId {} fail", nodeId);

            ob.setArpTableEntry(arpTable);
            return RpcResultBuilder.<GetArpTableOutput>success(ob.build()).buildFuture();
        }

        //execute getARPTable   (Notice the ARP v.s. Arp !)
        List<ARPTableEntry> table = getARPTable(node);
        if (table == null) {
            logger.debug("ERROR: getArpTable(): call getARPTable() fail, nodeId {}", nodeId);

            ob.setArpTableEntry(arpTable);
            return RpcResultBuilder.<GetArpTableOutput>success(ob.build()).buildFuture();
        }

        //prepare the arp table to return
        List<ArpTableEntry> retTable = new ArrayList<>();
        for (ARPTableEntry entry : table) {
            ArpTableEntryBuilder entryBuilder = new ArpTableEntryBuilder()
                    .setIpAddress(entry.ipAddress)
                    .setMacAddress(entry.macAddress);
            ArpTableEntry retEntry = entryBuilder.build();
            retTable.add(retEntry);
        }

        ob.setArpTableEntry(retTable);
        return RpcResultBuilder.<GetArpTableOutput>success(ob.build()).buildFuture();
    }

    //CLI: s4sSTP
    public void _s4sSTP(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: s4sSTP [getPortState <switch> <port> | setPortState <switch> <port> <enable(Y/N)> | ");
            ci.println("\t\t  getSTPRoot <switch> <port> | disableSTP <switch> | enableSTP <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getPortState") == 0){
            ci.println();
            _s4sGetSTPPortState(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setPortState") == 0){
            ci.println();
            _s4sSetSTPPortState(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getSTPRoot") == 0){
            ci.println();
            _s4sGetSTPRoot(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("disableSTP") == 0){
            ci.println();
            _s4sDisableSTP(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("enableSTP") == 0){
            ci.println();
            _s4sEnableSTP(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: s4sSTP [getPortState <switch> <port> | setPortState <switch> <port> <enable(Y/N)> | ");
            ci.println("\t\t  getSTPRoot <switch> <port> | disableSTP <switch> | enableSTP <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: s4sSTP getPortState <switch> <port>
    public void _s4sGetSTPPortState(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sSTP getPortState <switch> <port>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String port to int value vlanId
        short portNum = -1;
        Short.parseShort(arg3);
        try{
            portNum = Short.parseShort(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to short value error: " + e1);
            return;
        }

        Node node = createSNMPNode(nodeId);
        STPPortState state = getSTPPortState(node, portNum);
        if(state == null){
            ci.println();
            ci.println("Fail to get STP port state of node " + nodeId + " port " + portNum);
            ci.println();
        }
        else{
            ci.println();
            ci.println("STP port state of node " + nodeId + " port " + portNum + ": " + state);
            ci.println();
        }
    }

    //CLI: s4sSTP setPortState <switch> <port> <enable(Y/N)>
    public void _s4sSetSTPPortState(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sSTP setPortState <switch> <port> <enable(Y/N)>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String port to int value vlanId
        short portNum = -1;
        Short.parseShort(arg3);
        try{
            portNum = Short.parseShort(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to short value error: " + e1);
            return;
        }

        //parse arg4: enable port to run STP or not
        boolean isEnable;
        if(arg4.compareToIgnoreCase("Y") == 0) {
            isEnable = true;
        } else if(arg4.compareToIgnoreCase("N") == 0) {
            isEnable = false;
        } else{
            ci.println();
            ci.println("Please use: s4sSTP setPortState <switch> <port> <enable(Y/N)>");
            return;
        }

        Node node = createSNMPNode(nodeId);
        Status status = setSTPPortState(node, portNum, isEnable);
        //SNMP4SDNErrorCode ret = setSTPPortState(nodeId, portNum, isEnable);
        if(status.isSuccess()){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            ci.println();
            ci.println("Successfully to set node " + nodeId + " port " + portNum + " of STP state as " + isEnable);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to set STP state of node " + nodeId + " port " + portNum);
            ci.println();
        }
    }

    //TODO: So far only this CLI testing method is written with MD-SAL call. Other's are not because in initial coding didn't write so.
    //CLI: s4sSTP getSTPRoot <switch> <port>
    public void _s4sGetSTPRoot(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sSTP getSTPRoot <switch> <port>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //parse arg3: String port to int value vlanId
        short port = -1;
        Short.parseShort(arg3);
        try{
            port = Short.parseShort(arg3);
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg3 + " to short value error: " + e1);
            return;
        }

        //prepare parameters to get the switch's port's stp root
        GetStpPortRootInputBuilder ib = new GetStpPortRootInputBuilder().setNodeId(nodeId).setPort(port);

        //execute getStpPortRoot(), and check return null parameters?
        Long rootNodeId = null;
        try {
            ListenableFuture<RpcResult<GetStpPortRootOutput>> ret = this.getStpPortRoot(ib.build());
            if(ret == null){
                ci.println();
                ci.println("Fail to get STP root of node " + nodeId + " port  " + port + " (null return)");
                ci.println();
                return;
            }
            RpcResult<GetStpPortRootOutput> result = ret.get();
            if(result == null){
                ci.println();
                ci.println("Fail to get STP root of node " + nodeId + " port  " + port + " (null result)");
                ci.println();
                return;
            }
            if(result.getResult() == null){
                ci.println();
                ci.println("Fail to get STP root of node " + nodeId + " port  " + port + " (null in result)");
                ci.println();
                return;
            }

            rootNodeId = result.getResult().getRootNodeId();
            if(rootNodeId == null){
                ci.println();
                ci.println("Fail to get STP root of node " + nodeId + " port  " + port);
                ci.println();
                return;
            }
        } catch (InterruptedException ie) {
            ci.println();
            ci.println("ERROR: call getStpPortRoot() occurs exception (node " + nodeId + " port  " + port + "): " + ie);
            ci.println();
            return;
        } catch (ExecutionException ee) {
            ci.println();
            ci.println("ERROR: call getStpPortRoot() occurs exception (node " + nodeId + " port  " + port + "): " + ee);
            ci.println();
            return;
        }

        ci.println();
        ci.println("The STP root of node " + nodeId + " port " + port + ": " + rootNodeId);
        ci.println();
    }


    //CLI: s4sSTP disableSTP <switch>
    public void _s4sDisableSTP(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sSTP disableSTP <switch>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //SNMP4SDNErrorCode ret = disableSTP(nodeId);
        Node node = createSNMPNode(nodeId);
        Status ret = disableSTP(node);
        if(ret.isSuccess()){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            ci.println();
            ci.println("Successfully to disable STP of node " + nodeId);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to disable STP of node " + nodeId);
            ci.println();
        }
    }

    //CLI: s4sSTP enableSTP <switch>
    public void _s4sEnableSTP(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sSTP disableSTP <switch>");
            return;
        }

        //parse arg2: String sw_mac to int value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //SNMP4SDNErrorCode ret = enableSTP(nodeId);
        Node node = createSNMPNode(nodeId);
        Status ret = enableSTP(node);
        if(ret.isSuccess()){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            ci.println();
            ci.println("Successfully to enable STP of node " + nodeId);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Fail to enable STP of node " + nodeId);
            ci.println();
        }
    }

    //CLI: s4sARP
    public void _s4sARP(CommandInterpreter ci){
        String arg1 = ci.nextArgument();
        if(arg1 == null){
            ci.println();
            ci.println("Please use: s4sARP [getEntry <switch> <ip_address> | deleteEntry <switch> <ip_address> | ");
            ci.println("\t\t  setEntry <switch> <ip_address> <mac_address> | getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
        else if(arg1.compareToIgnoreCase("getEntry") == 0){
            ci.println();
            _s4sGetARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("deleteEntry") == 0){
            ci.println();
            _s4sDeleteARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("setEntry") == 0){
            ci.println();
            _s4sSetARPEntry(ci);
            ci.println();
        }
        else if(arg1.compareToIgnoreCase("getTable") == 0){
            ci.println();
            _s4sGetARPTable(ci);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Please use: s4sARP [getEntry <switch> <ip_address> | deleteEntry <switch> <ip_address> | ");
            ci.println("\t\t  setEntry <switch> <ip_address> <mac_address> | getTable <switch>");
            ci.println("\t\t  (<swich>: node ID or mac address)");
            ci.println();
            return;
        }
    }

    //CLI: s4sARP getEntry <switch> <ip_address>
    public void _s4sGetARPEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sARP getEntry <switch> <ip_address>");
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        String ipAddress = new String(arg3);

        Node node = createSNMPNode(nodeId);
        ARPTableEntry entry = getARPEntry(node, ipAddress);
        if(entry == null){
            ci.println();
            ci.println("Fail to get ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
        else{
            ci.println();
            ci.println("ARP entry on node " + nodeId + " for IP " + ipAddress + ": <IP " + entry.ipAddress + ", MAC " + HexString.toHexString(entry.macAddress).toUpperCase() + ">");
            ci.println();
        }
    }

    //CLI: s4sARP deleteEntry <switch> <ip_address>
    public void _s4sDeleteARPEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sARP deleteEntry <switch> <ip_address>");
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //arg3
        String ipAddress = new String(arg3);

        //SNMP4SDNErrorCode ret = deleteARPEntry(nodeId, ipAddress);
        Node node = createSNMPNode(nodeId);
        Status ret = deleteARPEntry(node, ipAddress);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            ci.println();
            ci.println("Fail to delete ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
        else{
            ci.println();
            ci.println("Successfully delete ARP entry on node " + nodeId + " for IP " + ipAddress);
            ci.println();
        }
    }

    //CLI: s4sARP setEntry <switch> <ip_address> <mac_address>
    public void _s4sSetARPEntry(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String arg3 = ci.nextArgument();
        String arg4 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || arg3 == null || arg4 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sARP setEntry <switch> <ip_address> <mac_address>");
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        //arg3
        String ipAddress = new String(arg3);

        //parse arg4: String switch to long value nodeId
        long macId = -1;
        try{
            if(arg4.indexOf(":") < 0) {
                macId = Long.parseLong(arg4);
            } else {
                macId = HexString.toLong(arg4);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg4 + " to long value error: " + e1);
            return;
        }

        ARPTableEntry entry = new ARPTableEntry();
        entry.ipAddress = new String(ipAddress);
        entry.macAddress = macId;

        //SNMP4SDNErrorCode ret = setARPEntry(nodeId, entry);
        Node node = createSNMPNode(nodeId);
        Status ret = setARPEntry(node, entry);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            ci.println();
            ci.println("Fail to set ARP entry on node " + nodeId + ": <IP " + entry.ipAddress + ", " + "MAC " + HexString.toHexString(entry.macAddress).toUpperCase() + ">");
            ci.println();
        }
        else{
            ci.println();
            ci.println("Successfully set ARP entry on node " + nodeId + ": <IP " + entry.ipAddress + ", " + "MAC " + HexString.toHexString(entry.macAddress).toUpperCase() + ">");
            ci.println();
        }
    }

    //CLI: s4sARP getTable <switch>
    public void _s4sGetARPTable(CommandInterpreter ci){
        String arg2 = ci.nextArgument();
        String garbage = ci.nextArgument();

        if(arg2 == null || garbage != null){
            ci.println();
            ci.println("Please use: s4sARP getTable <switch>");
            return;
        }

        //parse arg2: String switch to long value nodeId
        long nodeId = -1;
        try{
            if(arg2.indexOf(":") < 0) {
                nodeId = Long.parseLong(arg2);
            } else {
                nodeId = HexString.toLong(arg2);
            }
        }catch(NumberFormatException e1){
            ci.println("Error: convert argument " + arg2 + " to long value error: " + e1);
            return;
        }

        Node node = createSNMPNode(nodeId);
        List<ARPTableEntry> arpTable = getARPTable(node);
        if(arpTable == null){
            ci.println();
            ci.println("Fail to get ARP Table on node " + nodeId);
            ci.println();
            return;
        }
        ci.println();
        ci.println("======== ARP Table of Node " + nodeId + " =========");
        ci.println("\tIP\t\t\tMAC");
        for(int i = 0; i < arpTable.size(); i++){
            ARPTableEntry entry = arpTable.get(i);
            ci.println(entry.ipAddress + "\t\t" + HexString.toHexString(entry.macAddress).toUpperCase());
        }
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("MiscConfigServiceImpl.getHelp():null");
    }

    private static Node createSNMPNode(long nodeId) {
        if(nodeId < 0){
            logger.debug("ERROR: createSNMPNode(), given nodeId {}, is invalid", nodeId);
            return null;
        }

        try {
            return new Node("SNMP", nodeId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: MiscConfigServiceImpl: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", nodeId, e1);
            return null;
        }
    }

    //private static NodeConnector createNodeConnector(Short portId, Node node) {
    private static NodeConnector createSNMPNodeConnector(short portId, long nodeId) {
        if(portId < 0){
            logger.debug("ERROR: createSNMPNodeConnector(): given nodeId {} and portId {}, portId is invalid", nodeId, portId);
            return null;
        }
        if(nodeId < 0){
            logger.debug("ERROR: createSNMPNodeConnector(): given nodeId {} and portId {}, nodeId is invalid", nodeId, portId);
            return null;
        }
        //if (node.getType().equals("SNMP")) {
            try {
                Node node = createSNMPNode(nodeId);
                return new NodeConnector("SNMP", new Short(portId), node);
            } catch (Exception e1) {
                logger.debug("ERROR: createSNMPNodeConnector(): given nodeId {} and portId {}, error: {}", nodeId, portId, e1);
                return null;
            }
        //}
    }

}
