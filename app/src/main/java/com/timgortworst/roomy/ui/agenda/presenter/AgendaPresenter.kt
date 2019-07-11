package com.timgortworst.roomy.ui.agenda.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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

        events.forEach {

            if (it.isDone) {
                // event is gemarkeerd als klaar
                if (it.eventMetaData.repeatStartDate.isTimeStampInPast()) {
                    // event is in het verleden
                    if (it.eventMetaData.repeatInterval == EventMetaData.RepeatingInterval.SINGLE_EVENT) {
                        // event is Single event dus verwijder
                        events.remove(it)
                        repository.removeAgendaEvent(it.agendaId)
                    } else {
                        // event is repeating dus update het met de nieuwe tijden
                        updateEventMetaData(it)
                    }
                } else {
                    // event is klaar en in de toekomst,  update next occurance
                    updateEventMetaData(it)
                }
            } else {
                // event is nog niet klaar
                if (it.eventMetaData.repeatStartDate.isTimeStampInPast()) {
                    // event is in het verleden
                    // todo send reminder
                }
            }
        }

        view.presentEvents(events)
    }

    suspend fun updateEventMetaData(event: Event) {
        val nextDate = event.eventMetaData.repeatStartDate + (event.eventMetaData.repeatInterval.interval * 1000)
        val eventMetaData = EventMetaData(
                repeatStartDate = nextDate,
                repeatInterval = event.eventMetaData.repeatInterval)

        event.eventMetaData = eventMetaData

        // reset done to false
        repository.updateAgendaEvent(event.agendaId, eventMetaData = eventMetaData, isEventDone = false)
    }
}
