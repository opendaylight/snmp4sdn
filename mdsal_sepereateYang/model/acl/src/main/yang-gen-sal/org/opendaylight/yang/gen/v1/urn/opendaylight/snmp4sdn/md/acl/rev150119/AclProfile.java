package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * grouping acl-profile {
 *     leaf acl-layer {
 *         type acl-layer;
 *     }
 *     leaf vlan-mask {
 *         type int32;
 *     }
 *     leaf src-ip-mask {
 *         type string;
 *     }
 *     leaf dst-ip-mask {
 *         type string;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/acl-profile</i>
 */
public interface AclProfile
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-profile");;

    AclLayer getAclLayer();
    
    java.lang.Integer getVlanMask();
    
    java.lang.String getSrcIpMask();
    
    java.lang.String getDstIpMask();

}

