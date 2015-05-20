package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry;
import java.util.List;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * container output {
 *     list vlan-table-entry {
 *         key     leaf vlan-id {
 *             type int32;
 *         }
 *         leaf vlan-name {
 *             type string;
 *         }
 *         leaf-list port-list {
 *             type int16;
 *         }
 *         uses vlan-entry;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/get-vlan-table/output</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.GetVlanTableOutputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.GetVlanTableOutputBuilder
 */
public interface GetVlanTableOutput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.GetVlanTableOutput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-05-15","output");;

    List<VlanTableEntry> getVlanTableEntry();

}

