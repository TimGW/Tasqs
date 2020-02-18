package com.timgortworst.roomy.presentation.features.event.adapter

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.data.model.Event

class EventItemKeyProvider(private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : ItemKeyProvider<String>(SCOPE_MAPPED) {

    override fun getKey(position: Int) = (adapter as EventListAdapter).getEvent(position).eventId

    override fun getPosition(eventId: String) = (adapter as EventListAdapter).getPosition(eventId)
}