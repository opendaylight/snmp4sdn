/*
 * Copyright (c) 2016 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal.util;

import org.opendaylight.snmp4sdn.sal.core.Edge;
import org.opendaylight.snmp4sdn.sal.core.Node;
import org.opendaylight.snmp4sdn.sal.core.NodeConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyServiceUtil{
    protected static final Logger logger = LoggerFactory
            .getLogger(TopologyServiceUtil.class);
    public static boolean getNodeAndNcIdString(Edge edge, String headNodeIdStr, String tailNodeIdStr, String headNcIdStr, String tailNcIdStr){
        NodeConnector headNC = edge.getHeadNodeConnector();
        NodeConnector tailNC = edge.getTailNodeConnector();
        if(headNC == null){
            logger.debug("ERROR: getNodeAndNcIdString(): given edge's head nodeconnector is null!");
            return false;
        }
        if(tailNC == null){
            logger.debug("ERROR: getNodeAndNcIdString(): given edge's tail nodeconnector is null!");
            return false;
        }
        Node headNode = headNC.getNode();
        Node tailNode = tailNC.getNode();
        if(headNode == null){
            logger.debug("ERROR: getNodeAndNcIdString(): given edge's head node is null!");
            return false;
        }
        if(tailNode == null){
            logger.debug("ERROR: getNodeAndNcIdString(): given edge's tail node is null!");
            return false;
        }

        headNodeIdStr = headNode.getNodeIDString();
        tailNodeIdStr = tailNode.getNodeIDString();
        headNcIdStr = headNC.getNodeConnectorIDString();
        tailNcIdStr = tailNC.getNodeConnectorIDString();

        return true;
    }
}
