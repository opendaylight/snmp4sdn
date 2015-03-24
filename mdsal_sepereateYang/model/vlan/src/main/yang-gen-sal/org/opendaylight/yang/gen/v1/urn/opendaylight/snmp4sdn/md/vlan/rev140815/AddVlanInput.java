package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * container input {
 *     leaf nodeID {
 *         type int64;
 *     }
 *     leaf vlanID {
 *         type int32;
 *     }
 *     leaf vlanName {
 *         type string;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/add-vlan/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInputBuilder
 */
public interface AddVlanInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2014-08-15","input");;

    java.lang.Long getNodeID();
    
    java.lang.Integer getVlanID();
    
    java.lang.String getVlanName();

}

