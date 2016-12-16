package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyShowApplyActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    public StudyShowApplyMemberListAdapter apply_member_list_adapter;
    public ListView list_view;
    ArrayList<JSONObject> member_list = new ArrayList<JSONObject>();

    int room_id;
    TextView tv_study_name;
    TextView tv_study_category;
    TextView tv_study_capacity;
    TextView tv_study_start_date;
    TextView tv_study_end_date;
    TextView tv_study_comment;

    Button btn_apply_go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_show_apply);

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
                Intent intent = new Intent(StudyShowApplyActivity.this, StudyShowActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");

        tv_study_name = (TextView)findViewById(R.id.tv_study_name);
        tv_study_category = (TextView)findViewById(R.id.tv_study_category);
        tv_study_capacity = (TextView)findViewById(R.id.tv_study_capacity);
        tv_study_start_date = (TextView)findViewById(R.id.tv_study_start_date);
        tv_study_end_date = (TextView)findViewById(R.id.tv_study_end_date);
        tv_study_comment = (TextView)findViewById(R.id.tv_study_comment);

        btn_apply_go = (Button)findViewById(R.id.btn_apply_go);

        list_view = (ListView)findViewById(R.id.apply_member_list_view);
        apply_member_list_adapter = new StudyShowApplyMemberListAdapter();

        //스터디 상세정보 + 참여하는 멤버정보 가져오기
        try {
            getStudyDetailFromServer(room_id, Member.getInstance().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            getMemberDetailFromServer(room_id);
        }catch(Exception e){
            e.printStackTrace();
        }

        btn_apply_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        joinRoomToServer(room_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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
                            String capacity = response.getString("capacity");
                            String category = response.getString("category");
                            String start_date = response.getString("start_date");
                            String end_date = response.getString("end_date");
                            String comment = response.getString("comment");

                            tv_study_name.setText(room_name);
                            tv_study_category.setText(category);
                            tv_study_capacity.setText(capacity);
                            tv_study_start_date.setText(start_date);
                            tv_study_end_date.setText(end_date);
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

    private void getMemberDetailFromServer(final int room_id) throws Exception{
        final String URL = "http://52.78.157.250:3000/get_study_member?room_id="+ URLEncoder.encode(String.valueOf(room_id), "UTF-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("response.length()", String.valueOf(response.length()));
                for(int i=0; i<response.length(); i++){
                    member_list.add(response.optJSONObject(i));
                }
                getStudyMembers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    public void getStudyMembers(){
        for(int i=0; i<member_list.size(); i++){
            String member_name = member_list.get(i).optString("name");
            String member_major = member_list.get(i).optString("major");
            String member_category = member_list.get(i).optString("category");
            String member_rating = member_list.get(i).optString("rating");

            if(member_name.equals(Member.getInstance().getName())){
                btn_apply_go.setVisibility(View.INVISIBLE);
            }
            Log.e("name", member_name);
            Log.e("major", member_major);
            Log.e("category", member_category);
            Log.e("rating", member_rating);
            apply_member_list_adapter.addApplyMember(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher), member_name, member_major,member_category,member_rating);
        }

        list_view.setAdapter(apply_member_list_adapter);
    }

    private void joinRoomToServer(final int room_id) throws Exception{
        final String URL = "http://52.78.157.250:3000/join_room";

        Map<String, Object> room_member_detail_param = new HashMap<String, Object>();
        room_member_detail_param.put("room_id", room_id);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(room_member_detail_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("fail")){
                                    Toast toast = Toast.makeText(getApplicationContext(), "인원초과로 참여할 수 없습니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();
                                }else if(response.getString("result").equals("success")) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "참여신청 되었습니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();

                                    Intent new_intent = new Intent(getApplicationContext(), StudyShowApplyActivity.class);
                                    startActivity(new_intent);
                                    finish();
                                }
                            }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StudyShowActivity.class);
        startActivity(intent);
        finish();
    }
}
