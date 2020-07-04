package com.timgortworst.tasqs.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Household(val householdId: String = "") : Parcelable
