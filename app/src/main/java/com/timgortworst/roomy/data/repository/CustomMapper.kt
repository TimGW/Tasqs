package com.timgortworst.roomy.data.repository

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventRecurrence
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.firestore.EventRecurrenceJson
import com.timgortworst.roomy.data.model.firestore.EventJson
import com.timgortworst.roomy.data.model.firestore.EventMetaDataJson
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

object CustomMapper {
    fun toEvent(eventJson: EventJson): Event? {
        if (eventJson.eventId == null || eventJson.description == null || eventJson.metaData == null ||
                eventJson.user == null || eventJson.householdId == null) {
            return null
        }

        return Event(
                eventJson.eventId!!,
                eventJson.description!!,
                eventJson.metaData!!.toEventMetaData(),
                eventJson.user!!,
                eventJson.householdId!!)
    }

    private fun EventMetaDataJson.toEventMetaData(): EventMetaData {
        return EventMetaData(
                Instant
                        .ofEpochMilli(startDateTime!!)
                        .atZone(ZoneId.of(timeZone!!)),
                recurrence!!.toEventInterval())
    }

    private fun EventRecurrenceJson.toEventInterval(): EventRecurrence {
        return when {
            singleEvent == true -> EventRecurrence.SingleEvent
            everyXDays != null -> EventRecurrence.Daily(everyXDays)
            everyXWeeks != null && onDaysOfWeek != null -> EventRecurrence.Weekly(everyXWeeks, onDaysOfWeek)
            everyXMonths != null -> EventRecurrence.Monthly(everyXMonths)
            everyXYears != null -> EventRecurrence.Annually(everyXYears)
            else -> EventRecurrence.SingleEvent
        }
    }

    fun convertToMap(event: Event): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result[EventJson.EVENT_ID_REF] = event.eventId
        result[EventJson.EVENT_DESCRIPTION_REF] = event.description
        result[EventJson.EVENT_META_DATA_REF] = event.metaData.toMap()
        result[EventJson.EVENT_USER_REF] = event.user
        result[EventJson.EVENT_HOUSEHOLD_ID_REF] = event.householdId
        return result
    }

    private fun EventMetaData.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any>()
        result[EventMetaDataJson.EVENT_DATE_TIME_REF] = startDateTime.toInstant().toEpochMilli()
        result[EventMetaDataJson.EVENT_TIME_ZONE_REF] = startDateTime.zone.id
        result[EventMetaDataJson.EVENT_INTERVAL_REF] = recurrence.toMap()
        return result
    }

    private fun EventRecurrence.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any>()
        result[EventRecurrenceJson.EVENT_INTERVAL_NONE] = false
        when (this) {
            is EventRecurrence.SingleEvent -> result[EventRecurrenceJson.EVENT_INTERVAL_NONE] = true
            is EventRecurrence.Daily -> result[EventRecurrenceJson.EVENT_INTERVAL_DAYS] = everyXDays!!
            is EventRecurrence.Weekly -> {
                result[EventRecurrenceJson.EVENT_INTERVAL_WEEKS] = everyXWeeks!!
                result[EventRecurrenceJson.EVENT_INTERVAL_WEEKDAYS] = onDaysOfWeek!!
            }
            is EventRecurrence.Monthly -> result[EventRecurrenceJson.EVENT_INTERVAL_MONTHS] = everyXMonths!!
            is EventRecurrence.Annually -> result[EventRecurrenceJson.EVENT_INTERVAL_YEARS] = everyXYears!!
        }
        return result
    }
}
