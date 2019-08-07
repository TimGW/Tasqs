package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.model.EventMetaData

interface NotificationReminder {
    fun enqueueOneTimeNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
    fun enqueuePeriodicNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
}