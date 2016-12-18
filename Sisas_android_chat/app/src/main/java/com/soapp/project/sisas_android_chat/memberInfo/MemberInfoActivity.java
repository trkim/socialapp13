package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.member.JoinActivity;
import com.soapp.project.sisas_android_chat.member.LoginActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeShowMainActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyShowActivity;

/**
 * Created by eelhea on 2016-10-14.
 */
public class MemberInfoActivity extends AppCompatActivity {

    SharedPreferences member_session;

    Toolbar toolbar;
    ImageButton icBackIcon;

    MyProfileFragment myProfileFragment;
    TimelineFragment timelineFragment;
    ScrapboxFragment scrapboxFragment;

    /*private TabLayout tab_layout;
    private ViewPager view_pager;

    ImageButton btn_member_info;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_info);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        icBackIcon = (ImageButton)findViewById(R.id.icBackIcon);
        icBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberInfoActivity.this, StudyMakeShowMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        member_session = getSharedPreferences("MemberSession", Context.MODE_PRIVATE);

        myProfileFragment = new MyProfileFragment();
        timelineFragment = new TimelineFragment();
        scrapboxFragment = new ScrapboxFragment();

        Intent intent = getIntent();
        String frag = intent.getExtras().getString("fragment");
        if(frag.equals("scrapbox")){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, scrapboxFragment).commit();
        } else{
            getSupportFragmentManager().beginTransaction().replace(R.id.container, myProfileFragment).commit();
        }

        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position==0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, myProfileFragment).commit();
                } else if(position==1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, timelineFragment).commit();
                } else if(position==2){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, scrapboxFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

}
