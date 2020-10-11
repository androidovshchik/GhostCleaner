package com.ghostcleaner.service

interface Optimization<T> {

    fun beforeOptimize() {}

    fun onOptimize(progress: T) {}

    fun afterOptimize() {}
}