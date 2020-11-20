package com.timgortworst.tasqs.presentation.features.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.timgortworst.tasqs.R
import kotlinx.android.synthetic.main.layout_input_edittext.view.*

class EditTextAdapter(
    private var viewItem: ViewItem
) : RecyclerView.Adapter<EditTextAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_input_edittext, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.editText.setText(viewItem.text)
        holder.hint?.error = viewItem.errorMessage

        // set listener
        holder.editText.doAfterTextChanged {
            if (it?.isNotBlank() == true) holder.hint?.error = null

            viewItem.callback?.onDescriptionInput(holder.editText.text.toString())
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.layout_input_edittext

    override fun getItemCount() = 1

    fun setViewItem(viewItem: ViewItem) {
        this.viewItem = viewItem
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val editText: TextInputEditText = itemView.task_description
        val hint: TextInputLayout? = itemView.task_description_hint
    }

    data class ViewItem(
        val text: String,
        val errorMessage: String?,
        val callback: Callback? = null
    )

    interface Callback {
        fun onDescriptionInput(text: String)
    }
}



