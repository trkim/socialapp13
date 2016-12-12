package com.soapp.project.sisas_android_chat.studyInRoom;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyShowActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyShowApplyActivity;
import com.soapp.project.sisas_android_chat.volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by eelhea on 2016-11-29.
 */

public class OtActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    EditText et_keyword;
    TextView tv_keyword_date;
    Button btn_ot_fix_keyword;
    Button btn_ot_scrap;
    Button btn_ot_start;

    int room_id;
    String email = Member.getInstance().getEmail();
    long keyword_date;

    String dday;
    long start_date_millis;
    long end_date_millis;

    String imgDecodableString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.study_in_room_ot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        btn_ot_start = (Button)findViewById(R.id.btn_ot_start);

        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");

        btn_ot_fix_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String et_keyword_content = et_keyword.getText().toString();
                    Log.e("et_keyword",et_keyword_content);
                    fixKeywordToServer(et_keyword_content, String.valueOf(keyword_date), email, room_id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btn_ot_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String et_keyword_content = et_keyword.getText().toString();

                    ///*키워드, 카테고리,
                    ///*scrapToServer(et_keyword_content)
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("room_id",String.valueOf(room_id));
        Log.e("activity room_id", String.valueOf(room_id));
        Fragment fragment = new OtChatFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace( R.id.container, fragment );
        fragmentTransaction.commit();
    }

    private void fixKeywordToServer(final String keyword, final String date, final String email, final int room_id) throws Exception{
        final String URL = "http://52.78.157.250:3000/fix_keyword";

        Map<String, Object> keyword_param = new HashMap<String, Object>();
        keyword_param.put("keyword", keyword);
        keyword_param.put("email", email);
        keyword_param.put("date", date);
        keyword_param.put("room_id", room_id);
        Log.e("keyword : ",keyword);
        Log.e("email : ",email);
        Log.e("date : ",date);
        Log.e("room_id : ",String.valueOf(room_id));

        ///*키워드 확정하고 넘어가는 페이지 고쳐야함
        ///*date 날짜 형태로 넘어가게 고쳐야함
        ///*확정 버튼 누르면 disable되게 해야함
        ///*스터디 시작 날짜가 지나기 전까지 키워드창에 키워드 유지되야함
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
                                    Toast toast = Toast.makeText(getApplicationContext(), "키워드가 등록되었습니다.", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0,0);
                                    toast.show();

                                    Intent new_intent = new Intent(getApplicationContext(), OtActivity.class);
                                    startActivity(new_intent);
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
        Log.d("onCreateOptionsMenu", "create menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.socket_activity_actions, menu);
        getStudyInfoFromServer(room_id);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_attach:
                Log.d("onOptionsItemSelected","action_attach");
                openGallery();
                return true;
            case R.id.action_capture:
                Log.d("onOptionsItemSelected","action_capture");
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
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
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
                            String start_date = response.getString("start_date");
                            String end_date = response.getString("end_date");

                            Log.e("start_date : ",start_date);
                            Log.e("end_date : ",end_date);

                            TimeZone time_zone = TimeZone.getTimeZone("Asia/Seoul");
                            Calendar start_date_picked = Calendar.getInstance(time_zone);
                            String[] start_date_split = start_date.split("-");
                            int start_year_picked = Integer.parseInt(start_date_split[0]);
                            int start_month_picked = Integer.parseInt(start_date_split[1]);
                            int start_day_picked = Integer.parseInt(start_date_split[2]);
                            start_date_picked.set(start_year_picked, start_month_picked, start_day_picked);
                            start_date_millis = start_date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);

                            Calendar end_date_picked = Calendar.getInstance(time_zone);
                            String[] end_date_split = end_date.split("-");
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

        int date_year_picked =0;
        int date_month_picked=0;
        int date_day_picked=0;
        keyword_date=0;
        if(!tv_keyword_date.getText().toString().equals("")){
            Calendar date_picked = Calendar.getInstance(time_zone);
            String temp_start = tv_keyword_date.getText().toString();
            String[] date_split = temp_start.split("-");
            date_year_picked = Integer.parseInt(date_split[0]);
            date_month_picked = Integer.parseInt(date_split[1]);
            date_day_picked = Integer.parseInt(date_split[2]);
            date_picked.set(date_year_picked, date_month_picked, date_day_picked);
            Log.e("선택한 날짜 : ",String.valueOf(date_year_picked));
            Log.e("선택한 날짜 : ",String.valueOf(date_month_picked));
            Log.e("선택한 날짜 : ",String.valueOf(date_day_picked));
            keyword_date = date_picked.getTimeInMillis() / (24 * 60 * 60 * 1000);
        }

        if(view.getTag().equals("date_dialog")) {
            Calendar date_picked = Calendar.getInstance(time_zone);
            date_year_picked = year;
            date_month_picked = monthOfYear + 1;
            date_day_picked = dayOfMonth;
            date_picked.set(date_year_picked, date_month_picked, date_day_picked);
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
            tv_keyword_date.setText(new StringBuilder().append(date_year_picked).append("-").append(date_month_picked).append("-").append(date_day_picked));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StudyShowActivity.class);
        startActivity(intent);
    }
}