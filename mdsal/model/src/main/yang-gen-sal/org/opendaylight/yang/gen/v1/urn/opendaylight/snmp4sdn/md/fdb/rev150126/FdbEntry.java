package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntryType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface FdbEntry
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:fdb","2015-01-26","fdb-entry")
    ;

    /**
    **/
    Long getDestMacAddr();
    
    /**
    **/
    Long getNodeId();
    
    /**
    **/
    Short getPort();
    
    /**
    **/
    FdbEntryType getType();
    
    /**
    **/
    Integer getVlanId();

}

