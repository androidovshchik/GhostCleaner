package com.ghostcleaner.screen

import android.os.Bundle
import android.os.Handler
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import org.jetbrains.anko.startActivity

class ScanningActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        Handler().postDelayed({
            startActivity<DoneActivity>()
        }, 2000)
    }
}