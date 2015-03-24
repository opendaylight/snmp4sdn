package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * list acl-index {
 *     key     leaf profile-id {
 *         type int32;
 *     }
 *     leaf rule-id {
 *         type int32;
 *     }
 *     leaf profile-name {
 *         type string;
 *     }
 *     leaf rule-name {
 *         type string;
 *     }
 *     uses acl-index;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/get-acl-index-on-switch/output/acl-index</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndexBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndexBuilder@see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndexKey
 */
public interface AclIndex
    extends
    ChildOf<GetAclIndexOnSwitchOutput>,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndex>,
    org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-index");;


}

