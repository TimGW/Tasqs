package com.timgortworst.roomy.data.model

import android.os.Parcelable
import com.timgortworst.roomy.R
import kotlinx.android.parcel.Parcelize

sealed class EventRecurrence : Parcelable {
    val name: Int
        get() = when (this) {
            SingleEvent -> R.string.days
            is Daily -> R.string.days
            is Weekly -> R.string.weeks
            is Monthly -> R.string.months
            is Annually -> R.string.years
        }

    @Parcelize
    object SingleEvent : EventRecurrence()

    @Parcelize
    data class Daily(val everyXDays: Int? = null) : EventRecurrence()

    @Parcelize
    data class Weekly(val everyXWeeks: Int? = null, val onDaysOfWeek: List<Int>? = null) : EventRecurrence()

    @Parcelize
    data class Monthly(val everyXMonths: Int? = null) : EventRecurrence()

    @Parcelize
    data class Annually(val everyXYears: Int? = null) : EventRecurrence()
}

