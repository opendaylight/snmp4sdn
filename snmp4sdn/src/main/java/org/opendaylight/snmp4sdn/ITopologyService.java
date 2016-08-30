/*
 * Copyright (c) 2016 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

import java.util.List;
import org.opendaylight.snmp4sdn.sal.core.Edge;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;

public interface ITopologyService {
    public void setMdNotifService(NotificationProviderService notifService);
    public void unsetMdNotifService(NotificationProviderService notifService);
}
