package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface FdbTableEntry
    extends
    ChildOf<GetFdbTableOutput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>,
    FdbEntry
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:fdb","2015-01-26","fdb-table-entry")
    ;


}

