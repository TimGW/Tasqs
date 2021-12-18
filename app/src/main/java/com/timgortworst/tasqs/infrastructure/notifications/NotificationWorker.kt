package com.timgortworst.tasqs.infrastructure.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.timgortworst.tasqs.R
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params), KoinComponent {
    private val notifications: Notifications by inject()

    override fun doWork(): Result = try {
        val id = inputData.getString(NotificationQueueImpl.NOTIFICATION_ID_KEY)
        val title = inputData.getString(NotificationQueueImpl.NOTIFICATION_TITLE_KEY)
            ?: context.getString(R.string.app_name)
        val text = inputData.getString(NotificationQueueImpl.NOTIFICATION_MSG_KEY)
            ?: context.getString(R.string.notification_default_msg)

        if (id != null) {
            notifications.notify(
                id,
                context.getString(R.string.notification_title, title),
                context.getString(R.string.notification_message, text)
            )
            Result.success()
        } else {
            Result.failure()
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Result.failure()
    }
}