package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A chat fragment containing messages view and input form.
 */
public class MainFragment extends Fragment {

    private static final int REQUEST_LOGIN = 0;
    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter, mUsrAdapter;
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

    public MainFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter(activity, mMessages);
        // shiahb
        mUsrAdapter = new UserAdapter(activity, mUserList);
    }

    @Override
    public void onResume() {
        super.onResume();
        // UserRegistration();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        // shihab added
        mSocket.on("say to someone", onSayToSomeone);
        mSocket.on("user_registration", user_registration);
        mSocket.on("get_Offline_Message", getOfflineMessage);

        mSocket.connect();

        // called

        prefsValues = new PrefsValues(getActivity().getApplicationContext(), "chat_me", 0);
        startSignIn();
    }

    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");
        addMessage(mUsername, message);

        // perform the sending message attempt.
        String man = "";
        mSocket.emit("new message", message);
    }

    private void sendToSpecificPeople() {
        if (null == mUsername)
            return;
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
            addMessage("me: " + mUsername, message);

            mSocket.emit("say to someone", mUsername, mReceiveUser.getSocket_id(), mReceiveUser.getEmail(), message);
            Log.e("send msg", "User Id: " + mReceiveUser.getSocket_id() + "  " + mReceiveUser.getEmail() + "  " + message);

        } else {

            Toast.makeText(getActivity(), "please select a user", Toast.LENGTH_SHORT).show();
            mInputMessageView.setText("");
            //addMessage(mUsername, message);
        }

    }

    private void UserRegistration() {

        mSocket.emit("user_registration", mUsername, mUsername + "@gmail.com");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("say to someone", onSayToSomeone);
        // mSocket.off("say to someone", onSayToSomeone);
        mSocket.off("user_registration", user_registration);
        mSocket.off("get_Offline_Message", getOfflineMessage);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mLogOutButton = (Button) view.findViewById(R.id.button);
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOut();
            }
        });
        // shihab

        mUserListView = (RecyclerView) view.findViewById(R.id.userList);
        mUserListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserListView.setAdapter(mUsrAdapter);

        mUserListView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mUserListView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                mReceiveUser = mUserList.get(position);
                //Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();

//                Snackbar.make(view, "" + mReceiveUser.getUserName() + " " + mReceiveUser.getSocket_id(), Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();

                Toast.makeText(getActivity(), mReceiveUser.getUserName() + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
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

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //attemptSend();
                sendToSpecificPeople();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        addLog(getResources().getString(R.string.message_welcome));
        addParticipantsLog(numUsers);
        UserRegistration();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    private void startSignIn() {


        mUsername = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void logOut() {

        prefsValues.clear();

//        prefsValues.getPrefs().edit().clear();
//        mUsername = null;
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivity(intent);
        getActivity().finish();
        //startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        startSignIn();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        JSONObject data = (JSONObject) args[0];
                        Log.e("onConnect", "" + data.toString());

                        if (!isConnected) {
                            if (null != mUsername)
                                mSocket.emit("add user", mUsername);

                            Toast.makeText(getActivity().getApplicationContext(),
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    removeTyping(username);
                    addMessage(username, message);
                }
            });

        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {

                        Log.e("on User Joined", "" + data.toString());
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                        mUserID = data.getString("socket_id");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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


    private Emitter.Listener onSayToSomeone = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
                    addMessage(username, message);
                }
            });
        }
    };


    private Emitter.Listener getOfflineMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

                            addMessage(sender_name, jsonObject.getString("message"));

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
            getActivity().runOnUiThread(new Runnable() {
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

                                Log.e("user_matched", "I am " + mUsername);
                                continue;

                            }
                            user.setEmail(jsonObject.getString("email"));
                            //user.setStatus("online");
                            user.setSocket_id(jsonObject.getString("socket_id"));
                            //user.setStatus(jsonObject.getString("status"));
                            Log.e("email", jsonObject.getString("email"));
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


    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}

