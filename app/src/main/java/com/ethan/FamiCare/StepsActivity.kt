package com.ethan.FamiCare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class StepsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        val client= HealthConnectClient.getOrCreate(this)
        this.findViewById<Button>(R.id.update).setOnClickListener{
            lifecycleScope.launch {
                val fragment: Fragment
                fragment=HealthFragment()
                val steps=fragment.getStepCount(client,
                    LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.ofHours(8)),
                    LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)))
                val first:TextView
                first = findViewById(R.id.today)
                first.text = steps[0].count.toString()
                val yesterday:TextView
                yesterday = findViewById(R.id.yesterday)
                yesterday.text = steps[1].count.toString()
                val twodaysago:TextView
                twodaysago = findViewById(R.id.twodaysago)
                twodaysago.text = steps[2].count.toString()
            }
        }
    }


}