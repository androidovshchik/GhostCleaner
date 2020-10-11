package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdmobClient
import kotlinx.android.synthetic.main.activity_done.*

class DoneActivity : BaseActivity() {

    private lateinit var preferences: Preferences

    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = Preferences(applicationContext)
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
        clickCount++
        if (clickCount <= 3) {
            if (AdmobClient.getInstance(applicationContext).showInterstitial()) {
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        ads_banner.destroy()
        super.onDestroy()
    }
}