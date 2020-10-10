package com.ghostcleaner.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.fragment.app.Fragment
import com.ghostcleaner.REQUEST_STORAGE
import com.ghostcleaner.extension.areGranted
import kotlinx.coroutines.*
import org.apache.commons.io.FileUtils
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class JunkManager(context: Context) : BaseManager<String?>(context) {

    private val isExternalStorageWritable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    @Suppress("DEPRECATION")
    private val externalDir: File?
        get() = Environment.getExternalStorageDirectory().takeIf { isExternalStorageWritable }

    private val cacheFiles: List<File>
        get() = externalDir?.let { dir ->
            val files = mutableListOf<File>()
            val dataDir = File(dir, "Android/data")
            files.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "cache").takeIf { it.exists() }
            }.orEmpty())
            files.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "code_cache").takeIf { it.exists() }
            }.orEmpty())
            files
        }.orEmpty()

    private val tempFiles: List<File>
        get() = externalDir?.let { dir ->
            val dirs = mutableListOf<File>()
            val dataDir = File(dir, "Android/data")
            dirs.addAll(dataDir.listFiles()?.mapNotNull { file ->
                File(file, "temp").takeIf { it.exists() && it.isDirectory }
            }.orEmpty())
            dirs
        }.orEmpty()

    private val otherFiles: List<File>
        get() {
            "log"
            "Bugreport"
        }

    private val systemFiles: List<File>
        get() {
            ".Trash",
            "LOST.DIR"
        }

    fun checkPermission(context: Context, fragment: Fragment): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (context.areGranted(permission)) {
            return true
        }
        fragment.requestPermissions(arrayOf(permission), REQUEST_STORAGE)
        return false
    }

    suspend fun getFileSizes(): Quadruple<Long, Long, Long, Long> {
        return withContext(Dispatchers.IO) {
            Quadruple(
                cacheFiles.sumOf { FileUtils.sizeOf(it) },
                tempFiles.sumOf { FileUtils.sizeOf(it) },
                otherFiles.sumOf { FileUtils.sizeOf(it) },
                systemFiles.sumOf { FileUtils.sizeOf(it) }
            )
        }
    }

    override fun optimize(vararg args: Any) {
        job.cancelChildren()
        launch {
            var count = 0
            val minCount = 32
            cacheFiles.forEach {
                it.deleteRecursively()
                optimization.postValue(it.path)
                delay(100)
                count++
            }
            tempFiles.forEach {
                it.deleteRecursively()
                optimization.postValue(it.path)
                delay(100)
                count++
            }
            otherFiles.forEach {
                it.deleteRecursively()
                optimization.postValue(it.path)
                delay(100)
                count++
            }
            systemFiles.forEach {
                it.deleteRecursively()
                optimization.postValue(it.path)
                delay(100)
                count++
            }
            if (count < minCount) {
                // afaik there is no way
                val dataDirs = listAllApps(PackageManager.GET_META_DATA).take(minCount - count)
                    .map { it.dataDir }
                dataDirs.forEach {
                    optimization.postValue(it)
                    delay(100)
                }
            }
        }
    }
}