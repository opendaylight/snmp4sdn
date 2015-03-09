package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>fdb</b>
 * <br />(Source path: <i>META-INF\yang\fdb.yang</i>):
 * <pre>
 * container output {
 *     leaf node-id {
 *         type int64;
 *     }
 *     leaf vlan-id {
 *         type int32;
 *     }
 *     leaf dest-mac-addr {
 *         type int64;
 *     }
 *     leaf port {
 *         type int16;
 *     }
 *     leaf type {
 *         type fdb-entry-type;
 *     }
 *     uses fdb-entry;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>fdb/get-fdb-entry/output</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutputBuilder
 */
public interface GetFdbEntryOutput
    extends
    FdbEntry,
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:fdb","2015-01-26","output");;


}

