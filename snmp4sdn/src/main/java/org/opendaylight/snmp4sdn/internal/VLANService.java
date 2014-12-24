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

import org.opendaylight.snmp4sdn.IKarafVLANService;//karaf

//For md-sal RPC call
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcError;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
//TODO: com.google.common import error in karaf
/*import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

//import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VLANService implements /*IPluginInVLANService,//custom ad-sal*/ VlanService/*md-sal*/, IVLANService/*not to remove this interface, otherwise code vary a lot */, IKarafVLANService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(VLANService.class);

    //Controller controller = null;
    IController controller = null;
    CLIHandler cli = null;
    CmethUtil cmethUtil = null;

    public void setController(IController core) {
        /*this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;//s4s add*/
        this.controller = core;
    }

    public void unsetController(IController core) {
        /*if (this.controller == (Controller)core) {
            this.controller = null;
        }*/
        if (this.controller == core) {
            this.controller = null;
        }
    }

     /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        cmethUtil = controller.getCmethUtil();
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
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


    /******For Karaf and OSGi CLI commands requesting VLANService*********/

    @Override//karaf
    public void addVLANSetPorts(String sw_mac, String vlanID, String vlanName, String portList){
        logger.info("VLANService.addVLANSetPorts() is called by Karaf");
        s4sAddVLANandSetPorts(sw_mac, vlanID, vlanName, portList);
    }

    @Override//karaf
    public void deleteVLAN(String sw_mac, String vlanID){   
        logger.info("VLANService.deleteVLAN() is called by Karaf");
        boolean isSuccess = s4sDeleteVLAN_execute(sw_mac, vlanID);
        if(isSuccess)
            System.out.println("Success to delete VLAN " + vlanID + " on switch " + sw_mac);
        else
            System.out.println("Fail to delete VLAN " + vlanID + " on switch " + sw_mac);
    }

    @Override//karaf
    public void printVLANTable(String sw_mac){
        logger.info("VLANService.printVLANTable() is called by Karaf");
        s4sPrintVLANTable_execute(sw_mac);
    }

    public void _s4sAddVLAN(CommandInterpreter ci){
        String sw_mac = null, vlanID = null, vlanName = null;
        
        sw_mac = ci.nextArgument();
        vlanID= ci.nextArgument();
        vlanName = ci.nextArgument();

        s4sAddVLAN_execute(sw_mac, vlanID, vlanName);
   }
   private boolean s4sAddVLAN_execute(String sw_mac, String vlanID, String vlanName){
        if(sw_mac == null || vlanID == null || vlanName == null){
            System.out.println("\nPlease use command: s4sAddVLAN <switch's mac addr> <vlan id> <vlan name>");
            return false;
        }
        
        Node node = null;
        try{
            node = new Node("SNMP", new Long(HexString.toLong(sw_mac)));
        }catch(Exception e){
            logger.error("in s4sAddVLAN_execute(): given switch mac \"" + sw_mac + "\", create node error -- " + e);
            return false;
        }
        //(new SNMPHandler(cmethUtil)).addVLAN(node, new Long(Long.parseLong(vlanID)), vlanName);//skip the VLANService wrapper, call SNMPHandler directly
        Status status = addVLAN(node, new Long(Long.parseLong(vlanID)), vlanName);
        if(status.isSuccess())
            return true;
        else
            return false;
    }

    public void _s4sSetVLANPorts(CommandInterpreter ci){
        String sw_mac = null, vID = null, portList = null;
        
        sw_mac = ci.nextArgument();
        vID = ci.nextArgument();
        portList = ci.nextArgument();

        s4sSetVLANPorts_execute(sw_mac, vID, portList);
    }
    private boolean s4sSetVLANPorts_execute(String sw_mac, String vID, String portList){
        Long vlanID;
        Node node = null;
        List<NodeConnector> nodeConns = new ArrayList<NodeConnector>();
        String portsStr = null;

        if(sw_mac == null || vID == null || portList == null){
            logger.error("\nPlease use command: s4sSetVLANPorts <switch's mac addr> <vlan id> <ports to the vlan (sepereate by comma)>");
            return false;
        }

        //create node
        try{
            node = new Node("SNMP", new Long(HexString.toLong(sw_mac)));
        }catch(Exception e){
            logger.error("in s4sSetVLANPorts_execute(): given switch mac \"" + sw_mac + "\", create node error -- " + e);
            return false;
        }

        //get vlandID
        vlanID = new Long(vID);
        
        //create nodeConnectors
        String[] ports = portList.split(",");
        try{
            for(int i = 0; i < ports.length; i++)
                nodeConns.add(new NodeConnector("SNMP", Short.parseShort(ports[i]), node));
        }catch(Exception e){
            logger.error("in s4sSetVLANPorts_execute(): create node or nodeconnector error -- " + e);
            return false;
        }

        //(new SNMPHandler(cmethUtil)).setVLANPorts(node, vlanID, nodeConns);//skip the VLANService wrapper, call SNMPHandler directly
        Status status = setVLANPorts(node, vlanID, nodeConns);
        if(status.isSuccess())
            return true;
        else
            return false;
    }

    public void _s4sDeleteVLAN(CommandInterpreter ci){
        String sw_mac = null, vlanID = null, vlanName = null;
        
        sw_mac = ci.nextArgument();
        vlanID= ci.nextArgument();
        //null string check will be done in following procedure...
        
        s4sDeleteVLAN_execute(sw_mac, vlanID);
    }
    
     private boolean s4sDeleteVLAN_execute(String sw_mac, String vlanID){
         if(sw_mac == null || vlanID == null){
             logger.error("\nPlease use command: s4s, vlanNameVLAN <switch's mac addr> <vlan id>");
             return false;
         }
         
         Node node = null;
         try{
             node = new Node("SNMP", new Long(HexString.toLong(sw_mac)));
         }catch(Exception e){
             logger.error("in s4sDeleteVLAN_execute(): given switch mac \"" + sw_mac + "\", create node error -- " + e);
             return false;
         }

         //(new SNMPHandler(cmethUtil)).deleteVLAN(node, new Long(Long.parseLong(vlanID)));//skip the VLANService wrapper, call SNMPHandler directly
         Status status = deleteVLAN(node, new Long(Long.parseLong(vlanID)));
         if(status.isSuccess())
             return true;
         else
            return false;
     }

    public void _s4sPrintVLANTable(CommandInterpreter ci){
        String sw_mac = ci.nextArgument();
        s4sPrintVLANTable_execute(sw_mac);
    }

    private void s4sPrintVLANTable_execute(String sw_mac){
        Long vlanID;
        Node node = null;

        if(sw_mac == null){
            logger.error("\nPlease use command: s4sPrintVLANTable <switch's mac addr>");
            return;
        }

        //create node
        try{
            node = new Node("SNMP", new Long(HexString.toLong(sw_mac)));
        }catch(Exception e){
            logger.error("in s4sPrintVLANTable_execute(): given switch mac \"" + sw_mac + "\", create node error -- " + e);
            return;
        }

        VLANTable table = null;
        table = getVLANTable(node);
        if(table == null){
            logger.error("in s4sPrintVLANTable_execute(): given switch mac \"" + sw_mac + "\", get null VLANTable(), so can't print");
            return;
        }
        System.out.println();
        System.out.println("Switch (mac: " + sw_mac + ") has VLANs:");
        System.out.println("VLAN(vlan_id): {port_list}");
        System.out.println("-------------------------");
        System.out.println(table.toString());
        
    }

    private void s4sAddVLANandSetPorts(String sw_mac, String vlanID, String vlanName, String portList){
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag = false;

        flag1 = s4sAddVLAN_execute(sw_mac, vlanID, vlanName);
        flag2 = s4sSetVLANPorts_execute(sw_mac, vlanID, portList);
        flag = flag1 && flag2;

        if(flag)
            System.out.println("\nVLAN " + vlanID + " (name: " + vlanName + ") is added to switch (mac: " + sw_mac + ") with ports " + portList);
        else
            System.out.println("\nFail to set VLAN " + vlanID + " (name: " + vlanName + ") to switch (mac: " + sw_mac + ") with ports " + portList);
    }

    public void _s4sDemoVLAN (CommandInterpreter ci){
        String sw_mac = ci.nextArgument();
        String vlanID= ci.nextArgument();
        String vlanName = ci.nextArgument();
        String portList = ci.nextArgument();

        if(sw_mac == null || vlanID == null || vlanName == null || portList == null){
            System.out.println("\nPlease use command: s4sDemoVLAN <switch's mac addr> <vlan id> <vlan name> <ports to the vlan (sepereate by comma)>");
            return;
        }
        s4sAddVLAN_execute(sw_mac, vlanID, vlanName);
        s4sSetVLANPorts_execute(sw_mac, vlanID, portList);

        System.out.println("\n--------------------------------------"
                        + "\n[VLAN DEMO]"
                        + "\nVLAN " + vlanID + "(name: " + vlanName + ") is added to switch (mac: " + sw_mac + ") with ports " + portList
                        +"\n==================================");
    }

    public void _s4sAutoDemoVLAN (CommandInterpreter ci){
        System.out.println("\n==================================");
        System.out.println("\n===VLAN DEMO========================");

        s4sAddVLANandSetPorts("00:00:90:94:e4:23:13:e0", "200", "vlan200", "1,3");//sw 32
        s4sAddVLANandSetPorts("00:00:90:94:e4:23:0b:00", "200", "vlan200", "1,10");//sw 33
        s4sAddVLANandSetPorts("00:00:90:94:e4:23:0b:20", "200", "vlan200", "3");//sw 34
        s4sAddVLANandSetPorts("00:00:90:94:e4:23:0a:e0", "200", "vlan200", "1,7");//sw 35

        System.out.println("\n==================================");
    }

    /******end of CLI (Karaf and OSGi)*********/
    
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
