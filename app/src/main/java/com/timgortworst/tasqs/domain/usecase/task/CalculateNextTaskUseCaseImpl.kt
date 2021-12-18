package com.timgortworst.tasqs.domain.usecase.task

import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.presentation.usecase.task.CalculateNextTaskUseCase
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class CalculateNextTaskUseCaseImpl : CalculateNextTaskUseCase {

    data class Params(val baseDate: ZonedDateTime, val recurrence: TaskRecurrence)

    override fun execute(params: Params): ZonedDateTime {
        return params.baseDate.plus(params.recurrence)
    }

    private fun ZonedDateTime.plus(recurrence: TaskRecurrence): ZonedDateTime = when (recurrence) {
        is TaskRecurrence.SingleTask -> this.plusDays(1)
        is TaskRecurrence.Daily -> this.plusDays(recurrence.frequency.toLong())
        is TaskRecurrence.Weekly -> calcNextWeekDay(this, recurrence)
        is TaskRecurrence.Monthly -> this.plusMonths(recurrence.frequency.toLong())
        is TaskRecurrence.Annually -> this.plusYears(recurrence.frequency.toLong())
    }

    private fun calcNextWeekDay(baseDate: ZonedDateTime, recurrence: TaskRecurrence.Weekly): ZonedDateTime {
        val baseWeekDay = baseDate.get(ChronoField.DAY_OF_WEEK)
        val skipNumOfWeeks = recurrence.frequency.toLong() - 1L
        var result: ZonedDateTime = baseDate

        val nextWeekDay = recurrence.onDaysOfWeek.find {
            it > baseWeekDay
        } ?: run {
            result = baseDate.plusWeeks(skipNumOfWeeks)
            recurrence.onDaysOfWeek.first()
        }
        val dayDiff = (WEEK_LENGTH + (nextWeekDay - baseWeekDay)) % WEEK_LENGTH

        return if (dayDiff == 0L) {
            result.plusWeeks(1)
        } else {
            result.plusDays(dayDiff)
        }
    }

    companion object {
        private const val WEEK_LENGTH = 7L
    }
}

