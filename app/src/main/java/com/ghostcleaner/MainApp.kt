package com.ghostcleaner

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import timber.log.Timber

@Suppress("unused")
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
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
    }
}