/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.util.concurrent.Future;
import org.opendaylight.snmp4sdn.core.IController;

//TODO: com.google.common import error in karaf
/*import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.internal.util.CommandProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.AddSwitchEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.AddSwitchEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.AddSwitchEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.ClearDbOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.ClearDbOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.DeleteSwitchEntryInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.DeleteSwitchEntryOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.DeleteSwitchEntryOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.ReloadDbOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.ReloadDbOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.SwitchDbService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.UpdateDbInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.UpdateDbOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.UpdateDbOutputBuilder;
//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yangtools.yang.common.RpcResult;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SwitchDbImpl implements SwitchDbService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(SwitchDbImpl.class);

    public boolean isDummy = false;

    private Controller controller = null;
    private CmethUtil cmethUtil = null;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        //registerWithOSGIConsole();
    }

    /*private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }*/

    public void setController(IController core) {
        this.controller = (Controller)core;
        cmethUtil = controller.cmethUtil;//s4s add
    }

    public void unsetController(IController core) {
        if (this.controller == (Controller)core) {
            this.controller = null;
        }
    }

    private Future<RpcResult<ReloadDbOutput>> createReloadDbRpcResult(){
        return RpcResultBuilder.<ReloadDbOutput>failed().buildFuture();
    }

    @Override//md-sal
    public Future<RpcResult<ReloadDbOutput>> reloadDb(){
        boolean isSuccess = cmethUtil.readDB();
        if(isSuccess){
            ReloadDbOutputBuilder ob = new ReloadDbOutputBuilder().setReloadDbResult(Result.SUCCESS);
            return RpcResultBuilder.<ReloadDbOutput>success(ob.build()).buildFuture();
        }
        else{
            logger.debug("ERROR: reloadDb(): call cmethUtil.readDB() fail");
            return createReloadDbRpcResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<AddSwitchEntryOutput>> addSwitchEntry(AddSwitchEntryInput input){
            AddSwitchEntryOutputBuilder ob = new AddSwitchEntryOutputBuilder().setAddSwitchEntryResult(Result.SUCCESS);
            return RpcResultBuilder.<AddSwitchEntryOutput>success(ob.build()).buildFuture();
    }

    @Override//md-sal
    public Future<RpcResult<DeleteSwitchEntryOutput>> deleteSwitchEntry(DeleteSwitchEntryInput input){
            DeleteSwitchEntryOutputBuilder ob = new DeleteSwitchEntryOutputBuilder().setDeleteSwitchEntryResult(Result.SUCCESS);
            return RpcResultBuilder.<DeleteSwitchEntryOutput>success(ob.build()).buildFuture();
    }

    @Override//md-sal
    public Future<RpcResult<ClearDbOutput>> clearDb(){
            ClearDbOutputBuilder ob = new ClearDbOutputBuilder().setClearDbResult(Result.SUCCESS);
            return RpcResultBuilder.<ClearDbOutput>success(ob.build()).buildFuture();
    }

    @Override//md-sal
    public Future<RpcResult<UpdateDbOutput>> updateDb(UpdateDbInput input){
            UpdateDbOutputBuilder ob = new UpdateDbOutputBuilder().setUpdateDbResult(Result.SUCCESS);
            return RpcResultBuilder.<UpdateDbOutput>success(ob.build()).buildFuture();
    }


    //TODO: OSGi test command

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---SNMP4SDN SwitchDbImpl---\n");
        return help.toString();
    }


}
