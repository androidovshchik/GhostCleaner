package com.ghostcleaner.screen.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment : Fragment(), CoroutineScope {

    protected val job = SupervisorJob()

    protected val args: Bundle
        get() = arguments ?: Bundle()

    protected open fun showMessage(message: CharSequence) {
        view?.longSnackbar(message)
    }

    protected open fun showError(e: Throwable) {
        showMessage(e.message ?: e.toString())
    }

    override fun onDestroyView() {
        job.cancelChildren()
        super.onDestroyView()
    }

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException && view != null) {
            activity?.runOnUiThread {
                showError(e)
            }
        }
    }
}