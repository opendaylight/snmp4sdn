package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * container input {
 *     leaf nodeId {
 *         type int64;
 *     }
 *     leaf profile-id {
 *         type int32;
 *     }
 *     leaf profile-name {
 *         type string;
 *     }
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
 *     uses acl-profile;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/create-acl-profile/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInputBuilder
 */
public interface CreateAclProfileInput
    extends
    AclProfile,
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","input");;

    java.lang.Long getNodeId();
    
    java.lang.Integer getProfileId();
    
    java.lang.String getProfileName();

}

