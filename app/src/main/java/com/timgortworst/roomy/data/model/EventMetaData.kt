package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.threeten.bp.ZonedDateTime

@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class EventMetaData(
        var eventTimestamp: ZonedDateTime = ZonedDateTime.now(),
        var eventInterval: EventInterval = EventInterval.SingleEvent
) : Parcelable {

    sealed class EventInterval : Parcelable {
        @Parcelize object SingleEvent : EventInterval()
        @Parcelize data class Daily(val everyXDays: Int) : EventInterval()
        @Parcelize data class Weekly(val everyXWeeks: Int, val onDaysOfWeek: List<Int>) : EventInterval()
        @Parcelize data class Monthly(val everyXDays: Int, val onDaysOfMonth: MonthRepeat) : EventInterval()
        @Parcelize data class Annually(val everyXYears: Int) : EventInterval()

        enum class MonthRepeat {
            DAY_OF_MONTH, // e.g. -> Every 1st of the month
            WEEKDAY_OF_MONTH; // e.g. -> Every 1st sunday of the month
        }
    }
}