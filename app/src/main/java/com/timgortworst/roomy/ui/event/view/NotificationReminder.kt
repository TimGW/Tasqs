package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User

interface NotificationReminder {
    fun setSingleNotificationReminder(workRequestTag: String, eventMetaData: EventMetaData, category: Category, user: User)
    fun setRepeatingNotificationReminder(workRequestTag: String, eventMetaData: EventMetaData, category: Category, user: User)
}