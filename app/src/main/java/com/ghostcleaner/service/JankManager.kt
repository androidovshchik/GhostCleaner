package com.ghostcleaner.service

import android.os.Environment
import kotlinx.coroutines.cancelChildren
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
    fun optimize() {
        job.cancelChildren()
        if (isExternalStorageWritable) {
            val externalDataDirectory =
                File(Environment.getExternalStorageDirectory(), "Android/data")
            val externalCachePath = externalDataDirectory.absolutePath +
                    "/%s/cache"
            if (externalDataDirectory.isDirectory) {
                val files = externalDataDirectory.listFiles()
                for (file in files) {
                    if (!deleteDirectory(
                            File(
                                String.format(
                                    externalCachePath,
                                    file.name
                                )
                            ), true
                        )
                    ) {
                    }
                } else {
                }
            } else {
            }
        }

        fun File.deleteFiles() {
            listFiles()?.forEach {
                if (it.isDirectory) {
                    it.deleteFiles()
                } else {
                    it.delete()
                }
            }
        }
    }