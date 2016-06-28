package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;
    PrefsValues prefsValues;
    ChatApplication app;
    private Boolean isConnected = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.setTitle(getString(R.string.waiting));
            progressDialog.setCancelable(true);

            prefsValues = new PrefsValues(getApplicationContext(), "chat_me", 0);


            app = (ChatApplication) getApplication();
            //mSocket = app.getSocket();

            // Set up the login form.
            mUsernameView = (EditText) findViewById(R.id.username_input);
            mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin(mUsernameView.getText().toString().trim());
                        return true;
                    }
                    return false;
                }
            });

            Button signInButton = (Button) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(mUsernameView.getText().toString().trim());
                }
            });

            //mSocket.on("login", onLogin);


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            DebugLog.log("LOg In : " + e.toString());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("login", onLogin);
        mSocket.connect();
        Log.e("onResume", "onResume");

        String name = getSharedPreferences("chat_me", 0).getString("user_name", "");

        if (!name.isEmpty()) {

            //oldUserLogin(prefsValues.getUserName());
            attemptLogin(prefsValues.getUserName());

            Toast.makeText(getApplicationContext(), "Successfully Logged in", Toast.LENGTH_LONG).show();

        } else
            Toast.makeText(getApplicationContext(), "Give a name", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.off("login", onLogin);
        Log.e("onPause", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String mUsername) {

        progressDialog.show();
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        //String username = mUsernameView.getText().toString().trim();
        String username = mUsername;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        ApplicationData.mUserName = username;

        // set user name to preference

        prefsValues.setUserName(mUsername);

        // perform the user login attempt.
        mSocket.emit("add user", username + "@gmail.com");
    }

    private void oldUserLogin(String username) {

        mUsername = username;

        // perform the user login attempt.
        mSocket.emit("add user", username + "@gmail.com");
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            goToHomePage(mUsername, numUsers);
            progressDialog.dismiss();

        }
    };

    void goToHomePage(String name, int numUsers) {

        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra("username", name);
        intent.putExtra("numUsers", numUsers);
        startActivity(intent);
        finish();
    }


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
                    progressDialog.dismiss();
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
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

}



