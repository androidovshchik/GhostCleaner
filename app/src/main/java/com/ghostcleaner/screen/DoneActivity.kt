package com.ghostcleaner.screen

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdmobClient
import kotlinx.android.synthetic.main.activity_done.*
import org.jetbrains.anko.wrapContent

class DoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        btn_home.setOnClickListener {
            onBackPressed()
        }
        val params = ConstraintLayout.LayoutParams(wrapContent, wrapContent).apply {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        AdmobClient.getInstance(applicationContext)
            .showBanner(cl_done, params)
        lifecycle.addObserver(AdmobClient.getInstance(applicationContext))
    }

    override fun onBackPressed() {
        if (AdmobClient.getInstance(applicationContext).showInterstitial()) {
            finish()
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(AdmobClient.getInstance(applicationContext))
        AdmobClient.getInstance(applicationContext)
            .hideBanner(cl_done)
        super.onDestroy()
    }
}