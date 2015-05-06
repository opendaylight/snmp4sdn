package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import java.util.concurrent.Future;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryInput;
import org.opendaylight.yangtools.yang.binding.RpcService;


/**
**/
public interface FdbService
    extends
    RpcService
{




    /**
    **/
    Future<RpcResult<DelFdbEntryOutput>> delFdbEntry(DelFdbEntryInput input);
    
    /**
    **/
    Future<RpcResult<GetFdbEntryOutput>> getFdbEntry(GetFdbEntryInput input);
    
    /**
    **/
    Future<RpcResult<GetFdbTableOutput>> getFdbTable(GetFdbTableInput input);
    
    /**
    **/
    Future<RpcResult<SetFdbEntryOutput>> setFdbEntry(SetFdbEntryInput input);

}

