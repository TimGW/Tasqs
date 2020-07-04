package com.timgortworst.tasqs.presentation.features.task.actionmode

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.tasqs.presentation.features.task.adapter.TaskFirestoreAdapter

class TaskItemKeyProvider(
    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
) : ItemKeyProvider<String>(SCOPE_MAPPED) {

    override fun getKey(position: Int) = (adapter as TaskFirestoreAdapter).getItem(position).id

    override fun getPosition(id: String) = (adapter as TaskFirestoreAdapter).getPosition(id)
}