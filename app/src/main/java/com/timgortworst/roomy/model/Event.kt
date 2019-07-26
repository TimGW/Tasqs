package com.timgortworst.roomy.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class Event(
        var eventId: String = "",
        var eventMetaData: EventMetaData = EventMetaData(),
        var eventCategory: Category = Category(),
        var user: User = User(),
        var householdId: String = ""
) : Parcelable
