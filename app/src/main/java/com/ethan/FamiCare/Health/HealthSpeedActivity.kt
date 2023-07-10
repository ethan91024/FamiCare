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
import androidx.health.connect.client.records.SpeedRecord
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

class HealthSpeedActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    val startOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
    val endOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingDayData = true
    var showingWeekData = false
    var showingMonthData = false
    var showingDay14Data = false
    var showingWeek14Data = false
    lateinit var client: HealthConnectClient
    lateinit var HR: List<SpeedRecord>
    lateinit var lineChart: LineChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_heart_rate)
        client = HealthConnectClient.getOrCreate(this)
        lineChart = findViewById(R.id.line_chart)
        val beforeBtn = findViewById<Button>(R.id.beforeBtn)
        val afterBtn = findViewById<Button>(R.id.afterBtn)
        val dayBtn = findViewById<Button>(R.id.dayBtn)
        val weekBtn = findViewById<Button>(R.id.weekBtn)
        val monthBtn = findViewById<Button>(R.id.monthBtn)
        val day14Btn = findViewById<Button>(R.id.day14Btn)
        val week14Btn = findViewById<Button>(R.id.week14Btn)
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
        week14Btn.setOnClickListener {
            showingDayData = false
            showingWeekData = false
            showingMonthData = false
            showingDay14Data = false
            showingWeek14Data = true
            intervalTextView.text = null
            updateChartForWeek14()
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
        } else if (showingWeek14Data) {
            updateChartForWeek14()
        } else {
            updateChartForDay()
        }
    }


    private fun updateChartForDay() {
        lifecycleScope.launch {
            HR = getDailyHR(client)
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 24
            val HRByHour = MutableList(numXAxisLabels) { 0 }

            HR.forEach { hr ->
                val localDateTime = hr.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    HRByHour[hour] += hr.samples[0].speed.inKilometersPerHour.toInt()
                }
            }

            val maxBPMAvg = HRByHour.toIntArray().max()
            val top = (maxBPMAvg / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRByHour[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRByHour[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "速度")
            val data = LineData(dataSet)

            dataSet.color = Color.RED
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(true)

            // 設定 Y 軸
            val yAxisLeft: YAxis = lineChart.axisLeft
            val yAxis = lineChart.axisRight
            yAxis.axisMinimum = 0f
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMaximum = top.toFloat()
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }


            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
            xAxis.labelCount = numXAxisLabels
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

            val aggregateHRToday = aggregateHRIntoDays(
                client,
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )
            val average: TextView = findViewById(R.id.averageTF)
            val avgText: TextView = findViewById(R.id.avgTV)
            var number: Long = 0
            for (i in aggregateHRToday.indices) {
                number += aggregateHRToday[i]
            }
            average.text = (number.div(aggregateHRToday.size)).toString()
            avgText.text = "平均:"
            lineChart.invalidate()
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
            val HR = aggregateHRIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val HRByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRByDay[index] = hr.toInt()  // 將資料填入對應位置
                }
            }

            val maxBPMAvg = HRByDay.toIntArray().max()
            val top = (maxBPMAvg / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRByDay[i].toFloat()))
                }
            }

            // 創建LineDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = LineDataSet(entries, "速度")
            lineChart.description.isEnabled = false

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
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
            yAxis.axisMinimum = 0f
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMaximum = top.toFloat()
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }


            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
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


            // 設定圖表樣式
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            lineChart.data = data
            lineChart.xAxis.setCenterAxisLabels(true)
            lineChart.xAxis.axisMinimum = 0.5f
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f
            lineChart.invalidate()
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f

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
            lineChart.invalidate()
        }
    }

    private fun updateChartForMonth() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val HR = aggregateHRIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val HRByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRByDay[index] = hr.toInt()  // 將資料填入對應位置
                }
            }

            val maxBPMAvg = HRByDay.toIntArray().max()
            val top = (maxBPMAvg / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRByDay[i].toFloat()))
                }
            }

            // 創建LineDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = LineDataSet(entries, "速度")
            lineChart.description.isEnabled = false

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
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
            yAxis.axisMinimum = 0f
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMaximum = top.toFloat()
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }


            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
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
            val startOfWeek =
                currentDisplayedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val endOfWeek =
                currentDisplayedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            date.text = startOfWeek.format(myDateTimeFormatter) + " - " + endOfWeek.format(
                myDateTimeFormatter
            )


            // 設定圖表樣式
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            lineChart.data = data
            lineChart.xAxis.setCenterAxisLabels(true)
            lineChart.xAxis.axisMinimum = 0.5f
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f
            lineChart.invalidate()
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f

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
            lineChart.invalidate()
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
            val HR = aggregateHRIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val HRByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRByDay[index] = hr.toInt()  // 將資料填入對應位置
                }
            }

            val maxBPMAvg = HRByDay.toIntArray().max()
            val top = (maxBPMAvg / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRByDay[i].toFloat()))
                }
            }

            // 創建LineDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = LineDataSet(entries, "速度")
            lineChart.description.isEnabled = false

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
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
            yAxis.axisMinimum = 0f
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMaximum = top.toFloat()
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }


            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
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


            // 設定圖表樣式
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            lineChart.data = data
            lineChart.xAxis.setCenterAxisLabels(true)
            lineChart.xAxis.axisMinimum = 0.5f
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f
            lineChart.invalidate()
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f

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
            lineChart.invalidate()
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
            val HR = aggregateHRIntoDays(
                client,
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (HR.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val HRByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            HR.forEachIndexed { index, hr ->  // 使用 forEachIndexed 迴圈
                if (hr != null) {
                    HRByDay[index] = hr.toInt()  // 資料填入對應位置
                }
            }

            val maxBPMAvg = HRByDay.toIntArray().max()
            val top = (maxBPMAvg / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (HRByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), HRByDay[i].toFloat()))
                }
            }

            // 創建LineDataSet對象，用於設置柱狀圖的樣式和顏色
            val dataSet = LineDataSet(entries, "速度")
            lineChart.description.isEnabled = false

            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
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
            yAxis.axisMinimum = 0f
            yAxis.setLabelCount(4, true)
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            yAxis.setDrawGridLines(true)
            yAxis.setDrawLabels(true)
            yAxis.setDrawAxisLine(true)
            dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            yAxisLeft.isEnabled = false
            yAxis.isEnabled = true
            yAxis.axisMaximum = top.toFloat()
            yAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }


            // 設定 X 軸
            val xAxis: XAxis = lineChart.xAxis

            xAxis.setDrawGridLines(false)
            xAxis.setCenterAxisLabels(false)
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = numXAxisLabels.toFloat() - 0.5f
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


            // 設定圖表樣式
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            val leftAxis = lineChart.axisLeft
            val rightAxis = lineChart.axisRight
            lineChart.data = data
            lineChart.xAxis.setCenterAxisLabels(true)
            lineChart.xAxis.axisMinimum = 0.5f
            lineChart.axisRight.isGranularityEnabled = true
            lineChart.axisRight.granularity = 1f
            lineChart.invalidate()
            leftAxis.axisMinimum = 0f
            rightAxis.axisMinimum = 0f

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
            lineChart.invalidate()
        }
    }

    suspend fun getDailyHR(//一天24筆的資料
        client: HealthConnectClient,
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<SpeedRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    SpeedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {

            throw exception
        }
    }

    suspend fun aggregateHRIntoDays(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Long> {
        val totalHRList = MutableList(7) { 0L } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SpeedRecord.SPEED_AVG),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                totalHRList[dayOfWeek - 1] = (dailyResult.result[SpeedRecord.SPEED_AVG] ?: 0L) as Long
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalHRList
    }
}