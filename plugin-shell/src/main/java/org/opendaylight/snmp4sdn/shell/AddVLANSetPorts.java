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
import org.opendaylight.snmp4sdn.IKarafVLANService;
import org.opendaylight.controller.sal.utils.ServiceHelper;

@Command(scope = "snmp4sdn", name = "AddVlanSetPorts", description="Add VLAN and set the VLAN ports on the switch")
public class AddVLANSetPorts extends OsgiCommandSupport{
    //private IKarafVLANService vlanService;

    @Argument(index=0, name="sw_mac", description="Target switch's MAC address", required=true, multiValued=false)
    String sw_mac = null;
    @Argument(index=1, name="vlanID", description="The VLAN ID to add to the switch", required=true, multiValued=false)
    String vlanID = null;
    @Argument(index=2, name="vlanName", description="The name of the specified VLAN", required=true, multiValued=false)
    String vlanName = null;
    @Argument(index=3, name="portList", description="The ports of the specified VLAN (seperated by comma)", required=true, multiValued=false)
    String portList = null;

    @Override
    protected Object doExecute() throws Exception {
        IKarafVLANService vlanService = (IKarafVLANService) ServiceHelper.getGlobalInstance(IKarafVLANService.class, this);
        vlanService.addVLANSetPorts(sw_mac, vlanID, vlanName, portList);
        return null;
    }

    public void setVLANService(IKarafVLANService vs){
        //this.vlanService = vs;
    }
}
