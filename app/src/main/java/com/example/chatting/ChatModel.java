package com.example.chatting;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ChatModel {

    private static ChatModel sChatModel;
    private List<Message> mMessages;

    public static ChatModel get(Context context)
    {
        if(sChatModel == null)
        {
            sChatModel = new ChatModel(context);
        }
        return  sChatModel;
    }

    private ChatModel(Context context)
    {
        mMessages = new ArrayList<>();
        populateWithInitialMessages(context);
    }

    private void populateWithInitialMessages(Context context)
    {

           Message message1 = new Message("test message");
           mMessages.add(message1);

    }

    public List<Message> getMessages()
    {
        return mMessages;
    }


}
