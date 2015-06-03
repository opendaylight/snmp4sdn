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

import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.opendaylight.snmp4sdn.protocol.statistics.SNMPDescriptionStatistics;


/**
 * Interface which defines the api which gets called when the information
 * contained in the OF description statistics reply message from a network
 * is updated with new one.
 */
public interface IStatisticsListener {
        public void descriptionRefreshed(Long switchId,
                                        SNMPDescriptionStatistics description);
}
