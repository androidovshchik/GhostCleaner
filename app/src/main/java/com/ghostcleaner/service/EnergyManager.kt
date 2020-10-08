package com.ghostcleaner.service

import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import com.ghostcleaner.Preferences
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.batteryManager
import org.jetbrains.anko.wifiManager
import timber.log.Timber

class EnergyManager(context: Context) : BaseManager(context) {

    private val preferences = Preferences(context)

    private val wifiManager = context.wifiManager

    private val batteryManager = context.batteryManager

    private val contentResolver = context.contentResolver

    override fun optimize(vararg args: Any) {
        job.cancelChildren()
        launch {
            val apps = listApps()
            Timber.d("User apps count ${apps.size}")
            apps.forEachIndexed { i, app ->
                activityManager.killBackgroundProcesses(app.packageName)
                progressData.postValue(100f * (i + 1) / apps.size)
                delay(100)
            }
            if (wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = false
            }
            val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
            if (defaultAdapter?.isEnabled == true) {
                defaultAdapter.disable()
            }
            Settings.System.putInt(contentResolver, "accelerometer_rotation", 0)
            Settings.System.putInt(contentResolver, "screen_brightness", 20)
            ContentResolver.setMasterSyncAutomatically(false)
            progressData.postValue(-1f)
        }
    }

    val batteryLevel: Int
        get() = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    val batteryTime: String
        get() = getBatteryTime(BatteryMode.valueOf(preferences.energyMode))

    fun getBatteryTime(mode: BatteryMode): String {
        val level = batteryLevel
        return when {
            level <= 5 -> formatTime(mode, 15, 145, 235)
            level <= 10 -> formatTime(mode, 30, 185, 360)
            level <= 15 -> formatTime(mode, 45, 230, 505)
            level <= 25 -> formatTime(mode, 90, 285, 775)
            level <= 35 -> formatTime(mode, 140, 380, 1142)
            level <= 50 -> formatTime(mode, 320, 565, 1320)
            level <= 65 -> formatTime(mode, 450, 661, 1695)
            level <= 75 -> formatTime(mode, 550, 865, 1855)
            level <= 85 -> formatTime(mode, 855, 1030, 2285)
            else -> formatTime(mode, 1245, 1800, 3895)
        }
    }

    private fun formatTime(mode: BatteryMode, vararg time: Int): String {
        val minutes = when (mode) {
            BatteryMode.ULTRA -> time[1]
            BatteryMode.EXTREME -> time[2]
            else -> time[0]
        }
        return "${minutes / 60}h ${minutes % 60}m"
    }

    companion object : Singleton<EnergyManager, Context>(::EnergyManager)
}