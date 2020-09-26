package com.ghostcleaner

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.BatteryManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.annotation.UiThread
import androidx.core.app.AlarmManagerCompat
import com.ghostcleaner.extension.appWidgetManager
import com.ghostcleaner.extension.getComponent
import com.ghostcleaner.extension.pendingReceiverFor
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.batteryManager
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber

class WidgetClock : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        setAlarm(context)
    }

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
            val ids = appWidgetManager.getAppWidgetIds(getComponent<WidgetClock>())
            when (val action = intent.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED,
                Intent.ACTION_LOCALE_CHANGED, Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW,
                Intent.ACTION_BATTERY_OKAY, Intent.ACTION_SCREEN_ON -> {
                    Timber.d("onReceive action=$action")
                    if (ids.isNotEmpty()) {
                        onUpdate(applicationContext, appWidgetManager, ids)
                    }
                }
                else -> Timber.d("onReceive action=$action")
            }
            super.onReceive(context, intent)
            if (ids.isNotEmpty()) {
                val start = LocalTime.now()
                val end = start.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)
                val millis = Duration.between(start, end).toMillis()
                Timber.d("Alarm millis=$millis")
                AlarmManagerCompat.setExact(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + millis,
                    pendingReceiverFor<WidgetClock>()
                )
            }
            setAlarm(applicationContext)
        }
    }

    private fun setAlarm(context: Context) {
        with(context) {
        }
    }

    companion object {

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
                    // todo
                    setTextViewText(R.id.tv_time, "8h 5m remaining")
                    setTextViewText(R.id.tv_battery, "$level%")
                }
            )
        }
    }
}