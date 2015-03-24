package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput;


/**
 * Interface for implementing the following YANG RPCs defined in module <b>acl</b>
 * <br />(Source path: <i>META-INF\yang\acl.yang</i>):
 * <pre>
 * rpc add-acl-rule {
 *     "Add ACL rule";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf rule-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *         leaf rule-name {
 *             type string;
 *         }
 *         leaf-list port-list {
 *             type int16;
 *         }
 *         leaf src-ip {
 *             type string;
 *         }
 *         leaf dst-ip {
 *             type string;
 *         }
 *         leaf ctrl-vid {
 *             type int32;
 *         }
 *         leaf acl-action {
 *             type acl-action;
 *         }
 *     }
 *     
 *     output {
 *         leaf add-acl-rule-result {
 *             type result;
 *         }
 *     }
 *     status CURRENT;
 * }
 * rpc create-acl-profile {
 *     "(2)	Create an ACL profile on the switch";
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
 *             type int32;
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
 *         leaf profile-id {
 *             type int32;
 *         }
 *         leaf rule-id {
 *             type int32;
 *         }
 *         leaf profile-name {
 *             type string;
 *         }
 *         leaf rule-name {
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
 * rpc get-acl-index-on-switch {
 *     "Get the rule IDs in each of ACL profiles on the switch";
 *     input {
 *         leaf nodeId {
 *             type int64;
 *         }
 *     }
 *     
 *     output {
 *         list acl-index {
 *             key     leaf profile-id {
 *                 type int32;
 *             }
 *             leaf rule-id {
 *                 type int32;
 *             }
 *             leaf profile-name {
 *                 type string;
 *             }
 *             leaf rule-name {
 *                 type string;
 *             }
 *             uses acl-index;
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
     * Add ACL rule
     */
    Future<RpcResult<AddAclRuleOutput>> addAclRule(AddAclRuleInput input);
    
    /**
     * (2)Create an ACL profile on the switch
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
     * Get the rule IDs in each of ACL profiles on the switch
     */
    Future<RpcResult<GetAclIndexOnSwitchOutput>> getAclIndexOnSwitch(GetAclIndexOnSwitchInput input);

}

