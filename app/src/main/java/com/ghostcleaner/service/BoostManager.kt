package com.ghostcleaner.service

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class BoostManager private constructor(context: Context) : BaseManager<Float>(context) {

    val progressData = MutableLiveData<Float>()

    val memorySizes: Pair<Long, Long>
        get() {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val usedMem = memoryInfo.totalMem - memoryInfo.availMem
            Timber.d("Used memory $usedMem of ${memoryInfo.totalMem}")
            return usedMem to memoryInfo.totalMem
        }

    override fun optimize(vararg args: Any) {
        job.cancelChildren()
        launch {
            killProcesses {
                progressData.postValue(it)
            }
            progressData.postValue(-1f)
        }
    }

    companion object : Singleton<BoostManager, Context>(::BoostManager)
}