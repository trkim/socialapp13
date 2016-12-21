package com.soapp.project.sisas_android_chat.studyInRoom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by eelhea on 2016-12-12.
 */

public class ScrapWithKeywordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    TextView tv_scrap_keyword;
    String email = Member.getInstance().getEmail();
    int room_id;
    String date;
    String keyword;
    public ListView scarp_listview;
    public ScrapWithKeywordListAdapter scrap_keyword_adapter;
    ArrayList<ScrapWithKeywordItem> scrap_item_list = new ArrayList<ScrapWithKeywordItem>();

    ArrayList<String> array_list = new ArrayList<String>();
    ArrayList<JSONObject> scrap_obj_array_list = new ArrayList<JSONObject>();

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
                intent.putExtra("room_id", room_id);
                intent.putExtra("date", date);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        scarp_listview = (ListView)findViewById(R.id.scrap_list);
        scrap_keyword_adapter = new ScrapWithKeywordListAdapter();

        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");
        keyword = intent.getExtras().getString("keyword");
        date = intent.getExtras().getString("date");

        tv_scrap_keyword = (TextView)findViewById(R.id.tv_scrap_keyword);
        tv_scrap_keyword.setText(keyword);

        try {
            getScrapWithKeywordListFromServer(keyword);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getScrapWithKeywordListFromServer(final String keyword) throws Exception {
        final ProgressDialog loading = ProgressDialog.show(this,"기사를 가져오는 중입니다...","Please wait...",false,false);
        final String URL = "http://52.78.157.250:3000/scrap_with_keyword?keyword="+ URLEncoder.encode(keyword, "utf-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if(response.length()==0){
                        Toast.makeText(ScrapWithKeywordActivity.this, "스크랩 할 기사가 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        loading.dismiss();
                        for(int i=0; i<response.length(); i++) {
                            array_list.add(response.getString(i));
                            JSONObject obj = new JSONObject(array_list.get(i));
                            scrap_obj_array_list.add(obj);
                        }

                        getArticlesDetail(keyword);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy( 60000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volley.getInstance().addToRequestQueue(req);
    }

    private void getArticlesDetail(final String keyword){
        for(int i=0; i<scrap_obj_array_list.size(); i++){
            String title = scrap_obj_array_list.get(i).optString("title");
            String url = scrap_obj_array_list.get(i).optString("url");
            String content = scrap_obj_array_list.get(i).optString("content");

            scrap_keyword_adapter.addArticleInfo(title, url, content, "", keyword, date, email, room_id);
        }
        scarp_listview.setAdapter(scrap_keyword_adapter);
    }

}
