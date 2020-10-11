package com.ghostcleaner

import org.threeten.bp.format.DateTimeFormatter

const val REQUEST_SETTINGS = 100
const val REQUEST_STORAGE = 200
const val REQUEST_ADS = 300

const val ACTION_ROCKET = "action_rocket"
const val ACTION_BATTERY = "action_battery"
const val ACTION_TEMPERATURE = "action_temperature"
const val ACTION_TRASH = "action_trash"

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")