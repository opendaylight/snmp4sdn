package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput;
import org.opendaylight.yangtools.yang.binding.RpcService;


/**
**/
public interface AclService
    extends
    RpcService
{




    /**
    **/
    Future<RpcResult<ClearAclTableOutput>> clearAclTable(ClearAclTableInput input);
    
    /**
    **/
    Future<RpcResult<CreateAclProfileOutput>> createAclProfile(CreateAclProfileInput input);
    
    /**
    **/
    Future<RpcResult<DelAclProfileOutput>> delAclProfile(DelAclProfileInput input);
    
    /**
    **/
    Future<RpcResult<DelAclRuleOutput>> delAclRule(DelAclRuleInput input);
    
    /**
    **/
    Future<RpcResult<SetAclRuleOutput>> setAclRule(SetAclRuleInput input);

}

