package com.ghostcleaner.screen.main.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.ghostcleaner.R
import kotlinx.android.synthetic.main.merge_no_ads.view.*

/**
 * see https://github.com/cchandurkar/Glowy
 */
@Suppress("MemberVisibilityCanBePrivate")
class GlowingText(
    private val view: TextView,
    private var minGlowRadius: Float,
    private var maxGlowRadius: Float,
    private var startGlowRadius: Float,
    private var glowColor: Int,
    private var glowSpeed: Long
) {
    private val handler = Handler(Looper.getMainLooper())

    var currentGlowRadius = startGlowRadius
        private set

    private var isDirectionUp = true // Whether radius should increase or Decrease.
    private val runnable = object : Runnable {
        override fun run() {
            view.setShadowLayer(currentGlowRadius, 0f, 0f, glowColor)
            /* currentGlowRadius  -     Glow radius.
             * dx                 -     Spread of Shadow in X direction
             * dy                 -     Spread of Shadow in Y direction
             * color              -     Color used to create Glow (White in our case )
             */
            if (isDirectionUp) {
                /* Increase Glow Radius by 1 */
                if (currentGlowRadius < maxGlowRadius) {
                    /* Maximun has not reached. Carry On */
                    currentGlowRadius++
                } else {
                    /* Oops !! Max is reached. Stars decreasing glow radius now.
                     * Change the Direction to Down. */
                    isDirectionUp = false
                }
            } else {
                /* Decrease Glow Radius by 1 */
                if (currentGlowRadius > minGlowRadius) {
                    /* Minimum has not reached yet. Keep Decreasing */
                    currentGlowRadius--
                } else {
                    /* Oops !! Min is reached. Stars Increasing glow radius again.
                     * Change the Direction to UP */
                    isDirectionUp = true
                }
            }
            handler.postDelayed(this, glowSpeed)
        }
    }

    init {
        startGlowRadius =
            (0..maxGlowRadius.toInt() - minGlowRadius.toInt()).random() + minGlowRadius
        glowSpeed *= 25
    }

    fun startGlowing() {
        handler.postDelayed(runnable, glowSpeed)
    }

    fun stopGlowing() {
        handler.removeCallbacks(runnable)
    }
}

class NoAdsLayout : LinearLayout {

    private lateinit var glowingText: GlowingText

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
        glowingText = GlowingText(
            tv_ads,  // TextView
            3f,  // Minimum Glow Radius
            15f,  // Maximum Glow Radius
            6f,  // Start Glow Radius - Increases to MaxGlowRadius then decreases to MinGlowRadius.
            Color.GREEN,  // Glow Color (int)
            10
        )
        orientation = VERTICAL
        View.inflate(context, R.layout.merge_no_ads, this)
        tv_ads
    }

    override fun hasOverlappingRendering() = false
}