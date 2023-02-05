package com.ethan.FamiCare

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val healthConnectClient: HealthConnectClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        //啟動直接在群組
        val fm = supportFragmentManager
        fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, GroupFragment())
            .commit()
        checkAvailability()
    }

    //判斷HealthConnect是否安裝了
    fun checkAvailability() {
        if (HealthConnectClient.isProviderAvailable(this@MainActivity)) {
            Toast.makeText(
                this@MainActivity,
                "Health Connect is installed and supported!",
                Toast.LENGTH_SHORT
            ).show()
            checkPermissionsAndRun()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Toast.makeText(this@MainActivity, "Health Connect is not supported!", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@MainActivity, "Health Connect is not installed!", Toast.LENGTH_SHORT)
                .show()
            installHealthConnect()
            checkPermissionsAndRun()
        }
    }

    fun installHealthConnect() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val data = Uri.parse("market://details")
            .buildUpon()
            .appendQueryParameter("id", "com.google.android.apps.healthdata")
            .appendQueryParameter("url", "healthconnect://onboarding")
            .build()
        intent.data = data
        startActivity(intent)
    }

    fun checkPermissionsAndRun() {

        val client = HealthConnectClient.getOrCreate(this)

        val permissionsSet = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class),
            HealthPermission.getWritePermission(HeightRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(WeightRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getWritePermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getWritePermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(SleepStageRecord::class),
            HealthPermission.getWritePermission(SleepStageRecord::class),
        )

        val requestPermissionActivityContract =
            PermissionController.createRequestPermissionResultContract()

        val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(permissionsSet)) {
                lifecycleScope.launch {
                    onPermissionAvailable(client)
                }
            } else {
                Toast.makeText(
                    this, "Permissions not granted", Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launch {
            val granted = client.permissionController
                .getGrantedPermissions()
            if (granted.containsAll(permissionsSet)) {
                onPermissionAvailable(client)
            } else {
                requestPermissions.launch(permissionsSet)
            }
        }
    }
//
    private suspend fun onPermissionAvailable(client: HealthConnectClient) {
        // todo: read data
    }
    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_health -> selectedFragment = HealthFragment()
            R.id.navigation_group -> selectedFragment = GroupFragment()
            R.id.navigation_mood -> selectedFragment = MoodFragment()
            R.id.navigation_diary -> selectedFragment = DiaryFragment()
            R.id.navigation_settings -> selectedFragment = SettingsFragment()
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            selectedFragment!!
        ).commit()
        true
    }
}