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

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.NodeConnector.NodeConnectorIDType;
import org.opendaylight.controller.sal.utils.NetUtils;
import org.opendaylight.controller.sal.utils.NodeConnectorCreator;
import org.openflow.protocol.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class which provides the utilities for converting the Openflow port
 * number to the equivalent NodeConnector and vice versa
 *
 *
 *
 */
public abstract class PortConverter {
    private static final Logger log = LoggerFactory
            .getLogger(PortConverter.class);
    private static final int maxOFPhysicalPort = NetUtils
            .getUnsignedShort(OFPort.OFPP_MAX.getValue());

    /**
     * Converts the Openflow port number to the equivalent NodeConnector.
     */
    public static NodeConnector toNodeConnector(short port, Node node) {
        return NodeConnectorCreator.createNodeConnector("SNMP", port, node);
    }

    /**
     * Converts the NodeConnector to the equivalent Openflow port number
     */
    public static short toOFPort(NodeConnector salPort) {
        log.trace("SAL Port", salPort);
        if (salPort.getType().equals(NodeConnectorIDType.SWSTACK)) {
            return OFPort.OFPP_LOCAL.getValue();
        } else if (salPort.getType().equals(NodeConnectorIDType.HWPATH)) {
            return OFPort.OFPP_NORMAL.getValue();
        } else if (salPort.getType().equals(NodeConnectorIDType.CONTROLLER)) {
            return OFPort.OFPP_CONTROLLER.getValue();
        }
        return (Short) salPort.getID();
    }
}
