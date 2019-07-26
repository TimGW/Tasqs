package com.timgortworst.roomy.ui.event.presenter

import android.widget.Filter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.event.view.EventListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.utils.isTimeStampInPast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EventListPresenter @Inject constructor(
    private val view: EventListView,
    private val agendaRepository: EventRepository,
    private val userRepository: UserRepository
) : EventRepository.EventListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    override fun eventAdded(event: Event) {
        if (event.eventMetaData.repeatStartDate.isTimeStampInPast()) {
            // event is in het verleden
            // todo send reminder
        }
        view.presentAddedEvent(event)
    }

    override fun eventModified(event: Event) {
        view.presentEditedEvent(event)
    }

    override fun eventDeleted(event: Event) {
        view.presentDeletedEvent(event)
    }

    override fun setLoading(isLoading: Boolean) {
        view.setLoading(isLoading)
    }

    fun detachEventListener() {
        agendaRepository.detachEventListener()
    }

    fun listenToEvents() = agendaRepository.listenToEvents(this@EventListPresenter)

    fun filterMe(filter: Filter) {
        filter.filter(userRepository.getCurrentUserId())
    }

    fun markEventAsCompleted(event: Event) = scope.launch {
        if (event.eventMetaData.repeatInterval == EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            agendaRepository.deleteEvent(event.eventId)
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

    private suspend fun updateEventMetaData(event: Event, nextOccurrence: Long) {
        val eventMetaData = EventMetaData(
                repeatStartDate = nextOccurrence,
                repeatInterval = event.eventMetaData.repeatInterval
        )

        event.eventMetaData = eventMetaData

        // reset done to false
        agendaRepository.updateEvent(event.eventId, eventMetaData = eventMetaData)
    }

    fun deleteEvent(event: Event) = scope.launch {
        agendaRepository.deleteEvent(event.eventId)
    }
}
