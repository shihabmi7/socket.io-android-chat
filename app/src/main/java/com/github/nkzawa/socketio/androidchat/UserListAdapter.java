package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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
        //viewHolder.setMessage(aUser.getStatus());
        viewHolder.setUsername(aUser.getUserName());
        viewHolder.setStatus(aUser.getStatus());
        // long date= Long.parseLong(message.getLastSeen().get;
        try {

            //Log.i("aUser_LastSeen", "" + aUser.getLastSeen());
            //String lastSeen = getLastSeen(aUser.getLastSeen());
            String lastSeen = getTimeAgo(aUser.getLastSeen());
            //Log.i("lastSeen", "" + lastSeen);
            viewHolder.setmLastSeen(lastSeen);

        } catch (Exception e) {

            e.printStackTrace();
        }

        //viewHolder.setmLastSeen(aUser.getLastSeen());
    }

    String getLastSeen(String formatted_date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss SS");
        try {

            Log.e("Sample Date", "" + formatted_date);
            formatted_date = formatted_date.replace('T', ' ');
            formatted_date = formatted_date.replace(".000Z", "");
            Log.e("Sample Date", "" + formatted_date);

            Date oldDate = dateFormat.parse(formatted_date);
            System.out.println(oldDate);

            Date currentDate = new Date();
            long diff = currentDate.getTime() - oldDate.getTime();

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (oldDate.before(currentDate)) {

//                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
//                        + " hours: " + hours + " days: " + days);
                //Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                //    + " hours: " + hours + " days: " + days);
            }
            return
                    "last seen: " + days + " day " + hours + " h ago";
        } catch (ParseException e) {

            e.printStackTrace();
            return "error";
        }
        // return "";

    }

    public String getTimeAgo(String formatted_date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm:ss");

        String LAST_SEEN = "last seen: ";
        //Log.e("JSON Date", "" + formatted_date);
        formatted_date = formatted_date.replace('T', ' ');
        formatted_date = formatted_date.replace(".000Z", "");
        //Log.e("Formatted Date", "" + formatted_date);

        try {

            Date oldDate = dateFormat.parse(formatted_date);
            long time = oldDate.getTime();
//            if (time < 1000000000000L) {
//                //if timestamp given in seconds, convert to millis time *= 1000; }
//                long now = getCurrentTime();
//                if (time > now || time <= 0) {
//                    return null;
//                }
// TODO: localize final
            long diff = getCurrentTime() - time;
            if (diff < MINUTE_MILLIS) {
                return LAST_SEEN + "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return LAST_SEEN + "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return LAST_SEEN + (diff / MINUTE_MILLIS) + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return LAST_SEEN + "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return LAST_SEEN + (diff / HOUR_MILLIS) + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return LAST_SEEN + "yesterday";
            } else {
                return LAST_SEEN + (diff / DAY_MILLIS) + " days ago";
            }
            //  }

        } catch (Exception e) {
            Log.e("Exception", "" + e.toString());
            e.printStackTrace();
        }


        return "error";
    }

    long getCurrentTime() {
        Date date = new Date();
        return date.getTime();
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mStatusView;
        private TextView mLastSeen;
        private ImageView mStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.messanger_name);
            //mStatusView = (TextView) itemView.findViewById(R.id.msger_job_name);
            mLastSeen = (TextView) itemView.findViewById(R.id.chat_time);
            mStatus = (ImageView) itemView.findViewById(R.id.image_status);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            //mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setStatus(boolean status) {
            if (null == mStatus) return;

            //Log.e("status", "" + status);
            if (status) {
                mStatus.setImageResource(R.drawable.circle_online);
            } else {
                mStatus.setImageResource(R.drawable.circle_offline);
            }

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
