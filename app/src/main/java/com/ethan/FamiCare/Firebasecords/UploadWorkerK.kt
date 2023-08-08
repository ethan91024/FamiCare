package com.ethan.FamiCare.Firebasecords

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period

class UploadWorkerK(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    lateinit var client: HealthConnectClient
    var currentDisplayedDate: LocalDateTime = LocalDateTime.now()

    override fun doWork(): Result {
        // 使用 runBlocking 來調用使用協程的方法
        runBlocking {

            // 獲取使用者的ID
            val userId = getUserId()

            // 建立 StatusModel 物件
            val statusModel = StatusModel()


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
            val totalSteps = calTotalSteps(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            val avgHeartRate = calAverageHeartRate(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            val maxSpeed = calMaxSpeed(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            val totalCalories = calTotalCalories(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            val avgRespiratoryRate = calAverageRespiratoryRate(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            val avgOxygenSaturation = calAverageOxygenSaturation(
                currentDisplayedDate.toLocalDate().atStartOfDay(),
                currentDisplayedDate.toLocalDate().atTime(LocalTime.MAX)
            )

            // 獲取睡眠數據
            val startDate =
                currentDisplayedDate.minusDays(currentDisplayedDate.dayOfWeek.value.toLong() - 1)
            val endDate =
                currentDisplayedDate.plusDays(7 - currentDisplayedDate.dayOfWeek.value.toLong())
            val sleeps = statusModel.aggregateSleepIntoWeeks(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().atTime(LocalTime.MAX)
            )
            val totalSleep = sleeps.average()

            // 上傳 StatusModel 到 Firebase
            uploadStatusToFirebase(
                userId,
                totalSteps,
                avgHeartRate,
                maxSpeed,
                totalCalories,
                avgRespiratoryRate,
                avgOxygenSaturation,
                totalSleep
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
        totalSteps: Long,
        avgHeartRate: Double,
        maxSpeed: Double,
        totalCalories: Double,
        avgRespiratoryRate: Double,
        avgOxygenSaturation: Double,
        totalSleep: Double
    ) {
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val statusRef = database.getReference("Status").child(userId)
            val status_step: Int
            val status_heartRate: Int
            val status_speed: Double
            val status_calories: Int
            val status_respiratory: Int
            val status_bloodOxygen: Int
            val status_sleep: Int

            if (totalSteps >= 10000) {
                status_step = 3
            } else if (totalSteps >= 5000 && totalSteps < 10000) {
                status_step = 2
            } else if (totalSteps < 5000) {
                status_step = 1
            }

            if (avgHeartRate >= 60 && avgHeartRate < 85) {
                status_heartRate = 3
            } else if (avgHeartRate >= 85) {
                status_heartRate = 1
            }

            status_speed = maxSpeed

            if (totalCalories >= 300) {
                status_calories = 3
            } else if (totalCalories >= 150 && totalCalories < 300) {
                status_calories = 2
            } else if (totalCalories < 150) {
                status_calories = 1
            }

            if (avgRespiratoryRate >= 12 && avgRespiratoryRate < 20) {
                status_respiratory = 3
            } else if (avgRespiratoryRate >= 20 || avgRespiratoryRate < 12) {
                status_respiratory = 1
            }

            if (avgOxygenSaturation >= 95) {
                status_bloodOxygen = 3
            } else if (avgOxygenSaturation >= 90 && avgOxygenSaturation <= 94) {
                status_bloodOxygen = 2
            } else if (avgOxygenSaturation < 90) {
                status_bloodOxygen = 1
            }

            if (totalSleep >= 7 && totalSleep <= 9) {
                status_sleep = 3
            } else if (totalSleep < 7 || totalSleep > 9) {
                status_sleep = 1
            }


            try {
                // 使用 updateChildren 方法進行更新或新增資料
                val statusData = HashMap<String, Any>()
                statusData["status_step"] = totalSteps
                statusData["status_heartRate"] = avgHeartRate
                statusData["status_speed"] = maxSpeed
                statusData["status_calories"] = totalCalories
                statusData["status_respiratory"] = avgRespiratoryRate
                statusData["status_bloodOxygen"] = avgOxygenSaturation
                statusData["status_sleep"] = totalSleep

                statusRef.updateChildren(statusData)
            } catch (e: Exception) {
                // Handle exception here
            }
        }
    }

    suspend fun calTotalSteps(
        start: LocalDateTime,
        end: LocalDateTime
    ): Long {
        var number: Long = 0
        try {
            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            for (dailyResult in response) {
                number += dailyResult.result[StepsRecord.COUNT_TOTAL] ?: 0L
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    private suspend fun calAverageHeartRate(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.samples[0].beatsPerMinute.toDouble()
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    private suspend fun calMaxSpeed(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    SpeedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.samples[0].speed.inKilometersPerHour
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    private suspend fun calTotalCalories(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.energy.inKilocalories
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    private suspend fun calAverageRespiratoryRate(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.rate
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

    private suspend fun calAverageOxygenSaturation(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        var number = 0.0
        try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    OxygenSaturationRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            for (dailyResult in response.records) {
                number += dailyResult.percentage.value
            }
        } catch (exception: Exception) {
            // Handle exception here
        }

        return number
    }

}


