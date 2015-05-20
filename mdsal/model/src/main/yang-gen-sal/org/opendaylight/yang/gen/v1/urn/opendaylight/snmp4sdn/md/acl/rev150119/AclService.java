package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableInput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * rpc clear-acl-table {
 *     "Clear ACL table on the switch";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         leaf clear-acl-table-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc create-acl-profile {
 *     "Create an ACL profile on the switch";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *         leaf acl-layer {
 *             type acl-layer;
 *         }
 *         leaf vlan-mask {
 *             type int16;
 *         }
 *         leaf src-ip-mask {
 *             type string;
 *         }
 *         leaf dst-ip-mask {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf create-acl-profile-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc del-acl-profile {
 *     "Delete ACL profile on the switch";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf del-acl-profile-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc del-acl-rule {
 *     "Delete ACL rule";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *         leaf rule-id {
 *             type int32;
 *         }
 *         leaf rule-name {
 *             type string;
 *         }
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *     }
 *     
 *     output {
 *         leaf del-acl-rule-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc set-acl-rule {
 *     "Set ACL rule";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *         leaf rule-id {
 *             type int32;
 *         }
 *         leaf rule-name {
 *             type string;
 *         }
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *         leaf-list port-list {
 *             type int16;
 *         }
 *         leaf acl-layer {
 *             type acl-layer;
 *         }
 *         leaf vlan-id {
 *             type int32;
 *         }
 *         leaf src-ip {
 *             type string;
 *         }
 *         leaf dst-ip {
 *             type string;
 *         }
 *         leaf acl-action {
 *             type acl-action;
 *         }
 *     }
 *     
 *     output {
 *         leaf set-acl-rule-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * </pre>
 */
public interface AclService
    extends
    RpcService
{




    /**
     * Clear ACL table on the switch
     */
    Future<RpcResult<ClearAclTableOutput>> clearAclTable(ClearAclTableInput input);
    
    /**
     * Create an ACL profile on the switch
     */
    Future<RpcResult<CreateAclProfileOutput>> createAclProfile(CreateAclProfileInput input);
    
    /**
     * Delete ACL profile on the switch
     */
    Future<RpcResult<DelAclProfileOutput>> delAclProfile(DelAclProfileInput input);
    
    /**
     * Delete ACL rule
     */
    Future<RpcResult<DelAclRuleOutput>> delAclRule(DelAclRuleInput input);
    
    /**
     * Set ACL rule
     */
    Future<RpcResult<SetAclRuleOutput>> setAclRule(SetAclRuleInput input);

}

