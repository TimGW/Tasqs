package com.timgortworst.roomy.ui.features.event.view

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.ui.base.view.PageStateListener

interface EventListView : PageStateListener {
    fun presentAddedEvent(agendaEvent: Event)
    fun presentEditedEvent(agendaEvent: Event)
    fun presentDeletedEvent(agendaEvent: Event)
    fun presentEmptyView(isVisible: Boolean)
    fun removePendingNotificationReminder(eventId: String)
    fun enqueueOneTimeNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
    fun enqueuePeriodicNotification(eventId: String, eventMetaData: EventMetaData, categoryName: String, userName: String)
}