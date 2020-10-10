package com.ghostcleaner.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_cool.*

class CoolFragment : BaseFragment() {

    override var title = R.string.title_cooler

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cool, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cool.setOnClickListener {

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