package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
@Keep
data class Task(
    var id: String = "",
    var description: String = "",
    var metaData: TaskMetaData = TaskMetaData(),
    var user: TaskUser = TaskUser()
) : Parcelable
