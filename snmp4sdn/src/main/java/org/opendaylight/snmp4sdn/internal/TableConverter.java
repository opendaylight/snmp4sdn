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

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeTable;
import org.opendaylight.controller.sal.utils.NodeTableCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableConverter {
    private static final Logger log = LoggerFactory
            .getLogger(TableConverter.class);

    public static NodeTable toNodeTable(byte tableId, Node node) {
        log.trace("Openflow table ID: {}", Byte.toString(tableId));
        return NodeTableCreator.createNodeTable(tableId, node);
    }

    public static byte toOFTable(NodeTable salTable) {
        log.trace("SAL Table: {}", salTable);
        return (Byte) salTable.getID();
    }
}
