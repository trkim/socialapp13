package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-10-21.
 */
public class StudyShowWatchFragment extends Fragment {

    View view;
    String selected_category;
    ExpandableListView expandable_list_view;
    StudyListExpandableAdapter adapter;
    ArrayList<StudyListItem> study_list_parent_item;
    ArrayList<StudyListItemChild> study_list_child_item;
    HashMap<StudyListItem, StudyListItemChild> list_child_map;

    ArrayList<JSONObject> study_list;

    final int temp=1;

    public StudyShowWatchFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.study_show_watch_frag, container, false);
        expandable_list_view = (ExpandableListView)view.findViewById(R.id.study_list);

        try{
            Bundle bundle_arg = getArguments();
            if(bundle_arg != null) {
                study_list_parent_item = new ArrayList<StudyListItem>();
                study_list_child_item = new ArrayList<StudyListItemChild>();
                list_child_map = new HashMap<StudyListItem, StudyListItemChild>();
                study_list = new ArrayList<JSONObject>();

                study_list_parent_item.clear();
                study_list_child_item.clear();
                list_child_map.clear();
                study_list.clear();

                selected_category = bundle_arg.getString("category");
                getStudyFromServer(view);
            } else {
                Toast.makeText(getActivity(), "arguments is null " , Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        adapter = new StudyListExpandableAdapter(getActivity(), study_list_parent_item, list_child_map, temp);
        adapter.notifyDataSetChanged();
        expandable_list_view.setAdapter(adapter);

        expandable_list_view.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = adapter.getGroupCount();
                // 한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        expandable_list_view.collapseGroup(i);
                }
            }
        });

        return view;
    }

    private void getStudyFromServer(final View view) throws Exception{
        String category = selected_category;
        if(category.equals("전체")){
            category="all";
        } else if(category.equals("정치")){
            category="politics";
        } else if(category.equals("경제")){
            category="economics";
        } else if(category.equals("사회")){
            category="social";
        } else if(category.equals("IT")){
            category="it";
        } else if(category.equals("세계")){
            category="world";
        }
        String URL= "http://52.78.157.250:3000/get_ctgroomlist?category="+ URLEncoder.encode(category,"UTF-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    study_list.add(response.optJSONObject(i));
                    adapter.notifyDataSetChanged();
                }
                getStudy(view);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    public void getStudy(View view){
        int count = 0;
        for(int i=0; i<study_list.size(); i++){
            int room_id = study_list.get(i).optInt("room_id");
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

            //int result = (int)start_subs_day;
            String dday_result="";

            String date = start_date + " ~ " + end_date;
            String comment = study_list.get(i).optString("comment");

            if(start_subs_day >= 0 && end_subs_day <= 0){
                dday_result = String.format("~ing", start_subs_day);
                if(selected_category.equals("전체")) {
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                } else if(selected_category.equals("정치") && category.equals("정치")){
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                } else if(selected_category.equals("경제") && category.equals("경제")){
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                } else if(selected_category.equals("사회") && category.equals("사회")){
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                } else if(selected_category.equals("IT") && category.equals("IT")){
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                } else if(selected_category.equals("세계") && category.equals("세계")){
                    add(count, room_id, category, room_name, capacity, dday_result, date, comment);
                }
                count++;
            } //end if
        } //end for
    } //end getStudy

    private void add(int i, int room_id, String category, String room_name, int capacity, String dday_result, String date, String comment){
        addStudy(room_id, category, room_name, capacity, dday_result);
        addStudyChild(room_name, date, comment);
        list_child_map.put(study_list_parent_item.get(i), study_list_child_item.get(i));
    }

    private void addStudy(int room_id, String category, String room_name, int capacity, String dday){
        StudyListItem study = new StudyListItem(room_id, category, room_name, capacity, dday);
        study_list_parent_item.add(study);
    }

    private void addStudyChild(String room_name, String date, String comment){
        StudyListItemChild study_child = new StudyListItemChild(room_name, date, comment);
        study_list_child_item.add(study_child);
    }
}
