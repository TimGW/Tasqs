package com.timgortworst.roomy.data.model.firestore

import com.google.firebase.firestore.PropertyName

data class EventIntervalJson(
        @JvmField @PropertyName(EVENT_INTERVAL_NONE) val singleEvent: Boolean? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_DAYS) val everyXDays: Int? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_WEEKS) val everyXWeeks: Int? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_WEEKDAYS) val onDaysOfWeek: List<Int>? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_MONTHS) val everyXMonths: Int? = null,
        @JvmField @PropertyName(EVENT_INTERVAL_YEARS) val everyXYears: Int? = null) {

    companion object {
        const val EVENT_INTERVAL_NONE = "single_event"
        const val EVENT_INTERVAL_DAYS = "every_x_days"
        const val EVENT_INTERVAL_WEEKS = "every_x_weeks"
        const val EVENT_INTERVAL_WEEKDAYS = "every_x_weekdays"
        const val EVENT_INTERVAL_MONTHS = "every_x_months"
        const val EVENT_INTERVAL_YEARS = "every_x_years"
    }
}
