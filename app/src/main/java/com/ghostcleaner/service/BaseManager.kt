package com.ghostcleaner.service

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.jetbrains.anko.activityManager
import timber.log.Timber

abstract class BaseManager<T>(context: Context) : CoroutineScope {

    protected val job = SupervisorJob()

    protected val packageName: String = context.packageName

    protected val activityManager = context.activityManager

    protected val packageManager: PackageManager = context.packageManager

    val optimization = MutableLiveData<T>()

    abstract fun optimize(vararg args: Any)

    protected fun listAllApps(flags: Int = 0): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags)
    }

    protected fun list3rdPartyApps(flags: Int = 0): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags).filter {
            it.flags and ApplicationInfo.FLAG_SYSTEM == 0
        }
    }

    protected suspend inline fun killProcesses(callback: (percent: Float) -> Unit, interval: Long) {
        val apps = list3rdPartyApps()
        Timber.d("3rd party apps count ${apps.size}")
        apps.forEachIndexed { i, app ->
            if (app.packageName != packageName) {
                activityManager.killBackgroundProcesses(app.packageName)
            }
            callback(100f * (i + 1) / apps.size)
            if (interval > 0) {
                delay(interval)
            }
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}