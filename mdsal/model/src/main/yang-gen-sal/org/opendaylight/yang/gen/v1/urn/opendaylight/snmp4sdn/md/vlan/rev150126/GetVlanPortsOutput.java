package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Augmentable;
import java.util.List;


/**
 * <p>This class represents the following YANG schema fragment defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * container output {
 *     list get-port-list-entry {
 *         key     leaf port {
 *             type int16;
 *         }
 *         uses vlan-port;
 *     }
 * }
 * </pre>
 * The schema path to identify an instance is
 * <i>vlan/get-vlan-ports/output</i>
 * 
 * <p>To create instances of this class use {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutputBuilder}.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutputBuilder
 */
public interface GetVlanPortsOutput
    extends
    DataObject,
    Augmentable<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:vlan","2015-01-26","output");;

    List<GetPortListEntry> getGetPortListEntry();

}

