package com.ghostcleaner.screen

import android.os.Bundle
import androidx.core.view.isVisible
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdsLoader
import kotlinx.android.synthetic.main.activity_done.*

class DoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        btn_home.setOnClickListener {
            onBackPressed()
        }
        // todo
        if (true) {
            AdsLoader.getInstance(applicationContext)
                .loadBanner(ads_banner)
        } else {
            ads_banner.isVisible = false
        }
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
        // todo
        if (true) {
            AdsLoader.getInstance(applicationContext).showRewarded(this) {
                finish()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        ads_banner.destroy()
        super.onDestroy()
    }
}