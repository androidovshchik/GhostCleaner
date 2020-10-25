package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.AdmobClient
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        tv_back.setOnClickListener {
            finish()
        }
        val preferences = Preferences(applicationContext)
        preferences.enableAds = false
        AdmobClient.getInstance(applicationContext).enableAds.value = false
    }
}