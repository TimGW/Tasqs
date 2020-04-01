package com.timgortworst.roomy.presentation.base.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.CustomRepeatIconBinding
import com.timgortworst.roomy.domain.model.task.TaskRecurrence

class RepeatIcon
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var _binding: CustomRepeatIconBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = CustomRepeatIconBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setRepeatLabelText(interval: TaskRecurrence) {
        val label = when (interval) {
            is TaskRecurrence.SingleTask -> context.getString(R.string.empty_string)
            is TaskRecurrence.Daily -> context.getString(R.string.repeat_label_interval_text_day)
            is TaskRecurrence.Weekly -> context.getString(R.string.repeat_label_interval_text_week)
            is TaskRecurrence.Monthly -> context.getString(R.string.repeat_label_interval_text_month)
            is TaskRecurrence.Annually -> context.getString(R.string.repeat_label_interval_text_year)
        }
        binding.taskRepeatText.text = label.toUpperCase()
    }
}

