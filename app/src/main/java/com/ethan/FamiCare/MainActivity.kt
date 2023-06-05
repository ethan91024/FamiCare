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
import com.ethan.FamiCare.Diary.DiaryFragment
import com.ethan.FamiCare.Group.GroupFragment
import com.ethan.FamiCare.Health.HealthFragment
import com.ethan.FamiCare.Settings.Login
import com.ethan.FamiCare.Settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    //    var mAuth: FirebaseAuth? = null
    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)



        //啟動直接在群組
        val fm = supportFragmentManager
        fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container,
            GroupFragment()
        )
            .commit()
        checkAvailability()


        var  currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // 用户已登录
            // 在此处执行相应的操作
            val fm = supportFragmentManager
            fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container,
                GroupFragment()
            )
                .commit()
            checkAvailability()
        } else {
            // 用户未登录
            // 在此处执行相应的操作，例如跳转到登录界面
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

//        // 檢查用戶是否已經登入
//        val userLoggedIn: Boolean = checkUserLoggedIn()
//
//        if (!userLoggedIn) {
//            // 如果用戶未登錄，跳轉到登錄 Activity
//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            //啟動直接在群組
//            val fm = supportFragmentManager
//            fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, GroupFragment())
//                .commit()
//            checkAvailability()
//        }
    }

//    private fun checkUserLoggedIn(): Boolean {
//        // 檢查用戶是否已經登錄
//        // 如果已經登錄，返回 true
//        if(mAuth?.getCurrentUser() !=null){
//            return true;
//        }else{// 否則返回 false
//            return false;
//        }
//    }

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
            Toast.makeText(
                this@MainActivity,
                "Health Connect is not supported!",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Health Connect is not installed!",
                Toast.LENGTH_SHORT
            )
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
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(BloodGlucoseRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(SleepStageRecord::class),
            HealthPermission.getReadPermission(SpeedRecord::class),
            HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        )

        val requestPermissionActivityContract =
            PermissionController.createRequestPermissionResultContract()

        val requestPermissions =
            registerForActivityResult(requestPermissionActivityContract) { granted ->
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


    private suspend fun onPermissionAvailable(client: HealthConnectClient) {

    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_health -> selectedFragment = HealthFragment()
            R.id.navigation_group -> selectedFragment =
                GroupFragment()
            R.id.navigation_mood -> selectedFragment = MoodFragment()
            R.id.navigation_diary -> selectedFragment = DiaryFragment()
            R.id.navigation_settings -> selectedFragment =
                SettingsFragment()
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            selectedFragment!!
        ).commit()
        true
    }
}