package com.timgortworst.roomy.data.model.firestore

import com.google.firebase.firestore.PropertyName

data class EventMetaDataJson(
        @JvmField @PropertyName(EVENT_DATE_TIME_REF) var startDateTime: Long? = null,
        @JvmField @PropertyName(EVENT_TIME_ZONE_REF) var timeZone: String? = null,
        @JvmField @PropertyName(EVENT_FREQUENCY) val frequency: Int? = null,
        @JvmField @PropertyName(EVENT_RECURRENCE) val recurrenceType: String? = null,
        @JvmField @PropertyName(EVENT_ON_DAYS) val onDaysOfWeek: List<Int>? = null) {

    companion object {
        const val EVENT_DATE_TIME_REF = "start_datetime"
        const val EVENT_TIME_ZONE_REF = "time_zone"
        const val EVENT_RECURRENCE = "recurrence_type"
        const val EVENT_FREQUENCY = "recurrence_frequency"
        const val EVENT_ON_DAYS = "recurrence_weekdays"
    }
}