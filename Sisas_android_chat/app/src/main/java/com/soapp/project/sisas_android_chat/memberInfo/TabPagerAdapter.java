package com.soapp.project.sisas_android_chat.memberInfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by eelhea on 2016-10-14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private int tab_count;

    public TabPagerAdapter(FragmentManager fm, int tab_count) {
        super(fm);
        this.tab_count = tab_count;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MyProfileFragment my_profile_fragment = new MyProfileFragment();
                return my_profile_fragment;
            case 1:
                TimelineFragment timeline_fragment = new TimelineFragment();
                return timeline_fragment;
            case 2:
                ScrapboxFragment scrapbox_fragment = new ScrapboxFragment();
                return scrapbox_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tab_count;
    }
}
