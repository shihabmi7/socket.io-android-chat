package com.github.nkzawa.socketio.androidchat.activity;

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

import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.model.Message;
import com.github.nkzawa.socketio.androidchat.model.User;
import com.github.nkzawa.socketio.androidchat.utils.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.socket.emitter.Emitter;


public class ChatWindowActivity extends BaseActivity {

    private static final int TYPING_TIMER_LENGTH = 600;
    public Emitter.Listener onNewMessage = new Emitter.Listener() {
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
    private EditText mInputMessageView;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private User mReceiveUser;
    private Toolbar toolbar;
    private Emitter.Listener get_chat_history = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//   {"message_id":353,"sender_mail":"sam@gmail.com",
// "receiver_mail":"shihab@gmail.com","message":"fgdgdgd","message_status":1,
// "arrival_time":"2016-07-12T11:09:55.000Z"}
                    try {
                        JSONArray jsonArray = new JSONArray(args);

                        String aa = jsonArray.getString(0).toString();
                        Log.e("chat_history:", aa);
                        JSONArray newArr = new JSONArray(aa);
//                        Log.e("email",newArr.getJSONObject(0).getString("email"));
                        for (int i = 0; i < newArr.length(); i++) {

                            JSONObject jsonObject = newArr.getJSONObject(i);
                            String sender_name = jsonObject.getString("sender_mail");
                            String message = jsonObject.getString("message");
                            String arrival_time = jsonObject.getString("arrival_time");

                            if (sender_name.equalsIgnoreCase(mUsername + "@gmail.com")) {

                                addUserMessage(getDateToday(), message);

                            } else {

                                addFriendsMessage(arrival_time, message);

                            }

                        }
                        Toast.makeText(getApplicationContext(), newArr.length() + " message in history"
                                , Toast.LENGTH_SHORT).show();

                        Log.e("chat_history", newArr.length() + " message in history");

                    } catch (JSONException e) {
                        Log.e("get_chat_history", "JSONException" + e.toString());
                        //return;
                    } catch (Exception e) {
                        Log.e("get_chat_history", "Exception" + e.toString());
                        //return;
                    }

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
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
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

                        username = data.getString("id");
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

        connetSocketAndListener();
        initiateUI();
        getChatHistory();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getChatHistory() {

        mSocket.emit("get_chat_history", mUsername + "@gmail.com", mReceiveUser.getEmail());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.off("say to someone", onSayToSomeone);
        mSocket.off("new message", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("get_chat_history", get_chat_history);

    }

    String getDateToday() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("hh:mm:ss SS");
        return date.format(c.getTime());
    }

    public void connetSocketAndListener() {

        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("new message", onNewMessage);
        mSocket.on("say to someone", onSayToSomeone);
        mSocket.on("get_chat_history", get_chat_history);

    }

    public void initiateUI() {

        mReceiveUser = new User();

        mReceiveUser.setEmail(getIntent().getStringExtra("email"));
        mReceiveUser.setUserName(getIntent().getStringExtra("reciever_name"));

        Log.e("Private Chat with ", "" + getIntent().getStringExtra("email") + " : " + getIntent().getStringExtra("reciever_name"));
        //toolbar.setTitle(mReceiveUser.getUserName());
        getSupportActionBar().setTitle(mReceiveUser.getUserName());


        mMessagesRecyclerView = (RecyclerView) findViewById(R.id.messageRecycleView);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageAdapter = new MessageAdapter(this, mMessages);
        mMessagesRecyclerView.setAdapter(mMessageAdapter);


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

            mSocket.emit("say to someone", mUsername + "@gmail.com", mReceiveUser.getSocket_id(), mReceiveUser.getEmail(), message);
            Log.e("send msg", "From User Id: " + mUsername + "@gmail.com" + " To:  " + mReceiveUser.getEmail() + "  " + message);

        } else {

            Toast.makeText(this, "please select a user", Toast.LENGTH_SHORT).show();
            mInputMessageView.setText("");
            //addUserMessage(mUsername, message);
        }

    }

    private void addUserMessage(String messageTime, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE_MINE)
                .messageTime(messageTime).message(message).build());
        mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addFriendsMessage(String messageTime, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE_FRIENDS)
                .messageTime(messageTime).message(message).build());
        mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    public void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mMessageAdapter.notifyItemRemoved(i);
            }
        }
    }

    public void scrollToBottom() {
        mMessagesRecyclerView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
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
