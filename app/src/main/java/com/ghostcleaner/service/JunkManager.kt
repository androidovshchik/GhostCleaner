package com.ghostcleaner.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.ghostcleaner.extension.formatSize
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class JankManager(context: Context) : BaseManager(context) {

    private val isExternalStorageWritable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    @Suppress("DEPRECATION")
    private val externalDir: File?
        get() = Environment.getExternalStorageDirectory()
            .takeIf { isExternalStorageWritable && it.exists() }

    val filesData = MutableLiveData<String>()

    val fileSizes: Quadruple<Long, Long, Long, Long>
        get() {
            "log"
            ".Trash",
            "LOST.DIR",
            "thumbs?.db"
            FileUtils.sizeOfDirectory(folder)
            return Quadruple<Long, Long, Long, Long>("A", "B", "C", "D")
        }

    override fun optimize() {
        job.cancelChildren()
        launch {
            val count = 0
            val dataDir = externalDir
            if (dataDir != null) {
                dataDir.listFiles()?.forEach {
                    File(Environment.getExternalStorageDirectory(), "Android/data")
                    File(it, "cache").formatSize
                    File(it, "code_cache").deleteRecursively()
                    File(it, "temp").deleteRecursively()
                    filesData.postValue(it.path)
                    delay(100)
                }
            } else {
            }
            if (count < 30) {
                // afaik there is no way
                val dataDirs =
                    listApps(PackageManager.GET_META_DATA).take(30 - count).map { it.dataDir }
                dataDirs.forEach {
                    filesData.postValue(it)
                    delay(100)
                }
            }
        }
    }
}