/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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
import org.opendaylight.controller.sal.core.Config;
import org.opendaylight.controller.sal.core.State;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import java.net.SocketException;

public class SNMPListener implements SNMPv2TrapListener, Runnable, CommandProvider{
    private static final Logger logger = LoggerFactory
            .getLogger(SNMPListener.class);

    private final IController controller;
    private CmethUtil cmethUtil;
    private SNMPTrapReceiverInterface trapReceiverInterface;
    private static String bootColdStartOID = "1.3.6.1.6.3.1.1.5.1";
    private static String bootWarmStartOID = "1.3.6.1.6.3.1.1.5.2";
    private static String linkDownOID = "1.3.6.1.6.3.1.1.5.3";
    private static String linkUpOID = "1.3.6.1.6.3.1.1.5.4";

    //TODO: a unique module to provide general constant parameters for all modules in snmp4sdn plugin
    private static short MAX_PORT_NUM = 96;
    private static short MIN_PORT_NUM = 1;

    private boolean isFakeSim = false;//s4s: if true, generate fake parameters
    private boolean isSimHybridLinkTrap = false;//true: auto generate OF-Eth link-up/down event swID portNum. false: auto generate Eth-Eth link-up/down event swID portNum

    //for switches with SNMP-to-SNMP edges
    private int fakeSwNum = 0;
    private int fakeSwPortNum = 0;
    private int fakeIpCount = 1;
    private short fakeSwPortCount = 0;

    //for switches with OF-to-SNMP edge
    private int fakeSwNum2 = 0;
    private int fakeSwPortNum2 = 0;
    private int fakeIpCount2 = 1;
    private short fakeSwPortCount2 = 0;

    private boolean isTrapMechnismCancled = true;//s4s: if true, trap mechanism is cancled
    //private boolean isRegardAnyTrapAsSwJoin = false;//s4s: if true, regard any received snmp trap as a switch want to join in

    private static int recvTrapNum = 0;

    public SNMPListener(IController controller, CmethUtil cmethUtil) {
        this.controller = controller;
        this.cmethUtil = cmethUtil;
        try{
            trapReceiverInterface = new SNMPTrapReceiverInterface(new PrintWriter(new PipedWriter(new PipedReader())));
            trapReceiverInterface.addv2TrapListener(this);
            trapReceiverInterface.startReceiving();
        }catch(SocketException e){
            logger.warn("Unable to Bind to SNMP Trap Port: {} " , e.getMessage());
        }catch(Exception e){
            logger.warn("Problem starting SNMP Trap Interface: {}" , e);
        }
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    public void run(){
    }

    public void start(){
    }

    public void stopListening(){
        try{
            trapReceiverInterface.stopReceiving();
            trapReceiverInterface.closeSocket();
            trapReceiverInterface.removev2TrapListener(this);
        }
        catch (Exception e)
        {
            logger.warn("Got trapReceiverInterface exception: {}", e);
        }
    }

    public void startListening(){
        try{
            trapReceiverInterface.startReceiving();
        }
        catch (Exception e)
        {
            logger.warn("Got trapReceiverInterface exception: {}", e);
        }
    }

    @Override
    public void processv2Trap(SNMPv2TrapPDU pdu, String communityName, InetAddress agentIPAddress){
        recvTrapNum += 1;

        if(pdu == null){
            logger.debug("ERROR: processv2Trap(): given SNMPv2TrapPDU pdu is null");
            return;
        }
        if(communityName == null){
            logger.debug("ERROR: processv2Trap(): given communityName is null");
            return;
        }
        if(agentIPAddress == null){
            logger.debug("ERROR: processv2Trap(): given InetAddress agentIPAddress is null");
            return;
        }

        if(pdu.getSNMPTrapOID() == null){
            logger.debug("ERROR: processv2Trap(): given SNMPv2TrapPDU pdu, pud.getSNMPTrapOID() is null");
            return;
        }
        if(pdu.getVarBindList() == null){
            logger.debug("ERROR: processv2Trap(): given SNMPv2TrapPDU pdu, pud.getVarBindList() is null");
            return;
        }
        if(agentIPAddress.getHostAddress() == null){
            logger.debug("ERROR: processv2Trap(): given InetAddress agentIPAddress, agentIPAddress.getHostAddress() is null");
            return;
        }
        String switchIP = agentIPAddress.getHostAddress();
        String trapOID = pdu.getSNMPTrapOID().toString();
        SNMPSequence pduSeq = pdu.getVarBindList();
        String chassisID;
        Long sid;
        short port = getEventPort(trapOID, pduSeq);

        logger.debug("Got v2 trap - {} (trap OID {}):", getTrapType(trapOID), trapOID);
        logger.debug("  sender IP address: {}", switchIP);
        /*logger.debug("  community name:  {}", communityName);
        logger.debug("  system uptime:      {}", pdu.getSysUptime().toString());
        logger.debug("  trap OID:              {}", trapOID);*/
        //logger.debug("  var bind list:         {}", pduSeq.toString());
        logger.debug("  event port:         {}", port);

        //DB checking - switch ID
        sid = cmethUtil.getSID(switchIP);
        if(sid == null){
            logger.debug("ERROR: this switch is not listed in iplist.csv (can't find the sid of IP address {})", switchIP);
            return;
        }

        //DB checking - snmp community
        if(cmethUtil.getSnmpCommunity(sid) == null){
            logger.debug("ERROR: this switch is not listed in iplist.csv (can't find the community '{}' of IP address {})", communityName, switchIP);
            return;
        }
        if(!communityName.equals(cmethUtil.getSnmpCommunity(sid))){
            logger.debug("ERROR: this switch's community '{}' doesn't belong to our SNMP community '{}'", communityName, cmethUtil.getSnmpCommunity(sid));
            return;
        }

        

        /*
        //The following sections of isRegardAnyTrapAsSwJoin: regard any trap as switch join

        //add new switch
        if(isRegardAnyTrapAsSwJoin){
            if(controller.getSwitch(sid) == null)
                handleAddingSwitchAndItsPorts(sid);
        }
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

            //Produce fake switchID and portID. (It's also safe to remove this if{} code, the true data from trap still remains for later processing.)
            if(isFakeSim){
                if(isSimHybridLinkTrap){
                    getNextSimHybridLinkSwAndPort();//this line replaces the following marked lines
                    switchIP = new String("192.168.0." + (fakeSwNum + fakeIpCount2));
                    sid = cmethUtil.getSID(switchIP);
                    port = fakeSwPortCount2;
                }
                else{
                    getNextSimEthEthLinkSwAndPort();//this line replaces the following marked lines
                    switchIP = new String("192.168.0." + fakeIpCount);
                    sid = cmethUtil.getSID(switchIP);
                    port = fakeSwPortCount;
                }

                logger.debug("To created fake link-down trap of Switch IP {} Port {}", switchIP, port);
            }

            logger.info("Get link-down trap from switch (ip:{}, mac:{}) port number = {}", switchIP, HexString.toHexString(sid), port);

            handleDeletingPort(sid, port);
        }
        else if(trapOID.compareTo(linkUpOID) == 0)
        {//link up
            String portName = getLinkUpPortName(sid, pduSeq);
            if(portName == null){
                logger.debug("ERROR: processv2Trap: linkUpOID case: call getLinkUpPortName() given switch ID {} and SNMPSequence {}, gets null", sid, pduSeq);
                return;
            }

            //Produce fake switchID and portID. (It's also safe to remove this if{} code, the true data from trap still remains for later processing.)
            if(isFakeSim){
                if(isSimHybridLinkTrap){
                    getNextSimHybridLinkSwAndPort();//this line replaces the following marked lines
                    switchIP = new String("192.168.0." + (fakeSwNum + fakeIpCount2));
                    sid = cmethUtil.getSID(switchIP);
                    port = fakeSwPortCount2;
                }
                else{
                    getNextSimEthEthLinkSwAndPort();//this line replaces the following marked lines
                    switchIP = new String("192.168.0." + fakeIpCount);
                    sid = cmethUtil.getSID(switchIP);
                    port = fakeSwPortCount;
                }

                portName = new String("eth" + port);
                logger.debug("To created fake link-up trap of Switch IP {} Port {}", switchIP, port);
            }

            logger.info("Get link-up trap from switch (ip:{}, mac:{}) port number = {}", switchIP, HexString.toHexString(sid), port);

            handleAddingNewPort(sid, port, portName);
        }
        else{
            logger.debug("ERROR: can't recognize this trap (OID: {}) from switch {}", trapOID, switchIP);
        }
    }

    private void handleAddingSwitchAndItsPorts(Long sid){
        handleAddingSwitch(sid);
        while(controller.getSwitch(sid) == null){
            logger.trace("snmp4sdn-controller.handleNewConnection({}) not yet done", sid);
            try{
                Thread.sleep(500);
            }catch(Exception e){;}
        }
        scanAndAddPort(sid);
    }
    
    private ISwitch handleAddingSwitch(Long sid){
        ISwitch sw = controller.getSwitch(sid);
        if(sw != null){
            logger.trace("--> switch (ip: {}, sid: {}) already join in controller", cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
        }
        else{
            logger.trace("--> a new switch (ip: {}, sid: {}) join in controller", cmethUtil.getIpAddr(sid), HexString.toHexString(sid));
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
                logger.trace("In SNMPListener, add to switch (ip:{}, mac:{}) a new port, port number = ", cmethUtil.getIpAddr(sid), HexString.toHexString(sid), port);
                handleAddingNewPort(sid, port, portName);
            }
    }

    private short getLinkUpPortNum(SNMPSequence seq){
            if(seq.size() < 3){
                logger.warn("link up trap's information format error!");
                return -1;
            }
            SNMPSequence seq2 = (SNMPSequence)(((Vector)(seq.getValue())).elementAt(2));
            String oidstr = ((SNMPObject)(((Vector)(seq2.getValue())).elementAt(0))).toString();
            short port = Short.parseShort(oidstr.substring(oidstr.lastIndexOf(".") + 1));

            return port;
    }

    private String getLinkUpPortName(Long sid, SNMPSequence seq){
            if(isFakeSim) return "fakePort";

            short port = getLinkUpPortNum(seq);
            if(port <= 0){
                logger.debug("ERROR: getLinkUpPortName(): call getLinkUpPortNum() given SNMPSequence {} fails (from switch {})", seq, sid);
                return null;
            }
            Map<Short, String> portIDTable = (new SNMPHandler(cmethUtil)).readLLDPLocalPortIDs(sid);
            if(portIDTable == null){
                logger.debug("ERROR: getLinkUpPortName(): call SNMPHandler.readLLDPLocalPortIDs(), given switch ID {}, fail", sid);
                return null;
            }
            String portName = portIDTable.get(new Short(port));
            if(portName == null){
                logger.debug("ERROR: getLinkUpPortName(): portIDTable of switch {} has no entry for port {}", sid, port);
                return null;
            }
            return portName;
    }

    //return true means event sent successfully, not mean successfully report to SAL
    //TODO: should change function name to such as 'handlePortChange'
    private boolean handleAddingNewPort(Long sid, short port, String portName){
        if(portName == null){
            logger.error("ERROR: handleAddingNewPort(): given switch {} port {} but null portName", sid, port); 
            return false;
        }
        if(!isValidPort(port)){
            logger.error("ERROR: handleAddingNewPort(): given switch {} portName {} but invalid port {}", sid, portName, port); 
            return false;
        }
        ISwitch sw = controller.getSwitch(sid);
        if(sw == null){
            logger.error("ERROR: handleAddingNewPort(): given switch {} port {} portName {}, the ISwitch for switch {} is null!", sid, port, portName, sid); 
            return false;
        }

        SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
        phyPort.setName(portName);
        phyPort.setConfig(Config.ADMIN_UP);//Bug fix: "port on/off" is not "add/remove port"
        SNMPPortStatus portStatus = new SNMPPortStatus();
        portStatus.setDesc(phyPort);
        portStatus.setReason((byte)SNMPPortReason.SNMPPPR_MODIFY.ordinal());//Bug fix: "port on/off" is not "add/remove port". "add port" is SNMPPPR_ADD. "port on" should be SNMPPPR_MODIFY
        ((SwitchHandler)sw).handleMessages(portStatus);
        return true;
    }

    //return true means event sent successfully, not mean successfully report to SAL
    private boolean handleDeletingPort(Long sid, short port){
        if(!isValidPort(port)){
            logger.error("ERROR: handleDeletingPort(): given switch {} but invalid port {}", sid, port); 
            return false;
        }
        ISwitch sw = controller.getSwitch(sid);
        if(sw == null){
            logger.error("ERROR: handleDeletingPort(): given switch {} port {}, the ISwitch for switch {} is null!", sid, port, sid); 
            return false;
        }

        SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
        phyPort.setConfig(Config.ADMIN_DOWN);//Bug fix: "port on/off" is not "add/remove port"
        SNMPPortStatus portStatus = new SNMPPortStatus();
        portStatus.setDesc(phyPort);
        portStatus.setReason((byte)SNMPPortReason.SNMPPPR_MODIFY.ordinal());//Bug fix: "port on/off" is not "add/remove port". "remove port" is SNMPPPR_DELETE. "port off" should be SNMPPPR_MODIFY
        //logger.debug("handleDeletingPort(): now will call SwitchHandler.handleMessages() for switch {} port {}", sid, port);
        ((SwitchHandler)sw).handleMessages(portStatus);
        return true;
    }

    private boolean isValidPort(short port){
        if(port >= MIN_PORT_NUM && port <= MAX_PORT_NUM)
            return true;
        else
            return false;
    }


    /**********
    * The following code are for simulation
    ***********/

    private void fakeSwitch(Long sid){//s4s fake2sw
            /*
            Long sid = 2L;
            short port = 2;
            String switchIP = "10.217.0.32";
            logger.trace("fake switch (ip:{}, mac:{}) added", switchIP, HexString.toHexString(sid));
            */
            logger.debug("Add fake switch (sid:{})", sid);
            ((Controller)controller).handleNewConnection(sid);
    }
    private void fakePort(Long sid, short port){//s4s fake2sw
            /*
            Long sid = 2L;
            short port = 2;
            String switchIP = "10.217.0.32";
            logger.trace("fake port of switch (ip:{}, mac:{})'s link up trap, port number = {}", switchIP, HexString.toHexString(sid), port);
            */
            logger.debug("Add fake port (Switch sid:{}, port {})", sid, port);
            ISwitch sw = controller.getSwitch(sid);
            if(sw == null)logger.error("ISwitch sw is null!");
            
            SNMPPhysicalPort phyPort = new SNMPPhysicalPort(port);
            phyPort.setName("eth-" + port);

            SNMPPortStatus portStatus = new SNMPPortStatus();
            portStatus.setDesc(phyPort);
            portStatus.setReason((byte)SNMPPortReason.SNMPPPR_ADD.ordinal());
            ((SwitchHandler)sw).handleMessages(portStatus);
    }

    private String getTrapType(String oid){
        if(oid.equals(linkDownOID))
            return "link-down";
        else if(oid.equals(linkUpOID))
            return "link-up";
        else if(oid.equals(bootColdStartOID))
            return "cold-start";
        else if(oid.equals(bootWarmStartOID))
            return "warm-start";
        else{
            return "(unknown)";
        }
    }

    //TODO: please complete all kinds of events OID
    private short getEventPort(String eventOID, SNMPSequence seq){
        /* example
            trap OID:           1.3.6.1.6.3.1.1.5.4 (link-up)
            var bind list:      ( ( 1.3.6.1.2.1.1.3.0  4588 )  ( 1.3.6.1.6.3.1.1.4.1.0  1.3.6.1.6.3.1.1.5.4 )  ( 1.3.6.1.2.1.2.2.1.1.11  11 )  ( 1.3.6.1.2.1.2.2.1.7.11  1 )  ( 1.3.6.1.2.1.2.2.1.8.11  1 ) )
            ==>seq2 is the third (xxx xxx), i.e. ( 1.3.6.1.2.1.2.2.1.1.11  11 ); oidstr is ( 1.3.6.1.2.1.2.2.1.1.11  11 )'s first item, i.e. 1.3.6.1.2.1.2.2.1.1.11
        */
        if(eventOID.equals(linkDownOID)){
            SNMPSequence seq2 = (SNMPSequence)(((Vector)(seq.getValue())).elementAt(2));
            String valuestr = ((SNMPInteger)(((Vector)(seq2.getValue())).elementAt(1))).toString();
            short port = Short.parseShort(valuestr);
            return port;
        }
        else if(eventOID.equals(linkUpOID)){
            short port = getLinkUpPortNum(seq);
            return port;
        }
        else if(eventOID.equals(bootColdStartOID))
            return -1;
        else if(eventOID.equals(bootWarmStartOID))
            return -1;
        else{
            return -1;
        }
    }

    private void getNextSimHybridLinkSwAndPort(){
        //switch
        if(fakeSwPortCount2 == fakeSwPortNum2){//when port number is used in a round, then switch number increases.
            fakeIpCount2 %= fakeSwNum2;
            fakeIpCount2 += 1;
        }
        //port
        fakeSwPortCount2 %= fakeSwPortNum2;
        fakeSwPortCount2 += 1;
    }

    private void getNextSimEthEthLinkSwAndPort(){
        //switch
        if(fakeSwPortCount == fakeSwPortNum){//when port number is used in a round, then switch number increases.
            fakeIpCount %= fakeSwNum;
            fakeIpCount += 1;
        }
        //port
        fakeSwPortCount %= fakeSwPortNum;
        fakeSwPortCount += 1;
    }


    /**************
    * The following are OSGi CLI commands for simulation!!
    ***************/

    public void _s4sEnableSim_SNMPListener(CommandInterpreter ci){
        isFakeSim = true;
        System.out.println("Simulation is enabled now (SNMPListener)");
    }

    public void _s4sDisableSim(CommandInterpreter ci){
        isFakeSim = false;
        System.out.println("Simulation is disabled now");
    }

    public void _s4sSimResetCounter(CommandInterpreter ci){
        //for switches with SNMP-to-SNMP edges
        fakeIpCount = 1;
        fakeSwPortCount = 0;

        //for switches with OF-to-SNMP edge
        fakeIpCount2 = 1;
        fakeSwPortCount2 = 0;

        recvTrapNum = 0;
    }

    public void _s4sPrintSnmpjStatus(CommandInterpreter ci){
        //trapReceiverInterface.printStatus();
    }

    public void _s4sSimHybridLinkTrap(CommandInterpreter ci){
        isSimHybridLinkTrap= true;
        System.out.println("Hybrid link trap event simulation is adopted now");
        if(isFakeSim == false)
            System.out.println("Alert: Simulation is not enabled now (The command to enable: s4sEnableSim)");
    }

    public void _s4sSimEthEthLinkTrap(CommandInterpreter ci){
        isSimHybridLinkTrap = false;
        System.out.println("Eth-to-Eth link trap event simulation is adopted now");
        if(isFakeSim == false)
            System.out.println("Alert: Simulation is not enabled now (The command to enable: s4sEnableSim)");
    }

    public void _s4sShowTrapNum(CommandInterpreter ci){
        System.out.println("Accumulated received trap number = " + recvTrapNum);
    }

    public void _s4sLinkDown(CommandInterpreter ci){
        String swStr = ci.nextArgument();
        String portStr = ci.nextArgument();
        String gargabe = ci.nextArgument();

        if(swStr == null || portStr == null || gargabe != null){
            System.out.println("Please use: s4sLinkDown <switch> <port>");
            return;
        }

        Long sid = new Long(Long.parseLong(swStr));
        short port = Short.parseShort(portStr);

        handleDeletingPort(sid, port);
    }

    public void _s4sLinkUp(CommandInterpreter ci){
        String swStr = ci.nextArgument();
        String portStr = ci.nextArgument();
        String gargabe = ci.nextArgument();

        if(swStr == null || portStr == null || gargabe != null){
            System.out.println("Please use: s4sLinkUp <switch> <port>");
            return;
        }

        Long sid = new Long(Long.parseLong(swStr));
        short port = Short.parseShort(portStr);

        handleAddingNewPort(sid, port, "eth" + port);
    }

    //stress test
    public void _s4sStressTest_setFakeSwitchAndPortNumber_SNMPListener(CommandInterpreter ci){
        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        String swNum = ci.nextArgument();
        String portNum = ci.nextArgument();
        String swNum2 = ci.nextArgument();//the switches with OF-to-SNMP edge
        String portNum2 = ci.nextArgument();//the number of port on a switch with OF-to-SNMP edge
        String garbage = ci.nextArgument();

        if(swNum == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitcheAndPortNumber <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(portNum == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitcheAndPortNumber <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(swNum2 == null){
            logger.debug("ERROR: the number of switches with OF-to-SNMP edge is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitcheAndPortNumber <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(portNum2 == null){
            logger.debug("ERROR: switch number is not given!");
            logger.debug("Please use: s4sStressTest_setFakeSwitcheAndPortNumber <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }
        if(garbage != null){
            logger.debug("Please use: s4sStressTest_setFakeSwitcheAndPortNumber <switch_number> <port_number> <of_snmp_switch_number> <of_snmp_switch_port_number>");
            return;
        }

        fakeSwNum = Integer.parseInt(swNum);
        fakeSwPortNum = Short.parseShort(portNum);
        fakeSwNum2 = Integer.parseInt(swNum2);
        fakeSwPortNum2 = Short.parseShort(portNum2);

        System.out.println("SNMPListener: fakeSwNum is set as " + fakeSwNum);
        System.out.println("SNMPListener: fakeSwPortNum is set as " + fakeSwPortNum);
        System.out.println("SNMPListener: fakeSwNum2 (OF-to-SNMP) is set as " + fakeSwNum2);
        System.out.println("SNMPListener: fakeSwPortNum2 (OF-to-SNMP) is set as " + fakeSwPortNum2);
    }

    //stress test
    public void _s4sStressTest_createFakeSwitchesAndPorts(CommandInterpreter ci){
        logger.debug("==========================");
        logger.debug("Creating fake switches and ports...");
        logger.debug("----------------------------------");

        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        //Ethernet-to-Ethernet switches
        for(int i = 1; i <= fakeSwNum; i++){
            fakeSwitch(new Long(i));
            for(short j = 1; j <= fakeSwPortNum; j++)
                fakePort(new Long(i), j);
        }

        //OF-to-Ethernet switches
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            fakeSwitch(new Long(i));
            for(short j = 1; j <= fakeSwPortNum2; j++)
                fakePort(new Long(i), j);
        }
    }

    //stress test
    public void _s4sStressTest_createFakeSwitches(CommandInterpreter ci){
        logger.debug("==========================");
        logger.debug("Creating fake switche...");
        logger.debug("----------------------------------");

        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        //Ethernet-to-Ethernet switches
        for(int i = 1; i <= fakeSwNum; i++){
            fakeSwitch(new Long(i));
        }

        //OF-to-Ethernet switches
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            fakeSwitch(new Long(i));
        }
    }

    //stress test
    public void _s4sStressTest_createFakeSwitchesAndPortsForOFtoSNMPEdges(CommandInterpreter ci){
        logger.debug("Creating fake switches and ports, for OF to SNMP edges...");
        if(!isFakeSim){
            logger.debug("Fail: The testing parameter 'isFakeSim' is off now, so this command is meaningless.");
            return;
        }

        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            fakeSwitch(new Long(i));
            for(short j = 1; j <= fakeSwPortNum2; j++)
                fakePort(new Long(i), j);
        }
    }

    //stress test
    public void _s4sStressTest_createFakeLinkDownEvents(CommandInterpreter ci){
        logger.debug("Creating fake link down events...");

        logger.debug("(fake link down events: for Eth-to-Eth switches:)");
        for(int i = 1; i <= fakeSwNum; i++){
            for(short j = 1; j <= fakeSwPortNum; j++){
                recvTrapNum += 1;
                logger.debug("Switch {} Port {} link down!", i, j);
                handleDeletingPort(new Long(i), j);
            }
        }

        logger.debug("(fake link down events: for OF-to-Eth switches:)");
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            for(short j = 1; j <= fakeSwPortNum2; j++){
                recvTrapNum += 1;
                logger.debug("Switch {} Port {} link down!", i, j);
                handleDeletingPort(new Long(i), j);
            }
        }
    }

    //stress test
    public void _s4sStressTest_createFakeLinkUpEvents(CommandInterpreter ci){
        logger.debug("Creating fake link up events...");

        logger.debug("(fake up up events: for Eth-to-Eth switches:)");
        for(int i = 1; i <= fakeSwNum; i++){
            for(short j = 1; j <= fakeSwPortNum; j++){
                recvTrapNum += 1;
                logger.debug("Switch {} Port {} link up!", i, j);
                handleAddingNewPort(new Long(i), j, "eth" + j);
            }
        }

        logger.debug("(fake link up events: for OF-to-Eth switches:)");
        for(int i = fakeSwNum + 1; i <= fakeSwNum + fakeSwNum2; i++){
            for(short j = 1; j <= fakeSwPortNum2; j++){
                recvTrapNum += 1;
                logger.debug("Switch {} Port {} link up!", i, j);
                handleAddingNewPort(new Long(i), j, "eth" + j);
            }
        }
    }

    @Override
    public String getHelp(){
        return "";
    }

}
