package com.timgortworst.roomy.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class Event(
    var agendaId: String = "",
    var eventCategory: Category = Category(),
    var user: User = User(),
    var eventMetaData: EventMetaData = EventMetaData(),
    var isDone: Boolean = false
) : Parcelable {
}
