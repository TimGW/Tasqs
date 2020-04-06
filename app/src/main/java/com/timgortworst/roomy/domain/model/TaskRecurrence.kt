package com.timgortworst.roomy.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.timgortworst.roomy.R
import kotlinx.android.parcel.Parcelize

sealed class TaskRecurrence : Parcelable {
    abstract var frequency: Int

    val id: String
        get() = when (this) {
            is SingleTask -> SINGLE_TASK
            is Daily -> DAILY_TASK
            is Weekly -> WEEKLY_TASK
            is Monthly -> MONTHLY_TASK
            is Annually -> ANNUAL_TASK
        }

    @get:Exclude
    val name: Int
        get() = when (this) {
            is SingleTask -> R.string.empty_string
            is Daily -> R.string.day
            is Weekly -> R.string.week
            is Monthly -> R.string.month
            is Annually -> R.string.year
        }

    @get:Exclude
    val pluralName: Int
        get() = when (this) {
            is SingleTask -> R.string.empty_string
            is Daily -> R.string.days
            is Weekly -> R.string.weeks
            is Monthly -> R.string.months
            is Annually -> R.string.years
        }


    @Parcelize
    class SingleTask(override var frequency: Int = 1) : TaskRecurrence()

    @Parcelize
    class Daily(override var frequency: Int = 1) : TaskRecurrence()

    @Parcelize
    class Weekly(override var frequency: Int = 1, val onDaysOfWeek: List<Int> = emptyList()) : TaskRecurrence()

    @Parcelize
    class Monthly(override var frequency: Int = 1) : TaskRecurrence()

    @Parcelize
    class Annually(override var frequency: Int = 1) : TaskRecurrence()

    companion object {
        const val SINGLE_TASK = "SingleTask"
        const val DAILY_TASK = "DailyTask"
        const val WEEKLY_TASK = "WeeklyTask"
        const val MONTHLY_TASK = "MonthlyTask"
        const val ANNUAL_TASK = "AnnualTask"
    }
}

