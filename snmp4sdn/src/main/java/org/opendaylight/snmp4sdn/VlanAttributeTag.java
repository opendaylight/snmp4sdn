/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

public enum VlanAttributeTag{
    vlanCreate("vlan_create"),
    vlanName("vlan_name"),
    egressPorts("egress_ports"),
    //forbiddenEgressPorts("forbidden_egress_ports"),
    untaggedPorts("untagged_ports");
/*
    vlan_id,
    vlan_name,
    egress_ports,
    //forbidden_egress_ports,
    untagged_ports;
*/
    String description;
    private VlanAttributeTag(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

