package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventRecurrence
import org.koin.dsl.module
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

val timeCalcModule = module { single { TimeOperations() } }

class TimeOperations {
    fun nextEvent(baseLine: ZonedDateTime, recurrence: EventRecurrence): ZonedDateTime = when (recurrence) {
        is EventRecurrence.SingleEvent -> baseLine.plusDays(1)
        is EventRecurrence.Daily -> baseLine.plusDays(recurrence.frequency.toLong())
        is EventRecurrence.Weekly -> calcNextWeekDay(baseLine, recurrence)
        is EventRecurrence.Monthly -> baseLine.plusMonths(recurrence.frequency.toLong())
        is EventRecurrence.Annually -> baseLine.plusYears(recurrence.frequency.toLong())
    }

    fun nextEvent(baseLine: ZonedDateTime,
                  recurrence: String,
                  freq: Long,
                  onDaysOfWeek: List<Int>): ZonedDateTime = when (recurrence) {
        EventRecurrence.DAILY_EVENT -> baseLine.plusDays(freq)
        EventRecurrence.WEEKLY_EVENT -> calcNextWeekDay(baseLine, EventRecurrence.Weekly(freq.toInt(), onDaysOfWeek))
        EventRecurrence.MONTHLY_EVENT -> baseLine.plusMonths(freq)
        EventRecurrence.ANNUAL_EVENT -> baseLine.plusYears(freq)
        else -> baseLine.plusDays(1)
    }

    fun calcNextWeekDay(baseDate: ZonedDateTime, recurrence: EventRecurrence.Weekly): ZonedDateTime {
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
