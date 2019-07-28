package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.repository.DataLoadingListener

interface EventListView : DataLoadingListener {
    fun presentAddedEvent(agendaEvent: Event)
    fun presentEditedEvent(agendaEvent: Event)
    fun presentDeletedEvent(agendaEvent: Event)
}