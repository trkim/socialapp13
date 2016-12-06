package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.soapp.project.sisas_android_chat.R;

/**
 * Created by eelhea on 2016-10-14.
 */
public class MemberInfoActivity extends AppCompatActivity {

    SharedPreferences member_session;

    private TabLayout tab_layout;
    private ViewPager view_pager;

    ImageButton btn_member_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_info);
        setCustomActionBar();

        member_session = getSharedPreferences("MemberSession", Context.MODE_PRIVATE);

        tab_layout = (TabLayout)findViewById(R.id.member_info_layout);
        tab_layout.addTab(tab_layout.newTab().setText("프로필"));
        tab_layout.addTab(tab_layout.newTab().setText("타임라인"));
        tab_layout.addTab(tab_layout.newTab().setText("스크랩박스"));
        tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);

        view_pager = (ViewPager)findViewById(R.id.member_info_pager);
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tab_layout.getTabCount());
        view_pager.setAdapter(adapter);
        view_pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout));

        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void setCustomActionBar(){
        ActionBar action_bar = getSupportActionBar();

        action_bar.setDisplayShowCustomEnabled(true);
        action_bar.setDisplayShowTitleEnabled(false);
        action_bar.setDisplayHomeAsUpEnabled(false);

        View custom_view = LayoutInflater.from(this).inflate(R.layout.action_bar,null);
        action_bar.setCustomView(custom_view);

        btn_member_info = (ImageButton)findViewById(R.id.btn_member_info);

        btn_member_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
