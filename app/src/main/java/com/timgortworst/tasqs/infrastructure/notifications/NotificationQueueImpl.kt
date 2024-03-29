package com.timgortworst.tasqs.infrastructure.notifications

import android.content.Context
import androidx.work.*
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotificationQueueImpl(
    context: Context
) : NotificationQueue {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
        const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
        const val NOTIFICATION_MSG_KEY = "NOTIFICATION_MSG_KEY"
    }

    override fun enqueueNotification(
        taskId: String,
        taskDateTime: ZonedDateTime,
        userName: String,
        taskDescription: String
    ) {
        val delay = max(0L, Duration.between(ZonedDateTime.now(), taskDateTime).toMillis())
        val inputData = buildInputData(taskId, userName, taskDescription)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(taskId, ExistingWorkPolicy.REPLACE, workRequest)
    }

    override fun removePendingNotification(taskId: String) {
        workManager.cancelUniqueWork(taskId)
    }

    override fun removeAllPendingNotifications() {
        workManager.cancelAllWork()
    }

    private fun buildInputData(
        id: String,
        userName: String,
        taskDescription: String
    ) = Data.Builder()
        .putString(NOTIFICATION_ID_KEY, id)
        .putString(NOTIFICATION_TITLE_KEY, userName)
        .putString(NOTIFICATION_MSG_KEY, taskDescription)
        .build()
}