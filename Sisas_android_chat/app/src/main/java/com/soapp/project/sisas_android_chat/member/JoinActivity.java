package com.soapp.project.sisas_android_chat.member;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by eelhea on 2016-10-14.
 */
public class JoinActivity extends AppCompatActivity{
    LinearLayout join_layout;
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
    Button btn_join_go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setCustomActionBar();

        join_layout = (LinearLayout)findViewById(R.id.join_layout);
        join_layout.setOnClickListener(new View.OnClickListener() {
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
        et_member_pwd_chk.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = et_member_pwd.getText().toString();
                String pwd_chk = et_member_pwd_chk.getText().toString();

                if(pwd.equals(pwd_chk)){
                    tv_pwd_chk.setText("비밀번호가 일치합니다.");
                    tv_pwd_chk.setTextColor(Color.GREEN);
                } else{
                    tv_pwd_chk.setText("비밀번호가 일치하지 않습니다.");
                    tv_pwd_chk.setTextColor(Color.RED);
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        et_member_major = (EditText)findViewById(R.id.et_member_major);

        cb_member_politics = (CheckBox)findViewById(R.id.cb_member_politics);
        cb_member_economics = (CheckBox)findViewById(R.id.cb_member_economics);
        cb_member_social = (CheckBox)findViewById(R.id.cb_member_social);
        cb_member_it = (CheckBox)findViewById(R.id.cb_member_it);
        cb_member_world = (CheckBox)findViewById(R.id.cb_member_world);

        btn_join_go = (Button)findViewById(R.id.btn_join_go);
        btn_join_go.setOnClickListener(new View.OnClickListener() {
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
                validateJoin();
            }
        });
    }

    private void validateJoin(){
        if((et_member_name.getText().toString()).equals("")){
            Toast toast = Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }else if((!Patterns.EMAIL_ADDRESS.matcher(et_member_email.getText().toString()).matches()) || (et_member_email.getText().toString() == null)){
            Toast toast = Toast.makeText(JoinActivity.this, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        } else {
            try {
                joinToServer(et_member_name.getText().toString(),
                            et_member_email.getText().toString(),
                            et_member_pwd.getText().toString(),
                            et_member_major.getText().toString(),
                            interests);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void joinToServer(final String name, final String email, final String password, final String major, final String category) throws Exception{
        final String URL = "http://52.78.157.250:3000/insert_member";

        Map<String, Object> join_param = new HashMap<String, Object>();
        join_param.put("name", name);
        join_param.put("email", email);
        join_param.put("password", password);
        join_param.put("major", major);
        join_param.put("category", category);
        join_param.put("coupon", 0);
        join_param.put("rating", 0.0);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(join_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("already_used")){
                            Toast toast = Toast.makeText(JoinActivity.this, "이미 등록된 이메일 입니다.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0,0);
                            toast.show();
                        }else if(response.getString("result").equals("insert_success")) {
                            Toast.makeText(JoinActivity.this, "회원가입 완료! 로그인 해주세요.", Toast.LENGTH_SHORT).show();

                            Intent new_intent = new Intent(JoinActivity.this, LoginActivity.class);
                            startActivity(new_intent);
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

    private void setCustomActionBar(){
        ActionBar action_bar = getSupportActionBar();

        action_bar.setDisplayShowCustomEnabled(true);
        action_bar.setDisplayShowTitleEnabled(false);
        action_bar.setDisplayHomeAsUpEnabled(false);

        View custom_view = LayoutInflater.from(this).inflate(R.layout.action_bar,null);
        action_bar.setCustomView(custom_view);
    }
}
