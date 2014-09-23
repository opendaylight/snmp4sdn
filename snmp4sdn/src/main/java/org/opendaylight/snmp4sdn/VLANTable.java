/*
 * Copyright (c) 2014 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn;

import org.opendaylight.controller.sal.core.NodeConnector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class VLANTable{
    private Vector table = null;

    public VLANTable(){
        table = new Vector();
    }

    public void addEntry(Long vlanID, List<NodeConnector> ports){
        table.add(new VLANTableEntry(vlanID, ports));
    }

    public List<NodeConnector> getPorts(Long vlanID){
        VLANTableEntry entry = null;
        for(int i = 0; i < table.size(); i ++){
            entry = (VLANTableEntry)(table.get(i));
            if(entry.equals(vlanID))
            return entry.getPorts();
        }
        return null;
    }

    public String toString(){
        VLANTableEntry entry = null;
        String ans = "";
        for(int i = 0; i < table.size(); i ++){
            entry = (VLANTableEntry)(table.get(i));
            ans += "VLAN(" + entry.getVlanID() + "): {";
            List<NodeConnector> ports = entry.getPorts();
            for(int j = 0; j < ports.size(); j++)
                ans += (Short)(ports.get(j).getID()) + ",";
            if(ans.endsWith(",")) ans = ans.substring(0, ans.length() -1);
            ans += "}\n";
        }
        return ans;
    }
    
    public class VLANTableEntry{
        private Long vlanID = null;
        private List<NodeConnector> ports = null;
        
        public VLANTableEntry(Long vlanID, List<NodeConnector> ports){
            this.vlanID = new Long(vlanID);
            this.ports = new ArrayList<NodeConnector>(ports);
        }
    
        public Long getVlanID(){
            return this.vlanID;
            }
    
        public List<NodeConnector> getPorts(){
            return this.ports;
        }
    }
    
}
