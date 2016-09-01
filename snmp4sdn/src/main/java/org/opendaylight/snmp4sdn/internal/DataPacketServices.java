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

package org.opendaylight.snmp4sdn.internal;

import org.opendaylight.snmp4sdn.IDataPacketMux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.controller.sal.packet.IPluginInDataPacketService;
import org.opendaylight.controller.sal.packet.RawPacket;

public class DataPacketServices implements IPluginInDataPacketService {
    protected static final Logger logger = LoggerFactory
            .getLogger(DataPacketServices.class);
    private IDataPacketMux iDataPacketMux = null;

    void setIDataPacketMux(IDataPacketMux s) {
        logger.trace("s4s:enter plugin's IPluginInDataPacketService.setIDataPacketMux() impl by DataPacketServices");//s4s
        this.iDataPacketMux = s;
    }

    void unsetIDataPacketMux(IDataPacketMux s) {
        if (this.iDataPacketMux == s) {
            this.iDataPacketMux = null;
        }
    }

    @Override
    public void transmitDataPacket(RawPacket outPkt) {
        // SNMP and commodity Ethernet switches do not support 'OFPacketOut'-like
        // function. This function will not be implemented until new usage in
        // the SNMP context is found.
        logger.trace("s4s:enter plugin's IPluginInDataPacketService.transmitDataPacket() impl by DataPacketServices");//s4s
    }
}
