package com.timgortworst.roomy.presentation.base.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.EventMetaData.EventInterval.*
import kotlinx.android.synthetic.main.custom_repeat_icon.view.*

class RepeatIcon
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_repeat_icon, this, true)
    }

    fun setRepeatLabelText(interval: EventMetaData.EventInterval) {
        val label = when (interval) {
            is SingleEvent -> context.getString(R.string.empty_string)
            is Daily -> context.getString(R.string.repeat_label_interval_text_day)
            is Weekly -> context.getString(R.string.repeat_label_interval_text_week)
            is Monthly -> context.getString(R.string.repeat_label_interval_text_month)
            is Annually -> context.getString(R.string.repeat_label_interval_text_year)
        }
        event_repeat_text.text = label.toUpperCase()
    }
}

