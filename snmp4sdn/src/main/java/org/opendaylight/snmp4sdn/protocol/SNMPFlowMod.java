/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package contributed by David Erickson, Inc. His/Her efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol;


import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.openflow.util.U16;

import org.opendaylight.controller.sal.flowprogrammer.Flow;//s4s add
import org.openflow.protocol.OFMatch;//s4s add. may remove after revison done (no OF in this file)
import org.openflow.protocol.OFPort;//s4s add. may remove after revison done (no OF in this file)


public class SNMPFlowMod extends SNMPMessage  {
    public static int MINIMUM_LENGTH = 72;

    public static final short ETHPFC_ADD = 3;//OFPFC_ADD = 0;                /* New flow. */
    public static final short ETHPFC_MODIFY = 3;//OFPFC_MODIFY = 1;             /* Modify all matching flows. */
    public static final short OFPFC_MODIFY_STRICT = 2;      /* Modify entry strictly matching wildcards */
    public static final short ETHPFC_DELETE=2;//OFPFC_DELETE=3;               /* Delete all matching flows. */
    public static final short OFPFC_DELETE_STRICT =4;       /* Strictly match wildcards and priority. */

    protected OFMatch match;
    protected short command;
    protected short outPort;

    Flow flow;//s4s add

    protected int xid;//s4s add. OFMessage has "xid", so OFFlowMod (extends OFMessage) has xid, so here we should also have a xid variable (it is indeed needed in other place).

    public SNMPFlowMod() {
        super();
        this.type = SNMPType.FLOW_MOD;
        this.length = U16.t(MINIMUM_LENGTH);
    }

    public SNMPFlowMod(short command, Flow flow) {//s4s add
        super();
        this.type = SNMPType.FLOW_MOD;
        this.length = U16.t(MINIMUM_LENGTH);
        this.command = command;
        this.flow = flow;
    }

    /**
     * Get command
     * @return
     */
    public short getCommand() {
        return this.command;
    }

    /**
     * Set command
     * @param command
     */
    public SNMPFlowMod setCommand(short command) {
        this.command = command;
        return this;
    }

    /**
     * Gets a copy of the OFMatch object for this FlowMod, changes to this
     * object do not modify the FlowMod
     * @return
     */
    public OFMatch getMatch() {
        return this.match;
    }

    /**
     * Set match
     * @param match
     */
    public SNMPFlowMod setMatch(OFMatch match) {
        this.match = match;
        return this;
    }

    /**
     * Get out_port
     * @return
     */
    public short getOutPort() {
        return this.outPort;
    }

    /**
     * Set out_port
     * @param outPort
     */
    public SNMPFlowMod setOutPort(short outPort) {
        this.outPort = outPort;
        return this;
    }

    /**
     * Set out_port
     * @param port
     */
    public SNMPFlowMod setOutPort(OFPort port) {
        this.outPort = port.getValue();
        return this;
    }

 
    public String toString() {
        return "SNMPFlowMod ["
                + "command=" + command
                + ", match="
                + ", outPort=" + outPort;
    }

    public Flow getFlow(){//s4s add
        return this.flow;
    }

    public int getXid() {//s4s add
        return xid;
    }

    public void setXid(int xid) {//s4s add
        this.xid = xid;
    }
}
