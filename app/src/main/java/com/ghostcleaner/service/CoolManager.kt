package com.ghostcleaner.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile

class CoolManager private constructor(context: Context) : BoostManager(context) {

    suspend fun getCPUTemp(): Int {
        withContext(Dispatchers.IO) {
            var temp: Float? = null
            for (path in paths) {
                try {
                    RandomAccessFile(path, "r").use {
                        val line = readLine()
                        val temp = line.toFloat() / 1000.0f
                    }
                    temp.toInt()
                } catch (ignored: Throwable) {
                }
            }
            while (temp ==) {
            }
        }
        return coroutineScope {
            val p: Process
            try {
                p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp")
                p.waitFor()
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                val line: String = reader.readLine()
                val temp = line.toFloat() / 1000.0f
                temp.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                0.0f
            }
            0
        }
    }

    companion object : Singleton<CoolManager, Context>(::CoolManager) {

        @Suppress("SpellCheckingInspection")
        private val paths = arrayOf(
            "/sys/class/hwmon/hwmon0/device/temp1_input",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/i2c-adapter/i2c-4/4-004c/temperature",
            "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature",
            "/sys/devices/platform/omap/omap_temp_sensor.0/temperature",
            "/sys/devices/platform/tegra_tmon/temp1_input",
            "/sys/kernel/debug/tegra_thermal/temp_tj",
            "/sys/devices/platform/s5p-tmu/temperature",
            "/sys/devices/platform/s5p-tmu/curr_temp"
        )
    }
}