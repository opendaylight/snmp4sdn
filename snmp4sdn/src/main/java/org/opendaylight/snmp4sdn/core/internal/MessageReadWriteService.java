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

package org.opendaylight.snmp4sdn.core.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.opendaylight.snmp4sdn.core.IMessageReadWrite;
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.BasicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.utils.Status;//s4s add
import org.opendaylight.controller.sal.utils.StatusCode;//s4s add

import org.opendaylight.snmp4sdn.protocol.SNMPFlowMod;//s4s add
import org.opendaylight.snmp4sdn.protocol.SNMPMessage;//s4s add
import org.opendaylight.snmp4sdn.protocol.SNMPType;//s4s add
import org.opendaylight.snmp4sdn.internal.SNMPHandler;//s4s add
import org.opendaylight.snmp4sdn.internal.util.CmethUtil;//s4s add

/**
 * This class implements methods to read/write messages over an established
 * socket channel. The data exchange is in clear text format.
 */
public class MessageReadWriteService implements IMessageReadWrite {
    private static final Logger logger = LoggerFactory
            .getLogger(MessageReadWriteService.class);
    private static final int bufferSize = 1024 * 1024;

    private Selector selector;
    private SelectionKey clientSelectionKey;
    private SocketChannel socket;
    private ByteBuffer inBuffer;
    private ByteBuffer outBuffer;
    private BasicFactory factory;
    private CmethUtil cmethUtil;//s4s add

    public MessageReadWriteService(SocketChannel socket, Selector selector, CmethUtil cmethUtil)
            throws ClosedChannelException {
        this.socket = socket;
        this.selector = selector;
        this.factory = new BasicFactory();
        this.inBuffer = ByteBuffer.allocateDirect(bufferSize);
        this.outBuffer = ByteBuffer.allocateDirect(bufferSize);
        /*this.clientSelectionKey = this.socket.register(this.selector,
                SelectionKey.OP_READ);*///s4s. we don't need socket
        this.cmethUtil = cmethUtil;
    }

    /**
     * Sends the OF message out over the socket channel.
     *
     * @param msg
     *            OF message to be sent
     * @throws IOException
     */
    @Override
    //public void asyncSend(OFMessage msg) throws IOException {//s4s. OF's
    public Status asyncSend(SNMPMessage msg) throws IOException {//s4s
        /*
        synchronized (outBuffer) {
            int msgLen = msg.getLengthU();
            if (outBuffer.remaining() < msgLen) {
                // increase the buffer size so that it can contain this message
                ByteBuffer newBuffer = ByteBuffer.allocateDirect(outBuffer
                        .capacity() + msgLen);
                outBuffer.flip();
                newBuffer.put(outBuffer);
                outBuffer = newBuffer;
            }
        }
        synchronized (outBuffer) {
            msg.writeTo(outBuffer);

            if (!socket.isOpen()) {
                return;
            }

            outBuffer.flip();
            socket.write(outBuffer);
            outBuffer.compact();
            if (outBuffer.position() > 0) {
                this.clientSelectionKey = this.socket.register(this.selector,
                        SelectionKey.OP_WRITE, this);
            }
            logger.trace("Message sent: {}", msg);
        }*/
        if(msg.getType() == SNMPType.FLOW_MOD){
            SNMPFlowMod msgMod = (SNMPFlowMod)msg;
            Status status = new SNMPHandler(cmethUtil).sendBySNMP(msgMod.getFlow(), msgMod.getCommand(), msg.getTargetSwitchID());
            if(status.getCode() == StatusCode.SUCCESS)
                logger.trace("Message sent: {}", msg);
            return status;
        }
        else{
            logger.warn("This SNMPMessage type doens't support (or not yet done): SNMPType " + msg.getType());
            return new Status(StatusCode.UNSUPPORTED, "This SNMPMessage type doens't support (or not yet done) in module MessageReadWriteService: SNMPType " + msg.getType());
        }
    }

    /**
     * Resumes sending the remaining messages in the outgoing buffer
     *
     * @throws Exception
     */
    @Override
    public void resumeSend() throws IOException {
        /*synchronized (outBuffer) {
            if (!socket.isOpen()) {
                return;
            }

            outBuffer.flip();
            socket.write(outBuffer);
            outBuffer.compact();
            if (outBuffer.position() > 0) {
                this.clientSelectionKey = this.socket.register(this.selector,
                        SelectionKey.OP_WRITE, this);
            } else {
                this.clientSelectionKey = this.socket.register(this.selector,
                        SelectionKey.OP_READ, this);
            }
        }*///seems useless for snmp4sdn
        //if you would like to know who called here: in SwitchHandler.java's startHandlerThread(), called its resumeSend(), then go to here
    }

    /**
     * Reads the incoming network data from the socket and retrieves the OF
     * messages.
     *
     * @return list of OF messages
     * @throws Exception
     */
    @Override
    public List</*OFMessage msg*/SNMPMessage> readMessages() throws IOException {
        if (!socket.isOpen()) {
            return null;
        }

        List</*OFMessage msg*/SNMPMessage> msgs = null;
        int bytesRead = -1;
        bytesRead = socket.read(inBuffer);
        if (bytesRead < 0) {
            throw new AsynchronousCloseException();
        }

        inBuffer.flip();
        //msgs = factory.parseMessages(inBuffer);//s4s. mark here, should deal with here, later
        if (inBuffer.hasRemaining()) {
            inBuffer.compact();
        } else {
            inBuffer.clear();
        }
        return msgs;
    }

    @Override
    public void stop() {
        inBuffer = null;
        outBuffer = null;
    }

}
