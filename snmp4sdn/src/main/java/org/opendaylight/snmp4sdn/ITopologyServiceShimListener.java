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

import java.util.List;
import java.util.Set;

import org.opendaylight.snmp4sdn.sal.core.Edge;
import org.opendaylight.snmp4sdn.sal.core.Property;
import org.opendaylight.snmp4sdn.sal.core.UpdateType;
import org.opendaylight.snmp4sdn.sal.topology.TopoEdgeUpdate;

/**
 * The Interface provides Edge updates to the topology listeners
 */
public interface ITopologyServiceShimListener {
    /**
     * Called to update on Edge in the topology graph
     *
     * @param topoedgeupdateList
     *            List of topoedgeupdates Each topoedgeupdate includes edge, its
     *            Properties ( BandWidth and/or Latency etc) and update type.
     */
    public void edgeUpdate(List<TopoEdgeUpdate> topoedgeupdateList);

    /**
     * Called when an Edge utilization is above the safe threshold configured on
     * the controller
     *
     * @param {@link org.opendaylight.snmp4sdn.sal.core.Edge}
     */
    public void edgeOverUtilized(Edge edge);

    /**
     * Called when the Edge utilization is back to normal, below the safety
     * threshold level configured on the controller
     *
     * @param {@link org.opendaylight.snmp4sdn.sal.core.Edge}
     */
    public void edgeUtilBackToNormal(Edge edge);
}
