/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

 package org.opendaylight.snmp4sdn.internal;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.snmp4sdn.IConfigService;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

public class ConfigService implements IConfigService, CommandProvider{

    Controller controller = null;
    CLIHandler cli = null;

    public void setController(IController core) {
        this.controller = (Controller)core;
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    @Override
    public Status disableSTP(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableSTP();
    }

   @Override
   public Status disableBpduFlooding(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding();
    }

   @Override
   public Status disableBpduFlooding(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableBpduFlooding((Short)nodeConnector.getID());
    }

    @Override
    public Status disableBroadcastFlooding(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding();
    }

    @Override
    public Status disableBroadcastFlooding(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableBroadcastFlooding((Short)nodeConnector.getID());
    }

    @Override
    public Status disableMulticastFlooding(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding();
    }

   @Override
   public Status disableMulticastFlooding(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableMulticastFlooding((Short)nodeConnector.getID());
    }

   @Override
   public Status disableUnknownFlooding(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding();
    }

   @Override
   public Status disableUnknownFlooding(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableUnknownFlooding((Short)nodeConnector.getID());
    }

   @Override
   public Status disableSourceMacCheck(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck();
    }

   @Override
   public Status disableSourceMacCheck(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableSourceMacCheck((Short)nodeConnector.getID());
    }

   @Override
   public Status disableSourceLearning(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableSourceLearning();
    }

   @Override
   public Status disableSourceLearning(Node node, NodeConnector nodeConnector){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        String username = cmethUtil.getCliUsername((Long)node.getID());
        String password = cmethUtil.getCliPassword((Long)node.getID());
        return new CLIHandler(sw_ipAddr, username, password).disableSourceLearning((Short)nodeConnector.getID());
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("ConfigService.getHelp():null");
    }
}
