package com.ghostcleaner.service

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.ghostcleaner.Preferences
import com.ghostcleaner.R
import com.ghostcleaner.extension.pendingActivityFor
import com.ghostcleaner.extension.pendingReceiverFor
import com.ghostcleaner.screen.SplashActivity
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RemindReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        with(context) {
            setAlarm(applicationContext)
            val builder = NotificationCompat.Builder(applicationContext, "main")
                .setSmallIcon(R.drawable.broom)
                .setColor(Color.WHITE)
                .setContentIntent(pendingActivityFor<SplashActivity>())
                .setContentTitle(D["notText"])
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
            notificationManager.notify(99, builder.build())
        }
    }

    companion object {

        private val maxInterval = TimeUnit.HOURS.toMillis(2)

        fun setAlarm(context: Context) {
            with(context) {
                val now = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
                val preferences = Preferences(applicationContext)
                val nextAlarm = preferences.nextAlarm
                val interval = if (nextAlarm <= now) {
                    preferences.nextAlarm = now + maxInterval
                    maxInterval
                } else {
                    nextAlarm - now
                }
                Timber.d("Next alarm ${DateUtils.formatElapsedTime(interval / 1000)}")
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingReceiverFor(intentFor<RemindReceiver>().also {
                        it.data = Uri.parse("$packageName://1")
                    }, 1)
                )
            }
        }
    }
}