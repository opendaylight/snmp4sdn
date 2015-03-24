package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>config</b>
 * <br />(Source path: <i>META-INF\yang\config.yang</i>):
 * <pre>
 * container input {
 *     leaf nodeId {
 *         type int64;
 *     }
 *     leaf ip-address {
 *         type string;
 *     }
 *     leaf mac-address {
 *         type int64;
 *     }
 *     uses arp-entry;
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>config/set-arp-entry/input</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryInputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryInputBuilder
 */
public interface SetArpEntryInput
    extends
    ArpEntry,
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryInput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:config","2014-08-15","input");;

    java.lang.Long getNodeId();

}

