package org.opendaylight.snmp4sdn.internal;

import org.snmpj.*;

import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.ActionType;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchField;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.reader.FlowOnNode;
import org.opendaylight.controller.sal.utils.NodeConnectorCreator;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;

import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus.SNMPPortReason;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.io.*;

public class SNMPListener implements SNMPv2TrapListener, Runnable{

    private final IController controller;
    private CmethUtil cmethUtil;
    private SNMPTrapReceiverInterface trapReceiverInterface;
    private static String bootColdStartOID = "1.3.6.1.6.3.1.1.5.1";
    private static String bootWarmStartOID = "1.3.6.1.6.3.1.1.5.2";
    private static String linkDownOID = "1.3.6.1.6.3.1.1.5.3";
    private static String linkUpOID = "1.3.6.1.6.3.1.1.5.4";

    private boolean isFakeSim = false;//s4s: if true, auto generate fake parameters
    private boolean isTrapMechnismCancled = true;//s4s: if true, trap mechanism is cancled
    //private boolean isRegardAnyTrapAsSwJoin = false;//s4s: if true, regard any received snmp trap as a switch want to join in

    public SNMPListener(IController controller, CmethUtil cmethUtil){
        this.controller = controller;
        this.cmethUtil = cmethUtil;
        try{
            trapReceiverInterface = new SNMPTrapReceiverInterface(new PrintWriter(new PipedWriter(new PipedReader())));
            trapReceiverInterface.addv2TrapListener(this);
            trapReceiverInterface.startReceiving();
        }catch(Exception e){
            System.out.println("Problem starting Trap Interface: " + e.toString());
        }
    }

    public void run(){
    }

    public void start(){
    }

    public void stopListening(){
        try{
            trapReceiverInterface.stopReceiving();
            trapReceiverInterface.removev2TrapListener(this);
        }
        catch (Exception e)
        {
            System.out.println("Got trapReceiverInterface exception:" + e);
        }
    }

    public void startListening(){
        try{
            trapReceiverInterface.startReceiving();
        }
        catch (Exception e)
        {
            System.out.println("Got trapReceiverInterface exception" + e);
        }
    }

    @Override
    public void processv2Trap(SNMPv2TrapPDU pdu, String communityName, InetAddress agentIPAddress){
        System.out.println("Got v2 trap:");
        System.out.println("  sender IP address:  " + agentIPAddress.getHostAddress());
        System.out.println("  community name:     " + communityName);
        System.out.println("  system uptime:      " + pdu.getSysUptime().toString());
        System.out.println("  trap OID:           " + pdu.getSNMPTrapOID().toString());
        System.out.println("  var bind list:      " + pdu.getVarBindList().toString());
        System.out.println("000000000");

        String switchIP = agentIPAddress.getHostAddress();
        String trapOID = pdu.getSNMPTrapOID().toString();

        ISwitch sw;
        String chassisID;
        Long sid;

        if(cmethUtil.getSID(switchIP) == null){
            System.out.println("--> this switch is not listed in iplist.csv (can't find the sid of IP address" + agentIPAddress.getHostAddress() +")");
            return;
        }
        if(cmethUtil.getSnmpCommunity(cmethUtil.getSID(switchIP)) == null){
            System.out.println("--> this switch is not listed in iplist.csv (can't find the community '" + communityName +"' of IP address" + agentIPAddress.getHostAddress() +")");
            return;
        }
        if(!communityName.equals(cmethUtil.getSnmpCommunity(cmethUtil.getSID(switchIP)))){
            System.out.println("--> this switch doesn't belong to our SNMP community");
            return;
        }
        if(communityName == null){
            System.out.println("--> the 'community name' in the trap is null. Ignore this trap.");
            return;
        }

        /*
        //The following sections of isRegardAnyTrapAsSwJoin: regard any trap as switch join

        //add new switch
        if(isRegardAnyTrapAsSwJoin){
            sid = cmethUtil.getSID(switchIP);
            handleAddingSwitchAndItsPorts(sid);
            return;
        */

        /*
        * The following 'if-elseif-else' section: identify different trap OID, to process switch-boot / link-up / link-down
        */
        /*if(trapOID.compareTo(bootColdStartOID) == 0 || trapOID.compareTo(bootWarmStartOID) == 0)
        {//new switch
            if(isFakeSim == false){
            chassisID = (new SNMPHandler(cmethUtil)).getLLDPChassis(switchIP);
            chassisID = chassisID.replaceAll(" ", ":");
            sid = HexString.toLong(chassisID);
            //TODO:should compare whether getLLDPChassis() == cmethUtil.getSID()
            //cmethUtil.addEntry(sid, switchIP);
            sid = cmethUtil.getSID(switchIP);
            }
            else sid = 1L;

            handleAddingSwitch(sid);
        }
        else */if(trapOID.compareTo(linkDownOID) == 0)
        {//link down
            short port = -1;
            //...retrieve the port number from the trap's content
            //...
            //...
            //sid = cmethUtil.getSID(switchIP);
            //sw = controller.getSwitch(sid);
            //((SwitchHandler)sw).deletePhysicalPort(new SNMPPhysicalPort(port));
        }
        else if(trapOID.compareTo(linkUpOID) == 0)
        {//link up
            short port = -1;
            String portName;
            if(isFakeSim == false){
            sid = cmethUtil.getSID(switchIP);
            SNMPSequence seq = pdu.getVarBindList();
            portName = getPortName(sid, seq);
            }
            else{
            port = 1;
            sid = 1L;
            portName = new String("eth" + port);
            }

            System.out.println("Get switch (ip: " + switchIP + ", mac:" + HexString.toHexString(sid) + ")'s link up trap, port number = " + port);
            handleAddingNewPort(sid, port, portName);
        }
        else{
            System.out.println("--> can't recognize this trap");
        }
        /* example
            trap OID:           1.3.6.1.6.3.1.1.5.4
            var bind list:      ( ( 1.3.6.1.2.1.1.3.0  4588 )  ( 1.3.6.1.6.3.1.1.4.1.0  1.3.6.1.6.3.1.1.5.4 )  ( 1.3.6.1.2.1.2.2.1.1.11  11 )  ( 1.3.6.1.2.1.2.2.1.7.11  1 )  ( 1.3.6.1.2.1.2.2.1.8.11  1 ) )
            ==>seq2 is the third (xxx xxx), i.e. ( 1.3.6.1.2.1.2.2.1.1.11  11 ); oidstr is ( 1.3.6.1.2.1.2.2.1.1.11  11 )'s first item, i.e. 1.3.6.1.2.1.2.2.1.1.11
        */
    }

    private void handleAddingSwitchAndItsPorts(Long sid){
        handleAddingSwitch(sid);
        while(controller.getSwitch(sid) == null){
            System.out.println("snmp4sdn-controller.handleNewConnection(" + sid + ") not yet done");
            try{
                Thread.sleep(500);
            }catch(Exception e){;}
        }
        scanAndAddPort(sid);
    }
    
    private ISwitch handleAddingSwitch(Long sid){
        ISwitch sw = controller.getSwitch(sid);
        if(sw != null){
            System.out.println("--> switch (ip: " + cmethUtil.getIpAddr(sid) + ", sid: " + HexString.toHexString(sid) + ") already join in controller");
        }
        else{
            System.out.println("--> a new switch (ip: " + cmethUtil.getIpAddr(sid) + ", sid: " + HexString.toHexString(sid) + ") join in controller");
            ((Controller)controller).handleNewConnection(sid);
        }
        return sw;
    }

    private void scanAndAddPort(Long sid){
            short port;
            SNMPPhysicalPort phyPort;
            Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(sid);
            for(Map.Entry<Short, String> entry : portIDTable.entrySet()){
                String portName = entry.getValue();
                port = entry.getKey().shortValue();
                phyPort = new SNMPPhysicalPort(port);
                phyPort.setName(portName);
                System.out.println("In SNMPListener.java, Add to switch (ip: " + cmethUtil.getIpAddr(sid) + ", mac:" + HexString.toHexString(sid) + ") a new port, port number = " + port);
                handleAddingNewPort(sid, port, portName);
            }
    }

    private String getPortName(Long sid, SNMPSequence seq){
            if(seq.size() < 3){
                System.out.println("link up trap's information format error!");
                System.exit(0);
            }
            SNMPSequence seq2 = (SNMPSequence)(((Vector)(seq.getValue())).elementAt(2));
            System.out.println(seq2.toString());
            String oidstr = ((SNMPObject)(((Vector)(seq2.getValue())).elementAt(0))).toString();
            short port = Short.parseShort(oidstr.substring(oidstr.lastIndexOf(".") + 1));
            Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(sid);
            String portName = portIDTable.get(new Short(port));
            return portName;
    }

    private Long getSIDfromLLDPChassis(String switchIP){
        String chassisID = (new SNMPHandler(cmethUtil)).getLLDPChassis(switchIP);
        chassisID = chassisID.replaceAll(" ", ":");
        Long sid = HexString.toLong(chassisID);
        return sid;
    }

    private void handleAddingNewPort(Long sid, short port, String portName){
        ISwitch sw = controller.getSwitch(sid);
        if(sw == null)System.out.println("ISwitch sw is null!"); 

        SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
        phyPort.setName(portName);

        SNMPPortStatus portStatus = new SNMPPortStatus();
        portStatus.setDesc(phyPort);
        portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());

        /*((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
        ((Controller)controller).takeSwitchEventMsg(sw, portStatus);*///done in the line below
        ((SwitchHandler)sw).handleMessages(portStatus);
    }

    private void fake2switch(){//s4s fake2sw
            Long sid = 2L;
            short port = 2;
            String switchIP = "10.217.0.32";
            System.out.println("fake switch (ip: " + switchIP + ", mac:" + HexString.toHexString(sid) + " added");
            ((Controller)controller).handleNewConnection(sid);
    }
    private void fake2switchport(){//s4s fake2sw
            Long sid = 2L;
            short port = 2;
            String switchIP = "10.217.0.32";
            System.out.println("fake port of switch (ip: " + switchIP + ", mac:" + HexString.toHexString(sid) + ")'s link up trap, port number = " + port);
            ISwitch sw = controller.getSwitch(sid);
            if(sw == null)System.out.println("ISwitch sw is null!");
            
            SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
            //Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(cmethUtil.getSID(switchIP));
            //String portName = portIDTable.get(new Short(port));
            //phyPort.setName(portName);
            phyPort.setName("eth2");

            SNMPPortStatus portStatus = new SNMPPortStatus();
            portStatus.setDesc(phyPort);
            portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());

            /*((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
            ((Controller)controller).takeSwitchEventMsg(sw, portStatus);*///done in the line below
            ((SwitchHandler)sw).handleMessages(portStatus);
    }
}
