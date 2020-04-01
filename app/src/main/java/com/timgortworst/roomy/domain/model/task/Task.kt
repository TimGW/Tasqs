package com.timgortworst.roomy.domain.model.task

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Task(
    var id: String = "",
    var description: String = "",
    var metaData: TaskMetaData = TaskMetaData(),
    var user: TaskUser = TaskUser(),
    var isDoneEnabled: Boolean = false
) : Parcelable
