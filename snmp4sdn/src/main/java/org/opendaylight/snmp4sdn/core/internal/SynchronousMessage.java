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

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.opendaylight.snmp4sdn.core.ISwitch;
import org.openflow.protocol.OFBarrierRequest;
import org.openflow.protocol.OFError;
import org.openflow.protocol.OFMessage;

import org.opendaylight.snmp4sdn.protocol.SNMPMessage;//s4s add

/**
 * This class implements synchronous operations on message send to a switch. If
 * syncRequest is set to true, it sends the requested message to the switch
 * followed by a Barrier request message. It returns the result once it gets the
 * reply from the switch or after a timeout. If the protocol does not dictate
 * the switch to reply the processing status for a particular message, the
 * Barrier request forces the switch to reply saying whether or not the message
 * processing was successful for messages sent to the switch up to this point.
 * If syncRequest is false, it simply skips the message send and just waits for
 * the response back.
 */
public class SynchronousMessage implements Callable<Object> {
    private ISwitch sw;
    private Integer xid;
    private SNMPMessage syncMsg;
    protected CountDownLatch latch;
    private Object result;
    private boolean syncRequest;

    public SynchronousMessage(ISwitch sw, Integer xid, SNMPMessage msg,
            boolean syncRequest) {
        this.sw = sw;
        this.xid = xid;
        syncMsg = msg;
        latch = new CountDownLatch(0);//s4s. in OF code, it is given new CountDownLatch(1) so that the wakeup() must be called once to make the varaible "latch" become 0, then the call() can finish. In all codes, only function about barrier would call wakeup() ==> So, I think here I given new CountDownLatch(0) instead is right.
        result = null;
        this.syncRequest = syncRequest;
    }

    @Override
    public Object call() throws Exception {
        /*
         * Send out message only if syncRequest is set to true. Otherwise, just
         * wait for the Barrier response back.
         */
        if (syncRequest) {
            sw.asyncSend(syncMsg, xid);
        }
        latch.await();
        return result;
    }

    public Integer getXid() {
        return this.xid;
    }

    public void wakeup() {
        this.latch.countDown();
    }

    public void wakeup(OFError e) {
        result = e;
        wakeup();
    }

}
