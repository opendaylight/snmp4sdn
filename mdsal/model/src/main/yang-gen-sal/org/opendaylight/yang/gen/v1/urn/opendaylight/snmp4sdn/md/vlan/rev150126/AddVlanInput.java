package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface AddVlanInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>
{




    /**
    **/
    Long getNodeId();
    
    /**
    **/
    Integer getVlanId();
    
    /**
    **/
    String getVlanName();

}

