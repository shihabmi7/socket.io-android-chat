package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<User> mUser;
    private int[] mUsernameColors;

    public UserListAdapter(Context context, List<User> user) {
        mUser = user;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        int layout = R.layout.chat_item;

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        User aUser = mUser.get(position);
        viewHolder.setMessage(aUser.getStatus());
        viewHolder.setUsername(aUser.getUserName());

        // long date= Long.parseLong(message.getLastSeen().get;
        try {

            //Log.i("aUser_LastSeen", "" + aUser.getLastSeen());
            String lastSeen = getLastSeen(aUser.getLastSeen());
            Log.i("lastSeen", "" + lastSeen);
            viewHolder.setmLastSeen(lastSeen);

        } catch (Exception e) {

        }

        //viewHolder.setmLastSeen(aUser.getLastSeen());
    }

    String getLastSeen(String toyBornTime) {


        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

        try {

            Date oldDate = dateFormat.parse(toyBornTime);
            System.out.println(oldDate);

            Date currentDate = new Date();

            long diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (oldDate.before(currentDate)) {

                //Log.e("oldDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                        + " hours: " + hours + " days: " + days);

            }

            return "seconds: " + seconds + " minutes: " + minutes
                    + " hours: " + hours + " days: " + days;

        } catch (ParseException e) {

            e.printStackTrace();
            return "error";
        }
        // return "";

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mStatusView;
        private TextView mLastSeen;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.messanger_name);
            mStatusView = (TextView) itemView.findViewById(R.id.msger_job_name);
            mLastSeen = (TextView) itemView.findViewById(R.id.chat_time);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            //mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mStatusView) return;
            mStatusView.setText(message);
        }

        public void setmLastSeen(String message) {
            if (null == mLastSeen) return;
            mLastSeen.setText(message);
        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }
}
