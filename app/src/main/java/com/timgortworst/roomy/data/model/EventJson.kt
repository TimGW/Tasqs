package com.timgortworst.roomy.data.model

import com.timgortworst.roomy.domain.utils.toZonedDateTime

data class EventJson(
        var eventId: String = "",
        var eventMetaData: EventMetaDataJson = EventMetaDataJson(),
        var eventCategory: Category = Category(),
        var user: User = User(),
        var householdId: String = ""
) {
    fun toEvent() = Event(
            eventId,
            EventMetaData(eventMetaData.eventDateTime.toZonedDateTime(eventMetaData.eventTimeZone)),
            eventCategory,
            user,
            householdId)


}
