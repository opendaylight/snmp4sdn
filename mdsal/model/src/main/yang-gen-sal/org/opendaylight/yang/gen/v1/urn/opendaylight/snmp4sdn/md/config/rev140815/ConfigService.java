package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DisableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput;
import org.opendaylight.yangtools.yang.binding.RpcService;


/**
**/
public interface ConfigService
    extends
    RpcService
{




    /**
    **/
    Future<RpcResult<DeleteArpEntryOutput>> deleteArpEntry(DeleteArpEntryInput input);
    
    /**
    **/
    Future<RpcResult<DisableStpOutput>> disableStp(DisableStpInput input);
    
    /**
    **/
    Future<RpcResult<EnableStpOutput>> enableStp(EnableStpInput input);
    
    /**
    **/
    Future<RpcResult<GetArpEntryOutput>> getArpEntry(GetArpEntryInput input);
    
    /**
    **/
    Future<RpcResult<GetArpTableOutput>> getArpTable(GetArpTableInput input);
    
    /**
    **/
    Future<RpcResult<GetStpPortRootOutput>> getStpPortRoot(GetStpPortRootInput input);
    
    /**
    **/
    Future<RpcResult<GetStpPortStateOutput>> getStpPortState(GetStpPortStateInput input);
    
    /**
    **/
    Future<RpcResult<SetArpEntryOutput>> setArpEntry(SetArpEntryInput input);
    
    /**
    **/
    Future<RpcResult<SetStpPortStateOutput>> setStpPortState(SetStpPortStateInput input);

}

