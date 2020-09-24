package com.ghostcleaner

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.BatteryManager
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.UiThread
import com.ghostcleaner.extension.appWidgetManager
import org.jetbrains.anko.batteryManager
import org.threeten.bp.LocalTime
import timber.log.Timber

class WidgetClock : AppWidgetProvider() {

    @UiThread
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        with(context) {
            val time = LocalTime.now().format(timeFormatter)
            val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            for (id in ids) {
                updateWidget(id, time, level)
            }
        }
    }

    @UiThread
    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        id: Int,
        newOptions: Bundle?
    ) {
        onUpdate(context, manager, intArrayOf(id))
    }

    @UiThread
    override fun onReceive(context: Context, intent: Intent) {
        with(context) {
            when (val action = intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED,
                Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW,
                Intent.ACTION_BATTERY_OKAY, Intent.ACTION_SCREEN_ON -> {
                    Timber.d("onReceive action=$action")
                    val ids = appWidgetManager.getAppWidgetIds(widget)
                    if (ids.isNotEmpty()) {
                        onUpdate(applicationContext, appWidgetManager, ids)
                    }
                }
                else -> Timber.d("onReceive action=$action")
            }
            super.onReceive(context, intent)
        }
    }

    companion object {

        private val Context.widget: ComponentName
            get() = ComponentName(applicationContext, WidgetClock::class.java)

        private fun Context.updateWidget(id: Int, time: String, level: Int) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val isPortrait = resources.configuration.orientation == ORIENTATION_PORTRAIT
            val width = options.getInt(
                if (isPortrait) {
                    AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
                } else {
                    AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
                }
            )
            Timber.d("updateWidget isPortrait=$isPortrait width=$width")
            appWidgetManager.updateAppWidget(
                id, RemoteViews(packageName, R.layout.widget_clock).apply {
                    setTextViewText(R.id.tv_clock, time)
                    setTextViewText(R.id.tv_time, "")
                    setTextViewText(R.id.tv_battery, "$level%")
                }
            )
        }
    }
}