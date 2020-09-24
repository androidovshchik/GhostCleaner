package com.ghostcleaner

import org.threeten.bp.format.DateTimeFormatter

const val ACTION_NONE = "action_none"
const val ACTION_DONE = "action_done"
const val ACTION_DELETE = "action_delete"
const val ACTION_SKIP = "action_skip"
const val ACTION_REFRESH = "action_refresh"
const val ACTION_OPEN = "action_open"

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")