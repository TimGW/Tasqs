package com.timgortworst.roomy.domain.utils

import com.google.firebase.Timestamp
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.utils.Constants.DEFAULT_HOUR_OF_DAY_NOTIFICATION
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

interface TimeOperations {
    fun nowUTC(): Instant
    fun todayAtEightUTC(): Instant
    fun format(dateTimeEvent: ZonedDateTime): String
    fun isDateInPast(timestamp: Instant): Boolean

    class Impl : TimeOperations {
        override fun nowUTC() = Instant.now()

        override fun todayAtEightUTC() = Instant.now().atZone(ZoneOffset.UTC)
                .withHour(DEFAULT_HOUR_OF_DAY_NOTIFICATION)
                .truncatedTo(ChronoUnit.HOURS)
                .toInstant()

        override fun format(dateTimeEvent: ZonedDateTime): String {
            val formatter = DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(Locale.getDefault())

            return dateTimeEvent.format(formatter)
        }

        override fun isDateInPast(timestamp: Instant): Boolean {
            return timestamp.isBefore(nowUTC())
        }
    }
}

fun Instant.plusInterval(repeatInterval: EventMetaData.EventInterval): Instant {
    return when (repeatInterval) {
        EventMetaData.EventInterval.SINGLE_EVENT -> plus(1, ChronoUnit.DAYS)
        EventMetaData.EventInterval.DAILY -> plus(1, ChronoUnit.DAYS)
        EventMetaData.EventInterval.WEEKLY -> plus(1, ChronoUnit.WEEKS)
        EventMetaData.EventInterval.MONTHLY -> plus(1, ChronoUnit.MONTHS)
        EventMetaData.EventInterval.ANNUALLY -> plus(1, ChronoUnit.YEARS)
    }
}

fun Timestamp.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000), zone)
}

fun Timestamp.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    val instant = Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000)
    return instant.atZone(zone).toLocalDate()
}

fun Instant.toTimestamp() = Timestamp(atZone(ZoneId.systemDefault()).toEpochSecond(), nano)

fun LocalDate.toTimestamp() = atTime(DEFAULT_HOUR_OF_DAY_NOTIFICATION, 0).atZone(ZoneId.systemDefault()).toInstant().toTimestamp()

fun Timestamp.toInstant(): Instant {
    return Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000)
}
