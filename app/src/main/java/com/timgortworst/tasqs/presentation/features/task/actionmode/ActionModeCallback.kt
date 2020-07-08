package com.timgortworst.tasqs.presentation.features.task.actionmode

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.Task

class ActionModeCallback(
    private var actionItemListener: ActionItemListener?,
    private val tracker: SelectionTracker<String>,
    private val taskList: List<Task>
) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selectedTaskIds = tracker.selection.map { it }.toList()
        val selectedTasks = taskList.filter { m -> selectedTaskIds.any { it == m.id } }

        return when (item.itemId) {
            R.id.delete -> {
                actionItemListener?.onActionItemDelete(selectedTasks, mode)
                true
            }
            R.id.edit -> {
                actionItemListener?.onActionItemEdit(selectedTasks.first())
                mode.finish()
                true
            }
            R.id.info -> {
                actionItemListener?.onActionItemInfo(selectedTasks.first())
                mode.finish()
                true
            }
            R.id.done -> {
                actionItemListener?.onActionItemDone(selectedTasks)
                mode.finish()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        tracker.clearSelection()
        actionItemListener = null
    }

    interface ActionItemListener {
        fun onActionItemDone(selectedTasks: List<Task>)
        fun onActionItemDelete(selectedTasks: List<Task>, mode: ActionMode)
        fun onActionItemEdit(selectedTask: Task)
        fun onActionItemInfo(selectedTask: Task)
    }
}