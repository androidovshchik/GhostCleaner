package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.BatteryManager

class PowerActivity : BaseActivity() {

    private lateinit var batteryManager: BatteryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        batteryManager = BatteryManager(applicationContext)
        setContentView(R.layout.activity_power)
    }
}