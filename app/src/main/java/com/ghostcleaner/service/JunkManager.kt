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
            var cacheBytes = 0L
            var tempBytes = 0L
            var otherBytes = 0L
            var systemBytes = 0L
            externalDir?.walk()?.forEach {
                when (it.name) {
                    in cacheFiles -> cacheBytes += FileUtils.sizeOf(it)
                    in tempFiles -> tempBytes += FileUtils.sizeOf(it)
                    in otherFiles -> otherBytes += FileUtils.sizeOf(it)
                    in systemFiles -> systemBytes += FileUtils.sizeOf(it)
                }
            }
            Quadruple(cacheBytes, tempBytes, otherBytes, systemBytes)
        }
    }

    override fun optimize(vararg args: Any) {
        super.optimize(args)
        job.cancelChildren()
        launch {
            var count = 0
            val minCount = 32
            externalDir?.walk()?.forEach {
                when (it.name) {
                    in cacheFiles, in tempFiles, in otherFiles, in systemFiles -> {
                        it.deleteRecursively()
                        optimization.postValue(it.path)
                        delay(100)
                        count++
                    }
                }
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

    @Suppress("SpellCheckingInspection")
    companion object {

        private val cacheFiles = arrayOf(
            ".cache",
            "cache",
            "code_cache"
        )

        private val tempFiles = arrayOf(
            "tmp",
            "temp"
        )

        private val otherFiles = arrayOf(
            "log",
            "logs",
            "bugreport",
            "bugreports"
        )

        private val systemFiles = arrayOf(
            ".Trash",
            "LOST.DIR"
        )
    }
}