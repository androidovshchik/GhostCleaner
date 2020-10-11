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

    val rewardedAd = RewardedAd(context, context.getString(R.string.ads_interstitial_video))

    var hasRewardedError = false

    private val loadCallback = object : RewardedAdLoadCallback() {

        override fun onRewardedAdLoaded() {
            Timber.d("onRewardedAdLoaded")
        }

        override fun onRewardedAdFailedToLoad(error: LoadAdError) {
            Timber.e(error.toString())
            hasRewardedError = true
        }
    }

    init {
        MobileAds.initialize(context)
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("3E7067504195846FB68B79AC74603642"))
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

    fun loadRewarded() {
        if (preferences.enableAds) {
            hasRewardedError = false
            rewardedAd.loadAd(AdRequest.Builder().build(), loadCallback)
        }
    }

    inline fun showRewarded(activity: Activity, crossinline done: () -> Unit): Boolean {
        if (hasRewardedError) {
            done()
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
                    done()
                }

                override fun onRewardedAdFailedToShow(error: AdError) {
                    Timber.e(error.toString())
                    done()
                }
            })
            return true
        }
        return false
    }

    companion object : Singleton<AdmobClient, Context>(::AdmobClient)
}