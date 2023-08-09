package com.ethan.FamiCare.Mood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.SymptomModel;
import com.ethan.FamiCare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SymptomActivity extends AppCompatActivity {
    private Button back;
    CheckBox ckb;
    private Button updatesy;
    private int[][] id = {
            {R.id.headache, R.id.dizzy, R.id.nausea, R.id.tired, R.id.stomachache},
            {R.id.headache_Two, R.id.dizzy_Two, R.id.nausea_Two, R.id.tired_Two, R.id.stomachache_Two},
            {R.id.headache_Three, R.id.dizzy_Three, R.id.nausea_Three, R.id.tired_Three, R.id.stomachache_Three},
            {R.id.headache_Four, R.id.dizzy_Four, R.id.nausea_Four, R.id.tired_Four, R.id.stomachache_Four},
            {R.id.headache_Five, R.id.dizzy_Five, R.id.nausea_Five, R.id.tired_Five, R.id.stomachache_Five},
            {R.id.headache_Six, R.id.dizzy_Six, R.id.nausea_Six, R.id.tired_Six, R.id.stomachache_Six},
            {R.id.headache_Seven, R.id.dizzy_Seven, R.id.nausea_Seven, R.id.tired_Seven, R.id.stomachache_Seven}
    };
    private double[] synumber = new double[7];
    boolean run;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);
        //更新標題
        this.setTitle("症狀勾選");
        //日期
        String[] rs = new String[7];//替代日期寫法
        Date = SetDate();
        String date[] = new String[7];
        for (int i = 0; i < 7; i++) {
            String s[] = Date[i].split("/");
            date[i] = s[1] + "/" + s[2];
            TextView tv = (TextView) findViewById(Daylist[i]);
            tv.setText(date[i]);
            rs[i] = Date[i].replace("/", "_");//把/換成_
//            System.out.println(rs[i]);

        }
        //firebase 讀資料
        if (auth.getCurrentUser() == null) {

            Toast.makeText(this, "錯誤", Toast.LENGTH_SHORT).show();


        } else {
            databaseReference.child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userid1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        userid = userid1;
                        System.out.println(userid);
                        // 獲取 userid 後，您可以在這裡繼續進行其他操作，例如從數據庫中讀取數據
                        //讀取firebase資料
                        for (int i = 0; i < rs.length; i++) {
                            String rdate = rs[i];
                            int finalI = i;
                            databaseReference.child("symptom").child(userid).child(rdate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Boolean RheadacheValue = dataSnapshot.child("headache").getValue(Boolean.class);
                                        boolean Rheadache = (RheadacheValue != null) ? RheadacheValue : false;
                                        Boolean RdizzyValue = dataSnapshot.child("dizzy").getValue(Boolean.class);
                                        boolean Rdizzy = (RdizzyValue != null) ? RdizzyValue : false;
                                        Boolean RnauseaValue = dataSnapshot.child("nausea").getValue(Boolean.class);
                                        boolean Rnausea = (RnauseaValue != null) ? RnauseaValue : false;
                                        Boolean RtiredValue = dataSnapshot.child("tired").getValue(Boolean.class);
                                        boolean Rtired = (RtiredValue != null) ? RtiredValue : false;
                                        Boolean RstomachacheValue = dataSnapshot.child("stomachache").getValue(Boolean.class);
                                        boolean Rstomachache = (RstomachacheValue != null) ? RstomachacheValue : false;
                                        Double RpressnValue = dataSnapshot.child("pressn").getValue(Double.class);
                                        double Rpressn = (RpressnValue != null) ? RpressnValue : 0;

//                                        // 示例：打印读取到的症状数据
//                                        Log.d("TAG", "Headache: " + Rheadache);
//                                        Log.d("TAG", "Dizzy: " + Rdizzy);
//                                        Log.d("TAG", "Nausea: " + Rnausea);
//                                        Log.d("TAG", "Tired: " + Rtired);
//                                        Log.d("TAG", "Stomachache: " + Rstomachache);
//                                        Log.d("TAG", "Pressn: " + Rpressn);

                                        boolean[] b = {Rheadache, Rdizzy, Rnausea, Rtired, Rstomachache};
                                        for (int j = 0; j < id[finalI].length; j++) {
                                            CheckBox rckb = findViewById(id[finalI][j]);
                                            rckb.setChecked(b[j]);
                                        }
                                    } else {
                                        Log.d("TAG", "No data found for the date: " + rdate);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("TAG", "Error getting symptom data.", databaseError.toException());
                                }
                            });
                        }


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }





        back = findViewById(R.id.back_to_mood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run = true;
                synumber = SymptomCheckBox(run);

                //開始存資料
                for (int i = 0; i < Date.length; i++) {
                    //    勾選症狀的加分，不知為何0.3會有很多小數點，因此只取小數點第一位
                    double td = synumber[i] * 10;
                    int ti = (int) td;
                    synumber[i] = (double) ti / 10;
                    CheckBox ckb0 = findViewById(id[i][0]);
                    boolean b0 = ckb0.isChecked();
                    CheckBox ckb1 = findViewById(id[i][1]);
                    boolean b1 = ckb1.isChecked();
                    CheckBox ckb2 = findViewById(id[i][2]);
                    boolean b2 = ckb2.isChecked();
                    CheckBox ckb3 = findViewById(id[i][3]);
                    boolean b3 = ckb3.isChecked();
                    CheckBox ckb4 = findViewById(id[i][4]);
                    boolean b4 = ckb4.isChecked();
                    String rs = Date[i].replace("/", "_");
                    saveSymptoms(rs, b0, b1, b2, b3, b4, synumber[i]);
                }

                //顯示textview
//                String sn = "" + synumber;
//                TextView stressnumber = (TextView) mainview.findViewById(R.id.number1);
//                stressnumber.setText(sn);

//                Bundle bundle = new Bundle();
//                bundle.putDouble("symptom", synumber);
//                Fragment MoodFragment = new MoodFragment();
//                MoodFragment.setArguments(bundle);
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                fm.beginTransaction().replace(R.id.Mood_Symptom_layout, MoodFragment).addToBackStack(null).commit();

                Toast.makeText(SymptomActivity.this, "已成功更新", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public double[] SymptomCheckBox(boolean run) {
        double[] checkboxn = new double[7];
        if (run) {
            for (int i = 0; i < id.length; i++) {
                for (int j = 0; j < id[i].length; j++) {
                    ckb = (CheckBox) findViewById(id[i][j]);
                    if (ckb.isChecked()) {
                        checkboxn[i] += 0.1;
                    }
                }
//                System.out.println("ck" + checkboxn[i]);
            }
        }



        return checkboxn;
    }

    public String[] SetDate() {
        // 定义好时间字串的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // 修改格式为 "yyyy/MM/dd"
        java.util.Date dt = new Date();
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

    //存資料函式
    public void saveSymptoms(String date, boolean headache, boolean dizzy, boolean nausea, boolean tired, boolean stomachache, double pressn) {
        // 将 Checkbox 的状态存储到 Firebase Database
        SymptomModel smodel = new SymptomModel(headache, dizzy, nausea, tired, stomachache, pressn);
        databaseReference.child("symptom").child(userid).child(date).setValue(smodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(SymptomActivity.this, "已成功更新", Toast.LENGTH_SHORT).show();
            }
        });
    }


}