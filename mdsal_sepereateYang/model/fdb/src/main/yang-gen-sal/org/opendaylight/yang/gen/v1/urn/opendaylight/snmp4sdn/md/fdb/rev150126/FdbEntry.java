package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>fdb</b>
 * <br />(Source path: <i>META-INF\yang\fdb.yang</i>):
 * <pre>
 * grouping fdb-entry {
 *     leaf node-id {
 *         type int64;
 *     }
 *     leaf dest-mac-addr {
 *         type int64;
 *     }
 *     leaf vlan-id {
 *         type int32;
 *     }
 *     leaf port {
 *         type int16;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>fdb/fdb-entry</i>
 */
public interface FdbEntry
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:fdb","2015-01-26","fdb-entry");;

    java.lang.Long getNodeId();
    
    java.lang.Long getDestMacAddr();
    
    java.lang.Integer getVlanId();
    
    java.lang.Short getPort();

}

