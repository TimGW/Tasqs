package com.timgortworst.roomy.presentation.base.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object ViewFadeAnimator {
    private const val ANIM_DURATION = 400L

    fun toggleFadeViews(fromView: View, toView: View) {
        toView.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(ANIM_DURATION)
                .setListener(null)
        }

        fromView.animate()
            .alpha(0f)
            .setDuration(ANIM_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fromView.visibility = View.GONE
                }
            })
    }
}