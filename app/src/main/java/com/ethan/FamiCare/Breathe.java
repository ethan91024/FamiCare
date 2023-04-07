package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Breathe extends AppCompatActivity {


    private Button breathe_start;
    CountThread_Breathe t = null;
    private EditText timer;
    private Button cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);
        timer=findViewById(R.id.breathe_timer);
        cancel=findViewById(R.id.breathe_cancel);
        breathe_start = findViewById(R.id.breathe_start);
        breathe_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (t == null || !t.isAlive()) {
                    t = new CountThread_Breathe();
                    t.start();

                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(t!=null) {
                    t.SetRunning(false);
                }

            }
        });


    }

    class CountThread_Breathe extends Thread { //計時器
        boolean running = true;

        CountThread_Breathe() {

        }

        public void run() {
            int time_cnt = 0;
            while (running) {
                  if (++time_cnt > (60 * Double.parseDouble(String.valueOf(timer.getText())))) {
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
        public void SetRunning(boolean run) {
            running = run;
//            seaAnimation.stop();
//            if (sea_sound != null) {
//                sea_sound.stop();
////                if(!sea_sound.isPlaying()){
////                    System.out.println("music_stop");
////                }
//                sea_sound.release();
//                sea_sound = null;
//
//            }
        }
    }
    @Override
    public void onResume() {
        Log.e(TAG, "呼吸---onResume");
        super.onResume();
    }

    public void onPause() {
        if (t != null) {
            t.SetRunning(false);
        }
        Log.e(TAG, "呼吸---onPause");
        super.onPause();
    }

}