package com.ethan.FamiCare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HealthBloodGlucoseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_blood_glucose)

        //血糖沒有辦法算平均，而且不知道為啥讀不到資料

        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val BG = getBloodGlucose(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val first: TextView = findViewById(R.id.today_bg)
            first.text =
                myDateTimeFormatter.format(Instant.parse(BG[BG.size - 1].time.toString())) + "  " + BG[BG.size - 1].level.toString()

            val yesterday: TextView = findViewById(R.id.yesterday_bg)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(BG[BG.size - 2].time.toString())) + "  " + BG[BG.size - 2].level.toString()

            val twodaysago: TextView = findViewById(R.id.twodaysago_bg)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(BG[BG.size - 3].time.toString())) + "  " + BG[BG.size - 3].level.toString()

        }

        this.findViewById<Button>(R.id.update_bg).setOnClickListener {
            lifecycleScope.launch {
                val BG = getBloodGlucose(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val first: TextView = findViewById(R.id.today_bg)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(BG[BG.size - 1].time.toString())) + "  " + BG[BG.size - 1].level.toString()

                val yesterday: TextView = findViewById(R.id.yesterday_bg)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(BG[BG.size - 2].time.toString())) + "  " + BG[BG.size - 2].level.toString()

                val twodaysago: TextView = findViewById(R.id.twodaysago_bg)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(BG[BG.size - 3].time.toString())) + "  " + BG[BG.size - 3].level.toString()

            }
        }
    }

    suspend fun getBloodGlucose(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<BloodGlucoseRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                BloodGlucoseRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }
}