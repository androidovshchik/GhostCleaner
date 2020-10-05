package com.ghostcleaner.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.screen.PowerActivity
import com.ghostcleaner.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_battery.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class BatteryFragment : BaseFragment(), View.OnClickListener {

    override var title = R.string.title_battery

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_battery, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pb_outer1.setOnClickListener(this)
        pb_outer2.setOnClickListener(this)
        pb_outer3.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        context?.run {
            when (v?.id) {
                R.id.pb_outer1, R.id.pb_outer2, R.id.pb_outer3 -> {
                    startActivity(intentFor<PowerActivity>("id" to v.id).newTask())
                }
            }
        }
    }

    companion object {

        fun newInstance(): BatteryFragment {
            return BatteryFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}