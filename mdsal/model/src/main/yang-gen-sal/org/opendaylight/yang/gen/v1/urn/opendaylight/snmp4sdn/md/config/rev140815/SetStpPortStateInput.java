package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface SetStpPortStateInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>
{




    /**
    **/
    Boolean isEnable();
    
    /**
    **/
    Long getNodeId();
    
    /**
    **/
    Short getPort();

}

