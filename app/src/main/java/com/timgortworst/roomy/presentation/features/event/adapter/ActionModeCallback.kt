package com.timgortworst.roomy.presentation.features.event.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event

class ActionModeCallback(private var actionItemListener: ActionItemListener?,
                         private val tracker: SelectionTracker<Event>) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val selectedEvents = tracker.selection.map { it }.toList()
        menu.findItem(R.id.edit).isVisible = selectedEvents.size == 1
        menu.findItem(R.id.info).isVisible = selectedEvents.size == 1
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selectedEvents = tracker.selection.map { it }.toList()

        return when (item.itemId) {
            R.id.delete -> {
                actionItemListener?.onActionItemDelete(selectedEvents, mode)
                true
            }
            R.id.edit -> {
                actionItemListener?.onActionItemEdit(selectedEvents)
                mode.finish()
                true
            }
            R.id.info -> {
                actionItemListener?.onActionItemInfo(selectedEvents)
                mode.finish()
                true
            }
            R.id.done -> {
                actionItemListener?.onActionItemDone(selectedEvents)
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
        fun onActionItemDelete(selectedEvents: List<Event>, mode: ActionMode)
        fun onActionItemEdit(selectedEvents: List<Event>)
        fun onActionItemInfo(selectedEvents: List<Event>)
        fun onActionItemDone(selectedEvents: List<Event>)
    }
}