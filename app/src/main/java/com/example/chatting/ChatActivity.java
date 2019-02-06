package com.example.chatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ChatActivity extends AppCompatActivity {


    private static final String TAG = "ChatActivity";

    private RecyclerView mChatView = null;
    private Button mSendButton = null;
    private String contactJid = null;
    private EditText mEdittext_chatbox = null;
    private Intent JidIntent = null;
    private BroadcastReceiver mBroadcastReceiver;
    private RecyclerView mChatRecyclerView;
    private MessageAdapter mAdapter;

    ChatModel model = ChatModel.get(getBaseContext());
    List<Message> chatMessages = model.getMessages();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView =(RecyclerView) findViewById(R.id.chat_view);
        mEdittext_chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        mSendButton =(Button) findViewById(R.id.button_chatbox_send);

        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_view);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mAdapter = new MessageAdapter(chatMessages);
        mChatRecyclerView.setAdapter(mAdapter);

        /*mChatView.setEventListener(new ChatViewEventListener() {
            @Override
            public void userIsTyping() {
                //Here you know that the user is typing
            }

            @Override
            public void userHasStoppedTyping() {
                //Here you know that the user has stopped typing.
            }
        });*/

      //  mSendButton = mChatView.getSendButton();

        JidIntent = getIntent();
        contactJid = JidIntent.getStringExtra("EXTRA_CONTACT_JID");
        setTitle(contactJid);

         mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ChattingConnectionService.getState().equals(ChattingConnection.ConnectionState.CONNECTED)) {
                    Log.d(TAG, "The client is connected to the server,Sendint Message");
                    //Send the message to the server

                    Intent intent = new Intent(ChattingConnectionService.SEND_MESSAGE);

                    intent.putExtra(ChattingConnectionService.BUNDLE_MESSAGE_BODY, mEdittext_chatbox.getText().toString());

                    intent.putExtra(ChattingConnectionService.BUNDLE_TO, contactJid);

                    sendBroadcast(intent);

                    //Update the chat view.
                 //   mChatView.sendMessage();

                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Client not connected to server ,Message not sent!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class MessageHolder extends RecyclerView.ViewHolder {

        private TextView messageTextView;
        private Message mMessage;

        public MessageHolder(View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.message_jid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        public void bindMessage( Message message)
        {
            mMessage = message;
            if (mMessage == null)
            {
                Log.d(TAG,"Trying to work on a null Message object ,returning.");
                return;
            }
            messageTextView.setText(mMessage.getJid());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action)
                {
                    case ChattingConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(ChattingConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(ChattingConnectionService.BUNDLE_MESSAGE_BODY);

                        if ( from.equals(contactJid))
                        {
                           /* mChatView.*/receiveMessage(body);

                        }else
                        {
                            Log.d(TAG,"Got a message from jid :"+from);
                        }

                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(ChattingConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver,filter);

    }


    public void receiveMessage(String body){

        Log.d(TAG,"receiveMessage()  Got a message :"+body);

        Message message = new Message(body.toString());

        Log.d(TAG,"receiveMessage()  Got a message toString() :"+body);

        chatMessages.add(message);
        mAdapter.notifyDataSetChanged();

    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder>
    {
        private List<Message> mMessages;

        public MessageAdapter( List<Message> messageList)
        {
            mMessages = messageList;
        }

        @Override
        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.list_item_message, parent,
                            false);
            return new MessageHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.bindMessage(message);

        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
        }


    }

