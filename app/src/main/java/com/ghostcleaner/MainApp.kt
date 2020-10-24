package com.ghostcleaner

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.ghostcleaner.extension.isOreoPlus
import com.ghostcleaner.service.D
import com.ghostcleaner.service.RemindReceiver
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.jetbrains.anko.notificationManager
import timber.log.Timber

@Suppress("unused")
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (isOreoPlus()) {
            notificationManager.createNotificationChannel(
                NotificationChannel("main", "Main", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        D.initialize(applicationContext)
        AndroidThreeTen.init(this)
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("font/SFUIText-Light.ttf")
                            .build()
                    )
                )
                .build()
        )
        val config = YandexMetricaConfig.newConfigBuilder("16f8ee36-4b17-4fff-b517-11a704de9e36")
            .build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
        RemindReceiver.setAlarm(applicationContext)
    }
}