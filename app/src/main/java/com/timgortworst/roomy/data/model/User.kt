package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class User(
        var userId: String = "",
        var name: String = "",
        var email: String = "",
        var role: String = Role.ADMIN.name,
        var householdId: String = ""
) : Parcelable

