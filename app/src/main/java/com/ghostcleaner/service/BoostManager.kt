package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import timber.log.Timber

class BoostManager(context: Context) : CoroutineScope {

    private val job = SupervisorJob()

    private val packageName = context.packageName

    private val packageManager = context.packageManager

    private val activityManager = context.activityManager

    val percentData = MutableLiveData(0)

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
            for (next in apps) {
                if (next.flags and 1 != 1 && next.packageName != packageName) {
                    activityManager.killBackgroundProcesses(next.packageName)
                    percentData.postValue(100)
                }
            }
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}