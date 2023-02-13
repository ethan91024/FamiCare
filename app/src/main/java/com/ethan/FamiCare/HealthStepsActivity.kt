package com.ethan.FamiCare

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HealthStepsActivity : AppCompatActivity() {
    val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone( ZoneId.systemDefault() )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_steps)

        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val steps = getStepCount(
                client,
                LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.DAYS)
                    .toInstant(ZoneOffset.ofHours(0)),
                LocalDateTime.now().toInstant(ZoneOffset.ofHours(0))
            )
            val first: TextView
            first = findViewById(R.id.today)
            first.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-1].endTime.toString()))+"  "+steps[steps.size-1].count.toString()+"步"
            val yesterday: TextView
            yesterday = findViewById(R.id.yesterday)
            yesterday.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-2].endTime.toString()))+"  "+steps[steps.size-2].count.toString()+"步"
            val twodaysago: TextView
            twodaysago = findViewById(R.id.twodaysago)
            twodaysago.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-3].endTime.toString()))+"  "+steps[steps.size-3].count.toString()+"步"
        }

        this.findViewById<Button>(R.id.update).setOnClickListener {
            lifecycleScope.launch {
                val steps = getStepCount(
                    client,
                    LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.DAYS)
                        .toInstant(ZoneOffset.ofHours(0)),
                    LocalDateTime.now().toInstant(ZoneOffset.ofHours(0))
                )
                val first: TextView
                first = findViewById(R.id.today)
                first.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-1].endTime.toString()))+"  "+steps[steps.size-1].count.toString()+"步"
                val yesterday: TextView
                yesterday = findViewById(R.id.yesterday)
                yesterday.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-2].endTime.toString()))+"  "+steps[steps.size-2].count.toString()+"步"
                val twodaysago: TextView
                twodaysago = findViewById(R.id.twodaysago)
                twodaysago.text = myDateTimeFormatter.format(Instant.parse(steps[steps.size-3].endTime.toString()))+"  "+steps[steps.size-3].count.toString()+"步"
            }
        }
    }

    suspend fun getStepCount(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): List<StepsRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

}