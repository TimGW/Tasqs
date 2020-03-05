package com.timgortworst.roomy.presentation.features.task.presenter

import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.presentation.features.task.view.TaskListView

class TaskListPresenter(
    private val view: TaskListView
) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

//    private fun setNotificationReminder(task: Task, hasPendingWrites: Boolean) {
//        if (hasPendingWrites && task.user.userId == uId) {
//            view.enqueueNotification(
//                task.id,
//                task.metaData,
//                task.description,
//                task.user.name
//            )
//        }
//    }
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

//    private fun renderDataState(snapshot: QuerySnapshot) {
//        snapshot.documentChanges.forEach {
//            val task = CustomMapper.toTask(it.document.toObject(TaskJson::class.java)) ?: return@forEach
//
//            when (it.type) {
//                DocumentChange.Type.ADDED -> {
//                    setNotificationReminder(task, snapshot.metadata.hasPendingWrites())
//                }
//                DocumentChange.Type.MODIFIED -> {
//                    view.removePendingNotificationReminder(task.id)
//                    setNotificationReminder(task, snapshot.metadata.hasPendingWrites())
//                }
//                DocumentChange.Type.REMOVED -> {
//                    view.removePendingNotificationReminder(task.id)
//                }
//            }
//        }
//    }

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
