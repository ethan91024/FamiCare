package com.ethan.FamiCare.Health


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class HealthStepsActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
    val startOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
    val endOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
    var currentDisplayedDate = LocalDateTime.now()
    lateinit var client: HealthConnectClient
    lateinit var steps: List<StepsRecord>
    lateinit var barChart: BarChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_steps)

        client = HealthConnectClient.getOrCreate(this)
        barChart = findViewById(R.id.bar_chart)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)

        beforeBtn.setOnClickListener {
            currentDisplayedDate = currentDisplayedDate.minusDays(1)
            updateChart()
        }

        afterBtn.setOnClickListener {
            currentDisplayedDate = currentDisplayedDate.plusDays(1)
            updateChart()
        }

        updateChart()
    }

    private fun updateChart() {
        lifecycleScope.launch {
            steps = getDailyStepCounts(client)
            if (steps.isEmpty()) {
                Toast.makeText(
                    this@HealthStepsActivity,
                    "Don't Have Data!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val numXAxisLabels = 24
            val stepCountsByHour = MutableList(numXAxisLabels) { 0 }

            steps.forEach { step ->
                val localDateTime = step.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    stepCountsByHour[hour] += step.count.toInt()
                }
            }

            val maxstep = stepCountsByHour.toIntArray().max()
            val top = (maxstep!! / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (stepCountsByHour[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), stepCountsByHour[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "步數")
            val data = BarData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)

            val yAxis = barChart.axisRight
            val yAxisLeft: YAxis = barChart.axisLeft
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
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
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
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
                        -1 -> label = ""
                        else -> label = "'"
                    }
                    return label
                }
            }

            val leftAxis = barChart.axisLeft
            val rightAxis = barChart.axisRight
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f
            barChart.description.isEnabled = false
            barChart.data = data
            barChart.setFitBars(true)
            barChart.axisRight.isGranularityEnabled = true
            barChart.axisRight.granularity = 1f
            barChart.data.barWidth = 0.7f

            barChart.invalidate()
        }
    }

    suspend fun getDailyStepCounts(
        client: HealthConnectClient,
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<StepsRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
            throw exception
        }
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