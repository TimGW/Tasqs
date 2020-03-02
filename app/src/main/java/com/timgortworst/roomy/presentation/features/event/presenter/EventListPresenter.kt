package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.NetworkResponse
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.presentation.features.event.view.EventListView

class EventListPresenter(
    private val view: EventListView
) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    private fun setNotificationReminder(event: Event, hasPendingWrites: Boolean) {
        if (hasPendingWrites && event.user.userId == uId) {
            view.enqueueNotification(
                event.eventId,
                event.metaData,
                event.description,
                event.user.name
            )
        }
    }
//
//    fun handleResponse(networkResponse: NetworkResponse) {
//        when (networkResponse) {
//            NetworkResponse.Loading -> {
////                view.setMsgView(View.GONE)
////                view.presentLoadingState(View.VISIBLE)
//            }
//            is NetworkResponse.Error -> {
////                view.presentLoadingState(View.GONE)
////                view.setMsgView(View.VISIBLE, R.string.error_list_state_title, R.string.error_list_state_text)
//            }
//            is NetworkResponse.Success -> {
////                view.presentLoadingState(View.GONE)
////                view.setMsgView(View.GONE)
////                networkResponse.data?.let { renderSuccessfulState(it) }
//            }
//        }
//    }

//    private fun renderSuccessfulState(snapshot: QuerySnapshot) {
//        if (snapshot.isEmpty) {
//            renderEmptyState()
//        } else {
//            renderDataState(snapshot)
//        }
//    }

    private fun renderDataState(snapshot: QuerySnapshot) {
        snapshot.documentChanges.forEach {
            val event = CustomMapper.toEvent(it.document.toObject(EventJson::class.java)) ?: return@forEach

            when (it.type) {
                DocumentChange.Type.ADDED -> {
                    setNotificationReminder(event, snapshot.metadata.hasPendingWrites())
//                    view.presentAddedEvent(event)
                }
                DocumentChange.Type.MODIFIED -> {
                    view.removePendingNotificationReminder(event.eventId)
                    setNotificationReminder(event, snapshot.metadata.hasPendingWrites())
//                    view.presentEditedEvent(event)
                }
                DocumentChange.Type.REMOVED -> {
                    view.removePendingNotificationReminder(event.eventId)
//                    view.presentDeletedEvent(event)
                }
            }
        }
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
