package com.soapp.project.sisas_android_chat.studyInRoom;

import android.graphics.Bitmap;

/**
 * Created by eelhea on 2016-12-14.
 */

public class MainChatMsgs {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;
    private String mTitle;
    private String mUrl;
    private String mOpinion;

    private MainChatMsgs() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() { return mUsername; };

    public String getTitle() {
        return mTitle;
    };

    public String getUrl() {
        return mUrl;
    };

    public String getOpinion() {
        return mOpinion;
    };


    public static class Builder {
        private final int mType;
        private String mMessage;
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

        public Builder message(String message) {
            mMessage = message;
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

        public MainChatMsgs build() {
            MainChatMsgs message = new MainChatMsgs();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mTitle = mTitle;
            message.mUrl = mUrl;
            message.mOpinion = mOpinion;
            return message;
        }
    }
}
