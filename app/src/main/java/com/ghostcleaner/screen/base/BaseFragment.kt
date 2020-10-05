package com.ghostcleaner.screen.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.ghostcleaner.R
import com.ghostcleaner.screen.BuyActivity
import kotlinx.coroutines.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment : Fragment(), CoroutineScope {

    protected val job = SupervisorJob()

    @StringRes
    protected open var title = 0

    protected val args: Bundle
        get() = arguments ?: Bundle()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.nal)?.setOnClickListener {
            context?.run {
                startActivity(intentFor<BuyActivity>("title" to title).newTask())
            }
        }
    }

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