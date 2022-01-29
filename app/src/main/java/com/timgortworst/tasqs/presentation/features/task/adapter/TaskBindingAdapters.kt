package com.timgortworst.tasqs.presentation.features.task.adapter

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.Response
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import java.util.*


@BindingAdapter("loadingVisibility")
fun ProgressBar.loadingVisibility(response: Response<Task>?) {
    visibility = if (response is Response.Loading) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("setDoneButtonActive")
fun MaterialButton.setDoneButtonActive(response: Response<Task>?) {
    isEnabled = response !is Response.Loading
}

@BindingAdapter("formatDateTime")
fun TextView.formatDateTime(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            val zonedDateTime = response.data?.metaData?.startDateTime ?: return
            val formattedDayOfWeek = zonedDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val formattedDayOfMonth = zonedDateTime.dayOfMonth.toString()
            val formattedMonth = zonedDateTime.month?.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val formattedYear = zonedDateTime.year.toString()
            "$formattedDayOfWeek $formattedDayOfMonth $formattedMonth $formattedYear @ ${formatTime(zonedDateTime)}"
        }
        is Response.Error -> context.getString(R.string.error_generic)
        else -> return
    }
}

fun formatTime(taskDateTime: ZonedDateTime): String {
    return String.format("%02d:%02d", taskDateTime.hour, taskDateTime.minute)
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
                val weeklyAddon = if (recurrence is TaskRecurrence.Weekly) " ${context.getString(R.string.on)} ${formatWeekdays(
                        context, recurrence.onDaysOfWeek
                    )}" else ""
                val msg = if (recurrence.frequency > 1) {
                    "${recurrence.frequency} ${context.getString(recurrence.pluralName)}"
                } else {
                    context.getString(recurrence.name)
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

@BindingAdapter("formatRotate")
fun TextView.formatRotate(response: Response<Task>?) {
    text = when(response) {
        Response.Loading -> "-"
        is Response.Success -> {
            if(response.data?.metaData?.rotateUser == true) {
                context.getString(R.string.yes)
            } else {
                context.getString(R.string.no)
            }
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