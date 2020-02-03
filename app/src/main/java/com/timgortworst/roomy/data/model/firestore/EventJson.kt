package com.timgortworst.roomy.data.model.firestore

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

data class EventJson(
        var eventId: String = "",
        var description: String = "",
        var eventMetaData: EventMetaDataJson = EventMetaDataJson(),
        var user: User = User(),
        var householdId: String = ""
) {
    fun toEvent() = Event(
            eventId,
            description,
            EventMetaData(toZonedDateTime(eventMetaData.eventTimeZone), eventMetaData.eventInterval),
            user,
            householdId)

    private fun toZonedDateTime(zoneId: String) = Instant.ofEpochMilli(eventMetaData.eventDateTime).atZone(ZoneId.of(zoneId))
}
