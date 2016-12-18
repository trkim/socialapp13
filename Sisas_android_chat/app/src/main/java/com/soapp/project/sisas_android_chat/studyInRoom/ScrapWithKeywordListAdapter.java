package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.member.JoinActivity;
import com.soapp.project.sisas_android_chat.member.LoginActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-12-12.
 */

public class ScrapWithKeywordListAdapter extends BaseAdapter {

    //ScrapWithKeywordItem scrap_item;
    public ArrayList<ScrapWithKeywordItem> scrap_keyword_item_list = new ArrayList<ScrapWithKeywordItem>();

    public ScrapWithKeywordListAdapter(){}

    @Override
    public int getCount() {
        return scrap_keyword_item_list.size();
    }

    @Override
    public ScrapWithKeywordItem getItem(int position) {
        return scrap_keyword_item_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        Log.e("scrap getView", "scrap getView");
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.scrap_with_keyword_item, parent, false);
            convertView.setTag(position);
        }

        TextView tv_scrap_title = (TextView)convertView.findViewById(R.id.tv_scrap_title);
        TextView tv_scrap_content = (TextView)convertView.findViewById(R.id.tv_scrap_content);

        final ScrapWithKeywordItem scrap_item = scrap_keyword_item_list.get(position);
        tv_scrap_title.setText(scrap_item.getTv_scrap_title());
        final String url = scrap_item.getScrap_url();
        tv_scrap_content.setText(scrap_item.getTv_scrap_content());

        //제목을 클릭 시, 해당 링크 브라우저로 이동
        tv_scrap_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        Button btn_scrap = (Button)convertView.findViewById(R.id.btn_scrap);
        btn_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScrapToServer(v, scrap_item.getTv_scrap_title(),
                        scrap_item.getScrap_url(),
                        scrap_item.getTv_scrap_content(),
                        scrap_item.getOpinion(),
                        scrap_item.getKeyword(),
                        scrap_item.getDate(),
                        scrap_item.getEmail(),
                        scrap_item.getRoom_id() );
            }
        });

        return convertView;
    }

    public void addArticleInfo(String title, String url, String content, String opinion, String keyword, String date, String email, int room_id){
        ScrapWithKeywordItem item = new ScrapWithKeywordItem();
        item.setTv_scrap_title(title);
        item.setScrap_url(url);
        item.setTv_scrap_content(content);
        item.setOpinion(opinion);
        item.setKeyword(keyword);
        item.setDate(date);
        item.setEmail(email);
        item.setRoom_id(room_id);

        scrap_keyword_item_list.add(item);
    }

    public void saveScrapToServer(final View view, final String title, final String url, final String content, final String opinion, final String keyword, final String date, final String email, final int room_id){
        final String URL = "http://52.78.157.250:3000/insert_scrap";

        String keyword_box_id = date+keyword;

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("article_title", title);
        param.put("url", url);
        param.put("opinion", opinion);
        param.put("content",content);
        param.put("keyword_box_id", keyword_box_id);
        param.put("email", email);
        param.put("room_id", room_id);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast toast1 = Toast.makeText(view.getContext(), "이미 스크랩한 기사 입니다.", Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER, 0,0);
                            toast1.show();
                        }else if(response.getString("result").equals("success")) {
                            Toast toast2 = Toast.makeText(view.getContext(), "스크랩 박스에 저장되었습니다.", Toast.LENGTH_SHORT);
                            toast2.setGravity(Gravity.CENTER, 0,0);
                            toast2.show();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("development", "Error: " + error.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }
}
