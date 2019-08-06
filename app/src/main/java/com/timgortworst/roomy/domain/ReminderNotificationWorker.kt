package com.timgortworst.roomy.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.timgortworst.roomy.R
import java.text.SimpleDateFormat
import java.util.*


class ReminderNotificationWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork() = try {
        val notificationMessage = inputData.getString(NOTIFICATION_MSG_KEY) ?: context.getString(R.string.default_notification_msg)
        triggerNotification(notificationMessage)
        Result.success()
    } catch (e: Exception) {
        Result.failure()
    }

    private fun triggerNotification(notificationMessage: String) {
        createNotificationChannelIfRequired()

        with(NotificationManagerCompat.from(context)) {
            notify(createNotificationID(), buildNotification(notificationMessage).build())
        }
    }

    private fun buildNotification(notificationMessage: String) = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.accent_home_icon)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(notificationMessage)
            .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(notificationMessage))
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

    private fun createNotificationID(): Int {
        val now = Date()
        return Integer.parseInt(SimpleDateFormat("ddHHmmssSS", Locale.getDefault()).format(now))
    }

    companion object {
        const val NOTIFICATION_MSG_KEY = "NOTIFICATION_MSG_KEY"
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_DESC = "channel for notifications to remind users to perform their tasks"
    }
}