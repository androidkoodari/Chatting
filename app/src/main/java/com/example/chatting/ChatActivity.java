package com.example.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private TextView mChatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView =(TextView) findViewById(R.id.chatting_chat_view);

        Intent intent = getIntent();
        String contactJid = intent.getStringExtra("EXTRA_CONTACT_JID");
        setTitle(contactJid);
    }
}
