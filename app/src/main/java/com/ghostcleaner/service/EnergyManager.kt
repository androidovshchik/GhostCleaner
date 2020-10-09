package com.ghostcleaner.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import com.ghostcleaner.Preferences
import com.ghostcleaner.REQUEST_SETTINGS
import com.ghostcleaner.extension.areGranted
import com.ghostcleaner.extension.isMarshmallowPlus
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.batteryManager
import org.jetbrains.anko.wifiManager

class EnergyManager private constructor(context: Context) : BaseManager<Int>(context) {

    private val preferences = Preferences(context)

    private val wifiManager = context.wifiManager

    private val batteryManager = context.batteryManager

    private val contentResolver = context.contentResolver

    val batteryLevel: Int
        get() = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    @SuppressLint("InlinedApi")
    fun checkPermissions(activity: Activity): Boolean {
        if (isMarshmallowPlus()) {
            if (Settings.System.canWrite(activity)) {
                return true
            }
        } else if (activity.areGranted(Manifest.permission.WRITE_SETTINGS)) {
            return true
        }
        activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }, REQUEST_SETTINGS)
        return false
    }

    override fun optimize(vararg args: Any) {
        val mode = args[0] as BatteryMode
        job.cancelChildren()
        launch {
            val interval = 500L
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            );
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                mode.brightness
            )
            delay(interval)
            optimization.postValue(1)
            Settings.System.putInt(
                contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                mode.toggleRotate
            )
            delay(interval)
            optimization.postValue(2)
            killProcesses({
            }, 0)
            delay(interval)
            optimization.postValue(3)
            ContentResolver.setMasterSyncAutomatically(mode.toggleSync)
            if (mode.disableBle) {
                BluetoothAdapter.getDefaultAdapter().disable()
            }
            if (mode.disableWifi) {
                @Suppress("DEPRECATION")
                wifiManager.isWifiEnabled = false
            }
            delay(interval)
            optimization.postValue(4)
        }
    }

    fun getBatteryTime(mode: BatteryMode = BatteryMode.valueOf(preferences.batteryMode)): String {
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

    fun getDescList(mode: BatteryMode): List<CharSequence> {
        val services = when (mode) {
            BatteryMode.ULTRA -> "Bluetooth,\nSync"
            BatteryMode.EXTREME -> "Wifi, Bluetooth,\nSync"
            else -> "Sync"
        }
        return listOf(
            "• Limit brightness up to ${mode.brightness}%",
            "• Disable device screen rotation",
            "• Close all battery consuming apps",
            "• Close system services like $services etc."
        )
    }

    private fun formatTime(mode: BatteryMode, nTime: Int, uTime: Int, eTime: Int): String {
        val minutes = when (mode) {
            BatteryMode.ULTRA -> uTime
            BatteryMode.EXTREME -> eTime
            else -> nTime
        }
        return "${minutes / 60}h ${minutes % 60}m"
    }

    companion object : Singleton<EnergyManager, Context>(::EnergyManager)
}