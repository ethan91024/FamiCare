package com.ethan.FamiCare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.Speed
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HealthSpeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_speed)

        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val Speed = getSpeed(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val total1=Speed[Speed.size - 1].samples[0].speed.toString().replaceFirst(" meters/sec","")
            val total11=String.format("%.2f",total1.toDouble()).toDouble()
            val first: TextView = findViewById(R.id.today_speed)
            first.text =
                myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 1].endTime.toString())) + "  " + total11.toString() + "公尺/秒"

            val total2=Speed[Speed.size - 2].samples[0].speed.toString().replaceFirst(" meters/sec","")
            val total22=String.format("%.2f",total2.toDouble()).toDouble()
            val yesterday: TextView = findViewById(R.id.yesterday_speed)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 2].endTime.toString())) + "  " + total22.toString() + "公尺/秒"

            val total3=Speed[Speed.size - 3].samples[0].speed.toString().replaceFirst(" meters/sec","")
            val total33=String.format("%.2f",total3.toDouble()).toDouble()
            val twodaysago: TextView = findViewById(R.id.twodaysago_speed)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 3].endTime.toString())) + "  " + total33.toString() + "公尺/秒"

            aggregateSpeedIntoWeeks(
                client,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
            )

            aggregateSpeedIntoMonths(
                client,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            )
        }

        this.findViewById<Button>(R.id.update_speed).setOnClickListener {
            lifecycleScope.launch {
                val Speed = getSpeed(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val total1=Speed[Speed.size - 1].samples[0].speed.toString().replaceFirst(" meters/sec","")
                val total11=String.format("%.2f",total1.toDouble()).toDouble()
                val first: TextView = findViewById(R.id.today_speed)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 1].endTime.toString())) + "  " + total11.toString() + "公尺/秒"

                val total2=Speed[Speed.size - 2].samples[0].speed.toString().replaceFirst(" meters/sec","")
                val total22=String.format("%.2f",total2.toDouble()).toDouble()
                val yesterday: TextView = findViewById(R.id.yesterday_speed)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 2].endTime.toString())) + "  " + total22.toString() + "公尺/秒"

                val total3=Speed[Speed.size - 3].samples[0].speed.toString().replaceFirst(" meters/sec","")
                val total33=String.format("%.2f",total3.toDouble()).toDouble()
                val twodaysago: TextView = findViewById(R.id.twodaysago_speed)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(Speed[Speed.size - 3].endTime.toString())) + "  " + total33.toString() + "公尺/秒"

                aggregateSpeedIntoWeeks(
                    client,
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now()
                )

                aggregateSpeedIntoMonths(
                    client,
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now()
                )
            }
        }
    }

    suspend fun getSpeed(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<SpeedRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                SpeedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateSpeedIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SpeedRecord.SPEED_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalSpeed = weeklyResult.result[SpeedRecord.SPEED_AVG]
            val week: TextView = findViewById(R.id.weekAvg_speed)
            if (totalSpeed != null) {
                val total=totalSpeed.toString().replaceFirst(" meters/sec","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                week.text = total1.toString() + " 公尺/秒"
            }
        }
    }

    suspend fun aggregateSpeedIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SpeedRecord.SPEED_AVG),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalSpeed = monthlyResult.result[SpeedRecord.SPEED_AVG]
            val month: TextView = findViewById(R.id.monthAvg_speed)
            if (totalSpeed != null) {
                val total=totalSpeed.toString().replaceFirst(" meters/sec","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                month.text = total1.toString() + " 公尺/秒"
            }
        }
    }
}

