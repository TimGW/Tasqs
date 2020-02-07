package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.RemoteApi
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class EventListPresenter @Inject constructor(
        private val view: EventListView,
        private val eventUseCase: EventUseCase
) : RemoteApi<Event>, DefaultLifecycleObserver {
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
        view.presentEmptyView(true)

        eventUseCase.listenToEvents(this@EventListPresenter)
    }

    fun eventsCompleted(events: List<Event>) = scope.launch {
        eventUseCase.eventsCompleted(events)
    }

    private fun setNotificationReminder(event: Event, hasPendingWrites: Boolean) {
        if (hasPendingWrites && event.user.userId == eventUseCase.getCurrentUserId()) {
            view.enqueueNotification(event.eventId, event.eventMetaData, event.description, event.user.name)
        }
    }

    fun deleteEvents(events: List<Event>) = scope.launch {
        eventUseCase.deleteEvents(events)
        events.forEach {
            view.removePendingNotificationReminder(it.eventId)
        }
    }

    override fun renderSuccessfulState(changeSet: List<Pair<Event, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        view.setLoadingView(false)
        view.setErrorView(false)
        view.presentEmptyView(totalDataSetSize == 0)

        changeSet.forEach {
            when (it.second) {
                DocumentChange.Type.ADDED -> {
                    setNotificationReminder(it.first, hasPendingWrites)
                    view.presentAddedEvent(it.first)
                }
                DocumentChange.Type.MODIFIED -> {
                    setNotificationReminder(it.first, hasPendingWrites)
                    view.presentEditedEvent(it.first)
                }
                DocumentChange.Type.REMOVED -> view.presentDeletedEvent(it.first)
            }
        }
    }


    override fun renderLoadingState() {
        view.setLoadingView(true)
    }

    override fun renderUnsuccessfulState() {
        view.setLoadingView(false)
        view.setErrorView(true, R.string.error_list_state_title, R.string.error_list_state_text)
    }

    fun onSelectionChanged(tracker: SelectionTracker<Event>, actionMode: ActionMode?) {
        if (tracker.hasSelection() && actionMode == null) {
            view.startActionMode(tracker)
            view.setActionModeTitle(tracker.selection.size())
        } else if (!tracker.hasSelection() && actionMode != null) {
            view.stopActionMode()
        } else {
            view.setActionModeTitle(tracker.selection.size())
            view.invalidateActionMode()
        }
    }
}
