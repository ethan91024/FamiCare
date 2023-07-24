package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.health.connect.client.HealthConnectClient;

import com.ethan.FamiCare.Firebasecords.SymptomModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MoodFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public MoodFragment() {
        // Required empty public constructor
    }

    public static MoodFragment newInstance(String param1, String param2) {
        MoodFragment fragment = new MoodFragment();
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


    private double StressNumber = 0;
    private CheckBox ckb;
    private boolean run = true; //可否分析壓力指數
    private int HeartRrte = 0;
    private int Sleep = 0;
    private int BloodOxygen = 0;
    private View mainview;
    private Button analize;

    private TextView menditation; //建議的 textview(現在先放冥想)
    private TextView breathe;
    // private Fragment advice_fm[]={new MeditationFragment(),new BreatheFragment(),new sportFragment()}; //各個緩解方式的介面，要做新的就加新的

    private Fragment sport_fm[] = {new sportFragment()};
    private TextView sport;//nai
    String Date[] = new String[7];
    private String dday;
    private double stressN;



    //firebase使用者
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid = auth.getCurrentUser().getUid();
    private String userid;
    private int totalheadache=0, totaldizzy=0, totalnausea=0, totaltired=0, totalstomachache=0;
    private TextView advice;





    //set linechart
    LineChartData lineChartData;
    LineChart lineChart;
    ArrayList<String> HeartRrtexData = new ArrayList<>();
//1

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_mood, container, false);
//        //更新標題
//        getActivity().setTitle("壓力分析");
        // 其他Fragment代码...
        ArrayList<String> HeartRateList = getHeaetRatepoints();
        ArrayList<String> SleepList = getSleeppoints();
        ArrayList<String> BloodOxygenList = getBloodOxygenpoints();







        if (HeartRateList.size() < 1 || SleepList.size() < 1 || BloodOxygenList.size() < 1) {//有缺少其中一項資料，顯示無法分析
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            stressnumber.setText("缺少資料無法分析");
            run = false;
        } else {
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            String getstressn = getStressNumber(HeartRateList, SleepList, BloodOxygenList);
            stressnumber.setText(getstressn);
            stressN=Double.parseDouble(getstressn);


        }

        //跳到MoodSymptom
        analize = mainview.findViewById(R.id.analize);
        analize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SymptomActivity.class);
                startActivity(intent);
            }
        });

        //勾選症狀的加分


        //firebase



        //載入心律、睡眠等資料
        lineChart = mainview.findViewById(R.id.lineChart);
        lineChartData = new LineChartData(lineChart, this.getContext());
        for (int i = 1; i <= 7; i++) {
            HeartRrtexData.add("第" + i + "天");
        }
        lineChartData.initX(HeartRrtexData);
        lineChartData.initY(0F, 10F);


        ArrayList<String> EntryHeartRatepoints = getHeaetRatepoints(); //從getHeaetRatepoints()取得隨機值
        ArrayList<Entry> HeartRatepoints = points(EntryHeartRatepoints);

        ArrayList<String> EntrySleeppoints = getSleeppoints();//從getSleeppoints取得隨機值
        ArrayList<Entry> Sleeppoints = points(EntrySleeppoints);

        ArrayList<String> EntryBloodOxygenpoints = getBloodOxygenpoints();//從getBloodOxygenpoints取得隨機值
        ArrayList<Entry> BloodOxygenpoints = points(EntryBloodOxygenpoints);

        lineChartData.initDataSet(HeartRatepoints, Sleeppoints, BloodOxygenpoints);

        //跳轉到冥想
        menditation = mainview.findViewById(R.id.Menditation);
        menditation.setText("  冥想");
        menditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               FragmentManager fm = getActivity().getSupportFragmentManager();
//               fm.beginTransaction().replace(R.id.Mood_layout, new MeditationFragment()).addToBackStack(null).commit();
                Intent intent = new Intent(getActivity(), Meditation.class);
                startActivity(intent);

            }
        });
        //跳轉呼吸介面
        breathe = mainview.findViewById(R.id.Breathe);
        breathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Breathe.class);
                startActivity(intent);
            }
        });

        //nai
        sport = mainview.findViewById(R.id.Sport);
        sport.setText("  運動");
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_layout, sport_fm[0]).addToBackStack(null).commit();
            }
        });

        //進階症狀分析圖片，設置於左方
        Drawable drawable = getResources().getDrawable(R.drawable.symptom_img);
        drawable.setBounds(70, 4, 185, 115);
        analize.setCompoundDrawables(drawable, null, null, null);
//        //冥想加圖案
//        Drawable meditationD = getResources().getDrawable(R.drawable.mood_med_img); // 取得圖案 Drawable 物件
//        meditationD.setBounds(5, 15, 510, 450);
//        Drawable scr1 = getResources().getDrawable(R.drawable.scr1_img);
//        scr1.setBounds(10, 40, 170, 180);
//        Drawable scr2 = getResources().getDrawable(R.drawable.scr2_img);
//        scr2.setBounds(-100,90, 300, 250);
//        advice.setCompoundDrawables(null,null,meditationD,null);
//
//        // 設置呼吸圖片的範圍，將圖案放置在右下角
//        Drawable breatheD = getResources().getDrawable(R.drawable.mood_breathe_img); // 取得圖案 Drawable 物件
//        breatheD.setBounds(65, 55, 595, 580);
//        Drawable scr3 = getResources().getDrawable(R.drawable.scr1_img);
//        scr3.setBounds(200, 0, 510, 160);
//        Drawable scr4 = getResources().getDrawable(R.drawable.scr3_img);
//        scr4.setBounds(-200,120, 520, 290);
//        breathe.setCompoundDrawables(null,scr3,breatheD,scr4);
//
//        // 設置運動圖片的範圍，將圖案放置在右下角
//        Drawable sportD = getResources().getDrawable(R.drawable.mood_sport_img); // 取得圖案 Drawable 物件
//        sportD.setBounds(35, 30, 580, 580);
//        Drawable scr5 = getResources().getDrawable(R.drawable.scr4_img);
//        scr5.setBounds(0, 10, 190, 150);
//        Drawable scr6 = getResources().getDrawable(R.drawable.scr4_img);
//        scr6.setBounds(-200,90, 740, 280);
//        sport.setCompoundDrawables(null,scr5,sportD,scr6);

        //緩減建議



        return mainview;
    }

    public String getStressNumber(ArrayList<String> HeartRateList, ArrayList<String> SleepList, ArrayList<String> BloodOxygenList) {//計算壓力指數
        int HeartRateStressN = 0; //心率的壓力指數
        int SleepStressN = 0; //睡眠的壓力指數
        int BloodCxygenN = 0;//血氧的壓力指數
        double[] daily = new double[7];
        double WeekStressN = 0;

        for (int i = 0; i < daily.length; i++) {
            String hearts = HeartRateList.get(i);
            String sleeps = SleepList.get(i);
            String bloods = BloodOxygenList.get(i);
            HeartRrte = Integer.parseInt(hearts);
            Sleep = Integer.parseInt(sleeps);
            BloodOxygen = Integer.parseInt(bloods);
            //心率
            if (HeartRrte > 79 && HeartRrte < 89) {
                HeartRateStressN = 6;
            } else if (HeartRrte > 89) {
                HeartRateStressN = 8;
            }
            //血氧
            if (BloodOxygen < 98 && BloodOxygen > 95) {
                BloodCxygenN = 7;
            } else if (BloodOxygen <= 95) {
                BloodCxygenN = 8;
            }
            //睡眠
            if (Sleep < 8 && Sleep > 6) {
                SleepStressN = 4;
            } else if (Sleep < 6 && Sleep > 4) {
                SleepStressN = 6;
            } else if (Sleep < 4) {
                SleepStressN = 8;
            }
            //壓力數日平均
            daily[i] = (HeartRateStressN + BloodCxygenN + SleepStressN) / 3;
            WeekStressN += daily[i];
        }
        int n1 = (int) WeekStressN / 7, n2 = (int) (WeekStressN / 7) + 1;//四捨五入
        if (WeekStressN / 7 > (n1 + n2) / 2) {
            StressNumber = n1 + 0.5;
        } else {
            StressNumber = n1;
        }
        String sn = StressNumber + "";
        return sn;
    }
    //症狀checkbox 加分計算
//    public double SymptomCheckBox(boolean run) {
//        double plusnumber = 0;
//        if (run) {
//            Bundle arguments = getArguments();
//            if (arguments != null) {
//                plusnumber = arguments.getDouble("symptom");
//            } else {
//                plusnumber = 0;
//            }
//        } else {
//            plusnumber = -1;//如果缺乏心率睡眠血氧資料，直接無法分析，回傳-1表示無法run
//        }
//        return plusnumber;
//    }


    private ArrayList<String> getHeaetRatepoints() {//製造心率假資料，名子不要改會亂掉
        ArrayList<String> points = new ArrayList<>();
        Random r = new Random(111);
        for (int i = 0; i < 7; i++) {
            int num = 60 + r.nextInt(100 - 60 + 1);
            String s = "" + num;
            //Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));
            //points.add(new Entry(i, f))
            points.add(s);


        }

        return points;
    }

    private ArrayList<String> getSleeppoints() {//製造睡眠假資料，名子不要改
        ArrayList<String> points = new ArrayList<>();
        Random r = new Random(111);
        for (int i = 0; i < 7; i++) {
            int num = r.nextInt(20);
            String s = "" + num;
            //Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));
            //points.add(new Entry(i, f));
            points.add(s);


        }

        return points;
    }

    private ArrayList<String> getBloodOxygenpoints() {//製造血氧假資料，名子不要改
        ArrayList<String> points = new ArrayList<>();
        Random r = new Random(111);
        for (int i = 0; i < 7; i++) {
            int num = 90 + r.nextInt(100 - 90 + 1);
            String s = "" + num;
            //Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));
            //points.add(new Entry(i, f));
            points.add(s);


        }

        return points;
    }

    private ArrayList<Entry> points(ArrayList<String> entrypoints) {//加進Y軸資料
        ArrayList<Entry> getpoints = new ArrayList<>();
        for (int i = 0; i < entrypoints.size(); i++) {
            String s = entrypoints.get(i);
            Float f = Float.parseFloat(s);
            if (f > 20) {
                f = f / 10;
            }
            getpoints.add(new Entry(i, f));
        }
        return getpoints;
    }


    public class LineChartData {
        Context context;
        LineChart lineChart;

        public LineChartData(LineChart lineChart, Context context) {
            this.context = context;
            this.lineChart = lineChart;
        }

        public void initDataSet(ArrayList<Entry> valuesY1, ArrayList<Entry> valuesY2, ArrayList<Entry> valuesY3) {
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            if (valuesY1.size() > 0) {
                LineDataSet set1;
                set1 = new LineDataSet(valuesY1, "heartrate");
                set1.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set1.setColor(context.getResources().getColor(R.color.brownred));//線的顏
                set1.setCircleColor(context.getResources().getColor(R.color.brownred));//圓點顏色
                set1.setCircleRadius(4);//原點大小
                set1.setDrawCircleHole(false);//圓點為實心(預設空心)
                set1.setLineWidth(2f);//線寬
                set1.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set1.setValueTextSize(8);//座標點數字大小
                dataSets.add(set1);
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(R.color.brownred);//文字顏色
            }

            if (valuesY2.size() > 0) {
                LineDataSet set2;
                set2 = new LineDataSet(valuesY2, "sleep");
                set2.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set2.setColor(context.getResources().getColor(R.color.orange));//線的顏
                set2.setCircleColor(context.getResources().getColor(R.color.orange));//圓點顏色
                set2.setCircleRadius(4);//原點大小
                set2.setDrawCircleHole(false);//圓點為實心(預設空心)
                set2.setLineWidth(2f);//線寬
                set2.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set2.setValueTextSize(8);//座標點數字大小
                dataSets.add(set2);
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(R.color.orange);//文字顏色
            }

            if (valuesY3.size() > 0) {
                LineDataSet set3;
                set3 = new LineDataSet(valuesY3, "blood");
                set3.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set3.setColor(context.getResources().getColor(R.color.colorAccent));//線的顏
                set3.setCircleColor(context.getResources().getColor(R.color.colorAccent));//圓點顏色
                set3.setCircleRadius(4);//原點大小
                set3.setDrawCircleHole(false);//圓點為實心(預設空心)
                set3.setLineWidth(2f);//線寬
                set3.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set3.setValueTextSize(8);//座標點數字大小
                dataSets.add(set3);
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(Color.GRAY);//文字顏色
            }

            Legend legend = lineChart.getLegend();
            legend.setEnabled(true);//顯示圖例 (預設顯示)
            Description description = lineChart.getDescription();
            description.setEnabled(false);//不顯示Description Label (預設顯示)
            LineData data = new LineData(dataSets);
            lineChart.setData(data);//一定要放在最後

            lineChart.invalidate();//繪製圖表
        }


        public void initX(ArrayList datalist) {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X軸標籤顯示位置(預設顯示在上方，分為上方內/外側、下方內/外側及上下同時顯示)
            xAxis.setTextColor(Color.BLACK);//X軸標籤顏色
            xAxis.setTextSize(13);//X軸標籤大小
            xAxis.setAxisLineColor(Color.BLACK);//X軸顏色


            xAxis.setLabelCount(datalist.size());//X軸標籤個數
            xAxis.setSpaceMin(0.5f);//折線起點距離左側Y軸距離
            xAxis.setSpaceMax(0.5f);//折線終點距離右側Y軸距離

            xAxis.setDrawGridLines(false);//不顯示每個座標點對應X軸的線 (預設顯示)
            xAxis.setValueFormatter(new IndexAxisValueFormatter(datalist));
        }

        public void initY(Float min, Float max) {
            YAxis rightAxis = lineChart.getAxisRight();//獲取右側的軸線
            rightAxis.setEnabled(false);//不顯示右側Y軸
            YAxis leftAxis = lineChart.getAxisLeft();//獲取左側的軸線

            leftAxis.setLabelCount(4);//Y軸標籤個數
            leftAxis.setTextColor(Color.BLACK);//Y軸標籤顏色
            leftAxis.setTextSize(13);//Y軸標籤大小
            leftAxis.setAxisLineColor(Color.BLACK);//Y軸顏色

            leftAxis.setAxisMinimum(min - 10);//Y軸標籤最小值
            leftAxis.setAxisMaximum(max + 10);//Y軸標籤最大值

            leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        }

        class MyYAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

            private DecimalFormat mFormat;

            public MyYAxisValueFormatter() {
                mFormat = new DecimalFormat("###,###.0");//Y軸數值格式及小數點位數
            }

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(value);
            }


        }
    }
    //日期設定
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

    public void saveSymptoms(String date,double stressn) {
        // 将 Checkbox 的状态存储到 Firebase Database
//        SymptomModel stressmodel = new SymptomModel(stressn);

        databaseReference.child("StressNumber").child(userid).child(date).setValue(stressn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(getContext(), "已成功更新", Toast.LENGTH_SHORT).show();
            }
        });

        //.child(date).child("headache").push().setValue(headache);
//        databaseReference.child("symptom").push().child(userid).child(date).child("dizzy").push().setValue(dizzy);
//        databaseReference.child("symptom").push().child(userid).child(date).child("nausea").push().setValue(nausea);
//        databaseReference.child("symptom").push().child(userid).child(date).child("tired").push().setValue(tired);
//        databaseReference.child("symptom").push().child(userid).child(date).child("stomachache").push().setValue(stomachache);
    }
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private void refreshData() {
        // 例如，如果你要更新TextView的文本，可以这样做：
        //日期
        String[] rs = new String[7];//替代日期寫法
        Date = SetDate();
        String date[] = new String[7];
        for (int i = 0; i < 7; i++) {
            String s[] = Date[i].split("/");
            date[i] = s[1] + "/" + s[2];
            rs[i] = Date[i].replace("/", "_");//把/換成_
//            System.out.println(rs[i]);
        }
        dday= Date[0].replace("/", "_");//把/換成_


        final double[] synumbern = {0};

        databaseReference.child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userid1 = snapshot.getValue(String.class);
                    userid = userid1;
                    System.out.println("UserID:"+userid);
                    saveSymptoms(dday,stressN);

                    // 讀取 firebase 資料並計算 synumbern[0]
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
                                    synumbern[0] += Rpressn;
                                    //去除小數點後一位

//                                    // 示例：打印读取到的症状数据
//                                    Log.d("TAG", "Headache: " + Rheadache);
//                                    Log.d("TAG", "Dizzy: " + Rdizzy);
//                                    Log.d("TAG", "Nausea: " + Rnausea);
//                                    Log.d("TAG", "Tired: " + Rtired);
//                                    Log.d("TAG", "Stomachache: " + Rstomachache);
//                                    Log.d("TAG", "Pressn: " + Rpressn);

                                    //計算症狀次數
                                    if(Rheadache==true) totalheadache+=1;
                                    if(Rdizzy==true) totaldizzy+=1;
                                    if(Rnausea==true) totalnausea+=1;
                                    if(Rtired==true) totaltired+=1;
                                    if(Rstomachache==true) totalstomachache+=1;
                                    int []smax={totalheadache,totaldizzy,totalnausea,totaltired,totalstomachache};

                                    //分析症狀資料且更新緩解建議
                                    int max = Arrays.stream(smax).max().getAsInt();
                                    if(totalheadache == max ){
                                        //跳轉運動介面
                                        advice= mainview.findViewById(R.id.Advice);
                                        advice.setText("  運動");
                                        int heightInDp = 160; // 设置高度为 100 dp
                                        int heightInPx = dpToPx(getContext(), heightInDp);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT 或具体数值
                                                heightInPx // 设置高度为 dp 转换后的像素值
                                        );
                                        advice.setLayoutParams(layoutParams);
                                        Drawable drawable = getResources().getDrawable(R.drawable.sport_background);
                                        advice.setBackground(drawable);
                                        advice.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                                fm.beginTransaction().replace(R.id.Mood_layout, sport_fm[0]).addToBackStack(null).commit();
                                            }
                                        });

                                    }
                                    if(totalstomachache==max||totaltired==max){
                                        //跳轉到冥想
                                        advice= mainview.findViewById(R.id.Advice);
                                        advice.setText("  冥想");
                                        int heightInDp = 160; // 设置高度为 100 dp
                                        int heightInPx = dpToPx(getContext(), heightInDp);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT 或具体数值
                                                heightInPx // 设置高度为 dp 转换后的像素值
                                        );
                                        advice.setLayoutParams(layoutParams);
                                        Drawable drawable = getResources().getDrawable(R.drawable.meditation_background);
                                        advice.setBackground(drawable);
                                        advice.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), Meditation.class);
                                                startActivity(intent);

                                            }
                                        });
                                    }
                                    if(totaldizzy==max||totalnausea==max){
                                        //跳轉呼吸介面
                                        advice= mainview.findViewById(R.id.Advice);
                                        advice.setText("  呼吸");
                                        int heightInDp = 160; // 设置高度为 100 dp
                                        int heightInPx = dpToPx(getContext(), heightInDp);
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT 或具体数值
                                                heightInPx // 设置高度为 dp 转换后的像素值
                                        );
                                        advice.setLayoutParams(layoutParams);
                                        Drawable drawable = getResources().getDrawable(R.drawable.breathe_background);
                                        advice.setBackground(drawable);
                                        advice.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Breathe.class);
                                                startActivity(intent);
                                            }
                                        });

                                    }



                                } else {
                                    Log.d("TAG", "No data found for the date: " + rdate);
                                }

                                // 檢查是否已經計算完所有資料
                                if (finalI == rs.length - 1) {

                                    // 在此處更新 synumbern[0] 後再次設置 TextView
                                    TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
                                    double d = stressN;
                                    d += synumbern[0];
                                    double td = d* 10;
                                    int ti = (int) td;
                                    d= (double) ti / 10;
                                    stressnumber.setText(d + "");
                                    System.out.println("stressnumber:" + d);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w("TAG", "Error getting symptom data.", databaseError.toException());
                            }
                        });
                    }
//                    System.out.println("s" + synumbern[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    public void onResume() {
        Log.e(TAG, "心情---onResume");
        super.onResume();
        refreshData(); // 自定义的方法，用于刷新数据和更新视图
    }

    @Override
    public void onPause() {
        Log.e(TAG, "心情---onPause");
        super.onPause();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "心情---onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }
}





