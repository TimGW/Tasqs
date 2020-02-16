package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class Event(
        var eventId: String = "",
        var description: String = "",
        var metaData: EventMetaData = EventMetaData(),
        var user: User = User(),
        var householdId: String = ""
) : Parcelable
