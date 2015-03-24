package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.Result;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * container output {
 *     leaf del-acl-profile-result {
 *         type result;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/del-acl-profile/output</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutputBuilder
 */
public interface DelAclProfileOutput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","output");;

    Result getDelAclProfileResult();

}

