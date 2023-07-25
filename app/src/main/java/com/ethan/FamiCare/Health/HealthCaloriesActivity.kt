package com.ethan.FamiCare.Health


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.calories
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
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
import kotlin.collections.ArrayList


class HealthCaloriesActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingDayData = true
    var showingWeekData = false
    var showingMonthData = false
    var showingDay14Data = false
    var showingWeek14Data = false
    lateinit var client: HealthConnectClient
    lateinit var calories: List<TotalCaloriesBurnedRecord>
    lateinit var barChart: BarChart


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_calories)

        client = HealthConnectClient.getOrCreate(this)
        barChart = findViewById(R.id.bar_chart)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)
        val dayBtn = findViewById<Button>(R.id.dayBtn)
        val weekBtn = findViewById<Button>(R.id.weekBtn)
        val monthBtn = findViewById<Button>(R.id.monthBtn)
        val day14Btn = findViewById<Button>(R.id.day14Btn)
//        val week14Btn = findViewById<Button>(R.id.week14Btn)
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


        beforeBtn.setOnClickListener {
            if (showingWeekData) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(1)
            } else if (showingMonthData) {
                currentDisplayedDate = currentDisplayedDate.minusMonths(1)
            } else if (showingDay14Data) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(2)
            } else if (showingWeek14Data) {
                currentDisplayedDate = currentDisplayedDate.minusWeeks(14)
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
            } else if (showingWeek14Data) {
                currentDisplayedDate = currentDisplayedDate.plusWeeks(14)
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
            showingWeek14Data = false
            updateChartForDay()
        }

        weekBtn.setOnClickListener {
            showingDayData = false
            showingWeekData = true
            showingMonthData = false
            showingDay14Data = false
            showingWeek14Data = false
            intervalTextView.text = null
            updateChartForWeek()
        }
        monthBtn.setOnClickListener {
            showingDayData = false
            showingWeekData = false
            showingMonthData = true
            showingDay14Data = false
            showingWeek14Data = false
            intervalTextView.text = null
            updateChartForMonth()
        }

        day14Btn.setOnClickListener {
            showingDayData = false
            showingWeekData = false
            showingMonthData = false
            showingDay14Data = true
            showingWeek14Data = false
            intervalTextView.text = null
            updateChartForDay14()
        }
//        week14Btn.setOnClickListener {
//            showingDayData = false
//            showingWeekData = false
//            showingMonthData = false
//            showingDay14Data = false
//            showingWeek14Data = true
//            intervalTextView.text = null
//            updateChartForWeek14()
//        }

        updateChart()
    }

    private fun updateChart() {
        if (showingWeekData) {
            updateChartForWeek()
        } else if (showingMonthData) {
            updateChartForMonth()
        } else if (showingDay14Data) {
            updateChartForDay14()
        } else if (showingWeek14Data) {
            updateChartForWeek14()
        } else {
            updateChartForDay()
        }
    }


    private fun updateChartForDay() {
        lifecycleScope.launch {
            calories = getDailyCaloriesCounts(client)
            if (calories.isEmpty()) {

            }

            val numXAxisLabels = 24
            val caloriesCountsByHour = MutableList(numXAxisLabels) { 0 }

            calories.forEach { calorie ->
                val localDateTime = calorie.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    caloriesCountsByHour[hour] += calorie.energy.inKilocalories.toInt()
                }
            }

            val maxcal = caloriesCountsByHour.toIntArray().max()
            val top = (maxcal!! / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (caloriesCountsByHour[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), caloriesCountsByHour[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里")
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

            val aggregateCaloriesToday = aggregateCaloriesIntoDays(
                client,
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )
            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            var number: Long = 0
            for (i in aggregateCaloriesToday.indices) {
                number += aggregateCaloriesToday[i]
            }
            average.text = number.toString()
            avgText.text = "總計:"
            barChart.invalidate()
        }
    }

    private fun updateChartForWeek() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val cal = aggregateCaloriesIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (cal.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val calCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            cal.forEachIndexed { index, cals ->  // 使用 forEachIndexed 迴圈
                if (cals != null) {
                    calCountsByDay[index] = cals.toInt()  // 將資料填入對應位置
                }
            }

            val maxcal = calCountsByDay.toIntArray().max()
            val top = (maxcal / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (calCountsByDay[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), calCountsByDay[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里")
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
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.granularity = 1f
            xAxis.labelCount = numXAxisLabels
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val label: String = when (value.toInt()) {
                        0 -> "一"
                        1 -> "二"
                        2 -> "三"
                        3 -> "四"
                        4 -> "五"
                        5 -> "六"
                        6 -> "日"
                        else -> ""
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
            if (cal.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", cal.sum().toDouble() / cal.count { it > 0 })
            }
            avgText.text = "平均:"
            barChart.invalidate()
        }
    }

    private fun updateChartForMonth() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val cal = aggregateCaloriesIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (cal.isEmpty()) {
                // 資料為空的處理邏輯
            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val calCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            cal.forEachIndexed { index, cals ->  // 使用 forEachIndexed 迴圈
                if (cals != null) {
                    calCountsByDay[index] = cals.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxcal = calCountsByDay.toIntArray().max()
            val top = (maxcal / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (calCountsByDay[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), calCountsByDay[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里")
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
            if (cal.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", cal.sum().toDouble() / cal.count { it > 0 })
            }
            avgText.text = "平均:"
            barChart.invalidate()
        }
    }

    //x軸日期暫時無法
    private fun updateChartForDay14() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(13)
            val endDate =
                currentDisplayedDate
            val cal = aggregateCaloriesIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (cal.isEmpty()) {

            }

            val numXAxisLabels = 14  // 修改為七筆資料
            val calCountsByDay14 = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            cal.forEachIndexed { index, cals ->  // 使用 forEachIndexed 迴圈
                if (cals != null) {
                    calCountsByDay14[index] = cals.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxcal = calCountsByDay14.toIntArray().max()
            val top = (maxcal / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (calCountsByDay14[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), calCountsByDay14[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里")
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
            if (cal.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", cal.sum().toDouble() / cal.count { it > 0 })
            }
            avgText.text = "平均:"
            barChart.invalidate()
        }
    }

    private fun updateChartForWeek14() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val cal = aggregateCaloriesIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (cal.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val calCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            cal.forEachIndexed { index, cals ->  // 使用 forEachIndexed 迴圈
                if (cals != null) {
                    calCountsByDay[index] = cals.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxcal = calCountsByDay.toIntArray().max()
            val top = (maxcal / 1000 + 1) * 1000

            val entries: MutableList<BarEntry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (calCountsByDay[i] == 0) {
                    entries.add(BarEntry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(BarEntry(entries.size.toFloat(), calCountsByDay[i].toFloat()))
                }
            }

            val dataSet = BarDataSet(entries, "卡路里")
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
            if (cal.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", cal.sum().toDouble() / cal.count { it > 0 })
            }
            avgText.text = "平均:"
            barChart.invalidate()
        }
    }

    suspend fun getDailyCaloriesCounts(//一天24筆的資料
        client: HealthConnectClient,
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

    suspend fun aggregateCaloriesIntoDays(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Long> {
        val totalCaloriesList = MutableList(7) { 0L } // 建立一個初始值為0的7個元素的陣列

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
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                totalCaloriesList[dayOfWeek - 1] =
                    (dailyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL] ?: 0L) as Long
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalCaloriesList
    }
}



