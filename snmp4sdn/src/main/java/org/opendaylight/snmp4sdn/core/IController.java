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

package org.opendaylight.snmp4sdn.core;

import java.util.Map;

import org.opendaylight.snmp4sdn.protocol.SNMPType;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

/**
 * This interface defines an abstraction of the SNMP Controller that allows applications to control and manage the SNMP switches.
 *
 */
public interface IController {

    /**
     * Allows application to start receiving SNMP messages received from switches.
     * @param type the type of SNMP message that applications want to receive
     * @param listener: Object that implements the IMessageListener
     */
    public void addMessageListener(SNMPType type, IMessageListener listener);

    /**
     * Allows application to stop receiving SNMP message received from switches.
     * @param type The type of SNMP message that applications want to stop receiving
     * @param listener The object that implements the IMessageListener
     */
    public void removeMessageListener(SNMPType type, IMessageListener listener);

    /**
     * Allows application to start receiving switch state change events.
     * @param listener The object that implements the ISwitchStateListener
     */
    public void addSwitchStateListener(ISwitchStateListener listener);

    /**
     * Allows application to stop receiving switch state change events.
     * @param listener The object that implements the ISwitchStateListener
     */
    public void removeSwitchStateListener(ISwitchStateListener listener);

    /**
     * Returns a map containing all the SNMP switches that are currently connected to the Controller.
     * @return Map of ISwitch
     */
    public Map<Long, ISwitch> getSwitches();

    /**
     * Returns the ISwitch of the given switchId.
     *
     * @param switchId The switch ID
     * @return ISwitch if present, null otherwise
     */
    public ISwitch getSwitch(Long switchId);

    public CmethUtil getCmethUtil();

}
