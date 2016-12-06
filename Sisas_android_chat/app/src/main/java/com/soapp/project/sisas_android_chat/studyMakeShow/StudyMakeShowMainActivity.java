package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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

    private String mUsername;
    private Socket mSocket;

    int room_id;

    Button btn_study_create;
    Button btn_study_join;

    ImageButton btn_member_info;
    TextView app_name;

    public ExpandableListView expandable_list_view;
    public StudyListMyExpandableAdapter my_adapter;
    public ArrayList<StudyListMyItem> my_study_list_parent_item = new ArrayList<StudyListMyItem>();
    public ArrayList<StudyListMyItemChild> my_study_list_child_item = new ArrayList<StudyListMyItemChild>();
    public HashMap<StudyListMyItem, StudyListMyItemChild> my_list_child_map = new HashMap<StudyListMyItem, StudyListMyItemChild>();

    ArrayList<JSONObject> study_list = new ArrayList<JSONObject>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_make_show_main);
        setCustomActionBar();

        btn_study_create = (Button)findViewById(R.id.btn_study_create);
        btn_study_join = (Button)findViewById(R.id.btn_study_join);

        btn_study_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent study_create_intent = new Intent(StudyMakeShowMainActivity.this, StudyMakeCreateActivity.class);
                startActivity(study_create_intent);
            }
        });
        btn_study_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent study_list_intent = new Intent(StudyMakeShowMainActivity.this, StudyShowActivity.class);
                startActivity(study_list_intent);
            }
        });

        try {
            getMyStudyFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getMyStudyFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };*/

    //email
    private void getMyStudyFromServer() throws Exception{
        String email = Member.getInstance().getEmail();
        String URL = "http://52.78.157.250:3000/get_myroomlist?email=" + URLEncoder.encode(email, "UTF-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    study_list.add(response.optJSONObject(i));
                }
                getMyStudy();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    public void getMyStudy(){
        for(int i=0 ;i<study_list.size(); i++) {
            int room_id = study_list.get(i).optInt("room_id");
            String king_name = study_list.get(i).optString("king_name");

            int head_icon = 0;
            if (king_name.equals(Member.getInstance().getName())) {
                head_icon = 1;
            } else {
                head_icon = 0;
            }

            String category = study_list.get(i).optString("category");
            String room_name = study_list.get(i).optString("room_name");
            int capacity = study_list.get(i).optInt("capacity");
            String start_date = study_list.get(i).optString("start_date");
            String end_date = study_list.get(i).optString("end_date");

            //dday 계산
            TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");
            Calendar today_calendar = Calendar.getInstance(time_zone);
            today_calendar.set(today_calendar.get(Calendar.YEAR), today_calendar.get(Calendar.MONTH) + 1, today_calendar.get(Calendar.DAY_OF_MONTH));

            //오늘부터 시작날짜까지의 디데이
            Calendar start_dday_calendar = Calendar.getInstance(time_zone);
            String[] start_dday_split = start_date.split("-");
            int start_dday_year = Integer.parseInt(start_dday_split[0]);
            int start_dday_month = Integer.parseInt(start_dday_split[1]);
            int start_dday_day = Integer.parseInt(start_dday_split[2]);
            start_dday_calendar.set(start_dday_year, start_dday_month, start_dday_day);

            long today = today_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
            long start_dday = start_dday_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
            int start_subs_day = (int)(today - start_dday);

            //오늘부터 끝날짜까지의 디데이
            Calendar end_dday_calendar = Calendar.getInstance(time_zone);
            String[] end_dday_split = end_date.split("-");
            int end_dday_year = Integer.parseInt(end_dday_split[0]);
            int end_dday_month = Integer.parseInt(end_dday_split[1]);
            int end_dday_day = Integer.parseInt(end_dday_split[2]);
            end_dday_calendar.set(end_dday_year, end_dday_month, end_dday_day);

            long end_dday = end_dday_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);
            int end_subs_day = (int)(today - end_dday);

            String dday_result = "";

            if (start_subs_day < 0) {
                dday_result = String.format("D%d", start_subs_day);
            } else if (start_subs_day >= 0 && end_subs_day <= 0){
                dday_result = "~ing";
            } else {
                int absR = Math.abs(end_subs_day);
                dday_result = String.format("D+%d", absR);
            }

            String date = start_date + " ~ " + end_date;
            String comment = study_list.get(i).optString("comment");
            String keyword = "아직";
            addMyStudy(room_id, head_icon, category, room_name, capacity, dday_result);
            addMyStudyChild(room_name, date, comment, keyword);
            my_list_child_map.put(my_study_list_parent_item.get(i), my_study_list_child_item.get(i));
        }

        // 앞서 정의해 놓은 ExpandableListView와 그 CustomAdapter를 선언 및 연결한 후
        // ExpandableListView에 대한 OnClickListener 등을 선언
        expandable_list_view = (ExpandableListView) findViewById(R.id.my_study_list);
        my_adapter = new StudyListMyExpandableAdapter(getApplicationContext(), my_study_list_parent_item, my_list_child_map);
        expandable_list_view.setAdapter(my_adapter);

        expandable_list_view.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = my_adapter.getGroupCount();
                // 한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        expandable_list_view.collapseGroup(i);
                }
            }
        });
    }

    public void addMyStudy(int room_id, int icon, String category, String room_name, int capacity, String dday){
        StudyListMyItem my_study = new StudyListMyItem(room_id, icon, category, room_name, capacity, dday);
        my_study_list_parent_item.add(my_study);
    }

    public void addMyStudyChild(String room_name, String date, String comment, String keyword){
        StudyListMyItemChild my_study_child = new StudyListMyItemChild(room_name, date, comment, keyword);
        my_study_list_child_item.add(my_study_child);
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
                Intent member_info_intent = new Intent(getApplicationContext(), MemberInfoActivity.class);
                startActivity(member_info_intent);
            }
        });

        app_name = (TextView)findViewById(R.id.btn_app_name);
        app_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent study_in_room_intent = new Intent(getApplicationContext(), StudyMakeShowMainActivity.class);
                startActivity(study_in_room_intent);
            }
        });
    }
}
