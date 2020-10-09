package com.ghostcleaner.screen

import android.os.Bundle
import com.android.billingclient.api.BillingFlowParams
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import com.ghostcleaner.service.GooglePayClient
import kotlinx.android.synthetic.main.activity_buy.*

class BuyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        val gpClient = GooglePayClient.getInstance(applicationContext)
        lifecycle.addObserver(gpClient)
        btn_not_now.setOnClickListener {
            finish()
        }
        btn_disable.setOnClickListener {
            gpClient.launchBillingFlow(
                this, BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()
            )
        }
    }
}