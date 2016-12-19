package com.soapp.project.sisas_android_chat.studyInRoom;

import android.graphics.Bitmap;

/**
 * Created by eelhea on 2016-12-14.
 */

public class MainChatMsgs {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;
    private Bitmap mImage;

    private MainChatMsgs() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() { return mUsername; };

    public Bitmap getImage() {
        return mImage;
    };


    public static class Builder {
        private final int mType;
        private Bitmap mImage;
        private String mMessage;
        private String mUsername;

        public Builder(int type) {
            mType = type;
        }

        public Builder image(Bitmap image) {
            mImage = image;
            return this;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public MainChatMsgs build() {
            MainChatMsgs message = new MainChatMsgs();
            message.mType = mType;
            message.mImage = mImage;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }
}
