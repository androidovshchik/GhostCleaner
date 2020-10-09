package com.ghostcleaner.screen

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.BatteryMode
import com.ghostcleaner.service.EnergyManager
import kotlinx.android.synthetic.main.activity_power.*
import kotlinx.android.synthetic.main.activity_scanning.*

class PowerActivity : BaseActivity() {

    @Suppress("SpellCheckingInspection")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power)
        val grayColor = Color.parseColor("#4cffffff")
        val mode = BatteryMode.valueOf(intent.getStringExtra("mode")!!)
        val manager = EnergyManager.getInstance(applicationContext)
        btn_apply.setOnClickListener {
            tv_line1.setTextColor(grayColor)
            tv_line2.setTextColor(grayColor)
            tv_line3.setTextColor(grayColor)
            tv_line4.setTextColor(grayColor)
            manager.optimize(mode)
        }
        val list = manager.getDescList(mode)
        tv_line1.text = list.getOrNull(0)
        tv_line2.text = list.getOrNull(1)
        tv_line3.text = list.getOrNull(2)
        tv_line4.text = list.getOrNull(3)
        manager.optimization.observeFreshly(this, {
            when (it) {
                1 -> {
                    tv_line1.setTextColor(Color.WHITE)
                }
                2 -> {
                    tv_line2.setTextColor(Color.WHITE)
                }
                3 -> {
                    tv_line3.setTextColor(Color.WHITE)
                }
                else -> {
                    tv_line4.setTextColor(Color.WHITE)
                }
            }
        })
    }
}