package com.ghostcleaner.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_cpu_danger.*
import org.jetbrains.anko.intentFor

class CPUDangerFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cpu_danger, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_cool.setOnClickListener {
            context?.run {
                startActivity(intentFor<ScanningActivity>())
            }
        }
    }

    companion object {

        fun newInstance(): CPUDangerFragment {
            return CPUDangerFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}