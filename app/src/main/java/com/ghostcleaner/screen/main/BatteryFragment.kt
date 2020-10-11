package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.screen.PowerActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.BatteryMode
import com.ghostcleaner.service.EnergyManager
import kotlinx.android.synthetic.main.fragment_battery.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class BatteryFragment : BaseFragment<Int>(), View.OnClickListener {

    override var title = R.string.title_battery

    private lateinit var energyManager: EnergyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        energyManager = EnergyManager(requireContext())
        lifecycle.addObserver(energyManager)
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_battery, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pb_outer1.setOnClickListener(this)
        pb_outer2.setOnClickListener(this)
        pb_outer3.setOnClickListener(this)
        energyManager.batteryChanges.observe(viewLifecycleOwner, {
            onOptimize(it)
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onOptimize(value: Int) {
        tv_percent.text = "$value%"
        afterOptimize()
        tv_time1.text = energyManager.getBatteryTime(BatteryMode.NORMAL)
        tv_time2.text = energyManager.getBatteryTime(BatteryMode.ULTRA)
        tv_time3.text = energyManager.getBatteryTime(BatteryMode.EXTREME)
    }

    override fun afterOptimize() {
        tv_time.text = energyManager.getBatteryTime()
    }

    override fun onClick(v: View?) {
        context?.let {
            if (energyManager.checkPermission(it.applicationContext, this)) {
                val mode = when (v?.id) {
                    R.id.pb_outer2 -> BatteryMode.ULTRA
                    R.id.pb_outer3 -> BatteryMode.EXTREME
                    else -> BatteryMode.NORMAL
                }
                startActivity(
                    it.intentFor<PowerActivity>("mode" to mode.name, "title" to title).newTask()
                )
            }
        }
    }

    override fun onDestroyView() {
        lifecycle.removeObserver(energyManager)
        super.onDestroyView()
    }

    companion object {

        fun newInstance(): BatteryFragment {
            return BatteryFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}