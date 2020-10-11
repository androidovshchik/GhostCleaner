package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.ghostcleaner.R
import com.ghostcleaner.REQUEST_ADS
import com.ghostcleaner.REQUEST_STORAGE
import com.ghostcleaner.extension.setTintCompat
import com.ghostcleaner.screen.ScanningActivity
import com.ghostcleaner.screen.base.BaseFragment
import com.ghostcleaner.service.JunkManager
import com.ghostcleaner.view.CircleBar
import kotlinx.android.synthetic.main.fragment_junk.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textColorResource

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class JunkFragment : BaseFragment<Int>() {

    override var title = R.string.title_junk

    private lateinit var junkManager: JunkManager

    private val circleBar by lazy { CircleBar(pb_outer, pb_inner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        junkManager = JunkManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, root: ViewGroup?, bundle: Bundle?): View {
        return inflater.inflate(R.layout.fragment_junk, root, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_clean.setOnClickListener {
            context?.let {
                if (junkManager.checkPermission(it.applicationContext, this)) {
                    startActivityForResult(
                        it.intentFor<ScanningActivity>("junk" to true),
                        REQUEST_ADS
                    )
                }
            }
        }
        beforeOptimize()
    }

    override fun beforeOptimize() {
        circleBar.progress = 0f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorRed))
        tv_size.text = "91 MB"
        tv_size.textColorResource = R.color.colorRed
        btn_clean.isVisible = true
        btn_cleaned.isInvisible = true
    }

    override fun afterOptimize() {
        circleBar.progress = 100f
        iv_temperature.drawable?.setTintCompat(resources.getColor(R.color.colorTeal))
        tv_size.text = "CRYSTAL CLEAR"
        tv_size.textColorResource = R.color.colorTeal
        btn_clean.isInvisible = true
        btn_cleaned.isVisible = true
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