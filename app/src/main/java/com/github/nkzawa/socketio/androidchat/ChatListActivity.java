package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.socket.client.Socket;


public class ChatListActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;

    //private RecyclerView.Adapter mUserAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    //private String mUsername;
    //private Socket mSocket;
    private String mUserID;

    private Boolean isConnected = true;
    private RecyclerView mUserListView;
    //ArrayList<User> mUserList = new ArrayList<User>();
    User mReceiveUser;
    Button mLogOutButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Job Town Chat");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //toolbar.setLogo(R.drawable.ic_launcher);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connetSocketAndListener();
        initiateUI();

        Log.e(" onCreate", "ChatListActivity Called : ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.e(" onStart ", "ChatListActivity Called : ");
        UserRegistration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(" onResume ", "ChatListActivity Called : ");

        //connetSocketAndListener();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.e(" onPause ", "ChatListActivity Called : ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(" onDestroy ", "ChatListActivity Called : ");
        //mSocket.disconnect();
        //mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        //mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        //mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        //mSocket.off("new message", onNewMessage);
        //.off("user joined", onUserJoined);
        //mSocket.off("user left", onUserLeft);
        // mSocket.off("say to someone", onSayToSomeone);
        //mSocket.off("user_registration", user_registration);
        //mSocket.off("get_Offline_Message", getOfflineMessage);
    }

    public void connetSocketAndListener() {

//        ChatApplication app = (ChatApplication) getApplication();
//        mSocket = app.getSocket();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("user_registration", user_registration);

        //mSocket.on("getOfflineMessage", getOfflineMessage);

        mSocket.connect();

    }

    public void initiateUI() {

        mUsername = prefsValues.getUserName();
        getSupportActionBar().setTitle(" I am " + mUsername);
        mUserListView = (RecyclerView) findViewById(R.id.userList);
        mUserListView.setLayoutManager(new LinearLayoutManager(this));
        mUserAdapter = new UserListAdapter(this, mUserList);
        mUserListView.setAdapter(mUserAdapter);

        mUserListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                mUserListView, new MainFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                mReceiveUser = mUserList.get(position);
                Toast.makeText(getApplicationContext(), mReceiveUser.getUserName() + " is selected!", Toast.LENGTH_SHORT).show();

                goToPrivateChatwindow(mReceiveUser.getUserName(), mReceiveUser.getEmail());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitApp();
    }

}
