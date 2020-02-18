package com.timgortworst.roomy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
data class EventMetaData(
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var recurrence: EventRecurrence = EventRecurrence.SingleEvent(1)
) : Parcelable