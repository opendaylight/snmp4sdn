package org.opendaylight.snmp4sdn.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmpj.*;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.internal.SwitchHandler;
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus;
import org.opendaylight.snmp4sdn.protocol.SNMPPortStatus.SNMPPortReason;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import java.net.InetAddress;
import java.util.Vector;
import java.io.*;

public class SNMPListener implements SNMPv2TrapListener, Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(SNMPListener.class);

    private final IController controller;
    private CmethUtil cmethUtil;
    private SNMPTrapReceiverInterface trapReceiverInterface;
    private static final String bootColdStartOID = "1.3.6.1.6.3.1.1.5.1";
    private static final String bootWarmStartOID = "1.3.6.1.6.3.1.1.5.2";
    private static final String linkDownOID = "1.3.6.1.6.3.1.1.5.3";
    private static final String linkUpOID = "1.3.6.1.6.3.1.1.5.4";

    public SNMPListener(IController controller, CmethUtil cmethUtil){
        this.controller = controller;
        this.cmethUtil = cmethUtil;
        try{
            trapReceiverInterface = new SNMPTrapReceiverInterface(new PrintWriter(new PipedWriter(new PipedReader())));
            trapReceiverInterface.addv2TrapListener(this);
            trapReceiverInterface.startReceiving();
        }catch(Exception e){
            LOG.error("Problem starting Trap Interface", e);
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
            LOG.error("Got trapReceiverInterface exception", e);
        }
    }

    public void startListening(){
        try{
            trapReceiverInterface.startReceiving();
        }
        catch (Exception e)
        {
            LOG.error("Got trapReceiverInterface exception", e);
        }
    }

    @Override
    public void processv2Trap(SNMPv2TrapPDU pdu, String communityName, InetAddress agentIPAddress){
        LOG.debug("Got v2 trap:");
        LOG.debug("  sender IP address:  {}", agentIPAddress.getHostAddress());
        LOG.debug("  community name:     {}", communityName);
        LOG.debug("  system uptime:      {}", pdu.getSysUptime().toString());
        LOG.debug("  trap OID:           {}", pdu.getSNMPTrapOID().toString());
        LOG.debug("  var bind list:      {}", pdu.getVarBindList().toString());

        String switchIP = agentIPAddress.getHostAddress();
        String trapOID = pdu.getSNMPTrapOID().toString();
        if(trapOID.compareTo(bootColdStartOID) == 0 || trapOID.compareTo(bootWarmStartOID) == 0)
        {//new switch
            String chassisID = (new SNMPHandler(cmethUtil)).getLLDPChassis(switchIP);
            chassisID = chassisID.replaceAll(" ", ":");
            Long sid = HexString.toLong(chassisID);
            //TODO:should compare whether getLLDPChassis() == cmethUtil.getSID()
            //cmethUtil.addEntry(sid, switchIP);
            sid = cmethUtil.getSID(switchIP);
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
                LOG.error("Unexpected link up sequence size {}", seq.size());
                throw new RuntimeException("Link up trap's information format error");
            }
            SNMPSequence seq2 = (SNMPSequence)(((Vector)(seq.getValue())).elementAt(2));
            LOG.debug(seq2.toString());
            String oidstr = ((SNMPObject)(((Vector)(seq2.getValue())).elementAt(0))).toString();
            short port = Short.parseShort(oidstr.substring(oidstr.lastIndexOf(".") + 1));
            Long sid = cmethUtil.getSID(switchIP);
            LOG.info("Get switch (ip: {}, mac: {})'s link up trap, port number = {}", switchIP, HexString.toHexString(sid), port);
            ISwitch sw = controller.getSwitch(sid);
            if(sw == null)LOG.warn("ISwitch sw is null!");
            //((SwitchHandler)sw).updatePhysicalPort(new SNMPPhysicalPort(port));
            SNMPPortStatus portStatus = new SNMPPortStatus();
            SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
            portStatus.setDesc(phyPort);
            portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());
            ((Controller)controller).takeSwitchEventMsg(sw, portStatus);
        }
        else{
            LOG.info("--> can't recognize this trap");
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
