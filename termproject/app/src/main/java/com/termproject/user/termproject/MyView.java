package com.termproject.user.termproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyView extends View {
    private Paint mPaint;
    private Bitmap busMap;
    private Bitmap bus;
    private Bitmap bus2;
    private Bitmap refesh;
    private Bitmap sign;
    private Bitmap bg;
    private Bitmap nobus2;

    private double HMapInterval;
    private double WMapInterval;
    private int height;
    private int width;
    public static Dialog dialog;

    boolean a = true;
    public ArrayList<HashMap<String, String>> dataList;
    public GetData receiver;

    public int[] check = new int[18];  //check bus is on stastion
    public int[] checkDest = new int[18];
    public int[] checkFull = new int[18];
    public int[] time = new int[18];

    //  새로고침
    // 쓰레드가 서버에서 데이터를 가져올때까지 ui쓰레드는 기다려야됨
    public void onClick() {
        receiver.getData("http://gachonladybug.esy.es/getData.php");
        while (receiver.check <=0); //동기화       
        dataList = receiver.getList();
        for (int i = 0; i < 18; i++) {
            check[i] = 0;
            checkDest[i] = 0;
            checkFull[i] = 0;
        }
        invalidate();
    }
    
    public void init() {
        receiver = new GetData();
        receiver.getData("http://gachonladybug.esy.es/getData.php");
        for (int i = 0; i < 18; i++) {
            check[i] = 0;
            checkDest[i] = 0;
            checkFull[i] = 0;
        }
        mPaint = new Paint();
        Resources res = getResources();
        this.height = (int) (getClientHeight() * 0.9);
        this.width = getClientWidth();
        busMap = BitmapFactory.decodeResource(res, R.drawable.map);
        refesh = BitmapFactory.decodeResource(res, R.drawable.refresh);
        bus = BitmapFactory.decodeResource(res, R.drawable.bus2);
        bus2 = BitmapFactory.decodeResource(res, R.drawable.bus1);
        sign = BitmapFactory.decodeResource(res, R.drawable.sign);
        bg = BitmapFactory.decodeResource(res, R.drawable.back);

        nobus2 = BitmapFactory.decodeResource(res, R.drawable.nobus2);
        busMap = Bitmap.createScaledBitmap(busMap, (int) (width * 0.97), (int) (height * 0.85), false);
        bus = Bitmap.createScaledBitmap(bus, (int) (width * 0.05), (int) (height * 0.06), false);
        bus2 = Bitmap.createScaledBitmap(bus2, (int) (width * 0.05), (int) (height * 0.06), false);
        refesh = Bitmap.createScaledBitmap(refesh, (int) (width * 0.15), (int) (height * 0.1), false);
        sign = Bitmap.createScaledBitmap(sign, (int) (width * 0.3), (int) (height * 0.1), false);
        bg = Bitmap.createScaledBitmap(bg, (int) (width), (int) (height), false);
        nobus2 = Bitmap.createScaledBitmap(nobus2, (int) (width), (int) (height), false);
        //모든 해상도를 지원하기위해
        //화면전체를 세로는 200등분, 가로는 100등분하여 단위를 생성
        HMapInterval = (height * 0.005);
        WMapInterval = (width * 0.01);
        //정류장 시이마다 걸리는 시간을 미리 선언
        //정류장 대기시간 계산시에 사용됨
        time[1] = 90;
        time[2] = 90;
        time[3] = 60;
        time[4] = 60;
        time[5] = 60;
        time[6] = 0;
        time[7] = 60;
        time[8] = 0;
        time[9] = 60;
        time[10] = 0;
        time[11] = 90;
        time[12] = 60;
        time[13] = 30;
        time[14] = 0;
        time[15] = 30;
        time[16] = 0;

        while (receiver.check)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        dataList = receiver.getList();
    }

    public MyView(Context c) {
        super(c);
        init();
    }

    public MyView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    //각 정류장을 클릭했을 때 dialog를 띄우게된다.
    //예상 도착시간을 출력한다.
    public void showDialog(int index) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Waiting Time");
        TextView tv = (TextView) dialog.findViewById(R.id.text);
        int point = 99;
        int sum = 0;
        boolean checkException = false;

        if (index == 5|| index==7 || index==9 || index==11)
            checkException = true;
        // 가장 가까운 버스 찾기
        if (!checkException) {
            System.out.println(index+"@@@");
            for (int i = 16 + (index - 1); i >= index + 1; i--) {
                if (i % 16 == 0) {
                    if (check[16] == 1)
                        point = 16;
                } else if (check[i % 16] == 1)
                    point = i % 16;
                sum += time[i% 16];
                if(point!=99)
                    break;
            }
        } else {
            for (int i = 16 + (index - 1); i >= index + 1; i--) {
                if (i % 16 == 0) {
                    if (check[16] == 1 && checkDest[16] != 1)
                        point = 16;
                } else if (check[i % 16] == 1 && checkDest[i%16] != 1)
                    point = i % 16;
                sum += time[(i)% 16];
                System.out.println(check[i%16]+"@@"+checkDest[i%16]+"@@"+i%16+"@@"+point);
                if(point!=99)
                    break;
            }
        }
        String msg;
        sum /= 60;
        if (point == 99)
            msg = "도착예정인 버스가 없습니다.";
        else {
            if (checkFull[point] == 1)
                msg = "대기시간: " + sum + "분 / 자리 없음.";
            else
                msg = "대기시간: " + sum + "분 / 자리 있음.";
        }

        tv.setText(msg);
        tv.setTextColor(Color.WHITE);

        ImageView iv = (ImageView) dialog.findViewById(R.id.image);
        iv.setImageResource(R.drawable.timer);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public boolean onTouchEvent(MotionEvent event) {
        //새로고침 및 도착예정시간을 알기 위한 정류장 클릭에 대한 이벤트감지
        int currX = (int) event.getX();
        int currY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((currX < WMapInterval * 83 + (WMapInterval * 10)) && (currX > WMapInterval * 83 - (WMapInterval * 10)) && (currY < HMapInterval * 152 + (HMapInterval * 15)) && (currY > HMapInterval * 152 - (HMapInterval * 15))) {
                onClick();
            } else if (currX < WMapInterval * 69 + (WMapInterval * 5) && currX > WMapInterval * 69 - (WMapInterval * 5) && currY < HMapInterval * 128 + (HMapInterval * 10) && currY > HMapInterval * 128 - (HMapInterval * 10)) {
                showDialog(1);
            }  else if (currX < WMapInterval * 74 + (WMapInterval * 5) && currX > WMapInterval * 74 - (WMapInterval * 5) && currY < HMapInterval * 86 + (HMapInterval * 10) && currY > HMapInterval * 86 - (HMapInterval * 10)) {
                showDialog(3);
            }  else if (currX < WMapInterval * 69 + (WMapInterval * 5) && currX > WMapInterval * 69 - (WMapInterval * 5) && currY < HMapInterval * 44 + (HMapInterval * 10) && currY > HMapInterval * 44 - (HMapInterval * 10)) {
                showDialog(5);
            } else if (currX < WMapInterval * 48 + (WMapInterval * 5) && currX > WMapInterval * 48 - (WMapInterval * 5) && currY < HMapInterval * 17 + (HMapInterval * 10) && currY > HMapInterval * 17 - (HMapInterval * 10)) {
                showDialog(7);
            } else if (currX < WMapInterval * 28 + (WMapInterval * 5) && currX > WMapInterval * 28 - (WMapInterval * 5) && currY < HMapInterval * 45 + (HMapInterval * 10) && currY > HMapInterval * 45 - (HMapInterval * 10)) {
                showDialog(9);
            } else if (currX < WMapInterval * 23 + (WMapInterval * 5) && currX > WMapInterval * 23 - (WMapInterval * 5) && currY < HMapInterval * 78 + (HMapInterval * 10) && currY > HMapInterval * 78 - (HMapInterval * 10)) {
                showDialog(11);
            } else if (currX < WMapInterval * 28 + (WMapInterval * 5) && currX > WMapInterval * 28 - (WMapInterval * 5) && currY < HMapInterval * 128 + (HMapInterval * 10) && currY > HMapInterval * 128 - (HMapInterval * 10)) {
                showDialog(13);
            } else if (currX < WMapInterval * 48 + (WMapInterval * 5) && currX > WMapInterval * 48 - (WMapInterval * 5) && currY < HMapInterval * 153 + (HMapInterval * 10) && currY > HMapInterval * 153 - (HMapInterval * 10)) {
                showDialog(15);
            }
        }
        invalidate();
        return true;
    }

    public int getClientHeight() {
        return ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
    }

    public int getClientWidth() {
        return ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
    }

    // 서버로부터 가져온 데이터를 토대로 버스가 어디에 그려져야 하는지에 대한 조건문들
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bg, 0, 0, mPaint);
        canvas.drawBitmap(busMap, (int) (width * 0.05) / 2, (int) (height * 0.05) / 2, mPaint);
        canvas.drawBitmap(refesh, (int) (WMapInterval * 78), (int) (HMapInterval * 145), mPaint);
        canvas.drawBitmap(sign, (int) (width * 0.05) / 2, (int) (height * 0.05) / 2, mPaint);
        canvas.save(); // Save the current state
        if (dataList.size() == 0) { // 운행중인 버스정보가 없을 때
            canvas.drawBitmap(nobus2, 0, 0, mPaint);
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).get(receiver.TAG_CURRENT).equals("1"))//it대학
            {
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 69), (int) (HMapInterval * 125), mPaint);//it대학
                    checkDest[1] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 69), (int) (HMapInterval * 125), mPaint);//it대학
                check[1] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[1] = 1;
            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("2")) {
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 72), (int) (HMapInterval * 105), mPaint);//가천관
                    checkDest[2] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 72), (int) (HMapInterval * 105), mPaint);//가천관
                check[2] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[2] = 1;
            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("3")) {
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    //아름관행이 아름관에 도착
                    //회차대기에 표시시켜줌
                    canvas.drawBitmap(bus, (int) (WMapInterval * 49), (int) (HMapInterval * 83), mPaint);
                    checkDest[3] = 1;
                } else {
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 74), (int) (HMapInterval * 83), mPaint);//아름관1
                    check[3] = 1;
                }
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[3] = 1;
            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("4"))// 중도
            {
                if (dataList.get(i).get(receiver.TAG_LAST).equals("3"))   //전 정류장이 아름관이였으면
                {
                    if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                        canvas.drawBitmap(bus, (int) (WMapInterval * 73), (int) (HMapInterval * 62), mPaint);//아름-세종 사이
                        checkDest[4] = 1;
                    } else
                        canvas.drawBitmap(bus2, (int) (WMapInterval * 73), (int) (HMapInterval * 62), mPaint);//아름-세종 사이
                    check[4] = 1;
                    if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                        checkFull[4] = 1;
                } else if (dataList.get(i).get(receiver.TAG_LAST).equals("5"))   //전 정류장이 중도- 세종이였으면
                {
                    if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                        canvas.drawBitmap(bus, (int) (WMapInterval * 23), (int) (HMapInterval * 75), mPaint); //중도
                        checkDest[11] = 1;
                    } else
                        canvas.drawBitmap(bus2, (int) (WMapInterval * 23), (int) (HMapInterval * 75), mPaint); //중도
                    check[11] = 1;
                    if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                        checkFull[11] = 1;
                }

            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("5")) {   //중도 - 세종

                if (dataList.get(i).get(receiver.TAG_LAST).equals("6"))   //전 정류장이 세종이였으면
                {
                    if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                        canvas.drawBitmap(bus, (int) (WMapInterval * 25), (int) (HMapInterval * 59), mPaint);//세종-중도
                        checkDest[10] = 1;
                    } else
                        canvas.drawBitmap(bus2, (int) (WMapInterval * 25), (int) (HMapInterval * 59), mPaint);//세종-중도
                    check[10] = 1;
                    if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                        checkFull[10] = 1;
                } else if (dataList.get(i).get(receiver.TAG_LAST).equals("4"))   //전 정류장이 중도 였으면
                {
                    //noting
                }

            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("6")) //세종
            {

                if (dataList.get(i).get(receiver.TAG_LAST).equals("5"))   //전 정류장이 중도였으면
                {
                    if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                        canvas.drawBitmap(bus, (int) (WMapInterval * 69), (int) (HMapInterval * 41), mPaint); //세종관
                        checkDest[5] = 1;
                    } else
                        canvas.drawBitmap(bus2, (int) (WMapInterval * 69), (int) (HMapInterval * 41), mPaint); //세종관
                    check[5] = 1;
                    if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                        checkFull[5] = 1;
                } else if (dataList.get(i).get(receiver.TAG_LAST).equals("7"))   //전 정류장이 긱사 였으면
                {
                    if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                        canvas.drawBitmap(bus, (int) (WMapInterval * 28), (int) (HMapInterval * 42), mPaint); // 세종2
                        checkDest[9] = 1;
                    } else
                        canvas.drawBitmap(bus2, (int) (WMapInterval * 28), (int) (HMapInterval * 42), mPaint); // 세종2
                    check[9] = 1;
                    if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                        checkFull[9] = 1;
                }

            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("7")) {  //긱사

                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 48), (int) (HMapInterval * 14), mPaint); //긱사
                    checkDest[7] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 48), (int) (HMapInterval * 14), mPaint); //긱사
                check[7] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[7] = 1;

            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("8")) { //아름관- 창조
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 24), (int) (HMapInterval * 102), mPaint);//아름- 창조?
                    checkDest[12] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 24), (int) (HMapInterval * 102), mPaint);//아름- 창조?
                check[12] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[12] = 1;
            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("9")) { //창조
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 28), (int) (HMapInterval * 125), mPaint);//창조관
                    checkDest[13] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 28), (int) (HMapInterval * 125), mPaint);//창조관
                check[13] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[13] = 1;

            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("10")) {  //국제
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 48), (int) (HMapInterval * 150), mPaint);//글존
                    checkDest[15] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 48), (int) (HMapInterval * 150), mPaint);//글존
                check[15] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[15] = 1;
            } else if (dataList.get(i).get(receiver.TAG_CURRENT).equals("11")) { //회차
                if (dataList.get(i).get(receiver.TAG_DEST).equals("1")) {
                    canvas.drawBitmap(bus, (int) (WMapInterval * 49), (int) (HMapInterval * 82), mPaint);//회차대기
                    checkDest[17] = 1;
                } else
                    canvas.drawBitmap(bus2, (int) (WMapInterval * 49), (int) (HMapInterval * 82), mPaint);//회차대기
                check[17] = 1;
                if (dataList.get(i).get(receiver.TAG_SEAT).equals("0"))
                    checkFull[17] = 1;
            } else
                Log.i("test", "unexpected data");
        }
        canvas.restore(); // Restore the original state
    }
}
