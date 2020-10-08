package com.ghostcleaner

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import com.ghostcleaner.service.EnergyMode

class Preferences(context: Context) : KotprefModel(context) {

    override val kotprefName = "${context.packageName}_preferences"

    var energyMode by stringPref(EnergyMode.NORMAL.name, "energy_mode")
}