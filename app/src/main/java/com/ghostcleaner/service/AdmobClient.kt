package com.ghostcleaner.service

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.ghostcleaner.BuildConfig
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.yandex.metrica.YandexMetrica
import timber.log.Timber

@Suppress("unused")
class AdmobClient private constructor(context: Context) : LifecycleObserver {

    val preferences = Preferences(context)

    val enableAds = MutableLiveData<Boolean>()

    private val adView = AdView(context).apply {
        adUnitId = context.getString(R.string.ads_banner)
        adSize = AdSize.LARGE_BANNER
        adListener = object : AdListener() {

            override fun onAdLoaded() {
                Timber.d("Banner: onAdLoaded")
            }

            override fun onAdOpened() {
                Timber.d("Banner: onAdOpened")
                reportBanner()
            }

            override fun onAdClicked() {
                Timber.d("Banner: onAdClicked")
            }

            override fun onAdLeftApplication() {
                Timber.d("Banner: onAdLeftApplication")
            }

            override fun onAdClosed() {
                Timber.d("Banner: onAdClosed")
            }

            override fun onAdFailedToLoad(error: LoadAdError?) {
                Timber.e("Banner: $error")
                hasInterstitialError = true
            }
        }
    }

    private val interstitialAd = InterstitialAd(context).apply {
        adUnitId = context.getString(R.string.ads_interstitial)
        adListener = object : AdListener() {

            override fun onAdLoaded() {
                Timber.d("Interstitial: onAdLoaded")
            }

            override fun onAdOpened() {
                Timber.d("Interstitial: onAdOpened")
                reportInterstitial()
            }

            override fun onAdClicked() {
                Timber.d("Interstitial: onAdClicked")
            }

            override fun onAdLeftApplication() {
                Timber.d("Interstitial: onAdLeftApplication")
            }

            override fun onAdClosed() {
                Timber.d("Interstitial: onAdClosed")
            }

            override fun onAdFailedToLoad(error: LoadAdError?) {
                Timber.e("Interstitial: $error")
                hasInterstitialError = true
            }
        }
    }

    val rewardedAd = RewardedAd(context, context.getString(R.string.ads_interstitial_video))

    private var hasInterstitialError = false

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
            val devices = listOf(
                "2F50B42D07B0A99307202272ACC0E627"
            )
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(devices)
                    .build()
            )
        }
    }

    fun loadBanner(): AdmobClient {
        if (preferences.enableAds) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
        return this
    }

    fun loadInterstitial(): AdmobClient {
        if (preferences.enableAds) {
            hasInterstitialError = false
            val adRequest = AdRequest.Builder().build()
            interstitialAd.loadAd(adRequest)
        }
        return this
    }

    fun loadRewarded(): AdmobClient {
        if (preferences.enableAds) {
            hasRewardedError = false
            val adRequest = AdRequest.Builder().build()
            rewardedAd.loadAd(adRequest, loadCallback)
        }
        return this
    }

    fun showBanner(container: ViewGroup, params: ViewGroup.LayoutParams) {
        hideBanner()
        if (preferences.enableAds) {
            container.addView(adView, params)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onResume() {
        if (adView.isAttachedToWindow) {
            adView.resume()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (adView.isAttachedToWindow) {
            adView.pause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (adView.isAttachedToWindow) {
            adView.destroy()
        }
        hideBanner()
    }

    fun hideBanner(container: ViewGroup) {
        container.removeView(adView)
    }

    fun hideBanner() {
        (adView.parent as? ViewGroup)?.removeView(adView)
    }

    fun showInterstitial(): Boolean {
        if (!preferences.enableAds || hasInterstitialError) {
            return true
        }
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
            return true
        }
        return false
    }

    inline fun showRewarded(activity: Activity, crossinline callback: () -> Unit): Boolean {
        if (!preferences.enableAds || hasRewardedError) {
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

    private fun reportInterstitial() {
        with(preferences) {
            val count = interstitialCount
            if (count <= 0) {
                YandexMetrica.reportEvent("ads_interstitial")
                interstitialCount = count + 1
            }
        }
    }

    private fun reportBanner() {
        with(preferences) {
            val count = bannerCount
            if (count < 10) {
                YandexMetrica.reportEvent("ads_banner")
                if (count == 9) {
                    YandexMetrica.reportEvent("ads_banner_10")
                }
                bannerCount = count + 1
            }
        }
    }

    companion object : Singleton<AdmobClient, Context>(::AdmobClient)
}