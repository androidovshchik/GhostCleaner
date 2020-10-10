package com.ghostcleaner

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import com.ghostcleaner.service.BatteryMode

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName = "${context.packageName}_preferences"

    var enableAds by booleanPref(true, "enable_ads")

    var cpuPath by nullableStringPref(null, "cpu_path")

    var batteryMode by stringPref(BatteryMode.NORMAL.name, "battery_mode")
}