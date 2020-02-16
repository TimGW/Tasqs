package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class EventMetaData(
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var recurrence: EventRecurrence = EventRecurrence.SingleEvent
) : Parcelable