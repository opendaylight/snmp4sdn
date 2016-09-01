/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code base of OpenFlow plugin contributed by Cisco Systems, Inc. Their efforts are appreciated.
*/

/**
 * @file   IDataPacketListen.java
 *
 * @brief  Interface to dispatch locally in the protocol plugin the
 * data packets, intended especially for Discovery, main difference
 * here with the analogous of SAL is that this is Global
 * inherently
 *
 */
package org.opendaylight.snmp4sdn;

import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.sal.packet.PacketResult;

/**
 * Interface to dispatch locally in the protocol plugin the
 * data packets, intended especially for Discovery, main difference
 * here with the analogous of SAL is that this is Global
 * inherently.
 */
public interface IDataPacketListen {
    /**
     * Dispatch received data packet
     *
     * @param inPkt
     *            The incoming raw packet
     * @return Possible results for Data packet processing handler
     */
    public PacketResult receiveDataPacket(RawPacket inPkt);
}
