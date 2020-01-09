package com.timgortworst.roomy.presentation.features.event.presenter

import android.widget.Filter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.ApiStatus
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class EventListPresenter @Inject constructor(
        private val view: EventListView,
        private val eventUseCase: EventUseCase
) : ApiStatus(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun detachEventListener() {
        eventUseCase.detachEventListener()
    }

    fun listenToEvents() = scope.launch {
        eventUseCase.listenToEvents(this@EventListPresenter)
    }

    fun filterMe(filter: Filter) {
        filter.filter(eventUseCase.getCurrentUserId())
    }

    fun markEventAsCompleted(event: Event) = scope.launch {
        if (event.eventMetaData.eventInterval == EventMetaData.EventInterval.SINGLE_EVENT) {
            eventUseCase.deleteEvent(event.eventId)
            return@launch
        }

        eventUseCase.markEventAsComplete(event)
    }

    fun setNotificationReminder(event: Event, hasPendingWrites: Boolean) {
        if (hasPendingWrites && event.user.userId == eventUseCase.getCurrentUserId()) {
            view.enqueuePeriodicNotification(event.eventId, event.eventMetaData, event.eventCategory.name, event.user.name)
        }
    }

    fun deleteEvent(event: Event) = scope.launch {
        eventUseCase.deleteEvent(event.eventId)
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
                    setNotificationReminder(event, hasPendingWrites)
                    view.presentAddedEvent(event)
                }
                DocumentChange.Type.MODIFIED -> {
                    setNotificationReminder(event, hasPendingWrites)
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

    fun checkIfUserCanEditEvent(event: Event) = scope.launch {
        if (eventUseCase.isUserAbleToCreateEvent()) {
            view.openEventEditActivity(event)
        } else {
            view.showToast(R.string.error_no_categories)
        }
    }
}
