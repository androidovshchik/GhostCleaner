package com.ghostcleaner.service

enum class EnergyMode(
    val brightness: Int,
    val disableSync: Boolean,
    val disableRotate: Boolean,
    val disableBle: Boolean,
    val disableWifi: Boolean
) {
    NORMAL(80, false, true, false, false),
    ULTRA(30, true, false, true, false),
    EXTREME(20, true, false, true, true);
}