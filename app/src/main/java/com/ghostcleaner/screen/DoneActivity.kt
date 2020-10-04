package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity

class DoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
    }
}