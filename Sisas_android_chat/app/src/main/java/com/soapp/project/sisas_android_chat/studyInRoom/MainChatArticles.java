package com.soapp.project.sisas_android_chat.studyInRoom;

import android.graphics.Bitmap;

/**
 * Created by samsung on 2016-12-19.
 */
public class MainChatArticles {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mUsername;
    private String mTitle;
    private String mUrl;
    private String mOpinion;
    private Bitmap mImage;

    private MainChatArticles() {}

    public int getType() {
        return mType;
    };

    public String getTitle() {
        return mTitle;
    };

    public String getUrl() {
        return mUrl;
    };

    public String getOpinion() {
        return mOpinion;
    };

    public String getUsername() { return mUsername; };

    public Bitmap getImage() {
        return mImage;
    };


    public static class Builder {
        private final int mType;
        private Bitmap mImage;
        private String mTitle;
        private String mUsername;
        private String mUrl;
        private String mOpinion;

        public Builder(int type) {
            mType = type;
        }

        public MainChatArticles.Builder image(Bitmap image) {
            mImage = image;
            return this;
        }

        public MainChatArticles.Builder username(String username) {
            mUsername = username;
            return this;
        }

        public MainChatArticles.Builder title(String title) {
            mTitle = title;
            return this;
        }

        public MainChatArticles.Builder url(String url) {
            mUrl = url;
            return this;
        }

        public MainChatArticles.Builder opinion(String opinion) {
            mOpinion = opinion;
            return this;
        }

        public MainChatArticles build() {
            MainChatArticles article = new MainChatArticles();
            article.mType = mType;
            article.mImage = mImage;
            article.mUsername = mUsername;
            article.mTitle = mTitle;
            article.mUrl = mUrl;
            article.mOpinion = mOpinion;
            return article;
        }
    }
}
