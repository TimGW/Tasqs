package com.timgortworst.roomy.data.model.firestore

import com.google.firebase.firestore.PropertyName

data class EventMetaDataJson(
        @JvmField @PropertyName(EVENT_DATE_TIME_REF) var startDateTime: Long? = null,
        @JvmField @PropertyName(EVENT_TIME_ZONE_REF) var timeZone: String? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_REF) var recurrence: EventRecurrenceJson? = null
) {
    companion object {
        const val EVENT_INTERVAL_REF = "recurrence"
        const val EVENT_DATE_TIME_REF = "start_datetime"
        const val EVENT_TIME_ZONE_REF = "time_zone"
    }
}