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
        mode.menuInflater.inflate(R.menu.multi_select_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selectedEvents = tracker.selection.map { it }.toList()

        return when (item.itemId) {
            R.id.delete -> {
                actionItemListener?.onActionItemDelete(selectedEvents)
                mode.finish()
                true
            }
            R.id.edit -> {
                actionItemListener?.onActionItemEdit(selectedEvents)
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
        fun onActionItemDelete(selectedEvents: List<Event>)
        fun onActionItemEdit(selectedEvents: List<Event>)
    }
}