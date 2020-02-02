package com.timgortworst.roomy.presentation.features.event.adapter

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.data.model.Event

class EventItemKeyProvider(private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : ItemKeyProvider<Event>(SCOPE_MAPPED) {

    override fun getKey(position: Int) = (adapter as EventListAdapter).getEvent(position)

    override fun getPosition(event: Event) = (adapter as EventListAdapter).getPosition(event)
}