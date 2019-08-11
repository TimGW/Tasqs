package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class Household(
        var householdId: String = "",
        var userIdBlackList: MutableList<String> = mutableListOf()
) : Parcelable
