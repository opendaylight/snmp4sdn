package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface DelFdbEntryInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>
{




    /**
    **/
    Long getDestMacAddr();
    
    /**
    **/
    Long getNodeId();
    
    /**
    **/
    Integer getVlanId();

}

