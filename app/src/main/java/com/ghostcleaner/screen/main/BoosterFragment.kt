package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import com.ghostcleaner.R
import com.ghostcleaner.extension.formatAsFileSize
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.BoostManager
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_booster.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColorResource

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class BoosterFragment : BaseFragment() {

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
        btn_optimize.setOnClickListener {
            boostManager.optimize()
        }
        boostManager.progressData.observe(viewLifecycleOwner, {
            if (it >= 0) {
                onOptimize(it)
            } else {
                afterOptimize()
            }
        })
        beforeOptimize()
    }

    private fun beforeOptimize() {
        val (used, total) = boostManager.memory
        val percent = 100f * used / total
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            Color.parseColor("#535353")
        )
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorRed
        tv_memory.text = used.formatAsFileSize
        tv_memory.textColorResource = R.color.colorAccent
        tv_status.text = "Found"
        tv_status.textColorResource = R.color.colorRed
        btn_optimize.isInvisible = false
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = true
    }

    private fun onOptimize(progress: Float) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            resources.getColor(R.color.colorTeal)
        )
        circleBar.colorInner = R.color.colorTeal
        circleBar.progressInner = progress
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = "Optimizing..."
        tv_memory.setTextColor(Color.WHITE)
        tv_status.text = "Found"
        tv_status.textColorResource = R.color.colorTeal
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = false
        btn_optimized.isInvisible = true
    }

    private fun afterOptimize() {
        val (used, total) = boostManager.memory
        val percent = 100f * used / total
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            Color.parseColor("#535353")
        )
        circleBar.colorInner = R.color.colorRed
        circleBar.progress = percent
        tv_storage.textColorResource = R.color.colorTeal
        tv_memory.text = used.formatAsFileSize
        tv_memory.textColorResource = R.color.colorAccent
        tv_status.text = "z"
        tv_status.textColorResource = R.color.colorTeal
        btn_optimize.isInvisible = true
        tv_scanning.isInvisible = true
        btn_optimized.isInvisible = false
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