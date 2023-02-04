package com.ethan.FamiCare

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*


class HealthConnect : AppCompatActivity() {
    private lateinit var mContext:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_connect)
    }
    fun onSelect(view: View) {
        when (view.id) {
            R.id.OnButton -> Toast.makeText(this@HealthConnect, "安安, 程式設計師!", Toast.LENGTH_SHORT).show()
            R.id.OffButton -> Toast.makeText(this@HealthConnect, "安安, UI設計師!", Toast.LENGTH_SHORT).show()
        }
    }
    val PERMISSIONS =
        setOf(
            HealthPermission.createReadPermission(HeartRateRecord::class),
            HealthPermission.createWritePermission(HeartRateRecord::class),
            HealthPermission.createReadPermission(HeightRecord::class),
            HealthPermission.createWritePermission(HeightRecord::class),
            HealthPermission.createReadPermission(WeightRecord::class),
            HealthPermission.createWritePermission(WeightRecord::class),
            HealthPermission.createReadPermission(OxygenSaturationRecord::class),
            HealthPermission.createWritePermission(OxygenSaturationRecord::class),
            HealthPermission.createReadPermission(SleepSessionRecord::class),
            HealthPermission.createWritePermission(SleepSessionRecord::class),
            HealthPermission.createReadPermission(SleepStageRecord::class),
            HealthPermission.createWritePermission(SleepStageRecord::class)
        )

    //val permissionController=HealthConnectClient.getOrCreate(mContext).permissionController
    // Create the permissions launcher.
    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

    val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(PERMISSIONS)) {
                // Permissions successfully granted
            } else {
                // Lack of required permissions
            }
        }

    suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions(PERMISSIONS)
        if (granted.containsAll(PERMISSIONS)) {
            // Permissions already granted, proceed with inserting or reading data.
        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }
}