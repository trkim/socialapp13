package com.soapp.project.sisas_android_chat.studyInRoom;

import android.graphics.Bitmap;

/**
 * Created by eelhea on 2016-11-04.
 */
public class OtChatMsgs {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;

    private OtChatMsgs() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() { return mUsername; };


    public static class Builder {
        private final int mType;
        private String mMessage;
        private String mUsername;

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

        public OtChatMsgs build() {
            OtChatMsgs message = new OtChatMsgs();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }
}
