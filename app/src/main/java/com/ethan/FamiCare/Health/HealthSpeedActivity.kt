package com.ethan.FamiCare.Health

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.max
import android.util.Half.toFloat
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.platform.client.proto.DataProto
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

class HealthSpeedActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_speed)
        val client = HealthConnectClient.getOrCreate(this)
        val ScatterChart: ScatterChart = findViewById(R.id.scatter_chart)

        lifecycleScope.launch {
            val Speed = getSpeed(client)
            if (Speed.isEmpty()) {
                Toast.makeText(
                    this@HealthSpeedActivity,
                    "Don't Have Data!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // 計算一天有多少個時間點需要顯示在 X 軸上
            val numXAxisLabels = 24

            // 初始化時間點計數器，用於計算每個時間點上的速度計數值
            val speedsByHour = MutableList(numXAxisLabels) { 0f }

            // 將速度資料的時間點轉換為對應的時間點索引，並放置到對應的時間點索引中
            Speed.forEach { speed ->
                val localDateTime = speed.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    speedsByHour[hour] = speed.samples.get(0).speed.inMilesPerHour.toFloat()
                }
            }

            //找最大速度
            val maxspeed = speedsByHour.toFloatArray().max()
            val top = maxspeed*10/10 + 1

            // 創建 Entry 對象，用於指定每一個小間隔中的數據值
            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                entries.add(Entry(i.toFloat(), speedsByHour[i]))
            }

            // 創建 ScatterDataSet 對象，用於設置燭台圖的樣式和顏色
            val dataSet = ScatterDataSet(entries, "速度(公里/小時)")
            dataSet.color = Color.GREEN

            // 創建 ScatterData 對象，用於將 CandleDataSet 對象添加到燭台圖中
            val data = ScatterData(dataSet)

            // 設置Scatter Chart的X軸
            val xAxis = ScatterChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.setGranularityEnabled(true)
            xAxis.labelCount = numXAxisLabels
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.granularity = 1f
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val hour = value.toInt()
                    val label: String
                    when (hour) {
                        0 -> label = "12AM"
                        6 -> label = "6AM"
                        12 -> label = "12PM"
                        18 -> label = "6PM"
                        else -> label = ""
                    }
                    return label
                }
            }

            // 設置Scatter Chart的Y軸
            val yAxis = ScatterChart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = top
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.labelCount = 4
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            val yAxisLeft: YAxis = ScatterChart.axisLeft
            yAxisLeft.isEnabled = false

            // 設置Scatter Chart的其他屬性
            ScatterChart.description.isEnabled = false

            val leftAxis = ScatterChart.axisLeft;
            val rightAxis = ScatterChart.axisRight;
            leftAxis.axisMinimum = 0f;
            rightAxis.axisMinimum = 0f;

            ScatterChart.data = data
            ScatterChart.axisRight.isGranularityEnabled = true
            ScatterChart.axisRight.granularity = 1f
            ScatterChart.invalidate()
        }


    }

    suspend fun getSpeed(
        client: HealthConnectClient,
        start: LocalDateTime = LocalDateTime.now().with(LocalTime.MIN),
        end: LocalDateTime = LocalDateTime.now().with(LocalTime.MAX)
    ): List<SpeedRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateSpeedIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SpeedRecord.SPEED_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalSpeed = weeklyResult.result[SpeedRecord.SPEED_AVG]
        }
    }

    suspend fun aggregateSpeedIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SpeedRecord.SPEED_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalSpeed = monthlyResult.result[SpeedRecord.SPEED_AVG]
        }
    }
}

