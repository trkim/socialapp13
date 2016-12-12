package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by eelhea on 2016-11-03.
 */
public class StudyListExpandableAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater inflater;
    private Context context;
    private ArrayList<StudyListItem> study_list_parent;
    private StudyListItemChildHolder study_list_child_holder = new StudyListItemChildHolder();
    private HashMap<StudyListItem, StudyListItemChild> list_child_map;

    ImageButton ib_study_go;

    public StudyListExpandableAdapter(Context context, ArrayList<StudyListItem> study_list_parent, HashMap<StudyListItem, StudyListItemChild> list_child_map){
        this.context = context;
        this.study_list_parent = study_list_parent;
        this.list_child_map = list_child_map;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public StudyListItem getGroup(int groupPosition) {
        return study_list_parent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return study_list_parent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView == null){
            //LayoutInflater group_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = group_inflater.inflate(R.layout.study_list_item, parent, false);
            convertView = inflater.inflate(R.layout.study_list_item, parent, false);

        }

        ib_study_go = (ImageButton)convertView.findViewById(R.id.ib_study_go);
        ib_study_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudyShowApplyActivity.class);
                int room_id = study_list_parent.get(groupPosition).getRoom_id();
                intent.putExtra("room_id", room_id);
                context.startActivity(intent);
            }
        });

        TextView tv_room_category = (TextView) convertView.findViewById(R.id.tv_room_category);
        TextView tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
        TextView tv_room_capacity = (TextView) convertView.findViewById(R.id.tv_room_capacity);
        TextView tv_room_dday = (TextView) convertView.findViewById(R.id.tv_room_dday);

        tv_room_name.setSelected(true);

        tv_room_category.setText(getGroup(groupPosition).getStudy_category());
        tv_room_name.setText(getGroup(groupPosition).getStudy_name());
        tv_room_capacity.setText(String.valueOf(getGroup(groupPosition).getStudy_capacity()));
        tv_room_dday.setText(getGroup(groupPosition).getStudy_dday());

        return convertView;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public StudyListItemChild getChild(int groupPosition, int childPosition) {
        return this.list_child_map.get(this.study_list_parent.get(groupPosition));
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        StudyListItemChild item_child = (StudyListItemChild)getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater child_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = child_inflater.inflate(R.layout.study_list_item_child, null);

            study_list_child_holder = new StudyListItemChildHolder();
            study_list_child_holder.study_name_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_name);
            study_list_child_holder.study_date_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_date);
            study_list_child_holder.study_comment_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_comment);

            convertView.setTag(study_list_child_holder);
        } else {
            study_list_child_holder = (StudyListItemChildHolder)convertView.getTag();
        }
        study_list_child_holder.study_name_holder.setText(item_child.getStudy_name());
        study_list_child_holder.study_date_holder.setText(item_child.getStudy_date());
        study_list_child_holder.study_comment_holder.setText(item_child.getStudy_comment());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
