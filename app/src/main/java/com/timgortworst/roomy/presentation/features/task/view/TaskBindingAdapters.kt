package com.timgortworst.roomy.presentation.features.task.view

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*


@BindingAdapter("formatDate")
fun TextView.formatDate(zonedDateTime: ZonedDateTime) {
    val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
    val formattedMonth = zonedDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val formattedYear = zonedDateTime.year.toString()
    text = "$formattedDayOfMonth $formattedMonth $formattedYear"
}

@BindingAdapter("formatRecurrence")
fun TextView.formatRecurrence(task: Task) {
    with(task.metaData.recurrence) {
        text = if (this is TaskRecurrence.SingleTask) {
            context.getString(R.string.is_not_repeated)
        } else {
            val weeklyAddon =
                if (this is TaskRecurrence.Weekly) " ${context.getString(R.string.on)} ${formatWeekdays(
                    context, onDaysOfWeek
                )}" else ""
            val isRepeatedOn = context.getString(R.string.is_repeated)
            val msg = if (frequency > 1) {
                "$isRepeatedOn $frequency ${context.getString(pluralName)}"
            } else {
                "$isRepeatedOn ${context.getString(name)}"
            }.plus(weeklyAddon)
            msg
        }
    }
}

// todo extract to viewmodel
private fun formatWeekdays(context: Context, daysOfWeek: List<Int>?): String {
    return daysOfWeek?.joinToString {
        when (it) {
            1 -> context.getString(R.string.repeat_mo)
            2 -> context.getString(R.string.repeat_tu)
            3 -> context.getString(R.string.repeat_we)
            4 -> context.getString(R.string.repeat_th)
            5 -> context.getString(R.string.repeat_fr)
            6 -> context.getString(R.string.repeat_sa)
            7 -> context.getString(R.string.repeat_su)
            else -> "-"
        }
    }.orEmpty()
}