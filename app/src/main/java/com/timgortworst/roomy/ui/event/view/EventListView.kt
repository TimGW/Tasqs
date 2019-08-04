package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Event

interface EventListView  {
    fun presentAddedEvent(agendaEvent: Event)
    fun presentEditedEvent(agendaEvent: Event)
    fun presentDeletedEvent(agendaEvent: Event)
    fun presentEmptyView(isVisible: Boolean)
    fun setLoadingView(isLoading: Boolean)
    fun setErrorView(isVisible: Boolean,
                     title: Int = R.string.error_list_state_title,
                     message: Int = R.string.error_list_state_text)
}