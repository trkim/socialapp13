package com.soapp.project.sisas_android_chat;

import android.util.Log;

/**
 * Created by eelhea on 2016-11-10.
 */
public class Member {
    private String name = null;
    private String email = null;
    private String major = null;
    private String category = null;
    private int coupon = 0;

    private Member(){}

    private static class Singleton {
        private static final Member member = new Member();
    }

    public static Member getInstance() {
        Log.e("development", "create singleton instance : Member");
        return Singleton.member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

}
