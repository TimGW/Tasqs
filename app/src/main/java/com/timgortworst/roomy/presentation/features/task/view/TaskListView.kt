package com.timgortworst.roomy.presentation.features.task.view

import androidx.annotation.StringRes
import androidx.recyclerview.selection.SelectionTracker
import com.timgortworst.roomy.domain.model.TaskMetaData

interface TaskListView {
    fun removePendingNotificationReminder(taskId: String)
    fun enqueueNotification(taskId: String, taskMetaData: TaskMetaData, taskName: String, userName: String)
    fun showToast(@StringRes stringRes: Int)
    fun setActionModeTitle(size: Int)
    fun startActionMode(tracker: SelectionTracker<String>)
    fun stopActionMode()
    fun invalidateActionMode()
}