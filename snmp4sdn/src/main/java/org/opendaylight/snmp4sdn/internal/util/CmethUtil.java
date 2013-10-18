/*
 * Copyright (c) 2013 Industrial Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal.util;

import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CmethUtil{
    ConcurrentMap<Long, String> table;//<Long, String> as <mac, ip>

    public CmethUtil(){
        table = new ConcurrentHashMap<Long, String>();

        /*table.put(HexString.toLong("00:00:00:00:00:01"), "10.217.0.31");
        table.put(HexString.toLong("00:00:00:00:00:02"), "10.217.0.32");
        table.put(HexString.toLong("00:00:00:00:00:03"), "10.217.0.33");
        table.put(HexString.toLong("00:00:00:00:00:04"), "10.217.0.34");
        table.put(HexString.toLong("00:00:00:00:00:05"), "10.217.0.35");*/
    }

    public String getIpAddr(Long macAddr){
        System.out.println("(CmethUtil:  " + HexString.toHexString(macAddr) + " --> " + table.get(macAddr) + ")");
        return table.get(macAddr);
    }

    public Long getSID(String switchIP){
        Long mac;
        String ip;
        for(ConcurrentMap.Entry<Long, String> entry : table.entrySet()){
            mac = entry.getKey();
            ip = entry.getValue();
            if(ip.equalsIgnoreCase(switchIP))
                return mac;
        }
        return null;
    }

    public void addEntry(Long mac, String ip){
        table.put(mac, ip);
    }

    public static String getIpAddr_old(Long macAddr){
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

