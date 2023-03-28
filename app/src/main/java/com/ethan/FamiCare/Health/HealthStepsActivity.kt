package com.ethan.FamiCare.Health

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.ethan.FamiCare.R
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter


class HealthStepsActivity : AppCompatActivity() {
    val myDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault())
    val startOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN)
    val endOfTheDay = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_steps)

        val client = HealthConnectClient.getOrCreate(this)

        lifecycleScope.launch {
            val steps = getDailyStepCounts(
                client,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now()
            )
            if (steps.size < 3) {
                Toast.makeText(
                    this@HealthStepsActivity,
                    "Don't Have Data!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val first: TextView = findViewById(R.id.today)
                first.text =
                    myDateTimeFormatter.format(Instant.parse(steps[steps.size - 1].endTime.toString())) + "  " + steps[steps.size - 1].count.toString() + "步"
                val yesterday: TextView = findViewById(R.id.yesterday)
                yesterday.text =
                    myDateTimeFormatter.format(Instant.parse(steps[steps.size - 2].endTime.toString())) + "  " + steps[steps.size - 2].count.toString() + "步"
                val twodaysago: TextView = findViewById(R.id.twodaysago)
                twodaysago.text =
                    myDateTimeFormatter.format(Instant.parse(steps[steps.size - 3].endTime.toString())) + "  " + steps[steps.size - 3].count.toString() + "步"
            }

            aggregateStepsIntoDays(
                client,
                startOfTheDay,
                endOfTheDay
            )
            var todaySteps = aggregateStepsIntoDays(
                client,
                startOfTheDay,
                endOfTheDay
            )
            val today: TextView = findViewById(R.id.todaySteps)
            today.text = todaySteps.toString() + "步"

            aggregateStepsIntoWeeks(
                client,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
            )

            aggregateStepsIntoMonths(
                client,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            )
        }

        this.findViewById<Button>(R.id.update).setOnClickListener {
            lifecycleScope.launch {

                val steps = getDailyStepCounts(
                    client,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now()
                )

                if (steps.size < 3) {
                    Toast.makeText(
                        this@HealthStepsActivity,
                        "Don't Have Data!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val first: TextView = findViewById(R.id.today)
                    first.text =
                        myDateTimeFormatter.format(Instant.parse(steps[steps.size - 1].endTime.toString())) + "  " + steps[steps.size - 1].count.toString() + "步"
                    val yesterday: TextView = findViewById(R.id.yesterday)
                    yesterday.text =
                        myDateTimeFormatter.format(Instant.parse(steps[steps.size - 2].endTime.toString())) + "  " + steps[steps.size - 2].count.toString() + "步"
                    val twodaysago: TextView = findViewById(R.id.twodaysago)
                    twodaysago.text =
                        myDateTimeFormatter.format(Instant.parse(steps[steps.size - 3].endTime.toString())) + "  " + steps[steps.size - 3].count.toString() + "步"
                }

                var todaySteps = aggregateStepsIntoDays(
                    client,
                    startOfTheDay,
                    endOfTheDay
                )
                val today: TextView = findViewById(R.id.todaySteps)
                today.text = todaySteps.toString() + "步"

                aggregateStepsIntoWeeks(
                    client,
                    LocalDateTime.now().minusDays(7),
                    LocalDateTime.now()
                )

                aggregateStepsIntoMonths(
                    client,
                    LocalDateTime.now().minusDays(30),
                    LocalDateTime.now()
                )
            }
        }


    }

    suspend fun getDailyStepCounts(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<StepsRecord> {
        val request = client.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return request.records
    }

    suspend fun aggregateStepsIntoDays(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ): Long? {
        var totalSteps: Long? = 0
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofDays(1)
                    )
                )
            for (dailyResult in response) {
                totalSteps = dailyResult.result[StepsRecord.COUNT_TOTAL]
            }
        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
        return totalSteps
    }

    suspend fun aggregateStepsIntoWeeks(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ) {
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofWeeks(1)
                    )
                )
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (weeklyResult in response) {
                val totalSteps = weeklyResult.result[StepsRecord.COUNT_TOTAL]
                val week: TextView = findViewById(R.id.weekAvg)
                if (totalSteps != null) {
                    week.text = totalSteps.div(7).toString() + "步"
                }
            }

        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    suspend fun aggregateStepsIntoMonths(
        client: HealthConnectClient,
        start: LocalDateTime,
        end: LocalDateTime
    ) {
        try {
            val response =
                client.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofMonths(1)
                    )
                )
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (monthlyResult in response) {
                val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]
                val month: TextView = findViewById(R.id.monthAvg)
                if (totalSteps != null) {
                    month.text = totalSteps.div(30).toString() + "步"
                }
            }
        } catch (exception: Exception) {
            Toast.makeText(
                this@HealthStepsActivity,
                "Don't Have Data!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}