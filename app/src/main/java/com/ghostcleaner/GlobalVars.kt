package com.ghostcleaner

import org.threeten.bp.format.DateTimeFormatter

const val ACTION_ROCKET = "action_rocket"
const val ACTION_BATTERY = "action_battery"
const val ACTION_TEMP = "action_temperature"
const val ACTION_TRASH = "action_trash"

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")