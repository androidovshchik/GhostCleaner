package com.ghostcleaner.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ghostcleaner.R
import kotlinx.android.synthetic.main.merge_radar.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.dip
import timber.log.Timber
import kotlin.math.sqrt

class RadarLayout : FrameLayout, CoroutineScope {

    private val job = SupervisorJob()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var colorBackground = ContextCompat.getColor(context, R.color.colorBackground)
    private var colorDarkGray = ContextCompat.getColor(context, R.color.colorDarkGray)
    private var colorGreen = ContextCompat.getColor(context, R.color.colorGreen)

    private var radarSize = resources.getDimension(R.dimen.radar_size)
    private var radarHyp = radarSize * sqrt(2f)

    private var circleOuterW = (radarHyp - radarSize) / 2
    private var circleDarkW = dip(11).toFloat()
    private var circleGreenW = dip(2).toFloat()
    private var circleGreenR = dip(212).toFloat() / 2
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
        val times = 200
        launch {
            while (isAttachedToWindow) {
                (0 until times).forEach { t ->
                    val h = iv_gradient.height
                    iv_gradient.translationY = (height + h) * (1 - t.toFloat() / times) - h
                    if (!iv_gradient.isVisible) {
                        iv_gradient.isVisible = true
                    }
                    delay(10)
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cX = w / 2
        val cY = h / 2
        with(canvas) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = circleOuterW
            paint.color = colorBackground
            drawCircle(cX, cY, radarHyp / 2 - circleOuterW / 2, paint)
            paint.strokeWidth = circleDarkW
            paint.color = colorDarkGray
            drawCircle(cX, cY, w / 2 - circleDarkW / 2, paint)
            paint.strokeWidth = circleGreenW
            paint.color = colorGreen
            drawCircle(cX, cY, circleGreenR - circleGreenW / 2, paint)
            paint.strokeWidth = circleMiniW
            drawLine(cX - circleGreenR, cY, cX + circleGreenR, cY, paint)
            drawLine(cX, cY + circleGreenR, cX, cY - circleGreenR, paint)
            val proj = circleGreenR / sqrt(2f)
            drawLine(cX - proj, cY - proj, cX + proj, cY + proj, paint)
            drawLine(cX + proj, cY - proj, cX - proj, cY + proj, paint)
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