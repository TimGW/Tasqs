package com.timgortworst.roomy.ui.features.event.view

import com.timgortworst.roomy.data.model.EventMetaData

interface NotificationReminder {
    fun enqueueOneTimeNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
    fun enqueuePeriodicNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
}