package com.example.chatting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class ContactListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
    }

    private class ContactHolder extends RecyclerView.ViewHolder
    {
        private TextView contactTextView;
        private Contact mContact;
        public ContactHolder ( View itemView)
        {
            super(itemView);

            contactTextView = (TextView) itemView.findViewById(R.id.contact_jid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Inside here we start the chat activity

                }
            });
        }


        public void bindContact( Contact contact)
        {
            mContact = contact;
            if (mContact == null)
            {
               // Log.d(TAG,"Trying to work on a null Contact object ,returning.");
                Log.d("CurrentClassName", "Trying to work on a null Contact object ,returning.: ");
                return;
            }
            contactTextView.setText(mContact.getJid());

        }
    }
}
