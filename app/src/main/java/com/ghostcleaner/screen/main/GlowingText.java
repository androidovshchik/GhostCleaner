package com.ghostcleaner.screen.main;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * see https://github.com/cchandurkar/Glowy
 */
public class GlowingText {

    private Activity activity;

    private View view;

    private float startGlowRadius,
        minGlowRadius,
        maxGlowRadius,
        currentGlowRadius = startGlowRadius,
        dx = 0f,
        dy = 0f;

    private int glowColor = 0xFFffffff,    //ffffff defines hexadecimal value of color
        glowSpeed;

    private boolean isDirectionUp = true;  // Whether radius should increase or Decrease.

    private Handler handler;
    private Runnable r;

    public GlowingText(Activity mActivity, View v, float minRadius, float maxRadius, float startRadius, int color, int speed) {
        this.activity = mActivity;
        this.view = v;
        this.minGlowRadius = minRadius;
        this.maxGlowRadius = maxRadius;
        this.startGlowRadius = startRadius;
        this.glowColor = color;
        this.glowSpeed = speed;
        if (view instanceof TextView) {
            if (startGlowRadius < minGlowRadius || startGlowRadius > maxGlowRadius) {
                Random r = new Random();
                startGlowRadius = r.nextInt((int) maxGlowRadius - (int) minGlowRadius + 1) + (int) minGlowRadius;
            }
            // Scale Up Glowing Transition as milliseconds
            glowSpeed *= 25;
            startGlowing();
        }
    }

    private void startGlowing() {
        handler = new Handler();
        r = new Runnable() {
            public void run() {
                // Check Which View Is this
                if (view instanceof TextView) {
                    ((TextView) view).setShadowLayer(currentGlowRadius, dx, dy, glowColor);
                }
                /* currentGlowRadius  -     Glow radius.
                 * dx                 -     Spread of Shadow in X direction
                 * dy                 -     Spread of Shadow in Y direction
                 * color              -     Color used to create Glow (White in our case )
                 */
                if (isDirectionUp) {
                    /* Increase Glow Radius by 1 */
                    if (currentGlowRadius < maxGlowRadius) {
                        /* Maximun has not reached. Carry On */
                        currentGlowRadius++;
                    } else {
                        /* Oops !! Max is reached. Stars decreasing glow radius now.
                         * Change the Direction to Down. */
                        isDirectionUp = false;
                    }
                } else {
                    /* Decrease Glow Radius by 1 */
                    if (currentGlowRadius > minGlowRadius) {
                        /* Minimum has not reached yet. Keep Decreasing */
                        currentGlowRadius--;
                    } else {
                        /* Oops !! Min is reached. Stars Increasing glow radius again.
                         * Change the Direction to UP */
                        isDirectionUp = true;
                    }
                }
                // Keep Looping
                handler.postDelayed(this, glowSpeed);
            }
        };

        // Starts Animation
        handler.postDelayed(r, glowSpeed);
    }

    public void setStartGlowRadius(final float startRadius) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                startGlowRadius = startRadius;
            }
        });
    }

    public void setMaxGlowRadius(final float maxRadius) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                maxGlowRadius = maxRadius;
            }
        });
    }

    public void setMinGlowRadius(final float minRadius) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                minGlowRadius = minRadius;
            }
        });
    }

    public void setTransitionSpeed(final int speed) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                glowSpeed = speed;
            }
        });
    }

    public void setGlowColor(final int color) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                glowColor = color;
            }
        });
    }

    public float getStartGlowRadius() {
        return this.startGlowRadius;
    }

    public float getMaxGlowRadius() {
        return this.maxGlowRadius;
    }

    public float getMinGlowRadius() {
        return this.minGlowRadius;
    }

    public float getCurrentGlowRadius() {
        return this.currentGlowRadius;
    }

    public int getTransitionSpeed() {
        return this.glowSpeed;
    }

    public String getGlowColor() {
        return String.format("#%X", glowColor);
    }

    public void stopGlowing() {
        /* 	Destroy the Runnable r */
        handler.removeCallbacks(r);
    }
}