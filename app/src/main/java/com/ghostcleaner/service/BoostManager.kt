package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
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
            Timber.d("Free memory ${memoryInfo.availMem} of ${memoryInfo.totalMem}")
            return memoryInfo.availMem to memoryInfo.totalMem
        }

    val userApps: List<ApplicationInfo>
        get() = packageManager.getInstalledApplications(0).filter {
            it.flags and ApplicationInfo.FLAG_SYSTEM == 0 && it.packageName != packageName
        }

    fun optimize() {
        job.cancelChildren()
        launch {
            val apps = userApps
            Timber.d("User apps count ${apps.size}")
            apps.forEachIndexed { i, app ->
                activityManager.killBackgroundProcesses(app.packageName)
                progressData.postValue(100f * (i + 1) / apps.size)
                delay(100)
            }
            progressData.postValue(-1f)
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}