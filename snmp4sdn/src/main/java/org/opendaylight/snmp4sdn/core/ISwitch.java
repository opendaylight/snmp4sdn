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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.openflow.protocol.OFMessage;
import org.opendaylight.snmp4sdn.protocol.SNMPPhysicalPort;
import org.openflow.protocol.OFStatisticsRequest;

import org.opendaylight.snmp4sdn.protocol.SNMPMessage;//s4s add


/**
 * This interface defines an abstraction of an Open Flow Switch.
 *
 */
public interface ISwitch {
    /**
     * Gets a unique XID.
     *
     * @return XID
     */
    public int getNextXid();

    /**
     * Returns the Switch's ID.
     *
     * @return the Switch's ID
     */
    public Long getId();

    /**
     * Returns the Switch's table numbers supported by datapath
     *
     * @return the tables
     */
    public Byte getTables();

    /**
     * Returns the Switch's bitmap of supported ofp_action_type
     *
     * @return the actions
     */
    public Integer getActions();

    /**
     * Returns the Switch's bitmap of supported ofp_capabilities
     *
     * @return the capabilities
     */
    public Integer getCapabilities();

    /**
     * Returns the Switch's buffering capacity in Number of Pkts
     *
     * @return the buffers
     */
    public Integer getBuffers();

    /**
     * Returns the Date when the switch was connected.
     *
     * @return Date The date when the switch was connected
     */
    public Date getConnectedDate();

    /**
     * This method puts the message in an outgoing priority queue with normal
     * priority. It will be served after high priority messages. The method
     * should be used for non-critical messages such as statistics request,
     * discovery packets, etc. An unique XID is generated automatically and
     * inserted into the message.
     *
     * @param msg
     *            The OF message to be sent
     * @return The XID used
     */
    public Integer asyncSend(SNMPMessage msg);

    /**
     * This method puts the message in an outgoing priority queue with normal
     * priority. It will be served after high priority messages. The method
     * should be used for non-critical messages such as statistics request,
     * discovery packets, etc. The specified XID is inserted into the message.
     *
     * @param msg
     *            The OF message to be Sent
     * @param xid
     *            The XID to be used in the message
     * @return The XID used
     */
    public Integer asyncSend(SNMPMessage msg, int xid);

    /**
     * This method puts the message in an outgoing priority queue with high
     * priority. It will be served first before normal priority messages. The
     * method should be used for critical messages such as hello, echo reply
     * etc. An unique XID is generated automatically and inserted into the
     * message.
     *
     * @param msg
     *            The OF message to be sent
     * @return The XID used
     */
    public Integer asyncFastSend(SNMPMessage msg);

    /**
     * This method puts the message in an outgoing priority queue with high
     * priority. It will be served first before normal priority messages. The
     * method should be used for critical messages such as hello, echo reply
     * etc. The specified XID is inserted into the message.
     *
     * @param msg
     *            The OF message to be sent
     * @return The XID used
     */
    public Integer asyncFastSend(SNMPMessage msg, int xid);

    /**
     * Sends the OF message followed by a Barrier Request with a unique XID
     * which is automatically generated, and waits for a result from the switch.
     *
     * @param msg
     *            The message to be sent
     * @return An Object which has one of the followings instances/values:
     *         Boolean with value true to indicate the message has been
     *         successfully processed and acknowledged by the switch; Boolean
     *         with value false to indicate the message has failed to be
     *         processed by the switch within a period of time or OFError to
     *         indicate that the message has been denied by the switch which
     *         responded with OFError.
     */
    public Object syncSend(SNMPMessage msg);

    /**
     * Returns a map containing all SNMPPhysicalPorts of this switch.
     *
     * @return The Map of SNMPPhysicalPort
     */
    public Map<Short, SNMPPhysicalPort> getPhysicalPorts();

    /**
     * Returns a Set containing all port IDs of this switch.
     *
     * @return The Set of port ID
     */
    public Set<Short> getPorts();

    /**
     * Returns SNMPPhysicalPort of the specified portNumber of this switch.
     *
     * @param portNumber
     *            The port ID
     * @return SNMPPhysicalPort for the specified PortNumber
     */
    public SNMPPhysicalPort getPhysicalPort(Short portNumber);

    /**
     * Returns the bandwidth of the specified portNumber of this switch.
     *
     * @param portNumber
     *            the port ID
     * @return bandwidth
     */
    public Integer getPortBandwidth(Short portNumber);

    /**
     * Returns True if the port is enabled,
     *
     * @param portNumber
     * @return True if the port is enabled
     */
    public boolean isPortEnabled(short portNumber);

    /**
     * Returns True if the port is enabled.
     *
     * @param port
     * @return True if the port is enabled
     */
    public boolean isPortEnabled(SNMPPhysicalPort port);

    /**
     * Returns a list containing all enabled ports of this switch.
     *
     * @return: List containing all enabled ports of this switch
     */
    public List<SNMPPhysicalPort> getEnabledPorts();

    /**
     * Sends OFStatisticsRequest with a unique XID generated automatically and
     * waits for a result from the switch.
     *
     * @param req
     *            the OF Statistic Request to be sent
     * @return Object has one of the following instances/values::
     *         List<OFStatistics>, a list of statistics records received from
     *         the switch as response from the request; OFError if the switch
     *         failed handle the request or NULL if timeout has occurred while
     *         waiting for the response.
     */
    public Object getStatistics(OFStatisticsRequest req);

    /**
     * Returns true if the switch has reached the operational state (has sent
     * FEATURE_REPLY to the controller).
     *
     * @return true if the switch is operational
     */
    public boolean isOperational();

    /**
     * Send Barrier message synchronously. The caller will be blocked until the
     * Barrier reply arrives.
     */
    Object syncSendBarrierMessage();

    /**
     * Send Barrier message asynchronously. The caller is not blocked. The
     * Barrier message will be sent in a transmit thread which will be blocked
     * until the Barrier reply arrives.
     */
    Object asyncSendBarrierMessage();
}
