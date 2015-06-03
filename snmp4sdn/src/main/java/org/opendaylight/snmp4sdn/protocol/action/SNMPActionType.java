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

package org.opendaylight.snmp4sdn.protocol.action;


import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.openflow.protocol.Instantiable;

/**
 * List of OpenFlow Action types and mappings to wire protocol value and
 * derived classes
 *
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public enum SNMPActionType implements Serializable{
    OUTPUT              (0, SNMPActionOutput.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionOutput();
                            }})/*,
    SET_VLAN_VID        (1, SNMPActionVirtualLanIdentifier.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionVirtualLanIdentifier();
                            }}),
    SET_VLAN_PCP        (2, SNMPActionVirtualLanPriorityCodePoint.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionVirtualLanPriorityCodePoint();
                            }}),
    STRIP_VLAN          (3, SNMPActionStripVirtualLan.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionStripVirtualLan();
                            }}),
    SET_DL_SRC          (4, SNMPActionDataLayerSource.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionDataLayerSource();
                            }}),
    SET_DL_DST          (5, SNMPActionDataLayerDestination.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionDataLayerDestination();
                            }}),
    SET_NW_SRC          (6, SNMPActionNetworkLayerSource.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionNetworkLayerSource();
                            }}),
    SET_NW_DST          (7, SNMPActionNetworkLayerDestination.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionNetworkLayerDestination();
                            }}),
    SET_NW_TOS          (8, SNMPActionNetworkTypeOfService.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionNetworkTypeOfService();
                            }}),
    SET_TP_SRC          (9, SNMPActionTransportLayerSource.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionTransportLayerSource();
                            }}),
    SET_TP_DST          (10, SNMPActionTransportLayerDestination.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionTransportLayerDestination();
                            }}),
    OPAQUE_ENQUEUE      (11, SNMPActionEnqueue.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionEnqueue();
                            }}),
    VENDOR              (0xffff, SNMPActionVendor.class, new Instantiable<SNMPAction>() {
                            @Override
                            public SNMPAction instantiate() {
                                return new SNMPActionVendor();
                            }})*/
                            ;

    protected static SNMPActionType[] mapping;

    protected Class<? extends SNMPAction> clazz;
    protected Constructor<? extends SNMPAction> constructor;
    protected Instantiable<SNMPAction> instantiable;
    protected int minLen;
    protected short type;

    /**
     * Store some information about the OpenFlow Action type, including wire
     * protocol type number, length, and derrived class
     *
     * @param type Wire protocol number associated with this SNMPType
     * @param clazz The Java class corresponding to this type of OpenFlow Action
     * @param instantiable the instantiable for the SNMPAction this type represents
     */
    SNMPActionType(int type, Class<? extends SNMPAction> clazz, Instantiable<SNMPAction> instantiable) {
        this.type = (short) type;
        this.clazz = clazz;
        this.instantiable = instantiable;
        try {
            this.constructor = clazz.getConstructor(new Class[]{});
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failure getting constructor for class: " + clazz, e);
        }
        SNMPActionType.addMapping(this.type, this);
    }

    /**
     * Adds a mapping from type value to SNMPActionType enum
     *
     * @param i OpenFlow wire protocol Action type value
     * @param t type
     */
    static public void addMapping(short i, SNMPActionType t) {
        if (mapping == null)
            mapping = new SNMPActionType[16];
        // bring higher mappings down to the edge of our array
        if (i < 0)
            i = (short) (16 + i);
        SNMPActionType.mapping[i] = t;
    }

    /**
     * Given a wire protocol OpenFlow type number, return the SNMPType associated
     * with it
     *
     * @param i wire protocol number
     * @return SNMPType enum type
     */

    static public SNMPActionType valueOf(short i) {
        if (i < 0)
            i = (short) (16+i);
        return SNMPActionType.mapping[i];
    }

    /**
     * @return Returns the wire protocol value corresponding to this
     *         SNMPActionType
     */
    public short getTypeValue() {
        return this.type;
    }

    /**
     * @return return the SNMPAction subclass corresponding to this SNMPActionType
     */
    public Class<? extends SNMPAction> toClass() {
        return clazz;
    }

    /**
     * Returns the no-argument Constructor of the implementation class for
     * this SNMPActionType
     * @return the constructor
     */
    public Constructor<? extends SNMPAction> getConstructor() {
        return constructor;
    }

    /**
     * Returns a new instance of the SNMPAction represented by this SNMPActionType
     * @return the new object
     */
    public SNMPAction newInstance() {
        return instantiable.instantiate();
    }

    /**
     * @return the instantiable
     */
    public Instantiable<SNMPAction> getInstantiable() {
        return instantiable;
    }

    /**
     * @param instantiable the instantiable to set
     */
    public void setInstantiable(Instantiable<SNMPAction> instantiable) {
        this.instantiable = instantiable;
    }
}
