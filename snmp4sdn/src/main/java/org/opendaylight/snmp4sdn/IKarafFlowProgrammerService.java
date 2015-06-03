/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

import org.opendaylight.controller.sal.utils.Status;

/**
 * This interface defines an abstraction of the SNMP Controller that allows Karaf to call.
 *
 */
public interface IKarafFlowProgrammerService {
    //The following methods are for Flow configuration. They will be discarded when the MD-SAL is provided later.
    public Status krfAddFlow(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr);
    public Status krfDeleteFlow(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr);
}
