package com.ghostcleaner.screen.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("MemberVisibilityCanBePrivate")
class RadarView : SurfaceView, SurfaceHolder.Callback, CoroutineScope {

    private var job: Job? = null

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
        holder.addCallback(this)
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
                holder.run {
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
        with(canvas) {
            181925, 100%
            drawCircle()
        }
        canvas.drawColor(Color.GREEN)
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