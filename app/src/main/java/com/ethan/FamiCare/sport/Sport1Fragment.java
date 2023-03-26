package com.ethan.FamiCare.sport;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethan.FamiCare.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sport1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sport1Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Sport1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sport1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Sport1Fragment newInstance(String param1, String param2) {
        Sport1Fragment fragment = new Sport1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View mainview;

    private static final long Start_time=60000;

    private ImageView imageView;
    private TextView textView_countdown;
    private Button start;
    private Button reset;

    private CountDownTimer countDownTimer;

    private boolean mTimerRunning;

    private long mTimerLeftInMillis=Start_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview= inflater.inflate(R.layout.fragment_sport1, container, false);

        textView_countdown=mainview.findViewById(R.id.textview_countdown);
        start=mainview.findViewById(R.id.start);
        reset=mainview.findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View v) {
            if(mTimerRunning){
                pauseTimer();
            }else{
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
        return mainview;
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

            }
        }.start();
        mTimerRunning=true;
        start.setText("暫停");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        mTimerRunning=false;
        start.setText("開始");
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

}