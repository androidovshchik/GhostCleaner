package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_cool.*
import org.jetbrains.anko.intentFor

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CoolFragment : BaseFragment<Int>() {

    override var title = R.string.title_cooler

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
        beforeOptimize()
    }

    override fun beforeOptimize() {
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
    }

    override fun afterOptimize() {
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
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