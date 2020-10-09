package com.ghostcleaner.service

enum class BatteryMode(
    val brightness: Int,
    val toggleSync: Boolean,
    val toggleRotate: Int,
    val disableBle: Boolean,
    val disableWifi: Boolean
) {
    NORMAL(120, true, 1, false, false),
    ULTRA(60, false, 0, true, false),
    EXTREME(20, false, 0, true, true);
}