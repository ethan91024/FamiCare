package com.ethan.FamiCare.sport;

import static com.ethan.FamiCare.R.raw.sport3voice;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ethan.FamiCare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class Sport4 extends AppCompatActivity {

    private static final long Start_time=60000;

    private ImageSwitcher imageSwitcher;
    int[] imgs={R.drawable.sport4_3,R.drawable.sport4_1};

    private TextView textView_countdown;
    private Button start;
    private Button reset;
    private FloatingActionButton fab;
    private CountDownTimer countDownTimer;

    private boolean mTimerRunning;

    private long mTimerLeftInMillis=Start_time;
    private MediaPlayer player=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport4);
        textView_countdown = findViewById(R.id.textview_countdown);
        start = findViewById(R.id.start);
        reset = findViewById(R.id.stop);
        fab=findViewById(R.id.fab);

        imageSwitcher = findViewById(R.id.sport1_imgswitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                //makeView返回當前需要顯示的imageview控件，填進imageSwitcher
                ImageView imageView = new ImageView(Sport4.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(R.drawable.sport4_3);
                return imageView;
            }
        });
        imageSwitcher.postDelayed(new Runnable() {
            int cnt = 0;

            @Override
            public void run() {
                cnt++;
                if (cnt > imgs.length - 1) {
                    cnt = 0;
                }
                imageSwitcher.setImageResource(imgs[cnt]);
                imageSwitcher.postDelayed(this, 3000);
            }
        }, 1000);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MediaPlayer mediaPlayer = MediaPlayer.create(Sport1.this, sport1voice);
                //mediaPlayer.start();
                new Thread(()->MP3player()).start();
            }
        });
    }

    private void MP3player() {
        try {
            if(player==null) {
                player = MediaPlayer.create(Sport4.this, sport3voice);

            }
            if(player != null) {
                if (!player.isPlaying()) {
                    player.start();
                    System.out.println("開始");
                }
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.release();
                    player = null;
                    System.out.println("完成");
                }
            });
        }catch (Exception e){
            System.out.println("Wrong");
        }
    }

    private void startTimer() {
        countDownTimer=new CountDownTimer(mTimerLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerLeftInMillis=millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning=false;
                start.setText("開始");
                reset.setVisibility(View.VISIBLE);
                mTimerLeftInMillis=Start_time;
                updateCountDownText();

            }
        }.start();
        mTimerRunning=true;
        start.setText("暫停");
        reset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        mTimerRunning=false;
        start.setText("開始");
        reset.setVisibility(View.VISIBLE);
    }
    private void resetTimer() {
        mTimerLeftInMillis=Start_time;
        updateCountDownText();

    }

    public void updateCountDownText(){
        int minutes=(int)(mTimerLeftInMillis/1000)/60;
        int sec=(int)(mTimerLeftInMillis/1000)%60;
        String timeformat=String.format(Locale.getDefault(),"%02d:%02d",minutes,sec);
        textView_countdown.setText(timeformat);

    }

    protected void onStop() {
        super.onStop();
        if(player!=null){
            player.stop();
            System.out.println("stop");
            player.release();
            player=null;
        }

    }


}