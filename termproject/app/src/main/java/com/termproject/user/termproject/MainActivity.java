package com.termproject.user.termproject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 Program to provide location of a bus
 Author: Kim Young Song. Kim Ma Ro . Lee Won Sang
 E-mail Address: infall346@gmail.com / maro2345@gamil.com / WonSang12@gmail.com
 Android Term Project
 Last Changed: June 22, 2016
 */

public class MainActivity extends FragmentActivity {

    ViewPager pager; //ViewPager 참조변수
    ActionBar actionBar;  //ActionBar 참조변수
    public static boolean isConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }//onCreate Method...

    public void init() {
        isConnect = isNetWork();
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        pager = (ViewPager) findViewById(R.id.container);
        final CustomAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //Page가 일정부분 넘겨져서 현재 Page가 바뀌었을 때 호출
            //이전 Page의 80%가 이동했을때 다음 Page가 현재 Position으로 설정됨.
            //파라미터 : 현재 변경된 Page의 위치
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                //ViewPager는 3개의 View를 가지고 있도록 설계하였으므로.
                //Position도 역시 가장 왼쪽 처음부터(0,1,2 순으로 되어있음)
                //현재 전면에 놓여지는 ViewPager의 Page에 해당하는 Position으로
                //ActionBar의 Tab위치를 변경.
                actionBar.setSelectedNavigationItem(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        //ActionBar에 추가 될 Tab 참조변수
        ActionBar.Tab tab = null;

        //첫번째 Tab 객체 생성 및 ActionBar에 추가하기
        tab = actionBar.newTab(); //ActionBar에 붇는 Tab객체 생성
        tab.setText("Notice");    //Tab에 보여지는 글씨
        //Tab의 선택이 변경되는 것을 인지하는 TabListener 설정(아래쪽 객체 생성 코드 참고)
        tab.setTabListener(listener);
        //ActionBar에 Tab 추가
        actionBar.addTab(tab);

        //두번째 Tab 객체 생성 및 ActionBar에 추가하기
        tab = actionBar.newTab(); //ActionBar에 붇는 Tab객체 생성
        tab.setText("Route");     //Tab에 보여지는 글씨
        //Tab의 선택이 변경되는 것을 인지하는 TabListener 설정(아래쪽 객체 생성 코드 참고)
        tab.setTabListener(listener);
        //ActionBar에 Tab 추가
        actionBar.addTab(tab);

        //세번째 Tab 객체 생성 및 ActionBar에 추가하기
        tab = actionBar.newTab(); //ActionBar에 붇는 Tab객체 생성
        tab.setText("Timetable");   //Tab에 보여지는 글씨
        //Tab의 선택이 변경되는 것을 인지하는 TabListener 설정(아래쪽 객체 생성 코드 참고)
        tab.setTabListener(listener);
        //ActionBar에 Tab 추가
        actionBar.addTab(tab);

        actionBar.setSelectedNavigationItem(1);
        //action bar layout
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);         //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);      //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar));
    }

    public class CustomAdapter extends FragmentPagerAdapter {

        public CustomAdapter(FragmentManager fm) {
            super(fm);
        }

        //PagerAdapter가 가지고 잇는 View의 개수를 리턴
        //Tab에 따른 View를 보여줘야 하므로 Tab의 개수인 3을 리턴..
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3; //보여줄 View의 개수 리턴(Tab이 3개라서 3을 리턴)
        }

        //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
        //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
        //첫번째 파라미터 : ViewPager
        //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)
        @Override
        public Fragment getItem(int position) {
            View view = null;//현재 position에서 보여줘야할 View를 생성해서 리턴.
            switch (position) {
                case 0:
                    return Notice.newInstance();
                case 1:
                    return Route.newInstance();
                case 2:
                    return TimeTable.newInstance();
            }
            return null;
        }

        @Override
        public int getItemPosition(Object item) {
            //인터넷 연결상태가 변한다면, fragment를 바꾸기 위한 새로고침이 필요하다.
            //position_none이 리턴되면 새로고침을 실행.
            if (isConnect != isNetWork()) {
                isConnect = isNetWork();
                return POSITION_NONE;
            } else
                return POSITION_UNCHANGED;
        }

    }

    //ActionBar의 Tab 선택에 변화가 생기는 것을 인지하는 리스너(Listener)
    private ActionBar.TabListener listener = new ActionBar.TabListener() {

        //Tab의 선택이 벗어날 때 호출
        //첫번째 파라미터 : 선택에서 벗어나는 Tab 객체
        //두번째 파라미터 : Tab에 해당하는 View를 Fragment로 만들때 사용하는 트랜젝션.(여기서는 사용X)
        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }

        //Tab이 선택될 때 호출
        //첫번째 파라미터 : 선택된 Tab 객체
        //두번째 파라미터 : Tab에 해당하는 View를 Fragment로 만들때 사용하는 트랜젝션.(여기서는 사용X)
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

            //선택된 Tab객체의 위치값(왼족 처음부터 0,1,2....순으로 됨)
            int position = tab.getPosition();
            if (position != 1) {                 //terminate existed dialog
                if (MyView.dialog != null)
                    MyView.dialog.dismiss();
            }
            //Tab의 선택 위치에 따라 ViewPager에서 보여질 Item(View)를 설정
            //첫번째 파라미터: ViewPager가 현재 보여줄 View의 위치
            //두번째 파라미터: 변경할 때 부드럽게 이동하는가? false면 팍팍 바뀜
            pager.setCurrentItem(position, true);
        }

        //Tab이 재 선택될 때 호출
        //첫번째 파라미터 : 재 선택된 Tab 객체
        //두번째 파라미터 : Tab에 해당하는 View를 Fragment로 만들때 사용하는 트랜젝션.(여기서는 사용X)
        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
        }
    };

    private Boolean isNetWork() { // 인터넷 연결 여부를 확인함 wifi나 모바일 네트워크중 하나라도 연결되있으면 true, 아니면 false 리턴.
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null)
            return false;
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        if (lte_4g != null)
            blte_4g = lte_4g.isConnected();
        if (mobile != null) {
            if (mobile.isConnected() || wifi.isConnected() || blte_4g)
                return true;
        } else {
            if (wifi.isConnected() || blte_4g)
                return true;
        }
        return false;
    }
}