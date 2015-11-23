/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code of OpenFlow Java package contributed by David Erickson, Inc. His/Her efforts are appreciated.
*/

package org.opendaylight.snmp4sdn.protocol;

public interface Instantiable<E> {

    /**
     * Create a new instance of a given subclass.
     * @return the new instance.
     */
    public E instantiate();
}
