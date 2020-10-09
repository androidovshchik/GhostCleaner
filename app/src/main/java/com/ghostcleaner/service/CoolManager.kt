package com.ghostcleaner.service

import android.content.Context
import kotlinx.coroutines.coroutineScope

class CoolManager private constructor(context: Context) : BoostManager(context) {

    suspend fun getCPUTemp(): Int {
        return coroutineScope {

        }
    }

    companion object : Singleton<CoolManager, Context>(::CoolManager) {

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