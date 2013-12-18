/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPHandler{
    private static final Logger LOG = LoggerFactory.getLogger(SNMPListener.class);

    static final String portGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.2";//s4s: MAC_PORT_GET's OID
    static final String typeGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.3";//s4s: MAC_TYPE_GET's OID
    static final String portSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.3";//s4s: MAC_PORT_SET's OID
    static final String typeSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.4";//s4s: MAC_TYPE_SET's OID

    static final String lldpRemoteChassisIdOID = "1.0.8802.1.1.2.1.4.1.1.5";//s4s
    static final String lldpLocalChassisIdOID = "1.0.8802.1.1.2.1.3.2.0";//s4s
    static final String lldpLocalPortIdOID = "1.0.8802.1.1.2.1.3.7.1.3";//s4s
    static final String lldpRemotePortIdOID = "1.0.8802.1.1.2.1.4.1.1.7";//s4s

    //String requestTypeOIDs = {"1.1.1.1.1"};//(when all kinds of OID more and more, may consider to use requestTypeOIDs[typeID] instead of rasing one by one like above

    CmethUtil cmethUtil = null;

    public SNMPHandler(CmethUtil cmethUtil){
        this.cmethUtil = cmethUtil;
    }

    private SNMPv1CommunicationInterface createSNMPv1CommInterface(int version, InetAddress hostAddress, String community){
        try{

            // create a communications interface to a remote SNMP-capable device;
            // need to provide the remote host's InetAddress and the community
            // name for the device; in addition, need to  supply the version number
            // for the SNMP messages to be sent (the value 0 corresponding to SNMP
            // version 1)
            /*InetAddress hostAddress = InetAddress.getByName("10.0.1.1");
            String community = "public";
            int version = 0;    // SNMPv1*/

          SNMPv1CommunicationInterface comInterface = new SNMPv1CommunicationInterface(version, hostAddress, community);
          return comInterface;

        }
        catch(Exception e)
        {
          LOG.error("Exception during SNMP createSNMPv1CommInterface to switch {}", e);
        }

        return null;
    }

    //s4s
    /*
        for example:
        0x00400000 ( port 10 )
        |00|40|00|00|
        | 00000000 | 01000000 | 00000000 | 00000000 |
    */

    //s4s
    String convertToEthSwitchPortString(int port){
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        String sep = ":";//see what seperate character is assigned, eg. ":" or " " or ""  (i.e.  colon or blank or connected)
        int pow;
        String str = "";

        if(port <= 8){
            pow = 8 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + str + "0"  + sep + "00";
        }
        else if(port <= 16){
            pow = 16 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0" + str + sep + "00";
        }
        else if(port <= 24){
            pow = 24 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + str + "0";
        }
        else if(port <= 32){
            pow = 32 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "0" + str;
        }
        else{
            LOG.error("convertToEthSwitchPortString() is given port > 32! ({})", port);
            throw new IllegalArgumentException("convertToEthSwitchPortString() is given port > 32!");
        }

        return ans;
    }

    //s4s
    String convertToEthSwitchPortString_4bits_as_a_character(int port){
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        String sep = ":";//see what seperate character is assigned, eg. ":" or " " or ""  (i.e.  colon or blank or connected)
        int pow;
        String str = "";

        if(port <= 4){
            pow = 4 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + str + "0" + sep + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 8){
            pow = 8 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0" + str + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 12){
            pow = 12 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + str + "0" + sep + "00" + sep + "00";
        }
        else if(port <= 16){
            pow = 16 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "000" + str + "00" + sep + "00";
        }
        else if(port <= 20){
            pow = 20 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0000" + str + "0" + sep + "00";
        }
        else if(port <= 24){
            pow = 24 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "0" + str + "00";
        }
        else if(port <= 28){
            pow = 28 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "00" + str + "0";
        }
        else if(port <= 32){
            pow = 32 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "00" + sep + "0" + str;
        }
        else{
            LOG.error("convertToEthSwitchPortString() is given port > 32! ({})", port);
            throw new IllegalArgumentException("convertToEthSwitchPortString() is given port > 32!");
        }

        return ans;
    }

    //s4s
    private String macAddrToOID(String macAddr){
        String macOID = "";
        for(int i = 0; i < 17; ){
            macOID += ".";
            macOID  += new Integer(Integer.parseInt(macAddr.substring(i, i + 2), 16)).toString();
            i += 3;
        }
        return macOID.substring(1, macOID.length());
    }

    //s4s
    private byte[] OIDToMacAddrBytes(String oid){
        byte[] result = new byte[6];
        int loc1 = 0, loc2 = 0;
        int count = 0;
        while(loc1 < oid.length()){
            if(count > 5){
                throw new IllegalArgumentException("fwd table has mac addr longer than 6 bytes");
            }
            loc2 = oid.indexOf(".", loc1 + 1);
            if(loc2 < 0)
                loc2 = oid.length();
            String subaddr = oid.substring(loc1, loc2);
            byte subaddrByte = (byte)(Integer.parseInt(subaddr));
            result[count++] = subaddrByte;
            loc1 = loc2 + 1;
        }
        return result;
    }

    //s4s
    private boolean setFwdTableEntry(SNMPv1CommunicationInterface comInterface, String destMac, short vlan, int port, int type){
        LOG.debug("enter setFwdTableEntry()...");
        try{
            String macOid = macAddrToOID(destMac);
            String portOid = portSetOID + "." + vlan + "." + macOid + ".0";
            String typeOid = typeSetOID + "." + vlan + "." + macOid + ".0";

            byte[] convPort = new HexString().fromHexString(convertToEthSwitchPortString(port));
            SNMPOctetString portOStr =  new SNMPOctetString(convPort);
            SNMPInteger typeInt =  new SNMPInteger(type);

            LOG.info("mac (" + destMac +")'s OID: " + macOid);
            LOG.info("type: " + typeInt.toString());

            if(type == 2){//delete entry
                SNMPVarBindList newVars = comInterface.setMIBEntry(typeOid, typeInt);
                LOG.info("set OID  " + typeOid + ", new value = " + typeInt.getClass().getName() + ":" + typeInt);
            }
            else if(type == 3){//add or modify entry
                LOG.info("port: " + portOStr.toString());

                String[] oids = {typeOid, portOid};
                SNMPObject [] newValues = {typeInt, portOStr};
                SNMPVarBindList newVars = comInterface.setMIBEntry(oids, newValues); //comInterface.setMIBEntry() can either input array or variable, like here or below

                for(int i = 0; i < oids.length; i++){
                    LOG.info("set OID  " + oids[i] + ", new value = " + newValues[i].getClass().getName() + ":" + newValues[i]);
                }
            }
            else{
                LOG.error("Error: given type (type {}) invalid", typeInt);
                throw new IllegalArgumentException("Invalid type encoutered");
            }

            return true;
        }
        catch(Exception e)
        {
            LOG.warn("Exception during SNMP setMIBEntry", e);
            return false;
        }
   }

    //s4s
    /*private String getIpAddr(Long macAddr){
        //look up table...
        return "10.216.0.31";
    }*///move to CmethUtil

    public Status sendBySNMP(Flow flow, int modType, Long sw_macAddr){
        LOG.debug("enter SNMPHandler.sendBySNMP()");

        LOG.debug("retrieving the metrics in the Flow...");
        //retrieve from the flow: (1)src mac (2)dest mac (3)the port value, to write into fwd table
            //to retrieve (1)&(2)
        Match match = flow.getMatch();
        MatchField fieldDlSrc= match.getField(MatchType.DL_SRC);
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        String srcMac = HexString.toHexString((byte[])fieldDlSrc.getValue());
        String destMac = HexString.toHexString((byte[])fieldDlDest.getValue());
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = ((Short)(fieldVlan.getValue())).shortValue();
            //to retrieve (3)
        Action action = flow.getActions().get(0);
        if(flow.getActions().size() > 1) {
            LOG.error("flow.getActions() > 1");
            throw new IllegalArgumentException("More than one action present in the flow");
        }
        if(action.getType() != ActionType.OUTPUT){
            LOG.error("flow's action is not to set OUTPUT port!");
            throw new IllegalArgumentException("flow's action is not to set OUTPUT port!");
        }
        NodeConnector oport = ((Output)action).getPort();


        //Use snmp to write to switch fwd table...

        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can set fwd table entry
           // System.out.println("going to set fwd table entry...");
            Short portShort = (Short)(oport.getID());
            short portID = portShort.shortValue();
            int portInt = (int)portID;
            boolean result = setFwdTableEntry(comInterface, destMac, vlan, portInt, modType);//oport.getID() is the port.  modType as 3 means "learned". modType as 2 means "invalid"
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", "snmp to set fwd table", "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!", sw_macAddr, e);
            throw new RuntimeException("sw_macAddr into InetAddress.getByName() error!", e);
        }
    }

    private int readFwdTableEntry(SNMPv1CommunicationInterface comInterface, short vlan, String destMac){
        LOG.debug("enter readFwdTableEntry()...");
        try{
            String macOid = macAddrToOID(destMac);
            String portOid = portGetOID + "." + vlan + "." + macOid;
            LOG.info("to retieve mac ({})'s port, the OID: {}", destMac, portOid);

            SNMPVarBindList newVars = comInterface.getMIBEntry(portOid);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
            int valueInt = ((BigInteger)value.getValue()).intValue();

            LOG.info("get value {}: {}", value.getClass().getName(), valueInt);

            return valueInt;

        }
        catch(Exception e)
        {
            LOG.warn("Exception during SNMP getMIBEntry", e);
            return -1;//meaning fail
        }
    }

    private Map<String, Integer> readAllFwdTableEntry(SNMPv1CommunicationInterface comInterface){
        LOG.debug("enter readAllFwdTableEntry()...");
        Map<String, Integer> table =  new HashMap<String, Integer>();

        try{
            LOG.debug("to retieve oid {}'s value...", portGetOID);

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(portGetOID);
            LOG.info("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
                int valueInt = ((BigInteger)value.getValue()).intValue();
                String snmpOIDstr = snmpOID.toString();
                String vlanmacOID = snmpOIDstr.substring(portGetOID.length() + 1);
                String vlanOID = vlanmacOID.substring(0, vlanmacOID.indexOf("."));
                String macOID = vlanmacOID.substring(vlanmacOID.indexOf(".") + 1);
                table.put(vlanmacOID, new Integer(valueInt));
                LOG.debug("Retrieved OID: " + snmpOID +" (the vlan:" + vlanOID + ", mac:" + macOID +"), value " + value.getClass().getName() + ":" + valueInt);
            }
            return table;

           }
           catch(Exception e)
           {
               LOG.warn("Exception during SNMP getMIBEntry", e);
               return null;
           }
      }

    public FlowOnNode readFlowRequest(Flow flow, Node node){
        LOG.debug("enter SNMPHandler.readFlowRequest()");

        LOG.debug("retrieving the metrics in the Flow...");
        //retrieve dest mac from the flow
        Match match = flow.getMatch();
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = ((Short)(fieldVlan.getValue())).shortValue();
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        String destMac = HexString.toHexString((byte[])fieldDlDest.getValue());

        //Use snmp to read switch fwd table...

        Long sw_macAddr = (Long) node.getID();
        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!", sw_macAddr, e);
            throw new RuntimeException("Failed to get address for switch", e);
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. read fwd table entry
        //System.out.println("going to read fwd table entry...");
        int port = readFwdTableEntry(comInterface, vlan, destMac);
        if(port < 0) return null;

        //3. convert the retrieved entry to FlowOnNode
        NodeConnector oport = NodeConnectorCreator.createNodeConnector("SNMP", (short)port, node);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow flown = new Flow(flow.getMatch(), actions);
        return new FlowOnNode(flown);
    }

    //return value: 1. null -- switch not found  2. an empty List<FlowOnNode> -- switch found and has no entries
    public List<FlowOnNode>  readAllFlowRequest(Node node){
        LOG.debug("enter SNMPHandler.readAllFlowRequest()");

        //Use snmp to read switch fwd table...

        Long sw_macAddr = (Long) node.getID();
        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!\n", sw_macAddr, e);
            throw new RuntimeException("Failed to convert switch mac address into an address", e);
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //System.out.println("going to read fwd table entry...");
        Map<String, Integer> entries = readAllFwdTableEntry(comInterface);
        if(entries == null) return null;
        return forwardingTableEntriesToFlows(entries, node);
    }

    private List<FlowOnNode> forwardingTableEntriesToFlows(Map<String, Integer> entries, Node node){
        List<FlowOnNode> list = new ArrayList<FlowOnNode>();
        for(Map.Entry<String, Integer> entry : entries.entrySet()){
            Match match = new Match();
            String str = entry.getKey();
            short vlan = Short.parseShort(str.substring(0, str.indexOf(".")));
            byte[] madAddrBytes = OIDToMacAddrBytes(str.substring(str.indexOf(".") + 1));

            match.setField(MatchType.DL_VLAN, vlan);
            match.setField(MatchType.DL_DST, madAddrBytes);
            List<Action> actions = new ArrayList<Action>();
            NodeConnector oport = NodeConnectorCreator.createNodeConnector("SNMP", Short.parseShort(entry.getValue().toString()), node);
            actions.add(new Output(oport));

            Flow flow = new Flow(match, actions);
            list.add(new FlowOnNode(flow));
        }
        return list;
    }

    private Short retrievePortNumFromChassisOID(String oidstr){
        //e.g. oidstr as "iso.0.8802.1.1.2.1.4.1.1.5.0.49.1", then return "49"
        int tail = oidstr.lastIndexOf(".");
        int head = oidstr.substring(0, tail).lastIndexOf(".") + 1;
        //System.out.println(oidstr + " ==> head=" + head + ",tail=" + tail);
        String ansStr= oidstr.substring(head, tail);
        Short ans = Short.parseShort(ansStr);
        //System.out.println("oidstr (" + oidstr +") to retrieve port number:" + ans);
        return ans;
    }

    private Map<Short, String> readLLDPRemoteChassisIDEntries(SNMPv1CommunicationInterface comInterface){
        LOG.debug("enter readLLDPRemoteChassisIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //System.out.println("to retieve oid " + lldpRemoteChassisIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpRemoteChassisIdOID);
            //System.out.println("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //System.out.println("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;

           }
           catch(Exception e)
           {
               LOG.warn("Exception during SNMP getMIBEntry", e);
               return null;
           }
      }

    public Map<Short, String>  readLLDPAllRemoteChassisID(Long sw_macAddr){//return <portNumber, remoteChassisID>
        LOG.debug("enter SNMPHandler.readLLDPAllRemoteChassisID()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!\n", sw_macAddr, e);
            throw new RuntimeException("Failed to convert switch mac address into an address", e);
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //System.out.println("going to read LLDP remote chassis IDs...");
        return readLLDPRemoteChassisIDEntries(comInterface);
    }

    public String getLLDPChassis(Long sw_macAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        LOG.debug("enter SNMPHandler.getLLDPChassis({})...", HexString.toHexString(sw_macAddr));
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);

            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
            //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            SNMPVarBindList newVars = comInterface.getMIBEntry(lldpLocalChassisIdOID);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            String valueStr = HexString.toHexString(valueBytes);

            LOG.info("to retieve switch ({})'s local chassis (OID: {}), get value: {}", sw_macAddr, lldpLocalChassisIdOID, valueStr);
            return valueStr;

        }
        catch(Exception e)
        {
            LOG.warn("Exception during SNMP getLLDPChassis", e);
            return null;//meaning fail
        }
    }

    public String getLLDPChassis(String sw_ipAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        LOG.debug("enter SNMPHandler.getLLDPChassis()...");
        try{
            InetAddress swIpAddr = InetAddress.getByName(sw_ipAddr);
            if(swIpAddr == null) return null;

            String community = cmethUtil.getSnmpCommunity(cmethUtil.getSID(sw_ipAddr));
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, swIpAddr, community);
            LOG.info("snmp connection created...swtich IP addr={}, community={}", sw_ipAddr, community);

            SNMPVarBindList newVars = comInterface.getMIBEntry(lldpLocalChassisIdOID);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            String valueStr = HexString.toHexString(valueBytes);

            LOG.debug("to retieve switch ({})'s local chassis (OID: {}), get value: {}", sw_ipAddr, lldpLocalChassisIdOID, valueStr);
            return valueStr;

        }
        catch(Exception e)
        {
            LOG.warn("Exception during SNMP getLLDPChassis", e);
            return null;//meaning fail
        }
    }

    public Map<Short, String>  readLLDPLocalPortIDs(Long sw_macAddr){//return <portNumber, remoteChassisID>
        LOG.debug("enter SNMPHandler.readLLDPLocalPortIDs()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!", sw_macAddr, e);
            throw new RuntimeException("Failed to convert switch mac address into an address", e);
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //System.out.println("going to read LLDP local port IDs...");
        return readLLDPLocalPortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPLocalPortIDEntries(SNMPv1CommunicationInterface comInterface){
        LOG.debug("enter SNMPHandler.readLLDPLocalPortIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //System.out.println("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpLocalPortIdOID);
            //System.out.println("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromPortOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //System.out.println("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;

           }
           catch(Exception e)
           {
               LOG.warn("Exception during SNMP getMIBEntry", e);
               return null;
           }
      }

    private Short retrievePortNumFromPortOID(String oidstr){//input is eg. :iso.0.8802.1.1.2.1.3.7.1.3.52, return is eg. 52
        //e.g. oidstr as "iso.0.8802.1.1.2.1.4.1.1.5.0.49.1", then return "49"
        int index = oidstr.lastIndexOf(".") + 1;
        String ansStr= oidstr.substring(index);
        Short ans = Short.parseShort(ansStr);
        //System.out.println("oidstr (" + oidstr +") to retrieve port number:" + ans);
        return ans;
    }

    public Map<Short, String>  readLLDPRemotePortIDs(Long sw_macAddr){//return <portNumber, remoteChassisID>
        LOG.debug("enter SNMPHandler.readLLDPRemotePortIDs()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            LOG.error("sw_macAddr {} into InetAddress.getByName() error!", sw_macAddr, e);
            throw new RuntimeException("Failed to convert switch mac address into an address", e);
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //System.out.println("going to read LLDP remote chassis IDs...");
        return readLLDPRemotePortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPRemotePortIDEntries(SNMPv1CommunicationInterface comInterface){
        LOG.debug("enter readLLDPRemotePortIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //System.out.println("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpRemotePortIdOID);
            //System.out.println("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //System.out.println("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;

           }
           catch(Exception e)
           {
               LOG.warn("Exception during SNMP getMIBEntry", e);
               return null;
           }
      }

    private String errorString(String phase, String action, String cause) {
        return "Failed to "
                + ((phase != null) ? phase + " the " + action
                        + " flow message: " : action + " the flow: ") + cause;
    }

}
