package com.termproject.user.termproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Route extends Fragment {

    public static Route newInstance() {
        Route fragment = new Route();
        return fragment;
    }

    public Route() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        // MainActivity에서 인터넷 연결유무를 파악하여 해당하는 layout을 화면에 표시
        if (!MainActivity.isConnect)
            rootView = inflater.inflate(R.layout.network_exception, container, false);
        else
            rootView = inflater.inflate(R.layout.layout_tab_1, container, false);
        return rootView;

    }
}
