package com.soapp.project.sisas_android_chat.memberInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soapp.project.sisas_android_chat.R;

/**
 * Created by eelhea on 2016-10-14.
 */
public class ScrapboxFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_info_scrapbox_frag, container, false);



        return view;
    }
}
