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
import org.opendaylight.snmp4sdn.IFlowProgrammerNotifier;//s4s
import org.opendaylight.snmp4sdn.IInventoryShimExternalListener;//s4s
import org.opendaylight.snmp4sdn.core.IController;//s4s
//import org.opendaylight.controller.protocol_plugin.openflow.core.IMessageListener;//s4s //receive()
import org.opendaylight.snmp4sdn.core.ISwitch;//s4s
import org.opendaylight.controller.sal.core.ContainerFlow;
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
        CommandProvider {
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
            log.error("Didn't receive the service correct properties");
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
            log.error("Didn't receive the service correct properties");
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
        System.out.println("enter FlowProgrammerService.addFlow()");
        return addFlowInternal(node, flow, 0);//OF's
        //return my_modifyFlow(node, flow, 3); //s4s. modType 3 is type of 3, means "learned"
    }

    @Override
    public Status modifyFlow(Node node, Flow oldFlow, Flow newFlow) {
        return modifyFlowInternal(node, oldFlow, newFlow, 0);
        //return my_modifyFlow(node, flow, 3); //s4s. modType 3 is type of 3, means "learned"
    }

    @Override
    public Status removeFlow(Node node, Flow flow) {
        return removeFlowInternal(node, flow, 0);
        //return my_modifyFlow(node, flow, 2); //s4s. modType 2 is type of 2, means "invalid(delete)"
    }

    @Override
    public Status addFlowAsync(Node node, Flow flow, long rid) {
        return addFlowInternal(node, flow, rid);//s4s
        //return new Status(StatusCode.SUCCESS);
    }

    @Override
    public Status modifyFlowAsync(Node node, Flow oldFlow, Flow newFlow,
            long rid) {
        return modifyFlowInternal(node, oldFlow, newFlow, rid);
    }

    @Override
    public Status removeFlowAsync(Node node, Flow flow, long rid) {
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
                /*FlowConverter x = new FlowConverter(flow);
                OFMessage msg = x.getOFFlowMod(OFFlowMod.OFPFC_ADD, null);*///s4s. OF's code
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

                //s4s's work, similar to above OF's work: 'check data correctness' and 'type convertion'
                if(checkSameSrcDest(oldFlow, newFlow)){
                    return new Status(StatusCode.NOTACCEPTABLE, errorString("send", action,
                            "Inconsistency of oldFlow and newFlow (src/dest mac inconsistent)"));
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
        return new Status(StatusCode.SUCCESS);
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
            System.out.println("flow.getActions() > 1");
            System.exit(0);
        }
        if(action1.getType() != ActionType.OUTPUT){
            System.out.println("flow's action is not to set OUTPUT port!");
            System.exit(0);
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
            System.out.println("flow.getActions() > 1");
            System.exit(0);
        }
        if(action2.getType() != ActionType.OUTPUT){
            System.out.println("flow's action is not to set OUTPUT port!");
            System.exit(0);
        }

        //now compare flow1 and flow2
        if(srcMac1 == srcMac2 && destMac1 == destMac2)
            return true;
        else
            return false;
    }
//--------------cmeth----------------//

/*
//s4s. can add/modify/delete flow

    //@Override
    public Status cmeth_override_addFlow(Node node, Flow flow) {
        System.out.println("enter FlowProgrammerService.addFlow()");
        return my_modifyFlow(node, flow, 3); //s4s. modType 3 is type of 3, means "learned"
    }

    //@Override
    public Status cmeth_override_modifyFlow(Node node, Flow oldFlow, Flow newFlow) {
        String action = "modify";//s4s
        if (!node.getType().equals(NodeIDType.ETHSW)) {//s4s orig:NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE,
                    errorString("send", action, "Invalid node type"));
        }

        //check consistency of oldFlow and newFlow: their src/dest mac same?
        if (checkSameSrcDest(oldFlow, newFlow) == false)
            return new Status(StatusCode.NOTACCEPTABLE,
                    errorString("send", action, "Inconsistency of oldFlow and newFlow (src/dest mac inconsistent)"));

        //check whether exsit oldFlow
        //
        //
        //


        Status result = my_modifyFlow(node, newFlow, 3);//modType 3 is type of 3, means "learned"
        return result;
    }

    //@Override //s4s
    public Status cmeth_override_removeFlow(Node node, Flow flow) {
        String action = "remove";
        if (!node.getType().equals(NodeIDType.ETHSW)) {//s4s orig:NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE,
                    errorString("send", action, "Invalid node type"));
        }

        return my_modifyFlow(node, flow, 2); //modType 2 is type of 2, means "invalid"
    }


//s4s. can add/modify/delete flow
    public Status my_modifyFlow(Node node, Flow flow, int modType) {
        System.out.println("enter FlowProgrammerService.my_modifyFlow()");
        String actionstr = "add";
        if (!node.getType().equals(NodeIDType.ETHSW)) {//s4s orig:NodeIDType.OPENFLOW
            return new Status(StatusCode.NOTACCEPTABLE,
                    errorString("send", actionstr, "Invalid node type"));
        }

        System.out.println("retrieving the metrics in the Flow...");
        //retrieve from the flow: (1)src mac (2)dest mac (3)the port value, to write into fwd table
            //to retrieve (1)&(2)
        Match match = flow.getMatch();
        MatchField fieldDlSrc= match.getField(MatchType.DL_SRC);
        MatchField fieldDlDest= match.getField(MatchType.DL_DST);
        String srcMac = HexString.toHexString((byte[])fieldDlSrc.getValue());
        String destMac = HexString.toHexString((byte[])fieldDlDest.getValue());
            //to retrieve (3)
        Action action = flow.getActions().get(0);
        if(flow.getActions().size() > 1) {
            System.out.println("flow.getActions() > 1");
            System.exit(0);
        }
        if(action.getType() != ActionType.OUTPUT){
            System.out.println("flow's action is not to set OUTPUT port!");
            System.exit(0);
        }
        NodeConnector oport = ((Output)action).getPort();


        //Use snmp to write to switch fwd table...

        String community = "private";
        Long sw_macAddr = (Long) node.getID();
        try{
            //1. open snmp communication interface
            InetAddress sw_ipAddr = InetAddress.getByName(getIpAddr(sw_macAddr));
            SNMPv1CommunicationInterface comInterface = createSNMPv1CommInterface(0, sw_ipAddr, community);

            System.out.println("snmp connection created...swtich IP addr=" + sw_ipAddr.toString() + ", community=" + community);

            //2. now can set fwd table entry
            System.out.println("going to set fwd table entry...");
            Short portShort = (Short)(oport.getID());
            short portID = portShort.shortValue();
            int portInt = (int)portID;
            boolean result = setFwdTableEntry(comInterface, destMac, portInt, modType);//oport.getID() is the port.  modType as 3 means "learned". modType as 2 means "invalid"
            return (result == true)?(new Status(StatusCode.SUCCESS, null)):
                    (new Status(StatusCode.INTERNALERROR,
                    errorString("program", actionstr, "Vendor Extension Internal Error")));
        }
        catch (UnknownHostException e) {
            System.out.println("sw_macAddr " + sw_macAddr + "into InetAddress.getByName() error!\n" + e);
            System.exit(0);
        }

            return new Status(StatusCode.SUCCESS, null);
    }
*/

/*snmp
    String portGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.2";//s4s: MAC_PORT_GET's OID
    String typeGetOID = "1.3.6.1.2.1.17.7.1.2.2.1.3";//s4s: MAC_TYPE_GET's OID
    String portSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.3";//s4s: MAC_PORT_SET's OID
    String typeSetOID = "1.3.6.1.2.1.17.7.1.3.1.1.4";//s4s: MAC_TYPE_SET's OID
    */

//snmp
/*
    private SNMPv1CommunicationInterface createSNMPv1CommInterface(int version, InetAddress hostAddress, String community){
        try{

            // create a communications interface to a remote SNMP-capable device;
            // need to provide the remote host's InetAddress and the community
            // name for the device; in addition, need to  supply the version number
            // for the SNMP messages to be sent (the value 0 corresponding to SNMP
            // version 1)
            //InetAddress hostAddress = InetAddress.getByName("10.0.1.1");
            //String community = "public";
            //int version = 0;    // SNMPv1

          SNMPv1CommunicationInterface comInterface = new SNMPv1CommunicationInterface(version, hostAddress, community);
          return comInterface;

        }
        catch(Exception e)
        {
            System.out.println("Exception during SNMP operation:  " + e + "\n");
        }

        return null;
    }

    //s4s
    private void getMIBEntry(SNMPv1CommunicationInterface comInterface, String oid){
        try{

            // now send an SNMP GET request to retrieve the value of the SNMP variable
            // corresponding to OID 1.3.6.1.2.1.2.1.0; this is the OID corresponding to
            // the device identifying string, and the type is thus SNMPOctetString
            String itemID = "1.3.6.1.2.1.1.1.0";
            System.out.println("Retrieving value corresponding to OID " + oid);

            // the getMIBEntry method of the communications interface returns an SNMPVarBindList
            // object; this is essentially a Vector of SNMP (OID,value) pairs. In this case, the
            // returned Vector has just one pair inside it.
            SNMPVarBindList newVars = comInterface.getMIBEntry(oid);
        }
        catch(Exception e)
        {
            System.out.println("Exception during SNMP operation:  " + e + "\n");
        }
    }

    //s4s
    String convertToSNMPSwitchPortString(int port){
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        String sep = ":";//see what seperate character is assigned, eg. ":" or " " or ""  (i.e.  colon or blank or connected)
        int pow;
        String str = "";

        if(port <= 8){
            pow = 8 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + str + "0"  + sep + "00";
        }
        else if(port <= 16){
            pow = 16 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0" + str + sep + "00";
        }
        else if(port <= 24){
            pow = 24 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + str + "0";
        }
        else if(port <= 32){
            pow = 32 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "0" + str;
        }
        else{
            System.out.println("convertToSNMPSwitchPortString() is given port > 32!");
            System.exit(0);
        }

        return ans;
    }

    //s4s
    String convertToSNMPSwitchPortString_4bits_as_a_character(int port){
        //String ans = "0x";//for linux snmpset command parameter form
        String ans = "";
        String sep = ":";//see what seperate character is assigned, eg. ":" or " " or ""  (i.e.  colon or blank or connected)
        int pow;
        String str = "";

        if(port <= 4){
            pow = 4 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + str + "0" + sep + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 8){
            pow = 8 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0" + str + "00" + sep + "00" + sep + "00";
        }
        else if(port <= 12){
            pow = 12 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + str + "0" + sep + "00" + sep + "00";
        }
        else if(port <= 16){
            pow = 16 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "000" + str + "00" + sep + "00";
        }
        else if(port <= 20){
            pow = 20 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "0000" + str + "0" + sep + "00";
        }
        else if(port <= 24){
            pow = 24 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "0" + str + "00";
        }
        else if(port <= 28){
            pow = 28 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "00" + str + "0";
        }
        else if(port <= 32){
            pow = 32 - port;
            str = new Integer((int)Math.pow(2, pow)).toString();
            ans = ans + "00" + sep + "00" + sep + "00" + sep + "0" + str;
        }
        else{
            System.out.println("convertToSNMPSwitchPortString() is given port > 32!");
            System.exit(0);
        }

        return ans;
    }

    //s4s
    private String macAddrToOID(String macAddr){
        String macOID = "";
        for(int i = 0; i < 17; ){
            macOID += ".";
            macOID  += new Integer(Integer.parseInt(macAddr.substring(i, i + 2), 16)).toString();
            i += 3;
        }
        return macOID.substring(1, macOID.length());
    }

    //s4s
    private boolean setFwdTableEntry(SNMPv1CommunicationInterface comInterface, String destMac, int port, int type){
        System.out.println("enter setFwdTableEntry()...");
        try{
            String macOid = macAddrToOID(destMac);
            String portOid = portSetOID + ".216." + macOid + ".0";
            String typeOid = typeSetOID + ".216." + macOid + ".0";

            byte[] convPort = new HexString().fromHexString(convertToSNMPSwitchPortString(port));
            SNMPOctetString portOStr =  new SNMPOctetString(convPort);
            SNMPInteger typeInt =  new SNMPInteger(type);

            System.out.println("mac (" + destMac +")'s OID: " + macOid);
            System.out.println("type: " + typeInt.toString());

            if(type == 2){//delete entry
                SNMPVarBindList newVars = comInterface.setMIBEntry(typeOid, typeInt);
                System.out.println("set OID  " + typeOid + ", new value = " + typeInt.getClass().getName() + ":" + typeInt);
            }
            else if(type == 3){//add or modify entry
                System.out.println("port: " + portOStr.toString());

                String[] oids = {typeOid, portOid};
                SNMPObject [] newValues = {typeInt, portOStr};
                SNMPVarBindList newVars = comInterface.setMIBEntry(oids, newValues); //comInterface.setMIBEntry() can either input array or variable, like here or below

                for(int i = 0; i < oids.length; i++){
                    System.out.println("set OID  " + oids[i] + ", new value = " + newValues[i].getClass().getName() + ":" + newValues[i]);
                }
            }
            else{
                System.out.println("Error: given type (type" + typeInt + ") invalid");
                System.exit(0);
            }

            return true;
        }
        catch(Exception e)
        {
            System.out.println("Exception during SNMP operation:  " + e + "\n");
            return false;
        }
   }

    //s4s
    private String getIpAddr(Long macAddr){
        //look up table...
        return "10.216.0.31";
    }
*/
}
