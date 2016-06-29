/**
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.snmp4sdn.shell;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opendaylight.snmp4sdn.ICore;

@Command(scope = "snmp4sdn", name = "TopoDiscoverSwitches", description="Inventory discovery (collect switches and ports)")
public class TopoDiscoverSwitch extends OsgiCommandSupport{
    private ICore controller;

    @Override
    protected Object doExecute() throws Exception {
        controller.topoDiscoverSwitch();
        return null;
    }

    public void setController(ICore controller){
        this.controller = controller;
    }
}
