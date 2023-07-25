package com.ethan.FamiCare.Health

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class HealthCaloriesActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingDayData = true
    var showingWeekData = false
    var showingMonthData = false
    var showingDay14Data = false
    var limitLine: LimitLine? = null
    lateinit var client: HealthConnectClient
    lateinit var barChart: BarChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_calories)

        val locale = Locale("zh", "CN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        client = HealthConnectClient.getOrCreate(this)
        barChart = findViewById(R.id.bar_chart)
        val calendar = findViewById<ImageView>(R.id.calendarIV)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)
        val dayBtn = findViewById<Button>(R.id.dayBtn)
        val weekBtn = findViewById<Button>(R.id.weekBtn)
        val monthBtn = findViewById<Button>(R.id.monthBtn)
        val day14Btn = findViewById<Button>(R.id.day14Btn)
        val intervalTextView: TextView = findViewById(R.id.timeTF)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
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
            } else if (showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.minusMonths(1)
            } else if (showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(2)
            } else {
                currentDisplayedDate = currentDisplayedDate.minusDays(1)
            }
            updateChart()
        }

        afterBtn.setOnClickListener {
            if (showingWeekData) {
                currentDisplayedDate = currentDisplayedDate.plusWeeks(1)
            } else if (showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.plusMonths(1)
            } else if (showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.plusWeeks(2)
            } else {
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
        } else if (showingMonthData) {
            updateChartForMonth()
        } else if (showingDay14Data) {
            updateChartForDay14()
        } else {
            updateChartForDay()
        }
    }

    private fun updateChartForDay() {
        lifecycleScope.launch {
            val Calories = getDailyCaloriesCounts()
            if (Calories.isEmpty()) {

            }
            if (limitLine != null) {
                val yAxis: YAxis = barChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
            }
            val numXAxisLabels = 24
            val CaloriesCountsByHour = MutableList(numXAxisLabels) { 0 }

            Calories.forEach { Calories ->
                val localDateTime = Calories.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    CaloriesCountsByHour[hour] += Calories.energy.inKilocalories.toInt()
                }
            }

            val maxCalories = CaloriesCountsByHour.toIntArray().max()
            val top = (maxCalories!! / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (CaloriesCountsByHour[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), CaloriesCountsByHour[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里(大卡)")
            val data = BarData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

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

            val date = findViewById<TextView>(R.id.dateText)
            date.text = currentDisplayedDate.format(myDateTimeFormatter)

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

            val aggregateCaloriesToday = aggregation(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )
            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            average.text = aggregateCaloriesToday.toString()
            avgText.text = "總計:"

            val limitValue = String.format("%.2f", aggregateCaloriesToday / Calories.count())
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            barChart.invalidate()
        }
    }

    fun updateChartForWeek() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            if (limitLine != null) {
                val yAxis: YAxis = barChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
            }
            val startDate =
                currentDisplayedDate.minusDays((currentDisplayedDate.dayOfWeek.value.toLong() - 1))
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val Calories = aggregateCaloriesIntoWeeks(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (Calories.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val CaloriesCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            Calories.forEachIndexed { index, Calories ->  // 使用 forEachIndexed 迴圈
                if (Calories != null) {
                    CaloriesCountsByDay[index] = Calories.toInt()  // 將卡路里(大卡)資料填入對應位置
                }
            }

            val maxCalories = CaloriesCountsByDay.toIntArray().max()
            val top = (maxCalories / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (CaloriesCountsByDay[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), CaloriesCountsByDay[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里(大卡)")
            val data = BarData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

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

            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Calories.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            barChart.invalidate()
        }
    }

    fun updateChartForMonth() {
        if (limitLine != null) {
            val yAxis: YAxis = barChart.axisRight
            yAxis.removeLimitLine(limitLine)
            limitLine = null
        }
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val Calories = aggregateCaloriesIntoMonths(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX),
                currentDisplayedDate.month.length(false)
            )
            if (Calories.isEmpty()) {
                // 資料為空的處理邏輯
            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val CaloriesCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            Calories.forEachIndexed { index, Calories ->  // 使用 forEachIndexed 迴圈
                if (Calories != null) {
                    CaloriesCountsByDay[index] = Calories.toInt()  // 將卡路里(大卡)資料填入對應位置
                }
            }

            val maxCalories = CaloriesCountsByDay.toIntArray().max()
            val top = (maxCalories / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (CaloriesCountsByDay[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), CaloriesCountsByDay[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里(大卡)")
            val data = BarData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

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

            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Calories.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            barChart.invalidate()
        }
    }

    //x軸日期暫時無法
    fun updateChartForDay14() {
        if (limitLine != null) {
            val yAxis: YAxis = barChart.axisRight
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
            val Calories = aggregateCaloriesInto14Days(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (Calories.isEmpty()) {

            }

            val numXAxisLabels = 14  // 修改為七筆資料
            val CaloriesCountsByDay14 = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            Calories.forEachIndexed { index, Calories ->  // 使用 forEachIndexed 迴圈
                if (Calories != null) {
                    CaloriesCountsByDay14[index] = Calories.toInt()  // 將卡路里(大卡)資料填入對應位置
                }
            }

            val maxCalories = CaloriesCountsByDay14.toIntArray().max()
            val top = (maxCalories / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (CaloriesCountsByDay14[i] >0) {
                    entries.add(BarEntry(entries.size.toFloat(), CaloriesCountsByDay14[i].toFloat()))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里(大卡)")
            val data = BarData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)
            dataSet.valueTextSize = 10f

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

            intervalTextView.text = ""

            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            if (Calories.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", Calories.sum().toDouble() / Calories.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            barChart.invalidate()
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
                    TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.energy.inKilocalories
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    suspend fun getDailyCaloriesCounts(//一天24筆的資料
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<TotalCaloriesBurnedRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {

            throw exception
        }
    }

    suspend fun aggregateCaloriesIntoWeeks(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Double> {
        val totalCaloriesList = MutableList(7) { 0.0 } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime = dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                val total = dailyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL].toString().replaceFirst(" kcal","").toDouble()
                val total1 = String.format("%.2f", total).toDouble()
                totalCaloriesList[dayOfWeek - 1] = total1
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalCaloriesList
    }

    suspend fun aggregateCaloriesIntoMonths(
        start: LocalDateTime,
        end: LocalDateTime,
        length: Int
    ): List<Double> {
        val totalCaloriesList = MutableList(length) { 0.0 } // 建立一個初始值為0的length個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfMonth = localDateTime.dayOfMonth
                totalCaloriesList[dayOfMonth - 1] =
                    (dailyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories) as Double
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalCaloriesList
    }

    suspend fun aggregateCaloriesInto14Days(
        start: LocalDateTime,
        end: LocalDateTime,
    ): MutableList<Double> {
        val totalCaloriesList = MutableList(14) { 0.0 }

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (i in 0 until 14) {
                val currentDate = start.plusDays(i.toLong()).toLocalDate()
                val dailyResult = response.find { it.startTime.toLocalDate() == currentDate }
                totalCaloriesList[i] =
                    (dailyResult?.result?.get(TotalCaloriesBurnedRecord.ENERGY_TOTAL)?.inKilocalories) as Double
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalCaloriesList
    }
}


