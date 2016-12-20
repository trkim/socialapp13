package com.soapp.project.sisas_android_chat.memberInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
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
 * Created by eelhea on 2016-10-14.
 */
public class TimelineFragment extends Fragment {

    ListView lv_timeline;
    TimelineListAdapter timelineListAdapter;
    ArrayList<JSONObject> timeline_list = new ArrayList<JSONObject>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_info_timeline_frag, container, false);

        lv_timeline = (ListView)view.findViewById(R.id.lv_timeline);
        timelineListAdapter = new TimelineListAdapter(getActivity());

        try{
            getTimelineFromServer();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void getTimelineFromServer() throws Exception{
        String email = Member.getInstance().getEmail();
        final String URL = "http://52.78.157.250:3000/get_mytimelinelist?email=" + URLEncoder.encode(email, "UTF-8");

        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    timeline_list.add(response.optJSONObject(i));
                }
                getMyTimelineList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    private void getMyTimelineList(){
        for(int i=0 ;i<timeline_list.size(); i++) {
            String keyword_box_id = timeline_list.get(i).optString("keyword_box_id");
            //keyword 만 추출
            String keyword = keyword_box_id.substring(keyword_box_id.length()-11, keyword_box_id.length());
            String title = timeline_list.get(i).optString("title");
            String url = timeline_list.get(i).optString("url");
            String content = timeline_list.get(i).optString("content");
            String opinion = timeline_list.get(i).optString("opinion");

            timelineListAdapter.addMyTimelineList(keyword, title, url, content, opinion);
        }
        lv_timeline.setAdapter(timelineListAdapter);
    }
}
