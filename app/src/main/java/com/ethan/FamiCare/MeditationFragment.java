package com.ethan.FamiCare;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeditationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeditationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button back;
    private View mainview;
    private TextView count_time;
    private TextView timeV;
    boolean shutdown = true;
    private final int MIN_CLICK_DELAY_TIME = 60000;
    private long lastClickTime = 0L;
    private boolean flag = true;
    private int cnt = 0;
    private AnimationDrawable seaAnimation;
    CountThread t = null;
    int pic[] = {R.drawable.sea, R.drawable.sea2, R.drawable.sea3};




//    SimpleDateFormat sdf = new SimpleDateFormat("mm分ss秒");


    public MeditationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeditationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeditationFragment newInstance(String param1, String param2) {
        MeditationFragment fragment = new MeditationFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_meditation, container, false);
        count_time = mainview.findViewById(R.id.counter);
        timeV = mainview.findViewById(R.id.time_view);
        Drawable drawable = getResources().getDrawable(pic[0]);
        drawable.setBounds(10, 10, 20, 10);
        timeV.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

        ImageView sea_an =mainview.findViewById(R.id.sea_animation); //首頁預設圖片
        sea_an.setBackgroundResource(R.drawable.sea);   //sea的動畫
        MediaPlayer sea_sound =MediaPlayer.create(getActivity(),R.raw.sea_sound);
//        if (isAdded()) {
//// Fragment 已附加到活动中
//            System.out.println("add");
//        } else {
//            System.out.println("no add");
//// Fragment 未附加到活动中，不能调用 getActivity() 方法
//        }



        count_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sea_an.setBackgroundResource(R.drawable.sea_animation);
                seaAnimation = (AnimationDrawable) sea_an.getBackground();
                if (t == null || !t.isAlive()) {
                    t = new CountThread(mainview);
                    t.start();
                    seaAnimation.start();

                    sea_sound.start();

                }







            }
        });


        back = mainview.findViewById(R.id.back_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(t!=null) {
                    t.SetRunning(false);
                    sea_sound.stop();

                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_Meditation_Layout, new MoodFragment()).addToBackStack(null).commit();
            }
        });



        return mainview;
    }

    class CountThread extends Thread { //計時器
        boolean running = true;
        TextView tv;
        View view;

        CountThread(View v) {
            view = v;
            tv = v.findViewById(R.id.time_view);
        }

        public void run() {
            int time_cnt = 1;
            int page=0;
            while (running) {
                tv.setText("" + time_cnt);
//                SetPic(view);
                if (++time_cnt > 300) {
                    seaAnimation.stop();
//                    sea_sound.stop();
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
                seaAnimation.stop();
//               sea_sound.stop();

        }




    }


}