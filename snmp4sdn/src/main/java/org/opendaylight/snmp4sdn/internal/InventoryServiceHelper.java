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

package org.opendaylight.snmp4sdn.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opendaylight.controller.sal.core.Bandwidth;
import org.opendaylight.controller.sal.core.AdvertisedBandwidth;
import org.opendaylight.controller.sal.core.SupportedBandwidth;
import org.opendaylight.controller.sal.core.PeerBandwidth;
import org.opendaylight.controller.sal.core.Config;
import org.opendaylight.controller.sal.core.Name;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.State;
import org.opendaylight.controller.sal.core.ConstructionException;

import org.opendaylight.controller.sal.utils.NodeCreator;

import org.opendaylight.snmp4sdn.core.ISwitch;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort.SNMPPortConfig;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort.SNMPPortFeatures;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort.SNMPPortState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class provides helper functions to retrieve inventory properties from
 * OpenFlow messages
 */
public class InventoryServiceHelper {
    /*
     * Returns BandWidth property from SNMPPhysicalPort features
     */
    public static Bandwidth SNMPPortToBandWidth(int portFeatures) {
        Bandwidth bw = null;
        int value = portFeatures
                & (SNMPPortFeatures.SNMPPPF_10MB_FD.getValue()
                        | SNMPPortFeatures.SNMPPPF_10MB_HD.getValue()
                        | SNMPPortFeatures.SNMPPPF_100MB_FD.getValue()
                        | SNMPPortFeatures.SNMPPPF_100MB_HD.getValue()
                        | SNMPPortFeatures.SNMPPPF_1GB_FD.getValue()
                        | SNMPPortFeatures.SNMPPPF_1GB_HD.getValue() | SNMPPortFeatures.SNMPPPF_10GB_FD
                        .getValue());

        switch (value) {
        case 1:
        case 2:
            bw = new Bandwidth(Bandwidth.BW10Mbps);
            break;
        case 4:
        case 8:
            bw = new Bandwidth(Bandwidth.BW100Mbps);
            break;
        case 16:
        case 32:
            bw = new Bandwidth(Bandwidth.BW1Gbps);
            break;
        case 64:
            bw = new Bandwidth(Bandwidth.BW10Gbps);
            break;
        default:
            break;
        }
        return bw;
    }

    /*
     * Returns Config property from SNMPPhysicalPort config
     */
    public static Config SNMPPortToConfig(int portConfig) {
        Config config;
        if ((SNMPPortConfig.SNMPPPC_PORT_DOWN.getValue() & portConfig) != 0)
            config = new Config(Config.ADMIN_DOWN);
        else
            config = new Config(Config.ADMIN_UP);
        return config;
    }

    /*
     * Returns State property from SNMPPhysicalPort state
     */
    public static State SNMPPortToState(int portState) {
        State state;
        if ((SNMPPortState.SNMPPPS_LINK_DOWN.getValue() & portState) != 0)
            state = new State(State.EDGE_DOWN);
        else
            state = new State(State.EDGE_UP);
        return state;
    }

    /*
     * Returns set of properties from SNMPPhysicalPort
     */
    public static Set<Property> SNMPPortToProps(SNMPPhysicalPort port) {
        Set<Property> props = new HashSet<Property>();
        Bandwidth bw = InventoryServiceHelper.SNMPPortToBandWidth(port
                .getCurrentFeatures());
        if (bw != null) {
            props.add(bw);
        }

        Bandwidth abw = InventoryServiceHelper.SNMPPortToBandWidth(port.getAdvertisedFeatures());
        if (abw != null) {
                AdvertisedBandwidth a = new AdvertisedBandwidth(abw.getValue());
                if (a != null) {
                        props.add(a);
                }
        }
        Bandwidth sbw = InventoryServiceHelper.SNMPPortToBandWidth(port.getSupportedFeatures());
        if (sbw != null) {
                SupportedBandwidth s = new SupportedBandwidth(sbw.getValue());
                if (s != null) {
                        props.add(s);
                }
        }
        Bandwidth pbw = InventoryServiceHelper.SNMPPortToBandWidth(port.getPeerFeatures());
        if (pbw != null) {
                PeerBandwidth p = new PeerBandwidth(pbw.getValue());
                if (p != null) {
                        props.add(p);
                }
        }
        props.add(new Name(port.getName()));
        props.add(InventoryServiceHelper.SNMPPortToConfig(port.getConfig()));
        props.add(InventoryServiceHelper.SNMPPortToState(port.getState()));
        return props;
    }

    /*
     * Returns set of properties for each nodeConnector in an OpenFLow switch
     */
    public static Map<NodeConnector, Set<Property>> SNMPSwitchToProps(ISwitch sw) {
        Map<NodeConnector, Set<Property>> ncProps = new HashMap<NodeConnector, Set<Property>>();

        if (sw == null) {
            return ncProps;
        }

        //Node node = NodeCreator.createSNMPNode(sw.getId());
        Node node = null;
        try{
            node = new Node("SNMP", sw.getId());
        }catch (ConstructionException e1) {
            //logger.error("",e1);
        }
        if (node == null) {
            return ncProps;
        }

        Set<Property> props;
        NodeConnector nodeConnector;
        SNMPPhysicalPort port;
        Map<Short, SNMPPhysicalPort> ports = sw.getPhysicalPorts();
        for (Map.Entry<Short, SNMPPhysicalPort> entry : ports.entrySet()) {
            nodeConnector = PortConverter.toNodeConnector(entry.getKey(), node);
            port = entry.getValue();
            props = InventoryServiceHelper.SNMPPortToProps(port);
            ncProps.put(nodeConnector, props);
        }

        return ncProps;
    }
}
