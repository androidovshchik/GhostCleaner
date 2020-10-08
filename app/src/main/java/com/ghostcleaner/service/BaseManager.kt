package com.ghostcleaner.service

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

abstract class BaseManager<T>(context: Context) : CoroutineScope {

    protected val job = SupervisorJob()

    protected val packageName: String = context.packageName

    protected val packageManager: PackageManager = context.packageManager

    val optimization = MutableLiveData<T>()

    abstract fun optimize(vararg args: Any)

    protected fun listAllApps(flags: Int = 0): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags)
    }

    protected fun list3rdPartyApps(flags: Int = 0): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags).filter {
            it.flags and ApplicationInfo.FLAG_SYSTEM == 0 && it.packageName != packageName
        }
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}