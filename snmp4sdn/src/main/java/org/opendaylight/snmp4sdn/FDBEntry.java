/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

public class FDBEntry{
    public enum EntryType{
        OTHER (1),
        INVALID (2),
        LEARNED (3),//dynamic
        SELF (4),
        MGMT (5);//static
        //the following code isn't used but must exists so that we can assign value to the enum items as above, otherwise the code fails to be compile
        private int value;
        private EntryType(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
    }
    public long nodeId;
    public long destMacAddr;
    public int vlanId;
    public short port;
    public EntryType type;
}

