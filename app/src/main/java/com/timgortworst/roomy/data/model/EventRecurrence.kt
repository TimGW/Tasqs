package com.timgortworst.roomy.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.timgortworst.roomy.R
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
sealed class EventRecurrence : Parcelable {
    val id: String
        get() = when (this) {
            SingleEvent -> SINGLE_EVENT
            Daily -> DAILY_EVENT
            is Weekly -> WEEKLY_EVENT
            Monthly -> MONTHLY_EVENT
            Annually -> ANNUAL_EVENT
        }

    @get:Exclude
    val name: Int
        get() = when (this) {
            SingleEvent -> R.string.empty_string
            Daily -> R.string.day
            is Weekly -> R.string.week
            Monthly -> R.string.month
            Annually -> R.string.year
        }

    @get:Exclude
    val pluralName: Int
        get() = when (this) {
            SingleEvent -> R.string.empty_string
            Daily -> R.string.days
            is Weekly -> R.string.weeks
            Monthly -> R.string.months
            Annually -> R.string.years
        }

    var frequency: Int = 1

    @Parcelize object SingleEvent : EventRecurrence()
    @Parcelize object Daily : EventRecurrence()
    @Parcelize data class Weekly(val onDaysOfWeek: List<Int>? = null) : EventRecurrence()
    @Parcelize object Monthly : EventRecurrence()
    @Parcelize object Annually : EventRecurrence()

    companion object {
       const val SINGLE_EVENT = "SingleEvent"
       const val DAILY_EVENT = "DailyEvent"
       const val WEEKLY_EVENT = "WeeklyEvent"
       const val MONTHLY_EVENT = "MonthlyEvent"
       const val ANNUAL_EVENT = "AnnualEvent"
    }
}

