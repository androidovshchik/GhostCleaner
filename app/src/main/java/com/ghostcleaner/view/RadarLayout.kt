package com.ghostcleaner.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.ghostcleaner.R
import kotlinx.android.synthetic.main.merge_radar.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.dip
import timber.log.Timber

class RadarLayout : FrameLayout, CoroutineScope {

    private val job = SupervisorJob()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radarSize = resources.getDimension(R.dimen.radar_size)
    private var circleDarkW = dip(11).toFloat()
    private var circleGreenW = dip(2).toFloat()
    private var circleGreenD = dip(212).toFloat()
    private var circleMiniW = dip(0.5f).toFloat()
    private var circleMiniDs = arrayOf(dip(170).toFloat(), dip(112).toFloat(), dip(48).toFloat())

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
        setWillNotDraw(false)
        View.inflate(context, R.layout.merge_radar, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val times = 120
        val distance =
            launch {
                while (isAttachedToWindow) {
                    (1..2).forEach { i ->
                        (0 until times).forEach { t ->
                            iv_gradient.translationY = if (i == 1) {
                                height * (1 - t.toFloat() / times)
                            } else {
                                -iv_gradient.height * t.toFloat() / times
                            }
                            if (!iv_gradient.isVisible) {
                                iv_gradient.isVisible = true
                            }
                            delay(10)
                        }
                    }
                }
            }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val w = width.toFloat()
        val h = width.toFloat()
        val cX = w / 2
        val cY = h / 2
        with(canvas) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = circleDarkW
            paint.color = Color.parseColor("#181925")
            drawCircle(cX, cY, radarSize / 2 - circleDarkW / 2, paint)
            paint.strokeWidth = circleGreenW
            paint.color = Color.parseColor("#3DA2A9")
            drawCircle(cX, cY, circleGreenD / 2 - circleGreenW / 2, paint)
            paint.strokeWidth = circleMiniW
            circleMiniDs.forEach {
                drawCircle(cX, cY, it / 2 - circleMiniW / 2, paint)
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