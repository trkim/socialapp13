package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.soapp.project.sisas_android_chat.memberInfo.MyProfileFragment.StringToBitmap;

/**
 * Created by eelhea on 2016-10-31.
 */
public class StudyShowApplyMemberListAdapter extends BaseAdapter{

    public ArrayList<StudyShowApplyMemberListItem> apply_member_item_list = new ArrayList<StudyShowApplyMemberListItem>();

    public StudyShowApplyMemberListAdapter() {}

    @Override
    public int getCount() {
        return apply_member_item_list.size();
    }

    @Override
    public StudyShowApplyMemberListItem getItem(int position) {
        return apply_member_item_list.get(position);
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
            convertView = inflater.inflate(R.layout.study_show_apply_member_list_item, parent, false);
        }

        CircleImageView profile_pic = (CircleImageView)convertView.findViewById(R.id.profile_pic);
        TextView tv_member_name = (TextView) convertView.findViewById(R.id.tv_member_name);
        TextView tv_member_major = (TextView) convertView.findViewById(R.id.tv_member_major);
        TextView tv_member_interest = (TextView) convertView.findViewById(R.id.tv_member_interest);

        StudyShowApplyMemberListItem list_view_item = apply_member_item_list.get(position);

        if(list_view_item.getProfile_pic().equals("")){
            profile_pic.setImageResource(R.mipmap.ic_profile);
        } else {
            profile_pic.setImageBitmap(StringToBitmap(list_view_item.getProfile_pic()));
        }
        tv_member_name.setText(list_view_item.getMember_name());
        tv_member_major.setText(list_view_item.getMember_major());
        tv_member_interest.setText(list_view_item.getMember_interest().substring(1));

        return convertView;
    }

    public void addApplyMember(String profile_pic, String name, String major, String interest){
        StudyShowApplyMemberListItem new_member = new StudyShowApplyMemberListItem();
        new_member.setProfile_pic(profile_pic);
        new_member.setMember_name(name);
        new_member.setMember_major(major);
        new_member.setMember_interest(interest);

        apply_member_item_list.add(new_member);
    }
}
