package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface DeleteArpEntryInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryInput>
{




    /**
    **/
    String getIpAddress();
    
    /**
    **/
    Long getNodeId();

}

