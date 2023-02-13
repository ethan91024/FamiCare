package com.ethan.FamiCare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)

        //更新按鈕
        view.findViewById<Button>(R.id.submit).setOnClickListener {
        }

        //資料按鈕intent
        view.findViewById<TextView>(R.id.stepsButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthStepsActivity::class.java))
        }
        view.findViewById<TextView>(R.id.hydrationButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthHydrationActivity::class.java))
        }
        view.findViewById<TextView>(R.id.caloriesButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthCaloriesActivity::class.java))
        }
        view.findViewById<TextView>(R.id.oxygensaturationButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthOxygenSaturationActivity::class.java))
        }
        view.findViewById<TextView>(R.id.speedButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthSpeedActivity::class.java))
        }
        view.findViewById<TextView>(R.id.heartrateButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthHeartRateActivity::class.java))
        }
        view.findViewById<TextView>(R.id.bloodpressureButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthBloodPressureActivity::class.java))
        }
        view.findViewById<TextView>(R.id.sleepstageButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthSleepActivity::class.java))
        }
        view.findViewById<TextView>(R.id.respiratoryrateButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthRespiratoryRateActivity::class.java))
        }
        view.findViewById<TextView>(R.id.bloodglucoseButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthBloodGlucoseActivity::class.java))
        }

        return view
    }

    //suspend fun readData(client: HealthConnectClient) {
    //    val today = ZonedDateTime.now()
    //    val startOfDay = today.truncatedTo(ChronoUnit.DAYS)
    //    val timeRangeFilter = TimeRangeFilter.between(
    //        startOfDay.toLocalDateTime(),
    //        today.toLocalDateTime()
    //    )
    //}


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): HealthFragment {
            val fragment = HealthFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}