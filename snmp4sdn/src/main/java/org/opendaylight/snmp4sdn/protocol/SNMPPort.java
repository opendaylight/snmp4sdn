/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package. The authours' efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol;

public enum SNMPPort {
    /*OFPP_MAX                ((short)0xff00),
    OFPP_IN_PORT            ((short)0xfff8),
    OFPP_TABLE              ((short)0xfff9),
    OFPP_NORMAL             ((short)0xfffa),
    OFPP_FLOOD              ((short)0xfffb),
    OFPP_ALL                ((short)0xfffc),
    OFPP_CONTROLLER         ((short)0xfffd),
    OFPP_LOCAL              ((short)0xfffe),
    OFPP_NONE               ((short)0xffff);*/
    SNMPPP_MAX                ((short)0xff00),
    SNMPPP_IN_PORT            ((short)0xfff8),
    SNMPPP_TABLE              ((short)0xfff9),
    SNMPPP_NORMAL             ((short)0xfffa),
    SNMPPP_FLOOD              ((short)0xfffb),
    SNMPPP_ALL                ((short)0xfffc),
    SNMPPP_CONTROLLER         ((short)0xfffd),
    SNMPPP_LOCAL              ((short)0xfffe),
    SNMPPP_NONE               ((short)0xffff);

    protected short value;

    private SNMPPort(short value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public short getValue() {
        return value;
    }
}
