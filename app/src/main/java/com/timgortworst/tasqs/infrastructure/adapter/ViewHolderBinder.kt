package com.timgortworst.tasqs.infrastructure.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * A interface for implementing an object that handles the binding of data to a ViewHolder
 * and instantiation of that viewholder
 */
interface ViewHolderBinder<T, VH : RecyclerView.ViewHolder> {

    fun createViewHolder(parent: ViewGroup): VH
    fun bind(viewHolder: VH, item: T)
    fun onAttachedToWindow(viewHolder: RecyclerView.ViewHolder) {
        // Default implementation
    }
    fun onDetachedToWindow(viewHolder: RecyclerView.ViewHolder) {
        // Default implementation
    }

}
