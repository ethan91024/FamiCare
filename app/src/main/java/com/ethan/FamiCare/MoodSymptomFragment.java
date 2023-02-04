package com.ethan.FamiCare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


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
    private int[] id = {R.id.head1, R.id.dizzy1, R.id.nausea1, R.id.tried1, R.id.stomachache1};
    private double synumber =0;
    private double []synumberlist = new double[7];

    private View mainview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_mood_symptom, container, false);
        updatesy = mainview.findViewById(R.id.updatesy);
        updatesy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                synumber = SymptomCheckBox(mainview);//勾選症狀的加分
                String sn = "" + synumber;
                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
                stressnumber.setText(sn);
            }

        });


        back = mainview.findViewById(R.id.back_to_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_Symptom_layout, new MoodFragment()).addToBackStack(null).commit();
            }
        });


        return mainview;
    }

    public double SymptomCheckBox(View view) {
        double checkboxn = 0;
        for (int i : id) {
            ckb = (CheckBox) view.findViewById(i);
            if (ckb.isChecked()) {
                checkboxn += 0.1;
            }
        }
        return checkboxn;
    }
}