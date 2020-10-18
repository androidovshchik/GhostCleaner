package com.ghostcleaner.service

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import com.ghostcleaner.R
import com.ghostcleaner.extension.pendingActivityFor
import com.ghostcleaner.extension.pendingReceiverFor
import com.ghostcleaner.screen.SplashActivity
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import java.util.concurrent.TimeUnit

class RemindReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        with(context) {
            setAlarm(applicationContext)
            val builder = NotificationCompat.Builder(applicationContext, "main")
                .setSmallIcon(R.drawable.broom)
                .setColor(Color.WHITE)
                .setContentIntent(pendingActivityFor<SplashActivity>())
                .setContentTitle("Clean junk files to boost your battery")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
            notificationManager.notify(99, builder.build())
        }
    }

    companion object {

        fun setAlarm(context: Context) {
            with(context) {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TimeUnit.HOURS.toMillis(2),
                    pendingReceiverFor(intentFor<RemindReceiver>().also {
                        it.data = Uri.parse("$packageName://1")
                    }, 1)
                )
            }
        }
    }
}