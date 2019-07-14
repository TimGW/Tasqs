package com.timgortworst.roomy.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.timgortworst.roomy.R
import com.timgortworst.roomy.RoomyApp
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class EventMetaData(
    var repeatStartDate: Long = 0L, // unix timestamp date without time
    var repeatInterval: RepeatingInterval = RepeatingInterval.SINGLE_EVENT // number of seconds between intervals
) : Parcelable {

    enum class RepeatingInterval(val title : Int, val interval: Long) {
        SINGLE_EVENT(R.string.repeating_interval_single_event, 1L),
        DAILY(R.string.repeating_interval_daily_event, 86400L),
        WEEKLY(R.string.repeating_interval_weekly_event, 604800L),
        MONTHLY(R.string.repeating_interval_monthly_event, 2628000L),
        ANNUALLY(R.string.repeating_interval_annually_event, 31536000L);

        override fun toString(): String {
            return RoomyApp.applicationContext().getString(title)
        }
    }
}
