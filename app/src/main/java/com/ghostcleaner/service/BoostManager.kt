package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import timber.log.Timber

class BoostManager(context: Context) : CoroutineScope {

    private val job = SupervisorJob()

    private val packageName = context.packageName

    private val packageManager = context.packageManager

    private val activityManager = context.activityManager

    val progressData = MutableLiveData<Float>()

    val memory: Pair<Long, Long>
        get() {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            return memoryInfo.availMem to memoryInfo.totalMem
        }

    fun optimize() {
        job.cancelChildren()
        launch {
            val apps = packageManager.getInstalledApplications(0)
            Timber.d("Apps count ${apps.size}")
            apps.forEachIndexed { i, app ->
                if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0 && app.packageName != packageName) {
                    activityManager.killBackgroundProcesses(app.packageName)
                    progressData.postValue(100f * (i + 1) / apps.size)
                    delay(100)
                }
            }
            progressData.postValue(-1f)
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}