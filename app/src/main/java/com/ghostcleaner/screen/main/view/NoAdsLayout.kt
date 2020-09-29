package com.ghostcleaner.screen.main.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.ghostcleaner.R
import kotlinx.android.synthetic.main.merge_no_ads.view.*
import kotlinx.coroutines.*
import timber.log.Timber

class NoAdsLayout : RelativeLayout, CoroutineScope {

    private val job = SupervisorJob()

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(attrs)
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.merge_no_ads, this)
        val maxR = 15f
        val green = ContextCompat.getColor(context, R.color.colorGreen)
        launch {
            while (isAttachedToWindow) {
                (1..4).forEach { i ->
                    (1..80).forEach { j ->
                        val r = if (i % 2 != 0) maxR * j / 80 else maxR * (80 - j) / 80
                        tv_no.setShadowLayer(r, 0f, 0f, green)
                        tv_ads.setShadowLayer(r, 0f, 0f, green)
                        delay(10)
                    }
                }
                delay(3000)
            }
        }
    }

    override fun onDetachedFromWindow() {
        job.cancelChildren()
        super.onDetachedFromWindow()
    }

    override fun hasOverlappingRendering() = false

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}