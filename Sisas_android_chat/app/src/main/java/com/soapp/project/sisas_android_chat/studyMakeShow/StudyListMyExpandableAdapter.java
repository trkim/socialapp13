package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyInRoom.MainChatActivity;
import com.soapp.project.sisas_android_chat.studyInRoom.OtChatActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyListMyExpandableAdapter extends BaseExpandableListAdapter {

    final int temp =1;
    private Context context;
    private ArrayList<StudyListMyItem> my_study_list_parent;
    private StudyListMyItemChildHolder my_study_list_child_holder = new StudyListMyItemChildHolder();
    private HashMap<StudyListMyItem, StudyListMyItemChild> my_list_child_map;
    private ArrayList<JSONObject> keyword_list = new ArrayList<JSONObject>();

    int room_id;
    ImageButton ib_head_icon;
    ImageButton ib_study_go;


    String keyword_available = "";
    String date_available = "";

    //private Socket mSocket;

    public StudyListMyExpandableAdapter(Context context, ArrayList<StudyListMyItem> my_study_list_parent, HashMap<StudyListMyItem, StudyListMyItemChild> my_list_child_map, ArrayList<JSONObject> keyword_list){
        this.context = context;
        this.my_study_list_parent = my_study_list_parent;
        this.my_list_child_map = my_list_child_map;
        this.keyword_list = keyword_list;
    }

    @Override
    public StudyListMyItem getGroup(int groupPosition) {
        return my_study_list_parent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return my_study_list_parent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater group_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = group_inflater.inflate(R.layout.study_list_my_item, parent, false);
        }

        ib_head_icon = (ImageButton)convertView.findViewById(R.id.ib_head_icon);
        if(getGroup(groupPosition).getStudy_icon() == 1) {
            ib_head_icon.setImageResource(R.mipmap.ic_header);
            ib_head_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StudyMakeUpdateDeleteActivity.class);
                    room_id = my_study_list_parent.get(groupPosition).getRoom_id();
                    intent.putExtra("room_id", room_id);
                    context.startActivity(intent);
                }
            });
        } else if(getGroup(groupPosition).getStudy_icon() == 0) {
            ib_head_icon.setVisibility(View.INVISIBLE);
            ib_head_icon.setEnabled(false);
            ib_head_icon.setClickable(false);
            ib_head_icon.setImageResource(R.mipmap.ic_whitespace);
            ib_head_icon.setBackgroundColor(Color.WHITE);
        }

        ib_study_go = (ImageButton)convertView.findViewById(R.id.ib_study_go);
        ib_study_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //status == pre -> ot 채팅방 입장
                int room_id = my_study_list_parent.get(groupPosition).getRoom_id();
                String dday = my_study_list_parent.get(groupPosition).getStudy_dday();

                if(dday.contains("D+")){ //D-day가 D+ 이면 ot 채팅방으로 못 들어가게 함.
                    Toast.makeText(context, "이미 끝난 스터디입니다." , Toast.LENGTH_LONG).show();
                } else{ //ot 채팅방으로 들어감
                    //등록된 키워드가 있는지 체크
                    try {
                        checkForKeywordFromServer(room_id, dday);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        TextView tv_my_room_category = (TextView) convertView.findViewById(R.id.tv_my_room_category);
        TextView tv_my_room_name = (TextView) convertView.findViewById(R.id.tv_my_room_name);
        TextView tv_my_room_capacity = (TextView) convertView.findViewById(R.id.tv_my_room_capacity);
        TextView tv_my_room_dday = (TextView) convertView.findViewById(R.id.tv_my_room_dday);

        tv_my_room_category.setText(getGroup(groupPosition).getStudy_category());
        tv_my_room_name.setText(getGroup(groupPosition).getStudy_name());
        tv_my_room_capacity.setText(String.valueOf(getGroup(groupPosition).getStudy_capacity()));
        tv_my_room_dday.setText(getGroup(groupPosition).getStudy_dday());
        tv_my_room_category.setTextColor(Color.BLACK);
        tv_my_room_name.setTextColor(Color.BLACK);
        tv_my_room_name.setSingleLine(true); //한줄로 나오게 하기.
        tv_my_room_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);//Ellipsize의 MARQUEE 속성 주기
        tv_my_room_name.setFocusable(true);
        tv_my_room_name.setSelected(true); //해당 텍스트뷰에 포커스가 없어도 문자 흐르게 하기

        tv_my_room_capacity.setTextColor(Color.BLACK);
        tv_my_room_dday.setTextColor(Color.BLACK);

        return convertView;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public StudyListMyItemChild getChild(int groupPosition, int childPosition) {
        return this.my_list_child_map.get(this.my_study_list_parent.get(groupPosition));
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
        StudyListMyItemChild item_child = (StudyListMyItemChild)getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater child_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = child_inflater.inflate(R.layout.study_list_my_item_child, null);

            my_study_list_child_holder = new StudyListMyItemChildHolder();
            my_study_list_child_holder.study_name_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_name);
            my_study_list_child_holder.study_date_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_date);
            my_study_list_child_holder.study_comment_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_comment);

            convertView.setTag(my_study_list_child_holder);
        } else {
            my_study_list_child_holder = (StudyListMyItemChildHolder)convertView.getTag();
        }

        my_study_list_child_holder.study_name_holder.setText(item_child.getStudy_name());
        my_study_list_child_holder.study_date_holder.setText(item_child.getStudy_date());
        my_study_list_child_holder.study_comment_holder.setText(item_child.getStudy_comment());

        my_study_list_child_holder.study_name_holder.setTextColor(Color.BLACK);
        my_study_list_child_holder.study_date_holder.setTextColor(Color.BLACK);
        my_study_list_child_holder.study_comment_holder.setTextColor(Color.BLACK);

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

    private void checkForKeywordFromServer(final int room_id, final String dday){
        final String URL = "http://52.78.157.250:3000/get_keyword?room_id="+room_id;

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    for(int i=0; i<response.length(); i++){
                        keyword_list.add(response.optJSONObject(i));
                    }
                    getKeyword(room_id, dday);
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

    private void getKeyword(final int room_id, final String dday){
        long min = 999999999;
        if(keyword_list.size()==0){
            Intent intent = new Intent(context, OtChatActivity.class);
            intent.putExtra("room_id", room_id);
            context.startActivity(intent);
        } else {
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

                // dday가 ~ing면 mainChat으로 넘어가기 < --아니면--> otChat으로 넘어가기
                if(dday.equals("~ing")){
                    Toast.makeText(context, "참가 입장합니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, MainChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("temp", 1); //참가하기 입장
                    if (!keyword_available.equals("") && !date_available.equals("")) {
                        intent.putExtra("keyword", keyword_available);
                        intent.putExtra("date", date_available);
                    }
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, OtChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("room_id", room_id);
                    if (!keyword_available.equals("") && !date_available.equals("")) {
                        intent.putExtra("keyword", keyword_available);
                        intent.putExtra("date", date_available);
                    }
                    context.startActivity(intent);
                }
            }
        }
    }
}
