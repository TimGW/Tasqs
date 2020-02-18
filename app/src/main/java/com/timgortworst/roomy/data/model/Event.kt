package com.timgortworst.roomy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
        var eventId: String = "",
        var description: String = "",
        var metaData: EventMetaData = EventMetaData(),
        var user: User = User(),
        var householdId: String = ""
) : Parcelable
