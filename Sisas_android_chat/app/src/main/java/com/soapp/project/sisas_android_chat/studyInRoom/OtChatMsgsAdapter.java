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
 * Created by eelhea on 2016-11-04.
 */
public class OtChatMsgsAdapter extends RecyclerView.Adapter<OtChatMsgsAdapter.ViewHolder> {

    private List<OtChatMsgs> mMessages;
    private int[] mUsernameColors;

    public OtChatMsgsAdapter(Context context, List<OtChatMsgs> messages) {
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        /*switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.item_message;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_action;
                break;
        }*/
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.study_in_room_ot_chat_msg, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        OtChatMsgs message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());
        viewHolder.setImage(message.getImage());
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
        public ViewHolder(View itemView) {
            super(itemView);
            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
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
