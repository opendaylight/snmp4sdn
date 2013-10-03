/*
 * Copyright (c) 2013 Industrial Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

 package org.opendaylight.snmp4sdn.internal.util;

import org.opendaylight.snmp4sdn.protocol.util.HexString;

public class CmethUtil{
    public static String getIpAddr(Long macAddr){
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

