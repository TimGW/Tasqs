package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.domain.model.TaskRecurrence
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class TimeOperations {
    fun nextTask(baseLine: ZonedDateTime, recurrence: TaskRecurrence): ZonedDateTime = when (recurrence) {
        is TaskRecurrence.SingleTask -> baseLine.plusDays(1)
        is TaskRecurrence.Daily -> baseLine.plusDays(recurrence.frequency.toLong())
        is TaskRecurrence.Weekly -> calcNextWeekDay(baseLine, recurrence)
        is TaskRecurrence.Monthly -> baseLine.plusMonths(recurrence.frequency.toLong())
        is TaskRecurrence.Annually -> baseLine.plusYears(recurrence.frequency.toLong())
    }

    fun calcNextWeekDay(baseDate: ZonedDateTime, recurrence: TaskRecurrence.Weekly): ZonedDateTime {
        val baseWeekDay = baseDate.get(ChronoField.DAY_OF_WEEK)
        val skipNumOfWeeks = recurrence.frequency.toLong() - 1L
        var result: ZonedDateTime = baseDate

        val nextWeekDay = recurrence.onDaysOfWeek.find {
            it > baseWeekDay
        } ?: run {
            result = baseDate.plusWeeks(skipNumOfWeeks)
            recurrence.onDaysOfWeek.first()
        }
        val dayDiff = (7L + (nextWeekDay - baseWeekDay)) % 7L

        return if (dayDiff == 0L) {
            result.plusWeeks(1)
        } else {
            result.plusDays(dayDiff)
        }
    }
}
