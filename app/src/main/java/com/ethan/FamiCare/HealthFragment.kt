package com.ethan.FamiCare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    lateinit var heightEditText: TextView
    lateinit var weightEditText: TextView

    lateinit var heightTextView:TextView
    lateinit var weightTextView:TextView

    lateinit var heightAverageTextView:TextView
    lateinit var weightAverageTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)
        //heightEditText = view.findViewById(R.id.heightEditText)
        //weightEditText = view.findViewById(R.id.weightEditText)

        //更新按鈕
        view.findViewById<Button>(R.id.submit).setOnClickListener {
            //val height = heightEditText.text.toString().toDouble().meters
            //val weight = weightEditText.text.toString().toDouble().kilograms

            //val client = HealthConnectClient.getOrCreate(requireContext())
            //insertData(client, height, weight)

            //heightEditText.setText("")
            //weightEditText.setText("")
            //weightEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        //資料按鈕intent
        view.findViewById<Button>(R.id.stepsButton).setOnClickListener {
            startActivity(Intent(requireContext(),StepsActivity::class.java ))
        }
        view.findViewById<Button>(R.id.heartrateButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.caloriesButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.bloodpressureButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.oxygensaturationButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.bloodglucoseButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.speedButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.respiratoryrateButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.hydrationButton).setOnClickListener {
        }
        view.findViewById<Button>(R.id.sleepstageButton).setOnClickListener {
        }


        //heightTextView = view.findViewById(R.id.heightTodayValue)
        //weightTextView = view.findViewById(R.id.weightTodayValue)

        //heightAverageTextView = view.findViewById(R.id.heightAverageValue)
        //weightAverageTextView = view.findViewById(R.id.weightAverageValue)
        return view
    }

    private fun insertData(client: HealthConnectClient, height: Length, weight: Mass) {
        val startTime = ZonedDateTime.now().minusSeconds(1).toInstant()
        val endTime = ZonedDateTime.now().toInstant()

        val records = listOf(
            HeightRecord(
                height = height,
                time = endTime,
                zoneOffset = null,
            ),
            WeightRecord(
                weight = weight,
                time = endTime,
                zoneOffset = null,
            )
        )

        lifecycleScope.launch {
            val insertRecords = client.insertRecords(records)

            if (insertRecords.recordIdsList.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Records inserted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            readData(client)
        }
    }

    suspend fun readData(client: HealthConnectClient) {
        readDailyRecords(client)
        readAggregatedData(client)
    }

    private suspend fun readDailyRecords(client: HealthConnectClient) {
        val view=getView()

        //val today = ZonedDateTime.now()
        //val startOfDay = today.truncatedTo(ChronoUnit.DAYS)
        //val timeRangeFilter = TimeRangeFilter.between(
        //    startOfDay.toLocalDateTime(),
        //    today.toLocalDateTime()
        //)
        val start = ZonedDateTime.now().minusSeconds(1).toInstant()
        val end = ZonedDateTime.now().toInstant()

        val heightRecordRequest = ReadRecordsRequest(HeightRecord::class,TimeRangeFilter.between(start, end))
        val HeightToday = client.readRecords(heightRecordRequest).records
        if (view != null) {
            //heightTextView = view.findViewById(R.id.heightTodayValue)
            heightTextView.text = HeightToday[0].height.toString()
        }

        val weightRecordRequest = ReadRecordsRequest(WeightRecord::class,TimeRangeFilter.between(start, end))
        val WeightToday = client.readRecords(weightRecordRequest).records
        if (view != null) {
            //weightTextView = view.findViewById(R.id.weightTodayValue)
            weightTextView.text = WeightToday[0].weight.toString()
        }
    }

    private suspend fun readAggregatedData(client: HealthConnectClient) {
        val view=getView()

        val today = ZonedDateTime.now()
        val startOfDayOfThisMonth = today.withDayOfMonth(1)
            .truncatedTo(ChronoUnit.DAYS)
        //用在算平均的分母，暫時用不到
            //val elapsedDaysInMonth = Duration.between(startOfDayOfThisMonth, today).toDays() + 1
        val timeRangeFilter = TimeRangeFilter.between(
            startOfDayOfThisMonth.toInstant(),
            today.toInstant()
        )

        val data = client.aggregate(
            AggregateRequest(
                metrics = setOf(
                    HeightRecord.HEIGHT_AVG,
                    WeightRecord.WEIGHT_AVG
                ),
                timeRangeFilter = timeRangeFilter,
            )
        )

        val height = data[HeightRecord.HEIGHT_AVG]
        if (view != null) {
            //heightTextView = view.findViewById(R.id.heightTodayValue)
            //heightAverageTextView.text = String.format("%.3f",height) //format會出bug
            heightAverageTextView.text =height.toString()
        }

        val weight = data[WeightRecord.WEIGHT_AVG]
        if (view != null) {
            //weightTextView = view.findViewById(R.id.weightTodayValue)
            //weightAverageTextView.text = String.format("%.3f",weight)
            weightAverageTextView.text =weight.toString()
        }
    }

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