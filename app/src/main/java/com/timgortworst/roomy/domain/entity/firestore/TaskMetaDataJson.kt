package com.timgortworst.roomy.domain.entity.firestore

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class TaskMetaDataJson(
    @JvmField @PropertyName(TASK_DATE_TIME_REF) var startDateTime: Long? = null,
    @JvmField @PropertyName(TASK_TIME_ZONE_REF) var timeZone: String? = null,
    @JvmField @PropertyName(TASK_FREQUENCY) val frequency: Int? = null,
    @JvmField @PropertyName(TASK_RECURRENCE) val recurrenceType: String? = null,
    @JvmField @PropertyName(TASK_ON_DAYS) val onDaysOfWeek: List<Int>? = null) {

    companion object {
        const val TASK_DATE_TIME_REF = "start_datetime"
        const val TASK_TIME_ZONE_REF = "time_zone"
        const val TASK_RECURRENCE = "recurrence_type"
        const val TASK_FREQUENCY = "recurrence_frequency"
        const val TASK_ON_DAYS = "recurrence_weekdays"
    }
}