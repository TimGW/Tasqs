package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.ui.main.view.PageStateListener

interface EventListView : PageStateListener, NotificationReminder {
    fun presentAddedEvent(agendaEvent: Event)
    fun presentEditedEvent(agendaEvent: Event)
    fun presentDeletedEvent(agendaEvent: Event)
    fun presentEmptyView(isVisible: Boolean)
}