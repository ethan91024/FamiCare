package com.ethan.FamiCare.Health

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ethan.FamiCare.R

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

        //資料按鈕intent
        view.findViewById<TextView>(R.id.stepsButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthStepsActivity::class.java))
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
        view.findViewById<TextView>(R.id.sleepstageButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthSleepActivity::class.java))
        }
        view.findViewById<TextView>(R.id.respiratoryrateButton).setOnClickListener {
            startActivity(Intent(requireContext(), HealthRespiratoryRateActivity::class.java))
        }


        //更新標題
        requireActivity().title = "健康"

        return view
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