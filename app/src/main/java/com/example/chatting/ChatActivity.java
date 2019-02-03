package com.example.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {


    private static final String TAG = "ChatActivity";

    private RecyclerView mChatView = null;
    private Button mSendButton = null;
    private String contactJid = null;
    private EditText mEdittext_chatbox = null;
    private Intent JidIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView =(RecyclerView) findViewById(R.id.chat_view);
        mEdittext_chatbox = (EditText) findViewById(R.id.edittext_chatbox);
        mSendButton =(Button) findViewById(R.id.button_chatbox_send);

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
}
