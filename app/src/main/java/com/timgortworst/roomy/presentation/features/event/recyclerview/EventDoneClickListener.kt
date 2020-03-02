package com.timgortworst.roomy.presentation.features.event.recyclerview

import com.timgortworst.roomy.domain.model.Event

interface EventDoneClickListener {
    fun onEventDoneClicked(event: Event, position: Int)
}