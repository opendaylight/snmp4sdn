package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.GetVlanTableInput;
import java.util.concurrent.Future;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.DeleteVlanOutput;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.PrintVlanTableInput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.GetVlanTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.DeleteVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.SetVlanPortsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.SetVlanPortsOutput;


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
 * rpc add-vlan-and-set-ports {
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
 *         leaf tagged-port-list {
 *             type string;
 *         }
 *         leaf untagged-port-list {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf add-vlan-and-set-ports-result {
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
 * rpc get-vlan-table {
 *     "get vlan table";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         list vlan-table-entry {
 *             key     leaf vlan-id {
 *                 type int32;
 *             }
 *             leaf vlan-name {
 *                 type string;
 *             }
 *             leaf-list port-list {
 *                 type int16;
 *             }
 *             uses vlan-entry;
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
 *         leaf tagged-port-list {
 *             type string;
 *         }
 *         leaf untagged-port-list {
 *             type string;
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
     * add vlan
     */
    Future<RpcResult<AddVlanAndSetPortsOutput>> addVlanAndSetPorts(AddVlanAndSetPortsInput input);
    
    /**
     * delete vlan
     */
    Future<RpcResult<DeleteVlanOutput>> deleteVlan(DeleteVlanInput input);
    
    /**
     * get vlan table
     */
    Future<RpcResult<GetVlanTableOutput>> getVlanTable(GetVlanTableInput input);
    
    /**
     * print vlan table
     */
    Future<RpcResult<java.lang.Void>> printVlanTable(PrintVlanTableInput input);
    
    /**
     * set vlan ports
     */
    Future<RpcResult<SetVlanPortsOutput>> setVlanPorts(SetVlanPortsInput input);

}

