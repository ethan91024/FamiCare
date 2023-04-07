package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Meditation extends AppCompatActivity {

    private Button count_time;

    private AnimationDrawable seaAnimation;
    CountThread t = null;
    int pic[] = {R.drawable.sea, R.drawable.sea2, R.drawable.sea3};
    MediaPlayer sea_sound ;
    private Button back;
    private EditText timer;
    private Button stop_counting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        count_time = findViewById(R.id.counter);
        Drawable drawable = getResources().getDrawable(pic[0]);
        drawable.setBounds(10, 10, 20, 10);

        ImageView sea_an = findViewById(R.id.Sea_animation); //首頁預設圖片
        sea_an.setBackgroundResource(R.drawable.sea);   //sea的動畫


        timer = findViewById(R.id.timer);
        stop_counting=findViewById(R.id.stop_counting);
//        sea_sound = MediaPlayer.create(this, R.raw.sea_sound);
        count_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sea_an.setBackgroundResource(R.drawable.sea_animation);
                seaAnimation = (AnimationDrawable) sea_an.getBackground();
                if (t == null || !t.isAlive()) {
                    t = new CountThread();
                    t.start();
//                    if (sea_sound == null) {
//                        sea_sound = MediaPlayer.create(Meditation.this, R.raw.sea_sound);
//                        System.out.println("create music");
//                    }
//                    seaAnimation.start();

                  startPlaying();

                }

            }
        });
        stop_counting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(t!=null) {
                    t.SetRunning(false);
                }

            }
        });




    }
    private void startPlaying() {
        try {
            if (sea_sound == null) {
                sea_sound = MediaPlayer.create(Meditation.this, R.raw.sea_sound);
            }
            sea_sound.start();

//            if(sea_sound.isPlaying()){
//                System.out.println("music_start");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CountThread extends Thread { //計時器

        boolean running = true;

        CountThread() {

        }

        public void run() {
            int time_cnt = 0;
            while (running) {
                if (++time_cnt > (60 * Double.parseDouble(String.valueOf(timer.getText())))) {
                    seaAnimation.stop();
                    break;
                }
//                System.out.println(Double.parseDouble(String.valueOf(timer.getText())));
                System.out.println(time_cnt);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        public void SetRunning(boolean run) {
            running = run;
            seaAnimation.stop();
            if (sea_sound != null) {
                sea_sound.stop();
//                if(!sea_sound.isPlaying()){
//                    System.out.println("music_stop");
//                }
                sea_sound.release();
                sea_sound = null;

            }
        }


    }

    @Override
    public void onResume() {
        Log.e(TAG, "冥想---onResume");
        super.onResume();
    }

    public void onPause() {
        if (t != null) {
            t.SetRunning(false);
        }
        Log.e(TAG, "冥想---onPause");
        super.onPause();
    }

}