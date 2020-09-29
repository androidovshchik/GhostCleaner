package com.ghostcleaner.screen.main.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
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
import java.util.*

/**
 * see https://github.com/cchandurkar/Glowy
 */
class GlowingText(
    private val activity: Activity,
    private val view: View,
    private var minGlowRadius: Float,
    private var maxGlowRadius: Float,
    private var startGlowRadius: Float,
    color: Int,
    speed: Int
) {
    var currentGlowRadius = startGlowRadius
        private set
    private val dx = 0f
    private val dy = 0f
    private var glowColor = -0x1

    //ffffff defines hexadecimal value of color
    private var glowSpeed = 0xFFffffff
    private var isDirectionUp = true // Whether radius should increase or Decrease.
    private val handler = Handler(Looper.getMainLooper())
    private val r: Runnable = object : Runnable {
        override fun run() {
            // Check Which View Is this
            if (view is TextView) {
                view.setShadowLayer(currentGlowRadius, dx, dy, glowColor)
            }
            /* currentGlowRadius  -     Glow radius.
             * dx                 -     Spread of Shadow in X direction
             * dy                 -     Spread of Shadow in Y direction
             * color              -     Color used to create Glow (White in our case )
             */if (isDirectionUp) {
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
            // Keep Looping
            handler.postDelayed(this, glowSpeed.toLong())
        }
    }

    init {
        glowColor = color
        glowSpeed = speed
        if (view is TextView) {
            if (startGlowRadius < minGlowRadius || startGlowRadius > maxGlowRadius) {
                val r = Random()
                startGlowRadius =
                    r.nextInt(maxGlowRadius.toInt() - minGlowRadius.toInt() + 1) + minGlowRadius as Int.toFloat()
            }
            // Scale Up Glowing Transition as milliseconds
            glowSpeed *= 25
            startGlowing()
        }
    }

    private fun startGlowing() {
        handler.postDelayed(r, glowSpeed.toLong())
    }

    fun setStartGlowRadius(startRadius: Float) {
        activity.runOnUiThread { startGlowRadius = startRadius }
    }

    fun setMaxGlowRadius(maxRadius: Float) {
        activity.runOnUiThread { maxGlowRadius = maxRadius }
    }

    fun setMinGlowRadius(minRadius: Float) {
        activity.runOnUiThread { minGlowRadius = minRadius }
    }

    fun setGlowColor(color: Int) {
        activity.runOnUiThread { glowColor = color }
    }

    fun getStartGlowRadius(): Float {
        return startGlowRadius
    }

    fun getMaxGlowRadius(): Float {
        return maxGlowRadius
    }

    fun getMinGlowRadius(): Float {
        return minGlowRadius
    }

    var transitionSpeed: Int
        get() = glowSpeed
        set(speed) {
            activity.runOnUiThread { glowSpeed = speed }
        }

    fun getGlowColor(): String {
        return String.format("#%X", glowColor)
    }

    fun stopGlowing() {
        handler.removeCallbacks(r)
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
            activity,  // Pass activity Object
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