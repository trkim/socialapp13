package com.soapp.project.sisas_android_chat.studyInRoom;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.soapp.project.sisas_android_chat.R;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyMakeShowMainActivity;
import com.soapp.project.sisas_android_chat.studyMakeShow.StudyShowActivity;

/**
 * Created by eelhea on 2016-12-14.
 */

public class MainChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton icBackIcon;
    String imgDecodableString;

    int room_id;
    int temp;
    String keyword;
    String date;

    TextView tv_study_keyword;

    EditText message_input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_in_room_main_chat);

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
                Intent intent = new Intent(MainChatActivity.this, StudyMakeShowMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
                finish();
            }
        });

        //ot chat에서 보내준 room_id, keyword, date
        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");
        temp = intent.getExtras().getInt("temp");
        keyword = intent.getExtras().getString("keyword");
        date = intent.getExtras().getString("date");

        tv_study_keyword = (TextView)findViewById(R.id.tv_study_keyword);
        tv_study_keyword.setText(keyword);

        Bundle bundle = new Bundle();
        bundle.putString("room_id",String.valueOf(room_id));
        bundle.putString("temp", String.valueOf(temp));
        bundle.putString("keyword", keyword);
        bundle.putString("date", date);
        Fragment fragment = new MainChatFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add( R.id.container, fragment );
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
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
            //MainChatFragment fragment = (MainChatFragment)getSupportFragmentManager().findFragmentById(R.id.chatArea);
            //fragment.sendImage(imgDecodableString);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StudyMakeShowMainActivity.class);
        startActivity(intent);
    }
}
