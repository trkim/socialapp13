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

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by eelhea on 2016-12-19.
 */

public class MainChatMsgsArticleAdapter extends RecyclerView.Adapter<MainChatMsgsArticleAdapter.ViewHolder> {
    private List<MainChatMsgsArticles> mArticles;
    private int[] mUsernameColors;

    public MainChatMsgsArticleAdapter(Context context, List<MainChatMsgsArticles> articles){
        mArticles = articles;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public MainChatMsgsArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.study_in_room_main_chat_msg_article, parent, false);
        return new MainChatMsgsArticleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainChatMsgsArticleAdapter.ViewHolder holder, int position) {
        MainChatMsgsArticles articles = mArticles.get(position);
        holder.setUsername(articles.getUsername());
        holder.setTitle(articles.getTitle());
        holder.setUrl(articles.getUrl());
        holder.setOpinion(articles.getOpinion());
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mArticles.get(position).getType();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView title;
        private TextView url;
        private TextView opinion;
        public ViewHolder(View itemView) {
            super(itemView);
            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            title = (TextView) itemView.findViewById(R.id.title);
            url = (TextView) itemView.findViewById(R.id.url);
            opinion = (TextView) itemView.findViewById(R.id.opinion);
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

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
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
