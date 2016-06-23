package com.termproject.user.termproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimeTable extends Fragment {

    public static TimeTable newInstance() {
        TimeTable fragment = new TimeTable();
        return fragment;
    }

    public TimeTable() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_tab_2, container, false);
        return rootView;
    }
}
