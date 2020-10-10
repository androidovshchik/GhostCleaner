package com.ghostcleaner.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_STORAGE
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.JunkManager
import kotlinx.android.synthetic.main.fragment_junk.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class JunkFragment : BaseFragment() {

    override var title = R.string.title_junk

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_junk, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_clean.setOnClickListener {
            context?.let {
                val manager = JunkManager.getInstance(it.applicationContext)
                if (manager.checkPermission(it.applicationContext, this)) {
                    startActivity(it.intentFor<ScanningActivity>("junk" to true).newTask())
                }
            }
        }
        beforeClean()
    }

    private fun beforeClean() {

    }

    private fun afterClean() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_STORAGE) {
            btn_clean?.performClick()
        }
    }

    companion object {

        fun newInstance(): JunkFragment {
            return JunkFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}