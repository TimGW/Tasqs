package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.EventMetaData.EventInterval.*
import org.threeten.bp.ZonedDateTime

fun ZonedDateTime.plusInterval(repeatInterval: EventMetaData.EventInterval): ZonedDateTime {
    return when (repeatInterval) {
        is SingleEvent -> plusDays(1)
        is Daily -> plusDays(1)
        is Weekly -> plusWeeks(1)
        is Monthly -> plusMonths(1)
        is Annually -> plusYears(1)
    }
}

fun ZonedDateTime.isDateInPast() = this.isBefore(ZonedDateTime.now())
