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

public class CmethUtil{
    private ConcurrentMap<Long, Vector> table;//<Long, Vector> as <mac, <ip, community_readonly, community_readwrite, cli_username, cli_password>>

    public CmethUtil(){
        table = new ConcurrentHashMap<Long, Vector>();
        readDB();
        /*table.put(HexString.toLong("00:00:00:00:00:01"), "10.217.0.31");
        table.put(HexString.toLong("00:00:00:00:00:02"), "10.217.0.32");
        table.put(HexString.toLong("00:00:00:00:00:03"), "10.217.0.33");
        table.put(HexString.toLong("00:00:00:00:00:04"), "10.217.0.34");
        table.put(HexString.toLong("00:00:00:00:00:05"), "10.217.0.35");*/
    }

    private void readDB(){
        System.out.println("enter CmethUtil.readDB()");
        try{
        FileReader FileStream=new FileReader("D:\\OpenDaylight\\snmp4sdn\\snmp4sdn\\src\\test\\switch_login_db.csv");
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
            if(mac.startsWith(","))System.out.println("mac field is empty");
            //System.out.println("mac: " + mac);

            str = str.substring(str.indexOf(",") + 1).trim();
            ip = str.substring(0, str.indexOf(",")).trim();
            if(ip.startsWith(","))System.out.println("ip field is empty");
            //System.out.println("ip: " + ip);
            
            str = str.substring(str.indexOf(",") + 1).trim();
            snmp_community = str.substring(0, str.indexOf(",")).trim();
            if(snmp_community.startsWith(","))System.out.println("snmp_community field is empty");
            //System.out.println("snmp_community: " + snmp_community);
            
            str = str.substring(str.indexOf(",") + 1).trim();
            cli_username = str.substring(0, str.indexOf(",")).trim();
            if(cli_username.startsWith(","))System.out.println("cli_username field is empty");
            //System.out.println("cli_username: " + cli_username);
            
            str = str.substring(str.indexOf(",") + 1).trim();
            cli_password = new String(str);
            if(cli_password.startsWith(","))System.out.println("cli_password field is empty");
            //System.out.println("cli_password: " + cli_password);

            addEntry(HexString.toLong(mac), ip, snmp_community, cli_username, cli_password);
        }
        }catch(Exception e){
            System.out.println("CmethUtil.readDB() err: " + e);
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

    public String getIpAddr(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String ipAddr = (String)(entryVec.get(0));
        //System.out.println("(CmethUtil:  " + HexString.toHexString(macAddr) + " --> " + ipAddr + ")");
        return ipAddr;
    }

    public String getSnmpCommunity(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String community = (String)(entryVec.get(1));
        //System.out.println("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s snmp community --> " + community + ")");
        return community;
    }

    public String getCliUsername(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String username = (String)(entryVec.get(2));
        //System.out.println("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s username --> " + username + ")");
        return username;
    }

    public String getCliPassword(Long macAddr){
        Vector entryVec = table.get(macAddr);
        if(entryVec == null)
            return null;
        String password = (String)(entryVec.get(3));
        //System.out.println("(CmethUtil:  " + HexString.toHexString(macAddr) + "'s password --> " + password + ")");
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

    public static String _getIpAddr(Long macAddr){
            //look up table...
            long mac = macAddr.longValue();
            /*if(mac == HexString.toLong("70:72:CF:2A:87:41"))
                return "10.217.0.31";
            else if(mac == HexString.toLong("90:94:E4:23:13:E0"))
                return "10.217.0.32";
            else if(mac == HexString.toLong("90:94:E4:23:0B:00"))
                return "10.217.0.33";
            else if(mac == HexString.toLong("90:94:E4:23:0B:20"))
                return "10.217.0.34";
            else if(mac == HexString.toLong("90:94:E4:23:0A:E0"))
                return "10.217.0.35";
            else
                return null;*/
            if(mac == HexString.toLong("00:00:00:00:00:01"))
                return "10.217.0.31";
            else if(mac == HexString.toLong("00:00:00:00:00:02"))
                return "10.217.0.32";
            else if(mac == HexString.toLong("00:00:00:00:00:03"))
                return "10.217.0.33";
            else if(mac == HexString.toLong("00:00:00:00:00:04"))
                return "10.217.0.34";
            else if(mac == HexString.toLong("00:00:00:00:00:05"))
                return "10.217.0.35";
            else
                return null;
        }
}

