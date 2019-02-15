
package com.example.chatting;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ChattingConnectionService extends Service {

    private BroadcastReceiver mBroadcastReceiver;

    private static final String TAG ="ChattingConnService";

    public static final String UI_AUTHENTICATED = "com.example.chatting.uiauthenticated";
    public static final String SEND_MESSAGE = "com.example.chatting.sendmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";
    public static final String NEW_MESSAGE = "com.example.chatting.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";
    public static final String DISCONNECTED = "com.example.chatting.disconnected";


    public static ChattingConnection.ConnectionState sConnectionState;
    public static ChattingConnection.LoggedInState sLoggedInState;

    private static ChattingConnectionService instance = null;
    private static XMPPConnection mXmppTcpConnection = null;

    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.

    private ChattingConnection mConnection;

    public ChattingConnectionService() {

    }

    public static ChattingConnection.ConnectionState getState()
    {
        if (sConnectionState == null)
        {
            return ChattingConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static ChattingConnection.LoggedInState getLoggedInState()
    {
        if (sLoggedInState == null)
        {
            return ChattingConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
    }


    private void initConnection()
    {
        Log.d(TAG,"initConnection()");
        if( mConnection == null)
        {
            mConnection = new ChattingConnection(this);
        }
        try
        {
            mConnection.connect();

        }catch (IOException | SmackException | XMPPException e)
        {
            Log.d(TAG,"Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }
    }

    public void start()
    {
        Log.d(TAG," Service Start() function called.");
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();
                    }
                });
                mThread.start();
            }
        }
    }

    public void stop()
    {
        Log.d(TAG,"stop()");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if( mConnection != null)
                    mConnection.disconnect();
                //CODE HERE IS MEANT TO SHUT DOWN OUR CONNECTION TO THE SERVER.
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
        stop();
    }

    public /*List<RosterEntry>*/Roster /*void*/ getBuddies() throws SmackException.NotLoggedInException,
            InterruptedException, SmackException.NotConnectedException {

        Log.d(TAG,"getBuddies() ");

        mXmppTcpConnection = XMPPConn.getInstance().getConnection();

       Roster roster = Roster.getInstanceFor(mXmppTcpConnection);

       if (!roster.isLoaded())
           roster.reloadAndWait();

          return roster;
    }


    public static ChattingConnectionService getInstance() {
        if(instance==null){
            instance = new ChattingConnectionService();
        }
        return instance;
    }
}