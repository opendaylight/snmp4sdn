package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface GetPortListEntry
    extends
    ChildOf<GetVlanPortsOutput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>,
    VlanPort
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-01-26","get-port-list-entry")
    ;


}

