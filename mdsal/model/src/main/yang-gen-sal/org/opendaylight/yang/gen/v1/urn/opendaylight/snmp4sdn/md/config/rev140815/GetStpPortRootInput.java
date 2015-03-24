package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface GetStpPortRootInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>
{




    /**
    **/
    Long getNodeId();
    
    /**
    **/
    Short getPort();

}

