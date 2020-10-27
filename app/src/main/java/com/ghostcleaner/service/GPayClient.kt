package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.ghostcleaner.BuildConfig
import timber.log.Timber
import java.lang.ref.WeakReference

@Suppress("unused")
class GPayClient private constructor(context: Context) : LifecycleObserver,
    BillingProcessor.IBillingHandler {

    private val reference = WeakReference(context)

    val enableAds = MutableLiveData<Boolean>()

    private var billing: BillingProcessor? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        reference.get()?.let {
            billing = BillingProcessor(it.applicationContext, null, this)
            billing?.initialize()
        }
        Timber.e("initialize")
    }

    fun purchase(activity: Activity, productId: String) {
        billing?.purchase(activity, productId)
    }

    override fun onBillingInitialized() {
        billing?.loadOwnedPurchasesFromGoogle()
    }

    override fun onPurchaseHistoryRestored() {
        if (billing?.isPurchased("shop_off_ads") == true) {
            //onSuccess()
        }
    }

    override fun onProductPurchased(id: String, details: TransactionDetails?) {
        if (id == "shop_off_ads") {
            //onSuccess()
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Timber.e("onBillingError errorCode=$errorCode")
        Timber.e(error)
        if (BuildConfig.DEBUG) {
            //onSuccess()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        billing?.handleActivityResult(requestCode, resultCode, data)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        billing?.release()
        Timber.e("release")
    }

    companion object : Singleton<GPayClient, Context>(::GPayClient)
}