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
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.snmp4sdn.IVLANService;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.List;

public class VLANService implements IVLANService, CommandProvider{

    Controller controller = null;
    CLIHandler cli = null;
    CmethUtil cmethUtil = null;

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;//s4s add
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    @Override
    public Status addVLAN(Node node, Long vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(vlanID < 1 || vlanID > 4096)
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when addVLAN to node (mac: " + HexString.toHexString((Long)node.getID()) + ")");

        return new SNMPHandler(cmethUtil).addVLAN(node, vlanID);
    }

    @Override
    public Status addVLAN(Node node, Long vlanID, String vlanName){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(vlanID < 1 || vlanID > 4096)
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when addVLAN to node (mac: " + HexString.toHexString((Long)node.getID()) + ")");

        return new SNMPHandler(cmethUtil).addVLAN(node, vlanID, vlanName);
    }

    @Override
    public Status setVLANPorts (Node node, Long vlanID, List<NodeConnector> nodeConns){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(vlanID < 1 || vlanID > 4096)
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when addVLAN to node (mac: " + HexString.toHexString((Long)node.getID()) + ")");

        return new SNMPHandler(cmethUtil).setVLANPorts(node, vlanID, nodeConns);
    }

    @Override
    public Status deleteVLAN(Node node, Long vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return status;

        if(vlanID < 1 || vlanID > 4096)
            return new Status(StatusCode.NOTACCEPTABLE, "VLAN ID as " + vlanID + " is invalid, when addVLAN to node (mac: " + HexString.toHexString((Long)node.getID()) + ")");

        return new SNMPHandler(cmethUtil).deleteVLAN(node, vlanID);
    }

    @Override
    public List<NodeConnector> getVLANPorts(Node node, Long vlanID){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return null;

        if(vlanID < 1 || vlanID > 4096) return null;

        return new SNMPHandler(cmethUtil).getVLANPorts(node, vlanID);
    }

    @Override
    public VLANTable getVLANTable(Node node){
        Status status = checkNodeIpValid(node);
        if(status.getCode() != StatusCode.SUCCESS) return null;

        return new SNMPHandler(cmethUtil).getVLANTable(node);
    }

    private Status checkNodeIpValid(Node node){
        CmethUtil cmethUtil = controller.getCmethUtil();
        String sw_ipAddr = cmethUtil.getIpAddr((Long)node.getID());
        if(sw_ipAddr == null)
            return new Status(StatusCode.NOTFOUND, "IP address of switch (mac address " + HexString.toHexString((Long)node.getID()) + ") is not found in DB");
        else
            return new Status(StatusCode.SUCCESS);
    }

    @Override//CommandProvider's
    public String getHelp() {
        return new String("VLANService.getHelp():null");
    }
}
