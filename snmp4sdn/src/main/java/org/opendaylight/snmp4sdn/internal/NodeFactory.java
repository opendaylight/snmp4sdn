/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.utils.INodeFactory;
import org.opendaylight.controller.sal.core.Node;

public class NodeFactory implements INodeFactory
    {
      void init() {
      }

      /**
       * Function called by the dependency manager when at least one dependency
       * become unsatisfied or when the component is shutting down because for
       * example bundle is being stopped.
       *
       */
      void destroy() {
      }

      /**
       * Function called by dependency manager after "init ()" is called and after
       * the services provided by the class are registered in the service registry
       *
       */
      void start() {
      }

      /**
       * Function called by the dependency manager before the services exported by
       * the component are unregistered, this will be followed by a "destroy ()"
       * calls
       *
       */
      void stop() {
      }

      public Node fromString(String nodeType, String nodeId){
          if(nodeType.equals("SNMP"))
              try{
                  return new Node("SNMP", Long.parseLong(nodeId));
              } catch(ConstructionException e)
              {
                  return null;
              }
          return null;
      }
}
