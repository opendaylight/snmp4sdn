package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * grouping vlan-port {
 *     leaf port {
 *         type int16;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/vlan-port</i>
 */
public interface VlanPort
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-01-26","vlan-port");;

    java.lang.Short getPort();

}

