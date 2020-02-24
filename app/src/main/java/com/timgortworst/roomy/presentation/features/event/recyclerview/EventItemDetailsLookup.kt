package com.timgortworst.roomy.presentation.features.event.recyclerview

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class EventItemDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<String>() {

    override fun getItemDetails(motionEvent: MotionEvent): ItemDetails<String>? =
        recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)?.let {
            return (recyclerView.getChildViewHolder(it) as? EventListAdapter.ViewHolder)?.getItemDetails()
        }
}