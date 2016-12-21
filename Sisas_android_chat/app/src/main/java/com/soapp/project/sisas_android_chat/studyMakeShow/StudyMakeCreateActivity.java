package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-10-21.
 */
public class StudyMakeCreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Toolbar toolbar;
    ImageButton icBackIcon;

    LinearLayout create_layout;

    EditText et_study_name;
    EditText et_study_capacity;
    RadioGroup rg_category_group;
    int selected_category=0;
    RadioButton rb_selected_category;
    TextView tv_study_start_date_picked;
    TextView tv_study_end_date_picked;
    EditText et_study_comment;
    Button btn_study_create_go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_make_create);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "210_appgullimB.ttf");
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setTypeface(typeface);

        icBackIcon = (ImageButton)findViewById(R.id.icBackIcon);
        icBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelStudyDialog();
            }
        });

        et_study_name = (EditText)findViewById(R.id.et_study_name);
        et_study_capacity = (EditText)findViewById(R.id.et_study_capacity);
        rg_category_group = (RadioGroup)findViewById(R.id.rg_category_group);

        tv_study_start_date_picked = (TextView) findViewById(R.id.tv_study_start_date_picked);
        tv_study_end_date_picked = (TextView) findViewById(R.id.tv_study_end_date_picked);

        final DateDialog date_dialog = new DateDialog();

        tv_study_start_date_picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_dialog.show(getFragmentManager(), "start_date_dialog");
            }
        });

        tv_study_end_date_picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_dialog.show(getFragmentManager(), "end_date_dialog");
            }
        });

        et_study_comment = (EditText)findViewById(R.id.et_study_comment);

        btn_study_create_go = (Button)findViewById(R.id.btn_study_create_go);
        btn_study_create_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_category = rg_category_group.getCheckedRadioButtonId();
                rb_selected_category = (RadioButton)findViewById(selected_category);

                createRoomToServer(et_study_name.getText().toString(),
                        Integer.parseInt(et_study_capacity.getText().toString()),
                        rb_selected_category.getText().toString(),
                        tv_study_start_date_picked.getText().toString(),
                        tv_study_end_date_picked.getText().toString(),
                        et_study_comment.getText().toString());
            }
        });

        create_layout = (LinearLayout) findViewById(R.id.create_layout);
        create_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_study_name.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_study_capacity.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(rg_category_group.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(tv_study_start_date_picked.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(tv_study_end_date_picked.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(et_study_comment.getWindowToken(), 0);
            }
        });
    }

    public void cancelStudyDialog(){
        LayoutInflater current_layout = LayoutInflater.from(StudyMakeCreateActivity.this);
        View dialog_view = current_layout.inflate(R.layout.study_make_cancel_dialog, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(StudyMakeCreateActivity.this);
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(StudyMakeCreateActivity.this, StudyMakeShowMainActivity.class);
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar today_calendar = Calendar.getInstance(time_zone);
        today_calendar.set(today_calendar.get(Calendar.YEAR), today_calendar.get(Calendar.MONTH) + 1, today_calendar.get(Calendar.DAY_OF_MONTH));
        long today = today_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);

        int start_year_picked=today_calendar.get(Calendar.YEAR);
        int start_month_picked=today_calendar.get(Calendar.MONTH) + 1;
        int start_day_picked=today_calendar.get(Calendar.DAY_OF_MONTH);
        long start=today;
        int end_year_picked=3000;
        int end_month_picked=13;
        int end_day_picked=32;
        long end=today*2;

        if(!tv_study_start_date_picked.getText().toString().equals("")){
            Calendar start_picked = Calendar.getInstance(time_zone);
            String temp_start = tv_study_start_date_picked.getText().toString();
            String[] start_split = temp_start.split("-");
            start_year_picked = Integer.parseInt(start_split[0]);
            start_month_picked = Integer.parseInt(start_split[1]);
            start_day_picked = Integer.parseInt(start_split[2]);
            start_picked.set(start_year_picked, start_month_picked, start_day_picked);
            start = start_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }
        if(!tv_study_end_date_picked.getText().toString().equals("")){
            Calendar end_picked = Calendar.getInstance(time_zone);
            String temp_end = tv_study_end_date_picked.getText().toString();
            String[] end_split = temp_end.split("-");
            end_year_picked = Integer.parseInt(end_split[0]);
            end_month_picked = Integer.parseInt(end_split[1]);
            end_day_picked = Integer.parseInt(end_split[2]);
            end_picked.set(end_year_picked, end_month_picked, end_day_picked);
            end = end_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }

        if(view.getTag().equals("start_date_dialog") && view.isShown()) {
            Calendar start_picked = Calendar.getInstance(time_zone);
            start_year_picked = year;
            start_month_picked = monthOfYear + 1;
            start_day_picked = dayOfMonth;
            start_picked.set(start_year_picked, start_month_picked, start_day_picked);
            start = start_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        } else if(view.getTag().equals("end_date_dialog")){
            Calendar end_picked = Calendar.getInstance(time_zone);
            end_year_picked = year;
            end_month_picked = monthOfYear + 1;
            end_day_picked = dayOfMonth;
            end_picked.set(end_year_picked, end_month_picked, end_day_picked);
            end = end_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }

        int flag = 0;
        if(start < today){
            Toast.makeText(getApplicationContext(), "지난 날짜를 선택하셨습니다." , Toast.LENGTH_SHORT).show();
            flag  = 1;
        }
        if(end < start){
            Toast.makeText(getApplicationContext(), "시작 날짜보다 이전 날짜를 선택하셨습니다." , Toast.LENGTH_SHORT).show();
            flag = 1;
        }
        if(flag == 0) {
            if (view.getTag().equals("start_date_dialog")) {
                tv_study_start_date_picked.setText(new StringBuilder().append(start_year_picked).append("-").append(start_month_picked).append("-").append(start_day_picked));
            } else if (view.getTag().equals("end_date_dialog")) {
                tv_study_end_date_picked.setText(new StringBuilder().append(end_year_picked).append("-").append(end_month_picked).append("-").append(end_day_picked));
            }
        }
    }

    private void createRoomToServer(final String name, final int capacity, final String category, final String start_date, final String end_date, final String comment){
        final String URL = "http://52.78.157.250:3000/insert_room";

        Map<String, Object> create_room_param = new HashMap<String, Object>();
        create_room_param.put("email", Member.getInstance().getEmail());
        create_room_param.put("king_name", Member.getInstance().getName());
        create_room_param.put("room_name", name);
        create_room_param.put("capacity", capacity);
        create_room_param.put("category", category);
        create_room_param.put("start_date", start_date);
        create_room_param.put("end_date", end_date);
        create_room_param.put("comment", comment);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(create_room_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.toString().contains("result")){
                        if(response.getString("result").equals("fail")){
                            Toast.makeText(getApplicationContext(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                        } else if(response.getString("result").equals("success")){
                            Toast.makeText(getApplicationContext(), "스터디 등록완료", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), StudyMakeShowMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }catch (Exception e){
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
