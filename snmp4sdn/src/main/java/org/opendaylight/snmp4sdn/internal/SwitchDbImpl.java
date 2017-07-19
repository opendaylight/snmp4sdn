/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.opendaylight.snmp4sdn.internal.util.CommandInterpreter;
import org.opendaylight.snmp4sdn.internal.util.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

//md-sal
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.switchdb.rev150901.*;

//For md-sal RPC call
import org.opendaylight.controller.sal.common.util.Rpcs;
import java.util.Collections;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
//import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

//TODO: com.google.common import error in karaf
/*import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;*/

import org.opendaylight.snmp4sdn.core.internal.Controller;
import org.opendaylight.snmp4sdn.core.IController;
import org.opendaylight.snmp4sdn.internal.CLIHandler;
import org.opendaylight.snmp4sdn.internal.SNMPHandler;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.opendaylight.snmp4sdn.protocol.util.HexString;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Vector;

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
        ReloadDbOutputBuilder ob = new ReloadDbOutputBuilder().setReloadDbResult(Result.FAIL);
        RpcResult<ReloadDbOutput> rpcResult =
                    Rpcs.<ReloadDbOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<ReloadDbOutput>> reloadDb(){
        boolean isSuccess = cmethUtil.readDB();
        if(isSuccess){
            ReloadDbOutputBuilder ob = new ReloadDbOutputBuilder().setReloadDbResult(Result.SUCCESS);
            RpcResult<ReloadDbOutput> rpcResult =
                    Rpcs.<ReloadDbOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: reloadDb(): call cmethUtil.readDB() fail");
            return createReloadDbRpcResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<AddSwitchEntryOutput>> addSwitchEntry(AddSwitchEntryInput input){
            AddSwitchEntryOutputBuilder ob = new AddSwitchEntryOutputBuilder().setAddSwitchEntryResult(Result.SUCCESS);
            RpcResult<AddSwitchEntryOutput> rpcResult =
                    Rpcs.<AddSwitchEntryOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
    }
    
    @Override//md-sal
    public Future<RpcResult<DeleteSwitchEntryOutput>> deleteSwitchEntry(DeleteSwitchEntryInput input){
            DeleteSwitchEntryOutputBuilder ob = new DeleteSwitchEntryOutputBuilder().setDeleteSwitchEntryResult(Result.SUCCESS);
            RpcResult<DeleteSwitchEntryOutput> rpcResult =
                    Rpcs.<DeleteSwitchEntryOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<ClearDbOutput>> clearDb(){
            ClearDbOutputBuilder ob = new ClearDbOutputBuilder().setClearDbResult(Result.SUCCESS);
            RpcResult<ClearDbOutput> rpcResult =
                    Rpcs.<ClearDbOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<UpdateDbOutput>> updateDb(UpdateDbInput input){
            UpdateDbOutputBuilder ob = new UpdateDbOutputBuilder().setUpdateDbResult(Result.SUCCESS);
            RpcResult<UpdateDbOutput> rpcResult =
                    Rpcs.<UpdateDbOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
    }


    //TODO: OSGi test command

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---SNMP4SDN SwitchDbImpl---\n");
        return help.toString();
    }


}
