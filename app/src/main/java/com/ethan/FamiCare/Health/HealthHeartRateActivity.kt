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
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class HealthHeartRateActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingDayData = true
    var showingWeekData = false
    var showingMonthData = false
    var showingDay14Data = false
    var limitLine: LimitLine? = null
    lateinit var client: HealthConnectClient
    lateinit var HR: List<HeartRateRecord>
    lateinit var lineChart: LineChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_heart_rate)

        val locale = Locale("zh", "CN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        client = HealthConnectClient.getOrCreate(this)
        lineChart = findViewById(R.id.line_chart)
        val calendar = findViewById<ImageView>(R.id.calendarIV)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)
        val dayBtn = findViewById<Button>(R.id.dayBtn)
        val weekBtn = findViewById<Button>(R.id.weekBtn)
        val monthBtn = findViewById<Button>(R.id.monthBtn)
        val day14Btn = findViewById<Button>(R.id.day14Btn)
        val intervalTextView: TextView = findViewById(R.id.timeTF)

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry, highlight: Highlight) {
                if (showingDayData) {
                    val startHour = entry.x.toInt()
                    val endHour = startHour + 1
                    val interval =
                        String.format(Locale.getDefault(), "%02d:00-%02d:00", startHour, endHour)
                    intervalTextView.text = interval
                }
            }

            override fun onNothingSelected() {
                // 如果沒有值被選取時的程式碼
                intervalTextView.text = ""
            }
        })

        calendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val selectedDate = LocalDate.of(year, month + 1, day)
                    val selectedDateTime = selectedDate.atStartOfDay()
                    currentDisplayedDate = selectedDateTime
                    intervalTextView.text = null
                    updateChart()
                }, year, month, day
            )

            datePickerDialog.show()
        }

        beforeBtn.setOnClickListener {
            if (showingWeekData) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(1)
            }else if(showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.minusMonths(1)
            }else if(showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(2)
            }else{
                currentDisplayedDate = currentDisplayedDate.minusDays(1)
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
            }else{
                currentDisplayedDate = currentDisplayedDate.plusDays(1)
            }
            updateChart()
        }

        dayBtn.setOnClickListener {
            showingDayData = true
            showingWeekData = false
            showingMonthData = false
            showingDay14Data = false
            updateChartForDay()
        }

        weekBtn.setOnClickListener {
            showingDayData = false
            showingWeekData = true
            showingMonthData = false
            showingDay14Data = false
            intervalTextView.text = null
            updateChartForWeek()
        }
        monthBtn.setOnClickListener {
            showingDayData = false
            showingWeekData = false
            showingMonthData = true
            showingDay14Data = false
            intervalTextView.text = null
            updateChartForMonth()
        }

        day14Btn.setOnClickListener {
            showingDayData = false
            showingWeekData = false
            showingMonthData = false
            showingDay14Data = true
            intervalTextView.text = null
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
        }else {
            updateChartForDay()
        }
    }


    private fun updateChartForDay() {
        lifecycleScope.launch {
            HR = getDailyHRCounts()
            if (HR.isEmpty()) {

            }
            if (limitLine != null) {
                val yAxis: YAxis = lineChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
            }
            val numXAxisLabels = 24
            val hrCountsByHour = MutableList(numXAxisLabels) { 0 }

            HR.forEach { hr ->
                val localDateTime = hr.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    hrCountsByHour[hour] += hr.samples[0].beatsPerMinute.toInt()
                }
            }

            val maxhr = hrCountsByHour.toIntArray().max()
            val top = (maxhr / 100 + 1) * 100

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (hrCountsByHour[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), hrCountsByHour[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "心率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
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

            val date = findViewById<TextView>(R.id.dateText)
            date.text = currentDisplayedDate.format(myDateTimeFormatter)

            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f
            lineChart.description.isEnabled = false
            lineChart.data = data
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f

            val aggregateStepsToday = aggregation(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )
            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if(aggregateStepsToday == null|| HR.isEmpty()){
                average.text="0.0"
            }else {
                average.text = String.format("%.2f", aggregateStepsToday / HR.count())
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", aggregateStepsToday / HR.count())
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            lineChart.invalidate()
        }
    }

    fun updateChartForWeek() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
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
            val HR = aggregateHRIntoWeeks(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val HRCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRCountsByDay[index] = hr.toInt() // 將資料填入對應位置
                }
            }

            val maxhr = HRCountsByDay.toIntArray().max()
            val top = (maxhr / 100 + 1) * 100

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRCountsByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "心率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
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


            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (HR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
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
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val HR = aggregateHRIntoMonths(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX),
                currentDisplayedDate.month.length(false)
            )
            if (HR.isEmpty()) {
                // 資料為空的處理邏輯
            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val HRCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRCountsByDay[index] = hr.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxhr = HRCountsByDay.toIntArray().max()
            val top = (maxhr / 100 + 1) * 100

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRCountsByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "心率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
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

            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (HR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
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
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(13)
            val endDate =
                currentDisplayedDate
            val HR = aggregateHRInto14Days(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 14  // 修改為七筆資料
            val HRCountsByDay14 = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRCountsByDay14[index] = hr.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxhr = HRCountsByDay14.toIntArray().max()
            val top = (maxhr / 100 + 1) * 100

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRCountsByDay14[i] >0) {
                    entries.add(Entry(entries.size.toFloat(), HRCountsByDay14[i].toFloat()))
                } else {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                }
            }

            val dataSet = LineDataSet(entries, "心率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
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

            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (HR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", HR.sum().toDouble() / HR.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            lineChart.invalidate()
        }
    }

    suspend fun aggregation(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.samples[0].beatsPerMinute.toDouble()
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }
    suspend fun getDailyHRCounts(//一天24筆的資料
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<HeartRateRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {


            throw exception
        }
    }


    suspend fun aggregateHRIntoWeeks(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Long> {
        val totalHRList = MutableList(7) { 0L } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                totalHRList[dayOfWeek - 1] = dailyResult.result[HeartRateRecord.BPM_AVG] ?: 0L
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalHRList
    }

    suspend fun aggregateHRIntoMonths(
        start: LocalDateTime,
        end: LocalDateTime,
        length: Int
    ): List<Long> {
        val totalHRList = MutableList(length) { 0L } // 建立一個初始值為0的length個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfMonth = localDateTime.dayOfMonth
                totalHRList[dayOfMonth - 1] = dailyResult.result[HeartRateRecord.BPM_AVG] ?: 0L
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalHRList
    }

    suspend fun aggregateHRInto14Days(
        start: LocalDateTime,
        end: LocalDateTime,
    ): MutableList<Long> {
        val totalHRList = MutableList(14) { 0L }

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (i in 0 until 14) {
                val currentDate = start.plusDays(i.toLong()).toLocalDate()
                val dailyResult = response.find { it.startTime.toLocalDate() == currentDate }
                totalHRList[i] = dailyResult?.result?.get(HeartRateRecord.BPM_AVG) ?: 0L
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalHRList
    }
}