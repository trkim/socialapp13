package com.soapp.project.sisas_android_chat.studyInRoom;

/**
 * Created by samsung on 2016-12-19.
 */
public class MainChatMsgsArticles {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mUsername;
    private String mTitle;
    private String mUrl;
    private String mOpinion;

    private MainChatMsgsArticles() {}

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



    public static class Builder {
        private final int mType;
        private String mTitle;
        private String mUsername;
        private String mUrl;
        private String mOpinion;

        public Builder(int type) {
            mType = type;
        }


        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder url(String url) {
            mUrl = url;
            return this;
        }

        public Builder opinion(String opinion) {
            mOpinion = opinion;
            return this;
        }

        public MainChatMsgsArticles build() {
            MainChatMsgsArticles article = new MainChatMsgsArticles();
            article.mType = mType;
            article.mUsername = mUsername;
            article.mTitle = mTitle;
            article.mUrl = mUrl;
            article.mOpinion = mOpinion;
            return article;
        }
    }
}
