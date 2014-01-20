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

/**
 * @author David Erickson (daviderickson@cs.stanford.edu) - Mar 11, 2010
 */
package org.opendaylight.snmp4sdn.protocol.action;

import java.nio.ByteBuffer;

import org.openflow.util.U16;

/**
 * @author David Erickson (daviderickson@cs.stanford.edu) - Mar 11, 2010
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 */
public class SNMPActionOutput extends SNMPAction implements Cloneable {
    public static int MINIMUM_LENGTH = 8;

    protected short port;
    protected short maxLength;

    public SNMPActionOutput() {
        super.setType(SNMPActionType.OUTPUT);
        super.setLength((short) MINIMUM_LENGTH);
    }

    public SNMPActionOutput(short port, short maxLength) {
        super();
        super.setType(SNMPActionType.OUTPUT);
        super.setLength((short) MINIMUM_LENGTH);
        this.port = port;
        this.maxLength = maxLength;
    }

    /**
     * Get the output port
     * @return
     */
    public short getPort() {
        return this.port;
    }

    /**
     * Set the output port
     * @param port
     */
    public SNMPActionOutput setPort(short port) {
        this.port = port;
        return this;
    }

    /**
     * Get the max length to send to the controller
     * @return
     */
    public short getMaxLength() {
        return this.maxLength;
    }

    /**
     * Set the max length to send to the controller
     * @param maxLength
     */
    public SNMPActionOutput setMaxLength(short maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        this.port = data.getShort();
        this.maxLength = data.getShort();
    }

    @Override
    public void writeTo(ByteBuffer data) {
        super.writeTo(data);
        data.putShort(port);
        data.putShort(maxLength);
    }

    @Override
    public int hashCode() {
        final int prime = 367;
        int result = super.hashCode();
        result = prime * result + maxLength;
        result = prime * result + port;
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
        if (!(obj instanceof SNMPActionOutput)) {
            return false;
        }
        SNMPActionOutput other = (SNMPActionOutput) obj;
        if (maxLength != other.maxLength) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SNMPActionOutput [maxLength=" + maxLength + ", port=" + U16.f(port)
                + ", length=" + length + ", type=" + type + "]";
    }
}
