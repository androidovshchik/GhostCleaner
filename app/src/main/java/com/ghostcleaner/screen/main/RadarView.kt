package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.os.Build
import android.os.SystemClock
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
    private var circleDarkW = dip(3).toFloat()
    private var circleGreenW = dip(2).toFloat()
    private var circleGreenD = dip(212).toFloat()
    private var circleMiniW = dip(0.5f).toFloat()
    private var circleMiniDs = arrayOf(dip(170).toFloat(), dip(112).toFloat(), dip(48).toFloat())
    private var lineH = dip(3).toFloat()
    private var rectH = dip(43).toFloat()

    private val lineShader = RadialGradient(
        radarSize / 2, dip(1.5f).toFloat(), dip(74.5f).toFloat(),
        Color.parseColor("#84F8FF"), Color.parseColor("#0000DBE9"), TileMode.CLAMP
    )
    private val rectShader = LinearGradient(
        dip(21.5f).toFloat(), dip(84.5f).toFloat(), dip(21.5f).toFloat(), dip(253.5f).toFloat(),
        Color.parseColor("#3084F8FF"), Color.parseColor("#0084F8FF"), TileMode.CLAMP
    )

    private var lastDrawTime = 0L

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
        val w = width.toFloat()
        val h = width.toFloat()
        val cX = w / 2
        val cY = h / 2
        val now = SystemClock.elapsedRealtime()
        val delay = if (lastDrawTime > 0) now - lastDrawTime else 0
        lastDrawTime = now
        with(canvas) {
            paint.shader = rectShader
            drawRect(0f, 0f, w, h, paint)
            paint.shader = lineShader
            drawRect(0f, 0f, w, lineH, paint)
            /*paint.style = Paint.Style.STROKE
            paint.strokeWidth = circleDarkW
            paint.color = Color.parseColor("#181925")
            drawCircle(cX, cY, radarSize / 2 - circleDarkW / 2, paint)
            paint.strokeWidth = circleGreenW
            paint.color = Color.parseColor("#3DA2A9")
            drawCircle(cX, cY, circleGreenD / 2 - circleGreenW / 2, paint)
            paint.strokeWidth = circleMiniW
            circleMiniDs.forEach {
                drawCircle(cX, cY, it / 2 - circleMiniW / 2, paint)
            }*/
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