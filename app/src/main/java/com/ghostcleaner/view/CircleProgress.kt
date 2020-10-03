package com.ghostcleaner.view

import com.mikhaellopez.circularprogressbar.CircularProgressBar

class CircleProgress(
    private var pbOuter: CircularProgressBar,
    private var pbInner: CircularProgressBar
) {

    var progress: Float
        get() = progressOuter
        set(value) {
            progressOuter = value
            progressInner = value
        }

    var progressOuter: Float
        get() = pbOuter.progress
        set(value) {
            pbOuter.progress = value
        }

    var progressInner: Float
        get() = pbInner.progress
        set(value) {
            pbInner.progress = value
        }
}