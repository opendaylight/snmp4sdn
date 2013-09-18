package org.snmpj;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

public class SNMPVarBindList extends SNMPSequence
{

    /**
    *    Create a new empty variable binding list.
    */


    public SNMPVarBindList()
    {
        super();
    }





    /**
    *    Return the variable pairs in the list, separated by spaces.
    */

    public String toString()
    {
        Vector sequence = (Vector)(this.getValue());

        StringBuffer valueStringBuffer = new StringBuffer();

        for (int i = 0; i < sequence.size(); ++i)
        {
            valueStringBuffer.append(((SNMPObject)sequence.elementAt(i)).toString());
            valueStringBuffer.append(" ");
        }

        return valueStringBuffer.toString();
    }



}


