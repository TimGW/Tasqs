package com.timgortworst.tasqs.presentation.features.task.actionmode

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.tasqs.presentation.features.task.adapter.TaskFirestoreAdapter

class TaskItemDetailsLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<String>() {

    override fun getItemDetails(motionEvent: MotionEvent): ItemDetails<String>? =
        recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)?.let {
            return (recyclerView.getChildViewHolder(it) as? TaskFirestoreAdapter.ViewHolder)?.getItemDetails()
        }
}