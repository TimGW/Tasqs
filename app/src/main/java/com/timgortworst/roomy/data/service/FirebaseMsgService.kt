package com.timgortworst.roomy.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.features.notifications.NotificationBuilder
import com.timgortworst.roomy.presentation.features.notifications.NotificationWorkManager
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
    private val workerNotification: NotificationWorkManager by inject()
    private val userUseCase: UserUseCase by inject()
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            with(remoteMessage.data) {
                val taskId = remoteMessage.data["TASK_ID"].toString()

                if (remoteMessage.data["IS_DELETED"].toString().toBoolean()) {
                    workerNotification.removePendingNotificationReminder(taskId)
                } else {
                    val taskDateTime: Long = get("TASK_START_DATE").toString().toLong()
                    val taskTimeZone: String = get("TASK_TIMEZONE").toString()
                    val userName = get("USER_NAME").toString()
                    val taskDescription = get("TASK_DESCRIPTION").toString()

                    setPendingNotification(
                        taskId,
                        taskDateTime,
                        taskTimeZone,
                        userName,
                        taskDescription
                    )
                }
            }
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
            handleNow(taskId, notificationTitle, notificationText)
        } else {
            scheduleJob(taskId, zonedDateTime, notificationTitle, notificationText)

            if (BuildConfig.DEBUG) handleNow(taskId, "Notification set for", debugNotificationText)
        }
    }

    private fun scheduleJob(
        taskId: String,
        zonedDateTime: ZonedDateTime,
        userName: String,
        taskDescription: String
    ) {
        workerNotification.enqueueNotification(
            taskId,
            zonedDateTime,
            userName,
            taskDescription
        )
    }

    private fun handleNow(
        taskId: String,
        title: String,
        text: String
    ) {
        NotificationBuilder.triggerNotification(
            this@FirebaseMsgService,
            taskId.hashCode(),
            title,
            text
        )
    }

    override fun onNewToken(token: String) {
        serviceScope.launch {
            userUseCase.addTokenToUser(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}