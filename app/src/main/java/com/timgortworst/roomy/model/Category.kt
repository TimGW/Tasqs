package com.timgortworst.roomy.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class Category(
    var categoryId: String = "",
    var name: String = "",
    var description: String = ""
) : Parcelable
