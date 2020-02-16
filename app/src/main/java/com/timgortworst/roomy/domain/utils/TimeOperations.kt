package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventRecurrence
import org.threeten.bp.ZonedDateTime

fun ZonedDateTime.plusInterval(recurrence: EventRecurrence): ZonedDateTime {
    return when (recurrence) {
        is EventRecurrence.SingleEvent -> plusDays(1)
        is EventRecurrence.Daily -> plusDays(1)
        is EventRecurrence.Weekly -> plusWeeks(1)
        is EventRecurrence.Monthly -> plusMonths(1)
        is EventRecurrence.Annually -> plusYears(1)
    }
}

fun ZonedDateTime.isDateInPast() = this.isBefore(ZonedDateTime.now())
