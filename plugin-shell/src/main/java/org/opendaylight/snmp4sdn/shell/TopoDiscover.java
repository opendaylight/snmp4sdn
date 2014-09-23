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
import org.opendaylight.snmp4sdn.IKarafCore;
import org.opendaylight.controller.sal.utils.ServiceHelper;

@Command(scope = "snmp4sdn", name = "TopoDiscover", description="Topology Discovery (collect topology among switches)")
public class TopoDiscover extends OsgiCommandSupport{
    //private IKarafCore controller;

    @Override
    protected Object doExecute() throws Exception {
        IKarafCore controller = (IKarafCore) ServiceHelper.getGlobalInstance(IKarafCore.class, this);
        controller.topoDiscover();
        return null;
    }

    public void setController(IKarafCore controller){
        //this.controller = controller;
    }
}
