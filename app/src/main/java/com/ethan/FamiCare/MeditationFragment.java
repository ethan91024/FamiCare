package com.ethan.FamiCare;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    boolean shutdown=true;
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
        count_time=mainview.findViewById(R.id.counter);
        timeV=mainview.findViewById(R.id.time_view);
        count_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                    final Runnable runnable = new Runnable() {
                        int countdownStarter = 5;
                        public void run() {
                            if(shutdown) {
                                System.out.println(countdownStarter);
                                count_time.setText(countdownStarter + "");
                                countdownStarter--;
                                timeV.setText("Start!(分鐘)");
                                if (countdownStarter < 0) {
                                    System.out.println("Timer Over!");
                                    scheduler.shutdown();
                                    timeV.setText("Timer Over!");
                                }
                            }else{
                                scheduler.shutdown();
                            }
                        }
                    };
                    scheduler.scheduleAtFixedRate(runnable, 0, 1, MINUTES);


            }
        });



        back = mainview.findViewById(R.id.back_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                shutdown=false;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_Meditation_Layout, new MoodFragment()).addToBackStack(null).commit();
            }
        });



        return mainview;
    }

}