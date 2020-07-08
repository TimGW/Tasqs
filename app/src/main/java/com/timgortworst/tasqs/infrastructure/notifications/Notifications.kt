package com.timgortworst.tasqs.infrastructure.notifications

interface Notifications {
    fun notify(id: String, notificationTitle: String, notificationText: String)
}