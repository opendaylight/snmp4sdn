package org.snmpj;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

public abstract class SNMPObject
{

    /**
    *    Must return a Java object appropriate to represent the value/data contained
    *     in the SNMP object
    */

    public abstract Object getValue();



    /**
    *    Must set the value of the SNMP object when supplied with an appropriate
    *     Java object containing an appropriate value.
    */

    public abstract void setValue(Object o)
        throws SNMPBadValueException;



    /**
    *    Should return an appropriate human-readable representation of the stored value.
    */

    @Override
    public abstract String toString();



    /**
    *    Must return the BER byte encoding (type, length, value) of the SNMP object.
    */

    protected abstract byte[] getBEREncoding();


    /**
    *   Compares two SNMPObject subclass objects by checking their values for equality.
    */

    @Override
    public boolean equals(Object other)
    {
        // false if other is null
        if (other == null)
        {
            return false;
        }

        // check first to see that they're both of the same class
        if (!this.getClass().equals(other.getClass()))
        {
            return false;
        }

        SNMPObject otherSNMPObject = (SNMPObject)other;

        // now see if their embedded values are equal
        if (this.getValue().equals(otherSNMPObject.getValue()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
    *   Generates a hash value so SNMP objects can be used in Hashtables.
    */

    @Override
    public int hashCode()
    {
        // just use hashcode value of embedded value by default
        if (this.getValue() != null)
        {
            return this.getValue().hashCode();
        }
        else
        {
            return 0;
        }
    }


}


