package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eelhea on 2016-12-12.
 */

public class ScrapWithKeywordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    TextView tv_scrap_keyword;
    String keyword;
    ListView scarp_listview;
    ArrayList<JSONObject> scrap_array_list = new ArrayList<JSONObject>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_with_keyword);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        icBackIcon = (ImageButton)findViewById(R.id.icBackIcon);
        icBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrapWithKeywordActivity.this, OtChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        Intent intent = getIntent();
        keyword = intent.getExtras().getString("keyword");

        tv_scrap_keyword = (TextView)findViewById(R.id.tv_scrap_keyword);
        tv_scrap_keyword.setText(keyword);
    }
}
