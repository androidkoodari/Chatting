package com.example.chatting;


public class Message {

    private String jid;

    public Message(String messageJid)
    {
        jid = messageJid;
    }


    public String getJid()
    {
        return jid;
    }

    public void setJid(String jid)
    {
        this.jid = jid;

    }
}
