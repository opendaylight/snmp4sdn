package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * container input {
 *     leaf nodeId {
 *         type int64;
 *     }
 *     leaf rule-id {
 *         type int32;
 *     }
 *     leaf rule-name {
 *         type string;
 *     }
 *     leaf profile-id {
 *         type int32;
 *     }
 *     leaf profile-name {
 *         type string;
 *     }
 *     leaf-list port-list {
 *         type int16;
 *     }
 *     leaf acl-layer {
 *         type acl-layer;
 *     }
 *     leaf vlan-id {
 *         type int32;
 *     }
 *     leaf src-ip {
 *         type string;
 *     }
 *     leaf dst-ip {
 *         type string;
 *     }
 *     leaf acl-action {
 *         type acl-action;
 *     }
 *     uses acl-rule;
 *     uses acl-index;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/set-acl-rule/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInputBuilder
 */
public interface SetAclRuleInput
    extends
    AclRule,
    AclIndex,
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","input");;

    java.lang.Long getNodeId();

}

