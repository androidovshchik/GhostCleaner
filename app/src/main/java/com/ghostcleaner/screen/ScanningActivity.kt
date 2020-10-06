package com.ghostcleaner.screen

import android.os.Bundle
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.CoolManager
import com.ghostcleaner.service.JunkManager
import kotlinx.android.synthetic.main.activity_scanning.*

class ScanningActivity : BaseActivity() {

    private val coolManager: CoolManager by lazy { CoolManager(applicationContext) }

    private val junkManager: JunkManager by lazy { JunkManager(applicationContext) }

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        if (intent.getBooleanExtra("has_list", false)) {
            junkManager.pathData.observeFreshly(this, {
                if (it != null) {
                    if (!tv_line2.text.isNullOrBlank()) {
                        tv_line1.text = tv_line2.text
                        if (!tv_line3.text.isNullOrBlank()) {
                            tv_line2.text = tv_line3.text
                            if (!tv_line4.text.isNullOrBlank()) {
                                tv_line3.text = tv_line4.text
                                tv_line4.text = it
                            } else {
                                tv_line3.text = it
                            }
                        } else {
                            tv_line2.text = it
                        }
                    } else {
                        tv_line1.text = it
                    }
                } else {
                    finish()
                }
            })
            junkManager.optimize()
        } else {
            coolManager.progressData.observeFreshly(this, {
                if (it < 0) {
                    finish()
                }
            })
            coolManager.optimize()
        }
    }
}