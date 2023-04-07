package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
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

    private TextView advice; //建議的 textview(現在先放冥想)
    private TextView breathe;
   // private Fragment advice_fm[]={new MeditationFragment(),new BreatheFragment(),new sportFragment()}; //各個緩解方式的介面，要做新的就加新的

    private Fragment advice_fm[]={new sportFragment()};
    private TextView sport;//nai


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

        }

        //跳到MoodSymptom
        analize = mainview.findViewById(R.id.analize);
        analize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_layout, new MoodSymptomFragment()).addToBackStack(null).commit();
            }
        });

        double synumber = SymptomCheckBox(run);//勾選症狀的加分
        if (synumber < 0) {
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            stressnumber.setText("缺少資料無法顯示");
        }else{
            synumber+=StressNumber;
            String sn=synumber+"";
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            stressnumber.setText(sn);

        }


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

        //跳轉到緩解方式介面
        advice = mainview.findViewById(R.id.Advice);
        advice.setText("冥想");
        advice.setOnClickListener(new View.OnClickListener() {
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
        sport=mainview.findViewById(R.id.Sport);
        sport.setText("運動");
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Mood_layout,advice_fm[0]).addToBackStack(null).commit();
            }
        });


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

    public double SymptomCheckBox(boolean run) { //症狀checkbox 加分計算
        double plusnumber = 0;
        if (run) {
            Bundle arguments = getArguments();
            if(arguments != null) {
                plusnumber = arguments.getDouble("symptom");
            }else{
                plusnumber=0;
            }
        } else {
            plusnumber = -1;//如果缺乏心率睡眠血氧資料，直接無法分析，回傳-1表示無法run
        }
        return plusnumber;
    }


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
                set1.setColor(context.getResources().getColor(R.color.colorPrimary));//線的顏
                set1.setCircleColor(context.getResources().getColor(R.color.colorPrimary));//圓點顏色
                set1.setCircleRadius(4);//原點大小
                set1.setDrawCircleHole(false);//圓點為實心(預設空心)
                set1.setLineWidth(1.5f);//線寬
                set1.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set1.setValueTextSize(8);//座標點數字大小
                dataSets.add(set1);
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(Color.BLUE);//文字顏色
            }

            if (valuesY2.size() > 0) {
                LineDataSet set2;
                set2 = new LineDataSet(valuesY2, "sleep");
                set2.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set2.setColor(context.getResources().getColor(R.color.colorTheme));//線的顏
                set2.setCircleColor(context.getResources().getColor(R.color.colorTheme));//圓點顏色
                set2.setCircleRadius(4);//原點大小
                set2.setDrawCircleHole(false);//圓點為實心(預設空心)
                set2.setLineWidth(1.5f);//線寬
                set2.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set2.setValueTextSize(8);//座標點數字大小
                dataSets.add(set2);
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(Color.YELLOW);//文字顏色
            }

            if (valuesY3.size() > 0) {
                LineDataSet set3;
                set3 = new LineDataSet(valuesY3, "blood");
                set3.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set3.setColor(context.getResources().getColor(R.color.colorAccent));//線的顏
                set3.setCircleColor(context.getResources().getColor(R.color.colorAccent));//圓點顏色
                set3.setCircleRadius(4);//原點大小
                set3.setDrawCircleHole(false);//圓點為實心(預設空心)
                set3.setLineWidth(1.5f);//線寬
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
            xAxis.setTextColor(Color.GRAY);//X軸標籤顏色
            xAxis.setTextSize(12);//X軸標籤大小

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
            leftAxis.setTextColor(Color.GRAY);//Y軸標籤顏色
            leftAxis.setTextSize(12);//Y軸標籤大小

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
    public void onResume() {
        Log.e(TAG,"心情---onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG,"心情---onPause");
        super.onPause();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG,"心情---onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }
}





