package com.timgortworst.roomy.ui.agenda.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.agenda.ui.AgendaView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.utils.isTimeStampInPast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AgendaPresenter(
    val view: AgendaView,
    val repository: AgendaRepository
) : AgendaRepository.AgendaEventListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

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

    fun getEvents() = scope.launch {
        val events = repository.getAgendaEvents() ?: return@launch

        for (event in events) {
            if (event.isDone) {
                events.remove(event)

                repository.removeAgendaEvent(event.agendaId)
            } else {

                if (event.eventMetaData.repeatInterval != EventMetaData.RepeatingInterval.SINGLE_EVENT) {

                    // if date is in past, update with next interval
                    if (event.eventMetaData.repeatStartDate.isTimeStampInPast()) {
                        val newStartDate =
                            event.eventMetaData.repeatStartDate + (event.eventMetaData.repeatInterval.interval * 1000)
                        val eventMetaData = EventMetaData(
                            repeatStartDate = newStartDate,
                            repeatInterval = event.eventMetaData.repeatInterval
                        )

                        event.eventMetaData = eventMetaData

                        repository.updateAgendaEvent(event.agendaId, eventMetaData = eventMetaData)
                    }
                } else {

                    // if single event is in past, remove from list
                    if (event.eventMetaData.repeatStartDate.isTimeStampInPast()) {
                        events.remove(event)

                        repository.removeAgendaEvent(event.agendaId)
                    }
                }
            }
        }
        view.presentEvents(events)
    }
}
