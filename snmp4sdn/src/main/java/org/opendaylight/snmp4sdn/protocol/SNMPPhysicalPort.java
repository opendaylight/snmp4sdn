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


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;



/**
 * Represents ofp_phy_port
 * @author David Erickson (daviderickson@cs.stanford.edu) - Mar 25, 2010
 */
public class SNMPPhysicalPort implements Cloneable, Serializable {
    public static int MINIMUM_LENGTH = 48;
    public static int SNMPP_ETH_ALEN = 6;

    public SNMPPhysicalPort(short portNumber){//s4s: OF doen't have this function, we write it here for convinience assigning the values of the data such as port number
        this.portNumber = portNumber;
    }
    public SNMPPhysicalPort(){
    }

    public enum SNMPPortConfig {
        SNMPPPC_PORT_DOWN    (1 << 0),
        SNMPPPC_NO_STP       (1 << 1),
        SNMPPPC_NO_RECV      (1 << 2),
        SNMPPPC_NO_RECV_STP  (1 << 3),
        SNMPPPC_NO_FLOOD     (1 << 4),
        SNMPPPC_NO_FWD       (1 << 5),
        SNMPPPC_NO_PACKET_IN (1 << 6);

        protected int value;

        private SNMPPortConfig(int value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }
    }

    public enum SNMPPortState {
        SNMPPPS_LINK_DOWN   (1 << 0),
        SNMPPPS_STP_LISTEN  (0 << 8),
        SNMPPPS_STP_LEARN   (1 << 8),
        SNMPPPS_STP_FORWARD (2 << 8),
        SNMPPPS_STP_BLOCK   (3 << 8),
        SNMPPPS_STP_MASK    (3 << 8);

        protected int value;

        private SNMPPortState(int value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }
    }

    public enum SNMPPortFeatures {
        SNMPPPF_10MB_HD    (1 << 0),
        SNMPPPF_10MB_FD    (1 << 1),
        SNMPPPF_100MB_HD   (1 << 2),
        SNMPPPF_100MB_FD   (1 << 3),
        SNMPPPF_1GB_HD     (1 << 4),
        SNMPPPF_1GB_FD     (1 << 5),
        SNMPPPF_10GB_FD    (1 << 6),
        SNMPPPF_COPPER     (1 << 7),
        SNMPPPF_FIBER      (1 << 8),
        SNMPPPF_AUTONEG    (1 << 9),
        SNMPPPF_PAUSE      (1 << 10),
        SNMPPPF_PAUSE_ASYM (1 << 11);

        protected int value;

        private SNMPPortFeatures(int value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }
    }

    protected short portNumber;
    protected byte[] hardwareAddress;
    protected String name;
    protected int config;
    protected int state;
    protected int currentFeatures;
    protected int advertisedFeatures;
    protected int supportedFeatures;
    protected int peerFeatures;

    /**
     * @return the portNumber
     */
    public short getPortNumber() {
        return portNumber;
    }

    /**
     * @param portNumber the portNumber to set
     */
    public void setPortNumber(short portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * @return the hardwareAddress
     */
    public byte[] getHardwareAddress() {
        return hardwareAddress;
    }

    /**
     * @param hardwareAddress the hardwareAddress to set
     */
    public void setHardwareAddress(byte[] hardwareAddress) {
        if (hardwareAddress.length != SNMPP_ETH_ALEN)
            throw new RuntimeException("Hardware address must have length "
                    + SNMPP_ETH_ALEN);
        this.hardwareAddress = hardwareAddress;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the config
     */
    public int getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(int config) {
        this.config = config;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * @return the currentFeatures
     */
    public int getCurrentFeatures() {
        return currentFeatures;
    }

    /**
     * @param currentFeatures the currentFeatures to set
     */
    public void setCurrentFeatures(int currentFeatures) {
        this.currentFeatures = currentFeatures;
    }

    /**
     * @return the advertisedFeatures
     */
    public int getAdvertisedFeatures() {
        return advertisedFeatures;
    }

    /**
     * @param advertisedFeatures the advertisedFeatures to set
     */
    public void setAdvertisedFeatures(int advertisedFeatures) {
        this.advertisedFeatures = advertisedFeatures;
    }

    /**
     * @return the supportedFeatures
     */
    public int getSupportedFeatures() {
        return supportedFeatures;
    }

    /**
     * @param supportedFeatures the supportedFeatures to set
     */
    public void setSupportedFeatures(int supportedFeatures) {
        this.supportedFeatures = supportedFeatures;
    }

    /**
     * @return the peerFeatures
     */
    public int getPeerFeatures() {
        return peerFeatures;
    }

    /**
     * @param peerFeatures the peerFeatures to set
     */
    public void setPeerFeatures(int peerFeatures) {
        this.peerFeatures = peerFeatures;
    }

    /**
     * Read this message off the wire from the specified ByteBuffer
     * @param data
     */
    public void readFrom(ByteBuffer data) {
        this.portNumber = data.getShort();
        if (this.hardwareAddress == null)
            this.hardwareAddress = new byte[SNMPP_ETH_ALEN];
        data.get(this.hardwareAddress);
        byte[] name = new byte[16];
        data.get(name);
        // find the first index of 0
        int index = 0;
        for (byte b : name) {
            if (0 == b)
                break;
            ++index;
        }
        this.name = new String(Arrays.copyOf(name, index),
                Charset.forName("ascii"));
        this.config = data.getInt();
        this.state = data.getInt();
        this.currentFeatures = data.getInt();
        this.advertisedFeatures = data.getInt();
        this.supportedFeatures = data.getInt();
        this.peerFeatures = data.getInt();
    }

    /**
     * Write this message's binary format to the specified ByteBuffer
     * @param data
     */
    public void writeTo(ByteBuffer data) {
        data.putShort(this.portNumber);
        data.put(hardwareAddress);
        try {
            byte[] name = this.name.getBytes("ASCII");
            if (name.length < 16) {
                data.put(name);
                for (int i = name.length; i < 16; ++i) {
                    data.put((byte) 0);
                }
            } else {
                data.put(name, 0, 15);
                data.put((byte) 0);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        data.putInt(this.config);
        data.putInt(this.state);
        data.putInt(this.currentFeatures);
        data.putInt(this.advertisedFeatures);
        data.putInt(this.supportedFeatures);
        data.putInt(this.peerFeatures);
    }

    @Override
    public int hashCode() {
        final int prime = 307;
        int result = 1;
        result = prime * result + advertisedFeatures;
        result = prime * result + config;
        result = prime * result + currentFeatures;
        result = prime * result + Arrays.hashCode(hardwareAddress);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + peerFeatures;
        result = prime * result + portNumber;
        result = prime * result + state;
        result = prime * result + supportedFeatures;
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
        if (!(obj instanceof SNMPPhysicalPort)) {
            return false;
        }
        SNMPPhysicalPort other = (SNMPPhysicalPort) obj;
        if (advertisedFeatures != other.advertisedFeatures) {
            return false;
        }
        if (config != other.config) {
            return false;
        }
        if (currentFeatures != other.currentFeatures) {
            return false;
        }
        if (!Arrays.equals(hardwareAddress, other.hardwareAddress)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (peerFeatures != other.peerFeatures) {
            return false;
        }
        if (portNumber != other.portNumber) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        if (supportedFeatures != other.supportedFeatures) {
            return false;
        }
        return true;
    }

    public SNMPPhysicalPort cloneSNMPPhysicalPort() {
        SNMPPhysicalPort p;
        try
        {
            p = (SNMPPhysicalPort) this.clone();

        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError();
        }
        return p;
    }

}
