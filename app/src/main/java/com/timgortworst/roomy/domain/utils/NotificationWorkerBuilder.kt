package com.timgortworst.roomy.domain.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.model.EventRecurrence
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotificationWorkerBuilder(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun enqueueNotification(eventId: String,
                            eventMetaData: EventMetaData,
                            userName: String,
                            categoryName: String) {
        removePendingNotificationReminder(eventId)

        val delay = max(0L, Duration.between(ZonedDateTime.now(), eventMetaData.startDateTime).toMillis())
        val inputData = buildInputData(eventId, userName, categoryName, eventMetaData.recurrence)
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

        val workRequest = OneTimeWorkRequest.Builder(ReminderNotificationWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(eventId)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

        workManager.enqueue(workRequest)
    }

    private fun buildInputData(eventId: String,
                               userName: String,
                               categoryName: String,
                               eventRecurrence: EventRecurrence): Data {

        val title = context.getString(R.string.notification_title, userName)
        val msg = context.getString(R.string.notification_message, categoryName)
        val dataBuilder = Data.Builder()
                .putString(NOTIFICATION_ID_KEY, eventId)
                .putString(NOTIFICATION_TITLE_KEY, title)
                .putString(NOTIFICATION_MSG_KEY, msg)
                .putLong(WM_FREQ_KEY, eventRecurrence.frequency.toLong())
                .putString(WM_RECURRENCE_KEY, eventRecurrence.id)

        (eventRecurrence as? EventRecurrence.Weekly)?.onDaysOfWeek?.let {
            dataBuilder.putIntArray(WM_WEEKDAYS_KEY, it.toIntArray())
        }

        return dataBuilder.build()
    }

    fun removePendingNotificationReminder(eventId: String) {
        workManager.cancelAllWorkByTag(eventId)
    }

    companion object {
        const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
        const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
        const val NOTIFICATION_MSG_KEY = "NOTIFICATION_MSG_KEY"
        const val WM_FREQ_KEY = "WM_FREQ_KEY"
        const val WM_RECURRENCE_KEY = "WM_RECURRENCE_KEY"
        const val WM_WEEKDAYS_KEY = "WM_WEEKDAYS_KEY"
    }
}