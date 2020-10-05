package com.ghostcleaner.service

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

open class BaseManager : CoroutineScope {

    protected val job = SupervisorJob()

    override val coroutineContext = Dispatchers.Default + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}