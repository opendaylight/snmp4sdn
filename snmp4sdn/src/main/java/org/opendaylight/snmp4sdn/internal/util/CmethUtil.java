/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal.util;

import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmethUtil{
    private static final Logger logger = LoggerFactory
            .getLogger(CmethUtil.class);

    private ConcurrentMap<Long, Vector> table;//<Long, Vector> as <mac, <ip, community_readonly, community_readwrite, cli_username, cli_password>>

    public CmethUtil(){
        table = new ConcurrentHashMap<Long, Vector>();
        readDB();
    }

    private void readDB(){
        logger.info("enter CmethUtil.readDB()");
        try{
        logger.info("open file /home/christine/snmp4sdn/snmp4sdn/src/test/switch_login_db.csv");
        FileReader FileStream=new FileReader("/home/christine/snmp4sdn/snmp4sdn/src/test/switch_login_db.csv");
        BufferedReader BufferedStream=new BufferedReader(FileStream);
        String line;

        line = BufferedStream.readLine();//skip the title raw
        while(true){
            line = BufferedStream.readLine();
            if(line == null)
                break;

            String mac, ip, snmp_community, cli_username, cli_password;

            String str = new String(line).trim();
            mac = str.substring(0, str.indexOf(",")).trim();
            if(mac.startsWith(","))logger.warn("mac field is empty");
            //logger.trace("mac: " + mac);

            str = str.substring(str.indexOf(",") + 1).trim();
            ip = str.substring(0, str.indexOf(",")).trim();
            if(ip.startsWith(","))logger.warn("ip field is empty");
            //logger.trace("ip: " + ip);

            str = str.substring(str.indexOf(",") + 1).trim();
            snmp_community = str.substring(0, str.indexOf(",")).trim();
            if(snmp_community.startsWith(","))logger.warn("snmp_community field is empty");
            //logger.trace("snmp_community: " + snmp_community);

            str = str.substring(str.indexOf(",") + 1).trim();
            cli_username = str.substring(0, str.indexOf(",")).trim();
            if(cli_username.startsWith(","))logger.warn("cli_username field is empty");
            //logger.trace("cli_username: " + cli_username);

            str = str.substring(str.indexOf(",") + 1).trim();
            cli_password = new String(str);
            if(cli_password.startsWith(","))logger.warn("cli_password field is empty");
            //logger.trace("cli_password: " + cli_password);

            addEntry(HexString.toLong(mac), ip, snmp_community, cli_username, cli_password);
        }
        }catch(Exception e){
            logger.error("CmethUtil.readDB() err: {}", e);
        }

    }

    public void addEntry(Long mac, String ip, String snmp_community, String cli_username, String cli_password){
        Vector entryVec = new Vector();
        entryVec.add(ip);
        entryVec.add(snmp_community);
        entryVec.add(cli_username);
        entryVec.add(cli_password);
        table.put(mac, entryVec);
    }

    public ConcurrentMap<Long, Vector> getEntries(){
        return new ConcurrentHashMap<Long, Vector>(table);
    }

    public String getIpAddr(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String ipAddr = (String)(entryVec.get(0));
        //logger.trace("(CmethUtil:  " + HexString.toHexString(macAddr) + " --> " + ipAddr + ")");
        return ipAddr;
    }

    public String getSnmpCommunity(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String community = (String)(entryVec.get(1));
        //logger.trace("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s snmp community --> " + community + ")");
        return community;
    }

    public String getCliUsername(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String username = (String)(entryVec.get(2));
        //logger.trace("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s username --> " + username + ")");
        return username;
    }

    public String getCliPassword(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String password = (String)(entryVec.get(3));
        //logger.trace("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s password --> " + password + ")");
        return password;
    }

    public Long getSID(String switchIP){
        Long mac;
        String ip;
        for(ConcurrentMap.Entry<Long, Vector> entry : table.entrySet()){
            ip = (String)(entry.getValue().get(0));
            if(ip.equalsIgnoreCase(switchIP)){
                mac = entry.getKey();
                return mac;
            }
        }
        return null;
    }

}

