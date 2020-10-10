package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * see https://github.com/android/play-billing-samples
 */
class GooglePayClient private constructor(context: Context) : CoroutineScope, LifecycleObserver,
    PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener {

    val purchasesList = MutableLiveData<List<Purchase>>()

    val skuWithDetails = MutableLiveData<Map<String, SkuDetails>>()

    private val job = SupervisorJob()

    private var billingClient: BillingClient? = null

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases() // Not used for subscriptions.
            .setListener(this)
            .build()
        if (billingClient?.isReady == false) {
            billingClient?.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Timber.d("onBillingSetupFinished: ${billingResult.responseCode} ${billingResult.debugMessage}")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            querySkuDetails()
            queryPurchases()
        }
    }

    /**
     * In order to make purchasese, you need the [SkuDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onSkuDetailsResponse].
     */
    fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(listOf("shop_off_ads"))
            .build()
        billingClient?.querySkuDetailsAsync(params, this)
    }

    /**
     * Receives the result from [querySkuDetails].
     *
     * Store the SkuDetails and post them in the [skuWithDetails]. This allows other parts
     * of the app to use the [SkuDetails] to show SKU information and make purchases.
     */
    override fun onSkuDetailsResponse(
        billingResult: BillingResult,
        skuDetailsList: MutableList<SkuDetails>?
    ) {
        Timber.d("onSkuDetailsResponse: ${billingResult.responseCode} ${billingResult.debugMessage}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (skuDetailsList == null) {
                    skuWithDetails.postValue(emptyMap())
                } else
                    skuWithDetails.postValue(HashMap<String, SkuDetails>().apply {
                        for (details in skuDetailsList) {
                            put(details.sku, details)
                        }
                        Timber.i("onSkuDetailsResponse: count $size")
                    })
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> {
                Timber.e("onSkuDetailsResponse: $responseCode $debugMessage")
            }
            BillingClient.BillingResponseCode.USER_CANCELED,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // These response codes are not expected.
                Timber.wtf("onSkuDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    fun queryPurchases() {
        if (billingClient?.isReady == false) {
            Timber.e("queryPurchases: BillingClient is not ready")
        }
        val result = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)
        purchasesList.postValue(result?.purchasesList)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Timber.d("onPurchasesUpdated: ${billingResult.responseCode} ${billingResult.debugMessage}")
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchasesList.postValue(purchases)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("onPurchasesUpdated: User canceled the purchase")
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Timber.i("onPurchasesUpdated: The user already owns this item")
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Timber.e("onPurchasesUpdated: Developer error means that Google Play does not recognize the configuration. If you are just getting started, make sure you have configured the application correctly in the Google Play Console. The SKU product ID must match and the APK you are using must be signed with release keys.")
            }
        }
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity): Int? {
        val sku = params.sku
        val oldSku = params.oldSku
        Timber.i("launchBillingFlow: sku: $sku, oldSku: $oldSku")
        if (billingClient?.isReady == false) {
            Timber.e("launchBillingFlow: BillingClient is not ready")
        }
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(listOf("shop_off_ads"))
            .build()
        val billingResult = billingClient?.launchBillingFlow(
            activity, BillingFlowParams.newBuilder()
                .setSkuDetails(params)
                .build()
        )
        val responseCode = billingResult?.responseCode
        val debugMessage = billingResult?.debugMessage
        Timber.d("launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    override fun onBillingServiceDisconnected() {
        job.cancelChildren()
        launch {
            var currentDelay = 1000L
            while (true) {
                billingClient?.startConnection(this@GooglePayClient)
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        job.cancelChildren()
        if (billingClient?.isReady == true) {
            billingClient?.endConnection()
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }

    companion object : Singleton<GooglePayClient, Context>(::GooglePayClient)
}
