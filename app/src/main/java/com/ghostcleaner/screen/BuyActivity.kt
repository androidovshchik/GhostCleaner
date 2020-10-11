package com.ghostcleaner.screen

import android.content.Intent
import android.os.Bundle
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.ghostcleaner.R
import com.ghostcleaner.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_buy.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import timber.log.Timber

class BuyActivity : BaseActivity() {

    private lateinit var billing: BillingProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billing =
            BillingProcessor(applicationContext, null, object : BillingProcessor.IBillingHandler {

                override fun onBillingInitialized() {}

                override fun onPurchaseHistoryRestored() {}

                override fun onProductPurchased(id: String, details: TransactionDetails?) {
                    if (id == "shop_off_ads") {
                        startActivity(intentFor<SuccessActivity>().newTask())
                        finish()
                    }
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {
                    Timber.e("onBillingError errorCode=$errorCode")
                    Timber.e(error)
                }
            })
        setContentView(R.layout.activity_buy)
        billing.initialize()
        btn_not_now.setOnClickListener {
            finish()
        }
        btn_disable.setOnClickListener {
            billing.purchase(this, "shop_off_ads")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!billing.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        billing.release()
        super.onDestroy()
    }
}