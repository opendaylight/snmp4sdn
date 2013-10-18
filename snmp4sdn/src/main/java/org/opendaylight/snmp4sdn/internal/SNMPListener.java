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

        String switchIP = agentIPAddress.getHostAddress();
        String trapOID = pdu.getSNMPTrapOID().toString();
        if(trapOID.compareTo(bootColdStartOID) == 0 || trapOID.compareTo(bootWarmStartOID) == 0)
        {//new switch
            String chassisID = (new SNMPHandler(cmethUtil)).getLLDPChassis(switchIP);
            chassisID = chassisID.replaceAll(" ", ":");
            Long sid = HexString.toLong(chassisID);
            cmethUtil.addEntry(sid, switchIP);
            ((Controller)controller).handleNewConnection(sid);
        }
        else if(trapOID.compareTo(linkDownOID) == 0)
        {//link down
            short port = -1;
            //...retrieve the port number from the trap's content
            //...
            //...
            Long sid = cmethUtil.getSID(switchIP);
            ISwitch sw = controller.getSwitch(sid);
            ((SwitchHandler)sw).deletePhysicalPort(new SNMPPhysicalPort(port));
        }
        else if(trapOID.compareTo(linkUpOID) == 0)
        {//link up
            SNMPSequence seq = pdu.getVarBindList();
            if(seq.size() < 3){
                System.out.println("link up trap's information format error!");
                System.exit(0);
            }
            SNMPSequence seq2 = (SNMPSequence)(((Vector)(seq.getValue())).elementAt(2));
            System.out.println(seq2.toString());
            String oidstr = ((SNMPObject)(((Vector)(seq2.getValue())).elementAt(0))).toString();
            short port = Short.parseShort(oidstr.substring(oidstr.lastIndexOf(".") + 1));
            Long sid = cmethUtil.getSID(switchIP);
            System.out.println("Get switch (ip: " + switchIP + ", mac:" + HexString.toHexString(sid) + ")'s link up trap, port number = " + port);
            ISwitch sw = controller.getSwitch(sid);
            ((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
            SNMPPortStatus portStatus = new SNMPPortStatus();
            portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());
            ((Controller)controller).takeSwitchEventMsg(sw, portStatus);
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

    private Long getSIDfromLLDPChassis(String switchIP){
        String chassisID = (new SNMPHandler(cmethUtil)).getLLDPChassis(switchIP);
        chassisID = chassisID.replaceAll(" ", ":");
        Long sid = HexString.toLong(chassisID);
        return sid;
    }
}
