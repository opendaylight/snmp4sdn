package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
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
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/del-acl-profile/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInputBuilder
 */
public interface DelAclProfileInput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","input");;

    java.lang.Long getNodeId();
    
    java.lang.Integer getProfileId();
    
    java.lang.String getProfileName();

}

