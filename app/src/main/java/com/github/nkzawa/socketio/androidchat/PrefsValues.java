package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefsValues {

    private SharedPreferences mPrefs;

    private String members_no = "mem_no";
    private String members_died_no = "mem_died_no";
    private String house_id = "house_id";
    private String userName = "user_name";

    Context context = null;
    Activity activity;

    public String getUserName() {
        return mPrefs.getString(userName, "");
    }

    public void setUserName(String userName) {

        mPrefs.edit().putString(this.userName, userName).commit();

    }


//    public PrefsValues(Context context) {
//
//        this.context = context;
//        mPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
//
//    }

    public PrefsValues(Activity activity) {

        this.activity = activity;
        mPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    }


    public SharedPreferences getPrefs() {
        return mPrefs;
    }

    public int getMembersNo() {
        return mPrefs.getInt(members_no, 0);
    }

    public void setMembers_no(int mem_no) {
        mPrefs.edit().putInt(members_no, mem_no).commit();
    }


    public String getHouseUniqueId() {
        return mPrefs.getString(house_id, "");
    }

    public void setHouseUnique_id(String houseUnique_id) {

        mPrefs.edit().putString(house_id, houseUnique_id).commit();

    }

    public String getActivityName() {
        return mPrefs.getString("activityName", "");
    }

    public void setActivityName(String activityName) {
        mPrefs.edit().putString("activityName", activityName).commit();
    }


    public void clearPreference(String value) {

        SharedPreferences settings = context.getSharedPreferences(value, Context.MODE_PRIVATE);
        mPrefs.edit().clear().commit();
//        SharedPreferences.Editor.clear();
    }

    public void clearPreference() {


        mPrefs.edit().clear().commit();

    }


    public void clearValue(String key) {

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().clear().clear();
    }
}