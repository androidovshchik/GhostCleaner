package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.format.MyFormatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.DoneActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.BoostManager
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_booster.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BoosterFragment : BaseFragment<Float>() {

    override var title = R.string.title_booster

    private lateinit var boostManager: BoostManager

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boostManager = BoostManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_booster, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_optimize.setOnClickListener {
            boostManager.optimize()
        }
        if (BuildConfig.DEBUG) {
            btn_optimized.setOnClickListener {
                btn_optimize?.performClick()
            }
        }
        boostManager.optimization.observeFreshly(viewLifecycleOwner, { value ->
            if (value >= 0) {
                onOptimize(value)
            } else {
                context?.let {
                    startActivityForResult(
                        it.intentFor<DoneActivity>("title" to title),
                        REQUEST_ADS
                    )
                }
            }
        })
        beforeOptimize()
    }

    override fun beforeOptimize() {
        val (used, total) = boostManager.memorySizes
        val percent = 100f * used / total
        fl_clock.backgroundDrawable?.setTintCompat(Color.parseColor("#535353"))
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorRed
        tv_memory.text = MyFormatter.formatFileSize(context, used)
        tv_memory.textColorResource = R.color.colorAccent
        tv_status.isVisible = true
        tv_status.text = "Found"
        tv_status.textColorResource = R.color.colorRed
        btn_optimize.isInvisible = false
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = true
        updateBottom(used, total)
    }

    override fun onOptimize(value: Float) {
        fl_clock.backgroundDrawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        circleBar.colorInner = R.color.colorTeal
        circleBar.progressInner = value
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = "Optimizing..."
        tv_memory.setTextColor(Color.WHITE)
        tv_status.isVisible = true
        tv_status.text = "Found"
        tv_status.textColorResource = R.color.colorTeal
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = false
        btn_optimized.isInvisible = true
    }

    override fun afterOptimize() {
        val (used, total) = boostManager.memorySizes
        val percent = 100f * used / total
        fl_clock.backgroundDrawable?.setTintCompat(Color.parseColor("#535353"))
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = MyFormatter.formatFileSize(context, used)
        tv_memory.setTextColor(Color.WHITE)
        tv_status.isVisible = false
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = false
        updateBottom(used, total)
    }

    private fun updateBottom(used: Long, total: Long) {
        val percent = 100f * used / total
        val count = boostManager.list3rdPartyApps().size
        val ratio = "${MyFormatter.formatFileSize(context, used)}/ ${MyFormatter.formatFileSize(context, total)}"
        tv_ratio1.text = ratio
        tv_ratio2.text = ratio
        tv_count.text = count.toString()
        tv_percent.text = "${percent.roundToInt()}%"
    }

    companion object {

        fun newInstance(): BoosterFragment {
            return BoosterFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}