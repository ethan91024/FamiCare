package com.ethan.FamiCare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HealthHeartRateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_heart_rate)

        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val HR = getHeartRate(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val total1 = HR[HR.size - 1].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
            val total11 = String.format("%.2f", total1.toDouble()).toDouble()
            val first: TextView = findViewById(R.id.today_hr)
            first.text =
                myDateTimeFormatter.format(Instant.parse(HR[HR.size - 1].endTime.toString())) + "  " + total11.toString() + "次/秒"

            val total2 = HR[HR.size - 2].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
            val total22 = String.format("%.2f", total2.toDouble()).toDouble()
            val yesterday: TextView = findViewById(R.id.yesterday_hr)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(HR[HR.size - 2].endTime.toString())) + "  " + total22.toString() + "次/秒"

            val total3 = HR[HR.size - 3].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
            val total33 = String.format("%.2f", total3.toDouble()).toDouble()
            val twodaysago: TextView = findViewById(R.id.twodaysago_hr)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(HR[HR.size - 3].endTime.toString())) + "  " + total33.toString() + "次/秒"

            aggregateHeartRateIntoWeeks(
                client,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
            )

            aggregateHeartRateIntoMonths(
                client,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            )
        }

        this.findViewById<Button>(R.id.update_hr).setOnClickListener {
            lifecycleScope.launch {
                val HR = getHeartRate(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val total1 =
                    HR[HR.size - 1].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
                val total11 = String.format("%.2f", total1.toDouble()).toDouble()
                val first: TextView = findViewById(R.id.today_hr)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(HR[HR.size - 1].endTime.toString())) + "  " + total11.toString() + "次/秒"

                val total2 =
                    HR[HR.size - 2].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
                val total22 = String.format("%.2f", total2.toDouble()).toDouble()
                val yesterday: TextView = findViewById(R.id.yesterday_hr)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(HR[HR.size - 2].endTime.toString())) + "  " + total22.toString() + "次/秒"

                val total3 =
                    HR[HR.size - 3].samples[0].beatsPerMinute.toString().replaceFirst(" ", "")
                val total33 = String.format("%.2f", total3.toDouble()).toDouble()
                val twodaysago: TextView = findViewById(R.id.twodaysago_hr)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(HR[HR.size - 3].endTime.toString())) + "  " + total33.toString() + "次/秒"

                aggregateHeartRateIntoWeeks(
                    client,
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now()
                )

                aggregateHeartRateIntoMonths(
                    client,
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now()
                )
            }
        }
    }

    suspend fun getHeartRate(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<HeartRateRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateHeartRateIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalHR = weeklyResult.result[HeartRateRecord.BPM_AVG]
            val week: TextView = findViewById(R.id.weekAvg_hr)
            if (totalHR != null) {
                val total = totalHR.toString().replaceFirst(" ", "")
                val total1 = String.format("%.2f", total.toDouble().div(response.size)).toDouble()
                week.text = total1.toString() + " 次/秒"
            }
        }
    }

    suspend fun aggregateHeartRateIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HeartRateRecord.BPM_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalHR = monthlyResult.result[HeartRateRecord.BPM_AVG]
            val month: TextView = findViewById(R.id.monthAvg_hr)
            if (totalHR != null) {
                val total = totalHR.toString().replaceFirst(" ", "")
                val total1 = String.format("%.2f", total.toDouble().div(response.size)).toDouble()
                month.text = total1.toString() + " 次/秒"
            }
        }
    }
}