/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.topology.rev150901.*;

import org.opendaylight.snmp4sdn.DiscoveryServiceAPI;

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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopologyImpl implements TopologyService, CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(TopologyImpl.class);

    private DiscoveryServiceAPI service = null;

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    public void init() {
        registerWithOSGIConsole();
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    public void setDiscoveryService(DiscoveryServiceAPI service) {
        this.service = service;
    }

    public void unsetDiscoveryService(DiscoveryServiceAPI service) {
        if (this.service == service) {
            this.service = null;
        }
    }

    private Future<RpcResult<RediscoverOutput>> createRediscoverRpcFailResult(){
        RediscoverOutputBuilder ob = new RediscoverOutputBuilder().setRediscoverResult(Result.FAIL);
        RpcResult<RediscoverOutput> rpcResult =
                    Rpcs.<RediscoverOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    private Future<RpcResult<SetPeriodicTopologyDiscoveryIntervalOutput>> createSetPeriodicTopologyDiscoveryIntervalRpcFailResult(){
        SetPeriodicTopologyDiscoveryIntervalOutputBuilder ob = new SetPeriodicTopologyDiscoveryIntervalOutputBuilder().setSetPeriodicTopologyDiscoveryIntervalResult(Result.FAIL);
        RpcResult<SetPeriodicTopologyDiscoveryIntervalOutput> rpcResult =
                    Rpcs.<SetPeriodicTopologyDiscoveryIntervalOutput> getRpcResult(false, ob.build(),
                            Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    @Override//md-sal
    public Future<RpcResult<RediscoverOutput>> rediscover(){

        logger.info("SNMP4SDN: Receive the Topology Discovery request!");

        boolean isSuccess = service.doTopologyDiscovery();
        if(isSuccess){
            RediscoverOutputBuilder ob = new RediscoverOutputBuilder().setRediscoverResult(Result.SUCCESS);
            RpcResult<RediscoverOutput> rpcResult =
                    Rpcs.<RediscoverOutput> getRpcResult(true, ob.build(),
                            Collections.<RpcError> emptySet());
            return Futures.immediateFuture(rpcResult);
        }
        else{
            logger.debug("ERROR: rediscover(): call DiscoveryService.doTopologyDiscovery() fail");
            return createRediscoverRpcFailResult();
        }
    }

    @Override//md-sal
    public Future<RpcResult<SetPeriodicTopologyDiscoveryIntervalOutput>> setPeriodicTopologyDiscoveryInterval(SetPeriodicTopologyDiscoveryIntervalInput input){

        //check null input parameters
        if(input == null){
            logger.debug("ERROR: setPeriodicTopologyDiscoveryInterval(): given null input");
            return createSetPeriodicTopologyDiscoveryIntervalRpcFailResult();
        }
        Integer interval = input.getIntervalSecond();
        if(interval == null){
            logger.debug("ERROR: setPeriodicTopologyDiscoveryInterval(): given interval is null");
            return createSetPeriodicTopologyDiscoveryIntervalRpcFailResult();
        }

        //parameters checking
        if(interval < 0){
            logger.debug("ERROR: setPeriodicTopologyDiscoveryInterval(): given invalid interval {}", interval);
            return createSetPeriodicTopologyDiscoveryIntervalRpcFailResult();
        }

        //set the interval, return success
        service.setPeriodicTopologyDiscoveryIntervalTime(interval.intValue());

        logger.info("SNMP4SDN: Periodic Topology Discovery interval time has been set as {} second", interval);

        SetPeriodicTopologyDiscoveryIntervalOutputBuilder ob = new SetPeriodicTopologyDiscoveryIntervalOutputBuilder().setSetPeriodicTopologyDiscoveryIntervalResult(Result.SUCCESS);
        RpcResult<SetPeriodicTopologyDiscoveryIntervalOutput> rpcResult =
                Rpcs.<SetPeriodicTopologyDiscoveryIntervalOutput> getRpcResult(true, ob.build(),
                Collections.<RpcError> emptySet());
        return Futures.immediateFuture(rpcResult);
    }

    //TODO: OSGi test command

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---SNMP4SDN TopologyImpl---\n");
        return help.toString();
    }


}
