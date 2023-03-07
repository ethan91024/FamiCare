package com.ethan.FamiCare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
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

class HealthRespiratoryRateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_respiratory_rate)

        //呼吸沒有辦法算平均

        val myDateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val RR = getRespiratoryRate(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            val total1 = RR[RR.size - 1].rate.toString().replaceFirst(" kcal", "")
            val total11 = String.format("%.2f", total1.toDouble()).toDouble()
            val first: TextView = findViewById(R.id.today_rr)
            first.text =
                myDateTimeFormatter.format(Instant.parse(RR[RR.size - 1].time.toString())) + "  " + total11.toString() + "次/分"

            val total2 = RR[RR.size - 2].rate.toString().replaceFirst(" kcal", "")
            val total22 = String.format("%.2f", total2.toDouble()).toDouble()
            val yesterday: TextView = findViewById(R.id.yesterday_rr)
            yesterday.text =
                myDateTimeFormatter.format(Instant.parse(RR[RR.size - 2].time.toString())) + "  " + total22.toString() + "次/分"

            val total3 = RR[RR.size - 3].rate.toString().replaceFirst(" kcal", "")
            val total33 = String.format("%.2f", total3.toDouble()).toDouble()
            val twodaysago: TextView = findViewById(R.id.twodaysago_rr)
            twodaysago.text =
                myDateTimeFormatter.format(Instant.parse(RR[RR.size - 3].time.toString())) + "  " + total33.toString() + "次/分"

        }

        this.findViewById<Button>(R.id.update_rr).setOnClickListener {
            lifecycleScope.launch {
                val RR = getRespiratoryRate(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )
                val total1 = RR[RR.size - 1].rate.toString().replaceFirst(" kcal", "")
                val total11 = String.format("%.2f", total1.toDouble()).toDouble()
                val first: TextView = findViewById(R.id.today_rr)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(RR[RR.size - 1].time.toString())) + "  " + total11.toString() + "次/分"

                val total2 = RR[RR.size - 2].rate.toString().replaceFirst(" kcal", "")
                val total22 = String.format("%.2f", total2.toDouble()).toDouble()
                val yesterday: TextView = findViewById(R.id.yesterday_rr)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(RR[RR.size - 2].time.toString())) + "  " + total22.toString() + "次/分"

                val total3 = RR[RR.size - 3].rate.toString().replaceFirst(" kcal", "")
                val total33 = String.format("%.2f", total3.toDouble()).toDouble()
                val twodaysago: TextView = findViewById(R.id.twodaysago_rr)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(RR[RR.size - 3].time.toString())) + "  " + total33.toString() + "次/分"

            }
        }
    }

    suspend fun getRespiratoryRate(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<RespiratoryRateRecord> {

        val request = client.readRecords(
            ReadRecordsRequest(
                RespiratoryRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }
}
