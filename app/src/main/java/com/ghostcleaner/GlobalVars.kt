package com.ghostcleaner

import org.threeten.bp.format.DateTimeFormatter

const val REQUEST_SETTINGS = 100
const val REQUEST_WRITE = 200
const val REQUEST_READ = 300
const val REQUEST_ADS = 400

const val ACTION_OPEN = "action_open"
const val ACTION_ROCKET = "action_rocket"
const val ACTION_BATTERY = "action_battery"
const val ACTION_TEMPERATURE = "action_temperature"
const val ACTION_TRASH = "action_trash"

const val EXTRA_ACTION = "action"
const val EXTRA_TITLE = "title_key"

const val SKU_ADS = "disabledads"
const val SUB_ADS = "sub_ads2"

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")