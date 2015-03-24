package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * grouping acl-rule {
 *     leaf-list port-list {
 *         type int16;
 *     }
 *     leaf src-ip {
 *         type string;
 *     }
 *     leaf dst-ip {
 *         type string;
 *     }
 *     leaf ctrl-vid {
 *         type int32;
 *     }
 *     leaf acl-action {
 *         type acl-action;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/acl-rule</i>
 */
public interface AclRule
    extends
    DataObject,
    AclField
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-rule");;

    List<java.lang.Short> getPortList();
    
    AclAction getAclAction();

}

