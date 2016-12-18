package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by eelhea on 2016-11-02.
 */
public class StudyMakeUpdateDeleteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Toolbar toolbar;
    ImageButton icBackIcon;

    int room_id;
    EditText et_study_name;
    EditText et_study_capacity;
    RadioGroup rg_category_group;
    RadioButton rb_study_politics;
    RadioButton rb_study_economics;
    RadioButton rb_study_social;
    RadioButton rb_study_it;
    RadioButton rb_study_world;
    RadioButton rb_selected_radio_button;
    int selected_radio_button=0;
    TextView tv_study_start_date_picked;
    TextView tv_study_end_date_picked;
    EditText et_study_comment;

    Button btn_study_update_go;
    Button btn_study_delete_go;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_make_update_delete);

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
                cancelStudyUpdateDeleteDialog();
            }
        });

        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");

        et_study_name = (EditText)findViewById(R.id.et_study_name);
        et_study_capacity = (EditText)findViewById(R.id.et_study_capacity);
        rg_category_group = (RadioGroup)findViewById(R.id.rg_category_group);

        rb_study_politics = (RadioButton)findViewById(R.id.rb_study_politics);
        rb_study_economics = (RadioButton)findViewById(R.id.rb_study_economics);
        rb_study_social = (RadioButton)findViewById(R.id.rb_study_social);
        rb_study_it = (RadioButton)findViewById(R.id.rb_study_it);
        rb_study_world = (RadioButton)findViewById(R.id.rb_study_world);

        tv_study_start_date_picked = (TextView) findViewById(R.id.tv_study_start_date_picked);
        tv_study_end_date_picked = (TextView) findViewById(R.id.tv_study_end_date_picked);

        et_study_comment = (EditText)findViewById(R.id.et_study_comment);

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

        btn_study_update_go = (Button)findViewById(R.id.btn_study_update_go);
        btn_study_delete_go = (Button)findViewById(R.id.btn_study_delete_go);



        btn_study_delete_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        try {
            getStudyDetailFromServer(room_id, Member.getInstance().getEmail());
        }catch(Exception e){
            e.printStackTrace();
        }

        btn_study_update_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    RadioButton rb_selected_radio_button = (RadioButton)findViewById(rg_category_group.getCheckedRadioButtonId());

                    updateStudyDetailToServer(room_id,
                            Member.getInstance().getEmail(),
                            Member.getInstance().getName(),
                            et_study_name.getText().toString(),
                            et_study_capacity.getText().toString(),
                            rb_selected_radio_button.getText().toString(),
                            tv_study_start_date_picked.getText().toString(),
                            tv_study_end_date_picked.getText().toString(),
                            et_study_comment.getText().toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void getStudyDetailFromServer(final int room_id, final String email) throws Exception{
        final String URL = "http://52.78.157.250:3000/get_room";

        Map<String, Object> room_detail_param = new HashMap<String, Object>();
        room_detail_param.put("room_id", room_id);
        room_detail_param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL,
                new JSONObject(room_detail_param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String room_name = response.getString("room_name");
                    String capacity = response.getString("capacity");
                    String category = response.getString("category");
                    String start_date = response.getString("start_date");
                    String end_date = response.getString("end_date");
                    String comment = response.getString("comment");

                    et_study_name.setText(room_name);
                    et_study_capacity.setText(capacity);

                    if(category.equals(rb_study_politics.getText())){
                        rb_study_politics.setChecked(true);
                        rb_study_politics.setSelected(true);
                    } else if(category.equals(rb_study_economics.getText())){
                        rb_study_economics.setChecked(true);
                        rb_study_economics.setSelected(true);
                    } else if(category.equals(rb_study_social.getText())){
                        rb_study_social.setChecked(true);
                        rb_study_social.setSelected(true);
                    } else if(category.equals(rb_study_it.getText())){
                        rb_study_it.setChecked(true);
                        rb_study_it.setSelected(true);
                    } else if(category.equals(rb_study_world.getText())){
                        rb_study_world.setChecked(true);
                        rb_study_world.setSelected(true);
                    }

                    tv_study_start_date_picked.setText(start_date);
                    tv_study_end_date_picked.setText(end_date);
                    et_study_comment.setText(comment);
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

    public void updateStudyDetailToServer(final int room_id, final String email, final String king_name, final String room_name,
                                           final String capacity, final String category, final String start_date,
                                           final String end_date, final String comment){
        final String URL = "http://52.78.157.250:3000/update_room";

        Map<String, Object> update_room_param = new HashMap<String, Object>();
        update_room_param.put("room_id", room_id);
        update_room_param.put("email", email);
        update_room_param.put("king_name", king_name);
        update_room_param.put("room_name", room_name);
        update_room_param.put("capacity", capacity);
        update_room_param.put("category", category);
        update_room_param.put("start_date", start_date);
        update_room_param.put("end_date", end_date);
        update_room_param.put("comment", comment);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(update_room_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("room_update_success")){
                                    Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), StudyMakeShowMainActivity.class);
                                    startActivity(intent);
                                }else if(response.getString("result").equals("fail")){
                                    Toast.makeText(getApplicationContext(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                                }
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear+1;
        if(view.getTag().equals("start_date_dialog")){
            tv_study_start_date_picked.setText(new StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(dayOfMonth));
        } else if(view.getTag().equals("end_date_dialog")){
            tv_study_end_date_picked.setText(new StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(dayOfMonth));
        }
    }

    protected void showDeleteDialog(){
        LayoutInflater current_layout = LayoutInflater.from(StudyMakeUpdateDeleteActivity.this);
        View dialog_view = current_layout.inflate(R.layout.study_make_delete_dialog, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(StudyMakeUpdateDeleteActivity.this);
        alert_dialog_builder.setView(dialog_view);

        final EditText et_study_delete_dialog = (EditText)dialog_view.findViewById(R.id.et_study_delete_dialog);
        alert_dialog_builder.setCancelable(false).setPositiveButton("보내기", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    deleteStudyToServer(room_id, Member.getInstance().getEmail());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alert_dialog_builder.create();
        alert.show();
    }

    private void deleteStudyToServer(final int room_id, final String email) throws Exception{
        final String URL = "http://52.78.157.250:3000/delete_room";

        Map<String, Object> delete_room_param = new HashMap<String, Object>();
        delete_room_param.put("room_id", room_id);
        delete_room_param.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(delete_room_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("fail")){
                                    Toast.makeText(getApplicationContext(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else  {
                                Toast.makeText(getApplicationContext(), "스터디원에게 메세지를 보내고 삭제하였습니다", Toast.LENGTH_SHORT).show();
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

    public void cancelStudyUpdateDeleteDialog(){
        LayoutInflater current_layout = LayoutInflater.from(StudyMakeUpdateDeleteActivity.this);
        View dialog_view = current_layout.inflate(R.layout.study_up_del_cancel_dialog, null);
        final AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(StudyMakeUpdateDeleteActivity.this);
        alert_dialog_builder.setView(dialog_view);

        alert_dialog_builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(StudyMakeUpdateDeleteActivity.this, StudyMakeShowMainActivity.class);
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
