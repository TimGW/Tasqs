package com.timgortworst.roomy.ui.customview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.timgortworst.roomy.R
import kotlinx.android.synthetic.main.custom_profile_statistic.view.*

class ProfileStatistic : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    protected fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        LayoutInflater.from(context).inflate(R.layout.custom_profile_statistic, this, true)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileStatistic, defStyleAttr, 0)
        if (a != null) {
            val statHeader = a.getString(R.styleable.ProfileStatistic_profile_stat_header)
            val statValue = a.getString(R.styleable.ProfileStatistic_profile_stat_value)

            if (!TextUtils.isEmpty(statHeader)) {
                profile_stat_header.text = statHeader
            }
            if (!TextUtils.isEmpty(statValue)) {
                profile_stat_value.text = statValue
            }
            a.recycle()
        }
    }

    fun setStatHeader(text: String) {
        profile_stat_header.text = text
    }

    fun setStatValue(text: String) {
        profile_stat_value.text = text
    }
}

