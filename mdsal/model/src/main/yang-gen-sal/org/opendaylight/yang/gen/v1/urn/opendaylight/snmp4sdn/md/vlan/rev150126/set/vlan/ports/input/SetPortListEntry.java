package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.set.vlan.ports.input;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface SetPortListEntry
    extends
    ChildOf<SetVlanPortsInput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.set.vlan.ports.input.SetPortListEntry>,
    VlanPort
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-01-26","set-port-list-entry")
    ;


}

