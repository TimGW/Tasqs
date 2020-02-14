package com.timgortworst.roomy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class EventInterval : Parcelable {
    @Parcelize object SingleEvent : EventInterval()
    @Parcelize data class Daily(val everyXDays: Int) : EventInterval()
    @Parcelize data class Weekly(val everyXWeeks: Int, val onDaysOfWeek: List<Int>) : EventInterval()
    @Parcelize data class Monthly(val everyXMonths: Int) : EventInterval()
    @Parcelize data class Annually(val everyXYears: Int) : EventInterval()
}

