package com.ethan.FamiCare.Health


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.RespiratoryRateRecord
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


class HealthRespiratoryRateActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    var showingDayData = true
    var showingWeekData = false
    var showingMonthData = false
    var showingDay14Data = false
    var limitLine: LimitLine? = null
    var midLine:LimitLine?=null
    private var currentImageIndex = 1
    private lateinit var goodface: ImageView
    private lateinit var wellface: ImageView
    private lateinit var badface: ImageView
    lateinit var client: HealthConnectClient
    lateinit var RR: List<RespiratoryRateRecord>
    lateinit var lineChart: LineChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_respiratory_rate)

        val locale = Locale("zh", "CN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        goodface = findViewById(R.id.goodface)
        wellface = findViewById(R.id.wellface)
        badface = findViewById(R.id.badface)

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
        val clickedTV: TextView=findViewById(R.id.clickedTV)

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry, highlight: Highlight) {
                if (showingDayData) {
                    val startHour = entry.x.toInt()
                    val endHour = startHour + 1
                    val interval =
                        String.format(Locale.getDefault(), "%02d:00-%02d:00", startHour, endHour)
                    intervalTextView.text = interval
                }
                val selectedValue = entry?.y
                clickedTV.text = selectedValue.toString()
            }

            override fun onNothingSelected() {
                intervalTextView.text = ""
                clickedTV.text = ""
            }
        })

        calendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
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

    private fun showImage(index: Int) {
        val tv: TextView=findViewById(R.id.encourageTV)
        wellface.visibility = if (index == 1) {
            tv.text=listOf("冷靜、吸氣、吐氣", "要多放鬆", "記得深呼吸").random()
            ImageView.VISIBLE
        } else {
            ImageView.GONE
        }

        goodface.visibility = if (index == 2) {
            tv.text=listOf("呼吸節奏平穩!", "呼吸正常，狀態良好!", "保持在正常範圍內!").random()
            ImageView.VISIBLE
        } else {
            ImageView.GONE
        }

        wellface.visibility = if (index == 3) {
            tv.text=listOf("呼吸較慢", "記得保持正常節奏", "記得深呼吸").random()
            ImageView.VISIBLE
        } else {
            ImageView.GONE
        }
    }

    private fun updateChartForDay() {
        lifecycleScope.launch {
            RR = getDailyRRCounts()
            if (RR.isEmpty()) {

            }
            if (limitLine != null||midLine!=null) {
                val yAxis: YAxis = lineChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
                val xAxis: XAxis = lineChart.xAxis
                xAxis.removeLimitLine(midLine)
                midLine = null
            }
            val numXAxisLabels = 24
            val RRCountsByHour = MutableList(numXAxisLabels) { 0 }

            RR.forEach { RR ->
                val localDateTime = RR.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val hour = localDateTime.hour
                if (hour in 0 until numXAxisLabels) {
                    RRCountsByHour[hour] += RR.rate.toInt()
                }
            }

            val maxRR = RRCountsByHour.toIntArray().max()
            val top = (maxRR / 10 + 1) * 10

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (RRCountsByHour[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), RRCountsByHour[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "呼吸速率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(false)
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
            if (aggregateStepsToday == null || RR.isEmpty()) {
                average.text = "0.0"
            } else {
                average.text = String.format("%.2f", aggregateStepsToday / RR.count())
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", aggregateStepsToday / RR.count())
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            currentImageIndex = when {
                limitValue.toFloat() >= 20 -> 1
                limitValue.toFloat() >= 12 && limitValue.toFloat() < 20 -> 2
                else -> 3
            }
            showImage(currentImageIndex)

            lineChart.invalidate()
        }
    }

    fun updateChartForWeek() {
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            if (limitLine != null||midLine!=null) {
                val yAxis: YAxis = lineChart.axisRight
                yAxis.removeLimitLine(limitLine)
                limitLine = null
                val xAxis: XAxis = lineChart.xAxis
                xAxis.removeLimitLine(midLine)
                midLine = null
            }
            val startDate =
                currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val RR = aggregateRRIntoWeeks(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (RR.isEmpty()) {

            }

            val numXAxisLabels = 7  // 修改為七筆資料
            val RRCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            RR.forEachIndexed { index, RR ->  // 使用 forEachIndexed 迴圈
                if (RR != null) {
                    RRCountsByDay[index] = RR.toInt() // 將資料填入對應位置
                }
            }

            val maxRR = RRCountsByDay.toIntArray().max()
            val top = (maxRR / 10 + 1) * 10

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (RRCountsByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), RRCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "呼吸速率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(false)
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
            if (RR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            currentImageIndex = when {
                limitValue.toFloat() >= 20 -> 1
                limitValue.toFloat() >= 12 && limitValue.toFloat() < 20 -> 2
                else -> 3
            }
            showImage(currentImageIndex)

            lineChart.invalidate()
        }
    }

    fun updateChartForMonth() {
        if (limitLine != null||midLine!=null) {
            val yAxis: YAxis = lineChart.axisRight
            yAxis.removeLimitLine(limitLine)
            limitLine = null
            val xAxis: XAxis = lineChart.xAxis
            xAxis.removeLimitLine(midLine)
            midLine = null
        }
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一個月的資料
        lifecycleScope.launch {
            val startDate = currentDisplayedDate.withDayOfMonth(1)
            val endDate =
                currentDisplayedDate.withDayOfMonth(currentDisplayedDate.month.length(false))
            val RR = aggregateRRIntoMonths(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX),
                currentDisplayedDate.month.length(false)
            )
            if (RR.isEmpty()) {
                // 資料為空的處理邏輯
            }

            val numXAxisLabels = currentDisplayedDate.month.length(false)  // 修改為該月的天數
            val RRCountsByDay = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            RR.forEachIndexed { index, RR ->  // 使用 forEachIndexed 迴圈
                if (RR != null) {
                    RRCountsByDay[index] = RR.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxRR = RRCountsByDay.toIntArray().max()
            val top = (maxRR / 10 + 1) * 10

            val entries: MutableList<Entry> = java.util.ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (RRCountsByDay[i] == 0) {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                } else {
                    entries.add(Entry(entries.size.toFloat(), RRCountsByDay[i].toFloat()))
                }
            }

            val dataSet = LineDataSet(entries, "呼吸速率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(false)
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
            if (RR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            currentImageIndex = when {
                limitValue.toFloat() >= 20 -> 1
                limitValue.toFloat() >= 12 && limitValue.toFloat() < 20 -> 2
                else -> 3
            }
            showImage(currentImageIndex)

            lineChart.invalidate()
        }
    }

    //x軸日期暫時無法
    private fun updateChartForDay14() {
        if (limitLine != null||midLine!=null) {
            val yAxis: YAxis = lineChart.axisRight
            yAxis.removeLimitLine(limitLine)
            limitLine = null
            val xAxis: XAxis = lineChart.xAxis
            xAxis.removeLimitLine(midLine)
            midLine = null
        }
        val intervalTextView: TextView = findViewById(R.id.timeTF)
        // 更新一星期的資料
        lifecycleScope.launch {
            val startDate =
                currentDisplayedDate.minusDays(13)
            val endDate =
                currentDisplayedDate
            val RR = aggregateRRInto14Days(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            if (RR.isEmpty()) {

            }

            val numXAxisLabels = 14  // 修改為七筆資料
            val RRCountsByDay14 = MutableList(numXAxisLabels) { 0 }  // 修改變數名稱

            RR.forEachIndexed { index, RR ->  // 使用 forEachIndexed 迴圈
                if (RR != null) {
                    RRCountsByDay14[index] = RR.toInt()  // 將步數資料填入對應位置
                }
            }

            val maxRR = RRCountsByDay14.toIntArray().max()
            val top = (maxRR / 10 + 1) * 10

            val entries: MutableList<Entry> = ArrayList()
            for (i in 0 until numXAxisLabels) {
                if (RRCountsByDay14[i] > 0) {
                    entries.add(Entry(entries.size.toFloat(), RRCountsByDay14[i].toFloat()))
                } else {
                    entries.add(Entry(entries.size.toFloat(), 0f))
                }
            }

            val dataSet = LineDataSet(entries, "呼吸速率")
            val data = LineData(dataSet)

            dataSet.color = Color.BLUE
            dataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
            dataSet.setDrawValues(false)
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
            val seventhDayX = 6f
            val eighthDayX = 7f
            val middleX = (seventhDayX + eighthDayX) / 2

            midLine = LimitLine(middleX)
            midLine!!.lineWidth = 2f
            midLine!!.lineColor = Color.LTGRAY

            xAxis.addLimitLine(midLine)

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
            if (RR.count { it > 0 } == 0) {
                average.text = "0.0"
            } else {
                average.text =
                    String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            }
            avgText.text = "平均:"

            val limitValue = String.format("%.2f", RR.sum().toDouble() / RR.count { it > 0 })
            limitLine = LimitLine(limitValue.toFloat())
            limitLine!!.lineWidth = 1f // 線寬
            limitLine!!.lineColor = Color.RED // 線的顏色
            yAxis.addLimitLine(limitLine)

            currentImageIndex = when {
                limitValue.toFloat() >= 20 -> 1
                limitValue.toFloat() >= 12 && limitValue.toFloat() < 20 -> 2
                else -> 3
            }
            showImage(currentImageIndex)

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
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.rate
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    suspend fun getDailyRRCounts(//一天24筆的資料
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<RespiratoryRateRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {


            throw exception
        }
    }


    suspend fun aggregateRRIntoWeeks(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Double> {
        val averageRateList = MutableList(7) { 0.0 }

        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                val localDateTime =
                    dailyResult.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                averageRateList[dayOfWeek - 1] = dailyResult.rate
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return averageRateList
    }

    suspend fun aggregateRRIntoMonths(
        start: LocalDateTime,
        end: LocalDateTime,
        length: Int
    ): List<Double> {
        val averageRateList = MutableList(length) { 0.0 }

        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )

            for (i in 0 until length) {
                val localDateTime =
                    response.records[i].time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfMonth = localDateTime.dayOfMonth
                averageRateList[dayOfMonth - 1] = response.records[i].rate
            }
        } catch (exception: Exception) {

        }

        return averageRateList
    }

    suspend fun aggregateRRInto14Days(
        start: LocalDateTime,
        end: LocalDateTime,
    ): MutableList<Double> {
        val averageRateList = MutableList(14) { 0.0 }

        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )

            for (i in 0 until 14) {
                val localDateTime =
                    response.records[i].time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayIndex = localDateTime.dayOfMonth - start.dayOfMonth
                averageRateList[dayIndex] = response.records[i].rate
            }
        } catch (exception: Exception) {

        }

        return averageRateList
    }

}