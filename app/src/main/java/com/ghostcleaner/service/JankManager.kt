package com.ghostcleaner.service

import android.content.pm.PackageManager
import android.os.Environment
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

private val genericFilterFiles = arrayOf(
    ".tmp",
    ".log"
)
private val genericFilterFolders = arrayOf(
    "Logs",
    "logs",
    "temp",
    "Temporary",
    "temporary"
)
private val aggressiveFilterFolders = arrayOf(
    "supersonicads",
    "Cache",
    "Analytics",
    "thumbnails?",
    "mobvista",
    "UnityAdsVideoCache",
    "albumthumbs?",
    "LOST.DIR",
    ".Trash",
    "desktop.ini",
    "leakcanary",
    ".DS_Store",
    ".spotlight-V100",
    "fseventsd",
    "Bugreport",
    "bugreports",
    ".Trash",
    "cache"
)
private val aggressiveFilterFiles = arrayOf(
    ".exo",
    "thumbs?.db"
)

class JankManager : BaseManager() {

    private val isExternalStorageWritable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    @Suppress("DEPRECATION")
    override fun optimize() {
        job.cancelChildren()
        launch {
            val externalDataDir = File(Environment.getExternalStorageDirectory(), "Android/data")
            if (isExternalStorageWritable && externalDataDir.exists()) {
                externalDataDir.listFiles()?.forEach {
                    File(it, "cache").deleteRecursively()

                    delay(100)
                }
            } else {
                val dirs =
                    listApps(PackageManager.GET_META_DATA).take(30).map { "${it.dataDir}/cache" }
                dirs.forEach {

                    delay(100)
                }
            }
        }
    }
}