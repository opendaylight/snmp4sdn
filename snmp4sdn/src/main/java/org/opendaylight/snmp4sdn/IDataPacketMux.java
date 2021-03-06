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

package org.opendaylight.snmp4sdn;

/**
 * @file   IDataPacketMux.java
 *
 * @brief  Simple wrapped interface for the IPluginInDataPacketService
 * which will be only exported by DataPacketServices mux/demux
 * component and will be only accessible by the openflow protocol
 * plugin
 */

import org.opendaylight.controller.sal.packet.IPluginInDataPacketService;

/**
 * Simple wrapped interface for the IPluginInDataPacketService
 * which will be only exported by DataPacketServices mux/demux
 * component and will be only accessible by the openflow protocol
 * plugin
 */
public interface IDataPacketMux extends IPluginInDataPacketService {

}
