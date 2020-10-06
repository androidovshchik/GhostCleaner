package com.ghostcleaner.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.ghostcleaner.extension.formatSize
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
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

    private val cacheDirs: List<File>
        get() = externalDir?.let { dir ->
            val dirs = mutableListOf<File>()
            dirs.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "cache").takeIf { it.exists() && it.isDirectory }
            }.orEmpty())
            val dataDir = File(dir, "Android/data")
            dirs.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "cache").takeIf { it.exists() && it.isDirectory }
            }.orEmpty())
            dirs.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "code_cache").takeIf { it.exists() && it.isDirectory }
            }.orEmpty())
            dirs
        }.orEmpty()

    private val tempDirs: List<File>
        get() = externalDir?.let { dir ->
            val dirs = mutableListOf<File>()
            val dataDir = File(dir, "Android/data")
            dirs.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "temp").takeIf { it.exists() && it.isDirectory }
            }.orEmpty())
            dirs
        }.orEmpty()

    private val residualDirs: List<File>
        get() {
            "log"
            "Bugreport"
        }

    private val systemFiles: List<File>
        get() {
            ".Trash",
            "LOST.DIR"
        }

    val filesData = MutableLiveData<String>()

    suspend fun getFileSizes(): Quadruple<Long, Long, Long, Long> = coroutineScope {
        FileUtils.sizeOfDirectory(folder)
        Quadruple(0L, 0L, 0L, 0L)
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