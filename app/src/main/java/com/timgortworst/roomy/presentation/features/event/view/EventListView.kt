package com.timgortworst.roomy.presentation.features.event.view

import androidx.annotation.StringRes
import androidx.recyclerview.selection.SelectionTracker
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.presentation.base.view.PageStateView

interface EventListView : PageStateView {
    fun presentAddedEvent(event: Event)
    fun presentEditedEvent(event: Event)
    fun presentDeletedEvent(event: Event)
    fun removePendingNotificationReminder(eventId: String)
    fun enqueueNotification(eventId: String, eventMetaData: EventMetaData, eventName: String, userName: String)
    fun showToast(@StringRes stringRes: Int)
    fun setActionModeTitle(size: Int)
    fun startActionMode(tracker: SelectionTracker<String>)
    fun stopActionMode()
    fun invalidateActionMode()
}