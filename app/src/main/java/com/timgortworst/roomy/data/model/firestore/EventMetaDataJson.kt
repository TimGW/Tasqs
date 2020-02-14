package com.timgortworst.roomy.data.model.firestore

import com.google.firebase.firestore.PropertyName
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventInterval
import com.timgortworst.roomy.data.model.EventMetaData
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

data class EventMetaDataJson(
        @JvmField @PropertyName(EVENT_DATE_TIME_REF) var eventDateTime: Long? = null,
        @JvmField @PropertyName(EVENT_TIME_ZONE_REF) var eventTimeZone: String? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_REF) var eventInterval: EventIntervalJson? = null
) {
    companion object {
        const val EVENT_INTERVAL_REF = "interval"
        const val EVENT_DATE_TIME_REF = "start_datetime"
        const val EVENT_TIME_ZONE_REF = "time_zone"
    }
}