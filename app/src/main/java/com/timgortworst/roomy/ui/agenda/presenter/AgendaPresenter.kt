package com.timgortworst.roomy.ui.agenda.presenter

import android.text.format.DateUtils
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.agenda.ui.AgendaView
import com.timgortworst.roomy.utils.AndroidUtil
import kotlinx.coroutines.InternalCoroutinesApi


class AgendaPresenter(
        val view: AgendaView,
        val repository: AgendaRepository
) : AgendaRepository.AgendaEventListener {

    override fun eventAdded(agendaEvent: Event) {
        view.presentAddedEvent(agendaEvent)
    }

    override fun eventModified(agendaEvent: Event) {
        view.presentEditedEvent(agendaEvent)
    }

    override fun eventDeleted(agendaEvent: Event) {
        view.presentDeletedEvent(agendaEvent)
    }

    fun detachEventListener() {
        repository.detachEventListener()
    }

    fun listenToEvents() {
        repository.listenToEvents(this)
    }

    fun getEvents() {
        repository.getAgendaEvents { events ->


            for (event in events){
                if(event.isDone){
                    events.remove(event)

                    repository.removeAgendaEvent(event.agendaId)
                } else {

                    if (event.eventMetaData.repeatInterval != EventMetaData.RepeatingInterval.SINGLE_EVENT) {

                        // if date is in past, update with next interval
                        if (AndroidUtil.isInPast(event.eventMetaData.repeatStartDate)) {
                            val newStartDate = event.eventMetaData.repeatStartDate + (event.eventMetaData.repeatInterval.interval * 1000)
                            val eventMetaData = EventMetaData(repeatStartDate = newStartDate, repeatInterval = event.eventMetaData.repeatInterval)

                            event.eventMetaData = eventMetaData

                            repository.updateAgendaEvent(event.agendaId, eventMetaData = eventMetaData)
                        }
                    } else {

                        // if single event is in past, remove from list
                        if (AndroidUtil.isInPast(event.eventMetaData.repeatStartDate)) {
                            events.remove(event)

                            repository.removeAgendaEvent(event.agendaId)
                        }
                    }
                }
            }

            view.presentEvents(events)
        }
    }
}
