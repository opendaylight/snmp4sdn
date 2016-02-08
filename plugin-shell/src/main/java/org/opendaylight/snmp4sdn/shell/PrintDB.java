/**
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.snmp4sdn.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opendaylight.snmp4sdn.ICore;

@Command(scope = "snmp4sdn", name = "PrintDB", description="Read the switch list file")
public class PrintDB extends OsgiCommandSupport{
    private ICore controller;

    @Override
    protected Object doExecute() throws Exception {
        controller.printDB();
        return null;
    }

    public void setController(ICore controller){
        this.controller = controller;
    }
}
