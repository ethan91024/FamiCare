package com.ethan.FamiCare.Firebasecords

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ethan.FamiCare.Health.HealthHeartRateActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import java.time.*
import java.time.temporal.ChronoUnit



class AnalyzeHealth(
        context: Context,
        workerParams: WorkerParameters



) : Worker(context, workerParams) {


    lateinit var client: HealthConnectClient


    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()
    val startDate = currentDisplayedDate.minusDays(6).toLocalDate().atStartOfDay()
    val endDate = currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)

    // 獲取當天日期
    val currentDate = LocalDate.now()

    // 獲取年、月、日
    val year = currentDate.year
    val month = currentDate.monthValue
    val day = currentDate.dayOfMonth
    val dday = year.toString() + "_" + month.toString() + "_" + day.toString()
    val appContext: Context = context.applicationContext // 將 context 存儲為 appContext


    override fun doWork(): Result {
        // 使用 runBlocking 來調用使用協程的方法
        runBlocking {

            client = HealthConnectClient.getOrCreate(appContext)
            // 獲取使用者的ID
            val userId = getUserId()

            // 建立 StatusModel 物件
            val statusModel = StatusModel()
            println("st" + startDate)


            // 獲取步數數據
//        val steps = statusModel.getStepCount()[0]
//
//        // 獲取心跳數據
//        val heartRates = statusModel.getDailyHRCounts()
//
//        // 獲取速度數據
//        val speeds = statusModel.getDailySpeedCounts()
//
//        // 獲取卡路里數據
//        val calories = statusModel.getDailyCaloriesCounts()
//
//        // 獲取呼吸數據
//        val respiratoryRates = statusModel.getDailyRRCounts()
//
//        // 獲取血氧數據
//        val oxygenSaturations = statusModel.getDailyOSCounts()


            // 計算各個 List 的總和


            val HeartRateList = aggregateHRIntoWeeks(
                    startDate, endDate
            )
//            println("HR" + HeartRateList)

            val OSList = aggregateOSIntoWeeks(
                    startDate, endDate
            )


            // 獲取睡眠數據
//            val startDate =
//                    currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
//            val endDate =
//                    currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val sleepList = aggregateSleepIntoWeeks(
                    startDate, endDate
            )


            // 上傳 StatusModel 到 Firebase
            uploadStatusToFirebase(
                    userId,
                    HeartRateList,
                    OSList,
                    sleepList
            )
        }
        return Result.success()

    }

    private fun getUserId(): String? {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        return currentUser?.uid
    }

    private fun uploadStatusToFirebase(
            userId: String?,
            avgHeartRate: List<Long>,
            avgOxygenSaturation: List<Long>,
            totalSleep: List<Double>
    ) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val statusRef = database.getReference("AnalyzeHealth").child(userId).child(dday)

            try {
                // 使用 updateChildren 方法進行更新或新增資料
                val statusData = HashMap<String, Any>()
                statusData["AnalyzeHealth_heartRate"] = avgHeartRate
                statusData["AnalyzeHealths_bloodOxygen"] = avgOxygenSaturation
                statusData["AnalyzeHealth_sleep"] = totalSleep

                statusRef.updateChildren(statusData)
            } catch (e: Exception) {
                // Handle exception here
            }
        }
    }

    suspend fun aggregateHRIntoWeeks(
            start: LocalDateTime,
            end: LocalDateTime
    ): List<Long> {
        val totalHRList = MutableList(7) { 0L } // 建立一個初始值為 0 的 7 個元素的陣列

        try {
        val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                        metrics = setOf(HeartRateRecord.BPM_AVG),
                        timeRangeFilter = TimeRangeFilter.between(start, end),
                        timeRangeSlicer = Period.ofDays(1)
                )
        )

        for (dailyResult in response) {
            val localDateTime =
                    dailyResult.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

            // 檢查日期是否在指定範圍內
            if (localDateTime >= start && localDateTime <= end) {
                val dayOfWeek = ChronoUnit.DAYS.between(start, localDateTime).toInt() // 取得當天和 start 間的天數差
                totalHRList[dayOfWeek] = dailyResult.result[HeartRateRecord.BPM_AVG] ?: 0L
            }
        }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalHRList
    }

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
                if (localDateTime >= start && localDateTime <= end) {
                    val dayOfWeek = ChronoUnit.DAYS.between(start, localDateTime).toInt() // 取得當天和 start 間的天數差
                    val duration = dailyResult.result[SleepSessionRecord.SLEEP_DURATION_TOTAL]
                    val durationInMillis = duration?.toMillis()
                    val hoursWithOneDecimal = durationInMillis?.div((1000.0 * 60.0 * 60.0))
                    totalSleepList[dayOfWeek] = hoursWithOneDecimal ?: 0.0
                }

            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalSleepList
    }

    suspend fun aggregateOSIntoWeeks(
            start: LocalDateTime,
            end: LocalDateTime
    ): List<Long> {
        val totalOSList = MutableList(7) { 0L } // 建立一個初始值為0的7個元素的陣列

        try {
            val response = client.readRecords(
                    ReadRecordsRequest(
                            OxygenSaturationRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(start, end)
                    )
            )
            for (dailyResult in response.records) {
                val localDateTime =
                        dailyResult.time.atZone(ZoneId.systemDefault()).toLocalDateTime()

                // 檢查日期是否在指定範圍內
                if (localDateTime >= start && localDateTime <= end) {
                    val dayOfWeek = ChronoUnit.DAYS.between(start, localDateTime).toInt() // 取得當天和 start 間的天數差
                    totalOSList[dayOfWeek] = dailyResult.percentage.value.toLong() ?: 0L
                }
            }

        } catch (exception: Exception) {
            // Handle exception here
        }

        return totalOSList
    }

}