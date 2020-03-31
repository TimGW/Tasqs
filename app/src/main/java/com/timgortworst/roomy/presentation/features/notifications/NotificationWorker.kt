package com.timgortworst.roomy.presentation.features.notifications

import android.content.Context
import androidx.work.*
import com.timgortworst.roomy.R
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private val workManager = WorkManager.getInstance(context)

    override fun doWork() = try {
        val title = inputData.getString(NotificationWorkManager.NOTIFICATION_TITLE_KEY)
            ?: context.getString(R.string.app_name)
        val text = inputData.getString(NotificationWorkManager.NOTIFICATION_MSG_KEY)
            ?: context.getString(R.string.notification_default_msg)
        val id =
            inputData.getString(NotificationWorkManager.NOTIFICATION_ID_KEY) ?: title.plus(text)

        NotificationBuilder.triggerNotification(
            context,
            id.hashCode(),
            context.getString(R.string.notification_title, title),
            context.getString(R.string.notification_message, text)
        )

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

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }
}