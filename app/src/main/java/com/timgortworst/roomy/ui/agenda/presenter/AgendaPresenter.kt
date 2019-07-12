package com.timgortworst.roomy.ui.agenda.presenter

import android.widget.Filter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.view.AgendaView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.utils.isTimeStampInPast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgendaPresenter(
        val view: AgendaView,
        val agendaRepository: AgendaRepository,
        val userRepository: UserRepository
) : AgendaRepository.AgendaEventListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    override fun eventAdded(agendaEvent: Event) {
        if (agendaEvent.eventMetaData.repeatStartDate.isTimeStampInPast()) {
            // event is in het verleden
            // todo send reminder
        }
        view.presentAddedEvent(agendaEvent)
    }

    override fun eventModified(agendaEvent: Event) {
        view.presentEditedEvent(agendaEvent)
    }

    override fun eventDeleted(agendaEvent: Event) {
        view.presentDeletedEvent(agendaEvent)
    }

    fun detachEventListener() {
        agendaRepository.detachEventListener()
    }

    fun listenToEvents() {
        agendaRepository.listenToEvents(this)
    }

    fun filterMe(filter: Filter) {
        filter.filter(userRepository.getCurrentUserId())
    }

    fun markEventAsCompleted(event: Event) = scope.launch {
        if (event.eventMetaData.repeatInterval == EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            agendaRepository.removeAgendaEvent(event.agendaId)
            return@launch
        }

        val nextOccurance = calcNextOccurance(event)
        updateEventMetaData(event, nextOccurance)
    }

    private fun calcNextOccurance(event: Event): Long {
        return if (event.eventMetaData.repeatStartDate.isTimeStampInPast()) {
            System.currentTimeMillis() + (event.eventMetaData.repeatInterval.interval * 1000)
        } else {
            event.eventMetaData.repeatStartDate + (event.eventMetaData.repeatInterval.interval * 1000)
        }
    }

    suspend fun updateEventMetaData(event: Event, nextOccurance: Long) {
        val eventMetaData = EventMetaData(
                repeatStartDate = nextOccurance,
                repeatInterval = event.eventMetaData.repeatInterval
        )

        event.eventMetaData = eventMetaData

        // reset done to false
        agendaRepository.updateAgendaEvent(event.agendaId, eventMetaData = eventMetaData, isEventDone = false)
    }
}
