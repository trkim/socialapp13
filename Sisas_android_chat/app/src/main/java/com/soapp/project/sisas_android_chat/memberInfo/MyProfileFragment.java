package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.soapp.project.sisas_android_chat.member.LoginActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-10-14.
 */
public class MyProfileFragment extends Fragment {

    SharedPreferences memberSession;

    TextView profile_name;
    TextView profile_email;
    TextView profile_grade;
    TextView profile_major;
    TextView profile_interest;
    TextView profile_coupon_num;

    Button btn_profile_update;
    Button btn_logout;
    Button btn_leave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_info_profile_frag, container, false);

        memberSession = getActivity().getSharedPreferences("MemberSession", Context.MODE_PRIVATE);

        profile_name = (TextView)view.findViewById(R.id.profile_name);
        profile_email = (TextView)view.findViewById(R.id.profile_email);
        profile_grade = (TextView)view.findViewById(R.id.profile_grade);
        profile_major = (TextView)view.findViewById(R.id.profile_major);
        profile_interest = (TextView)view.findViewById(R.id.profile_interest);
        profile_coupon_num = (TextView)view.findViewById(R.id.profile_coupon_num);

        profile_name.setText(Member.getInstance().getName());
        profile_email.setText(Member.getInstance().getEmail());
        profile_grade.setText(Member.getInstance().getRating());
        profile_major.setText(Member.getInstance().getMajor());
        profile_interest.setText(Member.getInstance().getCategory());
        profile_coupon_num.setText(String.valueOf(Member.getInstance().getCoupon()));

        btn_profile_update = (Button)view.findViewById(R.id.btn_profile_update);
        btn_logout = (Button)view.findViewById(R.id.btn_logout);
        btn_leave = (Button)view.findViewById(R.id.btn_leave);

        btn_profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent member_update_intent = new Intent(getActivity(), MemberUpdateActivity.class);
                startActivity(member_update_intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveDialog();
            }
        });

        return view;
    }

    protected void showLogoutDialog(){
        LayoutInflater current_layout = LayoutInflater.from(getActivity());
        View dialog_view = current_layout.inflate(R.layout.activity_logout_member, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getActivity());
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SharedPreferences.Editor editor = memberSession.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alert_dialog_builder.create();
        alert.show();
    }

    protected void showLeaveDialog(){
        LayoutInflater current_layout = LayoutInflater.from(getActivity());
        View dialog_view = current_layout.inflate(R.layout.activity_leave_member, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(getActivity());
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteMemberFromServer(Member.getInstance().getEmail());

                    SharedPreferences.Editor editor = memberSession.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alert_dialog_builder.create();
        alert.show();
    }

    private void deleteMemberFromServer(final String email){
        final String URL = "http://52.78.157.250:3000/delete_member";

        Map<String, Object> delete_member_param = new HashMap<String, Object>();
        delete_member_param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(delete_member_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("fail")){
                                    Toast.makeText(getActivity(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else  {
                                Toast.makeText(getActivity(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("development", "Error: " + volleyError.getMessage());
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }
}
