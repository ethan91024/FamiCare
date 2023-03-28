package com.ethan.FamiCare.Health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HealthCaloriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_calories)
        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val Cal = getCalories(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val total1=Cal[Cal.size - 1].energy.toString().replaceFirst(" kcal","")
            val total11=String.format("%.2f",total1.toDouble()).toDouble()
            val first: TextView = findViewById(R.id.today_cal)
            first.text =
                myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 1].endTime.toString())) + "  " + total11.toString() + "大卡"

            val total2=Cal[Cal.size - 2].energy.toString().replaceFirst(" kcal","")
            val total22=String.format("%.2f",total2.toDouble()).toDouble()
            val yesterday: TextView = findViewById(R.id.yesterday_cal)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 2].endTime.toString())) + "  " + total22.toString() + "大卡"

            val total3=Cal[Cal.size - 3].energy.toString().replaceFirst(" kcal","")
            val total33=String.format("%.2f",total3.toDouble()).toDouble()
            val twodaysago: TextView = findViewById(R.id.twodaysago_cal)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 3].endTime.toString())) + "  " + total33.toString() + "大卡"

            aggregateCaloriesIntoWeeks(
                client,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
            )

            aggregateCaloriesIntoMonths(
                client,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            )
        }

        this.findViewById<Button>(R.id.update_cal).setOnClickListener {
            lifecycleScope.launch {
                val Cal = getCalories(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val total1=Cal[Cal.size - 1].energy.toString().replaceFirst(" kcal","")
                val total11=String.format("%.2f",total1.toDouble()).toDouble()
                val first: TextView = findViewById(R.id.today_cal)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 1].endTime.toString())) + "  " + total11.toString() + "大卡"

                val total2=Cal[Cal.size - 2].energy.toString().replaceFirst(" kcal","")
                val total22=String.format("%.2f",total2.toDouble()).toDouble()
                val yesterday: TextView = findViewById(R.id.yesterday_cal)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 2].endTime.toString())) + "  " + total22.toString() + "大卡"

                val total3=Cal[Cal.size - 3].energy.toString().replaceFirst(" kcal","")
                val total33=String.format("%.2f",total3.toDouble()).toDouble()
                val twodaysago: TextView = findViewById(R.id.twodaysago_cal)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(Cal[Cal.size - 3].endTime.toString())) + "  " + total33.toString() + "大卡"

                aggregateCaloriesIntoWeeks(
                    client,
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now()
                )

                aggregateCaloriesIntoMonths(
                    client,
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now()
                )
            }
        }
    }

    suspend fun getCalories(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<TotalCaloriesBurnedRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateCaloriesIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalCal = weeklyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
            val week: TextView = findViewById(R.id.weekAvg_cal)
            if (totalCal != null) {
                val total=totalCal.toString().replaceFirst(" kcal","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                week.text = total1.toString() + " 大卡"
            }
        }
    }

    suspend fun aggregateCaloriesIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalCal = monthlyResult.result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
            val month: TextView = findViewById(R.id.monthAvg_cal)
            if (totalCal != null) {
                val total=totalCal.toString().replaceFirst(" kcal","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                month.text = total1.toString() + " 大卡"
            }
        }
    }
    }
