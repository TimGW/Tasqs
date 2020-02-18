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

    var frequency: Int = 1
//        get() = when (this) {
//            SingleEvent -> R.string.days
//            is Daily -> R.string.days
//            is Weekly -> R.string.weeks
//            is Monthly -> R.string.months
//            is Annually -> R.string.years
//        }

    @Parcelize object SingleEvent : EventRecurrence()
    @Parcelize object Daily : EventRecurrence()
    @Parcelize data class Weekly(val onDaysOfWeek: List<Int>? = null) : EventRecurrence()
    @Parcelize object Monthly : EventRecurrence()
    @Parcelize object Annually : EventRecurrence()
}

