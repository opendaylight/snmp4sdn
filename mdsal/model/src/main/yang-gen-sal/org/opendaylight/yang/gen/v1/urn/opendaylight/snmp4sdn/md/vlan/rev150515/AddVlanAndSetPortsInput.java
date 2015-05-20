package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * container input {
 *     leaf node-id {
 *         type int64;
 *     }
 *     leaf vlan-id {
 *         type int32;
 *     }
 *     leaf vlanName {
 *         type string;
 *     }
 *     leaf tagged-port-list {
 *         type string;
 *     }
 *     leaf untagged-port-list {
 *         type string;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/add-vlan-and-set-ports/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInputBuilder
 */
public interface AddVlanAndSetPortsInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-05-15","input");;

    java.lang.Long getNodeId();
    
    java.lang.Integer getVlanId();
    
    java.lang.String getVlanName();
    
    java.lang.String getTaggedPortList();
    
    java.lang.String getUntaggedPortList();

}

