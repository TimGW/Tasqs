package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Event(
        var eventId: String = "",
        var description: String = "",
        var metaData: EventMetaData = EventMetaData(),
        var user: User = User(),
        var householdId: String = ""
) : Parcelable
