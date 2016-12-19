package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.soapp.project.sisas_android_chat.studyInRoom.MainChatActivity;
import com.soapp.project.sisas_android_chat.studyInRoom.MainChatFragment;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-12-14.
 */

public class ScrapInRoomListAdapter extends BaseAdapter {

    private int room_id;
    private Context context;
    //ScrapInRoomListItem scrap_in_room_item;
    public ArrayList<ScrapInRoomListItem> scrap_in_room_item_list = new ArrayList<ScrapInRoomListItem>();
    String opinion = "";

    public ScrapInRoomListAdapter(Context context, int room_id){
        this.context = context;
        this.room_id = room_id;
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

        final ScrapInRoomListItem scrap_in_room_item = scrap_in_room_item_list.get(position);

        tv_single_keyword.setText("키워드 : " + scrap_in_room_item.getSingle_keyword());
        tv_single_keyword_date.setText(scrap_in_room_item.getSingle_keyword_date());
        tv_scrap_article_title.setText(scrap_in_room_item.getScrap_article_title());
        tv_scrap_content.setText(scrap_in_room_item.getScrap_content());
        final String scrap_article_url = scrap_in_room_item.getScrap_url();

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
        if(!scrap_in_room_item.getScrap_opinion().equals("")){
            opinion = scrap_in_room_item.getScrap_opinion();
            et_opinion.setText(opinion);
        }

        //의견 저장 버튼 클릭 시
        Button btn_save_opinion = (Button)convertView.findViewById(R.id.btn_save_opinion);
        btn_save_opinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scrap_id = scrap_in_room_item.getScrap_article_title()+scrap_in_room_item.getSingle_keyword_date()+scrap_in_room_item.getSingle_keyword();
                String opinion = et_opinion.getText().toString();
                Log.e("opinion", opinion);
                try {
                    saveOpinionToServer(scrap_id, opinion);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //채팅창으로 가져가기 버튼 클릭 시
        Button btn_bring_to_chat = (Button)convertView.findViewById(R.id.btn_bring_to_chat);
        btn_bring_to_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainChatFragment mainFrag = new MainChatFragment();
                mainFrag.sendArticle(scrap_in_room_item.getScrap_article_title(), scrap_in_room_item.getScrap_url(), opinion);
                //채팅방으로 돌아가야함

                Intent intent = new Intent(context, MainChatActivity.class);
                intent.putExtra("room_id", room_id);
                intent.putExtra("keyword", scrap_in_room_item.getSingle_keyword());
                intent.putExtra("date", scrap_in_room_item.getSingle_keyword_date());
                intent.putExtra("temp", 1);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void addKeywordScrap(String keyword, String date, String article_title, String url, String content, String opinion){
        ScrapInRoomListItem item = new ScrapInRoomListItem(keyword, date, article_title, url, content, opinion);
        item.setSingle_keyword(keyword);
        item.setSingle_keyword_date(date);
        item.setScrap_article_title(article_title);
        item.setScrap_url(url);
        item.setScrap_content(content);
        item.setScrap_opinion(opinion);

        scrap_in_room_item_list.add(item);
    }

    private void saveOpinionToServer(final String scrap_id, final String opinion){
        final String URL = "http://52.78.157.250:3000/save_opinion";

        Map<String, Object> scrap_opinion_param = new HashMap<String, Object>();
        scrap_opinion_param.put("scrap_id", scrap_id);
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
