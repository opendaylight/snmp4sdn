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
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.reader.FlowOnNode;
import org.opendaylight.controller.sal.utils.NodeConnectorCreator;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

import org.opendaylight.snmp4sdn.protocol.util.HexString;
import org.opendaylight.snmp4sdn.protocol.SNMPFlowMod;
import org.opendaylight.snmp4sdn.VLANTable;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import org.opendaylight.snmp4sdn.ARPTableEntry;
//import org.opendaylight.snmp4sdn.SNMP4SDNErrorCode;
import org.opendaylight.snmp4sdn.STPPortState;

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
import java.io.*;

public class SNMPHandler{
    private static final Logger logger = LoggerFactory
            .getLogger(SNMPHandler.class);

    String portGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.2";//s4s: MAC_PORT_GET's OID
    String typeGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.3";//s4s: MAC_TYPE_GET's OID
    String portSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.3";//s4s: MAC_PORT_SET's OID
    String typeSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.4";//s4s: MAC_TYPE_SET's OID

    String lldpRemoteChassisIdOID = "1.0.8802.1.1.2.1.4.1.1.5";//s4s
    String lldpLocalChassisIdOID = "1.0.8802.1.1.2.1.3.2.0";//s4s
    String lldpLocalPortIdOID = "1.0.8802.1.1.2.1.3.7.1.3";//s4s
    String lldpRemotePortIdOID = "1.0.8802.1.1.2.1.4.1.1.7";//s4s

    String vlanNameOID = "1.3.6.1.2.1.17.7.1.4.3.1.1";
    String vlanEgressPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.2";
    String vlanForbiddenEgressPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.3";
    String vlanUntaggedPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.4";
    String vlanRowStatusOID = "1.3.6.1.2.1.17.7.1.4.3.1.5";
    
    /*ipNetToMediaPhysAddress (ipNetToPhysicalPhysAddress is alternative choice)
    # snmpwalk -v2c -c public 192.168.0.32 1.3.6.1.2.1.4.22.1.2
    iso.3.6.1.2.1.4.22.1.2.5121.192.168.0.0 = Hex-STRING: FF FF FF FF FF FF
    iso.3.6.1.2.1.4.22.1.2.5121.192.168.0.32 = Hex-STRING: 90 94 E4 23 13 E0
    iso.3.6.1.2.1.4.22.1.2.5121.192.168.18.1 = Hex-STRING: 00 A0 D1 EA 3C 7C
    */
    String arpTableEntryPhyAddrOID = "1.3.6.1.2.1.4.22.1.2";    
    String midStuffForArpTableEntryOID = "5121";//TODO: d-link. vender-specific?

    /*ipNetToMediaType
    root@ubuntu:~# snmpwalk -v 2c -c private 192.168.0.32 1.3.6.1.2.1.4.22.1.4
    iso.3.6.1.2.1.4.22.1.4.5121.192.168.0.0 = INTEGER: 1
    iso.3.6.1.2.1.4.22.1.4.5121.192.168.0.32 = INTEGER: 1
    iso.3.6.1.2.1.4.22.1.4.5121.192.168.18.1 = INTEGER: 4

    1 : other
    2 : invalid
    3 : dynamic
    4 : static
    */
    String arpTableEntryTypeOID = "1.3.6.1.2.1.4.22.1.4";
    int arpTableEntryType_other = 1;
    int arpTableEntryType_invalid = 2;
    int arpTableEntryType_dynamic = 3;
    int arpTableEntryType_static = 4;

    /*dot1dStpPortState
    root@ubuntu:~# snmpwalk -v2c -c public 192.168.0.32 1.3.6.1.2.1.17.2.15.1.3
    iso.3.6.1.2.1.17.2.15.1.3.1 = INTEGER: 5
    iso.3.6.1.2.1.17.2.15.1.3.2 = INTEGER: 5
    ...
    iso.3.6.1.2.1.17.2.15.1.3.24 = INTEGER: 1 //all ports are listed

    1 : disabled
    2 : blocking
    3 : listening
    4 : learning
    5 : forwarding
    6 : broken
    */
    String stpPortStateOID = "1.3.6.1.2.1.17.2.15.1.3";

    String stpPortEnableOID = "1.3.6.1.2.1.17.2.15.1.4";//1: enable, 2: disable

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
            logger.error("In createSNMPv1CommInterface(), Exception during SNMP createSNMPv1CommInterface to switch {}: {}", hostAddress, e);
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
            logger.error("convertToEthSwitchPortString() is given port > 32!");
            return null;
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
            logger.error("convertToEthSwitchPortString() is given port > 32!");
            return null;
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
                logger.error("fwd table has mac addr longer than 6 bytes");
                return null;
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
        logger.debug("enter SNMPHandler.setFwdTableEntry()...");
        try{
            String macOid = macAddrToOID(destMac);
            String portOid = portSetOID + "." + vlan + "." + macOid + ".0";
            String typeOid = typeSetOID + "." + vlan + "." + macOid + ".0";

            String convPortStr = convertToEthSwitchPortString(port);
            if(convPortStr == null){
                return false;
            }
            byte[] convPort = new HexString().fromHexString(convPortStr);
            SNMPOctetString portOStr =  new SNMPOctetString(convPort);
            SNMPInteger typeInt =  new SNMPInteger(type);

            logger.info("switch ({})'s OID: {}", destMac, macOid);
            logger.info("type: {}", typeInt.toString());

            if(type == 2){//delete entry
                SNMPVarBindList newVars = comInterface.setMIBEntry(typeOid, typeInt);
                logger.info("set OID {}: as new value of {} = {}", typeOid, typeInt.getClass().getName(), typeInt);
            }
            else if(type == 3){//add or modify entry
                logger.info("port: {}", portOStr.toString());

                String[] oids = {typeOid, portOid};
                SNMPObject [] newValues = {typeInt, portOStr};
                SNMPVarBindList newVars = comInterface.setMIBEntry(oids, newValues); //comInterface.setMIBEntry() can either input array or variable, like here or below

                for(int i = 0; i < oids.length; i++){
                    logger.info("set OID {}: new value of {} = {}", oids[i], newValues[i].getClass().getName(), newValues[i]);
                }
            }
            else{
                logger.error("Error: given type (type {}) invalid", typeInt);
                return false;
            }

            return true;
        }
        catch(Exception e)
        {
            logger.error("In setFwdTableEntry(), Exception during SNMP setMIBEntry: {}", e);
            return false;
        }
   }

    //s4s
    /*private String getIpAddr(Long macAddr){
        //look up table...
        return "10.216.0.31";
    }*///move to CmethUtil

    public Status sendBySNMP(Flow flow, int modType, Long sw_macAddr){
        logger.debug("enter SNMPHandler.sendBySNMP()");

        logger.debug("retrieving the metrics in the Flow...");
        //retrieve from the flow: (1)src mac (2)dest mac (3)the port value, to write into fwd table
            //to retrieve (1)&(2)
        Match match = flow.getMatch();
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        String destMac = HexString.toHexString((byte[])fieldDlDest.getValue());
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = ((Short)(fieldVlan.getValue())).shortValue();
            //to retrieve (3)
        Action action = flow.getActions().get(0);
        if(flow.getActions().size() > 1) {
            logger.error("flow.getActions() > 1");
            return new Status(StatusCode.NOTALLOWED, null);
        }
        if(action.getType() != ActionType.OUTPUT){
            logger.error("flow's action is not to set OUTPUT port!");
            return new Status(StatusCode.NOTALLOWED, null);
        }
        NodeConnector oport = ((Output)action).getPort();


        //Use snmp to write to switch fwd table...

        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can set fwd table entry
           // logger.debug("going to set fwd table entry...");
            Short portShort = (Short)(oport.getID());
            short portID = portShort.shortValue();
            int portInt = (int)portID;
            boolean result = setFwdTableEntry(comInterface, destMac, vlan, portInt, modType);//oport.getID() is the port.  modType as 3 means "learned". modType as 2 means "invalid"
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", "snmp to set fwd table", "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr {} into InetAddress.getByName() error: {}", sw_macAddr, e);
            return new Status(StatusCode.INTERNALERROR, null);
        }
    }

    private int readFwdTableEntry(SNMPv1CommunicationInterface comInterface, short vlan, String destMac){
        logger.debug("enter readFwdTableEntry()...");
        try{
            String macOid = macAddrToOID(destMac);
            String portOid = portGetOID + "." + vlan + "." + macOid;
            logger.debug("to retieve mac ({})'s port, the OID: {}", destMac, portOid);

            SNMPVarBindList newVars = comInterface.getMIBEntry(portOid);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
            int valueInt = ((BigInteger)value.getValue()).intValue();

            logger.debug("get value of {} = {}" + value.getClass().getName(), valueInt);

            return valueInt;

        }
        catch(Exception e)
        {
            logger.error("In readFwdTableEntry(), Exception during SNMP getMIBEntry: {}", e);
            return -1;//meaning fail
        }
    }

    private Map<String, Integer> readAllFwdTableEntry(SNMPv1CommunicationInterface comInterface){
        logger.debug("enter readAllFwdTableEntry()...");
        Map<String, Integer> table =  new HashMap<String, Integer>();

        try{
            logger.debug("to retieve oid {}'s value...", portGetOID);

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(portGetOID);
            logger.debug("Number of table entries: {}", tableVars.size());
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
                logger.debug("Retrieved OID {} (vlan:{}, mac:{}): value of {} = {}", snmpOID, vlanOID, macOID, value.getClass().getName(), valueInt);
            }
            return table;

           }
           catch(Exception e)
           {
               logger.error("In readAllFwdTableEntry(), Exception during SNMP getMIBEntry: {}", e);
               return null;
           }
      }

    public FlowOnNode readFlowRequest(Flow flow, Node node){
        logger.debug("enter SNMPHandler.readFlowRequest()");

        logger.debug("retrieving the metrics in the Flow...");
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
            logger.error("sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. read fwd table entry
        //logger.debug("going to read fwd table entry...");
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
        logger.debug("enter SNMPHandler.readAllFlowRequest()");

        //Use snmp to read switch fwd table...

        Long sw_macAddr = (Long) node.getID();
        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //logger.debug("going to read fwd table entry...");
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
            byte[] macAddrBytes = OIDToMacAddrBytes(str.substring(str.indexOf(".") + 1));
            if(macAddrBytes == null)
                return null;

            match.setField(MatchType.DL_VLAN, vlan);
            match.setField(MatchType.DL_DST, macAddrBytes);
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
        //logger.debug(oidstr + " ==> head=" + head + ",tail=" + tail);
        String ansStr= oidstr.substring(head, tail);
        Short ans = Short.parseShort(ansStr);
        //logger.debug("oidstr (" + oidstr +") to retrieve port number:" + ans);
        return ans;
    }

    private Map<Short, String> readLLDPRemoteChassisIDEntries(SNMPv1CommunicationInterface comInterface){
        logger.debug("enter readLLDPRemoteChassisIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //logger.debug("to retieve oid " + lldpRemoteChassisIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpRemoteChassisIdOID);
            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;

           }
           catch(Exception e)
           {
               logger.error("In readLLDPRemoteChassisIDEntries(), Exception during SNMP getMIBEntry: {}", e);
               return null;
           }
      }

    public Map<Short, String>  readLLDPAllRemoteChassisID(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.debug("enter SNMPHandler.readLLDPAllRemoteChassisID()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP remote chassis IDs...");
        return readLLDPRemoteChassisIDEntries(comInterface);
    }

    public String getLLDPChassis(Long sw_macAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        logger.debug("enter SNMPHandler.getLLDPChassis({})...", HexString.toHexString(sw_macAddr));
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);

            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            SNMPVarBindList newVars = comInterface.getMIBEntry(lldpLocalChassisIdOID);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            String valueStr = HexString.toHexString(valueBytes);

            logger.debug("to retieve switch ({})'s local chassis (OID: {}), get value: {}", sw_macAddr, lldpLocalChassisIdOID, valueStr);
            return valueStr;

        }
        catch(Exception e)
        {
            logger.error("Exception during SNMP getLLDPChassis: {}", e);
            return null;//meaning fail
        }
    }

    public String getLLDPChassis(String sw_ipAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        logger.debug("enter SNMPHandler.getLLDPChassis()...");
        try{
            InetAddress swIpAddr = InetAddress.getByName(sw_ipAddr);
            if(swIpAddr == null) return null;

            String community = cmethUtil.getSnmpCommunity(cmethUtil.getSID(sw_ipAddr));
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, swIpAddr, community);
            logger.debug("snmp connection created...swtich IP addr={}, community={}" + sw_ipAddr, community);

            SNMPVarBindList newVars = comInterface.getMIBEntry(lldpLocalChassisIdOID);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            String valueStr = HexString.toHexString(valueBytes);

            logger.debug("to retieve switch ({})'s local chassis (OID: {}), get value: {}", sw_ipAddr, lldpLocalChassisIdOID, valueStr);
            return valueStr;

        }
        catch(Exception e)
        {
            logger.error("Exception during SNMP getLLDPChassis:  " + e + "\n");
            return null;//meaning fail
        }
    }

    public Map<Short, String>  readLLDPLocalPortIDs(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.debug("enter SNMPHandler.readLLDPLocalPortIDs()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr " + sw_macAddr + "into InetAddress.getByName() error!\n" + e);
            return null;
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP local port IDs...");
        return readLLDPLocalPortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPLocalPortIDEntries(SNMPv1CommunicationInterface comInterface){
        logger.debug("enter SNMPHandler.readLLDPLocalPortIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpLocalPortIdOID);
            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromPortOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //logger.debug("Retrieved OID: " + snmpOID + " (so port num=" + portNum + "), value: " + valueStr);
            }
            return table;

           }
           catch(Exception e)
           {
               logger.error("In readLLDPLocalPortIDEntries(), Exception during SNMP getMIBEntry:  " + e + "\n");
               return null;
           }
      }

    private Short retrievePortNumFromPortOID(String oidstr){//input is eg. :iso.0.8802.1.1.2.1.3.7.1.3.52, return is eg. 52
        //e.g. oidstr as "iso.0.8802.1.1.2.1.4.1.1.5.0.49.1", then return "49"
        int index = oidstr.lastIndexOf(".") + 1;
        String ansStr= oidstr.substring(index);
        Short ans = Short.parseShort(ansStr);
        //logger.debug("oidstr (" + oidstr +") to retrieve port number:" + ans);
        return ans;
    }

    public Map<Short, String>  readLLDPRemotePortIDs(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.debug("enter SNMPHandler.readLLDPRemotePortIDs()");

        //Use snmp to read switch fwd table...

        InetAddress sw_ipAddr = null;

        //1. open snmp communication interface
        try{
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr " + sw_macAddr + "into InetAddress.getByName() error!\n" + e);
            return null;
        }
        if(sw_ipAddr == null) return null;

        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
        //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP remote chassis IDs...");
        return readLLDPRemotePortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPRemotePortIDEntries(SNMPv1CommunicationInterface comInterface){
        logger.debug("enter readLLDPRemotePortIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();

        try{
            //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(lldpRemotePortIdOID);
            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr = HexString.toHexString(valueBytes);

                table.put(portNum, valueStr);
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;

        }
        catch(Exception e)
        {
            logger.error("In readLLDPRemotePortIDEntries(), Exception during SNMP getMIBEntry:  " + e + "\n");
            return null;
        }
    }

    public Status addVLAN(Node node, Long vlanID){
        logger.debug("enter SNMPHandler.addVLAN()...");
        return addVLAN(node, vlanID, "v" + vlanID);
    }
    public Status addVLAN(Node node, Long vlanID, String vlanName){
        Long sw_macAddr = (Long)(node.getID());
        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can add vlan
           // logger.debug("going to add vlan...");
            boolean result = addVLANtoSwitch(comInterface, vlanID, vlanName);
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", "snmp to add vlan", "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr {} into InetAddress.getByName() error: {}", sw_macAddr, e);
            return new Status(StatusCode.INTERNALERROR, null);
        }
    }

    private boolean addVLANtoSwitch(SNMPv1CommunicationInterface comInterface, Long vlanID, String vlanName){
        logger.debug("enter SNMPHandler.addVLANtoSwitch()...");
        String vlanIDStr = "." + vlanID;
        /*String zeros = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        String request = vlanNameOID + vlanIDStr + "s " + vlanName
                              + vlanEgressPortsOID + vlanIDStr + "x " + zeros
                              + vlanForbiddenEgressPortsOID + vlanIDStr + "x " + zeros
                              + vlanUntaggedPortsOID + vlanIDStr + "x " + zeros
                              + vlanRowStatusOID + vlanIDStr + "i 4";*/
        byte vlanNameBytes[] = new byte[vlanName.length()];
        try{
            vlanNameBytes = vlanName.getBytes("US-ASCII");
        }catch(Exception e){
            logger.error("addVLANtoSwitch(): convert String vlanName(" + vlanName + ") to byte[] fail");
        }
        try{
            String[] oids = {vlanNameOID + vlanIDStr,
                                    vlanEgressPortsOID + vlanIDStr,
                                    vlanForbiddenEgressPortsOID + vlanIDStr,
                                    vlanUntaggedPortsOID + vlanIDStr,
                                    vlanRowStatusOID + vlanIDStr
                                    };
            SNMPObject [] values = {
                                    new SNMPOctetString(vlanNameBytes), 
                                    new SNMPOctetString(new byte[42]), 
                                    new SNMPOctetString(new byte[42]), 
                                    new SNMPOctetString(new byte[42]), 
                                    new SNMPInteger(4)
                                    };
            SNMPVarBindList newVars = comInterface.setMIBEntry(oids, values);
            for(int i = 0; i < oids.length; i++){
                    logger.warn("set OID {}: new value of {} = {}", oids[i], values[i].getClass().getName(), values[i]);
            }
        }catch(Exception e){
            logger.error("addVLANtoSwitch, error: " + e);
            return false;
        }
        return true;
    }

    public Status setVLANPorts (Node node, Long vlanID, List<NodeConnector> nodeConns){
        logger.debug("enter SNMPHandler.setVLANPorts()...");
        Long sw_macAddr = (Long)(node.getID());
        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can add vlan
            // logger.debug("going to set vlan ports...");
            byte[] nodeConnsBytes = convertPortListToBytes(nodeConns);
            boolean result = setVLANPortstoSwitch(comInterface, vlanID, nodeConnsBytes);
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", "snmp to set vlan ports", "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            logger.error("sw_macAddr {} into InetAddress.getByName() error: {}", sw_macAddr, e);
            return new Status(StatusCode.INTERNALERROR, null);
        }
    }

    private byte[] convertPortListToBytes(List<NodeConnector> nodeConns){
        NodeConnector nc = null;
        int portNum = -1;
        int portList[] = new int[48];
        byte[] answer = new byte[6];
        int index = 0;
        for(int i = 0; i < nodeConns.size(); i++){
            nc = (NodeConnector)(nodeConns.get(i));
            portNum = ((Short)(nc.getID())).intValue() - 1;
            portList[portNum] = 1;
        }
        String listStr = "port list: ";
        for(int k = 0; k < 48; k++)
            listStr = listStr + portList[k];
        logger.info(listStr);
        for(int j = 0; j < 48 - 7; j += 8){
            int seg = portList[j] * 128 + portList[j + 1] * 64 + portList[j + 2] * 32 + portList[j + 3] * 16
                          + portList[j + 4] * 8 + portList[j + 5] * 4 + portList[j + 6] * 2 + portList[j + 7];
            /*int seg = portList[j] << 7 + portList[j + 1] << 6 + portList[j + 2] << 5 + portList[j + 3] << 4
                          + portList[j + 4] << 3 + portList[j + 5] << 2 + portList[j + 6] << 1 + portList[j + 7];*/
            logger.info("port list [" + j + "~" + (j+7) + "] = " + seg);    
            answer[index++] = (new Integer(seg)).byteValue();
        }
        logger.info("port list as hex string=" + HexString.toHexString(answer));
        return answer;
    }

    private boolean setVLANPortstoSwitch(SNMPv1CommunicationInterface comInterface, Long vlanID, byte[] nodeConnsBytes){
        logger.debug("enter SNMPHandler.setVLANPortstoSwitch()...");
        try{
            String oid = vlanEgressPortsOID + "." + vlanID;
            byte[] value = new byte[42];
            System.arraycopy(nodeConnsBytes, 0, value, 0, 6);
            SNMPOctetString octetValue =  new SNMPOctetString(value);
            comInterface.setMIBEntry(oid, octetValue);
        }
        catch(Exception e){
            logger.error("In setVLANPortstoSwitch(), Exception during SNMP setMIBEntry: {}", e);
            return false;
        }
        return true;
    }

    public Status deleteVLAN(Node node, Long vlanID){//return-- true:success, false:fail
        Long sw_macAddr = (Long)(node.getID());
        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can add vlan
           // System.out.println("going to add vlan...");
            boolean result = deleteVLANFromSwitch(comInterface, vlanID);
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", "snmp to delete vlan", "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            logger.error("switchIP {} into InetAddress.getByName() error: {}", cmethUtil.getIpAddr(sw_macAddr), e);
            return new Status(StatusCode.INTERNALERROR, null);
        }
    }

    private boolean deleteVLANFromSwitch(SNMPv1CommunicationInterface comInterface, Long vlanID){
        ///System.out.println("enter SNMPHandler.addVLANtoSwitch()...");
        String vlanIDStr = "." + vlanID.toString();
        try{
            String oid = vlanRowStatusOID + vlanIDStr;
            SNMPObject value = new SNMPInteger(6);
            SNMPVarBindList newVar = comInterface.setMIBEntry(oid, value);
            ///System.out.println("set OID " + oid + ": new value of " + value.getClass().getName() + " = " + value);
        }catch(Exception e){
            logger.info("deleteVLANFromSwitch, error: " + e);
            logger.info("(maybe because this vlan already exists)");
            return false;
        }
        return true;
    }

    public List<NodeConnector> getVLANPorts(Node node, Long vlanID){//return: the ports of the vlan on the switch
       Long sw_macAddr = (Long)(node.getID());
        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);
            //System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            String requestOID = vlanEgressPortsOID + "." + vlanID.toString();
            SNMPVarBindList newVars = comInterface.getMIBEntry(requestOID);
            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            byte[] portBytes = new byte[6];
            System.arraycopy(valueBytes, 0, portBytes, 0, 6);

            int ports[] = convertPortBytesToList(portBytes);

            String portsStr = HexString.toHexString(portBytes);
            logger.info("to retieve switch (" + switchIP +"'s local chassis (OID: " + requestOID + "), get value: " + portsStr);

            //convert ports (ex. 1010011001000... to {1,3,6,7,10})
            int ansTmp[] = new int[48];
            int index = 0;
            for(int i = 0; i < 48; i++){
                if(ports[i] !=0){
                    ansTmp[index] = i + 1;
                    index += 1;
                }
            }
            int answer[] = new int[index];
            System.arraycopy(ansTmp, 0, answer, 0, index);
            return portsToNcList(answer, node);
        }
        catch(Exception e)
        {
            logger.error("Exception during SNMP getVLANPorts: " + e);
            return null;//meaning fail
        }
    }

    private int[] convertPortBytesToList(byte portBytes[]){
        if(portBytes.length != 6){
            logger.error("convertPortBytesToList(), input portBytes's length != 6!");
            System.exit(0);
        }
        
        int ports[] = new int[48];
        int index = 0;
        for(int i = 0; i < portBytes.length; i++){
            int seg = portBytes[i] & 0xff;
            for(int j = 8; j > 0; j--){
                ports[index + j - 1] = seg % 2;
                seg = seg /2;
            }
            index = index + 8;
        }

        return ports;
    }

    private List<NodeConnector> portsToNcList(int ports[], Node node){
        List<NodeConnector> nodeConns = new ArrayList<NodeConnector>();
        for(int i = 0; i < ports.length; i++){
            nodeConns.add(createNodeConnector(new Short((short)ports[i]), node));
        }
        return nodeConns;
    }

    private static Node createSNMPNode(Long switchId) {
        try {
            return new Node("SNMP", switchId);
        } catch (ConstructionException e1) {
            logger.error("", e1);
            return null;
        }
    }

    private static NodeConnector createNodeConnector(Short portId, Node node) {
        if (node.getType().equals("SNMP")) {
            try {
                return new NodeConnector("SNMP", portId, node);
            } catch (ConstructionException e1) {
                logger.error("",e1);
                return null;
            }
        }
        return null;
    }

    public VLANTable getVLANTable(Node node){//return: all VLANs on the switch, and also the ports of each VLAN
        VLANTable table = new VLANTable();
        List<NodeConnector> ports;

        Long sw_macAddr = (Long)(node.getID());
        try{
            //1. open snmp communication interface
            String switchIP = cmethUtil.getIpAddr(sw_macAddr);
            InetAddress sw_ipAddr = InetAddress.getByName(switchIP);
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(vlanEgressPortsOID);
            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                String vlanIDstr = snmpOIDstr.substring(snmpOIDstr.lastIndexOf(".") + 1);
                Long vlanID = Long.valueOf(vlanIDstr);
                ports = getVLANPorts(createSNMPNode(sw_macAddr), vlanID);

                table.addEntry(vlanID, ports);
                //logger.debug("Retrieved OID: " + snmpOID + " (so port num=" + portNum + "), value: " + valueStr);
            }
            return table;

        }
        catch(Exception e)
        {
            logger.error("In readLLDPLocalPortIDEntries(), Exception during SNMP getMIBEntry:  " + e + "\n");
            return null;
        }
    }
    
    public Status/*SNMP4SDNErrorCode*/ setSTPPortState (long nodeID, short portNum, boolean isEnable){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: setSTPPortState(): invalid nodeID {}", nodeID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): invalid nodeID " + nodeID);
        }

        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: setSTPPortState(): invalid port {}", portNum);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): invalid portNum " + portNum);
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: setSTPPortState(): IP address of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): IP address of node " + nodeID + " is not in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: setSTPPortState(): SNMP community of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): SNMP community of node " + nodeID + " is not in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setSTPPortState(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): InetAddress.getByName() for node " + nodeID + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setSTPPortState(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): InetAddress.getByName() for node " + nodeID + " error: " + e2);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setSTPPortState(): InetAddress.getByName() for node {} fails", switchIP);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortState(): InetAddress.getByName() for node " + nodeID + " fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setSTPPortState(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortState(): create SNMP communication interface for node " + nodeID + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setSTPPortState(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortState(): create SNMP communication interface for node " + nodeID + " error: " + e2);
        }
        
        //2. now can add vlan
        //SNMP4SDNErrorCode ret = setSTPPortStateToSwitch(comInterface, portNum, isEnable);
        Status ret = setSTPPortStateToSwitch(comInterface, portNum, isEnable);
        if(ret.isSuccess()){
        //if(ret == SNMP4SDNErrorCode.SUCCESS){
            //return SNMP4SDNErrorCode.SUCCESS;
            return new Status(StatusCode.SUCCESS);
        }
        else{
            logger.debug("ERROR: setSTPPortState(): call setSTPPortState() for node {} port {} isEnable {} fails", nodeID, portNum, isEnable);
            return new Status(ret.getCode(), "SNMPHandler: setSTPPortState(): call setSTPPortState() for node " + nodeID + " port " + portNum + " isEnable " + isEnable + " fails: " + ret.getDescription());
            //return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortState(): call setSTPPortStateToSwitch() fail, with node " + nodeID + " port " + portNum + " isEnable " + isEnable + ": ErrorCode = " + ret);
        }
    }

    private Status/*SNMP4SDNErrorCode*/setSTPPortStateToSwitch(SNMPv1CommunicationInterface comInterface, short portNum, boolean isEnable){
        String portNumStr = "." + portNum;
        String oid = stpPortEnableOID + portNumStr;
        SNMPInteger value;
        SNMPVarBindList newVar;

        if(isEnable)
            value = new SNMPInteger(1);//enable
        else
            value = new SNMPInteger(2);//disable

        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: setSTPPortStateToSwitch(): invalid port {}", portNum);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setSTPPortStateToSwitch(): invalid port " + portNum);
        }
        
        try{
            newVar = comInterface.setMIBEntry(oid, value);
        }catch(Exception e1){
            logger.debug("ERROR: setSTPPortStateToSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, port: {}, error: {}", 
                                        comInterface.getHostAddress(), portNum, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortStateToSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", port: " + portNum + ", error: " + e1 + ")");
        }
        if(newVar == null){
            logger.debug("ERROR: setSTPPortStateToSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, port: {}", 
                                        comInterface.getHostAddress(), portNum);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortStateToSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", port: " + portNum);
        }

        logger.trace("SNMPHandler.setSTPPortStateToSwitch(node: {}, port: {})", comInterface.getHostAddress(), portNum);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVar.toString());
        
        //return SNMP4SDNErrorCode.SUCCESS;
        return new Status(StatusCode.SUCCESS);
    }

    public STPPortState getSTPPortState (long nodeID, short portNum){//return null means fail (TODO: enum object could be null or -1?)
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: getSTPPortState(): invalid nodeID {}", nodeID);
            return null;
        }

        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: getSTPPortState(): invalid port {}", portNum);
            return null;
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getSTPPortState(): IP address of node {} is not in DB", nodeID);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getSTPPortState(): SNMP community of node {} is not in DB", nodeID);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: getSTPPortState(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getSTPPortState(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getSTPPortState(): InetAddress.getByName() for node {} fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getSTPPortState(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getSTPPortState(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            return null;
        }

        //2. now can get stp port state from switch
        STPPortState portState = getSTPPortStateFromSwitch(comInterface, portNum);
        if(portState != null){
            return portState;
        }
        else{
            logger.debug("ERROR: getSTPPortState(): call getSTPPortState() for node {} port {} fails", nodeID, portNum);
            return null;
        }
    }

    private STPPortState getSTPPortStateFromSwitch(SNMPv1CommunicationInterface comInterface, short portNum ){
        String portNumStr = "." + portNum;
        String oid = stpPortStateOID + portNumStr;
        SNMPVarBindList newVars;

        if(portNum < 1){//TODO: valid port range
            logger.debug("ERROR: getSTPPortStateFromSwitch(): invalid port {}", portNum);
            return null;
        }

        try{
            newVars = comInterface.getMIBEntry(oid);
        }catch(Exception e1){
            logger.debug("ERROR: getSTPPortStateFromSwitch(): call SNMP getMIBEntry() fails, given node {} port {}: {}",
                                comInterface.getHostAddress(), portNum, e1);
            return null;
        }

        if(newVars == null){
            logger.debug("ERROR: getSTPPortStateFromSwitch(): null in switch response content, given node {} port {}",
                                comInterface.getHostAddress(), portNum);
            return null;
        }
        if(newVars.getSNMPObjectAt(0) == null){
            logger.debug("ERROR: getSTPPortStateFromSwitch(): null in switch response content, given node {} port {}",
                                comInterface.getHostAddress(), portNum);
            return null;
        }
        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        if(pair.getSNMPObjectAt(1) == null){
            logger.debug("ERROR: getSTPPortStateFromSwitch(): null in switch response content, given node {} port {}",
                                comInterface.getHostAddress(), portNum);
            return null;
        }
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: getSTPPortStateFromSwitch(): switch (node {}) requests STPPortState for port {} fails", comInterface.getHostAddress(), portNum);
            return null;
        }
        SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
        int valueInt = ((BigInteger)value.getValue()).intValue();

        logger.trace("SNMPHandler.getSTPPortStateFromSwitch(node: {}, port: {})", comInterface.getHostAddress(), portNum);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVars.toString());

        switch(valueInt){
            case 1:
                return STPPortState.DISABLED;
            case 2:
                return STPPortState.BLOCKING;
            case 3:
                return STPPortState.LISTENING;
            case 4:
                return STPPortState.LEARNING;
            case 5:
                return STPPortState.FORWARDING;
            case 6:
                return STPPortState.BROKEN;
            default:
                return null;
        }
    }

    public ARPTableEntry getARPEntry(long nodeID, String ipAddress){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: getARPEntry(): invalid nodeID {}", nodeID);
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

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getARPEntry(): IP address of node {} is not in DB", nodeID);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getARPEntry(): SNMP community of node {} is not in DB", nodeID);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: getARPEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getARPEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getARPEntry(): InetAddress.getByName() for node {} fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getARPEntry(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getARPEntry(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            return null;
        }

        //2. now can get arp entry from switch
        ARPTableEntry arpEntry = getARPEntryFromSwitch(comInterface, ipAddress);
        if(arpEntry != null){
            return arpEntry;
        }
        else{
            logger.debug("ERROR: getARPEntry(): call getARPEntryFromSwitch() for node {} arp_entry_ip_addr {} fails", nodeID, ipAddress);
            return null;
        }
    }

    private ARPTableEntry getARPEntryFromSwitch(SNMPv1CommunicationInterface comInterface, String ipAddress ){
        String oid = arpTableEntryPhyAddrOID + "."
                                + midStuffForArpTableEntryOID+ "." + ipAddress;
        SNMPVarBindList newVars;

        try{
            newVars = comInterface.getMIBEntry(oid);
        }catch(Exception e1){
            logger.debug("ERROR: getARPEntryFromSwitch(): call SNMP getMIBEntry() fails, given node {} arp_entry_ip_addr {}: {}",
                                comInterface.getHostAddress(), ipAddress, e1);
            return null;
        }

        if(newVars == null){
            logger.debug("ERROR: getARPEntryFromSwitch(): null in switch response content, given node {} arp_entry_ip_addr {}: {}",
                                comInterface.getHostAddress(), ipAddress);
            return null;
        }
        if(newVars.getSNMPObjectAt(0) == null){
            logger.debug("ERROR: getARPEntryFromSwitch(): null in switch response content, given node {} arp_entry_ip_addr {}: {}",
                                comInterface.getHostAddress(), ipAddress);
            return null;
        }
        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        if(pair.getSNMPObjectAt(1) == null){
            logger.debug("ERROR: getARPEntryFromSwitch(): null in switch response content, given node {} arp_entry_ip_addr {}: {}",
                                comInterface.getHostAddress(), ipAddress);
            return null;
        }
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: getARPEntryFromSwitch(): requests arp entry for node {} arp_entry_ip_addr {} fails",
                                comInterface.getHostAddress(), ipAddress);
            return null;
        }
        SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
        byte[] valueBytes = (byte[])value.getValue();

        logger.trace("SNMPHandler.getARPEntryFromSwitch(node: {}, port: {})", comInterface.getHostAddress(), ipAddress);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVars.toString());

        ARPTableEntry ret = new ARPTableEntry();
        ret.ipAddress = new String(ipAddress);
        String macAddrStr = HexString.toHexString(valueBytes);
        ret.macAddress = HexString.toLong(macAddrStr);

        return ret;
    }

    public Status/*SNMP4SDNErrorCode*/ deleteARPTableEntry (long nodeID, String ipAddress){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: deleteARPTableEntry(): invalid nodeID {}", nodeID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): invalid nodeID " + nodeID);
        }
        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: deleteARPTableEntry(): address translation for node {} for arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: deleteARPTableEntry(): address translation for node {} for arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): address translation for node " + nodeID + " for arp_entry_ip " + ipAddress + " error: " + e2);
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: deleteARPTableEntry(): IP address of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): IP address of node " + nodeID + " is not in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: deleteARPTableEntry(): SNMP community of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_READY;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): SNMP community of node " + nodeID + " is not in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: deleteARPTableEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): InetAddress.getByName() for node " + nodeID + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: deleteARPTableEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): InetAddress.getByName() for node " + nodeID + " error: " + e2);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: deleteARPTableEntry(): InetAddress.getByName() for node {} fails", switchIP);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: deleteARPTableEntry(): InetAddress.getByName() for node " + nodeID + " fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: deleteARPTableEntry(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntry(): create SNMP communication interface for node " + switchIP + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: deleteARPTableEntry(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntry(): create SNMP communication interface for node " + switchIP + " error: " + e2);
        }

        //2. now can get arp entry from switch
        //SNMP4SDNErrorCode ret = deleteARPTableEntryFromSwitch(comInterface, ipAddress);
        Status ret = deleteARPTableEntryFromSwitch(comInterface, ipAddress);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            logger.debug("ERROR: deleteARPTableEntry(): call getARPEntryFromSwitch() for node {} arp_entry_ip_addr {} fails", nodeID, ipAddress);
            return new Status(ret.getCode(), "SNMPHandler: deleteARPTableEntry(): call getARPEntryFromSwitch() for node " + nodeID + " arp_entry_ip_addr " + ipAddress + " fails: " + ret.getDescription());
        }

        return ret;
        //return new Status(StatusCode.SUCCESS);
    }

    private Status/*SNMP4SDNErrorCode*/ deleteARPTableEntryFromSwitch(SNMPv1CommunicationInterface comInterface, String ipAddress){
        //TODO: current behavior of deleting a non-existing entry would return success, because using linux tool 'snmpset' to do so is also such behavior.
        String oid = arpTableEntryTypeOID + "." + midStuffForArpTableEntryOID + "." + ipAddress;
        SNMPInteger value =new SNMPInteger(arpTableEntryType_invalid);
        SNMPVarBindList newVar;

        try{
            newVar = comInterface.setMIBEntry(oid, value);
        }catch(Exception e1){
            logger.debug("ERROR: deleteARPTableEntryFromSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}, error: {}", 
                                        comInterface.getHostAddress(), ipAddress, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntryFromSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", arp_ip_address: " + ipAddress + ", error: " + e1 + ")");
        }
        if(newVar == null){
            logger.debug("ERROR: deleteARPTableEntryFromSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}", 
                                        comInterface.getHostAddress(), ipAddress);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntryFromSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", arp_ip_address: " + ipAddress + ")");
        }

        logger.trace("SNMPHandler.deleteARPTableEntryFromSwitch(node: {}, arp_ip_address: {})", comInterface.getHostAddress(), ipAddress);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVar.toString());
        
        //return SNMP4SDNErrorCode.SUCCESS;
        return new Status(StatusCode.SUCCESS);
    }

    public Status/*SNMP4SDNErrorCode*/ setARPTableEntry(long nodeID, String ipAddress, long macID){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: setARPTableEntry(): invalid nodeID {}", nodeID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): invalid nodeID " + nodeID);
        }
        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setARPTableEntry(): address translation for node {} to arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): address translation for node " + nodeID + " to arp_entry_ip " + ipAddress + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPTableEntry(): address translation for node {} to arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): address translation for node " + nodeID + " to arp_entry_ip " + ipAddress + " error: " + e2);
        }
        if(macID < 0){//check macId valid?
            logger.debug("ERROR: setARPTableEntry(): invalid macID {}", macID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): invalid macID " + macID);
        }

        //Prepare required parameters for establishing SNMP communication, as follows

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: setARPTableEntry(): IP address of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): IP address of node " + nodeID + " is not in DB");
        }
        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: setARPTableEntry(): SNMP community of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_READY;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): SNMP community of node " + nodeID + " is not in DB");
        }
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setARPTableEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): InetAddress.getByName() for node " + switchIP + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPTableEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): InetAddress.getByName() for node " + switchIP + " error: " + e2);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setARPTableEntry(): InetAddress.getByName() for node {} fails", switchIP);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPTableEntry(): InetAddress.getByName() for node " + switchIP + " fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setARPTableEntry(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPTableEntry(): create SNMP communication interface for node " + switchIP + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPTableEntry(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPTableEntry(): create SNMP communication interface for node " + switchIP + " error: " + e2);
        }

        //2. now can get arp entry from switch
        //SNMP4SDNErrorCode ret = setARPTableEntryOnSwitch(comInterface, ipAddress, macID);
        Status ret = setARPTableEntryOnSwitch(comInterface, ipAddress, macID);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            logger.debug("ERROR: setARPTableEntry(): call setARPEntryFromSwitch() for node {} fails", nodeID, ipAddress);
            return new Status(ret.getCode(), "SNMPHandler: setARPTableEntry(): call setARPEntryFromSwitch() for node " + nodeID + " arp entry ip " + ipAddress + " fails: " + ret.getDescription());
        }

        //return ret;
        return new Status(StatusCode.SUCCESS);
    }

    private Status/*SNMP4SDNErrorCode*/ setARPTableEntryOnSwitch(SNMPv1CommunicationInterface comInterface, String ipAddress, long macID){
        String phyAddrOid = arpTableEntryPhyAddrOID + "." + midStuffForArpTableEntryOID + "." + ipAddress;
        String typeOid = arpTableEntryTypeOID + "." + midStuffForArpTableEntryOID + "." + ipAddress;
        String[] oids = {phyAddrOid, typeOid};

        String macAddrStr = new HexString().toHexString(macID);//convert long to hex-string
        byte[] macAddrBytes = new HexString().fromHexString(macAddrStr);//conver hex-string to bytes()
        SNMPOctetString macOct =  new SNMPOctetString(macAddrBytes);//TODO: debug: macAddrBytes should be full length (xx:xx:xx:xx:xx:xx)
        SNMPInteger typeInt = new SNMPInteger(arpTableEntryType_static);
        SNMPObject [] values = {macOct, typeInt};
System.out.println("ipAddress: " + ipAddress);
System.out.println("macID: " + macID);
System.out.println("phyAddrOid: " + phyAddrOid);
System.out.println("typeOid: " + typeOid);
System.out.println("macAddrStr: " + macAddrStr);
System.out.println("SNMPOctetString macOct: " + macOct.toHexString());
System.out.println("typeInt: " + typeInt);
        SNMPVarBindList newVars;

        try{//note: here not use rain's suggest "copy the variables above and paste", becase they will be used after the 'try..catch'
            newVars = comInterface.setMIBEntry(oids, values);
        }catch(Exception e1){
            logger.debug("ERROR: setARPTableEntryOnSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}, arp_mac_address: {}, error: {}", 
                                        comInterface.getHostAddress(), ipAddress, macAddrStr, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPTableEntryOnSwitch(): call SNMP setMIBEntry() fails, with node " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr + ", error: " + e1);
        }
        if(newVars == null){
            logger.debug("ERROR: setARPTableEntryOnSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}, arp_mac_address: {}", 
                                        comInterface.getHostAddress(), ipAddress, macAddrStr);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPTableEntryOnSwitch(): call SNMP setMIBEntry() fails, with node " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr);
        }

        logger.trace("SNMPHandler: setARPTableEntryOnSwitch(): parameters -- node: {}, arp_ip_address: {}, arp_mac_address: {}", comInterface.getHostAddress(), ipAddress, macAddrStr);
        logger.debug("\n[Send to switch]:");
        for(int i = 0; i < oids.length; i++){
            logger.debug("\n  OID: {}\n    value = {}", oids[i], values[i]);
        }
        logger.debug("\n[Switch response]:\n  " + newVars.toString());
        
        //return SNMP4SDNErrorCode.SUCCESS;
        return new Status(StatusCode.SUCCESS);
    }

    public List<ARPTableEntry> getARPTable(long nodeID){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: getARPTable(): invalid nodeID {}", nodeID);
            return null;
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getARPTable(): IP address of node {} is not in DB", nodeID);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getARPTable(): SNMP community of node {} is not in DB", nodeID);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: getARPTable(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getARPTable(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getARPTable(): InetAddress.getByName() for node {} fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getARPTable(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            return null;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getARPTable(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            return null;
        }

        //2. now can get arp entry from switch
        List<ARPTableEntry> arpTable = getARPTableFromSwitch(comInterface);
        if(arpTable != null){
            return arpTable;
        }
        else{
            logger.debug("ERROR: getARPTable(): call getARPTableFromSwitch() for node {} fails", nodeID);
            return null;
        }
    }

    private List<ARPTableEntry> getARPTableFromSwitch(SNMPv1CommunicationInterface comInterface){
        String oid = arpTableEntryPhyAddrOID + "." + midStuffForArpTableEntryOID;
        SNMPVarBindList tableVars;

        try{
            tableVars = comInterface.retrieveMIBTable(oid);
        }catch(Exception e1){
            logger.debug("ERROR: getARPTableFromSwitch(): call SNMP retrieveMIBTable() fails, given node {}: {}",
                                comInterface.getHostAddress(), e1);
            return null;
        }

        if(tableVars == null){
            logger.debug("ERROR: getARPTableFromSwitch(): null in switch response content, given node {}: {}",
                                comInterface.getHostAddress());
            return null;
        }

        ArrayList retList = new ArrayList<ARPTableEntry>();

        for(int i = 0; i < tableVars.size(); i++){
            if(tableVars.getSNMPObjectAt(i) == null){
                logger.debug("ERROR: getARPTableFromSwitch(): null in switch response content, given node {}: {}",
                                    comInterface.getHostAddress());
                return null;
            }
            SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
            if(pair.getSNMPObjectAt(1) == null){
                logger.debug("ERROR: getARPTableFromSwitch(): null in switch response content, given node {}: {}",
                                    comInterface.getHostAddress());
                return null;
            }
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
                logger.debug("ERROR: getARPTableFromSwitch(): requests arp entry for node {} fails",
                                    comInterface.getHostAddress());
                return null;
            }
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();

            ARPTableEntry entry = new ARPTableEntry();
            String snmpOIDStr = snmpOID.toString();
            String ipAddress = snmpOIDStr.substring(oid.length() + 1);
            entry.ipAddress = new String(ipAddress);
            String macAddrStr = HexString.toHexString(valueBytes);
            entry.macAddress = HexString.toLong(macAddrStr);
            logger.trace("SNMPHandler: getARPTableFromSwitch(): add entry <{}, {}>", ipAddress, macAddrStr);
            retList.add(entry);
        }

        logger.trace("SNMPHandler.getARPTableFromSwitch(node: {}, port: {})", comInterface.getHostAddress());
        logger.trace("\n[Send to switch]:\n  OID: {}", oid);
        logger.trace("\n[Switch response]:\n  " + tableVars.toString());

        return retList;
    }

    private String errorString(String phase, String action, String cause) {
        return "Failed to "
                + ((phase != null) ? phase + " the " + action
                        + " flow message: " : action + " the flow: ") + cause;
    }

}
