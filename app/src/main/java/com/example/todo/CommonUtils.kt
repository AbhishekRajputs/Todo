package com.example.todo

import android.transition.Slide
import android.view.Gravity
import android.view.Window
import android.view.animation.DecelerateInterpolator


object CommonUtils {
    fun setAnimation(window: Window) {
        val slide = Slide()
        slide.slideEdge = Gravity.START
        slide.duration = 400
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }
}