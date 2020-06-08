package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class Household(
        @JvmField @PropertyName(HOUSEHOLD_ID_REF) var householdId: String = ""
) : Parcelable {

    companion object {
        const val HOUSEHOLD_COLLECTION_REF = "households"
        const val HOUSEHOLD_ID_REF = "id"
    }
}