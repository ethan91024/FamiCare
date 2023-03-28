package com.ethan.FamiCare.Health

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.ethan.FamiCare.R

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
}//1