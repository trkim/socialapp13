package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import java.util.ArrayList;

/**
 * Created by eelhea on 2016-12-12.
 */

public class ScrapWithKeywordListAdapter extends BaseAdapter {

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
        }

        TextView tv_scrap_title = (TextView)convertView.findViewById(R.id.tv_scrap_title);
        TextView tv_scrap_content = (TextView)convertView.findViewById(R.id.tv_scrap_content);

        ScrapWithKeywordItem scrap_item = scrap_keyword_item_list.get(position);
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

        return convertView;
    }

    public void addArticleInfo(String title, String url, String content){
        ScrapWithKeywordItem item = new ScrapWithKeywordItem();
        item.setTv_scrap_title(title);
        item.setScrap_url(url);
        item.setTv_scrap_content(content);

        scrap_keyword_item_list.add(item);
    }
}
