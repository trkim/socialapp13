package com.soapp.project.sisas_android_chat.member;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeShowMainActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eelhea on 2016-10-14.
 */
public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;

    LinearLayout login_layout;
    EditText et_login_id, et_login_pwd;
    Button btn_login_go;
    Button btn_join;

    SharedPreferences memberSession;

    ImageButton btn_member_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        login_layout = (LinearLayout)findViewById(R.id.login_layout);
        login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_login_id.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_login_pwd.getWindowToken(), 0);
            }
        });

        btn_login_go = (Button)findViewById(R.id.btn_login_go);
        et_login_id = (EditText)findViewById(R.id.et_login_id);
        et_login_pwd = (EditText)findViewById(R.id.et_login_pwd);
        et_login_id.setLines(1);
        et_login_pwd.setLines(1);
        btn_join = (Button)findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent join_intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(join_intent);
                finish();
            }
        });

        memberSession = getSharedPreferences("MemberSession", Context.MODE_PRIVATE);

        if(memberSession.contains("email")){
            try {
                autoLoginToServer(memberSession.getString("email",""));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        //로그인
        btn_login_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_login_id.getText().toString();
                String pwd = et_login_pwd.getText().toString();
                if(id.isEmpty()){
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        loginToServer(id, pwd);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //로그인-서버 함수
    private void loginToServer(final String email, final String pwd) throws Exception {
        final String URL = "http://52.78.157.250:3000/login";

        Map<String, String> login_param = new HashMap<String, String>();
        login_param.put("email", email);
        login_param.put("password", pwd);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(login_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.toString().contains("result")) {
                        if (response.getString("result").equals("fail")) {
                            Toast.makeText(LoginActivity.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        } else if(response.getString("result").equals("fail_id")) {
                            Toast.makeText(LoginActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else if(response.getString("result").equals("fail_pwd")) {
                            Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Member.getInstance().setName(response.getString("name"));
                        Member.getInstance().setEmail(response.getString("email"));
                        Member.getInstance().setMajor(response.getString("major"));
                        Member.getInstance().setCategory(response.getString("category"));
                        Member.getInstance().setCoupon(response.getInt("coupon"));

                        getMyStudyListFromServer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
            }
        });
        volley.getInstance().addToRequestQueue(req);
    }

    private void autoLoginToServer(final String email) throws Exception{
        final String URL = "http://52.78.157.250:3000/get_member";

        Map<String, String> auto_login_param = new HashMap<String, String>();
        auto_login_param.put("email", email);
        Log.e("auto_login_param", auto_login_param.toString());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(auto_login_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast.makeText(LoginActivity.this, "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else  {
                        Member.getInstance().setName(response.getString("name"));
                        Member.getInstance().setEmail(response.getString("email"));
                        Member.getInstance().setMajor(response.getString("major"));
                        Member.getInstance().setCategory(response.getString("category"));
                        Member.getInstance().setCoupon(response.getInt("coupon"));

                        getMyStudyListFromServer();
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

    //개인 스터디 가져오는 함수
    private void getMyStudyListFromServer() throws Exception{
        SharedPreferences.Editor editor = memberSession.edit();
        editor.putString("email", Member.getInstance().getEmail());
        editor.apply();

        Intent study_make_show_intent = new Intent(LoginActivity.this, StudyMakeShowMainActivity.class);
        startActivity(study_make_show_intent);
        finish();
    }
}
