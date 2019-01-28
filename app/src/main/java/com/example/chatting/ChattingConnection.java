package com.example.chatting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;



public class ChattingConnection implements ConnectionListener{


    private static final String TAG = "ChattingConnection";

    private  final Context mApplicationContext;
    private  final String mUsername;
    private  final String mPassword;
    private  final String mServiceName;
    private XMPPTCPConnection mConnection;


    public static enum ConnectionState
    {
        CONNECTED ,AUTHENTICATED, CONNECTING ,DISCONNECTING ,DISCONNECTED;
    }

    public static enum LoggedInState
    {
        LOGGED_IN , LOGGED_OUT;
    }


    public ChattingConnection( Context context)
    {

        Log.d(TAG,"ChattingConnection Constructor called.");
        mApplicationContext = context.getApplicationContext();
        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_jid",null);
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_password",null);

        if( jid != null)
        {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        }else
        {
            mUsername ="";
            mServiceName="";
        }
    }


    public void connect() throws IOException,XMPPException,SmackException
    {
        Log.d(TAG, "Connecting to server " + mServiceName);

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();

        config.setXmppDomain(mServiceName);
        config.setUsernameAndPassword(mUsername, mPassword);
        //config.setRosterLoadedAtLogin(true);
        config.setResource("Chatting");

        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled); //*******
        config.setDebuggerEnabled(true);//********
       // config.setPort(5222);//***

        //Set up the ui thread broadcast message receiver.
        //setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(config.build());
        mConnection.addConnectionListener(this);

        try {
            mConnection.connect();
            if(mConnection.isConnected())
            {
                Log.d(TAG, "Connected to server " + mServiceName);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            mConnection.login();
        }
        catch (InterruptedException e) {
        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }



    public void disconnect()
    {
        Log.d(TAG,"Disconnecting from server "+ mServiceName);

   /*     try
        {
            if (mConnection != null)
            {
                mConnection.disconnect();
            }

        }catch (SmackException.NotConnectedException e)        {
            ChattingConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
            e.printStackTrace();

        }
        mConnection = null;

*/
    }


    @Override
    public void connected(XMPPConnection connection) {
        ChattingConnectionService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Connected Successfully");

    }

    @Override
    public void authenticated(XMPPConnection connection,boolean resumed) {
        ChattingConnectionService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Authenticated Successfully");

        showContactListActivityWhenAuthenticated();
    }

    @Override
    public void connectionClosed() {
        ChattingConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        ChattingConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        ChattingConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG,"ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        ChattingConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG,"ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        ChattingConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG,"ReconnectionFailed()");

    }

    private void showContactListActivityWhenAuthenticated()
    {
        Intent i = new Intent(ChattingConnectionService.UI_AUTHENTICATED);
        i.setPackage(mApplicationContext.getPackageName());
        mApplicationContext.sendBroadcast(i);
        Log.d(TAG,"Sent the broadcast that we are authenticated");
    }
}



