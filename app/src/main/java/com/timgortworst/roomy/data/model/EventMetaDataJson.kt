package com.timgortworst.roomy.data.model

import com.google.firebase.Timestamp
import com.timgortworst.roomy.domain.utils.toZonedDateTime

data class EventMetaDataJson(
        var eventTimestamp: Timestamp,
        var eventInterval: EventMetaData.EventInterval
)

fun EventMetaDataJson.parse(): EventMetaData {
    return EventMetaData(eventTimestamp.toZonedDateTime(), eventInterval)
}
