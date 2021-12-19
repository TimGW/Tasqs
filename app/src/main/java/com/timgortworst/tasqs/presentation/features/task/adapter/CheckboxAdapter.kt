package com.timgortworst.tasqs.presentation.features.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.tasqs.R
import kotlinx.android.synthetic.main.layout_input_checkbox.view.*

class CheckboxAdapter(
    private var viewItem: ViewItem
) : RecyclerView.Adapter<CheckboxAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_input_checkbox, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkbox.isChecked = viewItem.isChecked
        holder.checkbox.text = viewItem.text

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            viewItem.callback?.onCheckedChanged(isChecked)
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.layout_input_checkbox

    override fun getItemCount() = 1

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.task_repeat_checkbox
    }

    data class ViewItem(
        val isChecked: Boolean,
        val text: String,
        val callback: Callback? = null
    )

    interface Callback {
        fun onCheckedChanged(isChecked: Boolean)
    }
}



