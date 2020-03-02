package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Household(
        @JvmField @PropertyName(HOUSEHOLD_ID_REF) var householdId: String = "",
        @JvmField @PropertyName(HOUSEHOLD_BLACKLIST_REF) var userIdBlackList: MutableList<String> = mutableListOf()
) : Parcelable {

    companion object {
        const val HOUSEHOLD_COLLECTION_REF = "households"
        const val HOUSEHOLD_ID_REF = "id"
        const val HOUSEHOLD_BLACKLIST_REF = "user_id_blacklist"
    }
}