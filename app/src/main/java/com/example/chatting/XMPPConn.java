package com.example.chatting;

import org.jivesoftware.smack.XMPPConnection;

public class XMPPConn {

    private XMPPConnection connection = null;

    private static XMPPConn instance = null;

    public synchronized static XMPPConn getInstance() {
        if(instance==null){
            instance = new XMPPConn();
        }
        return instance;
    }

    public void setConnection(XMPPConnection connection){
        this.connection = connection;
    }

    public XMPPConnection getConnection() {
        return this.connection;
    }

}
