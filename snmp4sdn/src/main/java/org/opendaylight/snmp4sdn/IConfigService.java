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
    Status disableSTP(Node node);


    /**
     * Disable the function of BPDU flooding when STP is off
     *
     * @param XXX
     *            XXX
     */
    Status disableBpduFlooding(Node node);


    /**
     * Disable the function of BPDU flooding when STP is off
     *
     * @param XXX
     *            XXX
     */
    Status disableBpduFlooding(Node node, NodeConnector nodeConnector);

    /**
         * Disable the function of broadcast flooding on the switch
         *
         *
         * @param XXX
         *            XXX
         */
    Status disableBroadcastFlooding(Node node);

    /**
             * Disable the function of broadcast flooding on the switch
             *
             *
             * @param XXX
             *            XXX
             */
    Status disableBroadcastFlooding(Node node, NodeConnector nodeConnector);
        

    /**
     * Disable the function of multicast flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableMulticastFlooding(Node node);


    /**
     * Disable the function of multicast flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableMulticastFlooding(Node node, NodeConnector nodeConnector);


    /**
     * Disable the function of unknown flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableUnknownFlooding(Node node);


    /**
     * Disable the function of unknown flooding on the switch
     *
     *
     * @param XXX
     *            XXX
     */
    Status disableUnknownFlooding(Node node, NodeConnector nodeConnector);


    /**
     * Disable the function of source mac check on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceMacCheck(Node node);


    /**
     * Disable the function of source mac check on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceMacCheck(Node node, NodeConnector nodeConnector);


    /**
     * Disable the function of source learning on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceLearning(Node node);


    /**
     * Disable the function of source learning on the switch
     *
     * @param XXX
     *            XXX
     */
    Status disableSourceLearning(Node node, NodeConnector nodeConnector);
}

