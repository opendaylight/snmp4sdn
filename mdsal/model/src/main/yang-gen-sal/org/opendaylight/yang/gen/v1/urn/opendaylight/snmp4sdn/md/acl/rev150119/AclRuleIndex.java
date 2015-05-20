package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * grouping acl-rule-index {
 *     leaf rule-id {
 *         type int32;
 *     }
 *     leaf rule-name {
 *         type string;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/acl-rule-index</i>
 */
public interface AclRuleIndex
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-rule-index");;

    java.lang.Integer getRuleId();
    
    java.lang.String getRuleName();

}

