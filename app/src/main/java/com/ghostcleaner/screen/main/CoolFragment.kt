package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.CoolManager
import com.ghostcleaner.service.D
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_cool.*
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CoolFragment : BaseFragment<Int>() {

    override var titleKey = "titleCooler"

    private lateinit var coolManager: CoolManager

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coolManager = CoolManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cool, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cool.setOnClickListener {
            context?.let {
                startActivityForResult(it.intentFor<ScanningActivity>(), REQUEST_ADS)
            }
        }
        if (BuildConfig.DEBUG) {
            btn_cooled.setOnClickListener {
                btn_cool?.performClick()
            }
        }
        beforeOptimize()
    }

    override fun beforeOptimize() {
        checkTemp((40..45).random())
        circleBar.progress = 0f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
        tv_status.text = D["coolOverheat"]
        tv_status.textColorResource = R.color.colorRed
        btn_cool.isVisible = true
        btn_cooled.isInvisible = true
        tv_bottom.text = D["coolLarge"]
    }

    override fun afterOptimize() {
        checkTemp((35..40).random())
        circleBar.progress = 100f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        tv_status.text = D["coolNormal"]
        tv_status.textColorResource = R.color.colorTeal
        btn_cool.isInvisible = true
        btn_cooled.isVisible = true
        tv_bottom.text = D["coolGood"]
    }

    private fun checkTemp(random: Int) {
        job.cancelChildren()
        launch {
            tv_temperature.text = "${coolManager.getCPUTemp(random)}°С"
        }
    }

    companion object {

        fun newInstance(): CoolFragment {
            return CoolFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}