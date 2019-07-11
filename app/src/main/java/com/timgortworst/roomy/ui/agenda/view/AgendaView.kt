package com.timgortworst.roomy.ui.agenda.view

import com.timgortworst.roomy.model.Event

interface AgendaView {
    fun presentAddedEvent(agendaEvent: Event)
    fun presentEditedEvent(agendaEvent: Event)
    fun presentDeletedEvent(agendaEvent: Event)
    fun presentEvents(events: MutableList<Event>)
}