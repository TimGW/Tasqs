package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventMetaData
import org.threeten.bp.ZonedDateTime

fun ZonedDateTime.plusInterval(repeatInterval: EventMetaData.EventInterval): ZonedDateTime {
    return when (repeatInterval) {
        EventMetaData.EventInterval.SINGLE_EVENT -> plusDays(0)
        EventMetaData.EventInterval.DAILY -> plusDays(1)
        EventMetaData.EventInterval.WEEKLY -> plusWeeks(1)
        EventMetaData.EventInterval.MONTHLY -> plusMonths(1)
        EventMetaData.EventInterval.ANNUALLY -> plusYears(1)
    }
}
 fun ZonedDateTime.isDateInPast() = this.isBefore(ZonedDateTime.now())
