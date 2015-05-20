package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>config</b>
 * <br />(Source path: <i>META-INF\yang\config.yang</i>):
 * <pre>
 * rpc delete-arp-entry {
 *     "Delete an ARP entry on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf ip-address {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf delete-arp-entry-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc disable-stp {
 *     "Disable STP on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         leaf disable-stp-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc enable-stp {
 *     "Enable STP on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         leaf enable-stp-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-arp-entry {
 *     "Get an ARP entry on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf ip-address {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf ip-address {
 *             type string;
 *         }
 *         leaf mac-address {
 *             type int64;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-arp-table {
 *     "Get the ARP table on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         list arp-table-entry {
 *             key     leaf ip-address {
 *                 type string;
 *             }
 *             leaf mac-address {
 *                 type int64;
 *             }
 *             uses arp-entry;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-stp-port-root {
 *     "Get a port's STP root";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf port {
 *             type int16;
 *         }
 *     }
 *     
 *     output {
 *         leaf root-node-id {
 *             type int64;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-stp-port-state {
 *     "Get a port's STP state";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf port {
 *             type int16;
 *         }
 *     }
 *     
 *     output {
 *         leaf stp-port-state {
 *             type stp-port-state;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc set-arp-entry {
 *     "Set an ARP entry on a switch";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf ip-address {
 *             type string;
 *         }
 *         leaf mac-address {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         leaf set-arp-entry-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc set-stp-port-state {
 *     "Set a port's STP state";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf port {
 *             type int16;
 *         }
 *         leaf enable {
 *             type boolean;
 *         }
 *     }
 *     
 *     output {
 *         leaf set-stp-port-state-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * </pre>
 */
public interface ConfigService
    extends
    RpcService
{




    /**
     * Delete an ARP entry on a switch
     */
    Future<RpcResult<DeleteArpEntryOutput>> deleteArpEntry(DeleteArpEntryInput input);
    
    /**
     * Disable STP on a switch
     */
    Future<RpcResult<DisableStpOutput>> disableStp(DisableStpInput input);
    
    /**
     * Enable STP on a switch
     */
    Future<RpcResult<EnableStpOutput>> enableStp(EnableStpInput input);
    
    /**
     * Get an ARP entry on a switch
     */
    Future<RpcResult<GetArpEntryOutput>> getArpEntry(GetArpEntryInput input);
    
    /**
     * Get the ARP table on a switch
     */
    Future<RpcResult<GetArpTableOutput>> getArpTable(GetArpTableInput input);
    
    /**
     * Get a port's STP root
     */
    Future<RpcResult<GetStpPortRootOutput>> getStpPortRoot(GetStpPortRootInput input);
    
    /**
     * Get a port's STP state
     */
    Future<RpcResult<GetStpPortStateOutput>> getStpPortState(GetStpPortStateInput input);
    
    /**
     * Set an ARP entry on a switch
     */
    Future<RpcResult<SetArpEntryOutput>> setArpEntry(SetArpEntryInput input);
    
    /**
     * Set a port's STP state
     */
    Future<RpcResult<SetStpPortStateOutput>> setStpPortState(SetStpPortStateInput input);

}

