package com.ghostcleaner.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.observeFreshly
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.CoolManager
import com.ghostcleaner.service.JunkManager
import kotlinx.android.synthetic.main.activity_scanning.*
import org.jetbrains.anko.intentFor

class ScanningActivity : BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        if (intent.getBooleanExtra("junk", false)) {
            setSubtitle(R.string.title_junk)
            val manager = JunkManager(applicationContext)
            manager.optimization.observeFreshly(this, {
                if (it != null) {
                    if (!tv_line1.text.isNullOrBlank()) {
                        if (!tv_line2.text.isNullOrBlank()) {
                            if (!tv_line3.text.isNullOrBlank()) {
                                if (!tv_line4.text.isNullOrBlank()) {
                                    tv_line1.text = tv_line2.text
                                    tv_line2.text = tv_line3.text
                                    tv_line3.text = tv_line4.text
                                    tv_line4.text = "• $it"
                                } else {
                                    tv_line4.text = "• $it"
                                }
                            } else {
                                tv_line3.text = "• $it"
                            }
                        } else {
                            tv_line2.text = "• $it"
                        }
                    } else {
                        tv_line1.text = "• $it"
                    }
                } else {
                    startActivity(intentFor<DoneActivity>("title" to R.string.title_junk))
                    finish()
                }
            })
            manager.optimize()
        } else {
            setSubtitle(R.string.title_cooler)
            val manager = CoolManager(applicationContext)
            manager.optimization.observeFreshly(this, {
                if (it < 0) {
                    startActivity(intentFor<DoneActivity>("title" to R.string.title_cooler))
                    finish()
                }
            })
            manager.optimize()
        }
    }
}