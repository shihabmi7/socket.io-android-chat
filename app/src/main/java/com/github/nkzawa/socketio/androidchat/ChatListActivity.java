package com.github.nkzawa.socketio.androidchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatListActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mUsrAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername;
    private Socket mSocket;
    private String mUserID;

    private Boolean isConnected = true;
    private RecyclerView mUserListView;
    ArrayList<User> mUserList = new ArrayList<User>();
    User mReceiveUser;
    Button mLogOutButton;
    private PrefsValues prefsValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Log.e(" onCreate", "ChatListActivity Called : " );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(" onStart ", "ChatListActivity Called : " );
        UserRegistration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(" onResume ", "ChatListActivity Called : " );

        //connetSocketAndListener();

    }
    @Override
    protected void onPause() {

        super.onPause(); Log.e(" onPause ", "ChatListActivity Called : " );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(" onDestroy ", "ChatListActivity Called : " );
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        // mSocket.off("say to someone", onSayToSomeone);
        mSocket.off("user_registration", user_registration);
        mSocket.off("get_Offline_Message", getOfflineMessage);
    }

    public void connetSocketAndListener() {

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("user_registration", user_registration);
        mSocket.on("getOfflineMessage", getOfflineMessage);

        mSocket.connect();

    }

    private void UserRegistration() {

        mSocket.emit("user_registration", mUsername, mUsername + "@gmail.com");
        Log.e("user_registration", "Called : " + mUsername);
    }


    public void initiateUI() {

        // shihab

        //int numUsers = getIntent().getIntExtra("numUsers", 1);

        // addLog(getResources().getString(R.string.message_welcome));
        // addParticipantsLog(numUsers);


        prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);

        mUsername = prefsValues.getUserName();

        getSupportActionBar().setTitle(" I am "+mUsername);

        mUserListView = (RecyclerView) findViewById(R.id.userList);
        mUserListView.setLayoutManager(new LinearLayoutManager(this));
        mUsrAdapter = new UserListAdapter(this, mUserList);
        mUserListView.setAdapter(mUsrAdapter);

        mUserListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mMessagesView, new MainFragment.ClickListener() {
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

    void goToPrivateChatwindow(String name, String email) {

        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtra("reciever_name", name);
        intent.putExtra("email", email);

        startActivity(intent);
        //finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            //leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    private void startSignIn() {
//
//        mUsername = null;
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivityForResult(intent, REQUEST_LOGIN);
//    }

    private void logOut() {

        prefsValues.clear();

//        prefsValues.getPrefs().edit().clear();
//        mUsername = null;
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
        finish();
        //startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        //startSignIn();
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("onConnect", "" + data.toString());

                        if (!isConnected) {
                            if (null != mUsername)
                                mSocket.emit("add user", mUsername);

                            Toast.makeText(getApplicationContext(),
                                    R.string.connect, Toast.LENGTH_LONG).show();

                            isConnected = true;
                        }

                    } catch (Exception e) {

                    }


                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    //removeTyping(username);
                    // addMessage(username, message);
                }
            });

        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {

                        Log.i("on User Joined", "" + data.toString());
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                        mUserID = data.getString("socket_id");
                    } catch (JSONException e) {
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_left, username));
                    //addParticipantsLog(numUsers);
                    //removeTyping(username);
                }
            });
        }
    };


    private Emitter.Listener getOfflineMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONArray jsonArray = new JSONArray(args);

                        String aa = jsonArray.getString(0).toString();
                        Log.e("getOfflineMessage:", aa);
                        JSONArray newArr = new JSONArray(aa);
//                        Log.e("email",newArr.getJSONObject(0).getString("email"));
                        for (int i = 0; i < newArr.length(); i++) {

                            JSONObject jsonObject = newArr.getJSONObject(i);

                            String sender_name = jsonObject.getString("sender_mail");

                            //addMessage(sender_name, jsonObject.getString("message"));

                        }

                    } catch (JSONException e) {
                        Log.e("user_registration", "JSONException" + e.toString());
                        //return;
                    } catch (Exception e) {
                        Log.e("user_registration", "Exception" + e.toString());
                        //return;
                    }


                }
            });
        }
    };

    private Emitter.Listener user_registration = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        mUserList.clear();

                        JSONArray jsonArray = new JSONArray(args);

                        String aa = jsonArray.getString(0).toString();
                        Log.e("data:", aa);
                        JSONArray newArr = new JSONArray(aa);
//                        Log.e("email",newArr.getJSONObject(0).getString("email"));
                        for (int i = 0; i < newArr.length(); i++) {

                            JSONObject jsonObject = newArr.getJSONObject(i);

                            User user = new User();
                            String name = jsonObject.getString("user_name");
                            user.setUserName(name);
                            if (name.equalsIgnoreCase(mUsername)) {

                                Log.i("user_matched", "I am " + mUsername);
                                continue;

                            }
                            user.setEmail(jsonObject.getString("email"));
                            user.setSocket_id(jsonObject.getString("socket_id"));
                            //user.setStatus(jsonObject.getString("status"));
                            if (jsonObject.getString("status") == "1") {
                                user.setStatus("online");
                            } else
                                user.setStatus("offline");

                            Log.i("email", jsonObject.getString("email"));
                            mUserList.add(user);
                        }
                        mUsrAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("user_registration", "JSONException" + e.toString());
                        //return;
                    } catch (Exception e) {
                        Log.e("user_registration", "Exception" + e.toString());
                        //return;
                    }

                }
            });
        }
    };


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
