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
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import org.opendaylight.snmp4sdn.ARPTableEntry;
//import org.opendaylight.snmp4sdn.SNMP4SDNErrorCode;
import org.opendaylight.snmp4sdn.STPPortState;
import org.opendaylight.snmp4sdn.FDBEntry;

import org.opendaylight.snmp4sdn.VLANTable;
//import org.opendaylight.controller.sal.vlan.VLANTable;//ad-sal

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.*;

//TODO: decouple the hardware-dependent parts as another module

public class SNMPHandler{
    private static final Logger logger = LoggerFactory
            .getLogger(SNMPHandler.class);

    //TODO: read these from DB or vendor-specific configuration file
    private int NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB = 6;

    
    private int NUMBER_OF_PORT = 64;
    private int NUMBER_OF_PORT_IN_SNMP_VLAN = 64;
    private int NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN = NUMBER_OF_PORT_IN_SNMP_VLAN/8;
    private int NUMBER_OF_PORT_IN_SNMP_FDB = 64;
    /*//TODO: make the following covered by vendor-specific solution
    private int NUMBER_OF_PORT_DLINK = 24;
    private int NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK = 48;
    private int NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN_DLINK = NUMBER_OF_PORT_IN_SNMP_VLAN_DLINK/8;
    private int NUMBER_OF_PORT_IN_SNMP_FDB_DLINK = 32;
    */
    private int NUMBER_OF_PORT_GROUP_IN_VALUE_ASSIGN_IN_SNMP_VLAN = 7;
    private int DELETE_VLAN_SNMP_VALUE = 6;
    private int NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP = 6;

    private int NUMBER_OF_BYTES_FOR_IPV4_MAC_ADDRESS = 6;

    /************** IMPORTANT NOTE ***************
    * Behavior of FDB entry setting with SNMP:
    *   Limitation: If there's already exist the FDB entry on switch, also it's type is "dynamic", then we can't "delete" nor "set port" on this entry, but we can "set type as static" on this entry
    *   (If there's no such FDB entry on switch, or there's already exist the FDB entry with type as "static", then we can configure the entry (i.e. all ok to "delete" or "set type" or "set port" or "set type and port together")
    *   Due to the limitation, when call setFwdTableEntryPort() and setFwdTableEntry_SetTypeAndPort(), need to first also call setFwdTableEntryType() with type of "static".
    */
    String portGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.2";//s4s: MAC_PORT_GET's OID
    String typeGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.3";//s4s: MAC_TYPE_GET's OID
    String portSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.3";//s4s: MAC_PORT_SET's OID
    String typeSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.4";//s4s: MAC_TYPE_SET's OID
    /*private enum FdbEntryGetType{//move this part to FDBEntry.java
        OTHER (1),
        INVALID (2),
        LEARNED (3),//dynamic
        SELF (4),//CPU
        MGMT (5);//static
        private int value;
        private FdbEntryGetType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
    }*/
    private enum FdbEntrySetType{//by snmpset, only 2 and 3 (i.e. INVALID and PERMANENT can be set)
        OTHER (1),
        INVALID (2),//delete
        PERMANENT (3),//static
        DELETEONRESET (4),
        DELETEONTIMEOUT (5);
        private int value;
        private FdbEntrySetType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
    };

    String lldpLocalChassisIdOID = "1.0.8802.1.1.2.1.3.2.0";//s4s
    String lldpLocalPortIdTypeOID = "1.0.8802.1.1.2.1.3.7.1.2";//s4s
    String lldpLocalPortIdOID = "1.0.8802.1.1.2.1.3.7.1.3";//s4s
    String lldpRemoteChassisIdOID = "1.0.8802.1.1.2.1.4.1.1.5";//s4s
    String lldpRemotePortIdTypeOID = "1.0.8802.1.1.2.1.4.1.1.6";//s4s
    String lldpRemotePortIdOID = "1.0.8802.1.1.2.1.4.1.1.7";//s4s
    int portIdType_MacAddr = 3;
    int portIdType_ifName = 5;
    int portIdType_LocallyAssigned = 7;

    String vlanNameOID = "1.3.6.1.2.1.17.7.1.4.3.1.1";
    String vlanEgressPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.2";
    String vlanForbiddenEgressPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.3";
    String vlanUntaggedPortsOID = "1.3.6.1.2.1.17.7.1.4.3.1.4";
    String vlanRowStatusOID = "1.3.6.1.2.1.17.7.1.4.3.1.5";

    String productName_dlink_DGS_3120_24TC_OID = "1.3.6.1.4.1.171.12.11.1.9.4.1.9.1";

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

    String stpDesignatedRootOID = "1.3.6.1.2.1.17.2.5";//dot1dStpDesignatedRoot: return root switch's mac address (on D-link, e.g. 10:00:xx:xx:xx:xx:xx:xx, TODO: now just ignore prefix "10:00", right?)
    String stpPortDesignatedRootOID = "1.3.6.1.2.1.17.2.15.1.6";//dot1dStpPortDesignatedRoot: return root switch's mac address (on D-link, e.g. 10:00:xx:xx:xx:xx:xx:xx, TODO: now just ignore prefix "10:00", right?)

    String portStateOID = "1.3.6.1.2.1.2.2.1.7";
    /*ifAdminStatus
    root@ubuntu:~# snmpwalk -v2c -c public 192.168.0.32 1.3.6.1.2.1.2.2.1.7
    iso.3.6.1.2.1.2.2.1.7.1 = INTEGER: 2
    iso.3.6.1.2.1.2.2.1.7.2 = INTEGER: 1
    ...
    iso.3.6.1.2.1.2.2.1.7.50 = INTEGER: 1 //all ports are listed

    1 : up
    2 : down
    3 : testing
    */

    //String requestTypeOIDs = {"1.1.1.1.1"};//(when all kinds of OID more and more, may consider to use requestTypeOIDs[typeID] instead of rasing one by one like above

    CmethUtil cmethUtil = null;

    boolean isDummy = false;

    public SNMPHandler(CmethUtil cmethUtil){
        this.cmethUtil = cmethUtil;
    }

    /*
        for example:
        0x00400000 ( port 10 )
        |00|40|00|00|
        | 00000000 | 01000000 | 00000000 | 00000000 |
    */
    private String convertToEthSwitchPortString(int port){
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        int pow;

        pow = NUMBER_OF_PORT_IN_SNMP_FDB - port;
        ans = HexString.toHexString((long)Math.pow(2, pow));

        //error checking
        if(ans.length() != 16 + 7)//TODO: 16+7 is the uniform length of the output string from HexString.toHexString(long). Notice HexString library change in the future.
            return null;
        int lenghOfNoneZero = (NUMBER_OF_PORT_IN_SNMP_FDB/8)*3 - 1;
        int lengthOfZeroInFront = ans.length() - lenghOfNoneZero;
        if(HexString.toLong(ans.substring(0, lengthOfZeroInFront)) != 0)//the substring here should be "00:00:00..." all zero. If not, abnormal.
            return null;

        ans = ans.substring(lengthOfZeroInFront, ans.length());
        return ans;
    }


    /*
        for example:
        0x00400000 ( port 10 )
        |00|40|00|00|
        | 00000000 | 01000000 | 00000000 | 00000000 |
    */
    //s4s
    private String convertToEthSwitchPortString_4bit_as_seg(int port){//TODO: now only support max port number = 32
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        String sep = ":";//see what seperate character is assigned, eg. ":" or " " or ""  (i.e.  colon or blank or connected)
        int pow;
        String str = "";

        if(port <= 4){
            pow = 4 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = str + "0" + sep + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 8){
            pow = 8 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "0" + str + sep + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 12){
            pow = 12 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00" + sep + str + "0" + sep + "00" + sep + "00";
        }
        else if(port <= 16){
            pow = 16 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00"  + sep + "0" + str + sep + "00" + sep + "00";
        }
        else if(port <= 20){
            pow = 20 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00" + sep + "00" + sep + str + "0" + sep + "00";
        }
        else if(port <= 24){
            pow = 24 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00" + sep + "00" + sep + "0" + str + sep + "00";
        }
        else if(port <= 28){
            pow = 28 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00" + sep + "00" + sep + "00" + sep + str + "0";
        }
        else if(port <= NUMBER_OF_PORT_IN_SNMP_FDB){
            pow = 32 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = "00" + sep + "00" + sep + "00" + sep + "0" + str;
        }
        else{
            logger.debug("ERROR: convertToEthSwitchPortString_4bit_as_seg() is given port > 32!");
            return null;
        }

        return ans;
    }

    //s4s
    private String macAddrToOID(String macAddr){//TODO: only available for ipv4's mac address format (6 segments)
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
        byte[] result = new byte[NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB];
        int loc1 = 0, loc2 = 0;
        int count = 0;
        while(loc1 < oid.length()){
            if(count > 5){
                logger.debug("ERROR: OIDToMacAddrBytes(): {} is longer than NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB (default = {}) bytes", oid, NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB);
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

    // e.g. common mac address "00:00:00:00:12:AB" has length of 17
    // So, if length is not 17, needs making up
    /// TODO: error checking, such as "1:00:12..."  "000:00$:AB..."
    /// TODO: error checking, the input long value should be in range of ipv4 value
    private String longTo6SegHexString(long destMac){
        String destMacStr = HexString.toHexString(destMac);
        destMacStr = removeZeroInBeginning(destMacStr);

        //17 is exactly 6 segs Mac Addr
        if(destMacStr.length() == 17)
            return destMacStr;
        if(destMacStr.length() > 17){
            int strlen = destMacStr.length();
            destMacStr.substring(strlen - 17, strlen);
            return destMacStr;
        }
        while(destMacStr.length() < 17){
            destMacStr = "00:" + destMacStr;
        }
        return destMacStr;
    }

    private boolean isValidVlan(int vlanId){
        if(vlanId < 1 || vlanId > 4095)//TODO: valid vlan range?
            return false;
        else
            return true;
    }

    //This method is for FDB Service (FdbServiceImpl.java) to call
    //TODO: currently the fdb entry 'type' field is directly assigned as 'static'. It is okay to directly assign or ask caller to assign?
    public Status setFdbEntry(long nodeId, int vlanId, long destMac, short port){
        if(nodeId < 0 || !isValidVlan(vlanId) || destMac < 0 || port < 0){
            logger.debug("ERROR: setFdbEntry(): there is/are invalid input parameter: node {} vlanId {} destMac {} port {}", nodeId, vlanId, destMac, port);
            return new Status(StatusCode.BADREQUEST, "In SNMPHandler.setFdbEntry(), there is/are invalid input parameter: node " + nodeId + " vlanId " + vlanId + " destMac " + destMac + " port " + port);
        }
        if(isDummy){//TODO: remain isDummy? (place Dummy in every function, or remove from every function)
            logger.info("setFdbEntry(): set node {} with vlan {} destMac {} port {}: dummy return SUCCESS", nodeId, vlanId, destMac, port);
            return new Status(StatusCode.SUCCESS);
        }

        //1. create items switchIP, community, for SNMP request
        String switchIP = cmethUtil.getIpAddr(nodeId);
        if(switchIP == null){
            logger.debug("ERROR: setFdbEntry(): node {} is not in DB", nodeId);
            return new Status(StatusCode.BADREQUEST, "In SNMPHandler.setFdbEntry(), node " + nodeId + " is not in DB (nodeId " + nodeId + " vlanId " + vlanId + " destMac " + destMac + " port " + port);
        }
        InetAddress sw_ipAddr;
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }catch(Exception e1) {
            logger.debug("ERROR: setFdbEntry(): switchIP {} into InetAddress.getByName() error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.setFdbEntry(), switchIP " + switchIP + " into InetAddress.getByName() error: " + e1);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setFdbEntry(): switchIP {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.setFdbEntry(), switchIP " + switchIP + " into InetAddress.getByName() fails");
        }
        String community = cmethUtil.getSnmpCommunity(nodeId);
        if(community == null){
            logger.debug("ERROR: setFdbEntry(): Can't find the SNMP community of the node {} in DB", nodeId);
            return new Status(StatusCode.NOTFOUND, "In SNMPHandler.setFdbEntry(), can't find the SNMP community of the node " + nodeId + "in DB");
        }

        //2. open snmp communication interface
        SNMPv1CommunicationInterface comInterface;
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setFdbEntry(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.setFdbEntry(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //No need to set entry type, so skip this
        //3. set fwd table entry type 
        //TODO: remove setFwdTableEntryType()? Remove setFwdTableEntryType() seems ok, because by default entry will be set as 'permanent(means static)' (only 'invalid' and 'permanent' can be set by snmpset)
        //TODO: related to the previous TODO, actually I think it should be successful with setting both type and port at one. we should try to make it.
        //TODO:checking!
        /*boolean setTypeRet = setFwdTableEntryType(comInterface, vlanId, destMac, FdbEntrySetType.PERMANENT);
        if(!setTypeRet)
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.setFdbEntry(), call setFwdTableEntryType() fail, with node " + nodeId + " vlanId " + vlanId + " destMac " + destMac + " port " + port);
        */

        //4. set fwd table entry port
        boolean setPortRet = setFwdTableEntryPort(comInterface, vlanId, destMac, port);
        if(!setPortRet)
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.setFdbEntry(), call setFwdTableEntryPort() fail, with node " + nodeId + " vlanId " + vlanId + " destMac " + destMac + " port " + port);

        return new Status(StatusCode.SUCCESS);
    }

    //This method is for FDB Service (FdbServiceImpl.java) to call
    public Status delFdbEntry(long nodeId, int vlanId, long destMac){
        if(nodeId < 0 || !isValidVlan(vlanId) || destMac < 0){
            logger.debug("ERROR: delFdbEntry(): there is/are invalid input parameter: node {} vlanId {} destMac {}", nodeId, vlanId, destMac);
            return new Status(StatusCode.BADREQUEST, "In SNMPHandler.delFdbEntry(), there is/are invalid input parameter: node " + nodeId + " vlanId " + vlanId + " destMac " + destMac);
        }
        if(isDummy){//TODO: remain isDummy? (place Dummy in every function, or remove from every function)
            logger.info("delFdbEntry(): set node {} with vlan {} destMac {}: dummy return SUCCESS", nodeId, vlanId, destMac);
            return new Status(StatusCode.SUCCESS);
        }

        //1. create items switchIP, community, for SNMP request
        String switchIP = cmethUtil.getIpAddr(nodeId);
        if(switchIP == null){
            logger.debug("ERROR: delFdbEntry(): node {} is not in DB", nodeId);
            return new Status(StatusCode.BADREQUEST, "In SNMPHandler.delFdbEntry(), node " + nodeId + " is not in DB (nodeId " + nodeId + " vlanId " + vlanId + " destMac " + destMac);
        }
        InetAddress sw_ipAddr;
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }catch(Exception e1) {
            logger.debug("ERROR: delFdbEntry(): switchIP {} into InetAddress.getByName() error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.delFdbEntry(), switchIP " + switchIP + " into InetAddress.getByName() error: " + e1);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: delFdbEntry(): switchIP {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.delFdbEntry(), switchIP " + switchIP + " into InetAddress.getByName() fails");
        }
        String community = cmethUtil.getSnmpCommunity(nodeId);
        if(community == null){
            logger.debug("ERROR: delFdbEntry(): Can't find the SNMP community of the node {} in DB", nodeId);
            return new Status(StatusCode.NOTFOUND, "In SNMPHandler.delFdbEntry(), can't find the SNMP community of the node " + nodeId + "in DB");
        }

        //2. open snmp communication interface
        SNMPv1CommunicationInterface comInterface;
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: delFdbEntry(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.delFdbEntry(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //3. set fwd table entry's type as invalid
        boolean ret = setFwdTableEntryType(comInterface, vlanId, destMac, FdbEntrySetType.INVALID);
        if(ret)
            return new Status(StatusCode.SUCCESS);
        else
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.delFdbEntry(), call setFwdTableEntryType() fail, with node " + nodeId + " vlanId " + vlanId + " destMac " + destMac);
    }

    private boolean setFwdTableEntryPort(SNMPv1CommunicationInterface comInterface, int vlan, long destMac, short port){
            String macOid = macAddrToOID(longTo6SegHexString(destMac));/*macAddrToOID(destMac);*/
            String vlanMacOid = portSetOID + "." + vlan + "." + macOid + ".0";
            String typeOid = typeSetOID + "." + vlan + "." + macOid + ".0";

            String convPortStr = convertToEthSwitchPortString(port);//e.g. port 1 would be 0x80000000
            if(convPortStr == null){
                logger.debug("ERROR: setFwdTableEntryPort(): fail to convert port {} to hexstring for snmp to send", port);
                return false;
            }
            //logger.debug("Convert port {} to hexstring for snmp to send: {}", port, convPortStr);

            String destMacStr = HexString.toHexString(destMac).toUpperCase();//destMacStr has no real effect, just for printing info on screen
            if(convPortStr == null){
                logger.debug("ERROR: setFwdTableEntryPort(destMac: {}, vlan: {}), call convertToEthSwitchPortString(), port {} to 1-or-0-per-port string, fails", destMacStr, vlan, port);
                return false;
            }
            byte[] convPort = new HexString().fromHexString(convPortStr);
            SNMPOctetString portOStr =  new SNMPOctetString(convPort);
            SNMPInteger typeInt =  new SNMPInteger(FdbEntrySetType.PERMANENT.getValue());

            //TODO(NOTICE): if exchange the 'port' and 'type' item in the following array, the FDB entry will be given a system-given value for the port instead of the value given here. (As for 'snmpset' tool, either order is successful)
            String[] oids = {vlanMacOid, typeOid};
            SNMPObject [] values = {portOStr, typeInt};
            SNMPVarBindList newVars;
            try{
                    //newVars = comInterface.setMIBEntry(vlanMacOid, portOStr); //comInterface.setMIBEntry() can either input array or variable, like here or below
                    newVars = comInterface.setMIBEntry(oids, values);
            }catch(Exception e1){
                    logger.debug("ERROR: setFwdTableEntryPort(): get exception when calling SNMP setMIBEntry():" + "\n" + 
                                        "Given: vlan {}, destMac {}, port {}" + "\n" +
                                        "Send: node {}, vlanMacOid {}, port {}, type {}" + "\n" +
                                        "Exception: {}", 
                                        vlan, destMacStr, port, 
                                        comInterface.getHostAddress(), vlanMacOid, portOStr.toHexString(), typeInt,
                                        e1);
                    //logger.debug("\n[Send to switch]:");
                    //for(int i = 0; i < oids.length; i++)
                    //    logger.debug("\n  OID: {}\n    value = {}", oids[i], values[i]);
                    //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                    return false;
            }
            //logger.debug("SNMPHandler.setFwdTableEntryPort(node: {}, vlan: {}, destMac: {}, port: {}, type: {}):", comInterface.getHostAddress(), vlan, destMacStr, port, typeInt);
            //logger.debug("\n[Send to switch]:");
            //for(int i = 0; i < oids.length; i++)
            //    logger.debug("\n  OID: {}\n    value = {}", oids[i], values[i]);
            //logger.debug("\n[Switch response]:\n  " + newVars.toString());

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: setFwdTableEntryPort(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return false;
            }

            return true;
    }

    private boolean setFwdTableEntryType(SNMPv1CommunicationInterface comInterface, int vlan, long destMac, FdbEntrySetType type){
            logger.trace("enter SNMPHandler.setFwdTableEntryType()...");

            String macOid = macAddrToOID(longTo6SegHexString(destMac));/*macAddrToOID(destMac);*/
            String vlanMacOid = portSetOID + "." + vlan + "." + macOid + ".0";
            String typeOid = typeSetOID + "." + vlan + "." + macOid + ".0";

            String destMacStr = HexString.toHexString(destMac).toUpperCase();//destMacStr has no real effect, just for printing info on screen
            SNMPInteger typeInt =  new SNMPInteger(type.getValue());

            //logger.debug("switch ({})'s OID: {}", destMacStr, macOid);
            //logger.debug("type: {}", typeInt.toString());

            SNMPVarBindList newVar;
            try{
                newVar = comInterface.setMIBEntry(typeOid, typeInt);
            }catch(Exception e1){
                    logger.debug("ERROR: setFwdTableEntryType(): get exception when calling SNMP setMIBEntry():" + "\n" + 
                                        "Given: vlan {}, destMac {}" + "\n" +
                                        "Send: node {}, typeOid(vlan&destMac) {}, type {} (2:del,3:static)" + "\n" +
                                        "Exception: {}",
                                        vlan, destMacStr, 
                                        comInterface.getHostAddress(), typeOid, typeInt,
                                        e1);
                    //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                    return false;
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: setFwdTableEntryType(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return false;
            }

            if(newVar == null){
                    logger.debug("ERROR: setFwdTableEntryType(): get exception when calling SNMP setMIBEntry():" + "\n" + 
                                        "Given: vlan {}, destMac {}" + "\n" +
                                        "Send: node {}, typeOid(vlan&destMac) {}, type {} (2:del,3:static)" + "\n",
                                        vlan, destMacStr, 
                                        comInterface.getHostAddress(), typeOid, typeInt
                                        );
                return false;
            }

            //logger.debug("SNMPHandler.setFwdTableEntryType(node: {}, vlan: {}, destMac: {}):", comInterface.getHostAddress(), vlan, destMacStr);
            //logger.debug("\n[Send to switch]:\n  OID: {}\n    value = {}", typeOid, typeInt);
            //logger.debug("\n[Switch response]:\n  " + newVar.toString());

            return true;
    }

    //s4s
    //This method is only used by sendBySNMP() which is called from FlowProgrammerService
    //TODO: NOTICE!There's unexpected result when type and port are set together, like in this method. The unexpected result is that the port appeared in the fdb table is not as what user set, even if using snmpset.
    //TODO: the log message contains function name 'setFwdTableEntry', should change to setFwdTableEntry_SetTypeAndPort
    private boolean setFwdTableEntry_SetTypeAndPort(SNMPv1CommunicationInterface comInterface, int vlan, long destMac, short port, int type){
            logger.trace("enter SNMPHandler.setFwdTableEntry_SetTypeAndPort()...");

            String macOid = macAddrToOID(longTo6SegHexString(destMac));/*macAddrToOID(destMac);*/
            String vlanMacOid = portSetOID + "." + vlan + "." + macOid + ".0";
            String typeOid = typeSetOID + "." + vlan + "." + macOid + ".0";

            String convPortStr = convertToEthSwitchPortString(port);//e.g. port 1 would be 0x80000000
            if(convPortStr == null){
                logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(): fail to convert port {} to hexstring for snmp to send", port);
                return false;
            }
            //logger.debug("Convert port {} to hexstring for snmp to send: {}", port, convPortStr);

            String destMacStr = HexString.toHexString(destMac).toUpperCase();//destMacStr has no real effect, just for printing info on screen
            if(convPortStr == null){
                logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(destMac: {}, vlan: {}, type: {}), call convertToEthSwitchPortString(), port {} to 1-or-0-per-port string, fails", destMacStr, vlan, type, port);
                return false;
            }
            byte[] convPort = new HexString().fromHexString(convPortStr);
            SNMPOctetString portOStr =  new SNMPOctetString(convPort);
            SNMPInteger typeInt =  new SNMPInteger(type);

            //logger.debug("switch ({})'s OID: {}", destMacStr, macOid);
            //logger.debug("type: {}", typeInt.toString());

            if(type == SNMPFlowMod.SNMPFC_DELETE_STRICT){//delete entry
                SNMPVarBindList newVar;
                try{
                    newVar = comInterface.setMIBEntry(typeOid, typeInt);
                }catch(Exception e1){
                    logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(): get exception when calling SNMP setMIBEntry():" + "\n" + 
                                        "Given: vlan {}, destMac {}, port {}" + "\n" +
                                        "Send: node {}, typeOid(vlan&destMac) {}, type(2:del,3:set) {}" + "\n" +
                                        "Exception: {}",
                                        vlan, destMacStr, port, 
                                        comInterface.getHostAddress(), typeOid, typeInt,
                                        e1);
                    //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                    return false;
                }
                //logger.debug("SNMPHandler.setFwdTableEntry_SetTypeAndPort(node: {}, vlan: {}, destMac: {}, port: {}):", comInterface.getHostAddress(), vlan, destMacStr, port);
                //logger.debug("\n[Send to switch]:\n  OID: {}\n    value = {}", typeOid, typeInt);
                //logger.debug("\n[Switch response]:\n  " + newVar.toString());

                try{
                    comInterface.closeConnection();
                }
                catch(SocketException e2){
                    logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                    return false;
                }
            }
            else if(type == SNMPFlowMod.SNMPFC_ADD || type == SNMPFlowMod.SNMPFC_MODIFY_STRICT){//add or modify entry
                //logger.debug("port: {}", portOStr.toString());

                //NOTE: as the observation in setFwdTableEntryPort(), see the "TODO(NOTICE)" there, it suggest order to be 'port' then 'type'
                String[] oids = {vlanMacOid, typeOid};
                SNMPObject [] values = {portOStr, typeInt};
                SNMPVarBindList newVars;
                try{
                    newVars = comInterface.setMIBEntry(oids, values); //comInterface.setMIBEntry() can either input array or variable, like here or below
                }catch(Exception e1){
                    logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(): get exception when calling SNMP setMIBEntry():" + "\n" + 
                                        "Given: vlan {}, destMac {}, port {}" + "\n" +
                                        "Send: node {}, vlanMacOid {}, typeOid {}, type(2:del,3:set) {}, port {}" + "\n" +
                                        "Exception: {}", 
                                        vlan, destMacStr, port, 
                                        comInterface.getHostAddress(), vlanMacOid, typeOid, typeInt, portOStr.toHexString(),
                                        e1);
                    //logger.debug("\n[Send to switch]:");
                    //for(int i = 0; i < oids.length; i++)
                    //    logger.debug("\n  OID: {}\n    value = {}", oids[i], values[i]);
                    //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                    return false;
                }
                //logger.debug("SNMPHandler.setFwdTableEntry_SetTypeAndPort(node: {}, vlan: {}, destMac: {}, port: {}):", comInterface.getHostAddress(), vlan, destMacStr, port);
                //logger.debug("\n[Send to switch]:");
                //for(int i = 0; i < oids.length; i++)
                //    logger.debug("\n  OID: {}\n    value = {}", oids[i], values[i]);
                logger.trace("\n[Switch response]:\n  " + newVars.toString());

                try{
                    comInterface.closeConnection();
                }
                catch(SocketException e2){
                    logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                    return false;
                }
            }
            else{
                logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(node: {}, vlan: {}, destMac: {}, port: {}), given invalid action type {}", comInterface.getHostAddress(), vlan, destMacStr, port, type);

                try{
                    comInterface.closeConnection();
                }
                catch(SocketException e2){
                    logger.debug("ERROR: setFwdTableEntry_SetTypeAndPort(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                    return false;
                }

                return false;
            }

            return true;
        
   }
   /* This is an example for the 'unexpected result' as described on the top of setFwdTableEntry_SetTypeAndPort()
   snmpset -v2c -c private 192.168.0.32 1.3.6.1.2.1.17.7.1.3.1.1.4.1.18.52.86.120.154.1.0 i 3 1.3.6.1.2.1.17.7.1.3.1.1.3.1.18.52.86.120.154.1.0 x 0x80000000
   snmpset -v2c -c private 192.168.0.32 1.3.6.1.2.1.17.7.1.3.1.1.4.1.18.52.86.120.154.2.0 i 3 1.3.6.1.2.1.17.7.1.3.1.1.3.1.18.52.86.120.154.2.0 x 0x40000000
   snmpset -v2c -c private 192.168.0.32 1.3.6.1.2.1.17.7.1.3.1.1.4.1.18.52.86.120.154.3.0 i 3 1.3.6.1.2.1.17.7.1.3.1.1.3.1.18.52.86.120.154.3.0 x 0x08000000
   */

    //s4s
    /*private String getIpAddr(Long macAddr){
        //look up table...
        return "10.216.0.31";
    }*///move to CmethUtil

    public Status sendBySNMP(Flow flow, int modType, Long sw_macAddr){
        //logger.trace("enter SNMPHandler.sendBySNMP()");

        String swIP = cmethUtil.getIpAddr(sw_macAddr);
        if(swIP == null){
            logger.debug("ERROR: sendBySNMP(): node with mac " + sw_macAddr + " doesn't exist!");
            return null;
        }

        if(isDummy){
            logger.info("sendBySNMP(), node {} and modType {}: dummy return SUCCESS", sw_macAddr, modType);
            return new Status(StatusCode.SUCCESS, null);
        }

        //logger.debug("retrieving the metrics in the Flow...");
        //retrieve from the flow: (1)src mac (2)dest mac (3)the port value, to write into fwd table
            //to retrieve (1)&(2)
        Match match = flow.getMatch();
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        //String destMac = HexString.toHexString((byte[])fieldDlDest.getValue());
        long destMac = HexString.toLong(HexString.toHexString((byte[])fieldDlDest.getValue()));//byte[] to long
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = ((Short)(fieldVlan.getValue())).shortValue();
            //to retrieve (3)
        Action action = flow.getActions().get(0);
        if(flow.getActions().size() > 1) {
            logger.debug("ERROR: sendBySNMP(): flow.getActions() > 1");
            return new Status(StatusCode.NOTALLOWED, "SNMPHandler.sendBySNMP(): flow.getActions() > 1");
        }
        if(action.getType() != ActionType.OUTPUT){
            logger.debug("ERROR: sendBySNMP(): flow's action is not to set OUTPUT port!");
            return new Status(StatusCode.NOTALLOWED, "SNMPHandler.sendBySNMP(): flow's action is not to set OUTPUT port!");
        }
        NodeConnector oport = ((Output)action).getPort();


        //Use snmp to write to switch fwd table...
            //1. create items switchIP, community, for SNMP request
        String switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(swIP == null){
            logger.debug("ERROR: sendBySNMP(): Can't find the SNMP community of the node with mac " + sw_macAddr + "in DB");
            return new Status(StatusCode.NOTFOUND, "Can't find the IP address of the node with mac " + sw_macAddr + "in DB");
        }
        InetAddress sw_ipAddr;
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }catch(Exception e) {
            logger.debug("ERROR: sendBySNMP(): sw_macAddr {} into InetAddress.getByName() error: {}", sw_macAddr, e);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.sendBySNMP(), sw_macAddr " + sw_macAddr + " into InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: sendBySNMP(): sw_macAddr {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.sendBySNMP(), sw_macAddr " + sw_macAddr + " into InetAddress.getByName() fails");
        }
        String community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: sendBySNMP(): Can't find the SNMP community of the node with mac " + sw_macAddr + "in DB");
            return new Status(StatusCode.NOTFOUND, "Can't find the SNMP community of the node with mac " + sw_macAddr + "in DB");
        }

        //2. open snmp communication interface
        SNMPv1CommunicationInterface comInterface;
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: sendBySNMP(): for node {}" + ", create SNMP communication interface error: {}", swIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In SNMPHandler.sendBySNMP(), sw_macAddr " + sw_macAddr + " into InetAddress.getByName() error: " + e1);
        }

        //3. set fwd table entry
        // logger.debug("going to set fwd table entry...");
        Short portShort = (Short)(oport.getID());
        short portID = portShort.shortValue();
        boolean isSuccess = setFwdTableEntry_SetTypeAndPort(comInterface, (int)vlan, destMac, portID, modType);//oport.getID() is the port.  modType as 3 means "learned". modType as 2 means "invalid"
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to set fwd table", "Vendor Extension Internal Error");
            logger.debug("ERROR: sendBySNMP(): " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    //This method is for FDB Service (FdbServiceImpl.java) to call
    public FDBEntry readFdbTableEntry(long nodeId, int vlanId, long destMac){
        String switchIP;
        InetAddress sw_ipAddr = null;
        SNMPv1CommunicationInterface comInterface;

        //error check
        if(nodeId < 0){
            logger.debug("ERROR: readFdbTableEntry(): given invalid nodeId {}", nodeId);
            return null;
        }
        if(!isValidVlan(vlanId)){
            logger.debug("ERROR: readFdbTableEntry(): given invalid vlanId {}", vlanId);
            return null;
        }
        if(destMac < 0){
            logger.debug("ERROR: readFdbTableEntry(): given invalid destMac {}", destMac);
            return null;
        }

        //1. open snmp communication interface
        switchIP = cmethUtil.getIpAddr(nodeId);
        if(switchIP == null){
            logger.debug("ERROR: readFdbTableEntry(): can't find node {}'s IP address in DB", nodeId);
            return null;
        }
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (Exception e) {
            logger.debug("ERROR: readFdbTableEntry(): nodeId {} into InetAddress.getByName() error!: {}", nodeId, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readFdbTableEntry(): nodeId {}, convert InetAddress fails", switchIP);
            return null;
        }

        //2. read fwd table entry
        //logger.debug("going to read fwd table entry...");
        try{
            String community = cmethUtil.getSnmpCommunity(nodeId);
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readFdbTableEntry(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }
        short port = (short)readFwdTableEntry(comInterface, (short)vlanId, destMac);
        if(port < 0){
            logger.debug("ERROR: readFdbTableEntry(): given nodeId {} vlanId {} destMac {}, call readFwdTableEntry() returns invalid port {}", nodeId, vlanId, destMac, port);
            return null;
        }

        //3. read fwd table entry's 'type' field
        //logger.debug("going to read fwd table entry type...");
        try{
            String community = cmethUtil.getSnmpCommunity(nodeId);
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readFdbTableEntry(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }
        FDBEntry.EntryType type = readFwdTableEntryType(comInterface, (short)vlanId, destMac);
        if(type == null){
            logger.debug("ERROR: readFwdTableEntryType(): given nodeId {} vlanId {} destMac {}, call readFwdTableEntryType() returns null", nodeId, vlanId, destMac);
            return null;
        }

        //4. convert the retrieved entry to FDBEntry
        FDBEntry entry = new FDBEntry();
        entry.nodeId = nodeId;
        entry.destMacAddr = destMac;
        entry.vlanId = vlanId;
        entry.port = port;
        entry.type = type;
        return entry;
    }

    private int readFwdTableEntry(SNMPv1CommunicationInterface comInterface, short vlan, long destMac){
        //logger.trace("enter readFwdTableEntry()...");
        String macOid = macAddrToOID(longTo6SegHexString(destMac));/*macAddrToOID(destMac);*/
        String vlanMacOid = portGetOID + "." + vlan + "." + macOid;
        String destMacStr = HexString.toHexString(destMac).toUpperCase();//destMacStr has no real effect, just to print info on screen

        SNMPVarBindList newVars;
        try{
            newVars = comInterface.getMIBEntry(vlanMacOid);
        }
        catch(Exception e)
        {
            logger.debug("ERROR: readFwdTableEntry(): Exception during SNMP getMIBEntry: {}", e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return -1;//meaning fail
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readFwdTableEntry(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return -1;
        }

        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: readFwdTableEntry(): get SNMPUnknownObject, meaning no such FDB entry (node: {}, vlan: {}, destMac: {})", comInterface.getHostAddress(), vlan, destMacStr);
            return -1;//meaning fail
        }
        SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
        int valueInt = ((BigInteger)value.getValue()).intValue();

        logger.trace("readFwdTableEntry(): get value of {} = {}", value.getClass().getName(), valueInt);

        return valueInt;
    }

    private FDBEntry.EntryType readFwdTableEntryType(SNMPv1CommunicationInterface comInterface, short vlan, long destMac){
        //logger.trace("enter readFwdTableEntryType()...");
        String macOid = macAddrToOID(longTo6SegHexString(destMac));/*macAddrToOID(destMac);*/
        String getTypeOid = typeGetOID + "." + vlan + "." + macOid;
        String destMacStr = HexString.toHexString(destMac).toUpperCase();//destMacStr has no real effect, just to print info on screen

        SNMPVarBindList newVars;
        try{
            newVars = comInterface.getMIBEntry(getTypeOid);
        }
        catch(Exception e1)
        {
            logger.debug("ERROR: readFwdTableEntryType(): Exception during SNMP getMIBEntry: {}", e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;//meaning fail
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readFwdTableEntryType(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }

        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: readFwdTableEntryType(): get SNMPUnknownObject, meaning no such FDB entry (node: {}, vlan: {}, destMac: {})", comInterface.getHostAddress(), vlan, destMacStr);
            return null;//meaning fail
        }
        SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);
        int valueInt = ((BigInteger)value.getValue()).intValue();

        logger.trace("readFwdTableEntryType: get value of {} = {}" + value.getClass().getName(), valueInt);

        if(valueInt == FDBEntry.EntryType.OTHER.ordinal())
                return FDBEntry.EntryType.OTHER;
        else if(valueInt == FDBEntry.EntryType.INVALID.getValue())
                return FDBEntry.EntryType.INVALID;
        else if(valueInt == FDBEntry.EntryType.LEARNED.getValue())
                return FDBEntry.EntryType.LEARNED;
        else if(valueInt == FDBEntry.EntryType.SELF.getValue())
                return FDBEntry.EntryType.SELF;
        else if(valueInt == FDBEntry.EntryType.MGMT.getValue())
                return FDBEntry.EntryType.MGMT;
        else{
                logger.debug("ERROR: readFwdTableEntryType(): get unknown entry type value {} (node: {}, vlan: {}, destMac: {})", valueInt, comInterface.getHostAddress(), vlan, destMacStr);
                return null;
        }
    }

    private Map<String, Integer> readAllFwdTableEntry(SNMPv1CommunicationInterface comInterface){
            logger.trace("enter readAllFwdTableEntry()...");
            Map<String, Integer> table =  new HashMap<String, Integer>();

            //logger.debug("to retieve oid {}'s value...", portGetOID);

            SNMPVarBindList tableVars;
            try{
                tableVars = comInterface.retrieveMIBTable(portGetOID);
            }
            catch(Exception e1)
            {
                logger.debug("ERROR: readAllFwdTableEntry(), exception during SNMP getMIBEntry: {}", e1);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return null;
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: readAllFwdTableEntry(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return null;
            }
            
            logger.trace("Number of table entries: {}", tableVars.size());
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
                logger.trace("readAllFwdTableEntry(): Retrieved OID {} (vlan:{}, mac:{}): value of {} = {}", snmpOID, vlanOID, macOID, value.getClass().getName(), valueInt);
            }
            return table;
      }

    public FlowOnNode readFlowRequest(Flow flow, Node node){
        //logger.trace("enter SNMPHandler.readFlowRequest()");

        Long swMacAddr = (Long) node.getID();
        String swIP = cmethUtil.getIpAddr(swMacAddr);
        if(swIP == null){
            logger.debug("readFlowRequest(): node with mac" + swMacAddr + "doesn't exist!");
            return null;
        }

        if(isDummy){
            logger.info("dummy return the input flow as FlowOnNode");
            return new FlowOnNode(flow);
        }

        //logger.debug("retrieving the metrics in the Flow...");
        //retrieve dest mac from the flow
        Match match = flow.getMatch();
        MatchField fieldVlan = match.getField(MatchType.DL_VLAN);
        short vlan = ((Short)(fieldVlan.getValue())).shortValue();
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        long destMac = HexString.toLong(HexString.toHexString((byte[])fieldDlDest.getValue()));//byte[] to long

        //Use snmp to read switch fwd table...

        Long sw_macAddr = (Long) node.getID();
        String switchIP;
        InetAddress sw_ipAddr = null;
        SNMPv1CommunicationInterface comInterface;

        //1. open snmp communication interface
        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: readFlowRequest(): can't find node {}'s IP address in DB", sw_macAddr);
            return null;
        }
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (Exception e) {
            logger.debug("ERROR: readFlowRequest(): sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readFlowRequest(): sw_macAddr {}, convert InetAddress fails", switchIP);
            return null;
        }

        try{
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readFlowRequest(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. read fwd table entry
        //logger.debug("going to read fwd table entry...");
        int port = readFwdTableEntry(comInterface, vlan, destMac);
        if(port < 0){
            logger.debug("ERROR: readFlowRequest(): given node {}, call readFwdTableEntry() given vlan {} and destMac {}, fails", swMacAddr, vlan, destMac);
            return null;
        }

        //3. convert the retrieved entry to FlowOnNode
        NodeConnector oport = NodeConnectorCreator.createNodeConnector("SNMP", (short)port, node);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow flown = new Flow(flow.getMatch(), actions);
        return new FlowOnNode(flown);
    }

    //return value: 1. null -- switch not found  2. an empty List<FlowOnNode> -- switch found and has no entries
    public List<FlowOnNode>  readAllFlowRequest(Node node){
        //logger.trace("enter SNMPHandler.readAllFlowRequest()");

        Long swMacAddr = (Long) node.getID();
        String swIP = cmethUtil.getIpAddr(swMacAddr);
        if(swIP == null){
            logger.debug("ERROR: readAllFlowRequest(): node with mac" + swMacAddr + "doesn't exist!");
            return null;
        }

        if(isDummy){
            logger.info("readAllFlowRequest: dummy return empty List<FlowOnNode>");
            return new ArrayList<FlowOnNode>();
        }

        //Use snmp to read switch fwd table...

        Long sw_macAddr = (Long) node.getID();
        String switchIP;
        InetAddress sw_ipAddr = null;
        SNMPv1CommunicationInterface comInterface;

        //1. open snmp communication interface
        try{
            switchIP = cmethUtil.getIpAddr(sw_macAddr);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (Exception e) {
            logger.debug("ERROR: readFlowRequest(): sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readFlowRequest(): sw_macAddr {}, convert InetAddress fails", switchIP);
            return null;
        }

        try{
            String community = cmethUtil.getSnmpCommunity(sw_macAddr);
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readFlowRequest(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. now can set fwd table entry
        //logger.debug("going to read fwd table entry...");
        Map<String, Integer> entries = readAllFwdTableEntry(comInterface);
        if(entries == null){
            logger.debug("ERROR: readAllFlowRequest(): switch's IP is {}, call readAllFwdTableEntry() fail", swIP);
            return null;
        }
        return forwardingTableEntriesToFlows(entries, node);
    }


    //This method is for FDB Service (FdbServiceImpl.java) to call
    public List<FDBEntry> readAllFdbTableEntry(long nodeId){
        String swIP = cmethUtil.getIpAddr(nodeId);
        if(swIP == null){
            logger.debug("ERROR: readAllFdbTableEntry(): node {} is not in DB!", nodeId);
            return null;
        }

        if(isDummy){
            logger.info("readAllFdbTableEntry: dummy return empty List<FDBEntry> for node {}", nodeId);
            return new ArrayList<FDBEntry>();
        }

        //Use snmp to read switch fwd table...

        String switchIP;
        InetAddress sw_ipAddr = null;
        SNMPv1CommunicationInterface comInterface;

        //1. open snmp communication interface
        try{
            switchIP = cmethUtil.getIpAddr(nodeId);
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (Exception e1) {
            logger.debug("ERROR: readAllFdbTableEntry(): sw_ipAddr {} into InetAddress.getByName() error!: {}", sw_ipAddr, e1);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readAllFdbTableEntry(): sw_ipAddr {}, convert InetAddress fails", switchIP);
            return null;
        }

        try{
            String community = cmethUtil.getSnmpCommunity(nodeId);
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readAllFdbTableEntry(): for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. now can set fwd table entry
        //logger.debug("going to read fwd table entry...");
        List<FDBEntry> entries = readAllFdbTableEntryFromSwitch(nodeId, comInterface);
        if(entries == null){
            logger.debug("ERROR: readAllFdbTableEntry(): node {} ({}), call readAllFwdTableEntry() fail", nodeId, swIP);
            return null;
        }
        return entries;
    }

    private List<FDBEntry> readAllFdbTableEntryFromSwitch(long nodeId, SNMPv1CommunicationInterface comInterface){//here code is copy from readAllFwdTableEntry(), just modify the input and the return object
            logger.trace("enter readAllFdbTableEntry()...");
            List<FDBEntry> entries;

            //logger.debug("to retieve oid {}'s value...", portGetOID);

            SNMPVarBindList tableVars;
            try{
                tableVars = comInterface.retrieveMIBTable(new String[]{portGetOID, typeGetOID});
            }
            catch(Exception e1)
            {
                logger.debug("ERROR: readAllFdbTableEntryFromSwitch(): exception during SNMP retrieveMIBTable() from node {}: {}", comInterface.getHostAddress(), e1);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return null;
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: readAllFdbTableEntryFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return null;
            }

            entries = makeFDBEntryListFromSNMPVarBindList(nodeId, tableVars);
            if(entries == null){
                logger.debug("ERROR: readAllFdbTableEntryFromSwitch(): error in makeFDBEntryListFromSNMPVarBindList() for node {}", comInterface.getHostAddress());
                return null;
            }
            entries = fillInTypeFieldInFDBEntryList(nodeId, tableVars, entries);
            if(entries == null){
                logger.debug("ERROR: readAllFdbTableEntryFromSwitch(): error in fillInTypeFieldInFDBEntryList() for node {}", comInterface.getHostAddress());
                return null;
            }

            return entries;
    }

    private List<FDBEntry> makeFDBEntryListFromSNMPVarBindList(long nodeId, SNMPVarBindList tableVars){
        List<FDBEntry> entries = new ArrayList<FDBEntry>();
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);

                //convert recieved snmp fields into string values
                int valueInt = ((BigInteger)value.getValue()).intValue();//port value
                String snmpOIDstr = snmpOID.toString();
                if(snmpOIDstr.substring(0, typeGetOID.length()).equalsIgnoreCase(typeGetOID))
                    continue;//skip the rows for 'getting type', will process these row in the next for loop
                String vlanmacOID = snmpOIDstr.substring(portGetOID.length() + 1);
                String vlanOID = vlanmacOID.substring(0, vlanmacOID.indexOf("."));
                String macOID = vlanmacOID.substring(vlanmacOID.indexOf(".") + 1);
                //logger.debug("readAllFdbTableEntry(): Retrieved OID {} (vlan part:{}, mac part:{}): value of {} = {}", snmpOID, vlanOID, macOID, value.getClass().getName(), valueInt);

                //values error checking
                long macLong = macOIDToLong(macOID);
                int vlanId = Integer.parseInt(vlanOID);
                if(vlanId < 0){
                    logger.debug("ERROR: makeFDBEntryListFromSNMPVarBindList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, has invalid vlanId", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }
                if(macLong < 0){
                    logger.debug("ERROR: makeFDBEntryListFromSNMPVarBindList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, the mac part is invalid", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }
                if(valueInt < 0){
                    logger.debug("ERROR: makeFDBEntryListFromSNMPVarBindList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, has invalid port", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }

                FDBEntry entry = new FDBEntry();
                entry.nodeId = nodeId;
                entry.destMacAddr = macLong;
                entry.vlanId = vlanId;
                entry.port = (short)valueInt;
                //logger.debug("readAllFdbTableEntry(): convert received information to return value: nodeId {} vlanId {} destMac {}  port {}", entry.nodeId, entry.vlanId, entry.destMacAddr, valueInt);

                entries.add(entry);
        }
        return entries;
    }

    private List<FDBEntry> fillInTypeFieldInFDBEntryList(long nodeId, SNMPVarBindList tableVars, List<FDBEntry> entries){
        if(nodeId < 0 || tableVars == null || entries == null){
            logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): invalid parameter: invalid nodeId {} or null SNMPVarBindList or null List<FDBEntry>", nodeId);
            return null;
        }
 
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);

                //convert recieved snmp fields into string values
                int valueInt = ((BigInteger)value.getValue()).intValue();//port value
                String snmpOIDstr = snmpOID.toString();
                if(snmpOIDstr.substring(0, portGetOID.length()).equalsIgnoreCase(portGetOID))
                    continue;//skip the rows for 'getting port'
                String vlanmacOID = snmpOIDstr.substring(typeGetOID.length() + 1);
                String vlanOID = vlanmacOID.substring(0, vlanmacOID.indexOf("."));
                String macOID = vlanmacOID.substring(vlanmacOID.indexOf(".") + 1);
                //logger.debug("readAllFdbTableEntry(): Retrieved OID {} (vlan part:{}, mac part:{}): value of {} = {}", snmpOID, vlanOID, macOID, value.getClass().getName(), valueInt);

                //values error checking
                long macLong = macOIDToLong(macOID);
                int vlanId = Integer.parseInt(vlanOID);
                if(vlanId < 0){
                    logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, has invalid vlanId", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }
                if(macLong < 0){
                    logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, the mac part is invalid", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }
                if(valueInt < 0){
                    logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): Retrieved OID {} (vlan part:{}, mac part:{}) port {}, has invalid port", snmpOID, vlanOID, macOID, valueInt);
                    return null;
                }

                //convert entry type to FdbEntryType
                FDBEntry.EntryType type;
                if(valueInt == FDBEntry.EntryType.OTHER.getValue())
                        type = FDBEntry.EntryType.OTHER;
                else if(valueInt == FDBEntry.EntryType.INVALID.getValue())
                        type = FDBEntry.EntryType.INVALID;
                else if(valueInt == FDBEntry.EntryType.LEARNED.getValue())
                        type = FDBEntry.EntryType.LEARNED;
                else if(valueInt == FDBEntry.EntryType.SELF.getValue())
                        type = FDBEntry.EntryType.SELF;
                else if(valueInt == FDBEntry.EntryType.MGMT.getValue())
                        type = FDBEntry.EntryType.MGMT;
                else{
                        logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): get unknown entry type value {} (node: {}, vlan: {}, destMac: {})", valueInt, nodeId, vlanId, macLong);
                        return null;
                }
                if(type == null){
                    logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): can't convert snmp recieved entry type value {} to FDBEntry.EntryType (node: {}, vlan: {}, destMac: {})", valueInt, nodeId, vlanId, macLong);
                    return null;
                }

                FDBEntry fdbEntry = new FDBEntry();
                fdbEntry.nodeId = nodeId;
                fdbEntry.destMacAddr = macLong;
                fdbEntry.vlanId = vlanId;

                boolean found = fillInEntryType(entries, fdbEntry, type);
                if(!found){
                    logger.debug("ERROR: fillInTypeFieldInFDBEntryList(): can't find entry with the index (node: {}, vlan: {}, destMac: {}) in FDBEntry list (the FDBEntry list was created with snmp get_port_OID)", valueInt, nodeId, vlanId, macLong);
                    return null;
                }
        }
        return entries;
    }

    private boolean fillInEntryType(List<FDBEntry> entries, FDBEntry fdbEntry, FDBEntry.EntryType type){
        boolean found = false;
        for(FDBEntry entry : entries){
            if(entry.nodeId == fdbEntry.nodeId && entry.destMacAddr == fdbEntry.destMacAddr && entry.vlanId == fdbEntry.vlanId){
                entry.type = type;
                found = true;
            }
        }
        return found;
    }
    
    private long macOIDToLong(String macOID){//e.g. given "12.0.05.7.10.16", then return "0C:00:05:07:0A:10"'s long value
        String result = "";
        int loc1 = 0, loc2 = 0;
        int count = 0;

        while(loc1 < macOID.length()){
            if(count++ >= NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB){
                logger.debug("ERROR: FDB table has mac addr longer than NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB (default = " + NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_FDB + ")");
                return -1;
            }
            loc2 = macOID.indexOf(".", loc1 + 1);
            if(loc2 < 0)
                loc2 = macOID.length();
            String subaddr = macOID.substring(loc1, loc2);
            int subaddrInt = Integer.parseInt(subaddr);
            String subHex = Integer.toHexString(subaddrInt);
            if(subHex.length() == 1)
                result = result + ":" +  "0" + subHex;
            else if(subHex.length() == 2)
                result = result + ":" + subHex;
            else
                return -1;
            loc1 = loc2 + 1;
        }

        result = result.substring(1, result.length());
        return HexString.toLong(result);
    }
    
    private long macOIDToLong_cantWorkWhy(String macOID){//e.g. given "12.0.05.7.10.16", then return "0C:00:05:07:0A:10"'s long value
        String hexStr = macOID.replaceAll(".", ":");
        logger.debug("macOIDToLong(): convert {} to {}", macOID, hexStr);
        return HexString.toLong(hexStr);
    }

    private List<FlowOnNode> forwardingTableEntriesToFlows(Map<String, Integer> entries, Node node){
        List<FlowOnNode> list = new ArrayList<FlowOnNode>();
        for(Map.Entry<String, Integer> entry : entries.entrySet()){
            Match match = new Match();
            String str = entry.getKey();
            short vlan = Short.parseShort(str.substring(0, str.indexOf(".")));
            byte[] macAddrBytes = OIDToMacAddrBytes(str.substring(str.indexOf(".") + 1));
            if(macAddrBytes == null){
                logger.debug("ERROR: forwardingTableEntriesToFlows(): nodeID is {}, call OIDToMacAddrBytes() fail", (Long)node.getID());
                return null;
            }

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

    private Short retrievePortNumFromChassisOIDAtEnd(String oidstr){
        //e.g. oidstr as "iso.3.6.1.2.1.2.2.1.7.40", then return "40"
        int tail = oidstr.lastIndexOf(".") + 1;
        String ansStr= oidstr.substring(tail, oidstr.length());
        Short ans = Short.parseShort(ansStr);
        //logger.debug("oidstr (" + oidstr +") to retrieve port number:" + ans);
        return ans;
    }

    private String asciiStringToHexString(byte[] valueBytes){
        String ret = new String(valueBytes);
        ret = ret.replaceAll("-", ":");
        if(ret.length() != NUMBER_OF_BYTES_FOR_IPV4_MAC_ADDRESS * 2 + (NUMBER_OF_BYTES_FOR_IPV4_MAC_ADDRESS -1))//the number of characters in a mac-address format string
            logger.debug("WARNING: {} is of length {} which is not equal to 6-seg mac-address format", ret, ret.length());
        return ret;
    }

    private Map<Short, String> readLLDPRemoteChassisIDEntries(SNMPv1CommunicationInterface comInterface){
            logger.trace("enter readLLDPRemoteChassisIDEntries()...");
            Map<Short, String> table =  new HashMap<Short, String>();

            SNMPVarBindList tableVars;
            try{
                tableVars = comInterface.retrieveMIBTable(lldpRemoteChassisIdOID);
            }
            catch(Exception e1){
               logger.debug("ERROR: readLLDPRemoteChassisIDEntries(): Exception during SNMP retrieveMIBTable for node {}: {}", comInterface.getHostAddress(), e1);
               //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
               return null;
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: readLLDPRemoteChassisIDEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return null;
            }
 
            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                //TODO: unsolved issue: for D-link DGS-3650 switch, remote chassis ID subtype is 4 (mac-address form), but it gives a String instead of a Hex-String
                //Bug fix: for D-link DGS-3650, chassis ID is String; in earlier switch, chassis ID is Hex-String
                String valueStr  = HexString.toHexString(valueBytes);//for general switch
                if(cmethUtil.getModel(cmethUtil.getSID(comInterface.getHostAddress())).equals("D-Link_DGS-3650")){
                    valueStr = asciiStringToHexString(valueBytes);//for D-Link DGS-3650 switch
                }

                table.put(portNum, valueStr);
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueStr);
            }
            return table;
    }
 
    public Map<Short, String>  readLLDPAllRemoteChassisID(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.trace("enter SNMPHandler.readLLDPAllRemoteChassisID()");

        //Use snmp to read switch fwd table...

        String switchIP;
        InetAddress sw_ipAddr;
        String community;
        SNMPv1CommunicationInterface comInterface;

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: readLLDPAllRemoteChassisID() for node, mac addr: {}, can't find the IP address of the node in DB", sw_macAddr);
            return null;
        }
        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: readLLDPAllRemoteChassisID() for node, mac addr: {}, can't find the SNMP community of the node in DB", sw_macAddr);
            return null;
        }

        //1. open snmp communication interface
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (Exception e) {
            logger.debug("ERROR: readLLDPAllRemoteChassisID(): sw_macAddr {} into InetAddress.getByName() error!: {}", sw_macAddr, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readLLDPAllRemoteChassisID(): sw_macAddr {}, convert InetAddress fails", switchIP);
            return null;
        }

        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readLLDPAllRemoteChassisID(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }
        //2. now can set fwd table entry
        //logger.debug("going to read LLDP remote chassis IDs...");
        return readLLDPRemoteChassisIDEntries(comInterface);
    }

    public String getLLDPChassis(Long sw_macAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        logger.trace("enter SNMPHandler.getLLDPChassis({})...", HexString.toHexString(sw_macAddr));
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getLLDPChassis() for node, mac addr: {}, can't find the IP address of the node in DB", sw_macAddr);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getLLDPChassis() for node, mac addr: {}, can't find the SNMP community of the node in DB", sw_macAddr);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: getLLDPChassis() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getLLDPChassis() for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getLLDPChassis(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        SNMPVarBindList newVars;
        try{
                newVars = comInterface.getMIBEntry(lldpLocalChassisIdOID);
        }catch(Exception e1){
                logger.debug("ERROR: getLLDPChassis(), for node {}" + ", call SNMP comInterface.getMIBEntry error: {}", switchIP, e1);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return null;//meaning fail
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getLLDPChassis(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }

        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: getLLDPChassis(), for node {}, get SNMPUnknownObject, meaning no such LLDP chassis", switchIP);
            return null;
        }
        SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);//TODO: error checking (may reference to getVLANPort())
        byte[] valueBytes = (byte[])value.getValue();
        //TODO: issue: for D-link DGS-3650 switch, local chassis ID subtype is 4 (mac-address form), but it gives a String instead of a Hex-String
        //TODO: here we may try getting local chass ID subtype from earlier switch to confirm whether ths issue here is solvable
        //Bug fix: for D-link DGS-3650, chassis ID is String; in earlier switch, chassis ID is Hex-String
        String valueStr = HexString.toHexString(valueBytes);//for general switch
        if(cmethUtil.getModel(cmethUtil.getSID(comInterface.getHostAddress())).equals("D-Link_DGS-3650")){
            valueStr = asciiStringToHexString(valueBytes);//for D-Link DGS-3650 switch
        }

        return valueStr;        
    }

    public String getLLDPChassis(String sw_ipAddr){//return a hex-string, e.g. 70 72 CF 2A 80 E9 (just a chassis id, not mac address!)
        Long switchID = cmethUtil.getSID(sw_ipAddr);
        if(switchID == null){
            logger.debug("ERROR: getLLDPChassis(), for node {}, can't find MAC address in DB", sw_ipAddr);
            return null;
        }
        return getLLDPChassis(switchID);
    }

    public Map<Short, String>  readLLDPLocalPortIDs(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.trace("enter SNMPHandler.readLLDPLocalPortIDs()");

        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: readLLDPLocalPortIDs() for node, mac addr: {}, can't find the IP address of the node in DB", sw_macAddr);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: readLLDPLocalPortIDs() for node, mac addr: {}, can't find the SNMP community of the node in DB", sw_macAddr);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: readLLDPLocalPortIDs() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: readLLDPLocalPortIDs() for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readFlowRequest(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP local port IDs...");
        return readLLDPLocalPortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPLocalPortIDEntries(SNMPv1CommunicationInterface comInterface){
            logger.trace("enter SNMPHandler.readLLDPLocalPortIDEntries()...");
            Map<Short, String> table =  new HashMap<Short, String>();
            SNMPVarBindList tableVars;

            try{
                //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

                tableVars = comInterface.retrieveMIBTable(lldpLocalPortIdOID);
            }
           catch(Exception e1)
           {
               logger.debug("ERROR: readLLDPLocalPortIDEntries(), Exception during SNMP retrieveMIBTable() for node {}: {}", comInterface.getHostAddress(), e1);
               //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
               return null;
           }

            Map<Short, Integer> localPortIDTypeTable = readLLDPLocalPortIDTypeEntries(comInterface);
            if(localPortIDTypeTable == null){
                logger.debug("ERROR: readLLDPLocalPortIDEntries(): call readLLDPLocalPortIDTypeEntries() fail, given SNMP interface of node {}", comInterface.getHostAddress());
                return null;
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: readLLDPLocalPortIDEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return null;
            }

            //logger.debug("Number of table entries: " + tableVars.size());
            for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromPortOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr;

                if(localPortIDTypeTable.get(portNum) == null){
                    logger.debug("ERROR: readLLDPLocalPortIDEntries(), localPortIDTypeTable of node {} has no entry for port {}", comInterface.getHostAddress(), portNum);
                    return null;
                }
                int portIdType = localPortIDTypeTable.get(portNum).intValue();
                if(portIdType == portIdType_MacAddr){
                    valueStr = HexString.toHexString(valueBytes);
                }
                else if(portIdType == portIdType_ifName){
                    valueStr = new String(valueBytes);
                }
                else if(portIdType == portIdType_LocallyAssigned){
                    valueStr = new String(valueBytes);
                }
                else{
                    logger.debug("ERROR: readLLDPLocalPortIDEntries(), portIdType {} is unkown for node {} port {}", portIdType, comInterface.getHostAddress(), portNum);
                    return null;
                }

                table.put(portNum, valueStr.trim());//Bug fix: on D-Link DGS-3650, the portId is a string representing a number, however the string is from bytes and seems contains non-visual character in the string, so let's trim() to clean it
                //logger.debug("Retrieved OID: " + snmpOID + " (so port num=" + portNum + "), value: " + valueStr);
            }
            return table;
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
        logger.trace("enter SNMPHandler.readLLDPRemotePortIDs()");

        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: readLLDPRemotePortIDs() for node, mac addr: {}, can't find the IP address of the node in DB", sw_macAddr);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: readLLDPRemotePortIDs() for node, mac addr: {}, can't find the SNMP community of the node in DB", sw_macAddr);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: readLLDPRemotePortIDs() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR:readLLDPRemotePortIDs() for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readLLDPRemotePortIDs(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP remote chassis IDs...");
        return readLLDPRemotePortIDEntries(comInterface);
    }

    private Map<Short, String> readLLDPRemotePortIDEntries(SNMPv1CommunicationInterface comInterface){
        logger.trace("enter readLLDPRemotePortIDEntries()...");
        Map<Short, String> table =  new HashMap<Short, String>();
        SNMPVarBindList tableVars;

        try{
            //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            tableVars = comInterface.retrieveMIBTable(lldpRemotePortIdOID);
        }
        catch(Exception e1)
        {
            logger.debug("ERROR: readLLDPRemotePortIDEntries(), Exception during SNMP retrieveMIBTable() for node {}: {}", comInterface.getHostAddress(), e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        Map<Short, Integer> remotePortIDTypeTable = readLLDPRemotePortIDTypeEntries(comInterface);
        if(remotePortIDTypeTable == null){
            logger.debug("ERROR: readLLDPRemotePortIDEntries(): call readLLDPRemotePortIDTypeEntries() fail, given SNMP interface of node {}", comInterface.getHostAddress());
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readLLDPRemotePortIDEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }

        //logger.debug("Number of table entries: " + tableVars.size());
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);

                byte[] valueBytes = (byte[])value.getValue();
                String valueStr;

                if(remotePortIDTypeTable.get(portNum) == null){
                    logger.debug("ERROR: readLLDPRemotePortIDEntries(), remotePortIDTypeTable of node {} has no entry for port {}", comInterface.getHostAddress(), portNum);
                    return null;
                }
                int portIdType = remotePortIDTypeTable.get(portNum).intValue();
                if(portIdType == portIdType_MacAddr){
                    valueStr = HexString.toHexString(valueBytes);
                }
                else if(portIdType == portIdType_ifName){
                    valueStr = new String(valueBytes);
                }
                else if(portIdType == portIdType_LocallyAssigned){
                    valueStr = new String(valueBytes);
                }
                else{
                    logger.debug("ERROR: readLLDPRemotePortIDEntries(), portIdType {} is unknown for node {} port {}", portIdType, comInterface.getHostAddress(), portNum);
                    return null;
                }
                table.put(portNum, valueStr.trim());//Bug fix: on D-Link DGS-3650, the portId is a string representing a number, however the string is from bytes and seems contains non-visual character in the string, so let's trim() to clean it
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueStr);
        }
        return table;
    }

    /*
    * Bug fix
    *
    * Author: Christine
    * Date: 2015/4/17
    * Description: One needs PortIDType to correctly handle the remote port ID.
    * Code modification: for this fix, we add the following function,
    *                     and also utilize this function readLLDPRemotePortIDEntries() .
    *
    */
    private Map<Short, Integer> readLLDPRemotePortIDTypeEntries(SNMPv1CommunicationInterface comInterface){
        //logger.trace("enter readLLDPRemotePortIDTypeEntries()...");

        Map<Short, Integer> table =  new HashMap<Short, Integer>();
        SNMPVarBindList tableVars;

        try{
            //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            tableVars = comInterface.retrieveMIBTable(lldpRemotePortIdTypeOID);
        }
        catch(Exception e1)
        {
            logger.debug("ERROR: readLLDPRemotePortIDTypeEntries(), Exception during SNMP retrieveMIBTable() for node {}: {}", comInterface.getHostAddress(), e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        /*//TODO: snmp connection should be created and closed here inpendently? (but need fix that comInterface had been closed before entering here, and here we don't know the switchIP for create snmp)
        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readLLDPRemotePortIDTypeEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }*/

        //logger.debug("Number of table entries: " + tableVars.size());
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromChassisOID(snmpOIDstr);//TODO: suggest to change a function name?  

                int valueInt = ((BigInteger)value.getValue()).intValue();

                table.put(portNum, new Integer(valueInt));
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueInt);
        }
        return table;
    }

    private Map<Short, Integer> readLLDPLocalPortIDTypeEntries(SNMPv1CommunicationInterface comInterface){
        //logger.trace("enter readLLDPRemotePortIDTypeEntries()...");

        Map<Short, Integer> table =  new HashMap<Short, Integer>();
        SNMPVarBindList tableVars;

        try{
            //logger.debug("to retieve oid " + lldpLocalPortIdOID + "'s values...");

            tableVars = comInterface.retrieveMIBTable(lldpLocalPortIdTypeOID);
        }
        catch(Exception e1)
        {
            logger.debug("ERROR: readLLDPLocalPortIDTypeEntries(), Exception during SNMP retrieveMIBTable() for node {}: {}", comInterface.getHostAddress(), e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        /*//TODO: snmp connection should be created and closed here inpendently? (but need fix that comInterface had been closed before entering here, and here we don't know the switchIP for create snmp)
        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readLLDPLocalPortIDTypeEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }*/

        //logger.debug("Number of table entries: " + tableVars.size());
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                Short portNum = retrievePortNumFromPortOID(snmpOIDstr);//TODO: suggest to change a function name?  

                int valueInt = ((BigInteger)value.getValue()).intValue();

                table.put(portNum, new Integer(valueInt));
                //logger.debug("Retrieved OID: " + snmpOID + ", value: " + valueInt);
        }
        return table;
    }

    //abandom this method, because addVLAN() must be given vlanName also
    /*public Status addVLAN(Node node, Integer vlanID){
        logger.debug("enter SNMPHandler.addVLAN()...");
        return addVLAN(node, vlanID, "v" + vlanID);
    }*/

    public Map<Short, Integer>  readPortState(Long sw_macAddr){//return <portNumber, remoteChassisID>
        logger.trace("enter SNMPHandler.readPortState()");

        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: readPortState() for node, mac addr: {}, can't find the IP address of the node in DB", sw_macAddr);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: readPortState() for node, mac addr: {}, can't find the SNMP community of the node in DB", sw_macAddr);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: readPortState() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR:readPortState() for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: readPortState(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. now can set fwd table entry
        //logger.debug("going to read LLDP remote chassis IDs...");
        return readPortStateEntries(comInterface);
    }

    private Map<Short, Integer> readPortStateEntries(SNMPv1CommunicationInterface comInterface){
        logger.trace("enter readPortStateEntries()...");
        Map<Short, Integer> table =  new HashMap<Short, Integer>();
        SNMPVarBindList tableVars;

        try{
            //logger.debug("to retieve oid " + portStateOID + "'s values...");

            tableVars = comInterface.retrieveMIBTable(portStateOID);
        }
        catch(Exception e1)
        {
            logger.debug("ERROR: readPortStateEntries(), Exception during SNMP retrieveMIBTable() for node {}: {}", comInterface.getHostAddress(), e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: readPortStateEntries(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }

        //logger.debug("Number of table entries: " + tableVars.size());
        for(int i = 0; i < tableVars.size(); i++){
            SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
            SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            SNMPInteger value = (SNMPInteger)pair.getSNMPObjectAt(1);

            String snmpOIDstr = snmpOID.toString();
            Short portNum = retrievePortNumFromChassisOIDAtEnd(snmpOIDstr);

            int valueInt = ((BigInteger)value.getValue()).intValue();

            table.put(portNum, new Integer(valueInt));
            //logger.debug("readPortStateEntries: Retrieved OID: " + snmpOID + ", value: " + valueInt);
        }
        return table;
    }

    public Status addVLAN(long nodeID, int vlanID, String vlanName){
        //TODO: suggest to modify function name as "setXXX()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: addVLAN(), given nodeID {} and vlanID {}, nodeID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(), given invalid nodeID " + nodeID);
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLAN(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        if(vlanName == null || vlanName == ""){
            logger.debug("ERROR: addVLAN(), given nodeID {} and vlanID {}, vlanName is invalid as null or empty string", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "gIn addVLAN(), given nodeID " + nodeID + ", vlanID " + vlanID + ", vlanName is invalid as null or empty string");
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: addVLAN() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: addVLAN() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: addVLAN() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: addVLAN() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", convert InetAddress fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: addVLAN(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //2. now can add vlan
        // logger.debug("going to add vlan...");
        boolean isSuccess = addVLANtoSwitch(comInterface, vlanID, vlanName);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to add VLAN (node: " + switchIP + "vlanID: " + vlanID + "vlanName: " + vlanName + ")", "Vendor Extension Internal Error, or the VLAN already existed");
            logger.debug("ERROR: setVLANPorts(): " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    private boolean addVLANtoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, String vlanName){
        //TODO: suggest to modify function name as "setXXX()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        logger.trace("enter SNMPHandler.addVLANtoSwitch(), vlanID " + vlanID + " and vlanName " + vlanName);
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
            logger.debug("ERROR: addVLANtoSwitch(): convert String vlanName(" + vlanName + ") to byte[] fail");
            return false;
        }
       
        String[] oids = {   vlanNameOID + vlanIDStr,
                                    //vlanEgressPortsOID + vlanIDStr,
                                    //vlanForbiddenEgressPortsOID + vlanIDStr,
                                    //vlanUntaggedPortsOID + vlanIDStr,
                                    vlanRowStatusOID + vlanIDStr
                                    };
        SNMPObject [] values = {
                                    new SNMPOctetString(vlanNameBytes), 
                                    //new SNMPOctetString(new byte[42]), 
                                    //new SNMPOctetString(new byte[42]), 
                                    //new SNMPOctetString(new byte[42]), 
                                    new SNMPInteger(4)
                                    };

        SNMPVarBindList newVars;
        try{
            newVars = comInterface.setMIBEntry(oids, values);
        }catch(Exception e){
            logger.debug("ERROR: addVLANtoSwitch() get exception when calling SNMP setMIBEntry():"  + "\n" + 
                                "node: {}, vlanID: {}, vlanName: {}):"  + "\n" + 
                                "Exception: {}" + "\n" + 
                                "(Maybe because the VLAN ID already exists)", 
                                comInterface.getHostAddress(), vlanID, vlanName, e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return false;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: addVLANtoSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        logger.trace("SNMPHandler.addVLANtoSwitch(node: {}, vlanID: {}, vlanName: {}):", comInterface.getHostAddress(), vlanID, vlanName);
        logger.trace("\n[Send to switch]:");
        for(int i = 0; i < oids.length; i++){
            logger.trace("\n  OID: {}\n    value = {}", oids[i], values[i]);
        }
        logger.trace("\n[Switch response]:\n  " + newVars.toString());
        
        return true;
    }

    //TODO: suggest to modify function name, because this function works as setting tagged port (i.e. the input parameter "int portList[]" is tagged port list)
    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    public Status setVLANPorts (long nodeID, int vlanID, int portList[]){
        logger.trace("enter SNMPHandler.setVLANPorts(nodeId:" + nodeID + ",vlanID:" + vlanID + ",portList[])...");
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: setVLANPorts(), given nodeID {}, is invalid", nodeID);
            return new Status(StatusCode.NOTALLOWED, "In setVLANPorts(), given nodeID " + nodeID +", is invalid");
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: setVLANPorts(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In setVLANPorts(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: setVLANPorts() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In setVLANPorts() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: setVLANPorts() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In setVLANPorts() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: setVLANPorts() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPorts() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setVLANPorts() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPorts() for node " + switchIP +", convert InetAddress fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setVLANPorts(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPorts(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //2. now can add vlan
        // logger.debug("going to set vlan ports...");
        byte[] nodeConnsBytes = convertPortListToBytes(portList);
        boolean isSuccess = setVLANPortstoSwitch(comInterface, vlanID, nodeConnsBytes);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to set vlan ports (node: " + switchIP + ", vlanID: " + vlanID + ")", "Vendor Extension Internal Error, or the VLAN is not yet created");
            logger.debug("ERROR: setVLANPorts(): " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    //TODO: suggest to modify function name, because this function works as setting tagged port (i.e. the input parameter "byte[] nodeConnsBytes" is tagged port list)
    private boolean setVLANPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, byte[] nodeConnsBytes){
        logger.trace("enter SNMPHandler.setVLANPortstoSwitch(node: " + comInterface.getHostAddress() + ", vlanID:" + vlanID +",..)");

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: setVLANPortstoSwitch(), given node {} and vlanID {}, vlanID is invalid", comInterface.getHostAddress(), vlanID);
            return false;
        }

        String oid = vlanEgressPortsOID + "." + vlanID;
        int valueBytesNum = NUMBER_OF_PORT / 8;
        byte[] value = new byte[valueBytesNum];
        System.arraycopy(nodeConnsBytes, 0, value, 0, valueBytesNum);
        SNMPOctetString octetValue =  new SNMPOctetString(value);
        SNMPVarBindList newVar;
        try{
            newVar = comInterface.setMIBEntry(oid, octetValue);
        }
        catch(Exception e){
            logger.debug("ERROR: setVLANPortstoSwitch() get exception when calling SNMP to setMIBEntry()" + "\n" + 
                                "node: {}, vlanID: {}, ports: {}" + "\n" + 
                                "Exception: {}",
                                comInterface.getHostAddress(), vlanID, new SNMPOctetString(nodeConnsBytes).toHexString(), e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return false;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: setVLANPortstoSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        logger.trace("SNMPHandler.setVLANPortstoSwitch(node {}, vlanID: {}):", comInterface.getHostAddress(), vlanID);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, octetValue.toHexString());
        logger.trace("\n[Switch response]:\n  " + newVar.toString());
        return true;
    }

    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    public Status setVLANPorts (long nodeID, int vlanID, int taggedPortList[], int untaggedPortList[]){
        logger.trace("enter SNMPHandler.setVLANPorts(nodeId: {}, vlanID: {}, taggedPortList[]: {}, untaggedPortList[]: {}", nodeID, vlanID, Arrays.toString(taggedPortList), Arrays.toString(untaggedPortList));

        int portList[] = new int[NUMBER_OF_PORT];
        for(int i = 0; i < NUMBER_OF_PORT; i++)
            portList[i] = taggedPortList[i] | untaggedPortList[i];

        return setVLANPortsAndUntaggedPorts(nodeID, vlanID, portList, untaggedPortList);
    }

    //reuse and modify the code of setVLANPorts((long nodeID, int vlanID, int portList[]), mainly just add code of untaggedPort
    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    //TODO: suggest to change function as "setVLANPortsWitchUntaggedPorts" (so as to be consistent with addVLANandSetPortsWithUntaggedPorts)
    private Status setVLANPortsAndUntaggedPorts (long nodeID, int vlanID, int portList[], int untaggedPortList[]){
        logger.trace("enter SNMPHandler.setVLANPortsAndUntaggedPorts(nodeId: {}, vlanID: {}, portList[]: {}, untaggedPortList[]: {}", nodeID, vlanID, Arrays.toString(portList), Arrays.toString(untaggedPortList));
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts(), given nodeID {}, is invalid", nodeID);
            return new Status(StatusCode.NOTALLOWED, "In setVLANPortsAndUntaggedPorts(), given nodeID " + nodeID +", is invalid");
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In setVLANPortsAndUntaggedPorts(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In setVLANPortsAndUntaggedPorts() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In setVLANPortsAndUntaggedPorts() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPortsAndUntaggedPorts() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPortsAndUntaggedPorts() for node " + switchIP +", convert InetAddress fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In setVLANPortsAndUntaggedPorts(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //2. now can add vlan
        // logger.debug("going to set vlan ports and untagged ports...");
        byte[] nodeConnsBytes = convertPortListToBytes(portList);
        byte[] nodeConnsBytesU = convertPortListToBytes(untaggedPortList);
        boolean isSuccess = setVLANPortstoSwitch(comInterface, vlanID, nodeConnsBytes, nodeConnsBytesU);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to set vlan ports (node: " + switchIP + ", vlanID: " + vlanID + ", ports: " + Arrays.toString(portList) + ", untaggedPosts: " + Arrays.toString(untaggedPortList) + ")", "Vendor Extension Internal Error, or the VLAN is not yet created");
            logger.debug("ERROR: setVLANPortsAndUntaggedPorts(): " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    //reuse and modify the code of setVLANPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, byte[] nodeConnsBytes), mainly just add code of untaggedPort
    private boolean setVLANPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, byte[] nodeConnsBytes, byte[] untaggedNodeConnsBytes){
        logger.trace("enter SNMPHandler.setVLANPortstoSwitch(node: " + comInterface.getHostAddress() + ", vlanID:" + vlanID +",..)");

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: setVLANPortstoSwitch(), given node {} and vlanID {}, vlanID is invalid", comInterface.getHostAddress(), vlanID);
            return false;
        }

        String oid = vlanEgressPortsOID + "." + vlanID;
        String oidU = vlanUntaggedPortsOID+ "." + vlanID;
        int valueBytesNum = NUMBER_OF_PORT / 8;
        byte[] value = new byte[valueBytesNum];
        byte[] valueU = new byte[valueBytesNum];
        System.arraycopy(nodeConnsBytes, 0, value, 0, valueBytesNum);
        System.arraycopy(untaggedNodeConnsBytes, 0, valueU, 0, valueBytesNum);
        SNMPOctetString octetValue =  new SNMPOctetString(value);
        SNMPOctetString octetValueU =  new SNMPOctetString(valueU);

        String[] oids = {oid, oidU};
        SNMPOctetString [] values = {octetValue, octetValueU};
        SNMPVarBindList newVars;

        try{
            newVars = comInterface.setMIBEntry(oids, values);
        }
        catch(Exception e){
            logger.debug("ERROR: setVLANPortstoSwitch() get exception when calling SNMP to setMIBEntry()" + "\n" + 
                                "node: {}, vlanID: {}, ports: {}, untagged ports" + "\n" + 
                                "Exception: {}",
                                comInterface.getHostAddress(), vlanID, new SNMPOctetString(nodeConnsBytes).toHexString(), new SNMPOctetString(untaggedNodeConnsBytes).toHexString(), e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return false;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: setVLANPortstoSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        logger.trace("SNMPHandler.setVLANPortstoSwitch(node {}, vlanID: {}):", comInterface.getHostAddress(), vlanID);
        logger.trace("\n[Send to switch]:");
        for(int i = 0; i < oids.length; i++){
            logger.trace("\n  OID: {}\n    value = {}", oids[i], values[i]);
        }
        logger.trace("\n[Switch response]:\n  " + newVars.toString());
        return true;
    }

    //TODO: suggest to modify function name, because this function works as setting tagged port (i.e. the input parameter "int portList[]" is tagged port list)
    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    public Status addVLANandSetPorts(long nodeID, String vlanName, int vlanID, int portList[]){
        //TODO: suggest to modify function name as "setVLANandSetPorts()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, nodeID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(), given invalid nodeID " + nodeID);
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        if(vlanName == null || vlanName == ""){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, vlanName is invalid as null or empty string", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "gIn addVLAN(), given nodeID " + nodeID + ", vlanID " + vlanID + ", vlanName is invalid as null or empty string");
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: addVLANandSetPorts() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: addVLANandSetPorts() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: addVLANandSetPorts() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: addVLANandSetPorts() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", convert InetAddress fails");
        }

        String portListStr = "";
        for(int i = 0; i < portList.length; i++)
                portListStr += portList[i] + ",";
        byte[] nodeConnsBytes = convertPortListToBytes(portList);
        if(nodeConnsBytes == null){
            logger.debug("ERROR: addVLANandSetPorts(), for node {}" + ", convert port list {} to byte[] fail", switchIP, portListStr);
            return new Status(StatusCode.INTERNALERROR, "In addVLANandSetPorts(), for node " + switchIP + ", convert port list " + portListStr + " to byte[] fail");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: addVLANandSetPorts(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In addVLANandSetPorts(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //2. now can add vlan
        // logger.debug("going to add vlan...");
        boolean isSuccess = addVLANandSetPortstoSwitch(comInterface, vlanID, vlanName, nodeConnsBytes);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to addVLANandSetPorts (node: " + switchIP + ", vlanID: " + vlanID + ", vlanName: " + vlanName + ", port list: " + portListStr + ")", "Vendor Extension Internal Error, or the VLAN already existed, or VLAN name already existed, or invalid VLAN ports");
            logger.debug("ERROR: addVLANandSetPorts() fail: " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    //TODO: suggest to modify function name, because this function works as setting tagged port (i.e. the input parameter "byte[] nodeConnsBytes" is tagged port list)
    //TODO: Acction ECS4610-52T code not yet added
    private boolean addVLANandSetPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, String vlanName, byte[] nodeConnsBytes){
        //TODO: suggest to modify function name as "setVLANandSetPortstoSwitch()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        //logger.debug("enter SNMPHandler.addVLANandSetPortstoSwitch(), vlanID " + vlanID + " and vlanName " + vlanName);
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
            logger.debug("ERROR: addVLANandSetPortstoSwitch(): convert String vlanName(" + vlanName + ") to byte[] fail" + "\n" + 
                                "node: {}, vlanID: {}, vlanName: {}, ports: {}",
                                comInterface.getHostAddress(), vlanID, vlanName, new SNMPOctetString(nodeConnsBytes).toHexString()
                                );
            return false;
        }

        int valueBytesNum = NUMBER_OF_PORT / 8;
        byte[] value = new byte[valueBytesNum];
        System.arraycopy(nodeConnsBytes, 0, value, 0, valueBytesNum);

        String[] oids = {   vlanNameOID + vlanIDStr,
                                    vlanEgressPortsOID + vlanIDStr,
                                    //vlanForbiddenEgressPortsOID + vlanIDStr,
                                    //vlanUntaggedPortsOID + vlanIDStr,
                                    vlanRowStatusOID + vlanIDStr
                                    };
        SNMPObject [] values = {
                                    new SNMPOctetString(vlanNameBytes), 
                                    new SNMPOctetString(value), 
                                    //new SNMPOctetString(new byte[42]), 
                                    //new SNMPOctetString(new byte[42]), 
                                    new SNMPInteger(4)
                                    };

        SNMPVarBindList newVars;
        try{
            newVars = comInterface.setMIBEntry(oids, values);
        }catch(Exception e){
            logger.debug("ERROR: addVLANandSetPortstoSwitch() get exception when calling SNMP setMIBEntry():" + "\n" + 
                                "node: {}, vlanID: {}, vlanName: {}, ports: {}" + "\n" + 
                                "Exception: {}" + "\n" + 
                                "(Maybe because the VLAN ID already exists, or VLAN name already exists, or invalid ports)", 
                                comInterface.getHostAddress(), vlanID, vlanName, new SNMPOctetString(nodeConnsBytes).toHexString(), 
                                e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return false;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: addVLANandSetPortstoSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        logger.trace("SNMPHandler.addVLANandSetPortstoSwitch(node: {}, vlanID: {}, vlanName: {}, ports:(see below)):", comInterface.getHostAddress(), vlanID, vlanName);
        logger.trace("\n[Send to switch]:");
        for(int i = 0; i < oids.length; i++){
            if(i == 1)//the egress port
                logger.trace("\n  OID: {}\n    value = {}", oids[i], ((SNMPOctetString)values[i]).toHexString());
            else
                logger.trace("\n  OID: {}\n    value = {}", oids[i], values[i]);
        }
        logger.trace("\n[Switch response]:\n  " + newVars.toString());//TODO: for value of bytes[], make it SNMPOctetString then can print it
        
        return true;
    }

    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    public Status addVLANandSetPorts(long nodeID, String vlanName, int vlanID, int taggedPortList[], int untaggedPortList[]){
        logger.trace("enter SNMPHandler.addVLANandSetPorts(nodeId: {}, vlanID: {}, taggedPortList[]: {}, untaggedPortList[]: {}", nodeID, vlanID, Arrays.toString(taggedPortList), Arrays.toString(untaggedPortList));

        int portList[] = new int[NUMBER_OF_PORT];
        for(int i = 0; i < NUMBER_OF_PORT; i++)
            portList[i] = taggedPortList[i] | untaggedPortList[i];

        return addVLANandSetPortsWithUntaggedPorts(nodeID, vlanName, vlanID, portList, untaggedPortList);
    }

    //reuse and modify the code of addVLANandSetPorts(long nodeID, String vlanName, int vlanID, int portList[]), mainly just add code for untaggedPort
    //NOTE: the input parameter of port list is length of port number, e.g. {0,0,1,0,1,1,0,...} every bit represents a port
    public Status addVLANandSetPortsWithUntaggedPorts(long nodeID, String vlanName, int vlanID, int portList[], int untaggedPortList[]){
        //TODO: suggest to modify function name as "setVLANandSetPorts()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, nodeID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(), given invalid nodeID " + nodeID);
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In addVLAN(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        if(vlanName == null || vlanName == ""){
            logger.debug("ERROR: addVLANandSetPorts(), given nodeID {} and vlanID {}, vlanName is invalid as null or empty string", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "gIn addVLAN(), given nodeID " + nodeID + ", vlanID " + vlanID + ", vlanName is invalid as null or empty string");
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: addVLANandSetPorts() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: addVLANandSetPorts() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In addVLAN() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: addVLANandSetPorts() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: addVLANandSetPorts() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In addVLAN() for node " + switchIP +", convert InetAddress fails");
        }

        //for vlan ports
        String portListStr = "";
        for(int i = 0; i < portList.length; i++)
                portListStr += portList[i] + ",";
        byte[] nodeConnsBytes = convertPortListToBytes(portList);
        if(nodeConnsBytes == null){
            logger.debug("ERROR: addVLANandSetPorts(), for node {}" + ", convert port list {} to byte[] fail", switchIP, portListStr);
            return new Status(StatusCode.INTERNALERROR, "In addVLANandSetPorts(), for node " + switchIP + ", convert port list " + portListStr + " to byte[] fail");
        }

        //for untagged ports
        String untaggedPortListStr = "";
        for(int i = 0; i < untaggedPortList.length; i++)
                untaggedPortListStr += untaggedPortList[i] + ",";
        byte[] untaggedNodeConnsBytes = convertPortListToBytes(untaggedPortList);
        if(untaggedNodeConnsBytes == null){
            logger.debug("ERROR: addVLANandSetPorts(), for node {}" + ", convert untagged port list {} to byte[] fail", switchIP, untaggedPortListStr);
            return new Status(StatusCode.INTERNALERROR, "In addVLANandSetPorts(), for node " + switchIP + ", convert untagged port list " + untaggedPortListStr + " to byte[] fail");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: addVLANandSetPorts(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In addVLANandSetPorts(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }

        //2. now can add vlan
        // logger.debug("going to add vlan...");
        boolean isSuccess = addVLANandSetPortstoSwitch(comInterface, vlanID, vlanName, nodeConnsBytes, untaggedNodeConnsBytes);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to addVLANandSetPorts (node: " + switchIP + ", vlanID: " + vlanID + ", vlanName: " + vlanName + ", port list: " + portListStr + ", untagged port list: " + untaggedPortListStr + ")", "Vendor Extension Internal Error, or the VLAN already existed, or VLAN name already existed, or invalid VLAN ports");
            logger.debug("ERROR: addVLANandSetPorts() fail: " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    //reuse and modify the code of addVLANandSetPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, String vlanName, byte[] nodeConnsBytes), just mainly add code for untaggedPort
    private boolean addVLANandSetPortstoSwitch(SNMPv1CommunicationInterface comInterface, int vlanID, String vlanName, byte[] nodeConnsBytes, byte[] untaggedNodeConnsBytes){
        //TODO: suggest to modify function name as "setVLANandSetPortstoSwitch()" because its behavior is "set" (i.e. for existing vlan entry, this function still can write)
        //logger.debug("enter SNMPHandler.addVLANandSetPortstoSwitch(), vlanID " + vlanID + " and vlanName " + vlanName);
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
            logger.debug("ERROR: addVLANandSetPortstoSwitch(): convert String vlanName(" + vlanName + ") to byte[] fail" + "\n" + 
                                "node: {}, vlanID: {}, vlanName: {}, ports: {}, untagged ports: {}",
                                comInterface.getHostAddress(), vlanID, vlanName, new SNMPOctetString(nodeConnsBytes).toHexString(), new SNMPOctetString(untaggedNodeConnsBytes).toHexString()
                                );
            return false;
        }

        int valueBytesNum = NUMBER_OF_PORT / 8;
        byte[] value = new byte[valueBytesNum];
        System.arraycopy(nodeConnsBytes, 0, value, 0, valueBytesNum);
        byte[] valueUntagged = new byte[valueBytesNum];
        System.arraycopy(untaggedNodeConnsBytes, 0, valueUntagged, 0, valueBytesNum);


        //Bug fix: Accton ECS4610-52T requires two steps: activate VLAN first, then set its configuration
        //          For general switch, one step is done!
        if(cmethUtil.getModel(cmethUtil.getSID(comInterface.getHostAddress())).equals("Accton_ECS4610-52T")){
            //(1) activate VLAN
            String oid = vlanRowStatusOID + vlanIDStr;
            SNMPInteger valueInt = new SNMPInteger(4);

            SNMPVarBindList newVars;
            try{
                newVars = comInterface.setMIBEntry(oid, valueInt);
            }catch(Exception e){
                logger.debug("ERROR: addVLANandSetPortstoSwitch() get exception when calling SNMP setMIBEntry():" + "\n" + 
                                    "node: {}, vlanID: {}" + "\n" + 
                                    "Exception: {}" + "\n" + 
                                    "(Maybe because the VLAN ID already exists, or other reason)", 
                                    comInterface.getHostAddress(), vlanID,
                                    e);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return false;
            }

            //(2) set the VLAN's configuration
            String[] oids2 = {   vlanNameOID + vlanIDStr,
                                        vlanEgressPortsOID + vlanIDStr,
                                        //vlanForbiddenEgressPortsOID + vlanIDStr,
                                        vlanUntaggedPortsOID + vlanIDStr,
                                        //vlanRowStatusOID + vlanIDStr
                                        };
            SNMPObject [] values2 = {
                                        new SNMPOctetString(vlanNameBytes), 
                                        new SNMPOctetString(value), 
                                        //new SNMPOctetString(new byte[42]), 
                                        new SNMPOctetString(valueUntagged), 
                                        //new SNMPInteger(4)
                                        };
            try{
                newVars = comInterface.setMIBEntry(oids2, values2);
            }catch(Exception e){
                logger.debug("ERROR: addVLANandSetPortstoSwitch() get exception when calling SNMP setMIBEntry():" + "\n" + 
                                    "node: {}, vlanID: {}, vlanName: {}, ports: {}, untagged ports: {}" + "\n" + 
                                    "Exception: {}" + "\n" + 
                                    "(Maybe because the VLAN ID already exists, or VLAN name already exists, or invalid ports)", 
                                    comInterface.getHostAddress(), vlanID, vlanName, new SNMPOctetString(nodeConnsBytes).toHexString(), new SNMPOctetString(untaggedNodeConnsBytes).toHexString(),
                                    e);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return false;
            }

            //TODO: print sent data, as the bottom code of the following else section.
        }
        else{//for general switches: for switches other than Accton ECS4610-52T
            String[] oids = {   vlanNameOID + vlanIDStr,
                                        vlanEgressPortsOID + vlanIDStr,
                                        //vlanForbiddenEgressPortsOID + vlanIDStr,
                                        vlanUntaggedPortsOID + vlanIDStr,
                                        vlanRowStatusOID + vlanIDStr
                                        };
            SNMPObject [] values = {
                                        new SNMPOctetString(vlanNameBytes), 
                                        new SNMPOctetString(value), 
                                        //new SNMPOctetString(new byte[42]), 
                                        new SNMPOctetString(valueUntagged), 
                                        new SNMPInteger(4)
                                        };

            SNMPVarBindList newVars;
            try{
                newVars = comInterface.setMIBEntry(oids, values);
            }catch(Exception e){
                logger.debug("ERROR: addVLANandSetPortstoSwitch() get exception when calling SNMP setMIBEntry():" + "\n" + 
                                    "node: {}, vlanID: {}, vlanName: {}, ports: {}, untagged ports: {}" + "\n" + 
                                    "Exception: {}" + "\n" + 
                                    "(Maybe because the VLAN ID already exists, or VLAN name already exists, or invalid ports)", 
                                    comInterface.getHostAddress(), vlanID, vlanName, new SNMPOctetString(nodeConnsBytes).toHexString(), new SNMPOctetString(untaggedNodeConnsBytes).toHexString(),
                                    e);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return false;
            }

            logger.trace("SNMPHandler.addVLANandSetPortstoSwitch(node: {}, vlanID: {}, vlanName: {}, ports:(see below)):", comInterface.getHostAddress(), vlanID, vlanName);
            logger.trace("\n[Send to switch]:");
            for(int i = 0; i < oids.length; i++){
                if(i == 1 || i == 2)//the egress port and untagged port
                    logger.trace("\n  OID: {}\n    value = {}", oids[i], ((SNMPOctetString)values[i]).toHexString());
                else
                    logger.trace("\n  OID: {}\n    value = {}", oids[i], values[i]);
            }
            logger.trace("\n[Switch response]:\n  " + newVars.toString());//TODO: for value of bytes[], make it SNMPOctetString then can print it

        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: addVLANandSetPortstoSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        return true;
    }

    /*private byte[] convertPortListToBytes(List<NodeConnector> nodeConns){
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
        logger.debug("port list: ");
        for(int k = 0; k < 48; k++)
            System.out.print(portList[k]);
        System.out.println();
        for(int j = 0; j < 48 - 7; j += 8){
            int seg = portList[j] * 128 + portList[j + 1] * 64 + portList[j + 2] * 32 + portList[j + 3] * 16
                          + portList[j + 4] * 8 + portList[j + 5] * 4 + portList[j + 6] * 2 + portList[j + 7];
            //int seg = portList[j] << 7 + portList[j + 1] << 6 + portList[j + 2] << 5 + portList[j + 3] << 4
            //              + portList[j + 4] << 3 + portList[j + 5] << 2 + portList[j + 6] << 1 + portList[j + 7];
            logger.debug("answer[" + j + "~" + (j+7) + "]=" + seg);    
            answer[index++] = (new Integer(seg)).byteValue();
        }
        logger.debug("answer=" + HexString.toHexString(answer));
        return answer;
    }*/

    //The input is an array in which every element represents a port which is VLAN port or not, for example {1,0,0,1,1,1,0,0,...} (the length of the portList[] = the number of port on switch). 
    //Every 8 elements can be represented as a byte. In this manner, in this function, port list is converted to byte array
    private byte[] convertPortListToBytes(int portList[]){
        if(portList == null){
            logger.debug("ERROR: convertPortListToBytes(portList[]), given portList is null, can't proceed");
            return null;
        }
        String portListStr = portList2String(portList);
        byte[] answer = new byte[NUMBER_OF_PORT / 8];
        int index = 0;
        for(int i = 0; i < portList.length; i++){
            if(portList[i] < 0){//TODO: port max
                logger.debug("ERROR: convertPortListToBytes(portList[]), given portList " + portListStr + ", but portList[" + i + "] = " + portList[i] + " is invalid: < 0");
                return null;
            }
        }
        logger.trace("\nNow converting port list to bytes.\nThe port list: " + Arrays.toString(portList));
        for(int j = 0; j < NUMBER_OF_PORT - 7; j += 8){
            int seg = portList[j] * 128 + portList[j + 1] * 64 + portList[j + 2] * 32 + portList[j + 3] * 16
                          + portList[j + 4] * 8 + portList[j + 5] * 4 + portList[j + 6] * 2 + portList[j + 7];
            /*int seg = portList[j] << 7 + portList[j + 1] << 6 + portList[j + 2] << 5 + portList[j + 3] << 4
                          + portList[j + 4] << 3 + portList[j + 5] << 2 + portList[j + 6] << 1 + portList[j + 7];*/
            logger.trace("answer[" + j + "~" + (j+7) + "]=" + seg);    
            answer[index++] = (new Integer(seg)).byteValue();
        }
        logger.trace("converted to bytes[]: " + HexString.toHexString(answer));
        return answer;
    }

    private String portList2String(int portList[]){
        String str = "";
        for(int i = 0; i < portList.length; i++)
            str = str + "," + Integer.toString(portList[i]);
        return str;
    }

    public Status deleteVLAN(long nodeID, int vlanID){//return-- true:success, false:fail
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: deleteVLAN(), given nodeID {}, is invalid", nodeID);
            return new Status(StatusCode.NOTALLOWED, "In deleteVLAN(), given nodeID " + nodeID +", is invalid");
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: deleteVLAN(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return new Status(StatusCode.NOTALLOWED, "In deleteVLAN(nodeID: " + nodeID +"), given invalid vlanID " + vlanID);
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: deleteVLAN() for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In deleteVLAN() for node, nodeID " + nodeID + ", can't find the IP address of the node {} in DB");
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: deleteVLAN() for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return new Status(StatusCode.NOTFOUND, "In deleteVLAN() for node, nodeID " + nodeID + ", can't find the SNMP community of the node in DB");
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: deleteVLAN() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return new Status(StatusCode.INTERNALERROR, "In deleteVLAN() for node " + switchIP +", call InetAddress.getByName() error: " + e);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: deleteVLAN() for node {}, convert InetAddress fails", switchIP);
            return new Status(StatusCode.INTERNALERROR, "In deleteVLAN() for node " + switchIP +", convert InetAddress fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: deleteVLAN(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return new Status(StatusCode.INTERNALERROR, "In deleteVLAN(), for node " + switchIP + ", create SNMP communication interface error: " + e1);
        }
        
        //2. now can add vlan
        boolean isSuccess = deleteVLANFromSwitch(comInterface, vlanID);
        if(isSuccess){
            return new Status(StatusCode.SUCCESS, null);
        }
        else{
            String errorStr = errorString("program", "snmp to delete vlan (node: " + switchIP + ", vlanID: " + vlanID + ")", "Vendor Extension Internal Error, or the VLAN already not existed, or this VLAN is used somewhere");
            logger.debug("ERROR: deleteVLAN(): " + errorStr);
            return new Status(StatusCode.INTERNALERROR, errorStr);
        }
    }

    private boolean deleteVLANFromSwitch(SNMPv1CommunicationInterface comInterface, int vlanID){
        String vlanIDStr = "." + vlanID;
        String oid = vlanRowStatusOID + vlanIDStr;
        SNMPInteger value = new SNMPInteger(DELETE_VLAN_SNMP_VALUE);
        SNMPVarBindList newVar;

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: deleteVLANFromSwitch(): given invalid vlanID {}", vlanID);
            return false;
        }
        
        try{
            newVar = comInterface.setMIBEntry(oid, value);
        }catch(Exception e){
            logger.debug("ERROR: deleteVLANFromSwitch() get exception when calling SNMP setMIBEntry():" + "\n" + 
                                "node: {}, vlanID: {}" + "\n" + 
                                "(Maybe because this vlan already not exists, or this VLAN is used somewhere)", 
                                comInterface.getHostAddress(), vlanID, e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return false;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: deleteVLANFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return false;
        }

        logger.trace("SNMPHandler.deleteVLANFromSwitch(node: {}, vlanID: {})", comInterface.getHostAddress(), vlanID);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVar.toString());
        
        return true;
    }

    public List<NodeConnector> getVLANPorts(long nodeID, int vlanID){//return: the ports of the vlan on the switch
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: getVLANPorts(), given nodeID {}, is invalid", nodeID);
            return null;
        }

        if(!isValidVlan(vlanID)){
            logger.debug("ERROR: getVLANPorts(), given nodeID {} and vlanID {}, vlanID is invalid", nodeID, vlanID);
            return null;
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getVLANPorts() for nodeID {} and vlanID {}, can't find the IP address of the node {} in DB", nodeID, vlanID, nodeID);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getVLANPorts() for nodeID {} and vlanID {}, can't find the SNMP community of the node {} in DB", nodeID, vlanID, nodeID);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: getVLANPorts() for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getVLANPorts() for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getVLANPorts(), for node {}" + ", create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. send snmp command to get vlan ports
            SNMPVarBindList newVars;
            String requestOID = vlanEgressPortsOID + "." + vlanID;
            try{
                newVars = comInterface.getMIBEntry(requestOID);
            }catch(Exception e){
                logger.debug("ERROR: getVLANPorts() get exception when calling SNMP getMIBEntry():" + "\n" + 
                                "node: {}, vlanID: {}" + "\n" + 
                                "Exception: {}" + "\n" + 
                                "(Maybe because this vlan already not exists, or this VLAN is used somewhere)", 
                                comInterface.getHostAddress(), vlanID, e);
                //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
                return null;//meaning fail
            }

            try{
                comInterface.closeConnection();
            }
            catch(SocketException e2){
                logger.debug("ERROR: getVLANPorts(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
                return null;
            }

            SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
            //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
            if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
                logger.debug("ERROR: getVLANPorts(), get SNMPUnknownObject, meaning no such VLAN " + vlanID + " on " + switchIP);
                return null;
            }
            SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
            byte[] valueBytes = (byte[])value.getValue();
            byte[] portBytes = new byte[NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN];
            if(valueBytes.length < portBytes.length){
                logger.debug("ERROR: getVLANPorts(), length of retrieved port list = " + valueBytes.length +" is abnormal, it should be a much longer lengh than NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN " + NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN);
                return null;
            }
            System.arraycopy(valueBytes, 0, portBytes, 0, NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN);

            int ports[] = convertPortBytesToList(portBytes);
            if(ports == null)
                return null;

            String portsStr = HexString.toHexString(portBytes);
            logger.trace("\nto retieve switch (" + switchIP +"'s VLAN ports (OID: " + requestOID + "), get value: " + portsStr);

            //convert ports (ex. 1010011001000... to {1,3,6,7,10})
            int ansTmp[] = new int[NUMBER_OF_PORT_IN_SNMP_VLAN];
            int index = 0;
            for(int i = 0; i < NUMBER_OF_PORT_IN_SNMP_VLAN; i++){
                if(ports[i] !=0){
                    ansTmp[index] = i + 1;
                    index += 1;
                }
            }
            int answer[] = new int[index];
            System.arraycopy(ansTmp, 0, answer, 0, index);
            return portsToNcList(answer, nodeID);
    }

    //A byte can be convert to a list. For example, 129 to {1,0,0,0,0,0,0,1}
    //This function convert every byte in the byte[] to int[], getting a longer int[]
    private int[] convertPortBytesToList(byte portBytes[]){
        if(portBytes.length != NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN){
            logger.debug("ERROR: convertPortBytesToList(), input portBytes's length = " + portBytes.length + ", but valid length should be " + NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN);
            return null;
        }
        
        int ports[] = new int[NUMBER_OF_PORT_IN_SNMP_VLAN];
        int index = 0;
        for(int i = 0; i < portBytes.length; i++){
            int seg = portBytes[i] & 0xff;//convert a byte to an int
            for(int j = 8; j > 0; j--){
                ports[index + j - 1] = seg % 2;
                seg = seg /2;
            }
            index = index + 8;
        }

        return ports;
    }

    //private List<NodeConnector> portsToNcList(int ports[], Node node){
    private List<NodeConnector> portsToNcList(int ports[], long nodeId){
        if(nodeId < 0){
            logger.debug("ERROR: portsToNcList(), given nodeId {}, is invalid", nodeId);
            return null;
        }

        for(int i = 0; i < ports.length; i++){
            if(ports[i] < 0){
                logger.debug("ERROR: portsToNcList(), given nodeId " + nodeId +", and ports[" + i + "] = " + ports[i] + " is invalid");
                return null;
            }
        }

        List<NodeConnector> nodeConns = new ArrayList<NodeConnector>();
        for(int i = 0; i < ports.length; i++){
            //nodeConns.add(createNodeConnector(new Short((short)ports[i]), node));
            nodeConns.add(createSNMPNodeConnector((short)ports[i], nodeId));
        }
        return nodeConns;
    }

    private static Node createSNMPNode(long nodeId) {
        if(nodeId < 0){
            logger.debug("ERROR: createSNMPNode(), given nodeId {}, is invalid", nodeId);
            return null;
        }

        try {
            return new Node("SNMP", nodeId);
        } catch (ConstructionException e1) {
            logger.debug("ERROR: createSNMPNode(): SNMP Node creation fail, nodeId {}: {}", nodeId, e1);
            return null;
        }
    }

    //private static NodeConnector createNodeConnector(Short portId, Node node) {
    private static NodeConnector createSNMPNodeConnector(short portId, long nodeId) {
        if(portId < 0){
            logger.debug("ERROR: createSNMPNodeConnector(), given nodeId {} and portId {}, portId is invalid", nodeId, portId);
            return null;
        }
        if(nodeId < 0){
            logger.debug("ERROR: createSNMPNodeConnector(), given nodeId {} and portId {}, nodeId is invalid", nodeId, portId);
            return null;
        }
        //if (node.getType().equals("SNMP")) {
            try {
                Node node = createSNMPNode(nodeId);
                return new NodeConnector("SNMP", new Short(portId), node);
            } catch (Exception e1) {
                logger.debug("ERROR: createSNMPNodeConnector(): given nodeId {} and portId {}, error: {}", nodeId, portId, e1);
                return null;
            }
        //}
    }

    public VLANTable getVLANTable(long nodeID){//return: all VLANs on the switch, and also the ports of each VLAN
        VLANTable table = new VLANTable();
        List<NodeConnector> ports;

        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){
            logger.debug("ERROR: getVLANTable(): given nodeID, is invalid", nodeID);
            return null;
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getVLANTable(): for node, nodeID: {}, can't find the IP address of the node {} in DB", nodeID);
            return null;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getVLANTable(): for node, nodeID: {},  can't find the SNMP community of the node in DB", nodeID);
            return null;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e) {
            logger.debug("ERROR: getVLANTable(): for node {}, call InetAddress.getByName() error: {}", switchIP, e);
            return null;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getVLANTable(): for node {}, convert InetAddress fails", switchIP);
            return null;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getVLANTable(): for node {}, create SNMP communication interface error: {}", switchIP, e1);
            return null;
        }

        //2. get vlan table by snmp request
        SNMPVarBindList tableVars;
        try{
            tableVars = comInterface.retrieveMIBTable(vlanEgressPortsOID);
        }
        catch(Exception e){
            logger.debug("ERROR: getVLANTable(): for node {}, get exception when calling SNMP retrieveMIBTable(): ", comInterface.getHostAddress(), e);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getVLANTable(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return null;
        }

        //logger.debug("Number of table entries: " + tableVars.size());
        for(int i = 0; i < tableVars.size(); i++){
                SNMPSequence pair = (SNMPSequence)(tableVars.getSNMPObjectAt(i));
                SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
                SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);

                String snmpOIDstr = snmpOID.toString();
                String vlanIDstr = snmpOIDstr.substring(snmpOIDstr.lastIndexOf(".") + 1);
                int vlanId = Integer.parseInt(vlanIDstr);
                //if(vlanID == 1) continue;//ignore default vlan
                //ports = getVLANPorts(nodeID, vlanId);//replace by the next line, which is a more efficient correct way
                ports = snmpValueToNcList(value, nodeID);

                table.addEntry(vlanId, ports);//TODO: vlan name is ignore (fake a vlan name to fill in table)
                //logger.debug("Retrieved OID: " + snmpOID + " (so port num=" + portNum + "), value: " + valueStr);
        }
        return table;

    }

    private List<NodeConnector> snmpValueToNcList(SNMPOctetString value, long nodeID){
            byte[] valueBytes = (byte[])value.getValue();
            byte[] portBytes = new byte[NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN];
            if(valueBytes.length < portBytes.length){
                logger.debug("ERROR: snmpValueToNcList(): length of retrieved port list = " + valueBytes.length +" is abnormal, it should be a much longer lengh than NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN " + NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN);
                return null;
            }
            System.arraycopy(valueBytes, 0, portBytes, 0, NUMBER_BYTES_TO_REPRESENT_ALL_PORTS_ON_SWITCH_IN_SNMP_VLAN);

            int ports[] = convertPortBytesToList(portBytes);
            if(ports == null)
                return null;

            String portsStr = HexString.toHexString(portBytes);
            logger.trace("snmpValueToNcList(): convert SNMPOctetString {} to port list: {}", value, portsStr);

            //convert ports (ex. 1010011001000... to {1,3,6,7,10})
            int ansTmp[] = new int[NUMBER_OF_PORT_IN_SNMP_VLAN];
            int index = 0;
            for(int i = 0; i < NUMBER_OF_PORT_IN_SNMP_VLAN; i++){
                if(ports[i] !=0){
                    ansTmp[index] = i + 1;
                    index += 1;
                }
            }
            int answer[] = new int[index];
            System.arraycopy(ansTmp, 0, answer, 0, index);
            return portsToNcList(answer, nodeID);
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
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortStateToSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", port: " + portNum + ", error: " + e1 + ")");
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: setSTPPortStateToSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            new Status(StatusCode.INTERNALERROR, "SNMPHandler: setSTPPortStateToSwitch(): call SNMP comInterface.closeConnection() fails (node: " + comInterface.getHostAddress() + ", port: " + portNum + ", error: " + e2 + ")");
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
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getSTPPortStateFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
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

    public long getStpPortRoot(long nodeID, short port){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: getStpPortRoot(): invalid nodeID {}", nodeID);
            return -1;
        }

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: getStpPortRoot(): IP address of node {} is not in DB", nodeID);
            return -1;
        }

        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: getStpPortRoot(): SNMP community of node {} is not in DB", nodeID);
            return -1;
        }

        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: getStpPortRoot(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            return -1;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getStpPortRoot(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            return -1;
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: getStpPortRoot(): InetAddress.getByName() for node {} fails", switchIP);
            return -1;
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: getStpPortRoot(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            return -1;
        }
        catch (Exception e2) {
            logger.debug("ERROR: getStpPortRoot(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            return -1;
        }

        //2. now can get stp root from switch, given port
        long rootNodeId = getStpPortRootFromSwitch(comInterface, port);
        if(rootNodeId < 0){
            logger.debug("ERROR: getStpPortRoot(): call getStpPortRootFromSwitch() for node {} port {} fails", nodeID, port);
            return -1;
        }
        return rootNodeId;
    }

    private long getStpPortRootFromSwitch(SNMPv1CommunicationInterface comInterface, short port ){
        String oid = stpPortDesignatedRootOID + "." + port;
        SNMPVarBindList newVars;

        try{
            newVars = comInterface.getMIBEntry(oid);
        }catch(Exception e1){
            logger.debug("ERROR: getStpPortRootFromSwitch(): call SNMP getMIBEntry() fails, given node {} port {}: {}",
                                comInterface.getHostAddress(), port, e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return -1;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getStpPortRootFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return -1;
        }

        if(newVars == null){
            logger.debug("ERROR: getStpPortRootFromSwitch(): null in switch response content, given node {} port {}: {}",
                                comInterface.getHostAddress(), port);
            return -1;
        }
        if(newVars.getSNMPObjectAt(0) == null){
            logger.debug("ERROR: getStpPortRootFromSwitch(): null in switch response content, given node {} port {}: {}",
                                comInterface.getHostAddress(), port);
            return -1;
        }
        SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
        if(pair.getSNMPObjectAt(1) == null){
            logger.debug("ERROR: getStpPortRootFromSwitch(): null in switch response content, given node {} port {}: {}",
                                comInterface.getHostAddress(), port);
            return -1;
        }
        //SNMPObjectIdentifier snmpOID = (SNMPObjectIdentifier)pair.getSNMPObjectAt(0);
        if(pair.getSNMPObjectAt(1).getClass() == SNMPUnknownObject.class){
            logger.debug("ERROR: getStpPortRootFromSwitch(): requests stp root for node {} port {} fails",
                                comInterface.getHostAddress(), port);
            return -1;
        }
        SNMPOctetString value = (SNMPOctetString)pair.getSNMPObjectAt(1);
        byte[] valueBytes = (byte[])value.getValue();

        logger.trace("SNMPHandler.getStpPortRootFromSwitch(node: {}, port: {})", comInterface.getHostAddress(), port);
        logger.trace("\n[Send to switch]:\n  OID: {}\n    value = {}", oid, value.toString());
        logger.trace("\n[Switch response]:\n  " + newVars.toString());

        byte[] value6Bytes = new byte[NUMBER_OF_BYTES_FOR_IPV4_MAC_ADDRESS];
        System.arraycopy(valueBytes, 2, value6Bytes, 0, NUMBER_OF_BYTES_FOR_IPV4_MAC_ADDRESS);
        String macAddrStr = HexString.toHexString(value6Bytes);
        long rootNodeId = HexString.toLong(macAddrStr);

        return rootNodeId;
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
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getARPEntryFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
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
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntryFromSwitch(): call SNMP setMIBEntry() fails (node: " + comInterface.getHostAddress() + ", arp_ip_address: " + ipAddress + ", error: " + e1 + ")");
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: deleteARPTableEntryFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: deleteARPTableEntryFromSwitch(): call SNMP comInterface.closeConnection() fails (node: " + comInterface.getHostAddress() + ", arp_ip_address: " + ipAddress + ", error: " + e2 + ")");
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

    public Status/*SNMP4SDNErrorCode*/ setARPEntry(long nodeID, String ipAddress, long macID){
        Long sw_macAddr = new Long(nodeID);
        String switchIP;
        String community;
        InetAddress sw_ipAddr;
        SNMPv1CommunicationInterface comInterface;

        if(nodeID < 0){//check nodeID valid?
            logger.debug("ERROR: setARPEntry(): invalid nodeID {}", nodeID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): invalid nodeID " + nodeID);
        }
        try{//check ipAddress valid?
            InetAddress addr = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setARPEntry(): address translation for node {} to arp_entry_ip {} error: {}",
                                        nodeID, ipAddress, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): address translation for node " + nodeID + " to arp_entry_ip " + ipAddress + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPEntry(): address translation for node {} to arp entry ip {} error: {}",
                                        nodeID, ipAddress, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): address translation for node " + nodeID + " to arp_entry_ip " + ipAddress + " error: " + e2);
        }
        if(macID < 0){//check macId valid?
            logger.debug("ERROR: setARPEntry(): invalid macID {}", macID);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): invalid macID " + macID);
        }

        //Prepare required parameters for establishing SNMP communication, as follows

        switchIP = cmethUtil.getIpAddr(sw_macAddr);
        if(switchIP == null){
            logger.debug("ERROR: setARPEntry(): IP address of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_EXIST;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): IP address of node " + nodeID + " is not in DB");
        }
        community = cmethUtil.getSnmpCommunity(sw_macAddr);
        if(community == null){
            logger.debug("ERROR: setARPEntry(): SNMP community of node {} is not in DB", nodeID);
            //return SNMP4SDNErrorCode.NOT_READY;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): SNMP community of node " + nodeID + " is not in DB");
        }
        try{
            sw_ipAddr = InetAddress.getByName(switchIP);
        }
        catch (UnknownHostException e1) {
            logger.debug("ERROR: setARPEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): InetAddress.getByName() for node " + switchIP + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPEntry(): InetAddress.getByName() for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): InetAddress.getByName() for node " + switchIP + " error: " + e2);
        }
        if(sw_ipAddr == null){
            logger.debug("ERROR: setARPEntry(): InetAddress.getByName() for node {} fails", switchIP);
            //return SNMP4SDNErrorCode.INVALID_PARAM;
            return new Status(StatusCode.BADREQUEST, "SNMPHandler: setARPEntry(): InetAddress.getByName() for node " + switchIP + " fails");
        }

        //1. open snmp communication interface
        try{
            comInterface = new SNMPv1CommunicationInterface(1, sw_ipAddr, community);
            //logger.debug("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);
        }
        catch (SocketException e1) {
            logger.debug("ERROR: setARPEntry(): create SNMP communication interface for node {} error: {}", switchIP, e1);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntry(): create SNMP communication interface for node " + switchIP + " error: " + e1);
        }
        catch (Exception e2) {
            logger.debug("ERROR: setARPEntry(): create SNMP communication interface for node {} error: {}", switchIP, e2);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntry(): create SNMP communication interface for node " + switchIP + " error: " + e2);
        }

        //2. now can get arp entry from switch
        //SNMP4SDNErrorCode ret = setARPEntryOnSwitch(comInterface, ipAddress, macID);
        Status ret = setARPEntryOnSwitch(comInterface, ipAddress, macID);
        //if(ret != SNMP4SDNErrorCode.SUCCESS){
        if(!ret.isSuccess()){
            logger.debug("ERROR: setARPEntry(): call setARPEntryFromSwitch() for node {} fails", nodeID, ipAddress);
            return new Status(ret.getCode(), "SNMPHandler: setARPEntry(): call setARPEntryFromSwitch() for node " + nodeID + " arp entry ip " + ipAddress + " fails: " + ret.getDescription());
        }

        //return ret;
        return new Status(StatusCode.SUCCESS);
    }

    private Status/*SNMP4SDNErrorCode*/ setARPEntryOnSwitch(SNMPv1CommunicationInterface comInterface, String ipAddress, long macID){
        String phyAddrOid = arpTableEntryPhyAddrOID + "." + midStuffForArpTableEntryOID + "." + ipAddress;
        String typeOid = arpTableEntryTypeOID + "." + midStuffForArpTableEntryOID + "." + ipAddress;
        String[] oids = {phyAddrOid, typeOid};

        //TODO: the following can be replace with two line 'String macAddrStr = longTo6SegHexString(macID);byte[] macAddrBytes = new HexString().fromHexString(macAddrStr);'
        String macAddrStr = new HexString().toHexString(macID);//convert long to hex-string
        macAddrStr = removeZeroInBeginning(new String(macAddrStr));
        byte[] macAddrBytes = new HexString().fromHexString(macAddrStr);//conver hex-string to bytes()

        //check macAddrBytes's length, it must be 6, 6 is the length of the corresponding field in the snmp packet
        if(macAddrBytes.length > NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP){
            logger.debug("ERROR: setARPEntryOnSwitch(): invalid arp_mac_address (over ipv4's length 6): " + "\n" + 
                                        "node: {}, arp_ip_address: {}, arp_mac_address: {}", 
                                        comInterface.getHostAddress(), ipAddress, macAddrStr);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntryOnSwitch(): invalid arp_mac_address (over ipv4's length 6), with " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr);
        }
        else if(macAddrBytes.length < NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP){
            int indent = NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP - macAddrBytes.length;
            byte[] tmpBytes = new byte[NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP];
            System.arraycopy(macAddrBytes, 0, tmpBytes, indent , macAddrBytes.length);
            macAddrBytes = new byte[NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP];
            System.arraycopy(tmpBytes, 0, macAddrBytes, 0 , NUMBER_OF_MAC_ADDRESS_SEGMENTS_IN_SNMP_ARP);
        }
        //end of TODO: try 'String macAddrStr = longTo6SegHexString(macID);byte[] macAddrBytes = new HexString().fromHexString(macAddrStr);'

        SNMPOctetString macOct =  new SNMPOctetString(macAddrBytes);
        SNMPInteger typeInt = new SNMPInteger(arpTableEntryType_static);
        SNMPObject [] values = {macOct, typeInt};
        SNMPVarBindList newVars;

        try{//note: here not use rain's suggest "copy the variables above and paste", becase they will be used after the 'try..catch'
            newVars = comInterface.setMIBEntry(oids, values);
        }catch(Exception e1){
            logger.debug("ERROR: setARPEntryOnSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}, arp_mac_address: {}, error: {}", 
                                        comInterface.getHostAddress(), ipAddress, macAddrStr, e1);
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntryOnSwitch(): call SNMP setMIBEntry() fails, with node " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr + ", error: " + e1);
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: setARPEntryOnSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntryOnSwitch(): call SNMP comInterface.closeConnection() fails, with node " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr + ", error: " + e2);
        }

        if(newVars == null){
            logger.debug("ERROR: setARPEntryOnSwitch(): call SNMP setMIBEntry() fails: " + "\n" + 
                                        "node: {}, arp_ip_address: {}, arp_mac_address: {}", 
                                        comInterface.getHostAddress(), ipAddress, macAddrStr);
            //return SNMP4SDNErrorCode.FAIL;
            return new Status(StatusCode.INTERNALERROR, "SNMPHandler: setARPEntryOnSwitch(): call SNMP setMIBEntry() fails, with node " + comInterface.getHostAddress() + " arp_ip_address: " + ipAddress + " arp_mac_address " + macAddrStr);
        }

        logger.trace("SNMPHandler: setARPEntryOnSwitch(): parameters -- node: {}, arp_ip_address: {}, arp_mac_address: {}", comInterface.getHostAddress(), ipAddress, macAddrStr);
        logger.trace("\n[Send to switch]:");
        for(int i = 0; i < oids.length; i++){
            logger.trace("\n  OID: {}\n    value = {}", oids[i], values[i]);
        }
        logger.trace("\n[Switch response]:\n  " + newVars.toString());
        
        //return SNMP4SDNErrorCode.SUCCESS;
        return new Status(StatusCode.SUCCESS);
    }

    private String removeZeroInBeginning(String macStr){//For example, "00:00:2A:00:F1" would be "2A:00:F1"
        while(true){//TODO: more carefully checking, such as "2A:0:F1" is not examined. We skip checking here based on believing in the HexString library
            if(macStr.length() <= 2) break;
            if(macStr.substring(0, 2).compareTo("00") == 0){
                macStr = macStr.substring(3, macStr.length());
            }
            else
                break;
        }
        return macStr;
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
            //TODO: call comInterface.closeConnection()? (An innter try..catch here?)
            return null;
        }

        try{
            comInterface.closeConnection();
        }
        catch(SocketException e2){
            logger.debug("ERROR: getARPTableFromSwitch(), Exception during SNMPv1CommunicationInterface.closeConnection() for node {}: {}", comInterface.getHostAddress(), e2);
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
