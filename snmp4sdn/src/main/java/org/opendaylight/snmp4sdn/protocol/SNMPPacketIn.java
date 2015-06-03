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
import java.util.Arrays;

import org.openflow.util.U16;
import org.openflow.util.U8;

/**
 * Represents an ofp_packet_in
 *
 * @author David Erickson (daviderickson@cs.stanford.edu) - Feb 8, 2010
 */
public class SNMPPacketIn extends SNMPMessage {
    public static int MINIMUM_LENGTH = 18;

    public enum SNMPPacketInReason {
        NO_MATCH, ACTION
    }

    protected int bufferId;
    protected short totalLength;
    protected short inPort;
    protected SNMPPacketInReason reason;
    protected byte[] packetData;

    public SNMPPacketIn() {
        super();
        this.type = SNMPType.PACKET_IN;
        this.length = U16.t(MINIMUM_LENGTH);
    }

    /**
     * Get buffer_id
     * @return
     */
    public int getBufferId() {
        return this.bufferId;
    }

    /**
     * Set buffer_id
     * @param bufferId
     */
    public SNMPPacketIn setBufferId(int bufferId) {
        this.bufferId = bufferId;
        return this;
    }

    /**
     * Returns the packet data
     * @return
     */
    public byte[] getPacketData() {
        return this.packetData;
    }

    /**
     * Sets the packet data, and updates the length of this message
     * @param packetData
     */
    public SNMPPacketIn setPacketData(byte[] packetData) {
        this.packetData = packetData;
        this.length = U16.t(SNMPPacketIn.MINIMUM_LENGTH + packetData.length);
        return this;
    }

    /**
     * Get in_port
     * @return
     */
    public short getInPort() {
        return this.inPort;
    }

    /**
     * Set in_port
     * @param inPort
     */
    public SNMPPacketIn setInPort(short inPort) {
        this.inPort = inPort;
        return this;
    }

    /**
     * Get reason
     * @return
     */
    public SNMPPacketInReason getReason() {
        return this.reason;
    }

    /**
     * Set reason
     * @param reason
     */
    public SNMPPacketIn setReason(SNMPPacketInReason reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Get total_len
     * @return
     */
    public short getTotalLength() {
        return this.totalLength;
    }

    /**
     * Set total_len
     * @param totalLength
     */
    public SNMPPacketIn setTotalLength(short totalLength) {
        this.totalLength = totalLength;
        return this;
    }

    @Override
    public void readFrom(ByteBuffer data) {
        super.readFrom(data);
        this.bufferId = data.getInt();
        this.totalLength = data.getShort();
        this.inPort = data.getShort();
        this.reason = SNMPPacketInReason.values()[U8.f(data.get())];
        data.get(); // pad
        this.packetData = new byte[getLengthU() - MINIMUM_LENGTH];
        data.get(this.packetData);
    }

    @Override
    public void writeTo(ByteBuffer data) {
        super.writeTo(data);
        data.putInt(bufferId);
        data.putShort(totalLength);
        data.putShort(inPort);
        data.put((byte) reason.ordinal());
        data.put((byte) 0x0); // pad
        data.put(this.packetData);
    }

    @Override
    public int hashCode() {
        final int prime = 283;
        int result = super.hashCode();
        result = prime * result + bufferId;
        result = prime * result + inPort;
        result = prime * result + Arrays.hashCode(packetData);
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + totalLength;
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
        if (!(obj instanceof SNMPPacketIn)) {
            return false;
        }
        SNMPPacketIn other = (SNMPPacketIn) obj;
        if (bufferId != other.bufferId) {
            return false;
        }
        if (inPort != other.inPort) {
            return false;
        }
        if (!Arrays.equals(packetData, other.packetData)) {
            return false;
        }
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        if (totalLength != other.totalLength) {
            return false;
        }
        return true;
    }
}
