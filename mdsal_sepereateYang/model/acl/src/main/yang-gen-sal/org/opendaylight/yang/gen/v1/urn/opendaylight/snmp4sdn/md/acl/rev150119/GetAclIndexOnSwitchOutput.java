package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndex;
import java.util.List;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * container output {
 *     list acl-index {
 *         key     leaf profile-id {
 *             type int32;
 *         }
 *         leaf rule-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *         leaf rule-name {
 *             type string;
 *         }
 *         uses acl-index;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/get-acl-index-on-switch/output</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutputBuilder
 */
public interface GetAclIndexOnSwitchOutput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","output");;

    List<AclIndex> getAclIndex();

}

