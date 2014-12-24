package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>config</b>
 * <br />(Source path: <i>META-INF/yang/config.yang</i>):
 * <pre>
 * grouping arp-entry {
 *     leaf ip-address {
 *         type string;
 *     }
 *     leaf mac-address {
 *         type int64;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>config/arp-entry</i>
 */
public interface ArpEntry
    extends
    DataObject
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:config","2014-08-15","arp-entry");;

    java.lang.String getIpAddress();
    
    java.lang.Long getMacAddress();

}

