package com.soapp.project.sisas_android_chat.memberInfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.member.JoinActivity;
import com.soapp.project.sisas_android_chat.member.LoginActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeCreateActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeShowMainActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-11-28.
 */

public class MemberUpdateActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;

    LinearLayout update_layout;
    EditText et_member_name;
    EditText et_member_email;
    EditText et_member_pwd;
    EditText et_member_pwd_chk;
    TextView tv_pwd_chk;
    EditText et_member_major;
    String interests="";
    CheckBox cb_member_politics;
    CheckBox cb_member_economics;
    CheckBox cb_member_social;
    CheckBox cb_member_it;
    CheckBox cb_member_world;
    Button btn_update_go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_member);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        icBackIcon = (ImageButton)findViewById(R.id.icBackIcon);
        icBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpdateDialog();
            }
        });

        update_layout = (LinearLayout)findViewById(R.id.update_layout);
        update_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_member_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_member_email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_member_pwd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_member_pwd_chk.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_member_major.getWindowToken(), 0);
            }
        });

        et_member_name = (EditText)findViewById(R.id.et_member_name);
        et_member_email = (EditText)findViewById(R.id.et_member_email);
        et_member_pwd = (EditText)findViewById(R.id.et_member_pwd);
        et_member_pwd_chk = (EditText)findViewById(R.id.et_member_pwd_chk);
        tv_pwd_chk = (TextView)findViewById(R.id.tv_pwd_chk);
        et_member_major = (EditText)findViewById(R.id.et_member_major);

        cb_member_politics = (CheckBox)findViewById(R.id.cb_member_politics);
        cb_member_economics = (CheckBox)findViewById(R.id.cb_member_economics);
        cb_member_social = (CheckBox)findViewById(R.id.cb_member_social);
        cb_member_it = (CheckBox)findViewById(R.id.cb_member_it);
        cb_member_world = (CheckBox)findViewById(R.id.cb_member_world);

        et_member_name.setText(Member.getInstance().getName());
        et_member_name.setClickable(false);
        et_member_name.setEnabled(false);

        et_member_email.setText(Member.getInstance().getEmail());
        et_member_email.setClickable(false);
        et_member_email.setEnabled(false);

        et_member_major.setText(Member.getInstance().getMajor());

        String my_interest = Member.getInstance().getCategory();
        String[] interests_array = my_interest.split(",");
        if(my_interest.startsWith(",")) {
            for (int i = 0; i < interests_array.length; i++) {
                if (interests_array[i].equals("정치")) {
                    cb_member_politics.setChecked(true);
                } else if (interests_array[i].equals("경제")) {
                    cb_member_economics.setChecked(true);
                } else if (interests_array[i].equals("사회")) {
                    cb_member_social.setChecked(true);
                } else if (interests_array[i].equals("IT")) {
                    cb_member_it.setChecked(true);
                } else if (interests_array[i].equals("세계")) {
                    cb_member_world.setChecked(true);
                }
            }
        }else {
            if (my_interest.equals("politic")) { cb_member_politics.setChecked(true); }
            if (my_interest.equals("economics")) { cb_member_economics.setChecked(true); }
            if (my_interest.equals("social")) { cb_member_social.setChecked(true); }
            if (my_interest.equals("it")) { cb_member_it.setChecked(true); }
            if (my_interest.equals("world")) { cb_member_world.setChecked(true); }
        }

        et_member_pwd_chk.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = et_member_pwd.getText().toString();
                String pwd_chk = et_member_pwd_chk.getText().toString();

                if(pwd.equals(pwd_chk)){
                    tv_pwd_chk.setText("비밀번호가 일치합니다.");
                    tv_pwd_chk.setTextColor(Color.parseColor("#315556"));
                } else{
                    tv_pwd_chk.setText("비밀번호가 일치하지 않습니다.");
                    tv_pwd_chk.setTextColor(Color.parseColor("#EB9844"));
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        btn_update_go = (Button)findViewById(R.id.btn_update_go);
        btn_update_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_member_major.getText().toString().equals("")){
                    et_member_major.setText("");
                }
                if(cb_member_politics.isChecked()){
                    interests = interests+","+cb_member_politics.getText().toString();
                }
                if(cb_member_economics.isChecked()){
                    interests = interests+","+cb_member_economics.getText().toString();
                }
                if(cb_member_social.isChecked()){
                    interests = interests+","+cb_member_social.getText().toString();
                }
                if(cb_member_it.isChecked()){
                    interests = interests+","+cb_member_it.getText().toString();
                }
                if(cb_member_world.isChecked()){
                    interests = interests+","+cb_member_world.getText().toString();
                }

                try {
                    updateToServer(et_member_email.getText().toString(),
                            et_member_pwd.getText().toString(),
                            et_member_major.getText().toString(),
                            interests);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateToServer(final String email, final String password, final String major, final String category) throws Exception{
        final String URL = "http://52.78.157.250:3000/update_member";

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("email", email);
        param.put("password", password);
        param.put("major", major);
        param.put("category", category);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(MemberUpdateActivity.this, "회원정보가 수정되었습니다.다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();

                    Intent new_intent = new Intent(MemberUpdateActivity.this, LoginActivity.class);
                    startActivity(new_intent);
                    finish();
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

    public void cancelUpdateDialog(){
        LayoutInflater current_layout = LayoutInflater.from(MemberUpdateActivity.this);
        View dialog_view = current_layout.inflate(R.layout.cancel_update_member_dialog, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(MemberUpdateActivity.this);
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MemberUpdateActivity.this, MemberInfoActivity.class);
                intent.putExtra("fragment","profile");
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
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
}
