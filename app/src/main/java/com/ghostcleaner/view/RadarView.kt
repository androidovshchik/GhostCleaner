package com.ghostcleaner.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
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
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
class RadarView : SurfaceView, SurfaceHolder.Callback, CoroutineScope {

    private var job: Job? = null

    private val mCanvas = Canvas()

    private var output: Bitmap? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bmpGradient = BitmapFactory.decodeResource(resources, R.drawable.gradient)
    private val rectGradient = RectF()
    private val rectRatio = 6// 600x100

    private val bmpDot = BitmapFactory.decodeResource(resources, R.drawable.ic_dot)

    private var radarSize = resources.getDimension(R.dimen.radar_size)
    private var circleDarkW = dip(3).toFloat()
    private var circleGreenW = dip(2).toFloat()
    private var circleGreenD = dip(212).toFloat()
    private var circleMiniW = dip(0.5f).toFloat()
    private var circleMiniDs = arrayOf(dip(170).toFloat(), dip(112).toFloat(), dip(48).toFloat())
    private var lineH = dip(3).toFloat()

    private var positionY = radarSize
    private var speedPxPerMs = dip(10).toFloat() / 1000

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

    @SuppressLint("WrongCall")
    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!isRunning) {
            job = launch {
                while (true) {
                    with(holder) {
                        lockCanvas(null)?.let {
                            try {
                                synchronized(this) {
                                    onDraw(null)
                                    output?.let { image ->
                                        it.drawBitmap(image, 0f, 0f, null)
                                    }
                                }
                            } finally {
                                unlockCanvasAndPost(it)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val w = width.toFloat()
        val h = height.toFloat()
        val cX = w / 2
        val cY = h / 2
        val now = SystemClock.elapsedRealtime()
        val delay = if (lastDrawTime > 0) now - lastDrawTime else 0
        lastDrawTime = now
        with(mCanvas) {
            setBitmap(output)
            positionY -= delay * speedPxPerMs
            if (positionY < 0) {
                positionY = radarSize
            }
            Timber.e("sDistance $positionY")
            val rectH = w / rectRatio
            rectGradient.set(0f, positionY, w, positionY + rectH)
            //drawBitmap(bmpGradient, null, rectGradient, null)
            drawBitmap(bmpDot, 0f, positionY, null)
            //drawRect(0f, 40f, w, 60f, paint)
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

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        synchronized(holder) {
            output?.recycle()
            output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        job?.cancel()
    }

    // todo call
    fun release() {
        job?.cancel()
        holder.removeCallback(this)
        output?.recycle()
        bmpGradient.recycle()
        bmpDot.recycle()
    }

    override val coroutineContext = Dispatchers.Default
}