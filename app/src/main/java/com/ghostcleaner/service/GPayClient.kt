package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.SKU_ADS
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("unused")
class GPayClient private constructor(context: Context) : BillingProcessor.IBillingHandler {

    private val reference = WeakReference(context)

    private val billing = BillingProcessor(context, null, this)

    val purchaseSku = MutableLiveData<String>()

    init {
        billing.initialize()
    }

    fun purchase(activity: Activity, productId: String) {
        billing.purchase(activity, productId)
    }

    override fun onBillingInitialized() {
        billing.loadOwnedPurchasesFromGoogle()
    }

    override fun onPurchaseHistoryRestored() {
        if (billing.isPurchased(SKU_ADS)) {
            reference.get()?.run {
                AdmobClient.getInstance(applicationContext).disableAds()
            }
        }
    }

    override fun onProductPurchased(id: String, details: TransactionDetails?) {
        if (id == SKU_ADS) {
            reference.get()?.run {
                AdmobClient.getInstance(applicationContext).disableAds()
                purchaseSku.postValue(SKU_ADS)
            }
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Timber.e("onBillingError errorCode=$errorCode")
        Timber.e(error)
        if (BuildConfig.DEBUG) {
            onProductPurchased(SKU_ADS, null)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        billing.handleActivityResult(requestCode, resultCode, data)
    }

    companion object : Singleton<GPayClient, Context>(::GPayClient)
}