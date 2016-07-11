package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private int[] mUsernameColors;

    public MessageAdapter(Context context, List<Message> messages) {
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;

        switch (viewType) {
            case Message.TYPE_MESSAGE_MINE:
//            layout = R.layout.item_message;
                layout = R.layout.item_mine_message;
                break;
            case Message.TYPE_MESSAGE_FRIENDS:
//            layout = R.layout.item_message;
                layout = R.layout.item_other_image;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_action;
                break;
        }

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setMessageTime(message.getmMessageTime());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessageTime;
        private TextView mMessageView;

        public ViewHolder(View itemView) {
            super(itemView);

//            mUsernameView = (TextView) itemView.findViewById(R.id.username);
//            mMessageView = (TextView) itemView.findViewById(R.id.message);

            mMessageTime = (TextView) itemView.findViewById(R.id.time_n_date);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
        }

        public void setMessageTime(String messageTime) {
            if (null == mMessageTime) return;
            mMessageTime.setText(messageTime);
            // mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
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
