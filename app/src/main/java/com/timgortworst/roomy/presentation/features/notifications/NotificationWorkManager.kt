package com.timgortworst.roomy.presentation.features.notifications

import android.content.Context
import androidx.work.*
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotificationWorkManager(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
        const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
        const val NOTIFICATION_MSG_KEY = "NOTIFICATION_MSG_KEY"
    }

    fun enqueueNotification(
        taskId: String,
        taskDateTime: ZonedDateTime,
        userName: String,
        taskDescription: String
    ) {
        val delay = max(0L, Duration.between(ZonedDateTime.now(), taskDateTime).toMillis())
        val inputData = buildInputData(taskId, userName, taskDescription)

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(taskId)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(taskId, ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun removePendingNotificationReminder(taskId: String) {
        workManager.cancelUniqueWork(taskId)
    }

    private fun buildInputData(
        taskId: String,
        userName: String,
        taskDescription: String
    ) = Data.Builder()
        .putString(NOTIFICATION_ID_KEY, taskId)
        .putString(NOTIFICATION_TITLE_KEY, userName)
        .putString(NOTIFICATION_MSG_KEY, taskDescription)
        .build()
}