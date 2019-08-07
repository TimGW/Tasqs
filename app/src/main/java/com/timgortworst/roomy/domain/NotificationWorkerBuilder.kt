package com.timgortworst.roomy.domain

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.EventMetaData
import java.util.concurrent.TimeUnit


class NotificationWorkerBuilder(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun enqueueOneTimeNotification(eventId: String,
                                   eventMetaData: EventMetaData,
                                   userName: String,
                                   categoryName: String) {
        removePendingNotificationReminder(eventId)

        workManager.enqueue(OneTimeWorkRequest.Builder(ReminderNotificationWorker::class.java)
                .setInitialDelay(calculateInitialDelay(eventMetaData.repeatStartDate), TimeUnit.MILLISECONDS)
                .addTag(eventId)
                .setInputData(buildInputData(userName, categoryName))
                .build())
    }

    fun enqueuePeriodicNotification(eventId: String,
                                    eventMetaData: EventMetaData,
                                    userName: String,
                                    categoryName: String) {
        removePendingNotificationReminder(eventId)

        workManager.enqueue(PeriodicWorkRequest.Builder(
                ReminderNotificationWorker::class.java,
                eventMetaData.repeatInterval.interval,
                TimeUnit.SECONDS)
                .setInitialDelay(calculateInitialDelay(eventMetaData.repeatStartDate), TimeUnit.MILLISECONDS)
                .addTag(eventId)
                .setInputData(buildInputData(userName, categoryName))
                .build())
    }

    private fun buildInputData(userName: String, categoryName: String): Data {
        val title = context.getString(R.string.notification_title, userName)
        val msg = context.getString(R.string.notification_message, categoryName)
        return Data.Builder()
                .putString(ReminderNotificationWorker.NOTIFICATION_TITLE_KEY, title)
                .putString(ReminderNotificationWorker.NOTIFICATION_MSG_KEY, msg)
                .build()
    }

    fun removePendingNotificationReminder(eventId: String) {
        workManager.cancelAllWorkByTag(eventId)
    }

    private fun calculateInitialDelay(nextOccurrence: Long) = nextOccurrence - System.currentTimeMillis()
}