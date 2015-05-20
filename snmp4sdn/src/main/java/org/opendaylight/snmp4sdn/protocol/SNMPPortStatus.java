/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package. The authors' efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol;

import java.nio.ByteBuffer;

import org.openflow.util.U16;

/**
 * Represents an ofp_port_status message
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public class SNMPPortStatus extends SNMPMessage {
    public static int MINIMUM_LENGTH = 64;

    public enum SNMPPortReason {
        SNMPPPR_ADD,
        SNMPPPR_DELETE,
        SNMPPPR_MODIFY
    }

    protected byte reason;
    protected SNMPPhysicalPort desc;

    /**
     * @return the reason
     */
    public byte getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(byte reason) {
        this.reason = reason;
    }

    /**
     * @return the desc
     */
    public SNMPPhysicalPort getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(SNMPPhysicalPort desc) {
        this.desc = desc;
    }

    public SNMPPortStatus() {
        super();
        this.type = SNMPType.PORT_STATUS;
        this.length = U16.t(MINIMUM_LENGTH);
    }

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        this.reason = data.get();
        data.position(data.position() + 7); // skip 7 bytes of padding
        if (this.desc == null)
            this.desc = new SNMPPhysicalPort();
        this.desc.readFrom(data);
    }

    @Override
    public void writeTo(ByteBuffer data) {
        super.writeTo(data);
        data.put(this.reason);
        for (int i = 0; i < 7; ++i)
            data.put((byte) 0);
        this.desc.writeTo(data);
    }

    @Override
    public int hashCode() {
        final int prime = 313;
        int result = super.hashCode();
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + reason;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof SNMPPortStatus)) {
            return false;
        }
        SNMPPortStatus other = (SNMPPortStatus) obj;
        if (desc == null) {
            if (other.desc != null) {
                return false;
            }
        } else if (!desc.equals(other.desc)) {
            return false;
        }
        if (reason != other.reason) {
            return false;
        }
        return true;
    }
}
