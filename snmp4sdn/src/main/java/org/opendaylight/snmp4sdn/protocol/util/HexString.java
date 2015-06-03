/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package. The authors' efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol.util;


import java.math.BigInteger;

public class HexString {
    /**
     * Convert a string of bytes to a ':' separated hex string
     * @param bytes
     * @return "0f:ca:fe:de:ad:be:ef"
     */
    public static String toHexString(byte[] bytes) {
        int i;
        String ret = "";
        String tmp;
        for(i=0; i< bytes.length; i++) {
            if(i> 0)
                ret += ":";
            tmp = Integer.toHexString(U8.f(bytes[i]));
            if (tmp.length() == 1)
                ret += "0";
            ret += tmp;
        }
        return ret;
    }

    public static String toHexString(long val) {
        char arr[] = Long.toHexString(val).toCharArray();
        String ret = "";
        // prepend the right number of leading zeros
        int i = 0;
        for (; i < (16 - arr.length); i++) {
            ret += "0";
            if ((i % 2) == 1)
                ret += ":";
        }
        for (int j = 0; j < arr.length; j++) {
            ret += arr[j];
            if ((((i + j) % 2) == 1) && (j < (arr.length - 1)))
                ret += ":";
        }
        return ret;
    }


    /**
     * Convert a string of hex values into a string of bytes
     * @param values "0f:ca:fe:de:ad:be:ef"
     * @return [15, 5 ,2, 5, 17]
     */

    public static byte[] fromHexString(String values) {
        String[] octets = values.split(":");
        byte[] ret = new byte[octets.length];
        int i;

        for(i=0;i<octets.length; i++)
            ret[i] = Integer.valueOf(octets[i], 16).byteValue();
        return ret;
    }

    public static long toLong(String values) {
        long value = new BigInteger(values.replaceAll(":", ""), 16).longValue();
        return value;
    }
}
