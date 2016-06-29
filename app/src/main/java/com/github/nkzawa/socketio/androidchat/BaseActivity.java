package com.github.nkzawa.socketio.androidchat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;

import io.socket.client.Socket;


/**
 * Created by ASUS on 6/29/2016.
 */
public class BaseActivity extends AppCompatActivity {

    public String mUsername;
    public Socket mSocket;
    public String mUserID;

    public Boolean isConnected = true;
    public RecyclerView mUserListView;
    ArrayList<User> mUserList = new ArrayList<User>();
    User mReceiveUser;
    Button mLogOutButton;
    public PrefsValues prefsValues;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);
        mUsername = prefsValues.getUserName();
    }
}
