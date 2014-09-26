/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code base of OpenFlow plugin contributed by Cisco Systems, Inc. Their efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.internal;//s4s

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.net.InetAddress;//s4s add

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.ActionType;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.snmp4sdn.IKarafFlowProgrammerService;//karaf
import org.opendaylight.snmp4sdn.IFlowProgrammerNotifier;//s4s
import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;//s4s
import org.opendaylight.snmp4sdn.core.IController;//s4s
//import org.opendaylight.controller.protocol_plugin.openflow.core.IMessageListener;//s4s //receive()
import org.opendaylight.snmp4sdn.core.ISwitch;//s4s
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.ContainerFlow;
import org.opendaylight.controller.sal.core.IContainerAware;
import org.opendaylight.controller.sal.core.IContainerListener;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.Node.NodeIDType;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IPluginInFlowProgrammerService;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchField;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.utils.HexEncode;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.openflow.protocol.OFError;
import org.openflow.util.HexString;//s4s add
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.snmp4sdn.protocol.SNMPFlowMod;//s4s add
import org.opendaylight.snmp4sdn.protocol.SNMPMessage;//s4s add
//import org.opendaylight.snmp4sdn.internal.SNMPHandler;//s4s add

/*
//s4s add
import snmp.SNMPInteger;
import snmp.SNMPObject;
import snmp.SNMPOctetString;
import snmp.SNMPVarBindList;
import snmp.SNMPv1CommunicationInterface;
*/
import org.opendaylight.snmp4sdn.internal.SNMPHandler;//s4s add


/**
 * Represents the snmp4sdn plugin component in charge of programming the flows
 * the flow programming and relay them to functional modules above SAL.
 */
public class FlowProgrammerService implements IPluginInFlowProgrammerService,
        /*IMessageListener,*/ IContainerListener, IInventoryShimExternalListener,//s4s actully IInventoryShimExternalListener seems useless
        CommandProvider, IContainerAware, IKarafFlowProgrammerService {
    private static final Logger log = LoggerFactory
            .getLogger(FlowProgrammerService.class);
    private IController controller;
    private ConcurrentMap<String, IFlowProgrammerNotifier> flowProgrammerNotifiers;
    private final Map<String, Set<NodeConnector>> containerToNc;
    private final ConcurrentMap<Long, Map<Integer, Long>> xid2rid;
    private final int barrierMessagePriorCount = getBarrierMessagePriorCount();//s4s. useless in my plugin. may delete

    public FlowProgrammerService() {
        controller = null;
        flowProgrammerNotifiers = new ConcurrentHashMap<String, IFlowProgrammerNotifier>();
        containerToNc = new HashMap<String, Set<NodeConnector>>();
        xid2rid = new ConcurrentHashMap<Long, Map<Integer, Long>>();
    }

    public void setController(IController core) {
        this.controller = core;
    }

    public void unsetController(IController core) {
        if (this.controller == core) {
            this.controller = null;
        }
    }

//s4s mark 10
    public void setFlowProgrammerNotifier(Map<String, ?> props,
            IFlowProgrammerNotifier s) {
        if (props == null || props.get("containerName") == null) {
            log.warn("Didn't receive the service correct properties");
            return;
        }
        String containerName = (String) props.get("containerName");
        this.flowProgrammerNotifiers.put(containerName, s);
    }
//end of mark 10

//s4s mark 11
    public void unsetFlowProgrammerNotifier(Map<String, ?> props,
            IFlowProgrammerNotifier s) {
        if (props == null || props.get("containerName") == null) {
            log.warn("Didn't receive the service correct properties");
            return;
        }
        String containerName = (String) props.get("containerName");
        if (this.flowProgrammerNotifiers != null
                && this.flowProgrammerNotifiers.containsKey(containerName)
                && this.flowProgrammerNotifiers.get(containerName) == s) {
            this.flowProgrammerNotifiers.remove(containerName);
        }
    }
//end of mark 11

    /**
     * Function called by the dependency manager when all the required
     * dependencies are satisfied
     *
     */
    void init() {
        //this.controller.addMessageListener(OFType.FLOW_REMOVED, this);//s4s
        //this.controller.addMessageListener(OFType.ERROR, this);//s4s
        registerWithOSGIConsole();
    }

    /**
     * Function called by the dependency manager when at least one dependency
     * become unsatisfied or when the component is shutting down because for
     * example bundle is being stopped.
     *
     */
    void destroy() {
    }

    /**
     * Function called by dependency manager after "init ()" is called and after
     * the services provided by the class are registered in the service registry
     *
     */
    void start() {
    }

    /**
     * Function called by the dependency manager before the services exported by
     * the component are unregistered, this will be followed by a "destroy ()"
     * calls
     *
     */
    void stop() {
    }

    @Override
    public Status addFlow(Node node, Flow flow) {
        log.info("enter FlowProgrammerService.addFlow()");
        return addFlowInternal(node, flow, 0);//OF's
        //return my_modifyFlow(node, flow, 3); //s4s. modType 3 is type of 3, means "learned"
    }

    @Override
    public Status modifyFlow(Node node, Flow oldFlow, Flow newFlow) {
        log.info("enter FlowProgrammerService.modifyFlow()");
        return modifyFlowInternal(node, oldFlow, newFlow, 0);
        //return my_modifyFlow(node, flow, 3); //s4s. modType 3 is type of 3, means "learned"
    }

    @Override
    public Status removeFlow(Node node, Flow flow) {
        log.info("enter FlowProgrammerService.removeFlow()");
        return removeFlowInternal(node, flow, 0);
        //return my_modifyFlow(node, flow, 2); //s4s. modType 2 is type of 2, means "invalid(delete)"
    }

    @Override
    public Status addFlowAsync(Node node, Flow flow, long rid) {
        log.info("enter FlowProgrammerService.addFlowAsync()");
        return addFlowInternal(node, flow, rid);//s4s
        //return new Status(StatusCode.SUCCESS);
    }

    @Override
    public Status modifyFlowAsync(Node node, Flow oldFlow, Flow newFlow, long rid) {
        log.info("enter FlowProgrammerService.modifyFlowAsync()");
        return modifyFlowInternal(node, oldFlow, newFlow, rid);
    }

    @Override
    public Status removeFlowAsync(Node node, Flow flow, long rid) {
        log.info("enter FlowProgrammerService.removeFlowAsync()");
        return removeFlowInternal(node, flow, rid);
    }

//s4s mark 1
    private Status addFlowInternal(Node node, Flow flow, long rid) {
        String action = "add";
        if (!node.getType().equals("SNMP")) {//s4s.  Original OF's code: NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE, errorString("send",
                    action, "Invalid node type"));
        }

        if (controller != null) {
            ISwitch sw = controller.getSwitch((Long) node.getID());
            if (sw != null) {
                SNMPMessage msg = new SNMPFlowMod(SNMPFlowMod.ETHPFC_ADD, flow.clone());//s4s
                msg.setTargetSwitchID((Long) node.getID());//s4s

                Object result;
                if (rid == 0) {
                    // *
                    // * Synchronous message send. Each message is followed by a
                    // * Barrier message.
                    // * /
                    result = sw.syncSend(msg);

                    //to directly call snmp:
                    /*SNMPFlowMod msgMod = (SNMPFlowMod)msg;
                    new SNMPHandler().sendBySNMP(msgMod.getFlow(), msgMod.getCommand(), msg.getTargetSwitchID());
                    result = null;*/
                } else {
                    // *
                    // * Message will be sent asynchronously. A Barrier message
                    // * will be inserted automatically to synchronize the
                    // * progression.
                    // * /
                    result = asyncMsgSend(node, sw, msg, rid);
                }
                return getStatusInternal(result, action, rid);
            } else {
                return new Status(StatusCode.GONE, errorString("send", action,
                        "Switch is not available"));
            }
        }
        return new Status(StatusCode.INTERNALERROR, errorString("send", action,
                "Internal plugin error"));
    }
//enf of mark 1

//s4s mark 2
    private Status modifyFlowInternal(Node node, Flow oldFlow, Flow newFlow, long rid) {
        String action = "modify";
        if (!node.getType().equals("SNMP")) {//s4s.  Original OF's code: NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE, errorString("send",
                    action, "Invalid node type"));
        }
        if (controller != null) {
            ISwitch sw = controller.getSwitch((Long) node.getID());
            if (sw != null) {
                /* //OF's code require Flow converted to OFMessage; similarly, snmp4sdn use SNMPMessage
                OFMessage msg1 = null, msg2 = null;

                // If priority and match portion are the same, send a
                // modification message
                if (oldFlow.getPriority() != newFlow.getPriority()
                        || !oldFlow.getMatch().equals(newFlow.getMatch())) {
                    msg1 = new FlowConverter(oldFlow).getOFFlowMod(
                            OFFlowMod.OFPFC_DELETE_STRICT, OFPort.OFPP_NONE);
                    msg2 = new FlowConverter(newFlow).getOFFlowMod(
                            OFFlowMod.OFPFC_ADD, null);
                } else {
                    msg1 = new FlowConverter(newFlow).getOFFlowMod(
                            OFFlowMod.OFPFC_MODIFY_STRICT, null);
                }
                */

                //s4s's work, similar to OF's work: 'check data correctness' and 'type convertion'
                if(!checkSameSrcDest(oldFlow, newFlow)){
                    return new Status(StatusCode.NOTACCEPTABLE, errorString("send", action,
                            "Inconsistency of oldFlow and newFlow (src/dest mac inconsistent), or flow's action OUTPUT port not set"));
                }
                //SNMPMessage msg1 = null, msg2 = null;//s4s. msg2 is useless in cmeth's code
                SNMPMessage msg1 = new SNMPFlowMod(SNMPFlowMod.ETHPFC_MODIFY, newFlow.clone());//s4s
                msg1.setTargetSwitchID((Long) node.getID());//s4s

                // *
                // * Synchronous message send
                // * /
                //action = (msg2 == null) ? "modify" : "delete";//s4s. OF's code
                Object result;
                if (rid == 0) {
                    //*
                    // * Synchronous message send. Each message is followed by a
                    // * Barrier message.
                    // * /
                    result = sw.syncSend(msg1);
                } else {
                    // *
                    // * Message will be sent asynchronously. A Barrier message
                    // * will be inserted automatically to synchronize the
                    // * progression.
                    // * /
                    result = asyncMsgSend(node, sw, msg1, rid);
                }

                Status rv = getStatusInternal(result, action, rid);
                if (/*(msg2 == null) || */!rv.isSuccess()) {
                    return rv;
                }

                /* //s4s. OF's code
                action = "add";
                if (rid == 0) {
                    // *
                    // * Synchronous message send. Each message is followed by a
                    // * Barrier message.
                    // * /
                    result = sw.syncSend(msg2);
                } else {
                    // *
                    // * Message will be sent asynchronously. A Barrier message
                    // * will be inserted automatically to synchronize the
                    // * progression.
                    // * /
                    result = asyncMsgSend(node, sw, msg2, rid);
                }
                */
                return getStatusInternal(result, action, rid);
            } else {
                return new Status(StatusCode.GONE, errorString("send", action,
                        "Switch is not available"));
            }
        }
        return new Status(StatusCode.INTERNALERROR, errorString("send", action,
                "Internal plugin error"));
    }
//end of mark 2

//s4s mark 3
    private Status removeFlowInternal(Node node, Flow flow, long rid) {
        String action = "remove";
        if (!node.getType().equals("SNMP")) {//s4s.  Original OF's code: NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE, errorString("send",
                    action, "Invalid node type"));
        }
        if (controller != null) {
            ISwitch sw = controller.getSwitch((Long) node.getID());
            if (sw != null) {
                /*OFMessage msg = new FlowConverter(flow).getOFFlowMod(
                        OFFlowMod.OFPFC_DELETE_STRICT, OFPort.OFPP_NONE);*///s4s. OF's code
                SNMPMessage msg = new SNMPFlowMod(SNMPFlowMod.ETHPFC_DELETE, flow.clone());//s4s
                msg.setTargetSwitchID((Long) node.getID());//s4s

                Object result;
                if (rid == 0) {
                    // *
                    // * Synchronous message send. Each message is followed by a
                    // * Barrier message.
                    // * /
                    result = sw.syncSend(msg);
                } else {
                    // *
                    // * Message will be sent asynchronously. A Barrier message
                    // * will be inserted automatically to synchronize the
                    // * progression.
                    // * /
                    result = asyncMsgSend(node, sw, msg, rid);
                }
                return getStatusInternal(result, action, rid);
            } else {
                return new Status(StatusCode.GONE, errorString("send", action,
                        "Switch is not available"));
            }
        }
        return new Status(StatusCode.INTERNALERROR, errorString("send", action,
                "Internal plugin error"));
    }
//end of mark 3

    @Override
    public Status removeAllFlows(Node node) {
        return new Status(StatusCode.FORBIDDEN);
    }

    private String errorString(String phase, String action, String cause) {
        return "Failed to "
                + ((phase != null) ? phase + " the " + action
                        + " flow message: " : action + " the flow: ") + cause;
    }

/* //s4s mark 4
    @Override
    public void receive(ISwitch sw, OFMessage msg) {
        if (msg instanceof OFFlowRemoved) {
            handleFlowRemovedMessage(sw, (OFFlowRemoved) msg);
        } else if (msg instanceof OFError) {
            handleErrorMessage(sw, (OFError) msg);
        }
    }
*/ //end of mark 4

/* //s4s mark 5
    private void handleFlowRemovedMessage(ISwitch sw, OFFlowRemoved msg) {
        Node node = NodeCreator.createOFNode(sw.getId());
        Flow flow = new FlowConverter(msg.getMatch(),
                new ArrayList<OFAction>(0)).getFlow(node);
        flow.setPriority(msg.getPriority());
        flow.setIdleTimeout(msg.getIdleTimeout());
        flow.setId(msg.getCookie());

        Match match = flow.getMatch();
        NodeConnector inPort = match.isPresent(MatchType.IN_PORT) ? (NodeConnector) match
                .getField(MatchType.IN_PORT).getValue() : null;

        for (Map.Entry<String, IFlowProgrammerNotifier> containerNotifier : flowProgrammerNotifiers
                .entrySet()) {
            String container = containerNotifier.getKey();
            IFlowProgrammerNotifier notifier = containerNotifier.getValue();
            // *
            // * Switch only provide us with the match information. For now let's
            // * try to identify the container membership only from the input port
            // * match field. In any case, upper layer consumers can derive
            // * whether the notification was not for them. More sophisticated
            // * filtering can be added later on.
            // * /
            if (inPort == null
                    || container.equals(GlobalConstants.DEFAULT.toString())
                    || this.containerToNc.get(container).contains(inPort)) {
                notifier.flowRemoved(node, flow);
            }
        }
    }
*/ //end of mark 5

/* //s4s mark 6
    private void handleErrorMessage(ISwitch sw, OFError errorMsg) {
        Node node = NodeCreator.createOFNode(sw.getId());
        OFMessage offendingMsg = errorMsg.getOffendingMsg();
        Integer xid;
        if (offendingMsg != null) {
            xid = offendingMsg.getXid();
        } else {
            xid = errorMsg.getXid();
        }

        Long rid = getMessageRid(sw.getId(), xid);
        //*
        // * Null or zero requestId indicates that the error message is meant for
        // * a sync message. It will be handled by the sync message worker thread.
        // * Hence we are done here.
        // * /
        if ((rid == null) || (rid == 0)) {
            return;
        }

        // *
        // * Notifies the caller that error has been reported for a previous flow
        // * programming request
        // * /
        for (Map.Entry<String, IFlowProgrammerNotifier> containerNotifier : flowProgrammerNotifiers
                .entrySet()) {
            IFlowProgrammerNotifier notifier = containerNotifier.getValue();
            notifier.flowErrorReported(node, rid, errorMsg);
        }
    }
*/ //end of mark 6

    @Override
    public void tagUpdated(String containerName, Node n, short oldTag,
            short newTag, UpdateType t) {

    }

    @Override
    public void containerFlowUpdated(String containerName,
            ContainerFlow previousFlow, ContainerFlow currentFlow, UpdateType t) {
    }

    @Override
    public void nodeConnectorUpdated(String containerName, NodeConnector p,
            UpdateType type) {
        Set<NodeConnector> target = null;

        switch (type) {
        case ADDED:
            if (!containerToNc.containsKey(containerName)) {
                containerToNc.put(containerName, new HashSet<NodeConnector>());
            }
            containerToNc.get(containerName).add(p);
            break;
        case CHANGED:
            break;
        case REMOVED:
            target = containerToNc.get(containerName);
            if (target != null) {
                target.remove(p);
            }
            break;
        default:
        }
    }

    @Override
    public void containerModeUpdated(UpdateType t) {

    }

//s4s mark 7
    @Override
    public Status syncSendBarrierMessage(Node node) {
        //if (!node.getType().equals(NodeIDType.OPENFLOW)) {//s4s mark
            return new Status(StatusCode.NOTACCEPTABLE,
                    "The node does not support Barrier message.");
        //}//s4s mark

        /* //s4s. OF's code
        if (controller != null) {
            long swid = (Long) node.getID();
            ISwitch sw = controller.getSwitch(swid);
            if (sw != null) {
                sw.syncSendBarrierMessage();
                clearXid2Rid(swid);
                return (new Status(StatusCode.SUCCESS));
            } else {
                return new Status(StatusCode.GONE,
                        "The node does not have a valid Switch reference.");
            }
        }
        return new Status(StatusCode.INTERNALERROR,
                "Failed to send Barrier message.");
        */
    }
//enf of mark 7

//s4s mark 8
    @Override
    public Status asyncSendBarrierMessage(Node node) {
        //if (!node.getType().equals(NodeIDType.OPENFLOW)) {//s4s mark
            return new Status(StatusCode.NOTACCEPTABLE,
                    "The node does not support Barrier message.");
        //}//s4s mark

        /* //s4s. OF's code
        if (controller != null) {
            long swid = (Long) node.getID();
            ISwitch sw = controller.getSwitch(swid);
            if (sw != null) {
                sw.asyncSendBarrierMessage();
                clearXid2Rid(swid);
                return (new Status(StatusCode.SUCCESS));
            } else {
                return new Status(StatusCode.GONE,
                        "The node does not have a valid Switch reference.");
            }
        }
        return new Status(StatusCode.INTERNALERROR,
                "Failed to send Barrier message.");
        */
    }
//end of mark 8

    /**
     * This method sends the message asynchronously until the number of messages
     * sent reaches a threshold. Then a Barrier message is sent automatically
     * for sync purpose. An unique Request ID associated with the message is
     * passed down by the caller. The Request ID will be returned to the caller
     * when an error message is received from the switch.
     *
     * @param node
     *            The node
     * @param msg
     *            The switch
     * @param msg
     *            The OF message to be sent
     * @param rid
     *            The Request Id
     * @return result
     */
//s4s mark 9
    private Object asyncMsgSend(Node node, ISwitch sw, /*OFMessage msg*/SNMPMessage msg, long rid) {
        Object result = Boolean.TRUE;
        long swid = (Long) node.getID();
        int xid;

        xid = sw.asyncSend(msg);
        /* //OF's code: about xid rid BarrierMessage
        addXid2Rid(swid, xid, rid);

        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);
        if (swxid2rid == null) {
            return result;
        }

        int size = swxid2rid.size();
        if (size % barrierMessagePriorCount == 0) {
            result = asyncSendBarrierMessage(node);
        }
        */

        return result;
    }
//end of mark 9

    /**
     * A number of async messages are sent followed by a synchronous Barrier
     * message. This method returns the maximum async messages that can be sent
     * before the Barrier message.
     *
     * @return The max count of async messages sent prior to Barrier message
     */
    private int getBarrierMessagePriorCount() {//s4s. useless in my plugin. may delete
        String count = System.getProperty("of.barrierMessagePriorCount");
        int rv = 100;

        if (count != null) {
            try {
                rv = Integer.parseInt(count);
            } catch (Exception e) {
            }
        }

        return rv;
    }

    /**
     * This method returns the message Request ID previously assigned by the
     * caller for a given OF message xid
     *
     * @param swid
     *            The switch id
     * @param xid
     *            The OF message xid
     * @return The Request ID
     */
    private Long getMessageRid(long swid, Integer xid) {//s4s. useless in my plugin. may delete
        Long rid = null;

        if (xid == null) {
            return rid;
        }

        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);
        if (swxid2rid != null) {
            rid = swxid2rid.get(xid);
        }
        return rid;
    }

    /**
     * This method returns a copy of outstanding xid to rid mappings.for a given
     * switch
     *
     * @param swid
     *            The switch id
     * @return a copy of xid2rid mappings
     */
    public Map<Integer, Long> getSwXid2Rid(long swid) {//s4s. useless in my plugin. may delete
        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);

        if (swxid2rid != null) {
            return new HashMap<Integer, Long>(swxid2rid);
        } else {
            return new HashMap<Integer, Long>();
        }
    }

    /**
     * Adds xid to rid mapping to the local DB
     *
     * @param swid
     *            The switch id
     * @param xid
     *            The OF message xid
     * @param rid
     *            The message Request ID
     */
    private void addXid2Rid(long swid, int xid, long rid) {//s4s. useless in my plugin. may delete
        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);
        if (swxid2rid != null) {
            swxid2rid.put(xid, rid);
        }
    }

    /**
     * When an Error message is received, this method will be invoked to remove
     * the offending xid from the local DB.
     *
     * @param swid
     *            The switch id
     * @param xid
     *            The OF message xid
     */
    private void removeXid2Rid(long swid, int xid) {//s4s. useless in my plugin. may delete
        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);
        if (swxid2rid != null) {
            swxid2rid.remove(xid);
        }
    }

    /**
     * Convert various result into Status
     *
     * @param result
     *            The returned result from previous action
     * @param action
     *            add/modify/delete flow action
     * @param rid
     *            The Request ID associated with the flow message
     * @return Status
     */
    private Status getStatusInternal(Object result, String action, long rid) {
        if (result instanceof Boolean) {
            return ((Boolean) result == Boolean.TRUE) ? new Status(
                    StatusCode.SUCCESS, rid) : new Status(
                    StatusCode.TIMEOUT, errorString(null, action,
                            "Request Timed Out"));
        } else if (result instanceof Status) {
            return (Status) result;
        } else if (result instanceof OFError) {
            OFError res = (OFError) result;
            return new Status(StatusCode.INTERNALERROR, errorString(
                    "program", action,
                    "Internal Error" /*Utils.getOFErrorString(res)*/));//s4s change for easy compile(orig: Utils.getOFErrorString(res))
        } else {
            return new Status(StatusCode.INTERNALERROR, errorString(
                    "send", action, "Internal Error"));
        }
    }

    /**
     * When a Barrier reply is received, this method will be invoked to clear
     * the local DB
     *
     * @param swid
     *            The switch id
     */
    private void clearXid2Rid(long swid) {//s4s. useless in my plugin. may delete
        Map<Integer, Long> swxid2rid = this.xid2rid.get(swid);
        if (swxid2rid != null) {
            swxid2rid.clear();
        }
    }

//s4s mark 12 //s4s
    @Override
    public void updateNode(Node node, UpdateType type, Set<Property> props) {/*//s4s. useless in my plugin. may left empty
        long swid = (Long)node.getID();

        switch (type) {
        case ADDED:
            Map<Integer, Long> swxid2rid = new HashMap<Integer, Long>();
            this.xid2rid.put(swid, swxid2rid);
            break;
        case CHANGED:
            break;
        case REMOVED:
            this.xid2rid.remove(swid);
            break;
        default:
        }*/
    }
//end of mark 12

    @Override //s4s
    public void updateNodeConnector(NodeConnector nodeConnector,
            UpdateType type, Set<Property> props) {
    }

    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

/*********For Karaf/OSGi CLI commands*********/

    private static Node createSNMPNode(long switchId) {
        try {
            return new Node("SNMP", new Long(switchId));
        } catch (ConstructionException e1) {
            log.error("",e1);
            return null;
        }
    }

    private static NodeConnector createSNMPNodeConnector(short portId, Node node) {
        if (node.getType().equals("SNMP")) {
            try {
                return new NodeConnector("SNMP",
                        new Short(portId), node);
            } catch (ConstructionException e1) {
                log.error("",e1);
                return null;
            }
        }
        return null;
    }

    private static Flow createFlow(Long nodeId, short vlanId, String dstMacStr, short outport){
        byte dstMac[] = HexString.fromHexString(dstMacStr);
        short ethertype = EtherTypes.IPv4.shortValue();
        short vlan = vlanId;

        Match match = new Match();
        match.setField(MatchType.DL_DST, dstMac);
        match.setField(MatchType.DL_VLAN, vlan);
        match.setField(MatchType.DL_TYPE, ethertype);
        
        Node node = createSNMPNode(nodeId);
        NodeConnector oport = createSNMPNodeConnector(outport, node);
        
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Output(oport));
        Flow flow = new Flow(match, actions);
        
        return flow;
    }
    
    private Status s4sAddFlow_execute(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr){
        Long nodeId;
        try{
            nodeId = HexString.toLong(switch_mac);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid switch mac " + switch_mac + ": " + e1);
        }

        Short vlanID;
        try{
            vlanID = Short.valueOf(vlanIdStr);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid VLAN ID " + vlanIdStr + ": " + e1);
        }
        short vlanId = vlanID.shortValue();

        Short portNum;
        try{
            portNum = Short.valueOf(portNumStr);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid port number " + portNumStr + ": " + e1);
        }
        short port = portNum.shortValue();

        Node node = createSNMPNode(nodeId);
        Flow flow = createFlow(nodeId, vlanId, dstMacStr, portNum);
        return addFlow(node, flow);
    }

    private Status s4sDeleteFlow_execute(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr){
        Long nodeId;
        try{
            nodeId = HexString.toLong(switch_mac);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid switch mac " + switch_mac + ": " + e1);
        }

        Short vlanID;
        try{
            vlanID = Short.valueOf(vlanIdStr);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid VLAN ID " + vlanIdStr + ": " + e1);
        }
        short vlanId = vlanID.shortValue();

        Short portNum;
        try{
            portNum = Short.valueOf(portNumStr);
        }catch(Exception e1){
            return new Status(StatusCode.NOTACCEPTABLE, "Invalid port number " + portNumStr + ": " + e1);
        }
        short port = portNum.shortValue();

        Node node = createSNMPNode(nodeId);
        Flow flow = createFlow(nodeId, vlanId, dstMacStr, portNum);
        return removeFlow(node, flow);
    }

    public Status _s4sAddFlow(CommandInterpreter ci){
        //TODO: format check
        String switch_mac = ci.nextArgument();
        String vlanIdStr = ci.nextArgument();
        String dstMacStr = ci.nextArgument();
        String portNumStr = ci.nextArgument();

        return s4sAddFlow_execute(switch_mac, vlanIdStr, dstMacStr, portNumStr); 
    }

    public Status _s4sDeleteFlow(CommandInterpreter ci){
        //TODO: format check
        String switch_mac = ci.nextArgument();
        String vlanIdStr = ci.nextArgument();
        String dstMacStr = ci.nextArgument();
        String portNumStr = ci.nextArgument();

        return s4sDeleteFlow_execute(switch_mac, vlanIdStr, dstMacStr, portNumStr); 
    }

    @Override //karaf
    public Status krfAddFlow(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr){
        return s4sAddFlow_execute(switch_mac, vlanIdStr, dstMacStr, portNumStr); 
    }

    @Override //karaf
    public Status krfDeleteFlow(String switch_mac, String vlanIdStr, String dstMacStr, String portNumStr){
        return s4sDeleteFlow_execute(switch_mac, vlanIdStr, dstMacStr, portNumStr); 
    }

/*********end of Karaf/OSGi CLI commands*********/

    
///* //s4s mark 13
    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("-- Flow Programmer Service --\n");
        help.append("\t px2r <node id>          - Print outstanding xid2rid mappings for a given node id\n");
        help.append("\t px2rc                   - Print max num of async msgs prior to the Barrier\n");
        return help.toString();
    }
//*/ //end of mark 13

    public void _px2r(CommandInterpreter ci) {//s4s. OF's code, seems should delete
        String st = ci.nextArgument();
        if (st == null) {
            ci.println("Please enter a valid node id");
            return;
        }

        long sid;
        try {
            sid = HexEncode.stringToLong(st);
        } catch (NumberFormatException e) {
            ci.println("Please enter a valid node id");
            return;
        }

        Map<Integer, Long> swxid2rid = this.xid2rid.get(sid);
        if (swxid2rid == null) {
            ci.println("The node id entered does not exist");
            return;
        }

        ci.println("xid             rid");

        Set<Integer> xidSet = swxid2rid.keySet();
        if (xidSet == null) {
            return;
        }

        for (Integer xid : xidSet) {
            ci.println(xid + "       " + swxid2rid.get(xid));
        }
    }

    public void _px2rc(CommandInterpreter ci) {//s4s. OF's code, seems should delete
        ci.println("Max num of async messages sent prior to the Barrier message is "
                + barrierMessagePriorCount);
    }

    private boolean checkSameSrcDest(Flow flow1, Flow flow2){
        //for flow 1....
        //retrieve from the flow: (1)src mac (2)dest mac (3)action
            //to retrieve (1)&(2)
        Match match1 = flow1.getMatch();
        MatchField fieldDlSrc1= match1.getField(MatchType.DL_SRC);
        MatchField fieldDlDest1= match1.getField(MatchType.DL_DST);
        String srcMac1 = HexString.toHexString((byte[])fieldDlSrc1.getValue());
        String destMac1 = HexString.toHexString((byte[])fieldDlSrc1.getValue());
            //to retrieve (3) and check it structure correct
        Action action1 = flow1.getActions().get(0);
        if(flow1.getActions().size() > 1) {
            log.trace("flow1.getActions() > 1, the Action are:");
            for(int i = 0; i < flow1.getActions().size(); i++)
                log.trace("{}", flow1.getActions().get(i));
        }
        if(action1.getType() != ActionType.OUTPUT){
            log.warn("flow's action is not to set OUTPUT port!");
            return false;
        }

        //for flow 2... do the same things as above
            //to retrieve (1)&(2)
        Match match2 = flow2.getMatch();
        MatchField fieldDlSrc2= match2.getField(MatchType.DL_SRC);
        MatchField fieldDlDest2= match2.getField(MatchType.DL_DST);
        String srcMac2 = HexString.toHexString((byte[])fieldDlSrc2.getValue());
        String destMac2 = HexString.toHexString((byte[])fieldDlSrc2.getValue());
            //to retrieve (3) and check it structure correct
        Action action2 = flow2.getActions().get(0);
        if(flow2.getActions().size() > 1) {
            log.trace("flow2.getActions() > 1, the Action are:");
            for(int i = 0; i < flow1.getActions().size(); i++)
                log.trace("{}", flow1.getActions().get(i));
        }
        if(action2.getType() != ActionType.OUTPUT){
            log.warn("flow's action is not to set OUTPUT port!");
            return false;
        }

        //now compare flow1 and flow2
        if(srcMac1 == srcMac2 && destMac1 == destMac2)
            return true;
        else
            return false;
    }

    @Override
    public void containerCreate(String containerName) {
        // do nothing
    }

    @Override
    public void containerDestroy(String containerName) {
        containerToNc.remove(containerName);
    }
}
