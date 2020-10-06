package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.activityManager
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class BoostManager(context: Context) : BaseManager() {

    private val packageName = context.packageName

    private val packageManager = context.packageManager

    private val activityManager = context.activityManager

    val progressData = MutableLiveData<Float>()

    val memory: Pair<Long, Long>
        get() {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val usedMem = memoryInfo.totalMem - memoryInfo.availMem
            Timber.d("Used memory $usedMem of ${memoryInfo.totalMem}")
            return usedMem to memoryInfo.totalMem
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
}