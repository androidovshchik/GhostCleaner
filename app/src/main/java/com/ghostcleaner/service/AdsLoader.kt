package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import timber.log.Timber

class AdsLoader(context: Context) {

    val rewardedAd = RewardedAd(context, context.getString(R.string.ads_interstitial_video))

    private val loadCallback = object : RewardedAdLoadCallback() {

        override fun onRewardedAdLoaded() {
            Timber.d("onRewardedAdLoaded")
        }

        override fun onRewardedAdFailedToLoad(error: LoadAdError) {
            Timber.e(error.toString())
        }
    }

    init {
        MobileAds.initialize(context)
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                // todo
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("ABC"))
                    .build()
            )
        }
    }

    fun loadVideo() {
        rewardedAd.loadAd(AdRequest.Builder().build(), loadCallback)
    }

    inline fun showVideo(activity: Activity, crossinline onAdClosed: () -> Unit): Boolean {
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
                    onAdClosed()
                }

                override fun onRewardedAdFailedToShow(error: AdError) {
                    Timber.e(error.toString())
                }
            })
            return true
        }
        return false
    }

    fun loadBanner(view: AdView) {
        val adRequest = AdRequest.Builder().build()
        view.loadAd(adRequest)
    }

    companion object : Singleton<AdsLoader, Context>(::AdsLoader)
}