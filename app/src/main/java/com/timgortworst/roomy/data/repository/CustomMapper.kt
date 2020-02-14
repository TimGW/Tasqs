package com.timgortworst.roomy.data.repository

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventInterval
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.firestore.EventIntervalJson
import com.timgortworst.roomy.data.model.firestore.EventJson
import com.timgortworst.roomy.data.model.firestore.EventMetaDataJson
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

object CustomMapper {
    fun toEvent(eventJson: EventJson): Event? {
        if (eventJson.eventId == null || eventJson.description == null || eventJson.eventMetaData == null ||
                eventJson.user == null || eventJson.householdId == null) {
            return null
        }

        return Event(
                eventJson.eventId!!,
                eventJson.description!!,
                eventJson.eventMetaData!!.toEventMetaData(),
                eventJson.user!!,
                eventJson.householdId!!)
    }

    private fun EventMetaDataJson.toEventMetaData(): EventMetaData {
        return EventMetaData(
                Instant
                        .ofEpochMilli(eventDateTime!!)
                        .atZone(ZoneId.of(eventTimeZone!!)),
                eventInterval!!.toEventInterval())
    }

    private fun EventIntervalJson.toEventInterval(): EventInterval {
        return when {
            singleEvent == true -> EventInterval.SingleEvent
            everyXDays != null -> EventInterval.Daily(everyXDays)
            everyXWeeks != null && onDaysOfWeek != null -> EventInterval.Weekly(everyXWeeks, onDaysOfWeek)
            everyXMonths != null -> EventInterval.Monthly(everyXMonths)
            everyXYears != null -> EventInterval.Annually(everyXYears)
            else -> EventInterval.SingleEvent
        }
    }

    fun convertToMap(event: Event): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result[EventJson.EVENT_ID_REF] = event.eventId
        result[EventJson.EVENT_DESCRIPTION_REF] = event.description
        result[EventJson.EVENT_META_DATA_REF] = event.eventMetaData.toMap()
        result[EventJson.EVENT_USER_REF] = event.user
        result[EventJson.EVENT_HOUSEHOLD_ID_REF] = event.householdId
        return result
    }

    private fun EventMetaData.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any>()
        result[EventMetaDataJson.EVENT_DATE_TIME_REF] = eventTimestamp.toInstant().toEpochMilli()
        result[EventMetaDataJson.EVENT_TIME_ZONE_REF] = eventTimestamp.zone.id
        result[EventMetaDataJson.EVENT_INTERVAL_REF] = eventInterval.toMap()
        return result
    }

    private fun EventInterval.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any>()
        result[EventIntervalJson.EVENT_INTERVAL_NONE] = false
        when (this) {
            is EventInterval.SingleEvent -> result[EventIntervalJson.EVENT_INTERVAL_NONE] = true
            is EventInterval.Daily -> result[EventIntervalJson.EVENT_INTERVAL_DAYS] = everyXDays
            is EventInterval.Weekly -> {
                result[EventIntervalJson.EVENT_INTERVAL_WEEKS] = everyXWeeks
                result[EventIntervalJson.EVENT_INTERVAL_WEEKDAYS] = onDaysOfWeek
            }
            is EventInterval.Monthly -> result[EventIntervalJson.EVENT_INTERVAL_MONTHS] = everyXMonths
            is EventInterval.Annually -> result[EventIntervalJson.EVENT_INTERVAL_YEARS] = everyXYears
        }
        return result
    }
}
