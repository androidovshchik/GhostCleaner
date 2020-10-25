package com.ghostcleaner.screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.EXTRA_TITLE
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.BatteryMode
import com.ghostcleaner.service.D
import com.ghostcleaner.service.EnergyManager
import com.ghostcleaner.service.Optimization
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.activity_power.*
import org.jetbrains.anko.intentFor

@SuppressLint("SetTextI18n")
class PowerActivity : BaseActivity(), Optimization<Int> {

    private lateinit var preferences: Preferences

    private lateinit var energyManager: EnergyManager

    private lateinit var mode: BatteryMode

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    @Suppress("SpellCheckingInspection")
    private val grayColor = Color.parseColor("#4cffffff")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(applicationContext)
        energyManager = EnergyManager(applicationContext)
        mode = BatteryMode.valueOf(intent.getStringExtra("mode")!!)
        setContentView(R.layout.activity_power)
        setSubtitle(
            when (mode) {
                BatteryMode.ULTRA -> D["titleUltra"]
                BatteryMode.EXTREME -> D["titleExtreme"]
                else -> D["titleNormal"]
            }
        )
        val list = energyManager.getDescList(mode)
        tv_text1.text = list.getOrNull(0)
        tv_text2.text = list.getOrNull(1)
        tv_text3.text = list.getOrNull(2)
        tv_text4.text = list.getOrNull(3)
        btn_apply.setOnClickListener {
            preferences.batteryMode = mode.name
            tv_text1.setTextColor(grayColor)
            tv_text2.setTextColor(grayColor)
            tv_text3.setTextColor(grayColor)
            tv_text4.setTextColor(grayColor)
            energyManager.optimize(mode)
        }
        energyManager.optimization.observeFreshly(this, {
            onOptimize(it * 25)
            if (it >= 4) {
                afterOptimize()
            }
        })
        beforeOptimize()
    }

    override fun beforeOptimize() {
        circleBar.progressInner = 100f
        tv_percent.text = D["powAdd", energyManager.getBatteryTime(mode)]
        tv_up.isVisible = true
        btn_apply.isVisible = true
        tv_scanning.isInvisible = true
        tv_text1.setTextColor(Color.WHITE)
        tv_text2.setTextColor(Color.WHITE)
        tv_text3.setTextColor(Color.WHITE)
        tv_text4.setTextColor(Color.WHITE)
    }

    override fun onOptimize(value: Int) {
        circleBar.progressInner = value.toFloat()
        when (value) {
            25 -> {
                tv_percent.text = "$value%"
                tv_text1.setTextColor(Color.WHITE)
                tv_text2.setTextColor(grayColor)
                tv_text3.setTextColor(grayColor)
                tv_text4.setTextColor(grayColor)
            }
            50 -> {
                tv_percent.text = "$value%"
                tv_text2.setTextColor(Color.WHITE)
                tv_text2.setTextColor(Color.WHITE)
                tv_text3.setTextColor(grayColor)
                tv_text4.setTextColor(grayColor)
            }
            75 -> {
                tv_percent.text = "$value%"
                tv_text3.setTextColor(Color.WHITE)
                tv_text2.setTextColor(Color.WHITE)
                tv_text3.setTextColor(Color.WHITE)
                tv_text4.setTextColor(grayColor)
            }
            else -> {
                tv_percent.text = "$value%"
                tv_text4.setTextColor(Color.WHITE)
                tv_text2.setTextColor(Color.WHITE)
                tv_text3.setTextColor(Color.WHITE)
                tv_text4.setTextColor(Color.WHITE)
            }
        }
        tv_up.isInvisible = true
        btn_apply.isInvisible = true
        tv_scanning.isVisible = true
    }

    override fun afterOptimize() {
        startActivity(intentFor<DoneActivity>(EXTRA_TITLE to "titleBattery"))
        finish()
    }
}