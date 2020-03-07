package com.timgortworst.roomy.presentation.base.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    private var animationDuration: Int = 300

    override fun onAttach(context: Context) {
        super.onAttach(context)

        animationDuration = activity
            ?.resources
            ?.getInteger(android.R.integer.config_mediumAnimTime)
            ?: 300
    }

    fun toggleFadeViews(fromView: View, toView: View) {
        toView.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(animationDuration.toLong())
                .setListener(null)
        }

        fromView.animate()
            .alpha(0f)
            .setDuration(animationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fromView.visibility = View.GONE
                }
            })
    }
}
