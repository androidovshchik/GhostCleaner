package com.ghostcleaner.service

import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import org.jetbrains.anko.wifiManager

class BatteryManager(context: Context) : BaseManager() {

    private val wifiManager = context.wifiManager

    private val contentResolver = context.contentResolver

    fun applyNormalMode() {
        Settings.System.putInt(contentResolver, "accelerometer_rotation", 1)
        Settings.System.putInt(contentResolver, "screen_brightness", 255)
        ContentResolver.setMasterSyncAutomatically(true)
    }

    fun applyUltraMode() {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter?.isEnabled == true) {
            defaultAdapter.disable()
        }
        Settings.System.putInt(contentResolver, "accelerometer_rotation", 0)
        Settings.System.putInt(contentResolver, "screen_brightness", 30)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    fun applyExtremeMode() {
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
    }
}