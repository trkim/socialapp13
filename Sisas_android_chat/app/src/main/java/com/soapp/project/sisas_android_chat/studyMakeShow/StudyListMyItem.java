package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.graphics.drawable.Drawable;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyListMyItem {
    private int room_id;
    private int study_icon;
    private String study_category;
    private String study_name;
    private int study_capacity;
    private String study_dday;

    public StudyListMyItem(int room_id, int study_icon, String study_category, String study_name, int study_capacity, String study_dday){
        this.room_id = room_id;
        this.study_icon = study_icon;
        this.study_category = study_category;
        this.study_name = study_name;
        this.study_capacity = study_capacity;
        this.study_dday = study_dday;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getStudy_icon() {
        return study_icon;
    }

    public void setStudy_icon(int study_icon) {
        this.study_icon = study_icon;
    }

    public String getStudy_category() {
        return study_category;
    }

    public void setStudy_category(String study_category) {
        this.study_category = study_category;
    }

    public String getStudy_name() {
        return study_name;
    }

    public void setStudy_name(String study_name) {
        this.study_name = study_name;
    }

    public int getStudy_capacity() {
        return study_capacity;
    }

    public void setStudy_capacity(int study_capacity) {
        this.study_capacity = study_capacity;
    }

    public String getStudy_dday() {
        return study_dday;
    }

    public void setStudy_dday(String study_dday) {
        this.study_dday = study_dday;
    }

}
