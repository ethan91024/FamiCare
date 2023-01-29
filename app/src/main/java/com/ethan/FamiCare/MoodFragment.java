package com.ethan.FamiCare;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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
import java.util.Scanner;

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
    private boolean run = true; //是否能run分析壓力指數
    private View mainview;
    private double HeartRrte =-1;
    private double Sleep = -1;
    private double BloodOxygen = -1;
    private int[] id = {R.id.headache, R.id.dizzy, R.id.nausea, R.id.stomachache, R.id.tired};
    private ArrayList<Entry> HeartRateList =new ArrayList<>();
    private ArrayList<Entry> SleepList =new ArrayList<>();
    private ArrayList<Entry> BloodOxygenList =new ArrayList<>();

    //set linechart
    LineChartData lineChartData;
    LineChart lineChart;
    ArrayList<String> HeartRrtexData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Scanner cin = new Scanner(System.in);
        mainview = inflater.inflate(R.layout.fragment_mood, container, false);
        Button update = mainview.findViewById(R.id.update);

        HeartRrte = 82;
        BloodOxygen = 96;
        Sleep = 5;
        ArrayList<Entry> copy = new ArrayList<>();
        HeartRateList=getHeaetRatepoints();

        if (Sleep == -1 || HeartRrte == -1 || BloodOxygen == -1) {
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            stressnumber.setText("缺少資料無法分析");
            run = false;
        } else {
            if (HeartRrte > 79 && HeartRrte < 89) {
                StressNumber += 2;
            }
            if (BloodOxygen < 98 && BloodOxygen > 95) {
                StressNumber++;
            } else if (BloodOxygen <= 95) {
                StressNumber += 2;
            }
            if (Sleep < 8) {
                StressNumber++;
            } else if (Sleep < 6) {
                StressNumber += 2;
            }
            String sn = StressNumber + "";
            TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
            stressnumber.setText(sn);

        }
        update.setOnClickListener(new View.OnClickListener() {//checkbox 更新壓力指數分析
            @Override
            public void onClick(View view) {
                double plusnumber = updataStress(mainview, run);//勾選症狀的加分
                if (plusnumber < 0) {//如果沒有心率、睡眠、血氧資料，顯示無法分析
                    TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
                    stressnumber.setText("缺少資料無法分析");
                } else {//把症狀勾選的資料加上壓力指數
                    plusnumber += StressNumber;
                    String sn = "" + plusnumber;
                    TextView stressnumber = (TextView) mainview.findViewById(R.id.stressnumber);
                    stressnumber.setText(sn);
                }
            }
        });

        //載入心律、睡眠等資料
        lineChart = mainview.findViewById(R.id.lineChart);
        lineChartData = new LineChartData(lineChart, this.getContext());
        for (int i = 1; i <= 7; i++) {
            HeartRrtexData.add("第" + i + "天");
        }
        lineChartData.initX(HeartRrtexData);
        lineChartData.initY(0F, 10F);
        lineChartData.initY(0F,10F);

            lineChartData.initDataSet(getHeaetRatepoints(),getSleeppoints(),getBloodOxygenpoints());

        lineChartData.initDataSet(getpoints(), getpoints(), getpoints());


        return mainview;
    }

    public double updataStress(View view, boolean run) { //症狀checkbox 加分計算
        double plusnumber = 0;
        if (run) {
            for (int i : id) {
                ckb = (CheckBox) view.findViewById(i);
                if (ckb.isChecked()) {
                    plusnumber+=0.5;
                }
            }
        } else {
            plusnumber = -1;//如果缺乏心率睡眠血氧資料，直接無法分析，回傳-1表示無法run
        }
        return plusnumber;
    }


    private ArrayList<Entry> getHeaetRatepoints() {//製造心率假資料
        ArrayList<Entry> points = new ArrayList<>();
        Random r=new Random();
        for(int i = 0;i<7;i++){
            int num=60+r.nextInt(100-60+1);
            String s=""+num;
            Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));

            points.add(new Entry(i, f));


        }

        return points;
    }
    private ArrayList<Entry> getSleeppoints() {//製造睡眠假資料
        ArrayList<Entry> points = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 7; i++) {
            int num = r.nextInt(11);
            String s = "" + num;
            Float f = Float.parseFloat(s);
        Random r=new Random();
        for(int i = 0;i<7;i++){
            int num=r.nextInt(20);
            String s=""+num;
            Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));

            points.add(new Entry(i, f));


        }

        return points;
    }
    private ArrayList<Entry> getBloodOxygenpoints() {//製造血氧假資料
        ArrayList<Entry> points = new ArrayList<>();
        Random r=new Random();
        for(int i = 0;i<7;i++){
            int num=90+r.nextInt(100-90+1);
            String s=""+num;
            Float f=Float.parseFloat(s);
            //HeartRrtexData.add("第" + i + "天");
            //HeartRrteyData.add(new Entry(i+1,f));

            points.add(new Entry(i, f));


        }

    public class LineChartData {
        return points;
    }

    public class LineChartData{
        Context context;
        LineChart lineChart;

        public LineChartData(LineChart lineChart, Context context) {
            this.context = context;
            this.lineChart = lineChart;
        }

        public void initDataSet(ArrayList<Entry> valuesY1, ArrayList<Entry> valuesY2, ArrayList<Entry> valuesY3) {
            if (valuesY1.size() > 0) {
                LineDataSet set1, set2, set3;
                set1 = new LineDataSet(valuesY1, "heartrate");
                set2 = new LineDataSet(valuesY2, "sleep");
                set3 = new LineDataSet(valuesY3, "blood");

                set1.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set1.setColor(context.getResources().getColor(R.color.colorPrimary));//線的顏
                set1.setCircleColor(context.getResources().getColor(R.color.colorPrimary));//圓點顏色
                set1.setCircleRadius(4);//原點大小
                set1.setDrawCircleHole(false);//圓點為實心(預設空心)
                set1.setLineWidth(1.5f);//線寬
                set1.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set1.setValueTextSize(8);//座標點數字大小


                set2.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set2.setColor(context.getResources().getColor(R.color.colorTheme));//線的顏
                set2.setCircleColor(context.getResources().getColor(R.color.colorTheme));//圓點顏色
                set2.setCircleRadius(4);//原點大小
                set2.setDrawCircleHole(false);//圓點為實心(預設空心)
                set2.setLineWidth(1.5f);//線寬
                set2.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set2.setValueTextSize(8);//座標點數字大小


                set3.setMode((LineDataSet.Mode.LINEAR));///類型為折線
                set3.setColor(context.getResources().getColor(R.color.colorAccent));//線的顏
                set3.setCircleColor(context.getResources().getColor(R.color.colorAccent));//圓點顏色
                set3.setCircleRadius(4);//原點大小
                set3.setDrawCircleHole(false);//圓點為實心(預設空心)
                set3.setLineWidth(1.5f);//線寬
                set3.setDrawValues(true);//顯示座標點對應Y軸的數字(預設顯示)
                set3.setValueTextSize(8);//座標點數字大小

                Legend legend = lineChart.getLegend();
                legend.setEnabled(false);//不顯示圖例 (預設顯示)
                Description description = lineChart.getDescription();
                description.setEnabled(false);//不顯示Description Label (預設顯示)


                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                dataSets.add(set2);
                dataSets.add(set3);

                LineData data = new LineData(dataSets);
                lineChart.setData(data);//一定要放在最後
            } else {
                lineChart.setNoDataText("暫時沒有數據");
                lineChart.setNoDataTextColor(Color.BLUE);//文字顏色
            }
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
}


