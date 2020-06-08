package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class TaskUser(
    @JvmField @PropertyName(USER_ID_REF) var userId: String = "",
    @JvmField @PropertyName(USER_NAME_REF) var name: String = ""
) : Parcelable {

    companion object {
        const val USER_ID_REF = "id"
        const val USER_NAME_REF = "name"
    }
}

