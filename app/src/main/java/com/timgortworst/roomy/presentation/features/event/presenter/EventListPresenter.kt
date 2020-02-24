package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.UIState
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventListPresenter(
        private val view: EventListView,
        private val eventUseCase: EventUseCase
) : UIState<Event>, DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

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

    fun eventsCompleted(events: List<Event>) = scope.launch {
        eventUseCase.eventsCompleted(events)
    }

    private fun setNotificationReminder(event: Event, hasPendingWrites: Boolean) {
        if (hasPendingWrites && event.user.userId == uId) {
            view.enqueueNotification(event.eventId, event.metaData, event.description, event.user.name)
        }
    }

    fun deleteEvents(events: List<Event>) = scope.launch {
        eventUseCase.deleteEvents(events)
    }

    override fun renderSuccessfulState(changeSet: List<Pair<Event, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        view.setMsgView(totalDataSetSize == 0, R.string.empty_list_state_title_events, R.string.empty_list_state_text_events)

        changeSet.forEach {
            when (it.second) {
                DocumentChange.Type.ADDED -> {
                    setNotificationReminder(it.first, hasPendingWrites)
                    view.presentAddedEvent(it.first)
                }
                DocumentChange.Type.MODIFIED -> {
                    view.removePendingNotificationReminder(it.first.eventId)
                    setNotificationReminder(it.first, hasPendingWrites)
                    view.presentEditedEvent(it.first)
                }
                DocumentChange.Type.REMOVED -> {
                    view.removePendingNotificationReminder(it.first.eventId)
                    view.presentDeletedEvent(it.first)
                }
            }
        }
    }


    override fun renderLoadingState(isLoading: Boolean) {
        view.setLoadingView(isLoading)
    }

    override fun renderErrorState(hasError: Boolean) {
        view.setMsgView(hasError, R.string.error_list_state_title, R.string.error_list_state_text)
    }

    fun onSelectionChanged(tracker: SelectionTracker<String>, actionMode: ActionMode?) {
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
