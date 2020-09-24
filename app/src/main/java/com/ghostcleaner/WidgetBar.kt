package com.ghostcleaner

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.UiThread
import com.ghostcleaner.extension.appWidgetManager
import com.ghostcleaner.extension.getClickIntent
import timber.log.Timber

class WidgetBar : AppWidgetProvider() {

    @UiThread
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        with(context) {
            for (id in ids) {
                updateWidget(id)
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
                else -> Timber.d("onReceive action=$action")
            }
            super.onReceive(context, intent)
        }
    }

    companion object {

        private val Context.widget: ComponentName
            get() = ComponentName(applicationContext, WidgetBar::class.java)

        private fun Context.updateWidget(id: Int) {
            val options = appWidgetManager.getAppWidgetOptions(id)
            val isPortrait = resources.configuration.orientation == ORIENTATION_PORTRAIT
            val width = options.getInt(
                if (isPortrait) {
                    AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
                } else {
                    AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
                }
            )
            Timber.d("updateWidget isPortrait=$isPortrait")
            appWidgetManager.updateAppWidget(
                id, RemoteViews(packageName, R.layout.widget_bar).apply {
                    setOnClickPendingIntent(R.id.ib_rocket, getClickIntent(id, ACTION_DONE))
                    setOnClickPendingIntent(R.id.ib_battery, getClickIntent(id, ACTION_DONE))
                    setOnClickPendingIntent(R.id.ib_temperature, getClickIntent(id, ACTION_DONE))
                    setOnClickPendingIntent(R.id.ib_trash, getClickIntent(id, ACTION_DONE))
                }
            )
        }
    }
}