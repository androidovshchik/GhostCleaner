package com.ghostcleaner.screen

import android.os.Bundle
import android.os.CountDownTimer
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_offer.*
import org.jetbrains.anko.intentFor
import java.util.concurrent.TimeUnit

class OfferActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)
        ib_close.setOnClickListener {
            finish()
        }
        btn_disable.setOnClickListener {
            startActivity(intentFor<BuyActivity>().putExtras(intent))
            finish()
        }
        val min = TimeUnit.MINUTES.toMillis(5)
        val max = TimeUnit.MINUTES.toMillis(10)
        val total = (min..max).random()
        object : CountDownTimer(total, 1000) {

            override fun onTick(millis: Long) {
                val seconds = millis / 1000
                tv_time.text = String.format("00:%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }
}