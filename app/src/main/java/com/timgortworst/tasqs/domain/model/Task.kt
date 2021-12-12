package com.timgortworst.tasqs.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit

@Keep
@Parcelize
data class Task(
    var id: String? = null,
    var description: String = "",
    var metaData: MetaData = MetaData(),
    var user: User? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class MetaData(
        var startDateTime: ZonedDateTime = ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1),
            ZoneId.systemDefault()
        ),
        var recurrence: TaskRecurrence = TaskRecurrence.SingleTask(1)
    ) : Parcelable

    @Keep
    @Parcelize
    data class User(val userId: String, val name: String) : Parcelable
}
