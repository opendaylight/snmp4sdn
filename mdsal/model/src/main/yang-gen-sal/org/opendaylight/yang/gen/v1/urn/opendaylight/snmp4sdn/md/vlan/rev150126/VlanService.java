package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.concurrent.Future;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.PrintVlanTableInput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsInput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * rpc add-vlan {
 *     "add vlan";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *         leaf vlanName {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf add-vlan-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc delete-vlan {
 *     "delete vlan";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *     }
 *     
 *     output {
 *         leaf delete-vlan-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-vlan-ports {
 *     "Get vlan ports";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *     }
 *     
 *     output {
 *         list get-port-list-entry {
 *             key     leaf port {
 *                 type int16;
 *             }
 *             uses vlan-port;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc print-vlan-table {
 *     "print vlan table";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     status CURRENT;
 * }
 * rpc set-vlan-ports {
 *     "set vlan ports";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *         list set-port-list-entry {
 *             key     leaf port {
 *                 type int16;
 *             }
 *             uses vlan-port;
 *         }
 *     }
 *     
 *     output {
 *         leaf set-vlan-ports-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * </pre>
 */
public interface VlanService
    extends
    RpcService
{




    /**
     * add vlan
     */
    Future<RpcResult<AddVlanOutput>> addVlan(AddVlanInput input);
    
    /**
     * delete vlan
     */
    Future<RpcResult<DeleteVlanOutput>> deleteVlan(DeleteVlanInput input);
    
    /**
     * Get vlan ports
     */
    Future<RpcResult<GetVlanPortsOutput>> getVlanPorts(GetVlanPortsInput input);
    
    /**
     * print vlan table
     */
    Future<RpcResult<java.lang.Void>> printVlanTable(PrintVlanTableInput input);
    
    /**
     * set vlan ports
     */
    Future<RpcResult<SetVlanPortsOutput>> setVlanPorts(SetVlanPortsInput input);

}

