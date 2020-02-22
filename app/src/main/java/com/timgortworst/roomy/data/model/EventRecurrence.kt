package com.timgortworst.roomy.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.timgortworst.roomy.R
import kotlinx.android.parcel.Parcelize

sealed class EventRecurrence : Parcelable {
    abstract var frequency: Int

    val id: String
        get() = when (this) {
            is SingleEvent -> SINGLE_EVENT
            is Daily -> DAILY_EVENT
            is Weekly -> WEEKLY_EVENT
            is Monthly -> MONTHLY_EVENT
            is Annually -> ANNUAL_EVENT
        }

    @get:Exclude
    val name: Int
        get() = when (this) {
            is SingleEvent -> R.string.empty_string
            is Daily -> R.string.day
            is Weekly -> R.string.week
            is Monthly -> R.string.month
            is Annually -> R.string.year
        }

    @get:Exclude
    val pluralName: Int
        get() = when (this) {
            is SingleEvent -> R.string.empty_string
            is Daily -> R.string.days
            is Weekly -> R.string.weeks
            is Monthly -> R.string.months
            is Annually -> R.string.years
        }


    @Parcelize
    class SingleEvent(override var frequency: Int = 1) : EventRecurrence()

    @Parcelize
    class Daily(override var frequency: Int = 1) : EventRecurrence()

    @Parcelize
    class Weekly(override var frequency: Int = 1, val onDaysOfWeek: List<Int> = emptyList()) : EventRecurrence()

    @Parcelize
    class Monthly(override var frequency: Int = 1) : EventRecurrence()

    @Parcelize
    class Annually(override var frequency: Int = 1) : EventRecurrence()

    companion object {
        const val SINGLE_EVENT = "SingleEvent"
        const val DAILY_EVENT = "DailyEvent"
        const val WEEKLY_EVENT = "WeeklyEvent"
        const val MONTHLY_EVENT = "MonthlyEvent"
        const val ANNUAL_EVENT = "AnnualEvent"
    }
}

