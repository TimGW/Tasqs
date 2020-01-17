package com.timgortworst.roomy.domain.utils

import com.google.firebase.Timestamp
import com.timgortworst.roomy.data.model.EventMetaData
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
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

fun Timestamp.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000), zone)
}

fun ZonedDateTime.toTimestamp() = Timestamp(second.toLong(), nano)