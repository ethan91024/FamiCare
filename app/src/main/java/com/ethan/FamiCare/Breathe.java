package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Breathe extends AppCompatActivity {


    private Button breathe_start;
    CountThread_Breathe t;
    private EditText timer;
    private Button cancel;
    VideoView videoView;
    MediaPlayer breathe_sound = null;
    AnimationDrawable animationDrawable;
    View gift_ani;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);
        timer = findViewById(R.id.breathe_timer);
        cancel = findViewById(R.id.breathe_cancel);
        breathe_start = findViewById(R.id.breathe_start);
        videoView = findViewById(R.id.breath_videoview);
        videoView.setBackgroundResource(R.drawable.breathe_ain_img);
        MediaController mediaController = new MediaController(Breathe.this);
        mediaController.setAnchorView(videoView);
        mediaController.setVisibility(View.INVISIBLE);
        gift_ani = findViewById(R.id.gift_ani);
        gift_ani.setBackgroundResource(R.drawable.gift_animation);
        animationDrawable = (AnimationDrawable) gift_ani.getBackground();
        animationDrawable.setOneShot(true);


        breathe_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Integer.parseInt(String.valueOf(timer.getText())) <= 30 && Integer.parseInt(String.valueOf(timer.getText())) > 0) {
                        if (t == null || !t.isAlive()) {
                            videoView.setBackground(null);
                            t = new  CountThread_Breathe (timer);
                            timer.setEnabled(false);
                            t.start();

                            startPlaying();
                            //動畫
                            MediaController mediaController = new MediaController(Breathe.this);
                            mediaController.setAnchorView(videoView);
                            mediaController.setVisibility(View.INVISIBLE);
                            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.breatheanimation);
                            videoView.setVideoURI(uri);
                            videoView.setMediaController(mediaController);
                            videoView.start();

                        }
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                videoView.start();

                            }
                        });
                    }

                } catch (Exception e) {
                    Toast.makeText(Breathe.this, "時間輸入錯誤，只限整數", Toast.LENGTH_SHORT).show();

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t != null) {
                    t.SetRunning(false);
                }
                timer.setEnabled(true);
            }
        });

        //播放暫停增加圖案
        Drawable startD = getResources().getDrawable(R.drawable.button_star_red);
        startD.setBounds(-20, 0, 90, 100);
        breathe_start.setCompoundDrawables(null, null, startD, null);
        Drawable stopD = getResources().getDrawable(R.drawable.button_stop_red);
        stopD.setBounds(-40, 0, 70, 90);
        cancel.setCompoundDrawables(null, null, stopD, null);


    }

    private void startPlaying() {
        System.out.println("創音樂");
        try {
            if (breathe_sound == null) {
                breathe_sound = MediaPlayer.create(Breathe.this, R.raw.breathe_sound);
            }
            breathe_sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start(); //當播放完成時，重複撥放
                }
            });
            breathe_sound.start();

            if (breathe_sound.isPlaying()) {
                System.out.println("music_start");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CountThread_Breathe extends Thread { //計時器
        boolean running = true;

        CountThread_Breathe() {

        }
        public CountThread_Breathe(EditText timer1) {
            timer=timer1;
        }

        //計時器
        public void run() {
            int time_cnt = 0;
            while (running) {
                if (++time_cnt > (60 * Double.parseDouble(String.valueOf(timer.getText())))) {
                    SetRunning(false);
                    animationDrawable.start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.setEnabled(true);
                        }
                    });
                    break;
                }
                System.out.println(time_cnt);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        //計時器、音樂、動畫執行設定
        public void SetRunning(boolean run) {
            running = run;
//            seaAnimation.stop();
            if (videoView != null) {
                videoView.stopPlayback();
            }
            if (breathe_sound != null) {
                breathe_sound.stop();
                if (!breathe_sound.isPlaying()) {
                    System.out.println("music_stop");
                }
                breathe_sound.release();
                breathe_sound = null;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (t != null) {
            t.SetRunning(false);
        }
        if (breathe_sound != null) { // 检查 MediaPlayer 是否为 null
            breathe_sound.release();
        }
    }


}