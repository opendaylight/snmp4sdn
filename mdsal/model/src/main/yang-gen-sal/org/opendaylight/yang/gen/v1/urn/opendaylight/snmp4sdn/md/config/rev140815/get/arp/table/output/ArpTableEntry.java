package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.get.arp.table.output;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableOutput;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface ArpTableEntry
    extends
    ChildOf<GetArpTableOutput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.get.arp.table.output.ArpTableEntry>,
    ArpEntry
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:config","2014-08-15","arp-table-entry")
    ;


}

