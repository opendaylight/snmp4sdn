package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>fdb</b>
 * <br />(Source path: <i>META-INF\yang\fdb.yang</i>):
 * <pre>
 * container input {
 *     leaf node-id {
 *         type int64;
 *     }
 *     leaf dest-mac-addr {
 *         type int64;
 *     }
 *     leaf vlan-id {
 *         type int32;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>fdb/del-fdb-entry/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInputBuilder
 */
public interface DelFdbEntryInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:fdb","2015-01-26","input");;

    java.lang.Long getNodeId();
    
    java.lang.Long getDestMacAddr();
    
    java.lang.Integer getVlanId();

}

