package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mMessages;
    private int[] mUsernameColors;

    public UserAdapter(Context context, List<User> messages) {
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        int layout = R.layout.item_user;


        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        User message = mMessages.get(position);
        //viewHolder.setMessage(message.getStatus());
        viewHolder.setUsername(message.getUserName());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return mMessages.get(position).getType();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mStatusView;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.userName);
            mStatusView = (TextView) itemView.findViewById(R.id.status);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mStatusView) return;
            mStatusView.setText(message);
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
