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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.memberInfo.ScrapInRoomListItem;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
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
    ScrapWithKeywordListAdapter scrap_keyword_adapter;
    ArrayList<ScrapWithKeywordItem> scrap_item_list = new ArrayList<ScrapWithKeywordItem>();
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

        scarp_listview = (ListView)findViewById(R.id.scrap_list);
        scrap_keyword_adapter = new ScrapWithKeywordListAdapter();

        Intent intent = getIntent();
        keyword = intent.getExtras().getString("keyword");

        tv_scrap_keyword = (TextView)findViewById(R.id.tv_scrap_keyword);
        tv_scrap_keyword.setText(keyword);

        getScrapWithKeywordListFromServer(keyword);
    }

    private void getScrapWithKeywordListFromServer(final String keyword){
        final String URL = "http://52.78.157.250:3000/scrap_with_keyword?keyword="+keyword;

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    scrap_array_list.add(response.optJSONObject(i));
                }
                getArticles();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    private void getArticles(){
        for(int i=0; i<scrap_array_list.size(); i++){
            String title = scrap_array_list.get(i).optString("result_title");
            String content = scrap_array_list.get(i).optString("result_text");
            String url = scrap_array_list.get(i).optString("result_news_url");

            setArticleInfo(title, url, content);
        }
        scarp_listview.setAdapter(scrap_keyword_adapter);
    }

    private void setArticleInfo(String title, String url, String content){
        ScrapWithKeywordItem scrap_item = new ScrapWithKeywordItem();
        scrap_item.setTv_scrap_title(title);
        scrap_item.setScrap_url(url);
        scrap_item.setTv_scrap_content(content);

        scrap_item_list.add(scrap_item);
    }
}
