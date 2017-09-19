package com.github.nkzawa.socketio.androidchat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.model.Message;
import com.github.nkzawa.socketio.androidchat.model.User;
import com.github.nkzawa.socketio.androidchat.utils.ChatApplication;
import com.github.nkzawa.socketio.androidchat.utils.PrefsValues;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by ASUS on 6/29/2016.
 */
public class BaseActivity extends AppCompatActivity {

    public String mUsername;
    public Socket mSocket;
    public RecyclerView mMessagesRecyclerView;
    public RecyclerView.Adapter mUserAdapter, mMessageAdapter;
    public Boolean isConnected = true;
    public ArrayList<User> mUserList = new ArrayList<User>();
    public List<Message> mMessages = new ArrayList<Message>();
    public PrefsValues prefsValues;
    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        //JSONObject data = (JSONObject) args[0];
                        Log.e("onConnect->ChatList", "" + args.toString());

                        if (!isConnected) {
                            if (null != mUsername)
                                mSocket.emit("add user", mUsername);

                            Toast.makeText(getApplicationContext(),
                                    R.string.connect, Toast.LENGTH_LONG).show();

                            isConnected = true;
                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                    }


                }
            });
        }
    };
    public Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) args[0];
                    Log.e("onDisconnect->ChatList", "" + args.toString());
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //JSONObject data = (JSONObject) args[0];
                    Log.e("onContError->ChatList", "" + args.toString());
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    public Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {

                        Log.e("onUserJoined->ChatList", "" + data.toString());
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
    public Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {

                        Log.e("on User Left", "" + data.toString());
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
    ProgressDialog progressDialog;
    public Emitter.Listener user_registration = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        /*JSONArray jsonArray = new JSONArray(args);
                        String original_string = jsonArray.getString(0).toString();
                        //String aa = jsonArray.getString(0).toString();
                         Log.e("Original Json", original_string);*/

//                        JSONObject jObj = args.;
                        mUserList.clear();
                        Gson gson = new Gson();
                        String json = gson.toJson(args);
                        JSONArray test = new JSONArray(json);
                        Log.e("GSON->UserList", json);


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

                                //Log.i("user_matched", "I am " + mUsername);
                                continue;
                            }

                            User user = new User();
                            user.setUserName(name);
                            user.setEmail(jo.getString("email"));
                            user.setSocket_id(jo.getString("socket_id"));
//                            user.setLastSeen(jo.getString("last_seen"));
                            //Log.i("Status", "" + jo.getString("status"));
                            //user.setStatus(Boolean.parseBoolean(jo.getString("status")));

                            if (jo.getString("status").equalsIgnoreCase("1")) {
                                user.setStatus(true);
                            } else {
                                user.setStatus(false);
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
    private String TAG = BaseActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);
        mUsername = prefsValues.getUserName();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.setTitle(getString(R.string.waiting));
        progressDialog.setCancelable(true);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        Log.e(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void exitApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
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
    }

    public void logOut() {

        prefsValues.clear();
        mSocket.disconnect();
        //mSocket.connect();
        this.finish();
        startSignIn();

    }

    public void startSignIn() {

        mUsername = null;
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    public void UserRegistration() {

        progressDialog.show();
        mSocket.emit("user_login_and_registration", mUsername, mUsername + "@gmail.com");
        // Log.e("user_reg=>ChatList", "Called : " + mUsername);
    }

    void goToPrivateChatwindow(String name, String email) {

        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtra("reciever_name", name);
        intent.putExtra("email", email);

        startActivity(intent);
        //finish();
    }


}
