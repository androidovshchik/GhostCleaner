package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdmobClient
import kotlinx.android.synthetic.main.activity_done.*

class DoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        btn_home.setOnClickListener {
            onBackPressed()
        }
        AdmobClient.getInstance(applicationContext)
            .loadBanner(ads_banner)
    }

    override fun onResume() {
        super.onResume()
        ads_banner.resume()
    }

    override fun onPause() {
        ads_banner.pause()
        super.onPause()
    }

    override fun onBackPressed() {
        if (AdmobClient.getInstance(applicationContext).showInterstitial()) {
            finish()
        }
    }

    override fun onDestroy() {
        ads_banner.destroy()
        super.onDestroy()
    }
}