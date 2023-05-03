package com.ethan.FamiCare.Health


import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter


class HealthStepsActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
    val startOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
    val endOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_steps)
        val barChart: BarChart = findViewById(R.id.bar_chart)
        val client = HealthConnectClient.getOrCreate(this)



        lifecycleScope.launch {
            val dailysteps = getDailyStepCounts(client)
            if (dailysteps.isEmpty()) {
                Toast.makeText(
                    this@HealthStepsActivity,
                    "Don't Have Data!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // 計算一天有多少個時間點需要顯示在 X 軸上
            val numXAxisLabels = 24

            // 初始化時間點計數器，用於計算每個時間點上的步數計數值
            val stepCountsByHour = MutableList(numXAxisLabels) { 0 }

            // 將步數資料的時間點轉換為對應的時間點索引，並放置到對應的時間點索引中
            dailysteps.forEach { step ->
                val localDateTime = step.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    stepCountsByHour[hour] += step.count.toInt()
                }
            }

            //找最大步數
            val maxstep = stepCountsByHour.toIntArray().max()
            val top =(maxstep/1000+1)*1000

            // 創建BarEntry對象，用於指定每一個小間隔中的數據值
            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                entries.add(BarEntry(i.toFloat(), stepCountsByHour[i].toFloat()))
            }

            // 創建BarDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = BarDataSet(entries, "步數")
            barChart.description.isEnabled = false


            // 設置柱狀圖的顏色
            dataSet.color = Color.BLUE


            // 創建BarData對象，用於將BarDataSet對象添加到柱狀圖中
            val data = BarData(dataSet)

            val yAxis = barChart.axisRight
            val yAxisLeft: YAxis = barChart.axisLeft
            yAxis.setDrawAxisLine(true)
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMinimum = 0f
            yAxis.axisMaximum = top.toFloat()
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.labelCount = 5
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }



            val xAxis = barChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(true)
            xAxis.setGranularityEnabled(true)
            xAxis.labelCount = 24
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

            val leftAxis = barChart.getAxisLeft();
            val rightAxis = barChart.getAxisRight();
            leftAxis.setAxisMinimum(0f);
            rightAxis.setAxisMinimum(0f);

            barChart.setData(data)
            barChart.setFitBars(true)

            barChart.axisRight.isGranularityEnabled = true
            barChart.axisRight.granularity = 1f
            barChart.data.barWidth = 0.7f

            barChart.invalidate()

        }
    }


    suspend fun getDailyStepCounts(
        client: HealthConnectClient,
        start: LocalDateTime = LocalDateTime.now().with(LocalTime.MIN),
        end: LocalDateTime = LocalDateTime.now().with(LocalTime.MAX)
    ): List<StepsRecord> {
        val request = client.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateStepsIntoDays(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): Long? {
        var totalSteps: Long? = 0
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofDays(1)
                    )
                )
            for (dailyResult in response) {
                totalSteps = dailyResult.result[StepsRecord.COUNT_TOTAL]
            }
        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
        return totalSteps
    }

    suspend fun aggregateStepsIntoWeeks(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ) {
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofWeeks(1)
                    )
                )
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (weeklyResult in response) {
                val totalSteps = weeklyResult.result[StepsRecord.COUNT_TOTAL]

            }

        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    suspend fun aggregateStepsIntoMonths(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ) {
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofMonths(1)
                    )
                )
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (monthlyResult in response) {
                val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]

            }
        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}