/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
//import org.opendaylight.controller.sal.vlan.VLANTable;//ad-sal
import org.opendaylight.snmp4sdn.VLANTable;//no-sal

import java.util.List;

/**
 * Interface that defines the methods to be implemented by vendors for configration
 */
public interface IVLANService {//TODO: if with AD-SAL or MD-SAL, this interface should be defined some other where, not in snmp4sdn's own code

    /**
     * Add a VLAN to a switch
     *
     * @param XXX
     *            XXX
     */
    //public Status addVLAN(Node node, Integer vlanID);


    /**
     * Add a VLAN to a switch
     *
     * @param XXX
     *            XXX
     */
    public Status addVLAN(Node node, Integer vlanID, String vlanName);


    /**
     * Set ports to a specfic VLAN
     *
     * @param XXX
     *            XXX
     */
    public Status setVLANPorts (Node node, Integer vlanID, List<NodeConnector> nodeConns);


    public Status deleteVLAN(Node node, Integer vlanID);

    public List<NodeConnector> getVLANPorts(Node node, Integer vlanID);

    public VLANTable getVLANTable(Node node);

}

