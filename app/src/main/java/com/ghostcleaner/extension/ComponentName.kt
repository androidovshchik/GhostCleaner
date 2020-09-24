@file:Suppress("unused")

package com.ghostcleaner.extension

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.ghostcleaner.WidgetBar
import org.jetbrains.anko.intentFor

fun ComponentName.toggle(context: Context, enable: Boolean) {
    context.packageManager.setComponentEnabledSetting(
        this,
        if (enable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}

/**
 * @param force if true also make http requests besides ui updates
 */
fun ComponentName.updateAll(context: Context, force: Boolean) {
    val ids = context.appWidgetManager.getAppWidgetIds(this)
    if (ids.isNotEmpty()) {
        context.sendBroadcast(context.intentFor<WidgetBar>().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            putExtra("force", force)
        })
    }
}