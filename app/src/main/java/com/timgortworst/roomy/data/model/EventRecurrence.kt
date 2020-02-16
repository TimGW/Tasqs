package com.timgortworst.roomy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class EventRecurrence : Parcelable {
    @Parcelize object SingleEvent : EventRecurrence()
    @Parcelize data class Daily(val everyXDays: Int?) : EventRecurrence()
    @Parcelize data class Weekly(val everyXWeeks: Int?, val onDaysOfWeek: List<Int>?) : EventRecurrence()
    @Parcelize data class Monthly(val everyXMonths: Int?) : EventRecurrence()
    @Parcelize data class Annually(val everyXYears: Int?) : EventRecurrence()
}

