package com.ethan.FamiCare.Firebasecords

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId

class StatusModel {
    lateinit var client: HealthConnectClient
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()

    lateinit var Steps: List<StepsRecord>
    lateinit var HeartRates: List<HeartRateRecord>
    lateinit var Speeds: List<SpeedRecord>
    lateinit var Calories: List<TotalCaloriesBurnedRecord>
    lateinit var RespiratoryRates: List<RespiratoryRateRecord>
    lateinit var OxygenSaturations: List<OxygenSaturationRecord>
    lateinit var Sleeps: List<Double>

    // 步數:  一天24筆的資料
    suspend fun getStepCount(
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<StepsRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {
            throw exception
        }
    }

    // 心跳: 一天24筆的資料
    suspend fun getDailyHRCounts(//一天24筆的資料
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<HeartRateRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {
            throw exception
        }
    }

    //速度: 一天24筆的資料
    suspend fun getDailySpeedCounts(
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<SpeedRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    SpeedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {
            throw exception
        }
    }

    // 卡路里: 一天24筆的資料
    suspend fun getDailyCaloriesCounts(
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<TotalCaloriesBurnedRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {
            throw exception
        }
    }

    // 呼吸: 一天24筆的資料
    suspend fun getDailyRRCounts(
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

    // 血氧: 一天24筆的資料
    suspend fun getDailyOSCounts(
        start: LocalDateTime = currentDisplayedDate.with(LocalTime.MIN),
        end: LocalDateTime = currentDisplayedDate.with(LocalTime.MAX)
    ): List<OxygenSaturationRecord> {

        try {
            val request = client.readRecords(
                ReadRecordsRequest(
                    OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            return request.records
        } catch (exception: Exception) {


            throw exception
        }
    }

    // 睡眠: 一周的資料，一個 MutableList(7)
    suspend fun aggregateSleepIntoWeeks(
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Double> {
        val totalSleepList = MutableList(7) { 0.0 } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dayOfWeek = localDateTime.dayOfWeek.value // 取得星期幾的數字表示
                val duration = dailyResult.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]
                val durationInMillis = duration?.toMillis()
                val hoursWithOneDecimal = durationInMillis?.div((1000.0 * 60.0 * 60.0))
                totalSleepList[dayOfWeek - 1] = hoursWithOneDecimal ?: 0.0

            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalSleepList
    }

}