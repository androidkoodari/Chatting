package com.example.chatting;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ContactModel {

    private static ContactModel sContactModel;
    private List<Contact> mContacts;

    public static ContactModel get(Context context)
    {
        if(sContactModel == null)
        {
            sContactModel = new ContactModel(context);
        }
        return  sContactModel;
    }

    private ContactModel(Context context)
    {
        mContacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    private void populateWithInitialContacts(Context context)
    {

     //  Contact contact1 = new Contact("User1@server.com");
     //   mContacts.add(contact1);

    }

    public List<Contact> getContacts()
    {
        return mContacts;
    }


}
