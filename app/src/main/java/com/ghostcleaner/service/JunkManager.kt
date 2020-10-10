package com.ghostcleaner.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.ghostcleaner.REQUEST_STORAGE
import com.ghostcleaner.extension.areGranted
import com.ghostcleaner.extension.formatSize
import kotlinx.coroutines.*
import org.apache.commons.io.FileUtils
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class JunkManager(context: Context) : BaseManager<String?>(context) {

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

    val pathData = MutableLiveData<String?>()

    fun checkPermission(context: Context, fragment: Fragment): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (context.areGranted(permission)) {
            return true
        }
        fragment.requestPermissions(arrayOf(permission), REQUEST_STORAGE)
        return false
    }

    suspend fun getFileSizes(): Quadruple<Long, Long, Long, Long> {
        withContext(Dispatchers.IO) {
            Quadruple(
                cacheDirs.sumOf { FileUtils.sizeOfDirectory(it) },
                tempDirs.sumOf { FileUtils.sizeOfDirectory(it) },
                residualDirs.sumOf { FileUtils.sizeOfDirectory(it) },
                systemFiles.sumOf { FileUtils.sizeOf(it) }
            )
        }
    }

    override fun optimize(vararg args: Any) {
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
                    pathData.postValue(it.path)
                    delay(100)
                }
            } else {
            }
            cacheDirs.sumOf { FileUtils.sizeOfDirectory(it) },
            tempDirs.sumOf { FileUtils.sizeOfDirectory(it) },
            residualDirs.sumOf { FileUtils.sizeOfDirectory(it) },
            systemFiles.sumOf { FileUtils.sizeOf(it) }
            if (count < 30) {
                // afaik there is no way
                val dataDirs = listAllApps(PackageManager.GET_META_DATA).take(30 - count)
                    .map { it.dataDir }
                dataDirs.forEach {
                    pathData.postValue(it)
                    delay(100)
                }
            }
        }
    }
}