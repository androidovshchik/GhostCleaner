package com.ghostcleaner.screen

import android.os.Bundle
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_offer.*
import org.jetbrains.anko.intentFor

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
    }
}