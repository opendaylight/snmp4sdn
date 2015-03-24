package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * grouping acl-field {
 *     leaf src-ip {
 *         type string;
 *     }
 *     leaf dst-ip {
 *         type string;
 *     }
 *     leaf ctrl-vid {
 *         type int32;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>acl/acl-field</i>
 */
public interface AclField
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-field");;

    java.lang.String getSrcIp();
    
    java.lang.String getDstIp();
    
    java.lang.Integer getCtrlVid();

}

