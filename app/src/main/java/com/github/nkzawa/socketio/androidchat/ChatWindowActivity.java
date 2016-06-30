package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;


public class ChatWindowActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter, mUsrAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    //private String mUsername;
    private String mUserEmail;
    //private Socket mSocket;
    private String mUserID;

    private Boolean isConnected = true;
    private RecyclerView mUserListView;
    ArrayList<User> mUserList = new ArrayList<User>();
    User mReceiveUser;
    //private PrefsValues prefsValues;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_window);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Job Town Chat");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //toolbar.setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);
        mUsername = prefsValues.getUserName();

        Log.e("UserName", " UserName" + prefsValues.getUserName());
        connetSocketAndListener();
        initiateUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //connetSocketAndListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSocket.off("say to someone", onSayToSomeone);
        mSocket.off("new message", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("get_Offline_Message", getOfflineMessage);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //mSocket.off(Socket.EVENT_CONNECT, onConnect);
        // mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        //  mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        // mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        // mSocket.disconnect();
    }


    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    String getDateToday() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("hh:mm:ss SS");
        return date.format(c.getTime());
    }


    private void addUserMessage(String messageTime, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE_MINE)
                .messageTime(messageTime).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addFriendsMessage(String messageTime, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE_FRIENDS)
                .messageTime(messageTime).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public void connetSocketAndListener() {

//        ChatApplication app = (ChatApplication) getApplication();
//        mSocket = app.getSocket();
//        mSocket.on(Socket.EVENT_CONNECT, onConnect);
//        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        // mSocket.on("user joined", onUserJoined);
        // mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("new message", onNewMessage);
        mSocket.on("say to someone", onSayToSomeone);
        //mSocket.on("user_registration", user_registration);
        mSocket.on("get_Offline_Message", getOfflineMessage);
        //mSocket.connect();

    }

    public void initiateUI() {

        mReceiveUser = new User();

        mReceiveUser.setEmail(getIntent().getStringExtra("email"));
        mReceiveUser.setUserName(getIntent().getStringExtra("reciever_name"));

        Log.e("Private Chat with ", "" + getIntent().getStringExtra("email") + " : " + getIntent().getStringExtra("reciever_name"));
        //toolbar.setTitle(mReceiveUser.getUserName());
        getSupportActionBar().setTitle(mReceiveUser.getUserName());


        mMessagesView = (RecyclerView) findViewById(R.id.messageRecycleView);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter(this, mMessages);
        mMessagesView.setAdapter(mAdapter);


        mInputMessageView = (EditText) findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    //attemptSend();

                    sendToSpecificPeople();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //attemptSend();
                sendToSpecificPeople();
            }
        });
    }

    private void sendToSpecificPeople() {

        if (!mSocket.connected())
            return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        // perform the sending message attempt.
        if (mReceiveUser != null) {

            mInputMessageView.setText("");
            addUserMessage(getDateToday(), message);

            mSocket.emit("say to someone", mReceiveUser.getUserName(), mReceiveUser.getSocket_id(), mReceiveUser.getEmail(), message);
            Log.e("send msg", "From User Id: " + mUsername + " To:  " + mReceiveUser.getEmail() + "  " + message);

        } else {

            Toast.makeText(this, "please select a user", Toast.LENGTH_SHORT).show();
            mInputMessageView.setText("");
            //addUserMessage(mUsername, message);
        }

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
                    // addUserMessage(username, message);
                }
            });

        }
    };


    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    addTyping(username);
                }
            });
        }
    };
    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    removeTyping(username);
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
                            addUserMessage(getDateToday(), jsonObject.getString("message"));

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

    private Emitter.Listener onSayToSomeone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;

                    Log.e("Say To Someone", "" + data.toString());
                    try {

                        username = data.getString("username");
                        message = data.getString("message");

                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);
                    addFriendsMessage(getDateToday(), message);
                }
            });
        }
    };


    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        //finish();

        Log.e("onBackPressed", "");
    }

    /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
}
