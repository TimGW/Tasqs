package com.timgortworst.tasqs.presentation.features.notifications

import org.threeten.bp.ZonedDateTime

interface NotificationQueue {

    fun enqueueNotification(
        taskId: String,
        taskDateTime: ZonedDateTime,
        userName: String,
        taskDescription: String
    )

    fun removePendingNotification(taskId: String)
}