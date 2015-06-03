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

@Command(scope = "snmp4sdn", name = "PrintVlanTable", description="Print the VLAN Table on the switch")
public class PrintVlanTable extends OsgiCommandSupport{
    //private IKarafVLANService vlanService;

    @Argument(index=0, name="sw_mac", description="Target switch's MAC address", required=true, multiValued=false)
    String sw_mac = null;

    @Override
    protected Object doExecute() throws Exception {
        IKarafVLANService vlanService = (IKarafVLANService) ServiceHelper.getGlobalInstance(IKarafVLANService.class, this);
        vlanService.printVLANTable(sw_mac);
        return null;
    }

    public void setController(IKarafVLANService vs){
        //this.vlanService = vs;
    }
}
