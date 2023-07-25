package com.ethan.FamiCare.Health

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class HealthSleepActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingWeekData = true
    var showingMonthData = false
    var showingDay14Data = false
    var limitLine: LimitLine? = null
    lateinit var client: HealthConnectClient
    lateinit var lineChart: LineChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_sleep)

        val locale = Locale("zh", "CN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        client = HealthConnectClient.getOrCreate(this)
        lineChart = findViewById(R.id.line_chart)
        val calendar = findViewById<ImageView>(R.id.calendarIV)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)
        val weekBtn = findViewById<Button>(R.id.weekBtn)
        val monthBtn = findViewById<Button>(R.id.monthBtn)
        val day14Btn = findViewById<Button>(R.id.day14Btn)

        calendar.setOnClickListener {
            val onClickListener = View.OnClickListener { view ->
                val calendar = Calendar.getInstance()
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { _, year, month, day ->
                        val selectedDate = LocalDate.of(year, month + 1, day)
                        val selectedDateTime = selectedDate.atStartOfDay()
                        currentDisplayedDate = selectedDateTime
                        updateChart()
                    }, year, month, day
                )

                datePickerDialog.show()
            }

            calendar.setOnClickListener(onClickListener)
        }

        beforeBtn.setOnClickListener {
            if (showingWeekData) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(1)
            }else if(showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.minusMonths(1)
            }else if(showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(2)
            }
            updateChart()
        }

        afterBtn.setOnClickListener {
            if (showingWeekData) {
                currentDisplayedDate = currentDisplayedDate.plusWeeks(1)
            }else if(showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.plusMonths(1)
            }else if(showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.plusWeeks(2)
            }
            updateChart()
        }

        weekBtn.setOnClickListener {
            showingWeekData = true
            showingMonthData = false
            showingDay14Data = false
            updateChartForWeek()
        }
        monthBtn.setOnClickListener {
            showingWeekData = false
            showingMonthData = true
            showingDay14Data = false
            updateChartForMonth()
        }

        day14Btn.setOnClickListener {
            showingWeekData = false
            showingMonthData = false
            showingDay14Data = true
            updateChartForDay14()
        }
        updateChart()
    }

    private fun updateChart() {
        if (showingWeekData) {
            updateChartForWeek()
        }else if(showingMonthData){
            updateChartForMonth()
        }else if(showingDay14Data){
            updateChartForDay14()
        }
    }


    fun updateChartForWeek() {
        // 更新一星期的資料
        lifecycleScope.launch {
            if (limitLine != null) {
                val yAxis: YAxis = lineChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
            }
            val startDate =
                currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val Sleep = aggregateSleepIntoWeeks(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (Sleep.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val SleepCountsByDay = DoubleArray(numXAxisLabels) { 0.0 }  // 修改變數名稱

            Sleep.forEachIndexed { index, sleep ->  // 使用 forEachIndexed 迴圈
                SleepCountsByDay[index] = sleep
            }

            val maxSleep = SleepCountsByDay.maxOrNull() ?: 0.0
            val top = (maxSleep / 10 + 1) * 10

            val entries: ArrayList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (SleepCountsByDay[i] .equals(0.0)) {
                    entries.add(Entry(i.toFloat(), 0f))
                } else {
                    entries.add(Entry(i.toFloat(), SleepCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "睡眠時長")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.1f", value)
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

            val yAxis = lineChart.axisRight
            val yAxisLeft: YAxis = lineChart.axisLeft
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

            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.granularity = 1f
            xAxis.labelCount = numXAxisLabels
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val dayIndex = value.toInt()
                    val label: String
                    when (dayIndex) {
                        0 -> label = "一"
                        1 -> label = "二"
                        2 -> label = "三"
                        3 -> label = "四"
                        4 -> label = "五"
                        5 -> label = "六"
                        6 -> label = "日"
                        else -> label = ""
                    }
                    return label
                }
            }

            val date = findViewById<TextView>(R.id.dateText)
            val startOfWeek =
                currentDisplayedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek =
                currentDisplayedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            date.text = startOfWeek.format(myDateTimeFormatter) + " - " + endOfWeek.format(
                myDateTimeFormatter
            )

            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f
            lineChart.description.isEnabled = false
            lineChart.data = data
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Sleep.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            lineChart.invalidate()
        }
    }

    fun updateChartForMonth() {
        if (limitLine != null) {
            val yAxis: YAxis = lineChart.axisRight
            yAxis.removeLimitLine(limitLine)
            limitLine = null
        }
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val Sleep = aggregateSleepIntoMonths(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX),
                currentDisplayedDate.month.length(false)
            )
            if (Sleep.isEmpty()) {
                // 資料為空的處理邏輯
            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val SleepCountsByDay = DoubleArray(numXAxisLabels) { 0.0 }  // 修改變數名稱

            Sleep.forEachIndexed { index, sleep ->  // 使用 forEachIndexed 迴圈
                SleepCountsByDay[index] = sleep
            }

            val maxSleep = SleepCountsByDay.maxOrNull() ?: 0.0
            val top = (maxSleep / 10 + 1) * 10

            val entries: ArrayList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (SleepCountsByDay[i] .equals(0.0)) {
                    entries.add(Entry(i.toFloat(), 0f))
                } else {
                    entries.add(Entry(i.toFloat(), SleepCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "睡眠時長")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value.toDouble() == 0.0) {
                        return ""
                    }
                    return String.format("%.1f", value)
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

            val yAxis = lineChart.axisRight
            val yAxisLeft: YAxis = lineChart.axisLeft
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

            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.granularity = 1f
            xAxis.labelCount = numXAxisLabels
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val dayIndex = value.toInt()
                    val label: String
                    when (dayIndex) {
                        0, 6, 13, 20, 27 -> {
                            label = (dayIndex + 1).toString()
                        }
                        else -> {
                            label = ""
                        }
                    }
                    return label
                }
            }

            val date = findViewById<TextView>(R.id.dateText)
            date.text =
                startDate.format(myDateTimeFormatter) + " - " + endDate.format(myDateTimeFormatter)

            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f
            lineChart.description.isEnabled = false
            lineChart.data = data
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Sleep.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            lineChart.invalidate()
        }
    }

    //x軸日期暫時無法
    private fun updateChartForDay14() {
        if (limitLine != null) {
            val yAxis: YAxis = lineChart.axisRight
            yAxis.removeLimitLine(limitLine)
            limitLine = null
        }
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(13)
            val endDate =
                currentDisplayedDate
            val Sleep = aggregateSleepInto14Days(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (Sleep.isEmpty()) {

            }

            val numXAxisLabels = 14  // 修改為七筆資料
            val SleepCountsByDay14 = DoubleArray(numXAxisLabels) { 0.0 }  // 修改變數名稱

            Sleep.forEachIndexed { index, sleep ->  // 使用 forEachIndexed 迴圈
                SleepCountsByDay14[index] = sleep
            }

            val maxSleep = SleepCountsByDay14.maxOrNull() ?: 0.0
            val top = (maxSleep / 10 + 1) * 10

            val entries: ArrayList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (SleepCountsByDay14[i] .equals(0.0)) {
                    entries.add(Entry(i.toFloat(), 0f))
                } else {
                    entries.add(Entry(i.toFloat(), SleepCountsByDay14[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "睡眠時長")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.1f", value)
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f
            
            val yAxis = lineChart.axisRight
            val yAxisLeft: YAxis = lineChart.axisLeft
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

            val xAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.granularity = 1f
            xAxis.labelCount = numXAxisLabels
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val label: String = when (value.toInt()) {
                        0 -> "1"
                        6 -> "7"
                        13 -> "14"
                        else -> ""
                    }
                    return label
                }
            }
            val date = findViewById<TextView>(R.id.dateText)
            date.text = startDate.format(myDateTimeFormatter) + " - " + endDate.format(
                myDateTimeFormatter
            )

            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f
            lineChart.description.isEnabled = false
            lineChart.data = data
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Sleep.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Sleep.sum().toDouble() / Sleep.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            lineChart.invalidate()
        }
    }

    suspend fun aggregateSleepIntoWeeks(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Double> {
        val totalSleepList = MutableList(7) { 0.0 } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                val duration = dailyResult.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]
                val durationInMillis = duration?.toMillis()
                val hoursWithOneDecimal = durationInMillis?.div((1000.0 * 60.0 * 60.0))
                totalSleepList[dayOfWeek - 1] = hoursWithOneDecimal ?: 0.0

            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalSleepList
    }

    suspend fun aggregateSleepIntoMonths(
        start: LocalDateTime,
        end: LocalDateTime,
        length: Int
    ): List<Double> {
        val totalSleepList = MutableList(length) { 0.0 } // 建立一個初始值為0的length個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfMonth = localDateTime.dayOfMonth
                val duration = dailyResult.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]
                val durationInMillis = duration?.toMillis()
                val hoursWithOneDecimal = durationInMillis?.div((1000.0 * 60.0 * 60.0))
                totalSleepList[dayOfMonth - 1] = hoursWithOneDecimal ?: 0.0
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalSleepList
    }

    suspend fun aggregateSleepInto14Days(
        start: LocalDateTime,
        end: LocalDateTime,
    ): MutableList<Double> {
        val totalSleepList = MutableList(14) { 0.0 }

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (i in 0 until 14) {
                val currentDate = start.plusDays(i.toLong()).toLocalDate()
                val dailyResult = response.find { it.startTime.toLocalDate() == currentDate }
                val duration = dailyResult?.result?.get(SleepSessionRecord.SLEEP_DURATION_TOTAL)
                val durationInMillis = duration?.toMillis()
                val hoursWithOneDecimal = durationInMillis?.div((1000.0 * 60.0 * 60.0))
                totalSleepList[i] = hoursWithOneDecimal ?: 0.0
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalSleepList
    }
}