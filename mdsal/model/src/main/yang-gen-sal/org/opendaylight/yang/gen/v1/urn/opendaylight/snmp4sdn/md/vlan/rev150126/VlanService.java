package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.PrintVlanTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput;
import org.opendaylight.yangtools.yang.binding.RpcService;


/**
**/
public interface VlanService
    extends
    RpcService
{




    /**
    **/
    Future<RpcResult<AddVlanOutput>> addVlan(AddVlanInput input);
    
    /**
    **/
    Future<RpcResult<DeleteVlanOutput>> deleteVlan(DeleteVlanInput input);
    
    /**
    **/
    Future<RpcResult<GetVlanPortsOutput>> getVlanPorts(GetVlanPortsInput input);
    
    /**
    **/
    Future<RpcResult<java.lang.Void>> printVlanTable(PrintVlanTableInput input);
    
    /**
    **/
    Future<RpcResult<SetVlanPortsOutput>> setVlanPorts(SetVlanPortsInput input);

}

