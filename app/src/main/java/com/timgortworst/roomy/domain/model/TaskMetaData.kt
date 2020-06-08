package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Parcelize
@IgnoreExtraProperties
@Keep
data class TaskMetaData(
    var startDateTime: ZonedDateTime = ZonedDateTime.of(
        LocalDate.now(),
        LocalTime.NOON,
        ZoneId.systemDefault()
    ),
    var recurrence: TaskRecurrence = TaskRecurrence.SingleTask(
        1
    )
) : Parcelable

