/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package contributed by Rob Sherwood and David Erickson, Inc. His/Her efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol;

import java.lang.reflect.Constructor;

/**
 * List of OpenFlow types and mappings to wire protocol value and derived
 * classes
 *
 */
public enum SNMPType {
    /*HELLO               (0, SNMPHello.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPHello();
                            }}),
    ERROR               (1, SNMPError.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPError();
                            }}),
    ECHO_REQUEST        (2, SNMPEchoRequest.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPEchoRequest();
                            }}),
    ECHO_REPLY          (3, SNMPEchoReply.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPEchoReply();
                            }}),
    VENDOR              (4, SNMPVendor.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPVendor();
                            }}),
    FEATURES_REQUEST    (5, SNMPFeaturesRequest.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPFeaturesRequest();
                            }}),
    FEATURES_REPLY      (6, SNMPFeaturesReply.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPFeaturesReply();
                            }}),
    GET_CONFIG_REQUEST  (7, SNMPGetConfigRequest.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPGetConfigRequest();
                            }}),
    GET_CONFIG_REPLY    (8, SNMPGetConfigReply.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPGetConfigReply();
                            }}),
    SET_CONFIG          (9, SNMPSetConfig.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPSetConfig();
                            }}),*/
    PACKET_IN           (10, SNMPPacketIn.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPPacketIn();
                            }}),/*
    FLOW_REMOVED        (11, SNMPFlowRemoved.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPFlowRemoved();
                            }}),*/
    PORT_STATUS         (12, SNMPPortStatus.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPPortStatus();
                            }}),
    PACKET_OUT          (13, SNMPPacketOut.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPPacketOut();
                            }}),
    FLOW_MOD            (14, SNMPFlowMod.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPFlowMod();
                            }})/*,
    PORT_MOD            (15, SNMPPortMod.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPPortMod();
                            }}),
    STATS_REQUEST       (16, SNMPStatisticsRequest.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPStatisticsRequest();
                            }}),
    STATS_REPLY         (17, SNMPStatisticsReply.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPStatisticsReply();
                            }}),
    BARRIER_REQUEST     (18, SNMPBarrierRequest.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPBarrierRequest();
                            }}),
    BARRIER_REPLY       (19, SNMPBarrierReply.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPBarrierReply();
                            }}),
    QUEUE_CONFIG_REQUEST    (20, SNMPMessage.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPQueueConfigRequest();
                            }}),
    QUEUE_CONFIG_REPLY  (21, SNMPMessage.class, new Instantiable<SNMPMessage>() {
                            @Override
                            public SNMPMessage instantiate() {
                                return new SNMPQueueConfigReply();
                            }})*/;

    static SNMPType[] mapping;

    protected Class<? extends SNMPMessage> clazz;
    protected Constructor<? extends SNMPMessage> constructor;
    protected Instantiable<SNMPMessage> instantiable;
    protected byte type;

    /**
     * Store some information about the OpenFlow type, including wire protocol
     * type number, length, and derived class
     *
     * @param type Wire protocol number associated with this SNMPType
     * @param requestClass The Java class corresponding to this type of OpenFlow
     *              message
     * @param instantiator An Instantiator<SNMPMessage> implementation that creates an
     *          instance of the specified SNMPMessage
     */
    SNMPType(int type, Class<? extends SNMPMessage> clazz, Instantiable<SNMPMessage> instantiator) {
        this.type = (byte) type;
        this.clazz = clazz;
        this.instantiable = instantiator;
        try {
            this.constructor = clazz.getConstructor(new Class[]{});
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failure getting constructor for class: " + clazz, e);
        }
        SNMPType.addMapping(this.type, this);
    }

    /**
     * Adds a mapping from type value to SNMPType enum
     *
     * @param i OpenFlow wire protocol type
     * @param t type
     */
    static public void addMapping(byte i, SNMPType t) {
        if (mapping == null)
            mapping = new SNMPType[32];
        SNMPType.mapping[i] = t;
    }

    /**
     * Remove a mapping from type value to SNMPType enum
     *
     * @param i OpenFlow wire protocol type
     */
    static public void removeMapping(byte i) {
        SNMPType.mapping[i] = null;
    }

    /**
     * Given a wire protocol OpenFlow type number, return the SNMPType associated
     * with it
     *
     * @param i wire protocol number
     * @return SNMPType enum type
     */

    static public SNMPType valueOf(Byte i) {
        return SNMPType.mapping[i];
    }

    /**
     * @return Returns the wire protocol value corresponding to this SNMPType
     */
    public byte getTypeValue() {
        return this.type;
    }

    /**
     * @return return the SNMPMessage subclass corresponding to this SNMPType
     */
    public Class<? extends SNMPMessage> toClass() {
        return clazz;
    }

    /**
     * Returns the no-argument Constructor of the implementation class for
     * this SNMPType
     * @return the constructor
     */
    public Constructor<? extends SNMPMessage> getConstructor() {
        return constructor;
    }

    /**
     * Returns a new instance of the SNMPMessage represented by this SNMPType
     * @return the new object
     */
    public SNMPMessage newInstance() {
        return instantiable.instantiate();
    }

    /**
     * @return the instantiable
     */
    public Instantiable<SNMPMessage> getInstantiable() {
        return instantiable;
    }

    /**
     * @param instantiable the instantiable to set
     */
    public void setInstantiable(Instantiable<SNMPMessage> instantiable) {
        this.instantiable = instantiable;
    }
}
