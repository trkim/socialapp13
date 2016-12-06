package com.soapp.project.sisas_android_chat.studyMakeShow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.soapp.project.sisas_android_chat.R;

/**
 * Created by eelhea on 2016-10-21.
 */
public class StudyShowActivity extends AppCompatActivity {

    int button_state=0;
    Button btn_study_apply;
    Button btn_study_watch;

    int selected_category=0;
    RadioGroup rg_study_category;
    RadioButton rb_selected_category;

    int mCurrentFragmentIndex = 0;
    public final static int FRAGMENT_APPLY = 0;
    public final static int FRAGMENT_WATCH = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_show);
        setCustomActionBar();

        btn_study_apply = (Button)findViewById(R.id.btn_study_apply);
        btn_study_watch = (Button)findViewById(R.id.btn_study_watch);
        btn_study_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_state=0;
                btn_study_apply.setBackgroundColor(Color.YELLOW);
                btn_study_watch.setBackgroundColor(Color.WHITE);
                mCurrentFragmentIndex = FRAGMENT_APPLY;
                changeFragment(mCurrentFragmentIndex);
            }
        });
        btn_study_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_state=1;
                btn_study_watch.setBackgroundColor(Color.YELLOW);
                btn_study_apply.setBackgroundColor(Color.WHITE);
                mCurrentFragmentIndex = FRAGMENT_WATCH;
                changeFragment(mCurrentFragmentIndex);
            }
        });

        rg_study_category = (RadioGroup)findViewById(R.id.rg_study_category);
        selected_category = rg_study_category.getCheckedRadioButtonId();
        rb_selected_category = (RadioButton)findViewById(selected_category);

        rg_study_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_study_all:
                    case R.id.rb_study_politics:
                    case R.id.rb_study_economics:
                    case R.id.rb_study_social:
                    case R.id.rb_study_it:
                    case R.id.rb_study_world:
                        selected_category = checkedId;
                        rb_selected_category = (RadioButton)findViewById(selected_category);
                        break;
                }

                if (button_state==0){
                    mCurrentFragmentIndex = FRAGMENT_APPLY;
                    changeFragment(mCurrentFragmentIndex);
                } else if (button_state==1) {
                    mCurrentFragmentIndex = FRAGMENT_WATCH;
                    changeFragment(mCurrentFragmentIndex);
                }
            }
        });

        btn_study_apply.performClick();
    }

    public void changeFragment(int mCurrentFragmentIndex){
        Fragment fragment;

        switch(mCurrentFragmentIndex){
            default:
            case FRAGMENT_APPLY:
                fragment = new StudyShowApplyFragment();
                break;
            case FRAGMENT_WATCH:
                fragment = new StudyShowWatchFragment();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("category",rb_selected_category.getText().toString());
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace( R.id.fl_study_list, fragment );
        fragmentTransaction.commit();
    }

    private void setCustomActionBar(){
        ActionBar action_bar = getSupportActionBar();

        action_bar.setDisplayShowCustomEnabled(true);
        action_bar.setDisplayShowTitleEnabled(false);
        action_bar.setDisplayHomeAsUpEnabled(false);

        View custom_view = LayoutInflater.from(this).inflate(R.layout.action_bar,null);
        action_bar.setCustomView(custom_view);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), StudyMakeShowMainActivity.class);
        startActivity(intent);
    }
}
