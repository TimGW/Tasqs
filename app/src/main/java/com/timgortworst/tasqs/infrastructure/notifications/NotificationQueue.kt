package com.timgortworst.tasqs.infrastructure.notifications

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