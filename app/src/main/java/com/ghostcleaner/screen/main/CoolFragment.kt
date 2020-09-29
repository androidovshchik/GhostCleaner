package com.ghostcleaner.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseFragment

class CoolFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_temperature, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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