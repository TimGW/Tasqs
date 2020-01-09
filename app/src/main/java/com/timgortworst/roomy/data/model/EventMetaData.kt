package com.timgortworst.roomy.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.EventMetaData.EventInterval.SINGLE_EVENT
import com.timgortworst.roomy.presentation.base.RoomyApp
import kotlinx.android.parcel.Parcelize


@IgnoreExtraProperties
@SuppressLint("ParcelCreator")
@Parcelize
data class EventMetaData(
        var eventTimestamp: Timestamp = Timestamp.now(),
        var eventInterval: EventInterval = SINGLE_EVENT
) : Parcelable {

    enum class EventInterval(val title : Int) {
        SINGLE_EVENT(R.string.repeating_interval_single_event),
        DAILY(R.string.repeating_interval_daily_event),
        WEEKLY(R.string.repeating_interval_weekly_event),
        MONTHLY(R.string.repeating_interval_monthly_event),
        ANNUALLY(R.string.repeating_interval_annually_event);

        override fun toString(): String {
            return RoomyApp.applicationContext().getString(title)
        }
    }
}
