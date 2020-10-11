package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import androidx.core.view.isVisible
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import timber.log.Timber

class AdmobClient private constructor(context: Context) {

    private val preferences = Preferences(context)

    val interstitialAd = InterstitialAd(context).apply {
        adUnitId = context.getString(R.string.ads_interstitial)
        adListener = object : AdListener() {

            override fun onAdLoaded() {
                Timber.d("onAdLoaded")
            }

            override fun onAdOpened() {
                Timber.d("onAdOpened")
            }

            override fun onAdClicked() {
                Timber.d("onAdClicked")
            }

            override fun onAdLeftApplication() {
                Timber.d("onAdLeftApplication")
            }

            override fun onAdClosed() {
                Timber.d("onAdClosed")
            }

            override fun onAdFailedToLoad(error: LoadAdError?) {
                Timber.e(error.toString())
                hasInterstitialError = true
            }
        }
    }

    val rewardedAd = RewardedAd(context, context.getString(R.string.ads_interstitial_video))

    var hasInterstitialError = false

    var hasRewardedError = false

    private val loadCallback = object : RewardedAdLoadCallback() {

        override fun onRewardedAdLoaded() {
            Timber.d("onRewardedAdLoaded")
        }

        override fun onRewardedAdFailedToLoad(error: LoadAdError?) {
            Timber.e(error.toString())
            hasRewardedError = true
        }
    }

    init {
        MobileAds.initialize(context)
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(
                        listOf(
                            "3E7067504195846FB68B79AC74603642",
                            "2F1523C62A4E9362F72E4166F56D4FFA"
                        )
                    )
                    .build()
            )
        }
    }

    fun loadBanner(view: AdView) {
        if (preferences.enableAds) {
            val adRequest = AdRequest.Builder().build()
            view.loadAd(adRequest)
        } else {
            view.isVisible = false
        }
    }

    fun loadInterstitial() {
        if (preferences.enableAds) {
            hasInterstitialError = false
            val adRequest = AdRequest.Builder().build()
            interstitialAd.loadAd(adRequest)
        }
    }

    fun loadRewarded() {
        if (preferences.enableAds) {
            hasRewardedError = false
            val adRequest = AdRequest.Builder().build()
            rewardedAd.loadAd(adRequest, loadCallback)
        }
    }

    inline fun showInterstitial(crossinline callback: () -> Unit): Boolean {
        if (hasInterstitialError) {
            callback()
            return true
        }
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
            return true
        }
        return false
    }

    inline fun showRewarded(activity: Activity, crossinline callback: () -> Unit): Boolean {
        if (hasRewardedError) {
            callback()
            return true
        }
        if (rewardedAd.isLoaded) {
            rewardedAd.show(activity, object : RewardedAdCallback() {

                override fun onRewardedAdOpened() {
                    Timber.d("onRewardedAdOpened")
                }

                override fun onUserEarnedReward(rewardItem: RewardItem) {
                    Timber.d("onUserEarnedReward")
                }

                override fun onRewardedAdClosed() {
                    Timber.d("onRewardedAdClosed")
                    callback()
                }

                override fun onRewardedAdFailedToShow(error: AdError?) {
                    Timber.e(error.toString())
                    callback()
                }
            })
            return true
        }
        return false
    }

    companion object : Singleton<AdmobClient, Context>(::AdmobClient)
}