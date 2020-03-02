package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
@IgnoreExtraProperties
data class EventMetaData(
        var startDateTime: ZonedDateTime = ZonedDateTime.now(), // todo fetch right time
        var recurrence: EventRecurrence = EventRecurrence.SingleEvent(1)
) : Parcelable