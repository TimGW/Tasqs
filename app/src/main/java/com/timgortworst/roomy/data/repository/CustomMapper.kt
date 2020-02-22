package com.timgortworst.roomy.data.repository

import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.model.EventRecurrence
import com.timgortworst.roomy.domain.model.EventRecurrence.Companion.ANNUAL_EVENT
import com.timgortworst.roomy.domain.model.EventRecurrence.Companion.DAILY_EVENT
import com.timgortworst.roomy.domain.model.EventRecurrence.Companion.MONTHLY_EVENT
import com.timgortworst.roomy.domain.model.EventRecurrence.Companion.WEEKLY_EVENT
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.model.firestore.EventMetaDataJson
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
                buildRecurrence(recurrenceType, frequency ?: 1, onDaysOfWeek.orEmpty()))
    }

    private fun buildRecurrence(recurrenceType: String?, frequency: Int, onDaysOfWeek: List<Int>): EventRecurrence {
        return when (recurrenceType) {
            DAILY_EVENT -> EventRecurrence.Daily(frequency)
            WEEKLY_EVENT -> EventRecurrence.Weekly(frequency, onDaysOfWeek)
            MONTHLY_EVENT -> EventRecurrence.Monthly(frequency)
            ANNUAL_EVENT -> EventRecurrence.Annually(frequency)
            else -> EventRecurrence.SingleEvent(frequency)
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
        val result = mutableMapOf<String, Any?>()
        result[EventMetaDataJson.EVENT_DATE_TIME_REF] = startDateTime.toInstant().toEpochMilli()
        result[EventMetaDataJson.EVENT_TIME_ZONE_REF] = startDateTime.zone.id
        if (recurrence !is EventRecurrence.SingleEvent) result[EventMetaDataJson.EVENT_FREQUENCY] = recurrence.frequency
        result[EventMetaDataJson.EVENT_RECURRENCE] = recurrence.id
        (recurrence as? EventRecurrence.Weekly)?.let { result[EventMetaDataJson.EVENT_ON_DAYS] = it.onDaysOfWeek }
        return result
    }
}
