package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.threeten.bp.ZonedDateTime

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class EventMetaData(
        var eventTimestamp: ZonedDateTime = ZonedDateTime.now(),
        var eventInterval: EventInterval = EventInterval.SingleEvent
) : Parcelable