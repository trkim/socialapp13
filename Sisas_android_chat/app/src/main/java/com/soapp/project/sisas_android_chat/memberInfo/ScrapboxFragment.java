package com.soapp.project.sisas_android_chat.memberInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyListExpandableAdapter;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyListItem;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyListItemChild;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-10-14.
 */
public class ScrapboxFragment extends Fragment {

    ExpandableListView ing_study_list;
    ExpandableListView before_start_study_list;
    ExpandableListView after_study_list;

    StudyListExpandableAdapter adapter;
    ArrayList<StudyListItem> study_list_parent_ing_item = new ArrayList<StudyListItem>();
    ArrayList<StudyListItem> study_list_parent_before_item = new ArrayList<StudyListItem>();
    ArrayList<StudyListItem> study_list_parent_after_item = new ArrayList<StudyListItem>();
    ArrayList<StudyListItemChild> study_list_child_ing_item = new ArrayList<StudyListItemChild>();
    ArrayList<StudyListItemChild> study_list_child_before_item = new ArrayList<StudyListItemChild>();
    ArrayList<StudyListItemChild> study_list_child_after_item = new ArrayList<StudyListItemChild>();
    HashMap<StudyListItem, StudyListItemChild> list_ing_map = new HashMap<StudyListItem, StudyListItemChild>();
    HashMap<StudyListItem, StudyListItemChild> list_before_map = new HashMap<StudyListItem, StudyListItemChild>();
    HashMap<StudyListItem, StudyListItemChild> list_after_map = new HashMap<StudyListItem, StudyListItemChild>();

    ArrayList<JSONObject> study_list = new ArrayList<JSONObject>();

    String dday_result = "";

    final int temp =3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_info_scrapbox_frag, container, false);

        ing_study_list = (ExpandableListView)view.findViewById(R.id.ing_study_list);
        before_start_study_list = (ExpandableListView)view.findViewById(R.id.before_start_study_list);
        after_study_list = (ExpandableListView)view.findViewById(R.id.after_study_list);

        try {
            getStudyFromServer();
        } catch (Exception e) {
            e.printStackTrace();
        }



        return view;
    }

    private void getStudyFromServer() throws Exception{
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

    private void getMyStudy(){
        int countIng = 0;
        int countBefore = 0;
        int countAfter = 0;
        Log.e("study_list.size()", String.valueOf(study_list.size()));
        for(int i=0 ;i<study_list.size(); i++) {

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

            if(dday_result.equals("~ing")) {
                addIngStudy(room_id, category, room_name, capacity, dday_result);
                addIngStudyChild(room_name, date, comment);
                list_ing_map.put(study_list_parent_ing_item.get(countIng), study_list_child_ing_item.get(countIng));
                countIng++;
            } else if(dday_result.startsWith("D-")){
                addBeforeStudy(room_id, category, room_name, capacity, dday_result);
                addBeforeStudyChild(room_name, date, comment);
                list_before_map.put(study_list_parent_before_item.get(countBefore), study_list_child_before_item.get(countBefore));
                countBefore++;
            } else if(dday_result.startsWith("D+")){
                addAfterStudy(room_id, category, room_name, capacity, dday_result);
                addAfterStudyChild(room_name, date, comment);
                list_after_map.put(study_list_parent_after_item.get(countAfter), study_list_child_after_item.get(countAfter));
                countAfter++;
            }
        } //end for

        Log.e("temp", String.valueOf(temp));

        //ing_study_list = (ExpandableListView)getActivity().findViewById(R.id.ing_study_list);
        adapter = new StudyListExpandableAdapter(getActivity(), study_list_parent_ing_item, list_ing_map, temp);
        ing_study_list.setAdapter(adapter);
        ing_study_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = adapter.getGroupCount();
                // 한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        ing_study_list.collapseGroup(i);
                }
            }
        });

        //before_start_study_list = (ExpandableListView)getActivity().findViewById(R.id.before_start_study_list);
        adapter = new StudyListExpandableAdapter(getActivity(), study_list_parent_before_item, list_before_map, temp);
        before_start_study_list.setAdapter(adapter);

        before_start_study_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = adapter.getGroupCount();
                // 한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        before_start_study_list.collapseGroup(i);
                }
            }
        });

        //after_study_list = (ExpandableListView)getActivity().findViewById(R.id.after_study_list);
        adapter = new StudyListExpandableAdapter(getActivity(), study_list_parent_after_item, list_after_map, temp);
        after_study_list.setAdapter(adapter);

        after_study_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = adapter.getGroupCount();
                // 한 그룹을 클릭하면 나머지 그룹들은 닫힌다.
                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        after_study_list.collapseGroup(i);
                }
            }
        });
    }



    private void addIngStudy(int room_id, String category, String room_name, int capacity, String dday){
        StudyListItem study = new StudyListItem(room_id, category, room_name, capacity, dday);
        study_list_parent_ing_item.add(study);
    }

    private void addIngStudyChild(String room_name, String date, String comment){
        StudyListItemChild study_child = new StudyListItemChild(room_name, date, comment);
        study_list_child_ing_item.add(study_child);
    }

    private void addBeforeStudy(int room_id, String category, String room_name, int capacity, String dday){
        StudyListItem study = new StudyListItem(room_id, category, room_name, capacity, dday);
        study_list_parent_before_item.add(study);
    }

    private void addBeforeStudyChild(String room_name, String date, String comment){
        StudyListItemChild study_child = new StudyListItemChild(room_name, date, comment);
        study_list_child_before_item.add(study_child);
    }

    private void addAfterStudy(int room_id, String category, String room_name, int capacity, String dday){
        StudyListItem study = new StudyListItem(room_id, category, room_name, capacity, dday);
        study_list_parent_after_item.add(study);
    }

    private void addAfterStudyChild(String room_name, String date, String comment){
        StudyListItemChild study_child = new StudyListItemChild(room_name, date, comment);
        study_list_child_after_item.add(study_child);
    }
}
