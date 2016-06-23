package com.termproject.user.termproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Notice extends Fragment {

    static WebView browser;

    public static Notice newInstance() {
        Notice fragment = new Notice();
        return fragment;
    }

    public Notice() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        // MainActivity에서 인터넷 연결유무를 파악하여 해당하는 layout을 화면에 표시
        if (!MainActivity.isConnect)
            rootView = inflater.inflate(R.layout.network_exception, container, false);
        else {
            //연결이 되어있다면 webview에 표시할 내용을 가져옴
            rootView = inflater.inflate(R.layout.layout_tab_0, container, false);

            browser = (WebView) rootView.findViewById(R.id.webkit);
            final ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.web_progress);
            browser.getSettings().setJavaScriptEnabled(true); // allow scripts
            browser.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, int errorcode,
                                            String description, String fallingUrl) {
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    //연결중을 표시하는 이미지
                    progress.setVisibility(View.VISIBLE);
                }

                public void onPageFinished(WebView view, String Url) {
                    //연결이 끝나면 표시헤제
                    progress.setVisibility(View.GONE);
                }
            });
            browser.loadUrl("http://gachonladybug.esy.es/bulletin_board/");

            browser.requestFocus();
            browser.setFocusable(true);
            browser.setFocusableInTouchMode(true);
            browser.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                            break;
                    }
                    return false;
                }
            });


            WebSettings webSettings = browser.getSettings();
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }
        return rootView;
    }

}