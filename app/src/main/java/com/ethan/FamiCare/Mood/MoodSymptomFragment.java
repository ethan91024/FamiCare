package com.ethan.FamiCare.Mood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ethan.FamiCare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ethan.FamiCare.Firebasecords.SymptomModel;
import com.ethan.FamiCare.Settings.Addfriend;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MoodSymptomFragment extends Fragment {

    private Button back;
    CheckBox ckb;
    private Button updatesy;
    private int[][] id = {
            {R.id.headache, R.id.dizzy, R.id.nausea, R.id.tried, R.id.stomachache},
            {R.id.headache_Two, R.id.dizzy_Two, R.id.nausea_Two, R.id.tried_Two, R.id.stomachache_Two},
            {R.id.headache_Three, R.id.dizzy_Three, R.id.nausea_Three, R.id.tried_Three, R.id.stomachache_Three},
            {R.id.headache_Four, R.id.dizzy_Four, R.id.nausea_Four, R.id.tried_Four, R.id.stomachache_Four},
            {R.id.headache_Five, R.id.dizzy_Five, R.id.nausea_Five, R.id.tried_Five, R.id.stomachache_Five},
            {R.id.headache_Six, R.id.dizzy_Six, R.id.nausea_Six, R.id.tried_Six, R.id.stomachache_Six},
            {R.id.headache_Seven, R.id.dizzy_Seven, R.id.nausea_Seven, R.id.tried_Seven, R.id.stomachache_Seven}
    };
    private double []synumber =new double[7];
    boolean run;
    private View mainview;
    String Date[] = new String[7];
    int Daylist[] = {R.id.DDAY, R.id.TWOD, R.id.THREED, R.id.FOURD, R.id.FIVED, R.id.SIXD, R.id.SEVEND};
    boolean doublecheck = true;
    //firebase
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();



    //使用者

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid = auth.getCurrentUser().getUid();
    private String userid;

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
        //日期
        Date = SetDate();
        String date[] = new String[7];
        for (int i = 0; i < 7; i++) {
            String s[] = Date[i].split("/");
            date[i] = s[1] + "/" + s[2];
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
//                //勾選症狀的加分
//                double td = synumber * 10;//去除小數點後一位
//                int ti = (int) td;
//                synumber = (double) ti / 10;

                //開始存資料
                for (int i = 0; i < Date.length; i++) {
                    CheckBox ckb0 = mainview.findViewById(id[i][0]);
                    boolean b0 = ckb0.isChecked();
                    CheckBox ckb1 = mainview.findViewById(id[i][1]);
                    boolean b1 = ckb1.isChecked();
                    CheckBox ckb2 = mainview.findViewById(id[i][2]);
                    boolean b2 = ckb2.isChecked();
                    CheckBox ckb3 = mainview.findViewById(id[i][3]);
                    boolean b3 = ckb3.isChecked();
                    CheckBox ckb4 = mainview.findViewById(id[i][4]);
                    boolean b4 = ckb4.isChecked();
                    String rs =Date[i].replace("/","_");
                    saveSymptoms(rs, b0, b1, b2, b3, b4,synumber[i]);
                }

                //顯示textview
//                String sn = "" + synumber;
//                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
//                stressnumber.setText(sn);
                Toast.makeText(getContext(), "已更新症狀", Toast.LENGTH_SHORT).show();

//                Bundle bundle = new Bundle();
//                bundle.putDouble("symptom", synumber);
                Fragment MoodFragment = new MoodFragment();
//                MoodFragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_Symptom_layout, MoodFragment).addToBackStack(null).commit();


            }
        });


        //firebase
        if (auth.getCurrentUser() == null) {

            Toast.makeText(getContext(), "錯誤", Toast.LENGTH_SHORT).show();


        } else {
            databaseReference.child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userid1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        userid = userid1;
                        System.out.println(userid);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


        return mainview;
    }

    public double []SymptomCheckBox(View view, boolean run) {
        double []checkboxn = new double[7];
        if (run) {
            for (int i = 0; i < id.length; i++) {
                for (int j = 0; j < id[i].length; j++) {
                    ckb = (CheckBox) view.findViewById(id[i][j]);
                    if (ckb.isChecked()) {
                        checkboxn[i] += 0.1;
                    }
                }
                System.out.println(checkboxn[i]);

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
        // 定义好时间字串的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // 修改格式为 "yyyy/MM/dd"
        Date dt = new Date();
        // 透過SimpleDateFormat的format方法將Date轉為字串
        String dts = sdf.format(dt);
        try {
            Date dt2 = sdf.parse(dts);
            String[] dates = new String[7]; // 创建一个String数组来存储7个日期
            for (int i = 0; i < 7; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt2);
                calendar.add(Calendar.DATE, (i * -1)); // 日期减1
                Date tdt = calendar.getTime(); // 取得加減過後的Date
                String time = sdf.format(tdt);
                dates[i] = time;
            }
            return dates;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveSymptoms(String date, boolean headache, boolean dizzy, boolean nausea, boolean tired, boolean stomachache,double pressn) {
        // 将 Checkbox 的状态存储到 Firebase Database
        SymptomModel smodel =new SymptomModel(headache,dizzy,nausea,tired,stomachache,pressn);
        databaseReference.child("symptom").child(userid).child(date).setValue(smodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "已成功更新", Toast.LENGTH_SHORT).show();
            }
        });
                //.child(date).child("headache").push().setValue(headache);
//        databaseReference.child("symptom").push().child(userid).child(date).child("dizzy").push().setValue(dizzy);
//        databaseReference.child("symptom").push().child(userid).child(date).child("nausea").push().setValue(nausea);
//        databaseReference.child("symptom").push().child(userid).child(date).child("tired").push().setValue(tired);
//        databaseReference.child("symptom").push().child(userid).child(date).child("stomachache").push().setValue(stomachache);
    }
}