package com.timgortworst.roomy.domain.entity

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.timgortworst.roomy.domain.entity.TaskMetaData
import com.timgortworst.roomy.domain.entity.TaskUser
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
