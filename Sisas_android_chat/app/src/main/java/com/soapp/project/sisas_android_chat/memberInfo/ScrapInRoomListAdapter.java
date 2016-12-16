package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-12-14.
 */

public class ScrapInRoomListAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<ScrapInRoomListItem> scrap_in_room_item_list = new ArrayList<ScrapInRoomListItem>();

    public ScrapInRoomListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return scrap_in_room_item_list.size();
    }

    @Override
    public ScrapInRoomListItem getItem(int position) {
        return scrap_in_room_item_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.member_info_scrapbox_in_item, parent, false);
            convertView.setTag(position);
        }

        final TextView tv_single_keyword = (TextView)convertView.findViewById(R.id.tv_single_keyword);
        final TextView tv_single_keyword_date = (TextView)convertView.findViewById(R.id.tv_single_keyword_date);
        final TextView tv_scrap_article_title = (TextView)convertView.findViewById(R.id.tv_scrap_article_title);
        TextView tv_scrap_content = (TextView)convertView.findViewById(R.id.tv_scrap_content);


        ScrapInRoomListItem scrapInRoomListItem = scrap_in_room_item_list.get(position);

        tv_single_keyword.setText("< " + scrapInRoomListItem.getSingle_keyword() + " >");
        tv_single_keyword_date.setText(scrapInRoomListItem.getSingle_keyword_date());
        tv_scrap_article_title.setText(scrapInRoomListItem.getScrap_article_title());
        tv_scrap_content.setText(scrapInRoomListItem.getScrap_content());
        final String scrap_article_url = scrapInRoomListItem.getScrap_url();

        //제목을 클릭 시, 해당 링크 브라우저로 이동
        tv_scrap_article_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(scrap_article_url));
                context.startActivity(intent);
            }
        });

        final EditText et_opinion = (EditText)convertView.findViewById(R.id.et_opinion);
        //의견이 있는 기사는 의견 가지고 오기
        if(!scrapInRoomListItem.getScrap_opinion().equals("")){
            et_opinion.setText(scrapInRoomListItem.getScrap_opinion());
        }

        //의견 저장 버튼 클릭 시
        Button btn_save_opinion = (Button)convertView.findViewById(R.id.btn_save_opinion);
        btn_save_opinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                ScrapInRoomListItem item = getItem(position);

                String article_title = item.getScrap_article_title();
                String opinion = et_opinion.getText().toString();

                saveOpinionToServer(article_title, opinion);
            }
        });

        //채팅창으로 가져가기 버튼 클릭 시
        Button btn_bring_to_chat = (Button)convertView.findViewById(R.id.btn_bring_to_chat);

        return convertView;
    }

    private void saveOpinionToServer(String article_title, String opinion){
        final String URL = "http://52.78.157.250:3000/save_opinion";

        Map<String, Object> scrap_opinion_param = new HashMap<String, Object>();
        scrap_opinion_param.put("article_title", article_title);
        scrap_opinion_param.put("opinion", opinion);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(scrap_opinion_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast.makeText(context, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }else if(response.getString("result").equals("insert_success")) {
                            Toast.makeText(context, "의견이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, ScrapInRoomActivity.class);
                            context.startActivity(intent);
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
