package com.ghostcleaner.service

import android.content.Context
import com.ghostcleaner.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class CoolManager private constructor(context: Context) : BoostManager(context) {

    private val preferences = Preferences(context)

    suspend fun getCPUTemp(): Int {
        var temp = 40f
        var savedPath = preferences.cpuPath
        withContext(Dispatchers.IO) {
            if (savedPath.isNullOrBlank()) {
                for (path in paths) {
                    val value = execPath(path)
                    if (value != null) {
                        temp = value
                        savedPath = path
                        break
                    }
                }
            } else {
                execPath(savedPath!!)
            }
        }
        preferences.cpuPath = savedPath
        return temp.roundToInt()
    }

    private fun execPath(path: String): Float? {
        try {
            val process = Runtime.getRuntime().exec("cat $path")
            process.waitFor()
            process.inputStream.bufferedReader().useLines {
                return it.firstOrNull()?.toFloatOrNull()?.div(1000)
            }
        } catch (ignored: Throwable) {
        }
        return null
    }

    companion object : Singleton<CoolManager, Context>(::CoolManager) {

        // see https://stackoverflow.com/a/48641339/5279156
        @Suppress("SpellCheckingInspection")
        private val paths = arrayOf(
            "/sys/class/hwmon/hwmon0/device/temp1_input",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp",
            "/sys/class/i2c-adapter/i2c-4/4-004c/temperature",
            "/sys/kernel/debug/tegra_thermal/temp_tj",
            "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature",
            "/sys/devices/platform/tegra_tmon/temp1_input",
            "/sys/devices/platform/omap/omap_temp_sensor.0/temperature",
            "/sys/devices/platform/s5p-tmu/temperature",
            "/sys/devices/platform/s5p-tmu/curr_temp"
        )
    }
}