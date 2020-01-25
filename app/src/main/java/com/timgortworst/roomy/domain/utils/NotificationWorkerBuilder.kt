package com.timgortworst.roomy.domain.utils

import android.content.Context
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.EventMetaData
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

        val upcomingEvent = eventMetaData.eventTimestamp
        val futureEvent = upcomingEvent.plusInterval(eventMetaData.eventInterval)

        val repeatInterval = Duration.between(upcomingEvent, futureEvent).toMillis()
        val initialDelay = max(0L, Duration.between(ZonedDateTime.now(), upcomingEvent).toMillis())

        workManager.enqueue(PeriodicWorkRequest.Builder(
                    ReminderNotificationWorker::class.java,
                    repeatInterval,
                    TimeUnit.MILLISECONDS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
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
}