package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
open class BoostManager(context: Context) : BaseManager<Float>(context) {

    val memorySizes: Pair<Long, Long>
        get() {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val usedMem = memoryInfo.totalMem - memoryInfo.availMem
            Timber.d("Used memory $usedMem of ${memoryInfo.totalMem}")
            return usedMem to memoryInfo.totalMem
        }

    override fun optimize(vararg args: Any) {
        super.optimize(args)
        job.cancelChildren()
        launch {
            killProcesses({
                optimization.postValue(it)
            }, 100)
            optimization.postValue(-1f)
        }
    }
}