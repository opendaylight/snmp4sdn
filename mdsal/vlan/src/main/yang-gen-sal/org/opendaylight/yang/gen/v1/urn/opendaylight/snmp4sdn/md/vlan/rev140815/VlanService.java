package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.PrintVlanTableInput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.DeleteVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>vlan</b>
 * <br />(Source path: <i>META-INF\yang\vlan.yang</i>):
 * <pre>
 * rpc add-vlan {
 *     "add vlan";
 *     input {
 *         leaf nodeID {
 *             type int64;
 *         }
 *         leaf vlanID {
 *             type int32;
 *         }
 *         leaf vlanName {
 *             type string;
 *         }
 *     }
 *     
 *     status CURRENT;
 * }
 * rpc delete-vlan {
 *     "delete vlan";
 *     input {
 *         leaf nodeID {
 *             type int64;
 *         }
 *         leaf vlanID {
 *             type int32;
 *         }
 *     }
 *     
 *     status CURRENT;
 * }
 * rpc print-vlan-table {
 *     "print vlan table";
 *     input {
 *         leaf nodeID {
 *             type int64;
 *         }
 *     }
 *     
 *     status CURRENT;
 * }
 * rpc set-vlan-ports {
 *     "set vlan ports";
 *     input {
 *         leaf nodeID {
 *             type int64;
 *         }
 *         leaf vlanID {
 *             type int32;
 *         }
 *         leaf portList {
 *             type string;
 *         }
 *     }
 *     
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
    Future<RpcResult<java.lang.Void>> addVlan(AddVlanInput input);
    
    /**
     * delete vlan
     */
    Future<RpcResult<java.lang.Void>> deleteVlan(DeleteVlanInput input);
    
    /**
     * print vlan table
     */
    Future<RpcResult<java.lang.Void>> printVlanTable(PrintVlanTableInput input);
    
    /**
     * set vlan ports
     */
    Future<RpcResult<java.lang.Void>> setVlanPorts(SetVlanPortsInput input);

}

