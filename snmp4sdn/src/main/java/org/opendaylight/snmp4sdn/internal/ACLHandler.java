/*
 * Copyright (c) 2013 Industrial Technology Research Institute of Taiwan and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
This code reused the code base of OpenFlow plugin contributed by Cisco Systems, Inc. Their efforts are appreciated.
*/

 package org.opendaylight.snmp4sdn.internal;

import org.opendaylight.snmp4sdn.internal.util.CmethUtil;

import java.io.*;
import java.net.Socket;

public class ACLHandler{



}

class Telnet{

    String host;
    int portNum;

    private void Telnet() {
        host="Telnet IP";
        portNum=23;
        System.out.println("host:"+host+"\n"+"port:"+portNum);
        try {
            Socket s=new Socket(host,portNum);
            new Pipe(s.getInputStream(),System.out).start();
            new Pipe(System.in,s.getOutputStream()).start();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println("Connection OK!");
    }

    public static void main(String[] args) {
        Telnet telnet1 = new Telnet();
    }
}

class Pipe extends Thread{
    DataInputStream InS;
    PrintStream OutS;

    public Pipe(InputStream InS,OutputStream OutS){
        this.InS=new DataInputStream(InS);
        this.OutS=new PrintStream(OutS);
    }
    public void run(){
        String line;
        try {
            while((line=InS.readLine())!=null){
                OutS.print(line);
                OutS.print("\r\n");
                OutS.flush();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

