/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

/**
 * Interface that defines the methods to be implemented by vendors for configration
 */
public interface IConfigService {

    /**
     * Disable the function of Spanning Tree Protocol on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSTP(String sw_ipAddr, String username, String password);


    /**
     * Disable the function of BPDU flooding when STP is off
     *
     * @param XXX
     *            XXX
     */
    Status disableBpduFlooding(String sw_ipAddr, String username, String password);


    /**
     * Disable the function of broadcast flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableBpduFlooding(String sw_ipAddr, short port, String username, String password);


    /**
     * Disable the function of multicast flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableMulticastFlooding(String sw_ipAddr, String username, String password);


    /**
     * Disable the function of multicast flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableMulticastFlooding(String sw_ipAddr, short port, String username, String password);


    /**
     * Disable the function of unknown flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableUnknownFlooding(String sw_ipAddr, String username, String password);


    /**
     * Disable the function of unknown flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableUnknownFlooding(String sw_ipAddr, short port, String username, String password);


    /**
     * Disable the function of source mac check on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceMacCheck(String sw_ipAddr, String username, String password);


    /**
     * Disable the function of source mac check on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceMacCheck(String sw_ipAddr, short port, String username, String password);


    /**
     * Disable the function of source learning on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceLearning(String sw_ipAddr, String username, String password);
    

    /**
     * Disable the function of source learning on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceLearning(String sw_ipAddr, short port, String username, String password);
}

