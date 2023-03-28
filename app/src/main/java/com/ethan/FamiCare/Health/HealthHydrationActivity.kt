package com.ethan.FamiCare.Health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HydrationRecord
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

class HealthHydrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_hydration)
        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val Hydra = getHydration(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val total1=Hydra[Hydra.size - 1].volume.toString().replaceFirst(" L","")
            val total11=String.format("%.2f",total1.toDouble()).toDouble()
            val first: TextView = findViewById(R.id.today_hydration)
            first.text =
                myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 1].endTime.toString())) + "  " + total11.toString() + "公升"

            val total2=Hydra[Hydra.size - 2].volume.toString().replaceFirst(" L","")
            val total22=String.format("%.2f",total2.toDouble()).toDouble()
            val yesterday: TextView = findViewById(R.id.yesterday_hydration)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 2].endTime.toString())) + "  " + total22.toString() + "公升"

            val total3=Hydra[Hydra.size - 3].volume.toString().replaceFirst(" L","")
            val total33=String.format("%.2f",total3.toDouble()).toDouble()
            val twodaysago: TextView = findViewById(R.id.twodaysago_hydration)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 3].endTime.toString())) + "  " + total33.toString() + "公升"

            aggregateHydrationIntoWeeks(
                client,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
            )

            aggregateHydrationIntoMonths(
                client,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            )
        }

        this.findViewById<Button>(R.id.update_hydration).setOnClickListener {
            lifecycleScope.launch {
                val Hydra = getHydration(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )

                val total1=Hydra[Hydra.size - 1].volume.toString().replaceFirst(" L","")
                val total11=String.format("%.2f",total1.toDouble()).toDouble()
                val first: TextView = findViewById(R.id.today_hydration)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 1].endTime.toString())) + "  " + total11.toString() + "公升"

                val total2=Hydra[Hydra.size - 2].volume.toString().replaceFirst(" L","")
                val total22=String.format("%.2f",total2.toDouble()).toDouble()
                val yesterday: TextView = findViewById(R.id.yesterday_hydration)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 2].endTime.toString())) + "  " + total22.toString() + "公升"

                val total3=Hydra[Hydra.size - 3].volume.toString().replaceFirst(" L","")
                val total33=String.format("%.2f",total3.toDouble()).toDouble()
                val twodaysago: TextView = findViewById(R.id.twodaysago_hydration)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(Hydra[Hydra.size - 3].endTime.toString())) + "  " + total33.toString() + "公升"

                aggregateHydrationIntoWeeks(
                    client,
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now()
                )

                aggregateHydrationIntoMonths(
                    client,
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now()
                )
            }
        }
    }

    suspend fun getHydration(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<HydrationRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateHydrationIntoWeeks(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HydrationRecord.VOLUME_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofWeeks(1)
                )
            )

        for (weeklyResult in response) {
            val totalHydra = weeklyResult.result[HydrationRecord.VOLUME_TOTAL]
            val week: TextView = findViewById(R.id.weekAvg_hydration)
            if (totalHydra != null) {
                val total=totalHydra.toString().replaceFirst(" L","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                week.text = total1.toString() + " 公升"
            }
        }
    }

    suspend fun aggregateHydrationIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        val response =
            healthConnectClient.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(HydrationRecord.VOLUME_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    timeRangeSlicer = Period.ofMonths(1)
                )
            )

        for (monthlyResult in response) {
            val totalHydra = monthlyResult.result[HydrationRecord.VOLUME_TOTAL]
            val month: TextView = findViewById(R.id.month_hydration)
            if (totalHydra != null) {
                val total=totalHydra.toString().replaceFirst(" L","")
                val total1=String.format("%.2f",total.toDouble().div(response.size)).toDouble()
                month.text = total1.toString() + " 公升"
            }
        }
    }
}


