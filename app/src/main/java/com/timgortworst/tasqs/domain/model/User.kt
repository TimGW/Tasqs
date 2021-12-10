package com.timgortworst.tasqs.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class User(
    var userId: String = FirebaseAuth.getInstance().currentUser?.uid!!,
    var name: String = "",
    var email: String = "",
    var isAdmin: Boolean = false,
    var householdId: String
) : Parcelable


