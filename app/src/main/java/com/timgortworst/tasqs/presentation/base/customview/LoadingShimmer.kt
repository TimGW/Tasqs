package com.timgortworst.tasqs.presentation.base.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.timgortworst.tasqs.R

class LoadingShimmer(
    context: Context?,
    attrs: AttributeSet?
) : View(context, attrs) {

    private val myPaint = Paint()
    private val rect = RectF()
    private val animator: ValueAnimator = ValueAnimator.ofFloat(1f, 0.6f)

    init {
        animator.duration = 500
        animator.addUpdateListener { alpha = it.animatedValue as Float }
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect.right = width.toFloat()
        rect.bottom = height.toFloat()

        myPaint.color = ContextCompat.getColor(context, R.color.color_alpha_20)
        myPaint.style = Paint.Style.FILL
        myPaint.isAntiAlias = true

        canvas.drawRoundRect(rect, 10f, 10f, myPaint)
    }
}