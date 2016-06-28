package com.github.nkzawa.socketio.androidchat;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

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
    private RecyclerView.Adapter mUserAdapter;
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
    ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.setTitle(getString(R.string.waiting));
        progressDialog.setCancelable(true);

        Log.e(" onCreate", "ChatListActivity Called : ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(" onStart ", "ChatListActivity Called : ");
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

       /* mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        // mSocket.off("say to someone", onSayToSomeone);
        mSocket.off("user_registration", user_registration);
        mSocket.off("get_Offline_Message", getOfflineMessage);*/
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

        progressDialog.show();
        mSocket.emit("user_registration", mUsername, mUsername + "@gmail.com");
        Log.e("user_registration", "Called : " + mUsername);
    }


    public void initiateUI() {

        // shihab


        prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);

        mUsername = prefsValues.getUserName();

        getSupportActionBar().setTitle(" I am " + mUsername);

        mUserListView = (RecyclerView) findViewById(R.id.userList);
        mUserListView.setLayoutManager(new LinearLayoutManager(this));
        mUserAdapter = new UserListAdapter(this, mUserList);
        mUserListView.setAdapter(mUserAdapter);

        mUserListView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                mMessagesView, new MainFragment.ClickListener() {
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

                        //JSONObject data = (JSONObject) args[0];
                        Log.e("onConnect", "" + args.toString());

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
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) args[0];
                    Log.i("on Disconnect", "" + args.toString());
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) args[0];
                    Log.i("on Connect Error", "" + args.toString());
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
                    //JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    Log.i("on New Message", "" + args.toString());
                    try {
                        //username = data.getString("username");
                        //message = data.getString("message");
                    } catch (Exception e) {
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

                        Log.i("on User Left", "" + data.toString());
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
                        /*JSONArray jsonArray = new JSONArray(args);

                        String aa = jsonArray.getString(0).toString();
                        Log.i("get Offline Message:", aa);*/

                        JSONArray newArr = new JSONArray(args.toString());
//                        Log.e("email",newArr.getJSONObject(0).getString("email"));
                        for (int i = 0; i < newArr.length(); i++) {

                            JSONObject jsonObject = newArr.getJSONObject(i);
                            String sender_name = jsonObject.getString("sender_mail");

                            //addMessage(sender_name, jsonObject.getString("message"));

                        }

                    } catch (JSONException e) {
                        Log.e("getOfflineMessage", "JSONException" + e.toString());
                        //return;
                    } catch (Exception e) {
                        Log.e("getOfflineMessage", "Exception" + e.toString());
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

//                        JSONObject jObj = args.;

                        mUserList.clear();

                        Gson gson = new Gson();
                        String json = gson.toJson(args);

                        JSONArray test = new JSONArray(json);
                        Log.i("user registration", json);

                        //JSONArray jsonArray = new JSONArray(args);
                        //String aa = jsonArray.getString(0).toString();
                        //String aa = jsonArray.getString(0).toString();
                        // Log.i("user registration", aa);
                        String aa = test.getString(0).toString();
                        JSONObject jsonObject = test.getJSONObject(0);
                        JSONArray valueArrays = jsonObject.getJSONArray("values");

//                        Log.e("email",newArr.getJSONObject(0).getString("email"));
                        for (int i = 0; i < valueArrays.length(); i++) {

                            JSONObject jo = valueArrays.getJSONObject(i);

                            String nvp = jo.getString("nameValuePairs");

                            jo = new JSONObject(nvp);


                            String name = jo.getString("user_name");

                            if (name.equalsIgnoreCase(mUsername)) {

                                Log.i("user_matched", "I am " + mUsername);
                                continue;
                            }

                            User user = new User();
                            user.setUserName(name);
                            user.setEmail(jo.getString("email"));
                            user.setSocket_id(jo.getString("socket_id"));
                            user.setLastSeen(jo.getString("last_seen"));

                            if (jo.getString("status").equalsIgnoreCase("1")) {
                                user.setStatus("online");
                            } else {
                                user.setStatus("offline");
                            }

                            mUserList.add(user);

                        }
                        mUserAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitApp();
    }

    void exitApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
