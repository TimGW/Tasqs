package com.timgortworst.tasqs.infrastructure.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.presentation.features.task.view.TaskInfoActivity

class NotificationsImpl(
    private val context: Context
) : Notifications {

    override fun notify(id: String, notificationTitle: String, notificationText: String) {
        val title = context.getString(R.string.notification_title, notificationTitle)
        val text = context.getString(R.string.notification_message, notificationText)

        createNotificationChannelIfRequired(context)
        val requestCode = id.hashCode()

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            TaskInfoActivity.intentBuilder(context, id),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        with(NotificationManagerCompat.from(context)) {
            notify(requestCode, buildNotification(context, title, text, pendingIntent))
            notify(NOTIFICATION_GROUP_ID, buildSummaryNotification(context, title, text))
        }
    }

    private fun buildSummaryNotification(
        context: Context,
        title: String,
        text: String
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_home)
        .setContentTitle(title)
        .setContentText(text)
        .setAutoCancel(true)
        .setGroupSummary(true)
        .setGroup(NOTIFICATION_GROUP_KEY)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text))
        .build()

    private fun buildNotification(
        context: Context,
        notificationTitle: String,
        notificationMessage: String,
        notificationPendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_home)
        .setContentTitle(notificationTitle)
        .setContentText(notificationMessage)
        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
        .setGroup(NOTIFICATION_GROUP_KEY)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    private fun createNotificationChannelIfRequired(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description =
                        CHANNEL_DESC
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_DESC =
            "channel for notifications to remind users to perform their tasks"
        private const val NOTIFICATION_GROUP_KEY = "GROUP_1"
        private const val NOTIFICATION_GROUP_ID = 1
    }

}