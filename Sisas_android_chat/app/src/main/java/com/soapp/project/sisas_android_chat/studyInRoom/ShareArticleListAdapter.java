package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-12-21.
 */

public class ShareArticleListAdapter extends BaseAdapter {

    private int room_id;
    private Context context;
    public ArrayList<ShareArticleItem> shareArticleItemsList = new ArrayList<ShareArticleItem>();
    String opinion = "";

    public ShareArticleListAdapter(Context context, int room_id){
        this.context = context;
        this.room_id = room_id;
    }

    @Override
    public int getCount() {
        return shareArticleItemsList.size();
    }

    @Override
    public ShareArticleItem getItem(int position) {
        return shareArticleItemsList.get(position);
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
            convertView = inflater.inflate(R.layout.share_article_item, parent, false);
            convertView.setTag(position);
        }

        TextView tv_email = (TextView)convertView.findViewById(R.id.tv_email);
        TextView tv_share_title = (TextView)convertView.findViewById(R.id.tv_share_title);
        TextView tv_share_content = (TextView)convertView.findViewById(R.id.tv_share_content);
        TextView tv_share_opinion = (TextView)convertView.findViewById(R.id.tv_share_opinion);

        final ShareArticleItem shareArticleItem = shareArticleItemsList.get(position);
        tv_share_title.setText(shareArticleItem.getArticle_title());
        final String url = shareArticleItem.getUrl();
        //제목을 클릭 시, 해당 링크 브라우저로 이동
        tv_share_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        tv_email.setText(shareArticleItem.getEmail());
        tv_share_content.setText(shareArticleItem.getContent());
        if(!shareArticleItem.getOpinion().equals("")) {
            opinion = shareArticleItem.getOpinion();
        }
        tv_share_opinion.setText(opinion);

        Button btn_bring_to_timeline = (Button)convertView.findViewById(R.id.btn_bring_to_timeline);
        btn_bring_to_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //keyword_box_id, title, url, content , opinion, email
                    //keyword_box_id = date+keyword
                    String keyword_box_id = shareArticleItem.getDate()+shareArticleItem.getKeyword();
                    String title = shareArticleItem.getArticle_title();
                    String url = shareArticleItem.getUrl();
                    String content = shareArticleItem.getContent();
                    String opinion = shareArticleItem.getOpinion();
                    String email = shareArticleItem.getEmail();

                    insertTimelineToServer(keyword_box_id, title, url, content, opinion, email);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public void addShareArticle(String title, String url, String content, String opinion, String keyword, String date, int room_id, String email){
        ShareArticleItem item = new ShareArticleItem();
        item.setArticle_title(title);
        item.setUrl(url);
        item.setContent(content);
        item.setOpinion(opinion);
        item.setKeyword(keyword);
        item.setDate(date);
        item.setRoom_id(room_id);
        item.setEmail(email);

        shareArticleItemsList.add(item);
    }

    private void insertTimelineToServer(final String keyword_box_id, final String title, final String url, final String content, final String opinion, final String email) {
        final String URL = "http://52.78.157.250:3000/insert_timeline";

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("keyword_box_id", keyword_box_id);
        param.put("title", title);
        param.put("url", url);
        param.put("content", content);
        param.put("opinion", opinion);
        param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast toast1 = Toast.makeText(context, "이미 저장된 기사 입니다.", Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER, 0,0);
                            toast1.show();
                        }else if(response.getString("result").equals("success")) {
                            Toast toast2 = Toast.makeText(context, "타임라인에 저장되었습니다.", Toast.LENGTH_SHORT);
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
