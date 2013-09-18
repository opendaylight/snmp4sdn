package org.snmpj;


/**
*    Exception thrown whenever attempt made to create SNMPObject subclass with inappropriate
*    data, or to set its value with inappropriate data,
*/

public class SNMPBadValueException extends Exception
{

    public SNMPBadValueException()
    {
        super();
    }


    /**
    *    Create exception with message string.
    */

    public SNMPBadValueException(String s)
    {
        super(s);
    }

}
