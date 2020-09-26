package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.ghostcleaner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.dip

@Suppress("MemberVisibilityCanBePrivate")
class RadarView : SurfaceView, SurfaceHolder.Callback, CoroutineScope {

    private var job: Job? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radarSize = resources.getDimension(R.dimen.radar_size)
    private var circleDarkW = context.dip(11).toFloat()
    private var circleGreenW = context.dip(2).toFloat()
    private var circleGreenD = context.dip(212).toFloat()
    private var circleMiniW = context.dip(0.5f).toFloat()
    private var circleMiniDs = arrayOf(
        context.dip(170).toFloat(),
        context.dip(112).toFloat(),
        context.dip(48).toFloat()
    )

    val isRunning
        get() = job?.isActive == true

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        holder.setFormat(PixelFormat.TRANSLUCENT)
        if (!isInEditMode) {
            holder.addCallback(this)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        start()
    }

    @SuppressLint("WrongCall")
    fun start() {
        if (isRunning) {
            return
        }
        job = launch {
            while (true) {
                with(holder) {
                    lockCanvas(null)?.let {
                        try {
                            synchronized(this) {
                                onDraw(it)
                            }
                        } finally {
                            unlockCanvasAndPost(it)
                        }
                    }
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val cX = width / 2f
        val cY = height / 2f
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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }

    fun stop() {
        job?.cancel()
    }

    // todo call
    fun release() {
        holder.removeCallback(this)
    }

    override val coroutineContext = Dispatchers.Default
}