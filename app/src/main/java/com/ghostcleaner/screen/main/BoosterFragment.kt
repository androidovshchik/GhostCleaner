package com.ghostcleaner.screen.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_booster.*
import org.jetbrains.anko.backgroundDrawable

class BoosterFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_booster, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(fl_clock.backgroundDrawable!!),
            Color.parseColor("#535353")
        )
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