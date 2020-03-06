package com.timgortworst.roomy.domain.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder.Companion.NOTIFICATION_ID_KEY
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder.Companion.NOTIFICATION_MSG_KEY
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder.Companion.NOTIFICATION_TITLE_KEY
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class ReminderNotificationWorker(
        val context: Context,
        params: WorkerParameters
) : Worker(context, params) {
    private val workManager = WorkManager.getInstance(context)

    override fun doWork() = try {
        val title = inputData.getString(NOTIFICATION_TITLE_KEY)
                ?: context.getString(R.string.app_name)
        val text = inputData.getString(NOTIFICATION_MSG_KEY)
                ?: context.getString(R.string.notification_default_msg)
        val id = inputData.getString(NOTIFICATION_ID_KEY) ?: title.plus(text)

        triggerNotification(id.hashCode(), title, text)

        setFutureReminder(id)

        Result.success()
    } catch (e: Exception) {
        Result.failure()
    }

    private fun setFutureReminder(
        id: String
    ) {
        val now = ZonedDateTime.now()
        val tomorrowNoon = now.plusDays(1).with(LocalTime.NOON)
        val initialDelay = Duration.between(now, tomorrowNoon).toMillis()

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        workManager.enqueue(OneTimeWorkRequest.Builder(ReminderNotificationWorker::class.java)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(id)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build())
    }

    private fun triggerNotification(id: Int, title: String, text: String) {
        createNotificationChannelIfRequired()
        val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        with(NotificationManagerCompat.from(context)) {
            notify(id, buildNotification(title, text, pendingIntent).build())
            notify(NOTIFICATION_GROUP_ID, buildSummaryNotification(title, text).build())
        }
    }

    private fun buildSummaryNotification(title: String, text: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle(title)
                .setContentText(text)
                .setGroupSummary(true)
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
    }

    private fun buildNotification(notificationTitle: String,
                                  notificationMessage: String,
                                  notificationPendingIntent: PendingIntent) = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun createNotificationChannelIfRequired() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = CHANNEL_DESC
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_DESC = "channel for notifications to remind users to perform their tasks"
        const val NOTIFICATION_GROUP_KEY = "GROUP_1"
        const val NOTIFICATION_GROUP_ID = 1
        const val NO_REPEATING_TASK = -1L
    }
}