package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class User(
        @JvmField @PropertyName(USER_ID_REF) var userId: String = "",
        @JvmField @PropertyName(USER_NAME_REF) var name: String = "",
        @JvmField @PropertyName(USER_EMAIL_REF) var email: String = "",
        @JvmField @PropertyName(USER_ROLE_REF) var role: String = Role.ADMIN.name,
        @JvmField @PropertyName(USER_HOUSEHOLD_ID_REF) var householdId: String = ""
) : Parcelable {

    companion object {
        const val USER_COLLECTION_REF = "users"
        const val USER_ID_REF = "id"
        const val USER_NAME_REF = "name"
        const val USER_EMAIL_REF = "email"
        const val USER_ROLE_REF = "role"
        const val USER_HOUSEHOLD_ID_REF = "household_id"
    }
}

