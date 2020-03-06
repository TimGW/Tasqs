package com.timgortworst.roomy.presentation.features.task.presenter

import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.firebase.ui.common.ChangeEventType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SnapshotMetadata
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.presentation.features.task.view.TaskListView

class TaskListPresenter(
    private val view: TaskListView
) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

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

    fun renderDataState(
        type: ChangeEventType,
        task: Task?
    ) {
        if (task == null) return

        when (type) {
            ChangeEventType.ADDED -> setNotificationReminder(task)
            ChangeEventType.CHANGED -> setNotificationReminder(task)
            ChangeEventType.REMOVED -> view.removePendingNotificationReminder(task.id)
            ChangeEventType.MOVED -> { }
        }
    }

    private fun setNotificationReminder(task: Task) {
        if (task.user.userId == uId) {
            view.enqueueNotification(
                task.id,
                task.metaData,
                task.description,
                task.user.name
            )
        }
    }
}
