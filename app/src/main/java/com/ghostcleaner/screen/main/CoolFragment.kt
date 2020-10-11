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
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_cool.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CoolFragment : BaseFragment<Int>() {

    override var title = R.string.title_cooler

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

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
        circleBar.progress = 0f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
        tv_status.text = "Overheat"
        tv_status.textColorResource = R.color.colorRed
        btn_cool.isVisible = true
        btn_cooled.isInvisible = true
        tv_bottom.isVisible = false
    }

    override fun afterOptimize() {
        circleBar.progress = 100f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        tv_status.text = "NORMAL"
        tv_status.textColorResource = R.color.colorTeal
        btn_cool.isInvisible = true
        btn_cooled.isVisible = true
        tv_bottom.isVisible = true
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