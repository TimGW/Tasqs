package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime

@Parcelize
@IgnoreExtraProperties
data class TaskMetaData(
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var recurrence: TaskRecurrence = TaskRecurrence.SingleTask(1)
) : Parcelable