package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventInterval
import org.threeten.bp.ZonedDateTime

fun ZonedDateTime.plusInterval(repeatInterval: EventInterval): ZonedDateTime {
    return when (repeatInterval) {
        is EventInterval.SingleEvent -> plusDays(1)
        is EventInterval.Daily -> plusDays(1)
        is EventInterval.Weekly -> plusWeeks(1)
        is EventInterval.Monthly -> plusMonths(1)
        is EventInterval.Annually -> plusYears(1)
    }
}

fun ZonedDateTime.isDateInPast() = this.isBefore(ZonedDateTime.now())
