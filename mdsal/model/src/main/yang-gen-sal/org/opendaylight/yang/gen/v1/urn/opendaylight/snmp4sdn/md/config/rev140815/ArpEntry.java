package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface ArpEntry
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:config","2014-08-15","arp-entry")
    ;

    /**
    **/
    String getIpAddress();
    
    /**
    **/
    Long getMacAddress();

}

