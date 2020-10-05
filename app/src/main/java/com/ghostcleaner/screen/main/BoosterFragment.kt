package com.ghostcleaner.screen.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.BoostManager
import com.ghostcleaner.view.CircleProgress
import kotlinx.android.synthetic.main.fragment_booster.*
import org.jetbrains.anko.backgroundDrawable

class BoosterFragment : BaseFragment() {

    private lateinit var boostManager: BoostManager

    private val circleProgress by lazy { CircleProgress(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boostManager = BoostManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_booster, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            Color.parseColor("#535353")
        )
        btn_optimise.setOnClickListener {
            boostManager.optimize()
        }
        boostManager.percentData.observe(viewLifecycleOwner, {
            circleProgress.progressInner = it.toFloat()
        })
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