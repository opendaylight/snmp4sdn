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

import org.opendaylight.controller.sal.inventory.IPluginInInventoryService;

//the following imports are added due to the two APIs
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;

/**
 * The Interface provides inventory service to the local plugin modules
 */
public interface IInventoryProvider/* extends IPluginInInventoryService//Lithium adsal deprecated*/ {

    //the two API are copied from IPluginInInventoryService
    public ConcurrentMap<Node, Map<String, Property>> getNodeProps();

    public ConcurrentMap<NodeConnector, Map<String, Property>> getNodeConnectorProps(
            Boolean refresh);

}
