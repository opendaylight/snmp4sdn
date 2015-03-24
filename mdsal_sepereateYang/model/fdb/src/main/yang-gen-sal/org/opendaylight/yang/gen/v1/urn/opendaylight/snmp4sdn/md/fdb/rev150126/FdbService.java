package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryInput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>fdb</b>
 * <br />(Source path: <i>META-INF\yang\fdb.yang</i>):
 * <pre>
 * rpc del-fdb-entry {
 *     "Delete FDB entry";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf dest-mac-addr {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *     }
 *     
 *     output {
 *         leaf del-fdb-entry-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-fdb-entry {
 *     "Get FDB entry";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf dest-mac-addr {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *     }
 *     
 *     output {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf dest-mac-addr {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *         leaf port {
 *             type int16;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc get-fdb-table {
 *     "Get FDB table";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         list fdb-table-entry {
 *             key     leaf node-id {
 *                 type int64;
 *             }
 *             leaf dest-mac-addr {
 *                 type int64;
 *             }
 *             leaf vlan-id {
 *                 type int32;
 *             }
 *             leaf port {
 *                 type int16;
 *             }
 *             uses fdb-entry;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc set-fdb-entry {
 *     "Set FDB entry";
 *     input {
 *         leaf node-id {
 *             type int64;
 *         }
 *         leaf dest-mac-addr {
 *             type int64;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *         leaf port {
 *             type int16;
 *         }
 *     }
 *     
 *     output {
 *         leaf set-fdb-entry-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * </pre>
 */
public interface FdbService
    extends
    RpcService
{




    /**
     * Delete FDB entry
     */
    Future<RpcResult<DelFdbEntryOutput>> delFdbEntry(DelFdbEntryInput input);
    
    /**
     * Get FDB entry
     */
    Future<RpcResult<GetFdbEntryOutput>> getFdbEntry(GetFdbEntryInput input);
    
    /**
     * Get FDB table
     */
    Future<RpcResult<GetFdbTableOutput>> getFdbTable(GetFdbTableInput input);
    
    /**
     * Set FDB entry
     */
    Future<RpcResult<SetFdbEntryOutput>> setFdbEntry(SetFdbEntryInput input);

}

