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

@Command(scope = "snmp4sdn", name = "DeleteVlan", description="Delete VLAN from the switch")
public class DeleteVlan extends OsgiCommandSupport{
    //private IKarafVLANService vlanService;

    @Argument(index=0, name="sw_mac", description="Target switch's MAC address", required=true, multiValued=false)
    String sw_mac = null;
    @Argument(index=1, name="vlanID", description="The VLAN ID to add to the switch", required=true, multiValued=false)
    String vlanID = null;

    @Override
    protected Object doExecute() throws Exception {
        IKarafVLANService vlanService = (IKarafVLANService) ServiceHelper.getGlobalInstance(IKarafVLANService.class, this);
        vlanService.deleteVLAN(sw_mac, vlanID);
        return null;
    }

    public void setController(IKarafVLANService vs){
        //this.vlanService = vs;
    }
}
