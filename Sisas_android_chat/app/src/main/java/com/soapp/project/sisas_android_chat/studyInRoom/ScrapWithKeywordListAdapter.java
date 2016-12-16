package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Context;
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

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.scrap_with_keyword_item, parent, false);
        }

        TextView tv_scrap_url = (TextView)convertView.findViewById(R.id.tv_scrap_url);
        TextView tv_scrap_content = (TextView)convertView.findViewById(R.id.tv_scrap_content);
        Button btn_scrap = (Button)convertView.findViewById(R.id.btn_scrap);

        ScrapWithKeywordItem scrap_item = scrap_keyword_item_list.get(position);

        tv_scrap_url.setText(scrap_item.getTv_scrap_url());
        tv_scrap_content.setText(scrap_item.getTv_scrap_content());

        return convertView;
    }
}
