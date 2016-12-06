package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.graphics.drawable.Drawable;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyShowApplyMemberListItem {
    private Drawable profile_pic;
    private String member_name;
    private String member_major;
    private String member_interest;
    private String member_score;

    public Drawable getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Drawable profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_major() {
        return member_major;
    }

    public void setMember_major(String member_major) {
        this.member_major = member_major;
    }

    public String getMember_score() {
        return member_score;
    }

    public void setMember_score(String member_score) {
        this.member_score = member_score;
    }

    public String getMember_interest() {
        return member_interest;
    }

    public void setMember_interest(String member_interest) {
        this.member_interest = member_interest;
    }
}
