package com.ethan.FamiCare.Health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HealthOxygenSaturationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_oxygen_saturation)

        //血氧沒有辦法算平均

        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val OS = getOxygenSaturation(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val first: TextView = findViewById(R.id.today_os)
            first.text =
                myDateTimeFormatter.format(Instant.parse(OS[OS.size - 1].time.toString())) + "  " + OS[OS.size - 1].percentage.toString()

            val yesterday: TextView = findViewById(R.id.yesterday_os)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(OS[OS.size - 2].time.toString())) + "  " + OS[OS.size - 2].percentage.toString()

            val twodaysago: TextView = findViewById(R.id.twodaysago_os)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(OS[OS.size - 3].time.toString())) + "  " + OS[OS.size - 3].percentage.toString()

        }

        this.findViewById<Button>(R.id.update_os).setOnClickListener {
            lifecycleScope.launch {
                val OS = getOxygenSaturation(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val first: TextView = findViewById(R.id.today_os)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(OS[OS.size - 1].time.toString())) + "  " + OS[OS.size - 1].percentage.toString()

                val yesterday: TextView = findViewById(R.id.yesterday_os)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(OS[OS.size - 2].time.toString())) + "  " + OS[OS.size - 2].percentage.toString()

                val twodaysago: TextView = findViewById(R.id.twodaysago_os)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(OS[OS.size - 3].time.toString())) + "  " + OS[OS.size - 3].percentage.toString()

            }
        }
    }

    suspend fun getOxygenSaturation(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<OxygenSaturationRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                OxygenSaturationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }
}
