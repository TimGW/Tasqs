package com.timgortworst.tasqs.presentation.features.task.actionmode

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.google.firebase.auth.FirebaseAuth
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
        val selectedTaskIds = tracker.selection.map { it }.toList()
        val selectedTasks = taskList.filter { m -> selectedTaskIds.any { it == m.id } }
        val visibility = areOwnTasks(selectedTasks)

        menu.findItem(R.id.delete)?.isVisible = visibility
        menu.findItem(R.id.info)?.isVisible = selectedTasks.size == 1
        menu.findItem(R.id.edit)?.isVisible = visibility && selectedTasks.size == 1
        menu.findItem(R.id.done)?.isVisible = visibility

        return true
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

    private fun areOwnTasks(task: List<Task>): Boolean {
        return task.all {
            it.user?.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)
        }
    }

    interface ActionItemListener {
        fun onActionItemDone(selectedTasks: List<Task>)
        fun onActionItemDelete(selectedTasks: List<Task>, mode: ActionMode)
        fun onActionItemEdit(selectedTask: Task)
        fun onActionItemInfo(selectedTask: Task)
    }
}