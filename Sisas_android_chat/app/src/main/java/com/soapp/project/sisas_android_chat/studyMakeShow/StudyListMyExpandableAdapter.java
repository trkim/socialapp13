package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyInRoom.OtActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyListMyExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<StudyListMyItem> my_study_list_parent;
    private StudyListMyItemChildHolder my_study_list_child_holder = new StudyListMyItemChildHolder();
    private HashMap<StudyListMyItem, StudyListMyItemChild> my_list_child_map;

    ImageButton ib_head_icon;
    ImageButton ib_study_go;

    //private Socket mSocket;

    public StudyListMyExpandableAdapter(Context context, ArrayList<StudyListMyItem> my_study_list_parent, HashMap<StudyListMyItem, StudyListMyItemChild> my_list_child_map){
        this.context = context;
        this.my_study_list_parent = my_study_list_parent;
        this.my_list_child_map = my_list_child_map;
    }

    @Override
    public StudyListMyItem getGroup(int groupPosition) {
        return my_study_list_parent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return my_study_list_parent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater group_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = group_inflater.inflate(R.layout.study_list_my_item, parent, false);
        }

        ib_head_icon = (ImageButton)convertView.findViewById(R.id.ib_head_icon);
        if(getGroup(groupPosition).getStudy_icon() == 1) {
            ib_head_icon.setImageResource(R.mipmap.ic_header);
            ib_head_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StudyMakeUpdateDeleteActivity.class);
                    int room_id = my_study_list_parent.get(groupPosition).getRoom_id();
                    intent.putExtra("room_id", room_id);
                    context.startActivity(intent);
                }
            });
        } else if(getGroup(groupPosition).getStudy_icon() == 0) {
            ib_head_icon.setEnabled(false);
            ib_head_icon.setClickable(false);
            ib_head_icon.setImageResource(R.mipmap.ic_whitespace);
            ib_head_icon.setBackgroundColor(Color.WHITE);
        }

        ib_study_go = (ImageButton)convertView.findViewById(R.id.ib_study_go);
        ib_study_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //status == pre -> ot 채팅방 입장
                int room_id = my_study_list_parent.get(groupPosition).getRoom_id();
                String dday = my_study_list_parent.get(groupPosition).getStudy_dday();

                //D-day가 ~ing이면 ot 채팅방으로 못 들어가게 함.
                if(dday.equals("~ing")){
                    Toast.makeText(context, "이미 진행중인 스터디입니다." , Toast.LENGTH_LONG).show();
                }
                //D-day가 D+ 이면 ot 채팅방으로 못 들어가게 함.
                else if(dday.contains("D+")){
                    Toast.makeText(context, "이미 끝난 스터디입니다." , Toast.LENGTH_LONG).show();
                }
                //ot 채팅방으로 들어감
                else{
                    Intent intent = new Intent(context, OtActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("room_id", room_id);
                    Log.e("adapter room_id", String.valueOf(room_id));
                    context.startActivity(intent);
                }
            }
        });

        TextView tv_my_room_category = (TextView) convertView.findViewById(R.id.tv_my_room_category);
        TextView tv_my_room_name = (TextView) convertView.findViewById(R.id.tv_my_room_name);
        TextView tv_my_room_capacity = (TextView) convertView.findViewById(R.id.tv_my_room_capacity);
        TextView tv_my_room_dday = (TextView) convertView.findViewById(R.id.tv_my_room_dday);

        tv_my_room_category.setText(getGroup(groupPosition).getStudy_category());
        tv_my_room_name.setText(getGroup(groupPosition).getStudy_name());
        tv_my_room_capacity.setText(String.valueOf(getGroup(groupPosition).getStudy_capacity()));
        tv_my_room_dday.setText(getGroup(groupPosition).getStudy_dday());

        tv_my_room_category.setTextColor(Color.BLACK);
        tv_my_room_name.setTextColor(Color.BLACK);
        tv_my_room_capacity.setTextColor(Color.BLACK);
        tv_my_room_dday.setTextColor(Color.BLACK);

        return convertView;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public StudyListMyItemChild getChild(int groupPosition, int childPosition) {
        return this.my_list_child_map.get(this.my_study_list_parent.get(groupPosition));
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
        StudyListMyItemChild item_child = (StudyListMyItemChild)getChild(groupPosition, childPosition);

        if(convertView == null){
            LayoutInflater child_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = child_inflater.inflate(R.layout.study_list_my_item_child, null);

            my_study_list_child_holder = new StudyListMyItemChildHolder();
            my_study_list_child_holder.study_name_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_name);
            my_study_list_child_holder.study_date_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_date);
            my_study_list_child_holder.study_comment_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_comment);
            my_study_list_child_holder.study_keyword_holder = (TextView)convertView.findViewById(R.id.tv_slide_room_keyword);

            convertView.setTag(my_study_list_child_holder);
        } else {
            my_study_list_child_holder = (StudyListMyItemChildHolder)convertView.getTag();
        }
        my_study_list_child_holder.study_name_holder.setText(item_child.getStudy_name());
        my_study_list_child_holder.study_date_holder.setText(item_child.getStudy_date());
        my_study_list_child_holder.study_comment_holder.setText(item_child.getStudy_comment());
        my_study_list_child_holder.study_keyword_holder.setText(item_child.getStudy_keyword());

        my_study_list_child_holder.study_name_holder.setTextColor(Color.BLACK);
        my_study_list_child_holder.study_date_holder.setTextColor(Color.BLACK);
        my_study_list_child_holder.study_comment_holder.setTextColor(Color.BLACK);
        my_study_list_child_holder.study_keyword_holder.setTextColor(Color.BLACK);

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
