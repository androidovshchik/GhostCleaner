package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import com.ghostcleaner.R
import com.ghostcleaner.extension.formatAsFileSize
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.BoostManager
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_booster.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.textColorResource

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            Color.parseColor("#535353")
        )
        btn_optimise.setOnClickListener {
            boostManager.optimize()
        }
        boostManager.progressData.observe(viewLifecycleOwner, {
            if (it >= 0) {
                tv_storage.textColorResource = R.color.colorTeal
                tv_memory.text = "Optimizing..."
                tv_memory.setTextColor(Color.WHITE)
                tv_status.textColorResource = R.color.colorTeal
                circleBar.colorInner = R.color.colorTeal
                circleBar.progressInner = it
            } else {
                updateMemory()
            }
        })
        updateMemory()
    }

    private fun updateMemory() {
        val (available, total) = boostManager.memory
        tv_storage.textColorResource = R.color.colorRed
        tv_memory.text = available.formatAsFileSize
        tv_memory.textColorResource = R.color.colorAccent
        tv_status.textColorResource = R.color.colorRed
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