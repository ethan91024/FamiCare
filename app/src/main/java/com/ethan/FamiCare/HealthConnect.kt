package com.ethan.FamiCare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.*
import androidx.activity.result.ActivityResultLauncher

class HealthConnect : AppCompatActivity() {
    lateinit var HealthRadioGroup: RadioGroup
    lateinit var OnButton: RadioButton
    lateinit var OffButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_connect)

        HealthRadioGroup = findViewById(R.id.HealthRadioGroup)
        OnButton = findViewById(R.id.OnButton)
        OffButton = findViewById(R.id.OffButton)

    }
}