package com.example.chatting;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.security.AccessController.getContext;


public class ContactListActivity extends AppCompatActivity{

    private static final String TAG = "ContactListActivity";
    private RecyclerView contactsRecyclerView;
    private ContactAdapter mAdapter;
    private ChattingConnectionService mChattingConnectionService;
    private Roster mRoster;

    ContactModel model = ContactModel.get(getBaseContext());
    List<Contact> contacts = model.getContacts();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);


        contactsRecyclerView = (RecyclerView) findViewById(R.id.contact_list_recycler_view);

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mAdapter = new ContactAdapter(contacts);
        contactsRecyclerView.setAdapter(mAdapter);
        mChattingConnectionService = ChattingConnectionService.getInstance();

        try{
            mRoster = mChattingConnectionService.getBuddies();
        }
        catch (InterruptedException| SmackException.NotLoggedInException |
                SmackException.NotConnectedException e){
        }
        updateContactlist();
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
                    Intent intent = new Intent(ContactListActivity.this
                            , ChatActivity.class);
                    intent.putExtra("EXTRA_CONTACT_JID", mContact.getJid());
                    startActivity(intent);
                }
            });
        }


        public void bindContact( Contact contact)
        {
            mContact = contact;
            if (mContact == null)
            {
                Log.d(TAG,"Trying to work on a null Contact object ,returning.");
                return;
            }
            contactTextView.setText(mContact.getJid());

        }
    }



    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder>
    {
        private List<Contact> mContacts;

        public ContactAdapter( List<Contact> contactList)
        {
            mContacts = contactList;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.list_item_contact, parent,
                            false);
            return new ContactHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            Contact contact = mContacts.get(position);
            holder.bindContact(contact);

        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }
    }

    public void updateContactlist(){

        Log.d(TAG,"updateContactlist() ");

        Collection<RosterEntry> entries = mRoster.getEntries();

        List<RosterEntry> roasterInfo = new ArrayList<RosterEntry>();
        for (RosterEntry entry : entries) {
            roasterInfo.add(entry);
            Log.d(TAG,"Here: " + entry.toString());
           // Log.d(TAG,"User: " + entry.getUser());//get userinfo
            Log.d(TAG,"User Name:"+entry.getName());//get username
           // Log.d(TAG,"User Status: "+entry.getStatus()); //get status of user

            Contact contact = new Contact(entry.toString());
            contacts.add(contact);
        }
    }
}
