package com.ghostcleaner.service

import android.content.Context
import android.content.pm.ApplicationInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

open class BaseManager(context: Context) : CoroutineScope {

    protected val job = SupervisorJob()

    private val packageManager = context.packageManager

    fun listApps(flags: Int = 0): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(flags).filter {
            it.flags and ApplicationInfo.FLAG_SYSTEM == 0 && it.packageName != packageName
        }
    }

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}