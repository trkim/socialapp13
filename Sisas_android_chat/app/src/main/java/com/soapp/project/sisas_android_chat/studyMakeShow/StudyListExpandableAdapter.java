package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
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
import com.soapp.project.sisas_android_chat.memberInfo.ScrapInRoomActivity;
import com.soapp.project.sisas_android_chat.studyInRoom.MainChatActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-11-03.
 */
public class StudyListExpandableAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    private Context context;
    private ArrayList<StudyListItem> study_list_parent;
    private StudyListItemChildHolder study_list_child_holder = new StudyListItemChildHolder();
    private HashMap<StudyListItem, StudyListItemChild> list_child_map;
    int temp=0;
    private ArrayList<JSONObject> keyword_list = new ArrayList<JSONObject>();
    String keyword_available = "";
    String date_available = "";

    ImageButton ib_study_go;

    public StudyListExpandableAdapter(Context context, ArrayList<StudyListItem> study_list_parent, HashMap<StudyListItem, StudyListItemChild> list_child_map, int temp){
        this.context = context;
        this.study_list_parent = study_list_parent;
        this.list_child_map = list_child_map;
        inflater = LayoutInflater.from(context);
        this.temp = temp;
    }

    @Override
    public StudyListItem getGroup(int groupPosition) {
        return study_list_parent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return study_list_parent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.study_list_item, parent, false);

        }

        ib_study_go = (ImageButton)convertView.findViewById(R.id.ib_study_go);
        if(temp==1) { //참여신청 화면
            ib_study_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StudyShowApplyActivity.class);
                    int room_id = study_list_parent.get(groupPosition).getRoom_id();
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("temp", 1);
                    context.startActivity(intent);
                }
            });
        }  else if(temp==3){
            ib_study_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ScrapInRoomActivity.class);
                    intent.putExtra("room_id", study_list_parent.get(groupPosition).getRoom_id());
                    context.startActivity(intent);
                }
            });
        } else if(temp==2){ // 관전하기 화면
            ib_study_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        checkCouponFromServer(groupPosition);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

        TextView tv_room_category = (TextView) convertView.findViewById(R.id.tv_room_category);
        TextView tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
        TextView tv_room_capacity = (TextView) convertView.findViewById(R.id.tv_room_capacity);
        TextView tv_room_dday = (TextView) convertView.findViewById(R.id.tv_room_dday);

        tv_room_category.setText(getGroup(groupPosition).getStudy_category());
        tv_room_name.setText(getGroup(groupPosition).getStudy_name());
        tv_room_name.setSingleLine(true); //한줄로 나오게 하기.
        tv_room_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        tv_room_name.setFocusable(true);
        tv_room_name.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기
        tv_room_capacity.setText(String.valueOf(getGroup(groupPosition).getStudy_capacity()));
        tv_room_dday.setText(getGroup(groupPosition).getStudy_dday());

        return convertView;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public StudyListItemChild getChild(int groupPosition, int childPosition) {
        return this.list_child_map.get(this.study_list_parent.get(groupPosition));
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        StudyListItemChild item_child = (StudyListItemChild)getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater child_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = child_inflater.inflate(R.layout.study_list_item_child, null);

            study_list_child_holder = new StudyListItemChildHolder();
            study_list_child_holder.study_name_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_name);
            study_list_child_holder.study_date_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_date);
            study_list_child_holder.study_comment_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_comment);

            convertView.setTag(study_list_child_holder);
        } else {
            study_list_child_holder = (StudyListItemChildHolder)convertView.getTag();
        }
        study_list_child_holder.study_name_holder.setText(item_child.getStudy_name());
        study_list_child_holder.study_date_holder.setText(item_child.getStudy_date());
        study_list_child_holder.study_comment_holder.setText(item_child.getStudy_comment());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void checkCouponFromServer(final int groupPosition){
        final String URL = "http://52.78.157.250:3000/check_coupon";

        Map<String, String> param = new HashMap<String, String>();
        param.put("email", Member.getInstance().getEmail());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast.makeText(context, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        } else if(response.getString("result").equals("success")){
                            Toast.makeText(context, "관전 입장합니다.", Toast.LENGTH_SHORT).show();

                            int room_id = study_list_parent.get(groupPosition).getRoom_id();

                            try {
                                Member.getInstance().setCoupon(Member.getInstance().getCoupon()-1);
                                checkForKeywordFromServer(room_id);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else if(response.getString("result").equals("couponless")){
                            Toast.makeText(context, "관전쿠폰이 부족합니다.", Toast.LENGTH_SHORT).show();
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

    private void checkForKeywordFromServer(final int room_id){
        final String URL = "http://52.78.157.250:3000/get_keyword?room_id="+room_id;

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0; i<response.length(); i++){
                        keyword_list.add(response.optJSONObject(i));
                    }
                    getKeyword(room_id);
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

    private void getKeyword(final int room_id){
        long min = 999999999;
            for (int i = 0; i < keyword_list.size(); i++) {
                String keyword_from_server = keyword_list.get(i).optString("keyword");
                String date_from_server = keyword_list.get(i).optString("date");

                //오늘 날짜
                TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");
                Calendar today_calendar = Calendar.getInstance(time_zone);
                today_calendar.set(today_calendar.get(Calendar.YEAR), today_calendar.get(Calendar.MONTH) + 1, today_calendar.get(Calendar.DAY_OF_MONTH));
                long today_in_millis = today_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);

                //키워드 날짜
                Calendar keyword_calendar = Calendar.getInstance(time_zone);
                String[] keyword_date_split = date_from_server.split("-");
                int keyword_date_year = Integer.parseInt(keyword_date_split[0]);
                int keyword_date_month = Integer.parseInt(keyword_date_split[1]);
                int keyword_date_day = Integer.parseInt(keyword_date_split[2]);
                keyword_calendar.set(keyword_date_year, keyword_date_month, keyword_date_day);
                long keyword_date_in_millis = keyword_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);

                //오늘 날짜 이후의 키워드인지 판별
                long temp = keyword_date_in_millis - today_in_millis;
                if (temp <= min) {
                    min = temp;
                    keyword_available = keyword_from_server;
                    date_available = date_from_server;
                }

                // 관전 mainChat으로 입장
                Intent intent = new Intent(context, MainChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("room_id", room_id);
                intent.putExtra("temp",2);
                if (!keyword_available.equals("") && !date_available.equals("")) {
                    intent.putExtra("keyword", keyword_available);
                    intent.putExtra("date", date_available);
                }
                context.startActivity(intent);
            }
    }
}
