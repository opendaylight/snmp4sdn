/**
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.opendaylight.snmp4sdn.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opendaylight.snmp4sdn.IKarafFlowProgrammerService;
import org.opendaylight.controller.sal.utils.ServiceHelper;

@Command(scope = "snmp4sdn", name = "AddFlow", description="Add VLAN and set the VLAN ports on the switch")
public class AddFlow extends OsgiCommandSupport{
    //private IKarafFlowProgrammerService flowprogrammer;

    @Argument(index=0, name="sw_mac", description="Target switch's MAC address", required=true, multiValued=false)
    String sw_mac = null;
    @Argument(index=1, name="vlanId", description="VLAN ID of the flow", required=true, multiValued=false)
    String vlanIdStr = null;
    @Argument(index=2, name="dstMac", description="Destination MAC address of the flow", required=true, multiValued=false)
    String dstMacStr = null;
    @Argument(index=3, name="portNum", description="Output port number of the flow", required=true, multiValued=false)
    String portNumStr = null;

    @Override
    protected Object doExecute() throws Exception {
        IKarafFlowProgrammerService flowprogrammer = (IKarafFlowProgrammerService) ServiceHelper.getGlobalInstance(IKarafFlowProgrammerService.class, this);
        flowprogrammer.krfAddFlow(sw_mac, vlanIdStr, dstMacStr, portNumStr);
        return null;
    }

    public void setVLANService(IKarafFlowProgrammerService fps){
        //this.flowprogrammer = fps;
    }
}
