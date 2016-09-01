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

import org.opendaylight.controller.sal.flowprogrammer.IPluginOutFlowProgrammerService;

/**
 * Interface which defines the methods exposed by the Flow Programmer Notifier.
 * Their implementation relays the asynchronous messages received from the
 * network nodes to the the SAL Flow Programmer Notifier Service on the proper
 * container.
 */
public interface IFlowProgrammerNotifier extends
        IPluginOutFlowProgrammerService {

}
