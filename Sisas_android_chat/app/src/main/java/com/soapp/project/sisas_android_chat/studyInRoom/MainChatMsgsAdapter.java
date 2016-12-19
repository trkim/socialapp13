package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import java.util.List;

/**
 * Created by eelhea on 2016-12-14.
 */

public class MainChatMsgsAdapter extends RecyclerView.Adapter<MainChatMsgsAdapter.ViewHolder> {
    private List<MainChatMsgs> mMessages;
    private int[] mUsernameColors;

    public MainChatMsgsAdapter(Context context, List<MainChatMsgs> messages) {
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public MainChatMsgsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.study_in_room_main_chat_msg, parent, false);
        return new MainChatMsgsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainChatMsgsAdapter.ViewHolder viewHolder, int position) {
        MainChatMsgs message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());
        viewHolder.setImage(message.getImage());
        viewHolder.setTitle(message.getTitle());
        viewHolder.setUrl(message.getUrl());
        viewHolder.setOpinion(message.getOpinion());
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
        private ImageView mImageView;
        private TextView mMessageView;
        private TextView mUsernameView;
        private TextView title;
        private TextView url;
        private TextView opinion;
        public ViewHolder(View itemView) {
            super(itemView);
            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            title = (TextView) itemView.findViewById(R.id.title);
            url = (TextView) itemView.findViewById(R.id.url);
            opinion = (TextView) itemView.findViewById(R.id.opinion);
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            if(null == message) return;
            mMessageView.setText(message);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setImage(Bitmap bmp){
            if(null == mImageView) return;
            if(null == bmp) return;
            mImageView.setImageBitmap(bmp);
        }

        public void setTitle(String set_title) {
            if (null == title) return;
            if(null == set_title) return;
            title.setText(set_title);
        }
        public void setUrl(String set_url) {
            if (null == url) return;
            if(null == set_url) return;
            url.setText(set_url);
        }
        public void setOpinion(String set_opinion) {
            if (null == opinion) return;
            if(null == set_opinion) return;
            opinion.setText(set_opinion);
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
