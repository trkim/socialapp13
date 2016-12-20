package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import java.util.ArrayList;

/**
 * Created by eelhea on 2016-12-20.
 */

public class TimelineListAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<TimelineListItem> timelineListItems = new ArrayList<TimelineListItem>();
    String opinion="";

    public TimelineListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return timelineListItems.size();
    }

    @Override
    public TimelineListItem getItem(int position) {
        return timelineListItems.get(position);
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
            convertView = inflater.inflate(R.layout.member_info_timeline_item, parent, false);
            convertView.setTag(position);
        }

        TextView tv_keyword = (TextView)convertView.findViewById(R.id.tv_keyword);
        TextView tv_title = (TextView)convertView.findViewById(R.id.tv_title);
        TextView tv_content = (TextView)convertView.findViewById(R.id.tv_content);
        TextView tv_opinion = (TextView)convertView.findViewById(R.id.tv_opinion);

        TimelineListItem listItem = timelineListItems.get(position);

        final String url = listItem.getUrl();

        //제목을 클릭 시, 해당 링크 브라우저로 이동
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        tv_keyword.setText(listItem.getKeyword());
        tv_title.setText(listItem.getTitle());
        tv_content.setText(listItem.getContent());
        opinion = listItem.getOpinion();
        tv_opinion.setText(opinion);

        return convertView;
    }

    public void addMyTimelineList(String keyword, String title, String url, String content, String opinion){
        TimelineListItem item = new TimelineListItem();
        item.setKeyword(keyword);
        item.setTitle(title);
        item.setUrl(url);
        item.setContent(content);
        item.setOpinion(opinion);

        timelineListItems.add(item);
    }
}
