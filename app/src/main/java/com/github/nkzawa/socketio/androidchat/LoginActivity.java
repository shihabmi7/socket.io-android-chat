package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;
    PrefsValues prefsValues;ChatApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {

            prefsValues = new PrefsValues(getApplicationContext(),"chat_me",0);


            app = (ChatApplication) getApplication();
            //mSocket = app.getSocket();

            // Set up the login form.
            mUsernameView = (EditText) findViewById(R.id.username_input);
            mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button signInButton = (Button) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            //mSocket.on("login", onLogin);

            String name =prefsValues.getUserName();

            if (!name.isEmpty()) {

                oldUserLogin(prefsValues.getUserName());

                Toast.makeText(getApplicationContext(),"Successfully Logged in",Toast.LENGTH_LONG);
            }

        }catch (Exception e){

            Toast.makeText(getApplicationContext(),"Error"+e.toString(),Toast.LENGTH_LONG);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        mSocket = app.getSocket();
        mSocket.on("login", onLogin);

        Log.e("onResume","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.off("login", onLogin);
        Log.e("onPause","onPause");
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
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // set user name to preference

        prefsValues.setUserName(mUsername);

        // perform the user login attempt.
        mSocket.emit("add user", username+"@gmail.com");
    }

    private void oldUserLogin(String username) {

        mUsername = username;

        // perform the user login attempt.
        mSocket.emit("add user", username+"@gmail.com");
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

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
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

}



