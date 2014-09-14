/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

//no-sal/*not to remove this interface, otherwise code vary a lot //TODO:clean code */
import org.opendaylight.snmp4sdn.IVLANService;
import org.opendaylight.snmp4sdn.VLANTable;
import org.opendaylight.snmp4sdn.VLANTable.VLANTableEntry;
/*//custom ad-sal
import org.opendaylight.controller.sal.vlan.IPluginInVLANService;
import org.opendaylight.controller.sal.vlan.VLANTable;
import org.opendaylight.controller.sal.vlan.VLANTable.VLANTableEntry;*/
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.VlanService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.DeleteVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.PrintVlanTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput;

//For md-sal RPC call
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
//TODO: com.google.common import error in karaf
/*import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VLANService implements /*IPluginInVLANService//custom ad-sal*/VlanService/*md-sal*/, IVLANService/*not to remove this interface, otherwise code vary a lot */, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(VLANService.class);

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

    @Override//md-sal
    public Future<RpcResult<java.lang.Void>> addVlan(final AddVlanInput input){
        Long nodeId = input.getNodeID();
        //Integer vlanId = input.getVlanID();
        Long vlanId = new Long(input.getVlanID().intValue());//TODO: inconsistent vlanId value type (Int or Long) among methods
        String vlanName = input.getVlanName();
        Node node = createSNMPNode(nodeId.longValue());
        addVLAN(node, vlanId, vlanName);//argument value validation is checked in the following

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
    }

    @Override//md-sal
    public Future<RpcResult<java.lang.Void>> deleteVlan(DeleteVlanInput input){
        Long nodeId = input.getNodeID();
        //Integer vlanId = input.getVlanID();
        Long vlanId = new Long(input.getVlanID().intValue());//TODO: inconsistent vlanId value type (Int or Long) among methods
        Node node = createSNMPNode(nodeId.longValue());
        deleteVLAN(node, vlanId);//argument value validation is checked in the following

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
    }

    @Override//md-sal
    public Future<RpcResult<java.lang.Void>> printVlanTable(final PrintVlanTableInput input){
        Long nodeId = input.getNodeID();
        Node node = createSNMPNode(nodeId.longValue());
        VLANTable vTable = getVLANTable(node);//argument value validation is checked in the following
        System.out.println(vTable.toString());

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
    }

    @Override//md-sal
    public Future<RpcResult<java.lang.Void>> setVlanPorts(final SetVlanPortsInput input){
        Long nodeId = input.getNodeID();
        //Integer vlanId = input.getVlanID();
        Long vlanId = new Long(input.getVlanID().intValue());//TODO: inconsistent vlanId value type (Int or Long) among methods
        String portList = input.getPortList();
        Node node = createSNMPNode(nodeId.longValue());

        //create nodeConnectors
        List<NodeConnector> nodeConns = new ArrayList<NodeConnector>();
        String[] ports = portList.split(",");
        for(int i = 0; i < ports.length; i++)
            createSNMPNodeConnector(nodeId.longValue(), Integer.parseInt(ports[i]));
        setVLANPorts(node, vlanId, nodeConns);//argument value validation is checked in the following

        //TODO: the 'Future' mechanism is not yet coded here, now just directly return successful
        //return Futures.immediateFuture( RpcResultBuilder.<Void> success().build() );
        return null;
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

    private static Node createSNMPNode(long switchId) {
        try {
            return new Node("SNMP", new Long(switchId));
        } catch (ConstructionException e1) {
            logger.error("",e1);
            return null;
        }
    }

    private static NodeConnector createSNMPNodeConnector(long switchId, int portNum) {
        try {
            Node node = createSNMPNode(switchId);
            return new NodeConnector("SNMP", new Short((short)portNum), node);
        } catch (Exception e1) {
            logger.error("VLANService create NodeConnector of type SNMP fail: ", e1);
            return null;
        }
    }
}
