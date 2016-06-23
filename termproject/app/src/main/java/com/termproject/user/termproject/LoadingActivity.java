package com.termproject.user.termproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LoadingActivity extends Activity {
    AnimationDrawable mframeAnimation;

    public class myThread extends  Thread{
        @Override
        public void run() {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startAnimation();
        Thread th = new myThread();
        th.start();

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAnimation();
                finish();
            }
        }, 3000);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void startAnimation() {
        ImageView img = (ImageView) findViewById(R.id.splash);

        BitmapDrawable frame1 = (BitmapDrawable) getResources().getDrawable(R.drawable.back1);
        BitmapDrawable frame2 = (BitmapDrawable) getResources().getDrawable(R.drawable.back2);
        BitmapDrawable frame3 = (BitmapDrawable) getResources().getDrawable(R.drawable.back3);

// Get the background, which has been compiled to an AnimationDrawable object.
        int reasonableDuration = 200;
        mframeAnimation = new AnimationDrawable();
        mframeAnimation.setOneShot(false); // loop continuously
        mframeAnimation.addFrame(frame1, reasonableDuration);
        mframeAnimation.addFrame(frame2, reasonableDuration);
        mframeAnimation.addFrame(frame3, reasonableDuration);

        img.setBackground(mframeAnimation);
        mframeAnimation.setVisible(true, true);
        mframeAnimation.start();
    }

    public void stopAnimation() {
        mframeAnimation.stop();
        mframeAnimation.setVisible(false, false);
    }
}

