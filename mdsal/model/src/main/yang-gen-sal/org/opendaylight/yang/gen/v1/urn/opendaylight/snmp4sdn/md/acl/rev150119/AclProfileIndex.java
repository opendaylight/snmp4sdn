package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * grouping acl-profile-index {
 *     leaf profile-id {
 *         type int32;
 *     }
 *     leaf profile-name {
 *         type string;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/acl-profile-index</i>
 */
public interface AclProfileIndex
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-profile-index");;

    java.lang.Integer getProfileId();
    
    java.lang.String getProfileName();

}

