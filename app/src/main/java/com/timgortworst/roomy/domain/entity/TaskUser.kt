package com.timgortworst.roomy.domain.entity

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class TaskUser(
    @JvmField @PropertyName(USER_ID_REF) var userId: String = "",
    @JvmField @PropertyName(USER_NAME_REF) var name: String = ""
) : Parcelable {

    companion object {
        const val USER_ID_REF = "id"
        const val USER_NAME_REF = "name"
    }
}

