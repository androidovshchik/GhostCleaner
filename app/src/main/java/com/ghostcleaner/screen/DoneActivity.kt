package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.android.synthetic.main.activity_done.*

class DoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        MobileAds.initialize(applicationContext)
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("ABC"))
                    .build()
            )
        }
        btn_home.setOnClickListener {
            onBackPressed()
        }
        val adRequest = AdRequest.Builder().build()
        ads_banner.loadAd(adRequest)
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
    }

    override fun onDestroy() {
        ads_banner.destroy()
        super.onDestroy()
    }
}