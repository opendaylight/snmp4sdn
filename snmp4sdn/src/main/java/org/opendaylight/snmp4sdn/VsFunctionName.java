/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

//Vendor-specific function name
public enum VsFunctionName{
    addVLANandSetPorts("add_vlan_and_set_port");

    String description;
    private VsFunctionName(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return description;
    }
}

