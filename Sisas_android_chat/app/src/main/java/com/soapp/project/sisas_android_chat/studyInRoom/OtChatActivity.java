package com.soapp.project.sisas_android_chat.studyInRoom;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soapp.project.sisas_android_chat.Member;
import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyMakeShow.DateDialog;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeShowMainActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyShowActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-11-29.
 */

public class OtChatActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Toolbar toolbar;
    ImageButton icBackIcon;

    EditText et_keyword;
    TextView tv_keyword_date;
    Button btn_ot_fix_keyword;
    Button btn_ot_scrap;
    Button btn_main_start;

    int room_id;
    String keyword_from_server="";
    String date_from_server="";
    String email = Member.getInstance().getEmail();
    long keyword_date;

    String study_start_date;
    String study_end_date;
    String dday;
    long start_date_millis;
    long end_date_millis;

    String imgDecodableString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_in_room_ot_chat);

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
                Intent intent = new Intent(OtChatActivity.this, StudyMakeShowMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        et_keyword = (EditText)findViewById(R.id.et_keyword);
        tv_keyword_date = (TextView)findViewById(R.id.tv_keyword_date);
        final DateDialog date_dialog = new DateDialog();
        tv_keyword_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_dialog.show(getFragmentManager(), "date_dialog");
            }
        });

        btn_ot_fix_keyword = (Button)findViewById(R.id.btn_ot_fix_keyword);
        btn_ot_scrap = (Button)findViewById(R.id.btn_ot_scrap);

        //해당 스터디 정보 가지고 오기
        try{
            getStudyInfoFromServer(room_id);
        }catch(Exception e){
            e.printStackTrace();
        }

        //FragmentGetMyStudy의 StudyListMyExpandableAdapter에서 보내준 정보 가져오기
        Intent intent = getIntent();
        Log.e("intent----------","ok");
        room_id = intent.getExtras().getInt("room_id");
        if(!intent.getExtras().getString("keyword").equals("") && !intent.getExtras().getString("date").equals("")) {
            keyword_from_server = intent.getExtras().getString("keyword");
            date_from_server = intent.getExtras().getString("date");

            et_keyword.setText(keyword_from_server);
            tv_keyword_date.setText(date_from_server);

            et_keyword.setClickable(false);
            et_keyword.setCursorVisible(false);
            et_keyword.setEnabled(false);
            et_keyword.setFocusable(false);
            tv_keyword_date.setClickable(false);
            tv_keyword_date.setEnabled(false);
            btn_ot_fix_keyword.setClickable(false);
            btn_ot_fix_keyword.setBackgroundColor(Color.parseColor("#C1C9C8"));
            btn_ot_scrap.setClickable(true);
            btn_ot_scrap.setBackgroundColor(Color.parseColor("#1f9e8e"));
            btn_ot_fix_keyword.setBackgroundColor(Color.GRAY);
        }

        if(btn_ot_fix_keyword.isClickable()){
            btn_ot_scrap.setClickable(false);
        } else {
            btn_ot_scrap.setClickable(true);
        }

        btn_ot_fix_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String et_keyword_content = et_keyword.getText().toString();
                    fixKeywordToServer(et_keyword_content, tv_keyword_date.getText().toString(), email, room_id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btn_ot_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //키워드로 크롤링
                    String et_keyword_content = et_keyword.getText().toString();
                    Intent intent = new Intent(OtChatActivity.this, ScrapWithKeywordActivity.class);
                    intent.putExtra("keyword", et_keyword_content);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("room_id",String.valueOf(room_id));
        Fragment fragment = new OtChatFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace( R.id.container, fragment );
        fragmentTransaction.commit();

        btn_main_start = (Button)findViewById(R.id.btn_main_start);
        btn_main_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");

                String study_start_date = tv_keyword_date.getText().toString();
                Calendar start_date_picked = Calendar.getInstance(time_zone);
                String[] study_start_date_split = study_start_date.split("-");
                int study_start_date_year = Integer.parseInt(study_start_date_split[0]);
                int study_start_date_month = Integer.parseInt(study_start_date_split[1]);
                int study_start_date_day = Integer.parseInt(study_start_date_split[2]);
                start_date_picked.set(study_start_date_year, study_start_date_month, study_start_date_day);
                long start = start_date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);

                Calendar today_calendar = Calendar.getInstance(time_zone);
                today_calendar.set(today_calendar.get(Calendar.YEAR), today_calendar.get(Calendar.MONTH) + 1, today_calendar.get(Calendar.DAY_OF_MONTH));
                long today = today_calendar.getTimeInMillis() / (24 * 60 * 60 * 1000);

                if(start==today){
                    Intent intent = new Intent(OtChatActivity.this, MainChatActivity.class);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("keyword", et_keyword.getText().toString());
                    intent.putExtra("date", study_start_date);
                    startActivity(intent);
                } else {
                    Toast.makeText(OtChatActivity.this, "아직 스터디 시작날이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fixKeywordToServer(final String keyword, final String date, final String email, final int room_id) throws Exception{
        final String URL = "http://52.78.157.250:3000/fix_keyword";

        Map<String, Object> keyword_param = new HashMap<String, Object>();
        keyword_param.put("keyword", keyword);
        keyword_param.put("email", email);
        keyword_param.put("date", date);
        keyword_param.put("room_id", room_id);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(keyword_param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.toString().contains("result")){
                                if(response.getString("result").equals("fail")){
                                    Toast toast = Toast.makeText(getApplicationContext(), "알 수 없는 에러가 발생합니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();
                                }else if(response.getString("result").equals("success")) {
                                    et_keyword.setClickable(false);
                                    et_keyword.setCursorVisible(false);
                                    et_keyword.setEnabled(false);
                                    et_keyword.setFocusable(false);
                                    tv_keyword_date.setClickable(false);
                                    tv_keyword_date.setEnabled(false);
                                    btn_ot_fix_keyword.setClickable(false);
                                    btn_ot_fix_keyword.setBackgroundColor(Color.parseColor("#C1C9C8"));
                                    btn_ot_scrap.setClickable(true);
                                    btn_ot_scrap.setBackgroundColor(Color.parseColor("#1f9e8e"));
                                    Toast toast = Toast.makeText(getApplicationContext(), "키워드가 등록되었습니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();
                                }else if(response.getString("result").equals("duplication")){
                                    Toast toast = Toast.makeText(getApplicationContext(), "이미 같은 키워드가 등록되었습니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.socket_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_attach:
                openGallery();
                return true;
            case R.id.action_capture:
                // openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openGallery()
    {
        Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryintent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            //Log.d("onActivityResult",imgDecodableString);
            OtChatFragment fragment = (OtChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatArea);
            fragment.sendImage(imgDecodableString);
        }
        try{
            getStudyInfoFromServer(room_id);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getStudyInfoFromServer(final int room_id){
        final String URL = "http://52.78.157.250:3000/get_room";

        Map<String, Object> room = new HashMap<String, Object>();
        room.put("room_id", room_id);
        room.put("email", email);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(room),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            study_start_date = response.getString("start_date");
                            study_end_date = response.getString("end_date");

                            tv_keyword_date.setText(study_start_date+" ~ "+study_end_date);

                            TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");
                            Calendar start_date_picked = Calendar.getInstance(time_zone);
                            String[] start_date_split = study_start_date.split("-");
                            int start_year_picked = Integer.parseInt(start_date_split[0]);
                            int start_month_picked = Integer.parseInt(start_date_split[1]);
                            int start_day_picked = Integer.parseInt(start_date_split[2]);
                            start_date_picked.set(start_year_picked, start_month_picked, start_day_picked);
                            start_date_millis = start_date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);

                            Calendar end_date_picked = Calendar.getInstance(time_zone);
                            String[] end_date_split = study_end_date.split("-");
                            int end_year_picked = Integer.parseInt(end_date_split[0]);
                            int end_month_picked = Integer.parseInt(end_date_split[1]);
                            int end_day_picked = Integer.parseInt(end_date_split[2]);
                            end_date_picked.set(end_year_picked, end_month_picked, end_day_picked);
                            end_date_millis = end_date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
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
        TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");

        keyword_date=0;

        if(view.getTag().equals("date_dialog") && view.isShown()) {
            Calendar date_picked = Calendar.getInstance(time_zone);
            year = year;
            monthOfYear = monthOfYear + 1;
            dayOfMonth = dayOfMonth;
            date_picked.set(year, monthOfYear, dayOfMonth);
            keyword_date = date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }

        int flag = 0;
        if(start_date_millis > keyword_date){
            Toast.makeText(getApplicationContext(), "스터디 날짜보다 이전 날짜를 선택하셨습니다." , Toast.LENGTH_SHORT).show();
            flag  = 1;
        }
        if(keyword_date > end_date_millis){
            Toast.makeText(getApplicationContext(), "스터디 끝 날짜 이후 날짜를 선택하셨습니다." , Toast.LENGTH_SHORT).show();
            flag = 1;
        }
        if(flag == 0) {
            tv_keyword_date.setText(new StringBuilder().append(year).append("-").append(monthOfYear).append("-").append(dayOfMonth));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StudyShowActivity.class);
        startActivity(intent);
    }
}
