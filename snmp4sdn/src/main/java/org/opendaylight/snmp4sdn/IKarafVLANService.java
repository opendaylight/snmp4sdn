/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

/**
 * This interface defines an abstraction of the SNMP Controller that allows Karaf to call.
 *
 */
public interface IKarafVLANService {
    //The following methods are for VLAN configuration. They will be discarded when the VLAN Serivice's MD-SAL is provided later.
    public void addVLANSetPorts(String sw_mac, String vlanID, String vlanName, String portList);
    public void deleteVLAN(String sw_mac, String vlanID);
    public void printVLANTable(String sw_mac);
}
