package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.set.vlan.ports.input.SetPortListEntry;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
**/
public interface SetVlanPortsInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>
{




    /**
    **/
    Long getNodeId();
    
    List<SetPortListEntry> getSetPortListEntry();
    
    /**
    **/
    Integer getVlanId();

}

