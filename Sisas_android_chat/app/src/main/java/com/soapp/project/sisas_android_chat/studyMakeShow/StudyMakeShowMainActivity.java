package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.memberInfo.MemberInfoActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-10-21.
 */
public class StudyMakeShowMainActivity extends AppCompatActivity{

    Toolbar toolbar;
    ImageButton icMyPage;
    FragmentGetMyStudy fragmentGetMyStudy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_make_show_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        icMyPage = (ImageButton)findViewById(R.id.icMyPage);
        icMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudyMakeShowMainActivity.this, MemberInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
                finish();
            }
        });

        fragmentGetMyStudy = new FragmentGetMyStudy();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentGetMyStudy).commit();

        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position==0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentGetMyStudy).commit();
                } else if(position==1){
                    Intent intent = new Intent(StudyMakeShowMainActivity.this, StudyShowActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_study_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.icAddStudy:
                Intent intent = new Intent(StudyMakeShowMainActivity.this, StudyMakeCreateActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentGetMyStudy).commit();
    }
}
