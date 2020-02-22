package com.timgortworst.roomy.domain.model.firestore

import com.google.firebase.firestore.PropertyName
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.model.User
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

data class EventJson(
        @JvmField @PropertyName(EVENT_ID_REF) var eventId: String? = null,
        @JvmField @PropertyName(EVENT_DESCRIPTION_REF) var description: String? = null,
        @JvmField @PropertyName(EVENT_META_DATA_REF) var metaData: EventMetaDataJson? = null,
        @JvmField @PropertyName(EVENT_USER_REF) var user: User? = null,
        @JvmField @PropertyName(EVENT_HOUSEHOLD_ID_REF) var householdId: String? = null
) {
    companion object {
        const val EVENT_ID_REF = "id"
        const val EVENT_COLLECTION_REF = "events"
        const val EVENT_USER_REF = "user"
        const val EVENT_DESCRIPTION_REF = "description"
        const val EVENT_META_DATA_REF = "meta_data"
        const val EVENT_HOUSEHOLD_ID_REF = "household_id"
    }
}
