package com.timgortworst.tasqs.presentation.features.notifications

interface Notifications {
    fun notify(id: String, notificationTitle: String, notificationText: String)
}