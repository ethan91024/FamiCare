package com.ethan.FamiCare.Health

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

class HealthHeartRateActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_heart_rate)
        val client = HealthConnectClient.getOrCreate(this)
        val lineChart: LineChart = findViewById(R.id.line_chart)
        lifecycleScope.launch {
            val HR = getHeartRate(client)
            if (HR.isEmpty()) {
                Toast.makeText(
                    this@HealthHeartRateActivity,
                    "Don't Have Data!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // 計算一天有多少個時間點需要顯示在 X 軸上
            val numXAxisLabels = 24

            // 初始化時間點計數器，用於計算每個時間點上的步數計數值
            val BPMAvgByHour = MutableList(numXAxisLabels) { 0 }

            // 將步數資料的時間點轉換為對應的時間點索引，並放置到對應的時間點索引中
            HR.forEach { bpm ->
                val localDateTime = bpm.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour >= 0 && hour < numXAxisLabels) {
                    BPMAvgByHour[hour] += bpm.samples.get(0).beatsPerMinute.toInt()
                }
            }

            //找最大步數
            val maxBPMAvg = BPMAvgByHour.toIntArray().max()
            val top =(maxBPMAvg/10+1)*10

            // 創建Entry對象，用於指定每一個小間隔中的數據值
            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                entries.add(Entry(i.toFloat(), BPMAvgByHour[i].toFloat()))
            }

            // 創建LineDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = LineDataSet(entries, "心率")
            lineChart.description.isEnabled = false

            // 設置折線圖的顏色
            dataSet.color = Color.RED
            dataSet.setDrawCircles(false)
            dataSet.setDrawValues(false)
            dataSet.mode = LineDataSet.Mode.LINEAR
            dataSet.lineWidth = 2f

            // 創建LineData對象，用於將LineDataSet對象添加到柱狀圖中
            val data = LineData(dataSet)

            // 設定 Y 軸
            val yAxisLeft: YAxis = lineChart.axisLeft
            val yAxis = lineChart.axisRight
            yAxisLeft.isEnabled = false
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = top.toFloat()
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)

            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.setGranularityEnabled(true)
            xAxis.labelCount = 24
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "12AM"
                        6 -> "6AM"
                        12 -> "12PM"
                        18 -> "6PM"
                        24 -> "12AM"
                        else -> ""
                    }
                }
            }

            // 設定圖表樣式Z
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false

            val leftAxis = lineChart.axisLeft;
            val rightAxis = lineChart.axisRight;
            leftAxis.axisMinimum = 0f;
            rightAxis.axisMinimum = 0f;

            lineChart.data = data
            lineChart.xAxis.setCenterAxisLabels(true)
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f
            lineChart.invalidate()
        }


    }

    suspend fun getHeartRate(
        client: HealthConnectClient,
        start: LocalDateTime = LocalDateTime.now().with(LocalTime.MIN),
        end: LocalDateTime = LocalDateTime.now().with(LocalTime.MAX)
    ): List<HeartRateRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateHeartRateIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalHR = weeklyResult.result[HeartRateRecord.BPM_AVG]
        }
    }

    suspend fun aggregateHeartRateIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalHR = monthlyResult.result[HeartRateRecord.BPM_AVG]
        }
    }
}