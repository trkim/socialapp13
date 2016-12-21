package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.memberInfo.MemberInfoActivity;
import com.soapp.project.sisas_android_chat.memberInfo.ScrapInRoomActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by eelhea on 2016-12-20.
 */

public class ShareArticleActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    TextView tv_share_article_keyword;
    TextView tv_share_article_date;
    ListView lv_share_article;

    int room_id;
    String keyword_global;
    String date_global;
    Button btn_get_scrap_article;

    ShareArticleListAdapter shareArticleListAdapter;
    ArrayList<JSONObject> article_list = new ArrayList<JSONObject>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_article_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");
        keyword_global = intent.getExtras().getString("keyword");
        date_global = intent.getExtras().getString("date");

        tv_share_article_keyword = (TextView)findViewById(R.id.tv_share_article_keyword);
        tv_share_article_date = (TextView)findViewById(R.id.tv_share_article_date);
        tv_share_article_keyword.setText(keyword_global);
        tv_share_article_date.setText(date_global);

        lv_share_article = (ListView)findViewById(R.id.lv_share_article);
        shareArticleListAdapter = new ShareArticleListAdapter(getApplicationContext(), room_id);

        icBackIcon = (ImageButton)findViewById(R.id.icBackIcon);
        icBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareArticleActivity.this, OtChatActivity.class);
                intent.putExtra("room_id", room_id);
                intent.putExtra("keyword", keyword_global);
                intent.putExtra("date", date_global);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        //스크랩 한 기사 가져오기 버튼
        btn_get_scrap_article  = (Button)findViewById(R.id.btn_get_scrap_article);
        btn_get_scrap_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareArticleActivity.this, ScrapInRoomActivity.class);
                intent.putExtra("room_id", room_id);
                intent.putExtra("keyword", keyword_global);
                intent.putExtra("date", date_global);
                intent.putExtra("from", "share");
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        //공유하고있는 기사가져오기
        try{
            getShareArticleFromServer(room_id);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getShareArticleFromServer(final int room_id){
        String URL = "http://52.78.157.250:3000/get_share_scraplist?room_id=" + room_id;

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    article_list.add(response.optJSONObject(i));
                }
                getArticleList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    private void getArticleList(){
        for(int i=0; i<article_list.size(); i++){
            int room_id = article_list.get(i).optInt("room_id");
            String article_title = article_list.get(i).optString("article_title");
            String url = article_list.get(i).optString("url");
            String content = article_list.get(i).optString("content");
            String opinion = article_list.get(i).optString("opinion");
            String keyword = article_list.get(i).optString("keyword");
            String date = article_list.get(i).optString("date");
            String email = article_list.get(i).optString("email");

            if(keyword.equals(keyword_global)){
                shareArticleListAdapter.addShareArticle(article_title, url, content, opinion, keyword, date, room_id);
            }
        }
        lv_share_article.setAdapter(shareArticleListAdapter);
    }
}
