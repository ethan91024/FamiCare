package com.ethan.FamiCare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.sql.SQLOutput;
import java.util.ArrayList;


public class MoodSymptomFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MoodSymptomFragment() {
        // Required empty public constructor
    }

    public static MoodSymptomFragment newInstance(String param1, String param2) {
        MoodSymptomFragment fragment = new MoodSymptomFragment();
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

    private Button back;
    CheckBox ckb;
    private Button updatesy;
    private int[] id = {R.id.headache, R.id.dizzy, R.id.nausea, R.id.tried, R.id.stomachache};
    private double synumber = 0;
    ArrayList<Integer> RadioID = new ArrayList<>();
    int[] synumberlist =new int[7];
    boolean run;
    private View mainview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_mood_symptom, container, false);
        updatesy = mainview.findViewById(R.id.updatesy);
        RadioGroup weekID = (RadioGroup) mainview.findViewById(R.id.week);
        updatesy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekID.getCheckedRadioButtonId() == -1) {
                    // no radio buttons are checked
                    run = false;
                    synumber += SymptomCheckBox(mainview, run);
                    Toast.makeText(getContext(), "請選擇星期", Toast.LENGTH_SHORT).show();

                } else {
                    run = true;
                    synumber += SymptomCheckBox(mainview, run);
                    // one of the radio buttons is checked
                }//勾選症狀的加分
                double td = synumber * 10;//去除小數點後一位
                int ti = (int) td;
                synumber = (double) ti / 10;

                //顯示textview
//                String sn = "" + synumber;
//                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
//                stressnumber.setText(sn);

            }

        });
//1

        back = mainview.findViewById(R.id.back_to_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putDouble("symptom", synumber);
                MoodFragment MoodFragment = new MoodFragment();
                MoodFragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_Symptom_layout, MoodFragment).addToBackStack(null).commit();

            }
        });


        return mainview;
    }

    public double SymptomCheckBox(View view, boolean run) {
        double checkboxn = 0;
        if (run) {
            for (int i : id) {
                ckb = (CheckBox) view.findViewById(i);
                if (ckb.isChecked()) {
                    checkboxn += 0.1;
                }
            }
        }
        for (int i : id) {
            ckb = (CheckBox) view.findViewById(i);
            ckb.setChecked(false);
        }
        //不知為何0.3會有很多小數點，因此只取小數點第一位

        return checkboxn;
    }
}