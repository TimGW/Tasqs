package com.timgortworst.tasqs.presentation.features.notifications

import android.content.Context
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.timgortworst.tasqs.R
import org.threeten.bp.Duration
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
        val id = tags.first().toString()

        NotificationBuilder.triggerNotification(
            context,
            id,
            context.getString(R.string.notification_title, title),
            context.getString(R.string.notification_message, text)
        )

        setTomorrowReminder(id)

        Result.success()
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Result.failure()
    }

    private fun setTomorrowReminder(
        id: String
    ) {
        val now = ZonedDateTime.now()
        val nextReminder = now.plusDays(1)
        val initialDelay = Duration.between(now, nextReminder).toMillis()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(id)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }
}