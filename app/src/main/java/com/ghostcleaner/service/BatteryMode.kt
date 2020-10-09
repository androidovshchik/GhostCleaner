package com.ghostcleaner.service

enum class BatteryMode(
    val brightness: Int,
    val toggleSync: Boolean,
    val toggleRotate: Boolean,
    val disableBle: Boolean,
    val disableWifi: Boolean
) {
    NORMAL(60, true, true, false, false),
    ULTRA(30, false, false, true, false),
    EXTREME(20, false, false, true, true);
}