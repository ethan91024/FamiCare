package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Breathe extends AppCompatActivity {


    private Button breathe_start;
    CountThread_Breathe t ;
    private EditText timer;
    private Button cancel;
    VideoView videoView;
    MediaPlayer breathe_sound = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);
        timer = findViewById(R.id.breathe_timer);
        cancel = findViewById(R.id.breathe_cancel);
        breathe_start = findViewById(R.id.breathe_start);
        videoView = findViewById(R.id.breath_videoview);
        videoView.setBackgroundResource(R.drawable.breathe_pic);
        MediaController mediaController = new MediaController(Breathe.this);
        mediaController.setAnchorView(videoView);
        mediaController.setVisibility(View.INVISIBLE);


        breathe_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Double.parseDouble(String.valueOf(timer.getText()))<=30) {
                    if (t == null || !t.isAlive()) {
                        videoView.setBackground(null);
                        t = new CountThread_Breathe();
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
                }else{
                    Toast.makeText(Breathe.this, "時間輸入錯誤", Toast.LENGTH_SHORT).show();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t != null) {
                    t.SetRunning(false);
                }

            }
        });

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

        //計時器
        public void run() {
            int time_cnt = 0;
            while (running) {
                if (++time_cnt > (60 * Double.parseDouble(String.valueOf(timer.getText())))) {
                    SetRunning(false);
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