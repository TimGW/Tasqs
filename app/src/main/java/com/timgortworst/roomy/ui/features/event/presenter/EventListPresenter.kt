package com.timgortworst.roomy.ui.features.event.presenter

import android.widget.Filter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.repository.BaseResponse
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.domain.utils.Constants
import com.timgortworst.roomy.domain.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.domain.utils.isTimeStampInPast
import com.timgortworst.roomy.ui.features.event.view.EventListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EventListPresenter @Inject constructor(
        private val view: EventListView,
        private val eventListInteractor: EventUseCase
) : BaseResponse(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun detachEventListener() {
        eventListInteractor.detachEventListener()
    }

    fun listenToEvents() = scope.launch {
        eventListInteractor.listenToEvents(this@EventListPresenter)
    }

    fun filterMe(filter: Filter) {
        filter.filter(eventListInteractor.getCurrentUserId())
    }

    fun markEventAsCompleted(event: Event) = scope.launch {
        if (event.eventMetaData.repeatInterval == EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            eventListInteractor.deleteEvent(event.eventId)
            return@launch
        }

        updateEventMetaData(event, calcNextOccurance(event.eventMetaData))
    }

    private fun calcNextOccurance(eventMetaData: EventMetaData): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, Constants.DEFAULT_HOUR_OF_DAY_NOTIFICATION) // default of 20:00
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return if (eventMetaData.nextEventDate.isTimeStampInPast()) {
            // if timestamp is in past, update to the next interval measured from today
            cal.timeInMillis + (eventMetaData.repeatInterval.interval)
        } else {
            // if timestamp is in the future, update to the next interval measured from the previous
            eventMetaData.nextEventDate + (eventMetaData.repeatInterval.interval)
        }
    }

    private suspend fun updateEventMetaData(event: Event, nextOccurrence: Long) {
        val eventMetaData = EventMetaData(
                nextEventDate = nextOccurrence,
                repeatInterval = event.eventMetaData.repeatInterval
        )

        event.eventMetaData = eventMetaData

        // reset done to false
        eventListInteractor.updateEvent(event.eventId, eventMetaData = eventMetaData)
    }

    fun setNotificationReminder(workRequestTag: String,
                                eventMetaData: EventMetaData,
                                categoryName: String,
                                userName: String) {
        if (eventMetaData.repeatInterval == EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            view.enqueueOneTimeNotification(workRequestTag, eventMetaData, categoryName, userName)
        } else {
            view.enqueuePeriodicNotification(workRequestTag, eventMetaData, categoryName, userName)
        }
    }

    fun deleteEvent(event: Event) = scope.launch {
        eventListInteractor.deleteEvent(event.eventId)
        view.removePendingNotificationReminder(event.eventId)
    }

    override fun renderSuccessfulState(dc: List<DocumentChange>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        view.setLoadingView(false)
        view.setErrorView(false)
        view.presentEmptyView(totalDataSetSize == 0)

        dc.forEach {
            val event = it.document.toObject(Event::class.java)
            when (it.type) {
                DocumentChange.Type.ADDED -> {
                    if (hasPendingWrites) {
                        setNotificationReminder(event.eventId, event.eventMetaData, event.eventCategory.name, event.user.name)
                    }
                    view.presentAddedEvent(event)
                }
                DocumentChange.Type.MODIFIED -> {
                    if (hasPendingWrites) {
                        setNotificationReminder(event.eventId, event.eventMetaData, event.eventCategory.name, event.user.name)
                    }
                    view.presentEditedEvent(event)
                }
                DocumentChange.Type.REMOVED -> view.presentDeletedEvent(event)
            }
        }
    }

    override fun renderLoadingState() {
        view.setLoadingView(true)
    }

    override fun renderUnsuccessfulState(throwable: Throwable) {
        view.setLoadingView(false)
        view.setErrorView(true, R.string.error_list_state_title, R.string.error_list_state_text)
    }
}
