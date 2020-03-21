package com.timgortworst.roomy.domain.utils

import android.content.Context
import androidx.work.*
import com.timgortworst.roomy.R
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotificationWorkManager(private val context: Context) {
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
        removePendingNotificationReminder(taskId)

        val delay = max(0L, Duration.between(ZonedDateTime.now(), taskDateTime).toMillis())
        val inputData = buildInputData(taskId, userName, taskDescription)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(taskId)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(workRequest)
    }

    fun removePendingNotificationReminder(taskId: String) {
        workManager.cancelAllWorkByTag(taskId)
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

    private inner class NotificationWorker(
        params: WorkerParameters
    ) : Worker(context, params) {

        override fun doWork() = try {
            val title = inputData.getString(NOTIFICATION_TITLE_KEY)
                ?: context.getString(R.string.app_name)
            val text = inputData.getString(NOTIFICATION_MSG_KEY)
                ?: context.getString(R.string.notification_default_msg)
            val id = inputData.getString(NOTIFICATION_ID_KEY) ?: title.plus(text)

            NotificationBuilder.triggerNotification(context, id.hashCode(), title, text)

            setTomorrowReminder(id)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

        private fun setTomorrowReminder(
            id: String
        ) {
            val now = ZonedDateTime.now()
            val tomorrowNoon = now.plusDays(1).with(LocalTime.NOON)
            val initialDelay = Duration.between(now, tomorrowNoon).toMillis()

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            workManager.enqueue(
                OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .addTag(id)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()
            )
        }
    }
}