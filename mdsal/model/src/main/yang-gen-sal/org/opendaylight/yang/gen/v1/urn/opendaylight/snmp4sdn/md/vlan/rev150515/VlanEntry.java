package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.List;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * grouping vlan-entry {
 *     leaf vlan-id {
 *         type int32;
 *     }
 *     leaf vlan-name {
 *         type string;
 *     }
 *     leaf-list port-list {
 *         type int16;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/vlan-entry</i>
 */
public interface VlanEntry
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-05-15","vlan-entry");;

    java.lang.Integer getVlanId();
    
    java.lang.String getVlanName();
    
    List<java.lang.Short> getPortList();

}

