/*
 * Copyright (c) 2015 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.snmp4sdn.internal;

import java.io.*; 
import java.util.*; 
import org.dom4j.*; 
import org.dom4j.io.*; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;

import org.opendaylight.snmp4sdn.VsFunctionName;
import org.opendaylight.snmp4sdn.VlanAttributeTag;

import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

public class VenderSpecificHandler implements CommandProvider{
    private static final Logger logger = LoggerFactory.getLogger(VenderSpecificHandler.class);

    CmethUtil cmethUtil;

    //parser tutorial: http://blog.xuite.net/javax/programmer/22438335
    Document doc;
    Element root;
    String defaultConfigFilePath = "/etc/snmp4sdn_VendorSpecificSwitchConfig.xml";
    //String defaultConfigFilePath = "D:\\svns4s\\controller\\opendaylight\\protocol_plugins\\snmp4sdn\\snmp4sdn_VendorSpecificSwitchConfig.xml";

    public VenderSpecificHandler(CmethUtil cmethUtil){
        this.cmethUtil = cmethUtil;
        boolean bool = readConfigFile(defaultConfigFilePath);
        if(!bool){
            logger.debug("ERROR: VenderSpecificHandler(): call readConfigFile() given config file {} fail", defaultConfigFilePath);
        }
    }

    //read the xml config file
    private boolean readConfigFile(String filePath){
        try{
            File f = new File(filePath);
            SAXReader reader = new SAXReader();
            doc = reader.read(f);
            root = doc.getRootElement();
        }catch(Exception e1){
            logger.debug("ERROR: readConfig(): open file {} for SAXReader fail", defaultConfigFilePath, e1);
            return false;
        }
        return true;
    }

    private Element getSwitchFunctionConfig(Node node, VsFunctionName funcName){
        Long nodeID = (Long)node.getID();
        String modelName = cmethUtil.getModel(nodeID);
        return getSwitchFunctionConfig(modelName, funcName);
    }

    private Element getSwitchFunctionConfig(Long nodeID, VsFunctionName funcName){
        String modelName = cmethUtil.getModel(nodeID);
        return getSwitchFunctionConfig(modelName, funcName);
    }
    
    private Element getSwitchFunctionConfig(String modelName, VsFunctionName funcName){
        //return doc's sub-tree, given model name and function name
        for (Iterator i = root.elementIterator("SwitchConfig"); i.hasNext(); ){
            Element foo = (Element) i.next();
            String modelNameValue = foo.elementText("model");
            if(modelNameValue.trim().equals(modelName)){
                for (Iterator j = foo.elementIterator("FunctionConfig"); j.hasNext(); ){
                    Element foo2 = (Element) j.next();
                    if(foo2.elementText("function_name").trim().equals(funcName.toString()))
                        return foo2;
                }
            }
        }

        logger.debug("ERROR: getSwitchFunctionConfig(): can't find the config for switch {} function_name {}", modelName, funcName);
        return null;
    }

    private List<String> parseAttributesToList(String attrsStr){
        List<String> attrList = new ArrayList<String>();
        int i = attrsStr.indexOf("${");
        int j = attrsStr.indexOf("}$");
        while(i < j){
            String attr = attrsStr.substring(i + 2, j);
            attrList.add(new String(attr));
            i = attrsStr.indexOf("${", i + 1);
            j = attrsStr.indexOf("}$", j + 1);
        }

        //just print for debugging
        String tmpStr = "";
        for(String str : attrList)
            tmpStr += str;
        logger.trace("parseAttributesToList(): convert \"{}\" to List<String>, here's the converted List<String>: {}", attrsStr, tmpStr);

        return attrList;
    }

    public Status addVLANandSetPorts(long nodeID, String vlanName, int vlanID, int taggedPortList[], int untaggedPortList[]){
        Element funcCfg = getSwitchFunctionConfig(nodeID, VsFunctionName.addVLANandSetPorts);
        if(funcCfg == null){
            logger.debug("ERROR: addVLANandSetPorts(): for setting nodeID {} vlanID {}, call getSwitchFunctionConfig(), given nodeID {} and function name {}, fail", nodeID, vlanID, nodeID, VsFunctionName.addVLANandSetPorts);
            return new Status(StatusCode.INTERNALERROR, "ERROR");
        }

            String channel = funcCfg.elementText("channel");
            channel = channel.trim();
            if(channel.equals("snmp")){
                    //put the commands in a List of List of String, and pass to SNMPHandler.addVLANandSetPortstoSwitch()
                    Element commands = funcCfg.element("commands_with_items_in_order");
                    List<List<String>> cmdList = new ArrayList<List<String>>();
                    //TODO: check null in the following for loop
                    for(Iterator i = commands.elementIterator("command"); i.hasNext();){
                        Element cmd = (Element) i.next();
                        String attributes = cmd.getText();
                        List<String> attrList = parseAttributesToList(attributes);
                        cmdList.add(attrList);
                    }
                    Status status = new SNMPHandler(cmethUtil).addVLANandSetPorts(nodeID, vlanName, vlanID, taggedPortList, untaggedPortList, cmdList);
                    return status;
            }
            else if(channel.equals("telnet")){
                    logger.debug("ERROR: addVLANandSetPorts(): given Node {}, configuration via telnet is not supported!", nodeID);
                    return new Status(StatusCode.INTERNALERROR, "ERROR");
            }
            else{
                    logger.debug("ERROR: addVLANandSetPorts(): given Node {}, configuration via what channel is not given!", nodeID);
                    return new Status(StatusCode.INTERNALERROR, "ERROR");
            }
    }


    private void registerWithOSGIConsole() {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                .getBundleContext();
        bundleContext.registerService(CommandProvider.class.getName(), this,
                null);
    }

    @Override
    public String getHelp() {
        StringBuffer help = new StringBuffer();
        help.append("---SNMP4SDN VenderSpecificHandler---\n");
        help.append("\t readVsConfig <config file path>\n");
        return help.toString();
    }

    public void _s4sReadVsConfig(CommandInterpreter ci){
        readConfigFile(ci.nextArgument());
    }
}


