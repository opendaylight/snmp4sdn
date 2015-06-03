/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package contributed by David Erickson and Rob Sherwood, Inc. Their efforts are appreciated.
*/

//package org.openflow.protocol;
package org.opendaylight.snmp4sdn.protocol;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.openflow.util.U16;
import org.openflow.util.U32;
import org.openflow.util.U8;

/**
 * The base class for all protocol messages.
 */
public class SNMPMessage implements Serializable{
    public static byte SNMPP_VERSION = 0x01;
    public static int MINIMUM_LENGTH = 8;

    protected byte version;
    protected SNMPType type;
    protected short length;
    protected int xid;
    protected Long targetSwitchID;//s4s add. In OF, socket to switch always there, but in snmp4sdn, we need to bring the switch ID in SNMPMessage, so that finally give snmp the switch mac address.

    public SNMPMessage() {
        this.version = SNMPP_VERSION;
    }

    /**
     * Get the length of this message
     *
     * @return
     */
    public short getLength() {
        return length;
    }

    /**
     * Get the length of this message, unsigned
     *
     * @return
     */
    public int getLengthU() {
        return U16.f(length);
    }

    /**
     * Set the length of this message
     *
     * @param length
     */
    public /*OFMessage*/SNMPMessage setLength(short length) {
        this.length = length;
        return this;
    }

    /**
     * Set the length of this message, unsigned
     *
     * @param length
     */
    public /*OFMessage*/SNMPMessage setLengthU(int length) {
        this.length = U16.t(length);
        return this;
    }

    /**
     * Get the type of this message
     *
     * @return
     */
    public /*OFType*/SNMPType getType() {
        return type;
    }

    /**
     * Set the type of this message
     *
     * @param type
     */
    public void setType(/*OFType*/SNMPType type) {
        this.type = type;
    }

    /**
     * Get the OpenFlow version of this message
     *
     * @return
     */
    public byte getVersion() {
        return version;
    }

    /**
     * Set the OpenFlow version of this message
     *
     * @param version
     */
    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * Get the transaction id of this message
     *
     * @return
     */
    public int getXid() {
        return xid;
    }

    /**
     * Set the transaction id of this message
     *
     * @param xid
     */
    public void setXid(int xid) {
        this.xid = xid;
    }

    /**
     * Read this message off the wire from the specified ByteBuffer
     * @param data
     */
    public void readFrom(ByteBuffer data) {
        this.version = data.get();
        this.type = /*OFType*/SNMPType.valueOf(data.get());
        this.length = data.getShort();
        this.xid = data.getInt();
    }

    /**
     * Write this message's binary format to the specified ByteBuffer
     * @param data
     */
    public void writeTo(ByteBuffer data) {
        data.put(version);
        data.put(type.getTypeValue());
        data.putShort(length);
        data.putInt(xid);
    }

    /**
     * Returns a summary of the message
     * @return "ofmsg=v=$version;t=$type:l=$len:xid=$xid"
     */
    public String toString() {
        return "ofmsg" +
            ":v=" + U8.f(this.getVersion()) +
            ";t=" + this.getType() +
            ";l=" + this.getLengthU() +
            ";x=" + U32.f(this.getXid());
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + version;
        result = prime * result + xid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof /*OFMessage*/SNMPMessage)) {
            return false;
        }
        /*OFMessage*/SNMPMessage other = (/*OFMessage*/SNMPMessage) obj;
        if (length != other.length) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        if (xid != other.xid) {
            return false;
        }
        return true;
    }

    public Long getTargetSwitchID(){
        return this.targetSwitchID;
    }

    public void setTargetSwitchID(Long id){
        this.targetSwitchID = id;
    }
}
