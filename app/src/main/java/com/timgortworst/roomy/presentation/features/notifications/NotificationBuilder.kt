package com.timgortworst.roomy.presentation.features.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.features.main.MainActivity

object NotificationBuilder {
    private const val CHANNEL_ID = "channel_01"
    private const val CHANNEL_DESC = "channel for notifications to remind users to perform their tasks"
    private const val NOTIFICATION_GROUP_KEY = "GROUP_1"
    private const val NOTIFICATION_GROUP_ID = 1

    fun triggerNotification(context: Context, id: Int, userName: String, taskDescription: String) {
        createNotificationChannelIfRequired(
            context
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationTitle = context.getString(R.string.notification_title, userName)
        val notificationText = context.getString(R.string.notification_message, taskDescription)

        with(NotificationManagerCompat.from(context)) {
            notify(id, buildNotification(
                context,
                notificationTitle,
                notificationText,
                pendingIntent
            ).build())
            notify(
                NOTIFICATION_GROUP_ID,
                buildSummaryNotification(
                    context,
                    notificationTitle,
                    notificationText
                ).build()
            )
        }
    }

    private fun buildSummaryNotification(
        context: Context,
        title: String,
        text: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setGroupSummary(true)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
    }

    private fun buildNotification(
        context: Context,
        notificationTitle: String,
        notificationMessage: String,
        notificationPendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context,
        CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_home)
        .setContentTitle(notificationTitle)
        .setContentText(notificationMessage)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
        .setGroup(NOTIFICATION_GROUP_KEY)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun createNotificationChannelIfRequired(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description =
                        CHANNEL_DESC
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}