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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MoodSymptomFragment extends Fragment {

    private Button back;
    CheckBox ckb;
    private Button updatesy;
    private int[] id = {R.id.headache, R.id.dizzy, R.id.nausea, R.id.tried, R.id.stomachache,
            R.id.headache_Two, R.id.dizzy_Two, R.id.nausea_Two, R.id.tried_Two, R.id.stomachache_Two,
            R.id.headache_Three, R.id.dizzy_Three, R.id.nausea_Three, R.id.tried_Three, R.id.stomachache_Three,
            R.id.headache_Four, R.id.dizzy_Four, R.id.nausea_Four, R.id.tried_Four, R.id.stomachache_Four,
            R.id.headache_Five, R.id.dizzy_Five, R.id.nausea_Five, R.id.tried_Five, R.id.stomachache_Five,
            R.id.headache_Six, R.id.dizzy_Six, R.id.nausea_Six, R.id.tried_Six, R.id.stomachache_Six,
            R.id.headache_Seven, R.id.dizzy_Seven, R.id.nausea_Seven, R.id.tried_Seven, R.id.stomachache_Seven};
    private double synumber = 0;
    boolean run;
    private View mainview;
    String Date[] = new String[7];
    int Daylist[] = {R.id.DDAY, R.id.TWOD, R.id.THREED, R.id.FOURD, R.id.FIVED, R.id.SIXD, R.id.SEVEND};
    boolean doublecheck = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_mood_symptom, container, false);
//        updatesy = mainview.findViewById(R.id.updatesy);
//        updatesy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    run = true;
//                    synumber = SymptomCheckBox(mainview, run);
//                    // one of the radio buttons is checked
//                //勾選症狀的加分
//                double td = synumber * 10;//去除小數點後一位
//                int ti = (int) td;
//                synumber = (double) ti / 10;
//
//                //顯示textview
////                String sn = "" + synumber;
////                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
////                stressnumber.setText(sn);
//                Toast.makeText(getContext(), "已更新症狀", Toast.LENGTH_SHORT).show();
//
//            }
//
//        });
        //更新標題
        getActivity().setTitle("症狀勾選");
//1
        String date[] = SetDate();
        for (int i = 0; i < date.length; i++) {
            TextView tv = (TextView) mainview.findViewById(Daylist[i]);
            tv.setText(date[i]);

        }


//依照設定格式取得字串


        back = mainview.findViewById(R.id.back_to_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run = true;
                synumber = SymptomCheckBox(mainview, run);
                // one of the radio buttons is checked
                //勾選症狀的加分
                double td = synumber * 10;//去除小數點後一位
                int ti = (int) td;
                synumber = (double) ti / 10;

                //顯示textview
//                String sn = "" + synumber;
//                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
//                stressnumber.setText(sn);
                Toast.makeText(getContext(), "已更新症狀", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putDouble("symptom", synumber);
                Fragment MoodFragment = new MoodFragment();
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
//        for (int i : id) {
//            ckb = (CheckBox) view.findViewById(i);
//            ckb.setChecked(false);
//        }
        //不知為何0.3會有很多小數點，因此只取小數點第一位

        return checkboxn;
    }

    public String[] SetDate() {
        //定義好時間字串的格式
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        Date dt = new Date();
//透過SimpleDateFormat的format方法將Date轉為字串
        String dts = sdf.format(dt);
        try {
            Date dt2 = sdf.parse(dts);
            for (int i = 0; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt2);
                calendar.add(Calendar.DATE, ((i * -1)));//日期減1
                Date tdt = calendar.getTime();//取得加減過後的Date
                String time = sdf.format(tdt);
                Date[i] = time;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Date;

    }
}