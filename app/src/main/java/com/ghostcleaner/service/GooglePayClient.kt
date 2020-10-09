/*
 * Copyright 2018 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import timber.log.Timber

/**
 * see https://github.com/android/play-billing-samples
 */
class GooglePayClient private constructor(
    private val context: Context
) : LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    SkuDetailsResponseListener {

    /**
     * The purchase event is observable. Only one oberver will be notified.
     */
    val purchaseUpdateEvent = MutableLiveData<List<Purchase>>()

    /**
     * Purchases are observable. This list will be updated when the Billing Library
     * detects new or existing purchases. All observers will be notified.
     */
    val purchases = MutableLiveData<List<Purchase>>()

    /**
     * SkuDetails for all known SKUs.
     */
    val skuWithDetails = MutableLiveData<Map<String, SkuDetails>>()

    private var billingClient: BillingClient? = null

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        // Create a new BillingClient in onCreate().
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()
        if (billingClient?.isReady == false) {
            Timber.d("BillingClient starting connection")
            billingClient?.startConnection(this)
        }
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (billingClient?.isReady == true) {
            Timber.d("BillingClient closing connection")
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            billingClient?.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.
            querySkuDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected")
        // TODO: Try connecting again with exponential backoff.
        // billingClient.startConnection(this)
    }

    /**
     * In order to make purchasese, you need the [SkuDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onSkuDetailsResponse].
     */
    fun querySkuDetails() {
        Timber.d("querySkuDetails")
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
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.i("onSkuDetailsResponse: $responseCode $debugMessage")
                if (skuDetailsList == null) {
                    Timber.w("onSkuDetailsResponse: null SkuDetails list")
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
        if (!billingClient.isReady) {
            Timber.e("queryPurchases: BillingClient is not ready")
        }
        Timber.d("queryPurchases: SUBS")
        val result = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)
        if (result == null) {
            Timber.i("queryPurchases: null purchase result")
            processPurchases(null)
        } else {
            if (result.purchasesList == null) {
                Timber.i("queryPurchases: null purchase list")
                processPurchases(null)
            } else {
                processPurchases(result.purchasesList)
            }
        }
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Timber.d("onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
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
     * Send purchase SingleLiveEvent and update purchases LiveData.
     *
     * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
     * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Timber.d("processPurchases: ${purchasesList?.size} purchase(s)")
        if (isUnchangedPurchaseList(purchasesList)) {
            Timber.d("processPurchases: Purchase list has not changed")
            return
        }
        purchaseUpdateEvent.postValue(purchasesList)
        purchases.postValue(purchasesList)
        purchasesList?.let {
            logAcknowledgementStatus(purchasesList)
        }
    }

    /**
     * Check whether the purchases have changed before posting changes.
     */
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>?): Boolean {
        // TODO: Optimize to avoid updates with identical data.
        return false
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        var ack_yes = 0
        var ack_no = 0
        for (purchase in purchasesList) {
            if (purchase.isAcknowledged) {
                ack_yes++
            } else {
                ack_no++
            }
        }
        Timber.d("logAcknowledgementStatus: acknowledged=$ack_yes unacknowledged=$ack_no")
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int? {
        val sku = params.sku
        val oldSku = params.oldSku
        Timber.i("launchBillingFlow: sku: $sku, oldSku: $oldSku")
        if (billingClient?.isReady == false) {
            Timber.e("launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient?.launchBillingFlow(activity, params)
        val responseCode = billingResult?.responseCode
        val debugMessage = billingResult?.debugMessage
        Timber.d("launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Acknowledge a purchase.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     *
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     *
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    fun acknowledgePurchase(purchaseToken: String) {
        Timber.d("acknowledgePurchase")
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient?.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            Timber.d("acknowledgePurchase: $responseCode $debugMessage")
        }
    }

    companion object : Singleton<GooglePayClient, Context>(::GooglePayClient)
}
