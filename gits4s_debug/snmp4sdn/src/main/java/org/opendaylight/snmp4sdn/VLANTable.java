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


public class VLANTable{//TODO: on document, say to use List<VLANConfig> instead of VLANTable
    private Vector table = null;

    public VLANTable(){
        table = new Vector();
    }

    public void addEntry(Integer vlanID, List<NodeConnector> ports){//TODO: not yet implemnent in snmphandler to get vlan name
        table.add(new VLANTableEntry(vlanID, new String("v" + vlanID.toString()), ports));//TODO: vlan name shouldn't be assigned by program
    }

    public Vector getEntries(){
        return table;
    }

    public List<NodeConnector> getPorts(Integer vlanID){
        VLANTableEntry entry = null;
        for(int i = 0; i < table.size(); i ++){
            entry = (VLANTableEntry)(table.get(i));
            if(entry.getVlanID().equals(vlanID))
            return entry.getPorts();
        }
        return null;
    }

    public String toString(){
        VLANTableEntry entry = null;
        String ans = "";
        for(int i = 0; i < table.size(); i ++){
            entry = (VLANTableEntry)(table.get(i));
            ans += "VLAN(" + entry.getVlanID() + "): ports {";
            List<NodeConnector> ports = entry.getPorts();
            for(int j = 0; j < ports.size(); j++)
                ans += (Short)(ports.get(j).getID()) + ",";
            if(ans.endsWith(",")) ans = ans.substring(0, ans.length() -1);
            ans += "}\n";
        }
        return ans;
    }
    
    public class VLANTableEntry{
        private Integer vlanID = null;
        private String vlanName = null;
        private List<NodeConnector> ports = null;
        
        public VLANTableEntry(Integer vlanID, String vlanName, List<NodeConnector> ports){
            this.vlanID = new Integer(vlanID);
            this.vlanName = new String(vlanName);
            this.ports = new ArrayList<NodeConnector>(ports);
        }
    
        public Integer getVlanID(){
            return new Integer(this.vlanID);
        }

        public String getVlanName(){
            return new String(this.vlanName);
        }

        public List<NodeConnector> getPorts(){
            return this.ports;//TODO: should create a new List and clone, and then return
        }
    }
    
}
