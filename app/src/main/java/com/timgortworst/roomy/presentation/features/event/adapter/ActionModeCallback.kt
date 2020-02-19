package com.timgortworst.roomy.presentation.features.event.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event

class ActionModeCallback(private var actionItemListener: ActionItemListener?,
                         private val tracker: SelectionTracker<String>,
                         private val eventList: List<Event>) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.action_mode_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selectedEventIds = tracker.selection.map { it }.toList()
        val selectedEvents = eventList.filter { m -> selectedEventIds.any { it == m.eventId } }

        return when (item.itemId) {
            R.id.delete -> {
                actionItemListener?.onActionItemDelete(selectedEvents, mode)
                true
            }
            R.id.edit -> {
                actionItemListener?.onActionItemEdit(selectedEvents.first())
                mode.finish()
                true
            }
            R.id.info -> {
                actionItemListener?.onActionItemInfo(selectedEvents.first())
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
        fun onActionItemDone(selectedEvents: List<Event>)
        fun onActionItemDelete(selectedEvents: List<Event>, mode: ActionMode)
        fun onActionItemEdit(selectedEvent: Event)
        fun onActionItemInfo(selectedEvent: Event)
    }
}