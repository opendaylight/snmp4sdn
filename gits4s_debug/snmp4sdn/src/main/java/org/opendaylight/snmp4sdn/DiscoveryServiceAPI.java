/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

public interface DiscoveryServiceAPI {

    public boolean doTopologyDiscovery();

    public void setPeriodicTopologyDiscoveryIntervalTime(int interval);

    public void notifyCancelTopologyDiscovery();//bug fix: without the protection of link-down and Topology Discovery by mutual exclusive, we let Topology Discovery to be canceled if link-down occurs.
}
