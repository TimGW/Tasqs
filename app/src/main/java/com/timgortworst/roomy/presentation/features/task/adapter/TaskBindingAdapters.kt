package com.timgortworst.roomy.presentation.features.task.adapter

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.response.Response
import org.threeten.bp.format.TextStyle
import java.util.*


@BindingAdapter("loadingVisibility")
fun ProgressBar.loadingVisibility(response: Response<Task>?) {
    visibility = if (response is Response.Loading) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("formatDate")
fun TextView.formatDate(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            val zonedDateTime = response.data?.metaData?.startDateTime ?: return
            val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
            val formattedMonth =
                zonedDateTime.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val formattedYear = zonedDateTime.year.toString()
            "$formattedDayOfMonth $formattedMonth $formattedYear"
        }
        is Response.Error -> context.getString(R.string.error_generic)
        else -> return
    }
}

@BindingAdapter("formatRecurrence")
fun TextView.formatRecurrence(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            val recurrence = response.data?.metaData?.recurrence ?: return
            if (recurrence is TaskRecurrence.SingleTask) {
                context.getString(R.string.is_not_repeated)
            } else {
                val weeklyAddon =
                    if (recurrence is TaskRecurrence.Weekly) " ${context.getString(R.string.on)} ${formatWeekdays(
                        context, recurrence.onDaysOfWeek
                    )}" else ""
                val isRepeatedOn = context.getString(R.string.is_repeated)
                val msg = if (recurrence.frequency > 1) {
                    "$isRepeatedOn ${recurrence.frequency} ${context.getString(recurrence.pluralName)}"
                } else {
                    "$isRepeatedOn ${context.getString(recurrence.name)}"
                }.plus(weeklyAddon)
                msg
            }
        }
        is Response.Error -> context.getString(R.string.error_generic)
        else -> return
    }
}

@BindingAdapter("formatDescription")
fun TextView.formatDescription(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            val description = response.data?.description
            if (description.isNullOrBlank()) "-" else description
        }
        is Response.Error -> context.getString(R.string.error_generic)
        else -> return
    }
}

@BindingAdapter("formatName")
fun TextView.formatName(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            val name = response.data?.user?.name
            if (name.isNullOrBlank()) "-" else name
        }
        is Response.Error -> context.getString(R.string.error_generic)
        else -> return
    }
}

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
    } ?: "-"
}