package com.timgortworst.tasqs.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.timgortworst.tasqs.BuildConfig
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.usecase.user.AddTokenUseCaseImpl
import com.timgortworst.tasqs.presentation.features.notifications.NotificationQueue
import com.timgortworst.tasqs.presentation.features.notifications.Notifications
import com.timgortworst.tasqs.presentation.usecase.user.AddTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class FirebaseMsgService : FirebaseMessagingService(), KoinComponent {
    private val notificationQueue: NotificationQueue by inject()
    private val addTokenUseCase: AddTokenUseCase by inject()
    private val notifications: Notifications by inject()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onMessageReceived(rm: RemoteMessage) {
        val data: Map<String, String> = if (rm.data.isNotEmpty()) rm.data else return
        val taskId = data["TASK_ID"].toString()

        if (data["IS_DELETED"].toString().toBoolean()) {
            notificationQueue.removePendingNotification(taskId)
            if (BuildConfig.DEBUG) notifications.notify(taskId, "Notification removed for", taskId)
        } else {
            val taskDateTime: Long = data["TASK_START_DATE"].toString().toLong()
            val taskTimeZone: String = data["TASK_TIMEZONE"].toString()
            val userName = data["USER_NAME"].toString()
            val taskDescription = data["TASK_DESCRIPTION"].toString()

            setPendingNotification(
                taskId,
                taskDateTime,
                taskTimeZone,
                userName,
                taskDescription
            )
        }

    }

    private fun setPendingNotification(
        taskId: String,
        taskDateTime: Long,
        taskTimeZone: String,
        userName: String,
        taskDescription: String
    ) {
        val zonedDateTime = Instant
            .ofEpochMilli(taskDateTime)
            .atZone(ZoneId.of(taskTimeZone))

        val notificationTitle = getString(R.string.notification_title, userName)
        val notificationText = getString(R.string.notification_message, taskDescription)
        val debugNotificationText =
            "username: $userName\n" +
                    "date: ${zonedDateTime.dayOfMonth}-${zonedDateTime.monthValue}-${zonedDateTime.year} " +
                    "time: ${zonedDateTime.hour}:${zonedDateTime.minute} " +
                    "zone: ${zonedDateTime.zone}"

        if (zonedDateTime.isBefore(ZonedDateTime.now())) {
            notifications.notify(taskId, notificationTitle, notificationText)
        } else {
            notificationQueue.enqueueNotification(taskId, zonedDateTime, notificationTitle, notificationText)

            if (BuildConfig.DEBUG) notifications.notify(taskId, "Notification set for", debugNotificationText)
        }
    }

    override fun onNewToken(token: String) {
        serviceScope.launch {
            addTokenUseCase.execute(AddTokenUseCaseImpl.Params(token))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}