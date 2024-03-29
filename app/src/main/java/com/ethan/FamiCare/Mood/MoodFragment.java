package com.ethan.FamiCare.Mood;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ethan.FamiCare.Health.HealthHeartRateActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.sportFragment;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private HealthHeartRateActivity HealthHeartRateActivity;

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
    private Long HeartRrte = 0l;
    private double Sleep = 0;
    private Long BloodOxygen = 0l;
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
    private int totalheadache = 0, totaldizzy = 0, totalnausea = 0, totaltired = 0, totalstomachache = 0;
    private TextView advice;
    private TextView advice2;
    // 獲取當天日期
    LocalDate currentDate = LocalDate.now();

    // 獲取年、月、日
    int year = currentDate.getYear();
    int month = currentDate.getMonthValue();
    int day = currentDate.getDayOfMonth();
    String cdday = year + "_" + month + "_" + day;
    TextView minuet1;


    //set linechart
    LineChartData lineChartData;
    LineChart lineChart;
    ArrayList<String> HeartRrtexData = new ArrayList<>();
    ArrayList<String> HRpoint = new ArrayList<>();
    LinearLayout l1;

//1

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_mood, container, false);



        //跳到MoodSymptom
        analize = mainview.findViewById(R.id.analize);
        analize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SymptomActivity.class);
                startActivity(intent);
            }
        });

        //分鐘
        minuet1= mainview.findViewById(R.id.Minute1);


        //firebase


        //載入心律、睡眠等資料
        lineChart = mainview.findViewById(R.id.lineChart);
        lineChartData = new LineChartData(lineChart, this.getContext());
        for (int i = 1; i <= 7; i++) {
            HeartRrtexData.add("第" + i + "天");
        }
        lineChartData.initX(HeartRrtexData);
        lineChartData.initY(0F, 10F);






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


        //呼叫健康
        // 获取Activity实例
//        HealthHeartRateActivity kotlinActivity = (HealthHeartRateActivity) getActivity();

        LocalDate start = LocalDate.now(); // 获取当前日期
        LocalDateTime startDateTime = start.atStartOfDay(); // 获取当天的开始时间（凌晨）
        LocalDate end = start.minusDays(7); // 从当前日期往前推7天
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX); // 获取当天的最后时间（23:59:59.999999999）


        return mainview;
    }


    public String getStressNumber(ArrayList<String> HeartRateList, ArrayList<String> SleepList, ArrayList<String> BloodOxygenList) {
        try {
            //計算壓力指數

            int HeartRateStressN = 0; //心率的壓力指數
            int SleepStressN = 0; //睡眠的壓力指數
            int totalsleep = 0;
            int BloodCxygenN = 0;//血氧的壓力指數
            double[] daily = new double[7];
            double WeekStressN = 0;

            for (int i = 0; i < daily.length; i++) {
                String hearts = HeartRateList.get(i);
                String sleeps = SleepList.get(i);
                String bloods = BloodOxygenList.get(i);
                HeartRrte = Long.parseLong(hearts);
                Sleep = Double.parseDouble(sleeps);
                BloodOxygen = Long.parseLong(bloods);
                //心率
                if (HeartRrte > 79l && HeartRrte < 100l) {
                    HeartRateStressN = 6;
                } else if (HeartRrte > 100) {
                    HeartRateStressN = 8;
                } else if (HeartRrte > 50l && HeartRrte < 80l) {
                    HeartRateStressN = 4;
                } else {

                }
                //血氧
                if (BloodOxygen < 98l && BloodOxygen > 95l) {
                    BloodCxygenN = 4;
                } else if (BloodOxygen <= 95l) {
                    BloodCxygenN = 7;
                } else {
                    BloodCxygenN = 9;
                }
                //睡眠
                totalsleep += Sleep;

                //壓力數日平均
                daily[i] = (HeartRateStressN + BloodCxygenN) / 2;
                WeekStressN += daily[i];
            }
            if (totalsleep >= 56 && totalsleep <= 70) {
                SleepStressN = 3;
            } else if (totalsleep >= 35 && totalsleep < 56) {
                SleepStressN = 6;
            } else if (totalsleep < 35 && totalsleep >= 28) {
                SleepStressN = 8;
            } else if (totalsleep < 28) {
                SleepStressN = 10;
            } else if (totalsleep > 96) {
                SleepStressN = 5;
            }
            int n1 = (int) WeekStressN / 7, n2 = (int) (WeekStressN / 7) + 1;//四捨五入
            WeekStressN += SleepStressN;

            if (WeekStressN / 7 > (n1 + n2) / 2) {
                StressNumber = n1 + 0.5;
            } else {
                StressNumber = n1;
            }
            String sn = StressNumber + "";
            return sn;
        } catch (Exception exception) {
            String sn = StressNumber + "";
            return sn;
        }
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(ArrayList<String> heartRatePoints, ArrayList<String> sleepPoints, ArrayList<String> bloodOxygenPoints);
    }

    private void loadDataFromFirebase(OnDataLoadedListener listener) {
        ArrayList<String> heartRatePoints = new ArrayList<>();
        ArrayList<String> sleepPoints = new ArrayList<>();
        ArrayList<String> bloodOxygenPoints = new ArrayList<>();
        Long[] HRList = new Long[7];
        Long[] OXYList = new Long[7];
        double[] SleepList = new double[7];
        try {


            //firebase 讀資料
            if (auth.getCurrentUser() == null) {
                // ...
            } else {
                // ...

                // 讀取心律資料
                databaseReference.child("AnalyzeHealth").child(uid).child(cdday).child("AnalyzeHealth_heartRate").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (int i = 0; i < 7; i++) {
                                HRList[i] = dataSnapshot.child(i + "").getValue(Long.class);
                                heartRatePoints.add(HRList[i] + "");
                            }


                        } else {
                            Log.d("TAG", "No data found for the date: " + "錯誤");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAG", "Error getting symptom data.", databaseError.toException());
                    }
                });

                // 讀取睡眠資料
                databaseReference.child("AnalyzeHealth").child(uid).child(cdday).child("AnalyzeHealth_sleep").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            for (int i = 0; i < 7; i++) {
                                SleepList[i] = dataSnapshot.child(i + "").getValue(Long.class);
                                sleepPoints.add(SleepList[i] + "");
                            }


                        } else {
                            Log.d("TAG", "No data found for the date: " + "錯誤");
                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAG", "Error getting symptom data.", databaseError.toException());
                    }
                });

                // 讀取血氧資料
                databaseReference.child("AnalyzeHealth").child(uid).child(cdday).child("AnalyzeHealths_bloodOxygen").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            for (int i = 0; i < 7; i++) {
                                OXYList[i] = dataSnapshot.child(i + "").getValue(Long.class);
                                bloodOxygenPoints.add(OXYList[i] + "");
                            }


                        } else {
                            Log.d("TAG", "No data found for the date: " + "錯誤");
                        }


                        listener.onDataLoaded(heartRatePoints, sleepPoints, bloodOxygenPoints);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAG", "Error getting symptom data.", databaseError.toException());
                    }
                });
            }
        } catch (Exception exception) {
            Log.d("TAG", "No data found for the date: " + "錯誤");
        }
    }


    private ArrayList<String> getSleeppoints() {//製造睡眠假資料，名子不要改
        ArrayList<String> points = new ArrayList<>();
        Random r = new Random(111);
        for (int i = 0; i < 7; i++) {
            int num = r.nextInt(20);
            String s = "" + num;
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
                set1 = new LineDataSet(valuesY1, "心率");
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
                set2 = new LineDataSet(valuesY2, "睡眠");
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
                set3 = new LineDataSet(valuesY3, "血氧");
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

            leftAxis.setAxisMinimum(0);//Y軸標籤最小值
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

    public void saveSymptoms(String date, double stressn) {
        // 将 Checkbox 的状态存储到 Firebase Database
//        SymptomModel stressmodel = new SymptomModel(stressn);

        databaseReference.child("StressNumber").child(userid).child(date).setValue(stressn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(getContext(), "已成功更新", Toast.LENGTH_SHORT).show();
            }
        });

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
        }
        dday = Date[0].replace("/", "_");//把/換成_


        final double[] synumbern = {0};
        //分辨第一個advice是哪個緩解方式，讓第二個advice不要重複
        final int[] AdviceN = {0};

    //載入心率、血氧、睡眠資料進折線圖
        loadDataFromFirebase(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded(ArrayList<String> heartRatePoints, ArrayList<String> sleepPoints, ArrayList<String> bloodOxygenPoints) {
                ArrayList<Entry> heartRateEntries = points(heartRatePoints);
                ArrayList<Entry> sleepEntries = points(sleepPoints);
                ArrayList<Entry> bloodOxygenEntries = points(bloodOxygenPoints);

                // 初始化圖表資料
                lineChartData.initDataSet(heartRateEntries, sleepEntries, bloodOxygenEntries);

                if (heartRatePoints.size() < 1 || sleepPoints.size() < 1 || bloodOxygenPoints.size() < 1) {
                    // 無資料，顯示無法分析
                    TextView stressnumber = mainview.findViewById(R.id.stressnumber);
                    stressnumber.setTextSize(20);
                    stressnumber.setText("缺少資料無法分析");
                    run = false;
                } else {
                    // 進行壓力指數計算等操作
                    TextView stressnumber = mainview.findViewById(R.id.stressnumber);
                    String getstressn = getStressNumber(heartRatePoints, sleepPoints, bloodOxygenPoints);
                    stressnumber.setText(getstressn);
                    stressN = Double.parseDouble(getstressn);
                }

            }
        });

        databaseReference.child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userid1 = snapshot.getValue(String.class);
                    userid = userid1;
                    saveSymptoms(dday, stressN);

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


                                    //計算症狀次數
                                    if (Rheadache == true) totalheadache += 1;
                                    if (Rdizzy == true) totaldizzy += 1;
                                    if (Rnausea == true) totalnausea += 1;
                                    if (Rtired == true) totaltired += 1;
                                    if (Rstomachache == true) totalstomachache += 1;
                                    int[] smax = {totalheadache, totaldizzy, totalnausea, totaltired, totalstomachache};

                                    //分析症狀資料且更新緩解建議
                                    int max = Arrays.stream(smax).max().getAsInt();
                                    advice = mainview.findViewById(R.id.Advice1);
                                    if (totalheadache == max) {
                                        //跳轉運動介面
                                        advice.setText("  運動");
                                        AdviceN[0] =3;
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

                                    } else if (totalstomachache == max || totalnausea == max) {
                                        //跳轉到冥想
                                        advice.setText("  冥想");
                                        AdviceN[0] =1;
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
                                    } else if (totaldizzy == max || totaltired == max) {
                                        //跳轉呼吸介面
                                        advice.setText("  呼吸緩解");
                                        AdviceN[0] =2;
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

                                    } else {
                                        //跳轉到冥想
                                        advice.setText("  冥想");
                                        AdviceN[0] =1;
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


                                } else {
                                    Log.d("TAG", "No data found for the date: " + rdate);
                                }

                                // 檢查是否已經計算完所有資料
                                if (finalI == rs.length - 1) {

                                    // 在此處更新 synumbern[0] 後再次設置 TextView
                                    TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
                                    double d = stressN;
                                    d += synumbern[0];
                                    double td = d * 10;
                                    int ti = (int) td;
                                    d = (double) ti / 10;
                                    stressnumber.setText(d + "");

                                    //第二個advice
                                    l1= mainview.findViewById(R.id.l1);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT，表示与父容器相同
                                            0 // 设置高度为 200 像素，您可以根据需要调整高度
                                    );
                                    int heightInPx = dpToPx(getContext(), 200);
                                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT，表示与父容器相同
                                            heightInPx // 设置高度为 160 dp，您可以根据需要调整高度
                                    );

                                    if(d>8){
                                        TextView m1t =mainview.findViewById(R.id.Minute1);
                                        m1t.setText("10");
                                        l1.setLayoutParams(layoutParams2);
                                        TextView advice2 =mainview.findViewById(R.id.Advice2);
                                        if(AdviceN[0]==1 ||AdviceN[0]==3 ){
                                            advice2.setText("  呼吸緩解");
                                            int heightInDp = 160; // 设置高度为 100 dp
                                            int heightInPx2 = dpToPx(getContext(), heightInDp);
                                            LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT 或具体数值
                                                    heightInPx2 // 设置高度为 dp 转换后的像素值
                                            );
                                            advice2.setLayoutParams(lp);
                                            Drawable drawable = getResources().getDrawable(R.drawable.breathe_background);
                                            advice2.setBackground(drawable);
                                            advice2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), Breathe.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }else if(AdviceN[0]==2){
                                            advice2.setText("  冥想");
                                            int heightInDp = 160; // 设置高度为 100 dp
                                            int heightInPx2 = dpToPx(getContext(), heightInDp);
                                            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, // 宽度设置为 MATCH_PARENT 或具体数值
                                                    heightInPx2 // 设置高度为 dp 转换后的像素值
                                            );
                                            advice2.setLayoutParams(layoutParams3);
                                            Drawable drawable = getResources().getDrawable(R.drawable.meditation_background);
                                            advice2.setBackground(drawable);
                                            advice2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getActivity(), Meditation.class);
                                                    startActivity(intent);

                                                }
                                            });

                                        }
                                    }else{
                                        l1.setLayoutParams(layoutParams);
                                    }
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


    public void onResume() {
        Log.e(TAG, "心情---onResume");
        super.onResume();
        refreshData(); // 自定义的方法，用于刷新数据和更新视图
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "心情---onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }
}





