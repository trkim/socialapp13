package com.soapp.project.sisas_android_chat.memberInfo;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-12-14.
 */

public class ScrapInRoomActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    TextView tv_study_name;
    TextView tv_study_date;
    TextView tv_study_comment;

    int room_id;
    String got_keyword;
    String got_date;
    ListView lv_scrapbox_keyword;
    ScrapInRoomListAdapter scrap_in_room_list_adapter;
    ArrayList<JSONObject> keyword_list = new ArrayList<JSONObject>();
    ArrayList<JSONObject> keyword_scrap_list = new ArrayList<JSONObject>();

    ArrayList<ScrapInRoomListItem> scrap_in_room_list_item = new ArrayList<ScrapInRoomListItem>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_info_scrapbox_in_activity);
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
                Intent intent = new Intent(ScrapInRoomActivity.this, MemberInfoActivity.class);
                intent.putExtra("fragment", "scrapbox");
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        lv_scrapbox_keyword = (ListView)findViewById(R.id.lv_scrapbox_keyword);
        scrap_in_room_list_adapter = new ScrapInRoomListAdapter(getApplicationContext());

        tv_study_name = (TextView)findViewById(R.id.tv_study_name);
        tv_study_date = (TextView)findViewById(R.id.tv_study_date);
        tv_study_comment = (TextView)findViewById(R.id.tv_study_comment);

        Intent intent = getIntent();
        if(intent.getExtras().size()==1) {
            room_id = intent.getExtras().getInt("room_id");
        } else if(intent.getExtras().size()==3){
            room_id = intent.getExtras().getInt("room_id");
            got_keyword = intent.getExtras().getString("keyword");
            got_date = intent.getExtras().getString("date");
        }

        try {
            //스터디 정보 가져오기
            getStudyDetailFromServer(room_id, Member.getInstance().getEmail());
            //keyword_box에서 keyword들 가지고 오기
            //getKeywordFromServer(room_id, Member.getInstance().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStudyDetailFromServer(final int room_id, final String email) throws Exception{
        final String URL = "http://52.78.157.250:3000/get_room";

        Map<String, Object> room_member_detail_param = new HashMap<String, Object>();
        room_member_detail_param.put("room_id", room_id);
        room_member_detail_param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(room_member_detail_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String room_name = response.getString("room_name");
                            String start_date = response.getString("start_date");
                            String end_date = response.getString("end_date");
                            String comment = response.getString("comment");

                            tv_study_name.setText(room_name);
                            tv_study_date.setText(start_date + " ~ " + end_date);
                            tv_study_comment.setText(comment);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    //room_id 와 email 로 keyword_box DB에서 keyword, date, keyword_box_id 가져오기
/*    private void getKeywordFromServer(final int room_id, final String email) throws Exception {
        String URL = "http://52.78.157.250:3000/get_keyword?room_id="+room_id+"&email=" + URLEncoder.encode(email, "UTF-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    keyword_list.add(response.optJSONObject(i));
                }
                getKeyword();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    //가져온 해당 room의 keyword개수 만큼 반복문을 수행
    private void getKeyword(){
        for(int i=0; i<keyword_list.size(); i++){
            String keyword = keyword_list.get(i).optString("keyword");
            String date = keyword_list.get(i).optString("date");
            String keyword_box_id = keyword_list.get(i).optString("keyword_box_id");

            //스터디 메인 채팅방에서 기사 가져오기 클릭 했을 때, 해당 스터디 날짜의 해당 키워드의 기사만 보여주기 위함
            if(!got_keyword.equals("") && !got_date.equals("") && got_keyword.equals(keyword)){
                try {
                    getScrapboxDetailFromServer(got_keyword, keyword_box_id, got_date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }else {
                //scrapbox에서 클릭해서 들어갔을 때 보이는 화면. 모든 키워드의 기사들을 보여줌
                //keyword와 keyword_box_id로 scrap_box에서 keyword에 해당하는 스크랩한 기사 가져오기
                try {
                    getScrapboxDetailFromServer(keyword, keyword_box_id, date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getScrapboxDetailFromServer(final String keyword, final String keyword_box_id, final String date){
        String keyword_keyword_box_id = keyword+keyword_box_id;
        String URL = "http://52.78.157.250:3000/get_keyword_scrap?keyword_keyword_box_id="+keyword_keyword_box_id;

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    keyword_scrap_list.add(response.optJSONObject(i));
                }
                getKeywordScrap(keyword, date);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    private void getKeywordScrap(final String keyword, final String date){
        for(int i=0; i<keyword_scrap_list.size(); i++){
            String article_title = keyword_scrap_list.get(i).optString("article_title");
            String url = keyword_scrap_list.get(i).optString("url");
            String content = keyword_scrap_list.get(i).optString("content");
            String opinion = keyword_scrap_list.get(i).optString("opinion");

            addKeywordScrap(keyword, date, article_title, url, content, opinion);
        }
        lv_scrapbox_keyword.setAdapter(scrap_in_room_list_adapter);
    }

    private void addKeywordScrap(String keyword, String date, String article_title, String url, String content, String opinion){
        ScrapInRoomListItem scrapInRoomListItem = new ScrapInRoomListItem(keyword, date, article_title, url, content, opinion);
        scrap_in_room_list_item.add(scrapInRoomListItem);
    }*/
}
